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

    suspend operator fun invoke(
        type: AnimeCollectionType,
        anime: Anime
    ): AppResult<AddToCollectionOutcome> = animeCollectionRepository.add(type, anime)
}
