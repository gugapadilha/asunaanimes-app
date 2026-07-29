package com.guga.asunaanimes.core.util

import java.text.SimpleDateFormat
import java.util.Locale

/**
 * Formats the ISO-ish dates returned by Jikan (`2013-04-06T00:00:00+00:00`) into the
 * `MMM-yyyy` label used across the app.
 */
object AiredDateFormatter {

    fun format(rawDate: String, locale: Locale = Locale.getDefault()): String? {
        if (!rawDate.contains("-")) return rawDate
        return runCatching {
            val yearAndMonth = rawDate.substring(0, rawDate.lastIndexOf("-"))
            val parsed = SimpleDateFormat("yyyy-MM", Locale.US).parse(yearAndMonth)
                ?: return null
            SimpleDateFormat("MMM-yyyy", locale).format(parsed)
        }.getOrNull()
    }
}
