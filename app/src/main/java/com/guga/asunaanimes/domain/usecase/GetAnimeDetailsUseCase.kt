package com.guga.asunaanimes.domain.usecase

import com.guga.asunaanimes.core.common.AppResult
import com.guga.asunaanimes.domain.model.Anime
import com.guga.asunaanimes.domain.repository.AnimeRepository
import javax.inject.Inject

class GetAnimeDetailsUseCase @Inject constructor(
    private val animeRepository: AnimeRepository
) {

    /**
     * Recommendations (and some seasonal cards) arrive as stubs. We only hit the network when the
     * selected anime is clearly missing the fields the detail sheet needs.
     */
    suspend operator fun invoke(anime: Anime): AppResult<Anime> {
        if (!anime.needsDetailsEnrichment()) {
            return AppResult.Success(anime)
        }
        return animeRepository.getAnimeById(anime.malId)
    }
}

fun Anime.needsDetailsEnrichment(): Boolean =
    synopsis.isNullOrBlank() || score == null || episodes == null || rating.isNullOrBlank()
