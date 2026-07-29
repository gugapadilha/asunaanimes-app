package com.guga.asunaanimes.domain.usecase

import com.guga.asunaanimes.core.common.AppResult
import com.guga.asunaanimes.domain.model.AnimeCollectionType
import com.guga.asunaanimes.util.FakeAnimeCollectionRepository
import com.guga.asunaanimes.util.FakeAnimeRepository
import com.guga.asunaanimes.util.FakeSearchHistoryRepository
import com.guga.asunaanimes.util.anime
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class SearchAnimeUseCaseTest {

    private val repository = FakeAnimeRepository()
    private val searchAnime = SearchAnimeUseCase(repository)

    @Test
    fun `does not hit the network for a blank query`() = runTest {
        val result = searchAnime("   ")

        assertTrue(repository.requestedQueries.isEmpty())
        assertEquals(emptyList<Int>(), (result as AppResult.Success).data.animes.map { it.malId })
    }

    @Test
    fun `trims the query before searching`() = runTest {
        searchAnime("  naruto  ")

        assertEquals(listOf("naruto"), repository.requestedQueries)
        assertEquals(listOf(1), repository.requestedSearchPages)
    }

    @Test
    fun `forwards the requested page`() = runTest {
        searchAnime("naruto", page = 3)

        assertEquals(listOf(3), repository.requestedSearchPages)
    }
}

class SaveSearchQueryUseCaseTest {

    private val repository = FakeSearchHistoryRepository()
    private val saveSearchQuery = SaveSearchQueryUseCase(repository)

    @Test
    fun `ignores blank queries`() = runTest {
        saveSearchQuery("")
        saveSearchQuery("    ")

        assertTrue(repository.savedQueries.isEmpty())
    }

    @Test
    fun `stores the trimmed query`() = runTest {
        saveSearchQuery(" one piece ")

        assertEquals(listOf("one piece"), repository.savedQueries)
    }
}

class FilterAnimesByTitleUseCaseTest {

    private val filterAnimes = FilterAnimesByTitleUseCase()

    private val animes = listOf(
        anime(malId = 1, title = "Sword Art Online"),
        anime(malId = 2, title = "Naruto"),
        anime(malId = 3, title = "sword of the stranger")
    )

    @Test
    fun `returns everything when the query is blank`() {
        assertEquals(animes, filterAnimes(animes, "   "))
    }

    @Test
    fun `matches titles ignoring case and position`() {
        val result = filterAnimes(animes, "sword")

        assertEquals(listOf(1, 3), result.map { it.malId })
    }

    @Test
    fun `matches title words by prefix so short queries still find the anime`() {
        val result = filterAnimes(animes, "sw")

        assertEquals(listOf(1, 3), result.map { it.malId })
    }
}

class GetTopAnimeUseCaseTest {

    private val repository = FakeAnimeRepository()
    private val getTopAnime = GetTopAnimeUseCase(repository)

    @Test
    fun `never requests a page below the first one`() = runTest {
        getTopAnime(0)
        getTopAnime(-3)

        assertEquals(listOf(1, 1), repository.requestedPages)
    }
}

class AddAnimeToCollectionUseCaseTest {

    private val repository = FakeAnimeCollectionRepository()
    private val addAnime = AddAnimeToCollectionUseCase(repository)

    @Test
    fun `favoriting an anime also places it in watched`() = runTest {
        val result = addAnime(AnimeCollectionType.FAVORITE, anime(42), userScore = 9)

        assertTrue(result is AppResult.Success)
        assertEquals(listOf(42), repository.getCollection(AnimeCollectionType.FAVORITE).map { it.malId })
        assertEquals(listOf(42), repository.getCollection(AnimeCollectionType.WATCHED).map { it.malId })
        assertEquals(9, repository.getCollection(AnimeCollectionType.FAVORITE).first().userScore)
        assertEquals(9, repository.getCollection(AnimeCollectionType.WATCHED).first().userScore)
    }

    @Test
    fun `adding only to watched does not touch favorites`() = runTest {
        addAnime(AnimeCollectionType.WATCHED, anime(7), userScore = 7)

        assertEquals(emptyList<Int>(), repository.getCollection(AnimeCollectionType.FAVORITE).map { it.malId })
        assertEquals(listOf(7), repository.getCollection(AnimeCollectionType.WATCHED).map { it.malId })
        assertEquals(7, repository.getCollection(AnimeCollectionType.WATCHED).first().userScore)
    }

    @Test
    fun `rejects scores outside the 1 to 10 range`() = runTest {
        val result = addAnime(AnimeCollectionType.WATCHED, anime(3), userScore = 11)

        assertTrue(result is AppResult.Failure)
        assertEquals(emptyList<Int>(), repository.getCollection(AnimeCollectionType.WATCHED).map { it.malId })
    }

    @Test
    fun `allows skipping the score`() = runTest {
        val result = addAnime(AnimeCollectionType.WATCHED, anime(5), userScore = null)

        assertTrue(result is AppResult.Success)
        assertNull(repository.getCollection(AnimeCollectionType.WATCHED).first().userScore)
    }
}
