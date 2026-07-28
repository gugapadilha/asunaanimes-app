package com.guga.asunaanimes.data.remote

import android.os.SystemClock
import okhttp3.Interceptor
import okhttp3.Response

/**
 * Jikan allows roughly 3 requests/second. We space calls and retry transient failures (429 / 5xx
 * gateway) so pagination does not die after the first burst (initial fill often requests page
 * 1 + 2 back-to-back; later pages often return 504).
 */
class JikanRateLimitInterceptor(
    private val minIntervalMs: Long = DEFAULT_MIN_INTERVAL_MS,
    private val maxRetries: Int = DEFAULT_MAX_RETRIES
) : Interceptor {

    private val lock = Any()
    private var lastRequestAtMs = 0L

    override fun intercept(chain: Interceptor.Chain): Response {
        var attempt = 0
        while (true) {
            waitForSlot()
            val response = chain.proceed(chain.request())
            if (!shouldRetry(response.code, attempt)) {
                return response
            }
            val retryAfterMs = parseRetryAfterMs(response) ?: defaultBackoffMs(attempt)
            response.close()
            attempt++
            try {
                Thread.sleep(retryAfterMs)
            } catch (_: InterruptedException) {
                Thread.currentThread().interrupt()
                return chain.proceed(chain.request())
            }
        }
    }

    private fun shouldRetry(code: Int, attempt: Int): Boolean {
        if (attempt >= maxRetries) return false
        return code == HTTP_TOO_MANY_REQUESTS ||
            code == HTTP_BAD_GATEWAY ||
            code == HTTP_SERVICE_UNAVAILABLE ||
            code == HTTP_GATEWAY_TIMEOUT
    }

    private fun waitForSlot() {
        synchronized(lock) {
            val now = SystemClock.elapsedRealtime()
            val waitMs = lastRequestAtMs + minIntervalMs - now
            if (waitMs > 0L) {
                try {
                    Thread.sleep(waitMs)
                } catch (_: InterruptedException) {
                    Thread.currentThread().interrupt()
                }
            }
            lastRequestAtMs = SystemClock.elapsedRealtime()
        }
    }

    private fun parseRetryAfterMs(response: Response): Long? {
        val header = response.header("Retry-After") ?: return null
        val seconds = header.toLongOrNull() ?: return null
        return (seconds * 1000L).coerceIn(500L, 10_000L)
    }

    private fun defaultBackoffMs(attempt: Int): Long =
        (1_000L * (attempt + 1)).coerceAtMost(5_000L)

    private companion object {
        const val DEFAULT_MIN_INTERVAL_MS = 400L
        const val DEFAULT_MAX_RETRIES = 2
        const val HTTP_TOO_MANY_REQUESTS = 429
        const val HTTP_BAD_GATEWAY = 502
        const val HTTP_SERVICE_UNAVAILABLE = 503
        const val HTTP_GATEWAY_TIMEOUT = 504
    }
}
