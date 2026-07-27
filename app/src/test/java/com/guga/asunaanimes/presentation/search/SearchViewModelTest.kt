package com.guga.asunaanimes.presentation.search

import com.guga.asunaanimes.R
import com.guga.asunaanimes.core.common.AppError
import com.guga.asunaanimes.core.common.AppResult
import com.guga.asunaanimes.domain.model.AnimeBrowseMode
import com.guga.asunaanimes.domain.model.AnimeCollectionType
import com.guga.asunaanimes.domain.usecase.AddAnimeToCollectionUseCase
import com.guga.asunaanimes.domain.usecase.FilterAnimesByTitleUseCase
import com.guga.asunaanimes.domain.usecase.GetSeasonalAnimeUseCase
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
        getSeasonalAnime = GetSeasonalAnimeUseCase(animeRepository),
        searchAnime = SearchAnimeUseCase(animeRepository),
        filterAnimesByTitle = FilterAnimesByTitleUseCase(),
        addAnimeToCollection = AddAnimeToCollectionUseCase(collectionRepository),
        saveSearchQuery = SaveSearchQueryUseCase(searchHistoryRepository),
        observeSearchHistory = ObserveSearchHistoryUseCase(searchHistoryRepository)
    )

    @Test
    fun `loads the first top page on start`() {
        val viewModel = createViewModel()

        assertEquals(listOf(1), animeRepository.requestedPages)
        assertEquals(25, viewModel.uiState.value.animes.size)
        assertFalse(viewModel.uiState.value.isLoading)
        assertTrue(viewModel.uiState.value.canLoadMore)
        assertEquals(AnimeBrowseMode.TOP, viewModel.uiState.value.browseMode)
    }

    @Test
    fun `reports failure when the first browse page fails`() = runTest(mainDispatcherRule.testDispatcher) {
        animeRepository.topAnimeResponse = { AppResult.Failure(AppError.Network()) }

        val viewModel = createViewModel()

        assertEquals(listOf(1), animeRepository.requestedPages)
        assertTrue(viewModel.uiState.value.animes.isEmpty())
        assertFalse(viewModel.uiState.value.isLoading)
        assertEquals(R.string.message_search_error, viewModel.messages.first().textResId)
    }

    @Test
    fun `searching hits the remote API instead of filtering the browse page`() {
        val viewModel = createViewModel()

        viewModel.onSearch("  naruto ")

        assertEquals(listOf("naruto"), animeRepository.requestedQueries)
        assertEquals(listOf(999), viewModel.uiState.value.animes.map { it.malId })
        assertFalse(viewModel.uiState.value.canLoadMore)
        assertTrue(viewModel.uiState.value.isSearchActive)
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
    fun `an empty query restores the browse listing without saving it to history`() {
        val viewModel = createViewModel()
        viewModel.onSearch("naruto")
        animeRepository.requestedPages.clear()

        viewModel.onSearch("   ")

        assertEquals(listOf(1), animeRepository.requestedPages)
        assertEquals(listOf("naruto"), searchHistoryRepository.savedQueries)
        assertFalse(viewModel.uiState.value.isSearchActive)
    }

    @Test
    fun `loading more appends the next browse page`() {
        val viewModel = createViewModel()
        animeRepository.requestedPages.clear()

        viewModel.onLoadMore()

        assertEquals(listOf(2), animeRepository.requestedPages)
        assertEquals(50, viewModel.uiState.value.animes.size)
    }

    @Test
    fun `seasonal filter loads the seasonal catalog`() {
        val viewModel = createViewModel()

        viewModel.onBrowseModeSelected(AnimeBrowseMode.SEASONAL)

        assertEquals(listOf(1), animeRepository.requestedSeasonalPages)
        assertEquals(AnimeBrowseMode.SEASONAL, viewModel.uiState.value.browseMode)
        assertEquals(25, viewModel.uiState.value.animes.size)
        assertTrue(viewModel.uiState.value.animes.first().title.startsWith("Seasonal"))
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
}
