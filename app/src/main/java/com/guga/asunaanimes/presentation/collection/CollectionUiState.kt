package com.guga.asunaanimes.presentation.collection

import com.guga.asunaanimes.domain.model.Anime

data class CollectionUiState(
    val animes: List<Anime> = emptyList(),
    val recentSearches: List<String> = emptyList(),
    val selectedAnime: Anime? = null,
    val isScoreDialogVisible: Boolean = false
)
