package com.guga.asunaanimes.presentation.search

import com.guga.asunaanimes.domain.model.Anime
import com.guga.asunaanimes.domain.model.AnimeBrowseMode

data class SearchUiState(
    val animes: List<Anime> = emptyList(),
    val isLoading: Boolean = true,
    val canLoadMore: Boolean = false,
    val browseMode: AnimeBrowseMode = AnimeBrowseMode.TOP,
    val isSearchActive: Boolean = false,
    val recentSearches: List<String> = emptyList(),
    val selectedAnime: Anime? = null,
    val isCollectionDialogVisible: Boolean = false,
    val searchBoxResetKey: Int = 0
)
