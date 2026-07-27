package com.guga.asunaanimes.data.repository

import android.util.Log
import com.guga.asunaanimes.core.common.AppResult
import com.guga.asunaanimes.core.common.map
import com.guga.asunaanimes.core.coroutines.IoDispatcher
import com.guga.asunaanimes.core.common.AppError
import com.guga.asunaanimes.data.mapper.toDomain
import com.guga.asunaanimes.data.mapper.toDomainOrNull
import com.guga.asunaanimes.data.remote.AniListApi
import com.guga.asunaanimes.data.remote.AnimeApi
import com.guga.asunaanimes.data.remote.dto.AniListSearchRequest
import com.guga.asunaanimes.data.remote.dto.AniListSearchVariables
import com.guga.asunaanimes.data.remote.safeApiCall
import com.guga.asunaanimes.domain.model.Anime
import com.guga.asunaanimes.domain.model.AnimePage
import com.guga.asunaanimes.domain.repository.AnimeRepository
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

@Singleton
class AnimeRepositoryImpl @Inject constructor(
    private val animeApi: AnimeApi,
    private val aniListApi: AniListApi,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : AnimeRepository {

    override suspend fun getTopAnime(page: Int): AppResult<AnimePage> = withContext(ioDispatcher) {
        safeApiCall { animeApi.getTopAnime(page) }.map { it.toDomain(requestedPage = page) }
    }

    override suspend fun getSeasonalAnime(page: Int): AppResult<AnimePage> = withContext(ioDispatcher) {
        safeApiCall { animeApi.getSeasonalAnime(page) }.map { it.toDomain(requestedPage = page) }
    }

    override suspend fun getRecommendedAnime(page: Int): AppResult<AnimePage> = withContext(ioDispatcher) {
        safeApiCall { animeApi.getRecommendedAnime(page) }.map { it.toDomain(requestedPage = page) }
    }

    override suspend fun getAnimeById(id: Int): AppResult<Anime> = withContext(ioDispatcher) {
        when (val result = safeApiCall { animeApi.getAnimeById(id) }) {
            is AppResult.Failure -> result
            is AppResult.Success -> {
                val anime = result.data.data?.toDomainOrNull()
                if (anime == null) {
                    AppResult.Failure(AppError.Unknown())
                } else {
                    AppResult.Success(anime)
                }
            }
        }
    }

    /**
     * Jikan's `/anime?q=` endpoint is currently very flaky (frequent 504s). We still try it first,
     * then fall back to AniList GraphQL so title search keeps working for any anime.
     */
    override suspend fun searchAnime(query: String): AppResult<AnimePage> = withContext(ioDispatcher) {
        val jikanResult = safeApiCall {
            animeApi.searchAnime(query = query, limit = AnimeApi.DEFAULT_SEARCH_LIMIT)
        }.map { it.toDomain(requestedPage = 1) }

        if (jikanResult is AppResult.Success && jikanResult.data.animes.isNotEmpty()) {
            return@withContext jikanResult
        }

        if (jikanResult is AppResult.Failure) {
            Log.w(TAG, "Jikan search failed for \"$query\": ${jikanResult.error}. Falling back to AniList.")
        }

        safeApiCall {
            aniListApi.search(
                AniListSearchRequest(
                    query = AniListApi.SEARCH_QUERY,
                    variables = AniListSearchVariables(search = query)
                )
            )
        }.map { it.toDomain(requestedPage = 1) }
    }

    private companion object {
        const val TAG = "AnimeRepositoryImpl"
    }
}
