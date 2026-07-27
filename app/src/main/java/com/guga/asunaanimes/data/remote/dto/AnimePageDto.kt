package com.guga.asunaanimes.data.remote.dto

import com.google.gson.annotations.SerializedName

data class AnimePageDto(
    @SerializedName("data")
    val data: List<AnimeDto>?,
    @SerializedName("pagination")
    val pagination: PaginationDto?
)

data class PaginationDto(
    @SerializedName("current_page")
    val currentPage: Int?,
    @SerializedName("has_next_page")
    val hasNextPage: Boolean?,
    @SerializedName("last_visible_page")
    val lastVisiblePage: Int?
)
