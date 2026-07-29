package com.guga.asunaanimes.presentation.search

import com.guga.asunaanimes.R
import com.guga.asunaanimes.core.common.AppError
import com.guga.asunaanimes.core.common.AppResult
import com.guga.asunaanimes.domain.model.AnimeBrowseMode
import com.guga.asunaanimes.domain.model.AnimeCollectionType
import com.guga.asunaanimes.domain.model.AnimePage
import com.guga.asunaanimes.domain.usecase.AddAnimeToCollectionUseCase
import com.guga.asunaanimes.domain.usecase.FilterAnimesByTitleUseCase
import com.guga.asunaanimes.domain.usecase.GetAnimeDetailsUseCase
import com.guga.asunaanimes.domain.usecase.GetRecommendedAnimeUseCase
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
        getRecommendedAnime = GetRecommendedAnimeUseCase(animeRepository),
        getAnimeDetails = GetAnimeDetailsUseCase(animeRepository),
        searchAnime = SearchAnimeUseCase(animeRepository),
        filterAnimesByTitle = FilterAnimesByTitleUseCase(),
        addAnimeToCollection = AddAnimeToCollectionUseCase(collectionRepository),
        saveSearchQuery = SaveSearchQueryUseCase(searchHistoryRepository),
        observeSearchHistory = ObserveSearchHistoryUseCase(searchHistoryRepository)
    )

    @Test
    fun `loads enough top pages on start to fill the grid`() {
        val viewModel = createViewModel()

        assertEquals(listOf(1, 2), animeRepository.requestedPages)
        assertEquals(50, viewModel.uiState.value.animes.size)
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
        assertEquals(listOf(1), animeRepository.requestedSearchPages)
        assertEquals(listOf(999), viewModel.uiState.value.animes.map { it.malId })
        assertTrue(viewModel.uiState.value.canLoadMore)
        assertTrue(viewModel.uiState.value.isSearchActive)
        assertEquals(listOf("naruto"), searchHistoryRepository.savedQueries)
    }

    @Test
    fun `loading more appends the next search page`() {
        val viewModel = createViewModel()
        animeRepository.searchResponse = { _, page ->
            AppResult.Success(
                AnimePage(
                    animes = listOf(anime(malId = 900 + page, title = "Hit $page")),
                    currentPage = page,
                    hasNextPage = page < 3
                )
            )
        }
        viewModel.onSearch("naruto")
        animeRepository.requestedSearchPages.clear()

        viewModel.onLoadMore()

        assertEquals(listOf(2), animeRepository.requestedSearchPages)
        assertEquals(listOf(901, 902), viewModel.uiState.value.animes.map { it.malId })
        assertTrue(viewModel.uiState.value.canLoadMore)
    }

    @Test
    fun `an empty query restores the browse listing without saving it to history`() {
        val viewModel = createViewModel()
        viewModel.onSearch("naruto")
        animeRepository.requestedPages.clear()

        viewModel.onSearch("   ")

        assertEquals(listOf(1, 2), animeRepository.requestedPages)
        assertEquals(listOf("naruto"), searchHistoryRepository.savedQueries)
        assertFalse(viewModel.uiState.value.isSearchActive)
    }

    @Test
    fun `loading more appends the next browse page`() {
        val viewModel = createViewModel()
        animeRepository.requestedPages.clear()

        viewModel.onLoadMore()

        assertEquals(listOf(3), animeRepository.requestedPages)
        assertEquals(75, viewModel.uiState.value.animes.size)
    }

    @Test
    fun `seasonal filter loads the seasonal catalog`() {
        val viewModel = createViewModel()

        viewModel.onBrowseModeSelected(AnimeBrowseMode.SEASONAL)

        assertEquals(listOf(1, 2), animeRepository.requestedSeasonalPages)
        assertEquals(AnimeBrowseMode.SEASONAL, viewModel.uiState.value.browseMode)
        assertEquals(50, viewModel.uiState.value.animes.size)
        assertTrue(viewModel.uiState.value.animes.first().title.startsWith("Seasonal"))
    }

    @Test
    fun `recommendations filter loads the recommendations catalog`() {
        val viewModel = createViewModel()

        viewModel.onBrowseModeSelected(AnimeBrowseMode.RECOMMENDATIONS)

        assertEquals(listOf(1, 2), animeRepository.requestedRecommendedPages)
        assertEquals(AnimeBrowseMode.RECOMMENDATIONS, viewModel.uiState.value.browseMode)
        assertEquals(50, viewModel.uiState.value.animes.size)
        assertTrue(viewModel.uiState.value.animes.first().title.startsWith("Recommended"))
    }

    @Test
    fun `failed pagination keeps canLoadMore so scrolling can retry`() {
        val viewModel = createViewModel()
        animeRepository.topAnimeResponse = { AppResult.Failure(AppError.Network()) }

        viewModel.onLoadMore()

        assertTrue(viewModel.uiState.value.canLoadMore)
        assertFalse(viewModel.uiState.value.isLoadingMore)
        assertEquals(50, viewModel.uiState.value.animes.size)
    }

    @Test
    fun `search failure falls back to browse cache titles`() = runTest(mainDispatcherRule.testDispatcher) {
        val viewModel = createViewModel()
        animeRepository.searchResponse = { _, _ -> AppResult.Failure(AppError.Network()) }

        viewModel.onSearch("Anime 0")

        assertTrue(viewModel.uiState.value.animes.isNotEmpty())
        assertTrue(viewModel.uiState.value.isSearchActive)
        assertEquals(R.string.message_search_error, viewModel.messages.first().textResId)
    }

    @Test
    fun `selecting a stub anime loads full details`() {
        val viewModel = createViewModel()
        val stub = anime(malId = 42, title = "Stub")

        viewModel.onAnimeSelected(stub)

        assertEquals(listOf(42), animeRepository.requestedAnimeIds)
        assertEquals("Full synopsis for 42", viewModel.uiState.value.selectedAnime?.synopsis)
        assertFalse(viewModel.uiState.value.isDetailsLoading)
    }

    @Test
    fun `adding to favorites also adds the anime to watched`() =
        runTest(mainDispatcherRule.testDispatcher) {
            val viewModel = createViewModel()
            viewModel.onAnimeSelected(anime(1))

            viewModel.onAddToCollection(AnimeCollectionType.FAVORITE, userScore = 8)

            assertEquals(R.string.message_added_to_favorites, viewModel.messages.first().textResId)
            assertEquals(listOf(1), collectionRepository.getCollection(AnimeCollectionType.FAVORITE).map { it.malId })
            assertEquals(listOf(1), collectionRepository.getCollection(AnimeCollectionType.WATCHED).map { it.malId })
            assertEquals(8, collectionRepository.getCollection(AnimeCollectionType.FAVORITE).first().userScore)
        }

    @Test
    fun `adding the same anime twice reports it is already in the collection`() =
        runTest(mainDispatcherRule.testDispatcher) {
            val viewModel = createViewModel()
            viewModel.onAnimeSelected(anime(1))

            viewModel.onAddToCollection(AnimeCollectionType.FAVORITE, userScore = 9)
            assertEquals(R.string.message_added_to_favorites, viewModel.messages.first().textResId)

            viewModel.onAddToCollection(AnimeCollectionType.FAVORITE, userScore = 9)
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
