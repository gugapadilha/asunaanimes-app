package com.guga.asunaanimes.data.remote.dto

import com.google.gson.annotations.SerializedName

data class AniListSearchRequest(
    val query: String,
    val variables: AniListSearchVariables
)

data class AniListSearchVariables(
    val search: String,
    val page: Int = 1,
    val perPage: Int = 25
)

data class AniListSearchResponse(
    @SerializedName("data")
    val data: AniListDataDto?
)

data class AniListDataDto(
    @SerializedName("Page")
    val page: AniListPageDto?
)

data class AniListPageDto(
    @SerializedName("pageInfo")
    val pageInfo: AniListPageInfoDto?,
    @SerializedName("media")
    val media: List<AniListMediaDto>?
)

data class AniListPageInfoDto(
    @SerializedName("hasNextPage")
    val hasNextPage: Boolean?,
    @SerializedName("currentPage")
    val currentPage: Int?
)

data class AniListMediaDto(
    @SerializedName("idMal")
    val idMal: Int?,
    @SerializedName("title")
    val title: AniListTitleDto?,
    @SerializedName("coverImage")
    val coverImage: AniListCoverDto?,
    @SerializedName("description")
    val description: String?,
    @SerializedName("episodes")
    val episodes: Int?,
    @SerializedName("averageScore")
    val averageScore: Int?,
    @SerializedName("siteUrl")
    val siteUrl: String?
)

data class AniListTitleDto(
    @SerializedName("romaji")
    val romaji: String?,
    @SerializedName("english")
    val english: String?
)

data class AniListCoverDto(
    @SerializedName("large")
    val large: String?,
    @SerializedName("medium")
    val medium: String?
)
