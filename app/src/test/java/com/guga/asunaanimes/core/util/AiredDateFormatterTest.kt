package com.guga.asunaanimes.core.util

import org.junit.Assert.assertEquals
import org.junit.Test

class AiredDateFormatterTest {

    @Test
    fun `formats an iso date into month and year`() {
        assertEquals("Apr-2011", AiredDateFormatter.format("2011-04-06T00:00:00+00:00"))
    }

    @Test
    fun `returns the raw value when there is nothing to parse`() {
        assertEquals("soon", AiredDateFormatter.format("soon"))
    }

    @Test
    fun `never throws on malformed input`() {
        assertEquals("Unknown date", AiredDateFormatter.format("not-a-date"))
    }
}
