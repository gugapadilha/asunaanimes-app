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
    val requestedQueries = mutableListOf<String>()

    var topAnimeResponse: (Int) -> AppResult<AnimePage> = { page ->
        AppResult.Success(
            AnimePage(
                animes = List(PAGE_SIZE) { index -> anime(malId = (page - 1) * PAGE_SIZE + index) },
                currentPage = page,
                hasNextPage = true
            )
        )
    }

    var searchResponse: (String) -> AppResult<AnimePage> = {
        AppResult.Success(AnimePage(animes = listOf(anime(malId = 999)), currentPage = 1, hasNextPage = false))
    }

    override suspend fun getTopAnime(page: Int): AppResult<AnimePage> {
        requestedPages += page
        return topAnimeResponse(page)
    }

    override suspend fun searchAnime(query: String): AppResult<AnimePage> {
        requestedQueries += query
        return searchResponse(query)
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

    fun setCollection(type: AnimeCollectionType, animes: List<Anime>) {
        collections.getValue(type).value = animes
    }
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
