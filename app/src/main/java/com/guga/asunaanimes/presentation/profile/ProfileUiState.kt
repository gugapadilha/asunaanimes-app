package com.guga.asunaanimes.presentation.profile

import com.guga.asunaanimes.domain.model.Anime

data class ProfileUiState(
    val userName: String = "",
    val avatarUri: String? = null,
    val watchedAnimes: List<Anime> = emptyList(),
    val favoriteAnimes: List<Anime> = emptyList(),
    val watchedCount: Int = 0,
    val favoriteCount: Int = 0,
    val isSavingName: Boolean = false
)
