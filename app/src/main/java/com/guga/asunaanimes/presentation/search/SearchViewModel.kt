package com.guga.asunaanimes.presentation.search

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.guga.asunaanimes.R
import com.guga.asunaanimes.core.common.AppResult
import com.guga.asunaanimes.domain.model.AddToCollectionOutcome
import com.guga.asunaanimes.domain.model.Anime
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
import com.guga.asunaanimes.domain.usecase.needsDetailsEnrichment
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
    private val getRecommendedAnime: GetRecommendedAnimeUseCase,
    private val getAnimeDetails: GetAnimeDetailsUseCase,
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
    private var detailsJob: Job? = null
    private var debounceJob: Job? = null
    private var cooldownRetryJob: Job? = null
    private var browseCache: List<Anime> = emptyList()
    /** Ignores finally-blocks from cancelled browse jobs so they cannot clear a newer load. */
    private var loadGeneration = 0
    private var loadMoreCooldownUntilMs = 0L
    /** One automatic retry per failure streak; further attempts need a scroll / EndOfGridEffect pulse. */
    private var autoRetryPending = false

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
        cooldownRetryJob?.cancel()
        browseMode = mode
        listingMode = ListingMode.BROWSE
        _uiState.update {
            it.copy(
                browseMode = mode,
                isSearchActive = false,
                isLoadingMore = false,
                searchBoxResetKey = it.searchBoxResetKey + 1
            )
        }
        loadBrowse(restart = true)
    }

    fun onQueryChanged(rawQuery: String) {
        val query = rawQuery.trim()
        debounceJob?.cancel()

        if (query.isEmpty()) {
            loadJob?.cancel()
            listingMode = ListingMode.BROWSE
            _uiState.update { it.copy(isSearchActive = false, isLoadingMore = false) }
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
            _uiState.update { it.copy(isSearchActive = false, isLoadingMore = false) }
            loadBrowse(restart = true)
            return
        }

        performRemoteSearch(query, persistHistory = true)
    }

    fun onLoadMore() {
        if (listingMode != ListingMode.BROWSE) return
        if (!_uiState.value.canLoadMore) return
        if (_uiState.value.isLoading || _uiState.value.isLoadingMore) return
        if (loadJob?.isActive == true) return
        if (System.currentTimeMillis() < loadMoreCooldownUntilMs) return
        // Manual / UI pulse: allow one automatic retry again if this attempt fails.
        autoRetryPending = false
        loadBrowse(restart = false)
    }

    fun onAnimeSelected(anime: Anime) {
        detailsJob?.cancel()
        _uiState.update {
            it.copy(
                selectedAnime = anime,
                isDetailsLoading = anime.needsDetailsEnrichment()
            )
        }
        if (!anime.needsDetailsEnrichment()) return

        detailsJob = viewModelScope.launch {
            when (val result = getAnimeDetails(anime)) {
                is AppResult.Success -> {
                    val detailed = result.data
                    browseCache = browseCache.map { cached ->
                        if (cached.malId == detailed.malId) detailed else cached
                    }
                    _uiState.update { state ->
                        state.copy(
                            selectedAnime = state.selectedAnime
                                ?.takeIf { it.malId == detailed.malId }
                                ?.let { detailed },
                            animes = state.animes.map { item ->
                                if (item.malId == detailed.malId) detailed else item
                            },
                            isDetailsLoading = false
                        )
                    }
                }

                is AppResult.Failure -> {
                    Log.w(TAG, "Unable to load details for ${anime.malId}: ${result.error}")
                    _uiState.update { it.copy(isDetailsLoading = false) }
                }
            }
        }
    }

    fun onDetailsDismissed() {
        detailsJob?.cancel()
        _uiState.update {
            it.copy(
                selectedAnime = null,
                isCollectionDialogVisible = false,
                isDetailsLoading = false
            )
        }
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
        cooldownRetryJob?.cancel()
        listingMode = ListingMode.SEARCH_RESULTS
        _uiState.update {
            it.copy(
                isLoading = true,
                isLoadingMore = false,
                isSearchActive = true,
                canLoadMore = false
            )
        }

        loadJob = viewModelScope.launch {
            try {
                when (val result = searchAnime(query)) {
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
                        if (ranked.isEmpty()) {
                            _messages.send(UiMessage(R.string.message_search_empty))
                        }
                    }

                    is AppResult.Failure -> {
                        Log.w(TAG, "Unable to search for \"$query\": ${result.error}")
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
            } finally {
                _uiState.update { it.copy(isLoading = false, isLoadingMore = false) }
            }
        }
    }

    private fun rankRemoteResults(animes: List<Anime>, query: String): List<Anime> {
        val ranked = filterAnimesByTitle(animes, query)
        if (ranked.isEmpty()) return animes
        val rankedIds = ranked.mapTo(mutableSetOf()) { it.malId }
        return ranked + animes.filterNot { rankedIds.contains(it.malId) }
    }

    private fun loadBrowse(restart: Boolean) {
        if (!restart && loadJob?.isActive == true) return
        if (!restart && (_uiState.value.isLoading || _uiState.value.isLoadingMore)) return

        if (restart) {
            loadJob?.cancel()
            cooldownRetryJob?.cancel()
            listingMode = ListingMode.BROWSE
            nextPage = GetTopAnimeUseCase.FIRST_PAGE
            browseCache = emptyList()
            loadMoreCooldownUntilMs = 0L
            autoRetryPending = false
            _uiState.update {
                it.copy(
                    animes = emptyList(),
                    isLoading = true,
                    isLoadingMore = false,
                    canLoadMore = false,
                    browseMode = browseMode,
                    isSearchActive = false
                )
            }
        } else {
            _uiState.update { it.copy(isLoadingMore = true) }
        }

        val startPage = nextPage
        val generation = ++loadGeneration
        loadJob = viewModelScope.launch {
            var hasNext = false
            var encounteredFailure = false
            try {
                var pageToFetch = startPage
                var emptyStreak = 0

                // Keep fetching while:
                // - pages are all duplicates (legacy recommendation pages), or
                // - the grid is still too short to scroll (first paint / large screens).
                while (emptyStreak <= MAX_EMPTY_PAGE_SKIPS) {
                    val result = fetchBrowsePage(pageToFetch)
                    if (result is AppResult.Failure) {
                        Log.w(TAG, "Unable to load $browseMode page $pageToFetch: ${result.error}")
                        encounteredFailure = true
                        if (restart && browseCache.isEmpty()) {
                            _messages.send(UiMessage(R.string.message_search_error))
                            hasNext = false
                        } else {
                            // Keep pagination armed so a later scroll / cooldown retry can continue.
                            hasNext = true
                        }
                        break
                    }

                    val page = (result as AppResult.Success).data
                    val beforeCount = browseCache.size
                    browseCache = browseCache.plusDistinct(page.animes)
                    val added = browseCache.size - beforeCount
                    hasNext = page.hasNextPage
                    nextPage = pageToFetch + 1
                    autoRetryPending = false

                    if (generation == loadGeneration && listingMode == ListingMode.BROWSE) {
                        _uiState.update { state ->
                            state.copy(
                                animes = browseCache,
                                canLoadMore = hasNext,
                                browseMode = browseMode,
                                isSearchActive = false
                            )
                        }
                    }

                    val needsMoreToFill = hasNext && browseCache.size < MIN_GRID_FILL
                    val shouldSkipEmptyPage = hasNext && added == 0
                    if (!needsMoreToFill && !shouldSkipEmptyPage) break

                    if (added == 0) {
                        emptyStreak++
                        // Pause only when burning through duplicate-heavy pages.
                        delay(PAGE_RETRY_GAP_MS)
                    } else {
                        emptyStreak = 0
                    }
                    pageToFetch = nextPage
                }

                if (generation == loadGeneration && listingMode == ListingMode.BROWSE) {
                    _uiState.update {
                        it.copy(
                            animes = browseCache,
                            canLoadMore = hasNext,
                            browseMode = browseMode,
                            isSearchActive = false
                        )
                    }
                }
            } finally {
                if (encounteredFailure && hasNext) {
                    scheduleLoadMoreRetry()
                }
                if (generation == loadGeneration) {
                    _uiState.update { it.copy(isLoading = false, isLoadingMore = false) }
                }
            }
        }
    }

    /**
     * After a failed page, [EndOfGridEffect] may already be "near end" and will not re-fire when
     * the cooldown expires (distinctUntilChanged). One automatic retry unsticks Top at ~25 items
     * without spinning forever if the API stays down.
     */
    private fun scheduleLoadMoreRetry() {
        loadMoreCooldownUntilMs = System.currentTimeMillis() + LOAD_MORE_FAILURE_COOLDOWN_MS
        if (autoRetryPending) return
        autoRetryPending = true
        cooldownRetryJob?.cancel()
        cooldownRetryJob = viewModelScope.launch {
            delay(LOAD_MORE_FAILURE_COOLDOWN_MS)
            if (listingMode != ListingMode.BROWSE) return@launch
            if (!_uiState.value.canLoadMore) return@launch
            if (_uiState.value.isLoading || _uiState.value.isLoadingMore) return@launch
            if (loadJob?.isActive == true) return@launch
            loadBrowse(restart = false)
        }
    }

    private suspend fun fetchBrowsePage(page: Int): AppResult<AnimePage> =
        when (browseMode) {
            AnimeBrowseMode.TOP -> getTopAnime(page)
            AnimeBrowseMode.SEASONAL -> getSeasonalAnime(page)
            AnimeBrowseMode.RECOMMENDATIONS -> getRecommendedAnime(page)
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
        const val MAX_EMPTY_PAGE_SKIPS = 3
        const val PAGE_RETRY_GAP_MS = 250L
        const val MIN_GRID_FILL = 30
        const val LOAD_MORE_FAILURE_COOLDOWN_MS = 2_500L
    }
}
