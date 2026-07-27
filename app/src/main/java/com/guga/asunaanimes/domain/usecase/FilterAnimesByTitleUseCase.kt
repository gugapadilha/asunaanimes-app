package com.guga.asunaanimes.domain.usecase

import com.guga.asunaanimes.domain.model.Anime
import javax.inject.Inject

/**
 * Local, case-insensitive title matching used by collection screens and as an instant fallback
 * while the remote search is in flight.
 *
 * Matching is intentionally loose: "sw" and "Sword" both hit "Sword Art Online" because every
 * title word is checked as a prefix, not only the full string.
 */
class FilterAnimesByTitleUseCase @Inject constructor() {

    operator fun invoke(animes: List<Anime>, query: String): List<Anime> {
        val sanitizedQuery = query.trim()
        if (sanitizedQuery.isEmpty()) return animes

        return animes
            .mapNotNull { anime ->
                val rank = matchRank(anime.title, sanitizedQuery) ?: return@mapNotNull null
                anime to rank
            }
            .sortedWith(compareBy({ it.second }, { it.first.title.lowercase() }))
            .map { it.first }
    }

    private fun matchRank(title: String, query: String): Int? {
        val normalizedTitle = title.trim()
        if (normalizedTitle.isEmpty()) return null

        val lowerTitle = normalizedTitle.lowercase()
        val lowerQuery = query.lowercase()

        return when {
            lowerTitle == lowerQuery -> RANK_EXACT
            lowerTitle.startsWith(lowerQuery) -> RANK_TITLE_PREFIX
            titleWords(lowerTitle).any { it.startsWith(lowerQuery) } -> RANK_WORD_PREFIX
            lowerTitle.contains(lowerQuery) -> RANK_CONTAINS
            else -> null
        }
    }

    private fun titleWords(title: String): List<String> =
        title.split(WORD_SEPARATORS).filter { it.isNotBlank() }

    private companion object {
        val WORD_SEPARATORS = Regex("[\\s\\-:/·]+")
        const val RANK_EXACT = 0
        const val RANK_TITLE_PREFIX = 1
        const val RANK_WORD_PREFIX = 2
        const val RANK_CONTAINS = 3
    }
}
