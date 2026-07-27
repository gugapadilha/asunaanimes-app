package com.guga.asunaanimes.domain.repository

import com.guga.asunaanimes.core.common.AppResult
import com.guga.asunaanimes.domain.model.AnimePage

interface AnimeRepository {

    suspend fun getTopAnime(page: Int): AppResult<AnimePage>

    suspend fun searchAnime(query: String): AppResult<AnimePage>
}
