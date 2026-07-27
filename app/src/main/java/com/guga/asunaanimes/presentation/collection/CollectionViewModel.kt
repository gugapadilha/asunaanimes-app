package com.guga.asunaanimes.presentation.collection

import android.util.Log
import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.guga.asunaanimes.R
import com.guga.asunaanimes.core.common.AppResult
import com.guga.asunaanimes.domain.model.Anime
import com.guga.asunaanimes.domain.model.AnimeCollectionType
import com.guga.asunaanimes.domain.usecase.FilterAnimesByTitleUseCase
import com.guga.asunaanimes.domain.usecase.ObserveAnimeCollectionUseCase
import com.guga.asunaanimes.domain.usecase.ObserveSearchHistoryUseCase
import com.guga.asunaanimes.domain.usecase.RemoveAnimeFromCollectionUseCase
import com.guga.asunaanimes.domain.usecase.SaveSearchQueryUseCase
import com.guga.asunaanimes.presentation.common.UiMessage
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/**
 * Shared behaviour of the favorite and watched screens: observe a stored collection, filter it
 * locally and remove entries from it.
 */
abstract class CollectionViewModel(
    private val collectionType: AnimeCollectionType,
    @StringRes private val removedMessageResId: Int,
    private val filterAnimesByTitle: FilterAnimesByTitleUseCase,
    private val removeAnimeFromCollection: RemoveAnimeFromCollectionUseCase,
    private val saveSearchQuery: SaveSearchQueryUseCase,
    observeAnimeCollection: ObserveAnimeCollectionUseCase,
    observeSearchHistory: ObserveSearchHistoryUseCase
) : ViewModel() {

    private val query = MutableStateFlow("")
    private val selectedAnime = MutableStateFlow<Anime?>(null)

    private val _messages = Channel<UiMessage>(Channel.BUFFERED)
    val messages = _messages.receiveAsFlow()

    val uiState: StateFlow<CollectionUiState> = combine(
        observeAnimeCollection(collectionType),
        observeSearchHistory(),
        query,
        selectedAnime
    ) { animes, recentSearches, currentQuery, selected ->
        CollectionUiState(
            animes = filterAnimesByTitle(animes, currentQuery),
            recentSearches = recentSearches,
            // Keeps the detail sheet consistent when the selected anime leaves the collection.
            selectedAnime = selected?.takeIf { anime -> animes.any { it.malId == anime.malId } }
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(STOP_TIMEOUT_MS),
        initialValue = CollectionUiState()
    )

    fun onQueryChanged(rawQuery: String) {
        query.value = rawQuery.trim()
    }

    fun onSearch(rawQuery: String) {
        val sanitizedQuery = rawQuery.trim()
        query.value = sanitizedQuery
        viewModelScope.launch { saveSearchQuery(sanitizedQuery) }
    }

    fun onAnimeSelected(anime: Anime) {
        selectedAnime.value = anime
    }

    fun onDetailsDismissed() {
        selectedAnime.value = null
    }

    fun onRemoveSelectedAnime() {
        val anime = selectedAnime.value ?: return
        viewModelScope.launch {
            val message = when (val result = removeAnimeFromCollection(collectionType, anime)) {
                is AppResult.Success -> removedMessageResId
                is AppResult.Failure -> {
                    Log.w(TAG, "Unable to remove anime from $collectionType: ${result.error}")
                    R.string.message_storage_error
                }
            }
            selectedAnime.value = null
            _messages.send(UiMessage(message))
        }
    }

    private companion object {
        const val TAG = "CollectionViewModel"
        const val STOP_TIMEOUT_MS = 5_000L
    }
}
