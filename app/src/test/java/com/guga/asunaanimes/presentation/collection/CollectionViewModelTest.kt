package com.guga.asunaanimes.presentation.collection

import com.guga.asunaanimes.R
import com.guga.asunaanimes.domain.model.AnimeCollectionType
import com.guga.asunaanimes.domain.usecase.FilterAnimesByTitleUseCase
import com.guga.asunaanimes.domain.usecase.ObserveAnimeCollectionUseCase
import com.guga.asunaanimes.domain.usecase.ObserveSearchHistoryUseCase
import com.guga.asunaanimes.domain.usecase.RemoveAnimeFromCollectionUseCase
import com.guga.asunaanimes.domain.usecase.SaveSearchQueryUseCase
import com.guga.asunaanimes.util.FakeAnimeCollectionRepository
import com.guga.asunaanimes.util.FakeSearchHistoryRepository
import com.guga.asunaanimes.util.MainDispatcherRule
import com.guga.asunaanimes.util.anime
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class CollectionViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val collectionRepository = FakeAnimeCollectionRepository()
    private val searchHistoryRepository = FakeSearchHistoryRepository()

    private val storedAnimes = listOf(
        anime(malId = 1, title = "Sword Art Online"),
        anime(malId = 2, title = "Naruto")
    )

    @Test
    fun `exposes the stored collection`() = runCollectionTest {
        val viewModel = createViewModel()
        keepSubscribed(viewModel)

        assertEquals(listOf(1, 2), viewModel.uiState.value.animes.map { it.malId })
    }

    @Test
    fun `filters the collection locally and keeps the query out of history when blank`() =
        runCollectionTest {
            val viewModel = createViewModel()
            keepSubscribed(viewModel)

            viewModel.onSearch("sword")
            assertEquals(listOf(1), viewModel.uiState.value.animes.map { it.malId })
            assertEquals(listOf("sword"), searchHistoryRepository.savedQueries)

            viewModel.onSearch("  ")
            assertEquals(listOf(1, 2), viewModel.uiState.value.animes.map { it.malId })
            assertEquals(listOf("sword"), searchHistoryRepository.savedQueries)
        }

    @Test
    fun `removing the selected anime updates the list, clears the selection and notifies`() =
        runCollectionTest {
            val viewModel = createViewModel()
            keepSubscribed(viewModel)
            viewModel.onAnimeSelected(storedAnimes.first())

            viewModel.onRemoveSelectedAnime()

            assertEquals(listOf(2), viewModel.uiState.value.animes.map { it.malId })
            assertNull(viewModel.uiState.value.selectedAnime)
            assertEquals(
                R.string.message_removed_from_favorites,
                viewModel.messages.first().textResId
            )
        }

    @Test
    fun `reports a message when the storage write fails`() = runCollectionTest {
        val viewModel = createViewModel()
        keepSubscribed(viewModel)
        viewModel.onAnimeSelected(storedAnimes.first())
        collectionRepository.failNextWrite = true

        viewModel.onRemoveSelectedAnime()

        assertEquals(R.string.message_storage_error, viewModel.messages.first().textResId)
        assertTrue(viewModel.uiState.value.animes.isNotEmpty())
    }

    private fun createViewModel() = FavoriteViewModel(
        filterAnimesByTitle = FilterAnimesByTitleUseCase(),
        removeAnimeFromCollection = RemoveAnimeFromCollectionUseCase(collectionRepository),
        saveSearchQuery = SaveSearchQueryUseCase(searchHistoryRepository),
        observeAnimeCollection = ObserveAnimeCollectionUseCase(collectionRepository),
        observeSearchHistory = ObserveSearchHistoryUseCase(searchHistoryRepository)
    )

    /** [kotlinx.coroutines.flow.SharingStarted.WhileSubscribed] only emits with an active collector. */
    private fun TestScope.keepSubscribed(viewModel: CollectionViewModel) {
        backgroundScope.launch { viewModel.uiState.collect {} }
    }

    private fun runCollectionTest(body: suspend TestScope.() -> Unit) =
        runTest(mainDispatcherRule.testDispatcher) {
            collectionRepository.setCollection(AnimeCollectionType.FAVORITE, storedAnimes)
            body()
        }
}
