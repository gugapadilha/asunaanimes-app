package com.guga.asunaanimes.domain.model

data class AnimePage(
    val animes: List<Anime>,
    val currentPage: Int,
    val hasNextPage: Boolean
)
