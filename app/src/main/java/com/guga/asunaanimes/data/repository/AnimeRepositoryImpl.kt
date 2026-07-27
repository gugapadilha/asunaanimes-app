package com.guga.asunaanimes.data.repository

import com.guga.asunaanimes.core.common.AppResult
import com.guga.asunaanimes.core.common.map
import com.guga.asunaanimes.core.coroutines.IoDispatcher
import com.guga.asunaanimes.data.mapper.toDomain
import com.guga.asunaanimes.data.remote.AnimeApi
import com.guga.asunaanimes.data.remote.safeApiCall
import com.guga.asunaanimes.domain.model.AnimePage
import com.guga.asunaanimes.domain.repository.AnimeRepository
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

@Singleton
class AnimeRepositoryImpl @Inject constructor(
    private val animeApi: AnimeApi,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : AnimeRepository {

    override suspend fun getTopAnime(page: Int): AppResult<AnimePage> = withContext(ioDispatcher) {
        safeApiCall { animeApi.getTopAnime(page) }.map { it.toDomain(requestedPage = page) }
    }

    override suspend fun searchAnime(query: String): AppResult<AnimePage> = withContext(ioDispatcher) {
        safeApiCall { animeApi.searchAnime(query) }.map { it.toDomain(requestedPage = 1) }
    }
}
