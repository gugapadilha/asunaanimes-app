package com.guga.asunaanimes.domain.usecase

import com.guga.asunaanimes.core.common.AppError
import com.guga.asunaanimes.core.common.AppResult
import com.guga.asunaanimes.domain.model.AnimeCollectionType
import com.guga.asunaanimes.domain.model.UserScoreStats
import com.guga.asunaanimes.domain.repository.AnimeCollectionRepository
import javax.inject.Inject

class UpdateAnimeUserScoreUseCase @Inject constructor(
    private val animeCollectionRepository: AnimeCollectionRepository
) {

    /**
     * Updates the personal score for [malId] in [type] and mirrors it into the other collection
     * when the same anime exists there (favorite/watched stay aligned).
     */
    suspend operator fun invoke(
        type: AnimeCollectionType,
        malId: Int,
        userScore: Int?
    ): AppResult<Unit> {
        if (userScore != null && !UserScoreStats.isValid(userScore)) {
            return AppResult.Failure(AppError.Unknown())
        }
        val primary = animeCollectionRepository.updateUserScore(type, malId, userScore)
        if (primary is AppResult.Failure) return primary

        val otherType = when (type) {
            AnimeCollectionType.FAVORITE -> AnimeCollectionType.WATCHED
            AnimeCollectionType.WATCHED -> AnimeCollectionType.FAVORITE
        }
        animeCollectionRepository.updateUserScore(otherType, malId, userScore)
        return AppResult.Success(Unit)
    }
}
