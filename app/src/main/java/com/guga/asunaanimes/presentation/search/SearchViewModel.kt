package com.guga.asunaanimes.presentation.search

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.guga.asunaanimes.R
import com.guga.asunaanimes.core.common.AppResult
import com.guga.asunaanimes.domain.model.AddToCollectionOutcome
import com.guga.asunaanimes.domain.model.Anime
import com.guga.asunaanimes.domain.model.AnimeCollectionType
import com.guga.asunaanimes.domain.usecase.AddAnimeToCollectionUseCase
import com.guga.asunaanimes.domain.usecase.GetTopAnimeUseCase
import com.guga.asunaanimes.domain.usecase.ObserveSearchHistoryUseCase
import com.guga.asunaanimes.domain.usecase.SaveSearchQueryUseCase
import com.guga.asunaanimes.domain.usecase.SearchAnimeUseCase
import com.guga.asunaanimes.presentation.common.UiMessage
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val getTopAnime: GetTopAnimeUseCase,
    private val searchAnime: SearchAnimeUseCase,
    private val addAnimeToCollection: AddAnimeToCollectionUseCase,
    private val saveSearchQuery: SaveSearchQueryUseCase,
    observeSearchHistory: ObserveSearchHistoryUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(SearchUiState())
    val uiState: StateFlow<SearchUiState> = _uiState.asStateFlow()

    private val _messages = Channel<UiMessage>(Channel.BUFFERED)
    val messages = _messages.receiveAsFlow()

    private var listingMode = ListingMode.TOP_ANIME
    private var nextPage = GetTopAnimeUseCase.FIRST_PAGE
    private var loadJob: Job? = null

    init {
        observeSearchHistory()
            .onEach { queries -> _uiState.update { it.copy(recentSearches = queries) } }
            .launchIn(viewModelScope)

        loadTopAnime(restart = true)
    }

    fun onSearch(rawQuery: String) {
        val query = rawQuery.trim()
        loadJob?.cancel()

        if (query.isEmpty()) {
            loadTopAnime(restart = true)
            return
        }

        loadJob = viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            when (val result = searchAnime(query)) {
                is AppResult.Success -> {
                    listingMode = ListingMode.SEARCH_RESULTS
                    _uiState.update {
                        it.copy(animes = result.data.animes, isLoading = false, canLoadMore = false)
                    }
                }

                is AppResult.Failure -> {
                    Log.w(TAG, "Unable to search for \"$query\": ${result.error}")
                    _uiState.update { it.copy(animes = emptyList(), isLoading = false, canLoadMore = false) }
                }
            }
            saveSearchQuery(query)
        }
    }

    /** Called when the user scrolls to the end of the top anime listing. */
    fun onLoadMore() {
        if (listingMode != ListingMode.TOP_ANIME) return
        if (!_uiState.value.canLoadMore) return
        loadTopAnime(restart = false)
    }

    fun onAnimeSelected(anime: Anime) {
        _uiState.update { it.copy(selectedAnime = anime) }
    }

    fun onDetailsDismissed() {
        _uiState.update { it.copy(selectedAnime = null, isCollectionDialogVisible = false) }
    }

    fun onAddToListClick() {
        _uiState.update { it.copy(isCollectionDialogVisible = true) }
    }

    fun onCollectionDialogDismissed() {
        _uiState.update { it.copy(isCollectionDialogVisible = false) }
    }

    fun onAddToCollection(type: AnimeCollectionType) {
        val anime = _uiState.value.selectedAnime ?: return
        viewModelScope.launch {
            val message = when (val result = addAnimeToCollection(type, anime)) {
                is AppResult.Success -> messageFor(type, result.data)
                is AppResult.Failure -> {
                    Log.w(TAG, "Unable to add anime to $type: ${result.error}")
                    R.string.message_storage_error
                }
            }
            _messages.send(UiMessage(message))
        }
    }

    /**
     * Loads the top anime listing. A restart resets the pagination and pre-loads the first pages,
     * matching the amount of content the screen has always shown on entry.
     */
    private fun loadTopAnime(restart: Boolean) {
        if (!restart && loadJob?.isActive == true) return

        if (restart) {
            loadJob?.cancel()
            listingMode = ListingMode.TOP_ANIME
            nextPage = GetTopAnimeUseCase.FIRST_PAGE
            _uiState.update {
                it.copy(animes = emptyList(), isLoading = true, canLoadMore = false)
            }
        }

        loadJob = viewModelScope.launch {
            val pagesToLoad = if (restart) INITIAL_PAGE_COUNT else 1
            var loadedPages = 0
            while (loadedPages < pagesToLoad) {
                val result = getTopAnime(nextPage)
                if (result is AppResult.Failure) {
                    Log.w(TAG, "Unable to load top anime page $nextPage: ${result.error}")
                    break
                }
                val page = (result as AppResult.Success).data
                nextPage = page.currentPage + 1
                loadedPages++
                _uiState.update { state ->
                    state.copy(
                        animes = state.animes.plusDistinct(page.animes),
                        canLoadMore = page.hasNextPage
                    )
                }
            }
            _uiState.update { it.copy(isLoading = false) }
        }
    }

    private fun messageFor(type: AnimeCollectionType, outcome: AddToCollectionOutcome): Int =
        when (type) {
            AnimeCollectionType.FAVORITE -> when (outcome) {
                AddToCollectionOutcome.ADDED -> R.string.message_added_to_favorites
                AddToCollectionOutcome.ALREADY_IN_COLLECTION -> R.string.message_already_in_favorites
            }

            AnimeCollectionType.WATCHED -> when (outcome) {
                AddToCollectionOutcome.ADDED -> R.string.message_added_to_watched
                AddToCollectionOutcome.ALREADY_IN_COLLECTION -> R.string.message_already_in_watched
            }
        }

    private fun List<Anime>.plusDistinct(newAnimes: List<Anime>): List<Anime> {
        val knownIds = mapTo(mutableSetOf()) { it.malId }
        return this + newAnimes.filter { knownIds.add(it.malId) }
    }

    private enum class ListingMode { TOP_ANIME, SEARCH_RESULTS }

    private companion object {
        const val TAG = "SearchViewModel"
        const val INITIAL_PAGE_COUNT = 4
    }
}
