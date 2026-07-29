package com.guga.asunaanimes.data.local.model

import com.google.gson.annotations.SerializedName

/**
 * Flat persistence representation of an anime. Kept separate from [com.guga.asunaanimes.data.remote.dto.AnimeDto]
 * so changes in the Jikan payload never break already stored user collections.
 */
data class AnimeEntity(
    @SerializedName("mal_id")
    val malId: Int,
    @SerializedName("title")
    val title: String,
    @SerializedName("image_url")
    val imageUrl: String,
    @SerializedName("synopsis")
    val synopsis: String?,
    @SerializedName("score")
    val score: Float?,
    @SerializedName("episodes")
    val episodes: Int?,
    @SerializedName("rating")
    val rating: String?,
    @SerializedName("details_url")
    val detailsUrl: String?,
    @SerializedName("aired_from")
    val airedFrom: String?,
    @SerializedName("aired_to")
    val airedTo: String?,
    @SerializedName("user_score")
    val userScore: Int? = null
)
