package com.guga.asunaanimes.data.remote.dto

import com.google.gson.annotations.SerializedName

/**
 * Jikan returns `null` for most of these fields depending on the anime, and Gson happily writes
 * nulls into non-null Kotlin properties. Every field is therefore nullable and normalised by the
 * mapper before it reaches the domain layer.
 */
data class AnimeDto(
    @SerializedName("mal_id")
    val malId: Int?,
    @SerializedName("title")
    val title: String?,
    @SerializedName("images")
    val images: ImagesDto?,
    @SerializedName("synopsis")
    val synopsis: String?,
    @SerializedName("score")
    val score: Float?,
    @SerializedName("episodes")
    val episodes: Int?,
    @SerializedName("rating")
    val rating: String?,
    @SerializedName("url")
    val url: String?,
    @SerializedName("aired")
    val aired: AiredDto?
)
