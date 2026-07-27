package com.guga.asunaanimes.data.remote.dto

import com.google.gson.annotations.SerializedName

data class RecommendationsPageDto(
    @SerializedName("data")
    val data: List<RecommendationDto>?,
    @SerializedName("pagination")
    val pagination: PaginationDto?
)

data class RecommendationDto(
    @SerializedName("mal_id")
    val malId: String?,
    @SerializedName("entry")
    val entry: List<RecommendationEntryDto>?
)

data class RecommendationEntryDto(
    @SerializedName("mal_id")
    val malId: Int?,
    @SerializedName("title")
    val title: String?,
    @SerializedName("url")
    val url: String?,
    @SerializedName("images")
    val images: ImagesDto?
)
