package com.guga.asunaanimes.presentation.search

import com.guga.asunaanimes.R
import com.guga.asunaanimes.core.common.AppError
import com.guga.asunaanimes.core.common.AppResult
import com.guga.asunaanimes.domain.model.AnimeCollectionType
import com.guga.asunaanimes.domain.model.AnimePage
import com.guga.asunaanimes.domain.usecase.AddAnimeToCollectionUseCase
import com.guga.asunaanimes.domain.usecase.GetTopAnimeUseCase
import com.guga.asunaanimes.domain.usecase.ObserveSearchHistoryUseCase
import com.guga.asunaanimes.domain.usecase.SaveSearchQueryUseCase
import com.guga.asunaanimes.domain.usecase.SearchAnimeUseCase
import com.guga.asunaanimes.util.FakeAnimeCollectionRepository
import com.guga.asunaanimes.util.FakeAnimeRepository
import com.guga.asunaanimes.util.FakeSearchHistoryRepository
import com.guga.asunaanimes.util.MainDispatcherRule
import com.guga.asunaanimes.util.anime
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class SearchViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val animeRepository = FakeAnimeRepository()
    private val collectionRepository = FakeAnimeCollectionRepository()
    private val searchHistoryRepository = FakeSearchHistoryRepository()

    private fun createViewModel() = SearchViewModel(
        getTopAnime = GetTopAnimeUseCase(animeRepository),
        searchAnime = SearchAnimeUseCase(animeRepository),
        addAnimeToCollection = AddAnimeToCollectionUseCase(collectionRepository),
        saveSearchQuery = SaveSearchQueryUseCase(searchHistoryRepository),
        observeSearchHistory = ObserveSearchHistoryUseCase(searchHistoryRepository)
    )

    @Test
    fun `preloads the first pages and stops loading`() {
        val viewModel = createViewModel()

        assertEquals(listOf(1, 2, 3, 4), animeRepository.requestedPages)
        assertEquals(100, viewModel.uiState.value.animes.size)
        assertFalse(viewModel.uiState.value.isLoading)
        assertTrue(viewModel.uiState.value.canLoadMore)
    }

    @Test
    fun `stops preloading as soon as a page fails`() {
        animeRepository.topAnimeResponse = { page ->
            if (page >= 3) AppResult.Failure(AppError.Network()) else defaultPage(page)
        }

        val viewModel = createViewModel()

        assertEquals(listOf(1, 2, 3), animeRepository.requestedPages)
        assertEquals(50, viewModel.uiState.value.animes.size)
        assertFalse(viewModel.uiState.value.isLoading)
    }

    @Test
    fun `never lists the same anime twice when pages overlap`() {
        animeRepository.topAnimeResponse = { page ->
            AppResult.Success(
                AnimePage(animes = listOf(anime(1), anime(2)), currentPage = page, hasNextPage = true)
            )
        }

        val viewModel = createViewModel()

        assertEquals(listOf(1, 2), viewModel.uiState.value.animes.map { it.malId })
    }

    @Test
    fun `searching replaces the listing, disables pagination and records the query`() {
        val viewModel = createViewModel()

        viewModel.onSearch("  naruto ")

        assertEquals(listOf("naruto"), animeRepository.requestedQueries)
        assertEquals(listOf(999), viewModel.uiState.value.animes.map { it.malId })
        assertFalse(viewModel.uiState.value.canLoadMore)
        assertEquals(listOf("naruto"), searchHistoryRepository.savedQueries)
    }

    @Test
    fun `pagination is ignored while showing search results`() {
        val viewModel = createViewModel()
        viewModel.onSearch("naruto")
        val pagesBefore = animeRepository.requestedPages.size

        viewModel.onLoadMore()

        assertEquals(pagesBefore, animeRepository.requestedPages.size)
    }

    @Test
    fun `an empty query restores the top anime listing without saving it to history`() {
        val viewModel = createViewModel()
        viewModel.onSearch("naruto")
        animeRepository.requestedPages.clear()

        viewModel.onSearch("   ")

        assertEquals(listOf(1, 2, 3, 4), animeRepository.requestedPages)
        assertEquals(listOf("naruto"), searchHistoryRepository.savedQueries)
    }

    @Test
    fun `loading more appends the next page`() {
        val viewModel = createViewModel()
        animeRepository.requestedPages.clear()

        viewModel.onLoadMore()

        assertEquals(listOf(5), animeRepository.requestedPages)
        assertEquals(125, viewModel.uiState.value.animes.size)
    }

    @Test
    fun `adding the same anime twice reports it is already in the collection`() =
        runTest(mainDispatcherRule.testDispatcher) {
            val viewModel = createViewModel()
            viewModel.onAnimeSelected(anime(1))

            viewModel.onAddToCollection(AnimeCollectionType.FAVORITE)
            assertEquals(R.string.message_added_to_favorites, viewModel.messages.first().textResId)

            viewModel.onAddToCollection(AnimeCollectionType.FAVORITE)
            assertEquals(
                R.string.message_already_in_favorites,
                viewModel.messages.first().textResId
            )
        }

    @Test
    fun `dismissing the details sheet also closes the collection dialog`() {
        val viewModel = createViewModel()
        viewModel.onAnimeSelected(anime(1))
        viewModel.onAddToListClick()

        viewModel.onDetailsDismissed()

        assertNull(viewModel.uiState.value.selectedAnime)
        assertFalse(viewModel.uiState.value.isCollectionDialogVisible)
    }

    private fun defaultPage(page: Int) = AppResult.Success(
        AnimePage(
            animes = List(FakeAnimeRepository.PAGE_SIZE) { index ->
                anime(malId = (page - 1) * FakeAnimeRepository.PAGE_SIZE + index)
            },
            currentPage = page,
            hasNextPage = true
        )
    )
}
