package com.guga.asunaanimes.domain.model

import com.guga.asunaanimes.util.anime
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class UserScoreStatsTest {

    @Test
    fun `accepts scores from 1 to 10`() {
        assertTrue(UserScoreStats.isValid(1))
        assertTrue(UserScoreStats.isValid(10))
        assertFalse(UserScoreStats.isValid(0))
        assertFalse(UserScoreStats.isValid(11))
    }

    @Test
    fun `mean ignores unscored entries`() {
        val mean = UserScoreStats.mean(
            listOf(
                anime(1, userScore = 8),
                anime(2, userScore = null),
                anime(3, userScore = 10)
            )
        )

        assertEquals(9f, mean)
    }

    @Test
    fun `mean is null when nothing is scored`() {
        assertNull(UserScoreStats.mean(listOf(anime(1), anime(2))))
        assertNull(UserScoreStats.mean(emptyList()))
    }
}
