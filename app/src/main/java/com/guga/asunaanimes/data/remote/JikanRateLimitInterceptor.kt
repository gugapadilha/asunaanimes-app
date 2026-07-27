package com.guga.asunaanimes.data.remote

import android.os.SystemClock
import okhttp3.Interceptor
import okhttp3.Response

/**
 * Jikan allows roughly 3 requests/second. We space calls and retry HTTP 429 so pagination does not
 * die after the first burst (initial fill often requests page 1 + 2 back-to-back).
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
            if (response.code != HTTP_TOO_MANY_REQUESTS || attempt >= maxRetries) {
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
    }
}
