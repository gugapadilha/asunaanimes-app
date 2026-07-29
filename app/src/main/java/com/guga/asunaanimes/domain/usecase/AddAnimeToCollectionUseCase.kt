package com.guga.asunaanimes.domain.usecase

import com.guga.asunaanimes.core.common.AppError
import com.guga.asunaanimes.core.common.AppResult
import com.guga.asunaanimes.domain.model.AddToCollectionOutcome
import com.guga.asunaanimes.domain.model.Anime
import com.guga.asunaanimes.domain.model.AnimeCollectionType
import com.guga.asunaanimes.domain.model.UserScoreStats
import com.guga.asunaanimes.domain.repository.AnimeCollectionRepository
import javax.inject.Inject

class AddAnimeToCollectionUseCase @Inject constructor(
    private val animeCollectionRepository: AnimeCollectionRepository
) {

    /**
     * Adds [anime] to [type] with an optional personal [userScore] (1–10, or null to skip).
     * Favoriting also ensures the title is in Watched.
     */
    suspend operator fun invoke(
        type: AnimeCollectionType,
        anime: Anime,
        userScore: Int? = null
    ): AppResult<AddToCollectionOutcome> {
        if (userScore != null && !UserScoreStats.isValid(userScore)) {
            return AppResult.Failure(AppError.Unknown())
        }
        val scoredAnime = anime.copy(userScore = userScore)
        val result = animeCollectionRepository.add(type, scoredAnime)
        if (type == AnimeCollectionType.FAVORITE && result is AppResult.Success) {
            animeCollectionRepository.add(AnimeCollectionType.WATCHED, scoredAnime)
        }
        return result
    }
}
