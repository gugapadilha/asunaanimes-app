package com.guga.asunaanimes.domain.usecase

import com.guga.asunaanimes.core.common.AppResult
import com.guga.asunaanimes.util.FakeAnimeRepository
import com.guga.asunaanimes.util.FakeSearchHistoryRepository
import com.guga.asunaanimes.util.anime
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
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
