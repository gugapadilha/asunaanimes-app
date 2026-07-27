package com.guga.asunaanimes.core.util

import java.text.SimpleDateFormat
import java.util.Locale

/**
 * Formats the ISO-ish dates returned by Jikan (`2013-04-06T00:00:00+00:00`) into the
 * `MMM-yyyy` label used across the app.
 */
object AiredDateFormatter {

    private const val UNKNOWN_DATE = "Unknown date"

    private val inputFormat get() = SimpleDateFormat("yyyy-MM", Locale.US)
    private val outputFormat get() = SimpleDateFormat("MMM-yyyy", Locale.US)

    fun format(rawDate: String): String {
        if (!rawDate.contains("-")) return rawDate
        return runCatching {
            val yearAndMonth = rawDate.substring(0, rawDate.lastIndexOf("-"))
            val parsed = inputFormat.parse(yearAndMonth) ?: return UNKNOWN_DATE
            outputFormat.format(parsed)
        }.getOrDefault(UNKNOWN_DATE)
    }
}
