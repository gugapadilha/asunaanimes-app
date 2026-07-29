package com.guga.asunaanimes.core.util

import java.util.Locale
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class AiredDateFormatterTest {

    @Test
    fun `formats an iso date into month and year`() {
        assertEquals(
            "Apr-2011",
            AiredDateFormatter.format("2011-04-06T00:00:00+00:00", Locale.US)
        )
    }

    @Test
    fun `returns the raw value when there is nothing to parse`() {
        assertEquals("soon", AiredDateFormatter.format("soon", Locale.US))
    }

    @Test
    fun `never throws on malformed input`() {
        assertNull(AiredDateFormatter.format("not-a-date", Locale.US))
    }
}
