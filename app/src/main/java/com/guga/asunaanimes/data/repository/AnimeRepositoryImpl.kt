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
        safeApiCall { animeApi.getTopAnime(page = page) }.map { it.toDomain(requestedPage = page) }
    }

    override suspend fun getSeasonalAnime(page: Int): AppResult<AnimePage> = withContext(ioDispatcher) {
        safeApiCall { animeApi.getSeasonalAnime(page) }.map { it.toDomain(requestedPage = page) }
    }

    /**
     * Recent Jikan "recommendations" pages are extremely duplicate-heavy, so infinite scroll stalls
     * around one useful page. Prefer AniList popularity (clean pagination); fall back to Jikan
     * top-by-popularity if AniList is unavailable.
     */
    override suspend fun getRecommendedAnime(page: Int): AppResult<AnimePage> = withContext(ioDispatcher) {
        val aniListResult = safeApiCall {
            aniListApi.search(
                AniListSearchRequest(
                    query = AniListApi.POPULAR_QUERY,
                    variables = AniListSearchVariables(page = page, perPage = PAGE_SIZE)
                )
            )
        }.map { it.toDomain(requestedPage = page) }

        if (aniListResult is AppResult.Success && aniListResult.data.animes.isNotEmpty()) {
            return@withContext aniListResult
        }

        if (aniListResult is AppResult.Failure) {
            Log.w(TAG, "AniList popular page $page failed: ${aniListResult.error}. Falling back to Jikan.")
        }

        safeApiCall {
            animeApi.getTopAnime(page = page, filter = JIKAN_POPULARITY_FILTER)
        }.map { it.toDomain(requestedPage = page) }
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
     * Title search prefers AniList (Jikan `/anime?q=` is frequently flaky with 504s). Jikan is
     * used only when AniList fails or returns nothing, so titles outside the browse cache still
     * resolve (e.g. "Sword Art Online").
     */
    override suspend fun searchAnime(query: String): AppResult<AnimePage> = withContext(ioDispatcher) {
        val aniListResult = safeApiCall {
            aniListApi.search(
                AniListSearchRequest(
                    query = AniListApi.SEARCH_QUERY,
                    variables = AniListSearchVariables(search = query, page = 1, perPage = PAGE_SIZE)
                )
            )
        }.map { it.toDomain(requestedPage = 1) }

        if (aniListResult is AppResult.Success && aniListResult.data.animes.isNotEmpty()) {
            return@withContext aniListResult
        }

        if (aniListResult is AppResult.Failure) {
            Log.w(TAG, "AniList search failed for \"$query\": ${aniListResult.error}. Falling back to Jikan.")
        }

        val jikanResult = safeApiCall {
            animeApi.searchAnime(
                query = query,
                limit = AnimeApi.DEFAULT_SEARCH_LIMIT,
                page = 1
            )
        }.map { it.toDomain(requestedPage = 1) }

        when {
            jikanResult is AppResult.Success -> jikanResult
            aniListResult is AppResult.Success -> aniListResult
            else -> jikanResult
        }
    }

    private companion object {
        const val TAG = "AnimeRepositoryImpl"
        const val PAGE_SIZE = 25
        const val JIKAN_POPULARITY_FILTER = "bypopularity"
    }
}
