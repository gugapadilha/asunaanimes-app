package com.guga.asunaanimes.presentation.search

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.guga.asunaanimes.R
import com.guga.asunaanimes.core.common.AppError
import com.guga.asunaanimes.core.common.AppResult
import com.guga.asunaanimes.domain.model.AddToCollectionOutcome
import com.guga.asunaanimes.domain.model.Anime
import com.guga.asunaanimes.domain.model.AnimeBrowseMode
import com.guga.asunaanimes.domain.model.AnimeCollectionType
import com.guga.asunaanimes.domain.model.AnimePage
import com.guga.asunaanimes.domain.usecase.AddAnimeToCollectionUseCase
import com.guga.asunaanimes.domain.usecase.FilterAnimesByTitleUseCase
import com.guga.asunaanimes.domain.usecase.GetSeasonalAnimeUseCase
import com.guga.asunaanimes.domain.usecase.GetTopAnimeUseCase
import com.guga.asunaanimes.domain.usecase.ObserveSearchHistoryUseCase
import com.guga.asunaanimes.domain.usecase.SaveSearchQueryUseCase
import com.guga.asunaanimes.domain.usecase.SearchAnimeUseCase
import com.guga.asunaanimes.presentation.common.UiMessage
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
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
    private val getSeasonalAnime: GetSeasonalAnimeUseCase,
    private val searchAnime: SearchAnimeUseCase,
    private val filterAnimesByTitle: FilterAnimesByTitleUseCase,
    private val addAnimeToCollection: AddAnimeToCollectionUseCase,
    private val saveSearchQuery: SaveSearchQueryUseCase,
    observeSearchHistory: ObserveSearchHistoryUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(SearchUiState())
    val uiState: StateFlow<SearchUiState> = _uiState.asStateFlow()

    private val _messages = Channel<UiMessage>(Channel.BUFFERED)
    val messages = _messages.receiveAsFlow()

    private var listingMode = ListingMode.BROWSE
    private var browseMode = AnimeBrowseMode.TOP
    private var nextPage = GetTopAnimeUseCase.FIRST_PAGE
    private var loadJob: Job? = null
    private var debounceJob: Job? = null
    private var browseCache: List<Anime> = emptyList()

    init {
        observeSearchHistory()
            .onEach { queries -> _uiState.update { it.copy(recentSearches = queries) } }
            .launchIn(viewModelScope)

        loadBrowse(restart = true)
    }

    fun onBrowseModeSelected(mode: AnimeBrowseMode) {
        if (browseMode == mode && listingMode == ListingMode.BROWSE && !_uiState.value.isLoading) {
            return
        }
        debounceJob?.cancel()
        loadJob?.cancel()
        browseMode = mode
        listingMode = ListingMode.BROWSE
        _uiState.update {
            it.copy(
                browseMode = mode,
                isSearchActive = false,
                searchBoxResetKey = it.searchBoxResetKey + 1
            )
        }
        loadBrowse(restart = true)
    }

    /**
     * Live typing path. Waits for a short debounce and then always hits the remote search API —
     * never filters only the currently loaded browse page.
     */
    fun onQueryChanged(rawQuery: String) {
        val query = rawQuery.trim()
        debounceJob?.cancel()

        if (query.isEmpty()) {
            loadJob?.cancel()
            listingMode = ListingMode.BROWSE
            _uiState.update { it.copy(isSearchActive = false) }
            loadBrowse(restart = true)
            return
        }

        if (query.length < MIN_REMOTE_QUERY_LENGTH) return

        debounceJob = viewModelScope.launch {
            delay(SEARCH_DEBOUNCE_MS)
            performRemoteSearch(query, persistHistory = false)
        }
    }

    fun onSearch(rawQuery: String) {
        val query = rawQuery.trim()
        debounceJob?.cancel()
        loadJob?.cancel()

        if (query.isEmpty()) {
            listingMode = ListingMode.BROWSE
            _uiState.update { it.copy(isSearchActive = false) }
            loadBrowse(restart = true)
            return
        }

        performRemoteSearch(query, persistHistory = true)
    }

    fun onLoadMore() {
        if (listingMode != ListingMode.BROWSE) return
        if (!_uiState.value.canLoadMore) return
        loadBrowse(restart = false)
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
            _uiState.update { it.copy(isCollectionDialogVisible = false) }
            _messages.send(UiMessage(message))
        }
    }

    private fun performRemoteSearch(query: String, persistHistory: Boolean) {
        loadJob?.cancel()
        listingMode = ListingMode.SEARCH_RESULTS
        _uiState.update {
            it.copy(
                isLoading = true,
                isSearchActive = true,
                canLoadMore = false
            )
        }

        loadJob = viewModelScope.launch {
            val result = searchWithRetry(query)
            when (result) {
                is AppResult.Success -> {
                    val ranked = rankRemoteResults(result.data.animes, query)
                    _uiState.update {
                        it.copy(
                            animes = ranked,
                            isLoading = false,
                            canLoadMore = false,
                            isSearchActive = true
                        )
                    }
                }

                is AppResult.Failure -> {
                    Log.w(TAG, "Unable to search for \"$query\": ${result.error}")
                    // Soft fallback only — never pretend the local page is the full catalog.
                    val fallback = filterAnimesByTitle(browseCache, query)
                    _uiState.update {
                        it.copy(
                            animes = fallback,
                            isLoading = false,
                            canLoadMore = false,
                            isSearchActive = true
                        )
                    }
                    _messages.send(UiMessage(R.string.message_search_error))
                }
            }
            if (persistHistory) {
                saveSearchQuery(query)
            }
        }
    }

    private fun rankRemoteResults(animes: List<Anime>, query: String): List<Anime> {
        val ranked = filterAnimesByTitle(animes, query)
        // Keep every remote hit; ranking only reorders the ones that clearly match the query.
        if (ranked.isEmpty()) return animes
        val rankedIds = ranked.mapTo(mutableSetOf()) { it.malId }
        return ranked + animes.filterNot { rankedIds.contains(it.malId) }
    }

    private suspend fun searchWithRetry(query: String): AppResult<AnimePage> {
        var lastFailure: AppResult.Failure? = null
        repeat(SEARCH_RETRY_COUNT) { attempt ->
            when (val result = searchAnime(query)) {
                is AppResult.Success -> return result
                is AppResult.Failure -> {
                    lastFailure = result
                    if (attempt < SEARCH_RETRY_COUNT - 1) {
                        delay(SEARCH_RETRY_DELAY_MS * (attempt + 1))
                    }
                }
            }
        }
        return lastFailure ?: AppResult.Failure(AppError.Unknown())
    }

    private fun loadBrowse(restart: Boolean) {
        if (!restart && loadJob?.isActive == true) return

        if (restart) {
            loadJob?.cancel()
            listingMode = ListingMode.BROWSE
            nextPage = GetTopAnimeUseCase.FIRST_PAGE
            browseCache = emptyList()
            _uiState.update {
                it.copy(
                    animes = emptyList(),
                    isLoading = true,
                    canLoadMore = false,
                    browseMode = browseMode,
                    isSearchActive = false
                )
            }
        }

        loadJob = viewModelScope.launch {
            val result = fetchBrowsePage(nextPage)
            if (result is AppResult.Failure) {
                Log.w(TAG, "Unable to load $browseMode page $nextPage: ${result.error}")
                if (restart) {
                    _uiState.update { it.copy(isLoading = false, canLoadMore = false) }
                    _messages.send(UiMessage(R.string.message_search_error))
                }
                return@launch
            }

            val page = (result as AppResult.Success).data
            nextPage = page.currentPage + 1
            browseCache = browseCache.plusDistinct(page.animes)
            if (listingMode == ListingMode.BROWSE) {
                _uiState.update { state ->
                    state.copy(
                        animes = browseCache,
                        canLoadMore = page.hasNextPage,
                        isLoading = false,
                        browseMode = browseMode,
                        isSearchActive = false
                    )
                }
            }
        }
    }

    private suspend fun fetchBrowsePage(page: Int): AppResult<AnimePage> =
        when (browseMode) {
            AnimeBrowseMode.TOP -> getTopAnime(page)
            AnimeBrowseMode.SEASONAL -> getSeasonalAnime(page)
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

    private enum class ListingMode { BROWSE, SEARCH_RESULTS }

    private companion object {
        const val TAG = "SearchViewModel"
        const val MIN_REMOTE_QUERY_LENGTH = 2
        const val SEARCH_DEBOUNCE_MS = 450L
        const val SEARCH_RETRY_COUNT = 3
        const val SEARCH_RETRY_DELAY_MS = 800L
    }
}
