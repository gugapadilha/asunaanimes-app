package com.guga.asunaanimes.domain.usecase

import com.guga.asunaanimes.core.common.AppResult
import com.guga.asunaanimes.domain.model.AddToCollectionOutcome
import com.guga.asunaanimes.domain.model.Anime
import com.guga.asunaanimes.domain.model.AnimeCollectionType
import com.guga.asunaanimes.domain.repository.AnimeCollectionRepository
import javax.inject.Inject

class AddAnimeToCollectionUseCase @Inject constructor(
    private val animeCollectionRepository: AnimeCollectionRepository
) {

    /**
     * Adds [anime] to [type]. Favoriting also ensures the title is in Watched — a favorite
     * implies the user has already seen it.
     */
    suspend operator fun invoke(
        type: AnimeCollectionType,
        anime: Anime
    ): AppResult<AddToCollectionOutcome> {
        val result = animeCollectionRepository.add(type, anime)
        if (type == AnimeCollectionType.FAVORITE && result is AppResult.Success) {
            animeCollectionRepository.add(AnimeCollectionType.WATCHED, anime)
        }
        return result
    }
}
