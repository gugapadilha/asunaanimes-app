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
import com.guga.asunaanimes.data.remote.dto.AnimePageDto
import com.guga.asunaanimes.data.remote.safeApiCall
import com.guga.asunaanimes.domain.model.Anime
import com.guga.asunaanimes.domain.model.AnimePage
import com.guga.asunaanimes.domain.repository.AnimeRepository
import java.util.Calendar
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

    /**
     * Prefer AniList score ranking. Jikan `/top/anime` frequently returns 504 after a few pages,
     * which freezes infinite scroll around ~75 items.
     */
    override suspend fun getTopAnime(page: Int): AppResult<AnimePage> = withContext(ioDispatcher) {
        fetchAniListFirst(
            page = page,
            query = AniListApi.TOP_SCORE_QUERY,
            variables = AniListSearchVariables(page = page, perPage = PAGE_SIZE),
            logLabel = "top score"
        ) {
            animeApi.getTopAnime(page = page)
        }
    }

    /**
     * Prefer AniList current-season catalog for the same reason as top: Jikan pagination is flaky.
     */
    override suspend fun getSeasonalAnime(page: Int): AppResult<AnimePage> = withContext(ioDispatcher) {
        val (season, year) = currentAnimeSeason()
        fetchAniListFirst(
            page = page,
            query = AniListApi.SEASONAL_QUERY,
            variables = AniListSearchVariables(
                page = page,
                perPage = PAGE_SIZE,
                season = season,
                seasonYear = year
            ),
            logLabel = "seasonal"
        ) {
            animeApi.getSeasonalAnime(page)
        }
    }

    /**
     * Recent Jikan "recommendations" pages are extremely duplicate-heavy, so infinite scroll stalls
     * around one useful page. Prefer AniList popularity (clean pagination); fall back to Jikan
     * top-by-popularity if AniList is unavailable.
     */
    override suspend fun getRecommendedAnime(page: Int): AppResult<AnimePage> = withContext(ioDispatcher) {
        fetchAniListFirst(
            page = page,
            query = AniListApi.POPULAR_QUERY,
            variables = AniListSearchVariables(page = page, perPage = PAGE_SIZE),
            logLabel = "popular"
        ) {
            animeApi.getTopAnime(page = page, filter = JIKAN_POPULARITY_FILTER)
        }
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
    override suspend fun searchAnime(query: String, page: Int): AppResult<AnimePage> =
        withContext(ioDispatcher) {
            fetchAniListFirst(
                page = page,
                query = AniListApi.SEARCH_QUERY,
                variables = AniListSearchVariables(
                    search = query,
                    page = page,
                    perPage = PAGE_SIZE
                ),
                logLabel = "search \"$query\""
            ) {
                animeApi.searchAnime(
                    query = query,
                    limit = AnimeApi.DEFAULT_SEARCH_LIMIT,
                    page = page
                )
            }
        }

    /**
     * Tries AniList first. On failure or an empty page, falls back to Jikan so browse/search keep
     * working when either provider is degraded.
     */
    private suspend fun fetchAniListFirst(
        page: Int,
        query: String,
        variables: AniListSearchVariables,
        logLabel: String,
        jikanCall: suspend () -> AnimePageDto
    ): AppResult<AnimePage> {
        val aniListResult = safeApiCall {
            aniListApi.search(AniListSearchRequest(query = query, variables = variables))
        }.map { it.toDomain(requestedPage = page) }

        if (aniListResult is AppResult.Success && aniListResult.data.animes.isNotEmpty()) {
            return aniListResult
        }

        if (aniListResult is AppResult.Failure) {
            Log.w(TAG, "AniList $logLabel page $page failed: ${aniListResult.error}. Falling back to Jikan.")
        }

        val jikanResult = safeApiCall { jikanCall() }.map { it.toDomain(requestedPage = page) }
        return when {
            jikanResult is AppResult.Success -> jikanResult
            aniListResult is AppResult.Success -> aniListResult
            else -> jikanResult
        }
    }

    private companion object {
        const val TAG = "AnimeRepositoryImpl"
        const val PAGE_SIZE = 25
        const val JIKAN_POPULARITY_FILTER = "bypopularity"

        /**
         * Standard TV season windows used by MAL / AniList (month is Calendar 0-based).
         */
        fun currentAnimeSeason(nowMs: Long = System.currentTimeMillis()): Pair<String, Int> {
            val calendar = Calendar.getInstance().apply { timeInMillis = nowMs }
            val month = calendar.get(Calendar.MONTH)
            val year = calendar.get(Calendar.YEAR)
            val season = when (month) {
                Calendar.JANUARY, Calendar.FEBRUARY, Calendar.MARCH -> "WINTER"
                Calendar.APRIL, Calendar.MAY, Calendar.JUNE -> "SPRING"
                Calendar.JULY, Calendar.AUGUST, Calendar.SEPTEMBER -> "SUMMER"
                else -> "FALL"
            }
            return season to year
        }
    }
}
