package com.guga.asunaanimes.util

import com.guga.asunaanimes.core.common.AppError
import com.guga.asunaanimes.core.common.AppResult
import com.guga.asunaanimes.domain.model.AddToCollectionOutcome
import com.guga.asunaanimes.domain.model.Anime
import com.guga.asunaanimes.domain.model.AnimeCollectionType
import com.guga.asunaanimes.domain.model.AnimePage
import com.guga.asunaanimes.domain.repository.AnimeCollectionRepository
import com.guga.asunaanimes.domain.repository.AnimeRepository
import com.guga.asunaanimes.domain.repository.SearchHistoryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map

class FakeAnimeRepository : AnimeRepository {

    val requestedPages = mutableListOf<Int>()
    val requestedSeasonalPages = mutableListOf<Int>()
    val requestedRecommendedPages = mutableListOf<Int>()
    val requestedQueries = mutableListOf<String>()
    val requestedSearchPages = mutableListOf<Int>()

    var topAnimeResponse: (Int) -> AppResult<AnimePage> = { page ->
        AppResult.Success(
            AnimePage(
                animes = List(PAGE_SIZE) { index -> anime(malId = (page - 1) * PAGE_SIZE + index) },
                currentPage = page,
                hasNextPage = true
            )
        )
    }

    var seasonalAnimeResponse: (Int) -> AppResult<AnimePage> = { page ->
        AppResult.Success(
            AnimePage(
                animes = List(PAGE_SIZE) { index ->
                    anime(malId = 10_000 + (page - 1) * PAGE_SIZE + index, title = "Seasonal $index")
                },
                currentPage = page,
                hasNextPage = true
            )
        )
    }

    var recommendedAnimeResponse: (Int) -> AppResult<AnimePage> = { page ->
        AppResult.Success(
            AnimePage(
                animes = List(PAGE_SIZE) { index ->
                    anime(malId = 20_000 + (page - 1) * PAGE_SIZE + index, title = "Recommended $index")
                },
                currentPage = page,
                hasNextPage = true
            )
        )
    }

    var searchResponse: (String, Int) -> AppResult<AnimePage> = { _, page ->
        AppResult.Success(
            AnimePage(
                animes = listOf(anime(malId = 999, title = "Naruto")),
                currentPage = page,
                hasNextPage = page < 2
            )
        )
    }

    var animeByIdResponse: (Int) -> AppResult<Anime> = { id ->
        AppResult.Success(
            anime(
                malId = id,
                title = "Detailed $id",
                synopsis = "Full synopsis for $id"
            )
        )
    }

    val requestedAnimeIds = mutableListOf<Int>()

    override suspend fun getTopAnime(page: Int): AppResult<AnimePage> {
        requestedPages += page
        return topAnimeResponse(page)
    }

    override suspend fun getSeasonalAnime(page: Int): AppResult<AnimePage> {
        requestedSeasonalPages += page
        return seasonalAnimeResponse(page)
    }

    override suspend fun getRecommendedAnime(page: Int): AppResult<AnimePage> {
        requestedRecommendedPages += page
        return recommendedAnimeResponse(page)
    }

    override suspend fun getAnimeById(id: Int): AppResult<Anime> {
        requestedAnimeIds += id
        return animeByIdResponse(id)
    }

    override suspend fun searchAnime(query: String, page: Int): AppResult<AnimePage> {
        requestedQueries += query
        requestedSearchPages += page
        return searchResponse(query, page)
    }

    companion object {
        const val PAGE_SIZE = 25
    }
}

class FakeAnimeCollectionRepository : AnimeCollectionRepository {

    private val collections = AnimeCollectionType.values()
        .associateWith { MutableStateFlow(emptyList<Anime>()) }

    var failNextWrite = false

    override fun observeCollection(type: AnimeCollectionType): Flow<List<Anime>> =
        collections.getValue(type).map { it }

    override suspend fun add(
        type: AnimeCollectionType,
        anime: Anime
    ): AppResult<AddToCollectionOutcome> {
        if (failNextWrite) return AppResult.Failure(AppError.Storage())
        val state = collections.getValue(type)
        if (state.value.any { it.malId == anime.malId }) {
            return AppResult.Success(AddToCollectionOutcome.ALREADY_IN_COLLECTION)
        }
        state.value = state.value + anime
        return AppResult.Success(AddToCollectionOutcome.ADDED)
    }

    override suspend fun remove(type: AnimeCollectionType, anime: Anime): AppResult<Unit> {
        if (failNextWrite) return AppResult.Failure(AppError.Storage())
        val state = collections.getValue(type)
        state.value = state.value.filterNot { it.malId == anime.malId }
        return AppResult.Success(Unit)
    }

    override suspend fun updateUserScore(
        type: AnimeCollectionType,
        malId: Int,
        userScore: Int?
    ): AppResult<Boolean> {
        if (failNextWrite) return AppResult.Failure(AppError.Storage())
        val state = collections.getValue(type)
        val index = state.value.indexOfFirst { it.malId == malId }
        if (index < 0) return AppResult.Success(false)
        state.value = state.value.toMutableList().also { list ->
            list[index] = list[index].copy(userScore = userScore)
        }
        return AppResult.Success(true)
    }

    fun setCollection(type: AnimeCollectionType, animes: List<Anime>) {
        collections.getValue(type).value = animes
    }

    fun getCollection(type: AnimeCollectionType): List<Anime> =
        collections.getValue(type).value
}

class FakeSearchHistoryRepository : SearchHistoryRepository {

    private val queries = MutableStateFlow(emptyList<String>())
    val savedQueries = mutableListOf<String>()

    override fun observeRecentQueries(): Flow<List<String>> = queries

    override suspend fun saveQuery(query: String) {
        savedQueries += query
        queries.value = (listOf(query) + queries.value).distinct()
    }
}
