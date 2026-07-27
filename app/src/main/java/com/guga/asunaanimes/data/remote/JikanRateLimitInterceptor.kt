package com.guga.asunaanimes.data.remote

import android.os.SystemClock
import okhttp3.Interceptor
import okhttp3.Response

/**
 * Jikan allows roughly 3 requests/second. Bursting the initial pages used to burn the quota and
 * make the very next search fail, which left the UI stuck filtering only the cached page.
 */
class JikanRateLimitInterceptor(
    private val minIntervalMs: Long = DEFAULT_MIN_INTERVAL_MS
) : Interceptor {

    private val lock = Any()
    private var lastRequestAtMs = 0L

    override fun intercept(chain: Interceptor.Chain): Response {
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
        return chain.proceed(chain.request())
    }

    private companion object {
        const val DEFAULT_MIN_INTERVAL_MS = 450L
    }
}
