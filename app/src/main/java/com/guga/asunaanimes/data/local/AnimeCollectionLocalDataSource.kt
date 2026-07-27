package com.guga.asunaanimes.data.local

import com.guga.asunaanimes.data.local.model.AnimeEntity
import com.guga.asunaanimes.domain.model.AnimeCollectionType
import kotlinx.coroutines.flow.Flow

interface AnimeCollectionLocalDataSource {

    fun observe(type: AnimeCollectionType): Flow<List<AnimeEntity>>

    /** @return `true` when the anime was stored, `false` when it already belonged to the list. */
    suspend fun add(type: AnimeCollectionType, anime: AnimeEntity): Boolean

    suspend fun remove(type: AnimeCollectionType, malId: Int)
}
