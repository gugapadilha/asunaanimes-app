package com.guga.asunaanimes.domain.repository

import com.guga.asunaanimes.core.common.AppResult
import com.guga.asunaanimes.domain.model.AddToCollectionOutcome
import com.guga.asunaanimes.domain.model.Anime
import com.guga.asunaanimes.domain.model.AnimeCollectionType
import kotlinx.coroutines.flow.Flow

interface AnimeCollectionRepository {

    fun observeCollection(type: AnimeCollectionType): Flow<List<Anime>>

    suspend fun add(type: AnimeCollectionType, anime: Anime): AppResult<AddToCollectionOutcome>

    suspend fun remove(type: AnimeCollectionType, anime: Anime): AppResult<Unit>
}
