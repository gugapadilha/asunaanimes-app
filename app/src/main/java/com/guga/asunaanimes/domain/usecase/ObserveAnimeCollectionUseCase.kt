package com.guga.asunaanimes.domain.usecase

import com.guga.asunaanimes.domain.model.Anime
import com.guga.asunaanimes.domain.model.AnimeCollectionType
import com.guga.asunaanimes.domain.repository.AnimeCollectionRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

class ObserveAnimeCollectionUseCase @Inject constructor(
    private val animeCollectionRepository: AnimeCollectionRepository
) {

    operator fun invoke(type: AnimeCollectionType): Flow<List<Anime>> =
        animeCollectionRepository.observeCollection(type)
}
