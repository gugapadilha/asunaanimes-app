package com.guga.asunaanimes.domain.repository

import com.guga.asunaanimes.core.common.AppResult
import com.guga.asunaanimes.domain.model.Anime
import com.guga.asunaanimes.domain.model.AnimePage

interface AnimeRepository {

    suspend fun getTopAnime(page: Int): AppResult<AnimePage>

    suspend fun getSeasonalAnime(page: Int): AppResult<AnimePage>

    suspend fun getRecommendedAnime(page: Int): AppResult<AnimePage>

    suspend fun getAnimeById(id: Int): AppResult<Anime>

    suspend fun searchAnime(query: String, page: Int = 1): AppResult<AnimePage>
}
