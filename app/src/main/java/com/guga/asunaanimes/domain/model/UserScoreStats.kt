package com.guga.asunaanimes.domain.model

/**
 * MAL-style mean of personal scores. Entries without a [Anime.userScore] are ignored.
 */
object UserScoreStats {

    const val MIN_SCORE = 1
    const val MAX_SCORE = 10

    fun isValid(score: Int): Boolean = score in MIN_SCORE..MAX_SCORE

    fun mean(animes: List<Anime>): Float? {
        val scores = animes.mapNotNull { it.userScore }
        if (scores.isEmpty()) return null
        return (scores.sum().toDouble() / scores.size).toFloat()
    }
}
