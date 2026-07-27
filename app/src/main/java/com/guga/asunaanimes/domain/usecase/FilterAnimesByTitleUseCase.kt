package com.guga.asunaanimes.domain.usecase

import com.guga.asunaanimes.domain.model.Anime
import javax.inject.Inject

/**
 * Local, case-insensitive filtering used by the favorite and watched lists.
 */
class FilterAnimesByTitleUseCase @Inject constructor() {

    operator fun invoke(animes: List<Anime>, query: String): List<Anime> {
        val sanitizedQuery = query.trim()
        if (sanitizedQuery.isEmpty()) return animes
        return animes.filter { it.title.contains(sanitizedQuery, ignoreCase = true) }
    }
}
