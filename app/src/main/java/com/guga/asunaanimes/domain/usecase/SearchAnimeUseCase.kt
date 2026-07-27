package com.guga.asunaanimes.domain.usecase

import com.guga.asunaanimes.core.common.AppResult
import com.guga.asunaanimes.domain.model.AnimePage
import com.guga.asunaanimes.domain.repository.AnimeRepository
import javax.inject.Inject

class SearchAnimeUseCase @Inject constructor(
    private val animeRepository: AnimeRepository
) {

    /**
     * A blank query has no meaning for the remote search endpoint, so it short-circuits to an
     * empty page instead of hitting the network.
     */
    suspend operator fun invoke(query: String): AppResult<AnimePage> {
        val sanitizedQuery = query.trim()
        if (sanitizedQuery.isEmpty()) {
            return AppResult.Success(AnimePage(animes = emptyList(), currentPage = 1, hasNextPage = false))
        }
        return animeRepository.searchAnime(sanitizedQuery)
    }
}
