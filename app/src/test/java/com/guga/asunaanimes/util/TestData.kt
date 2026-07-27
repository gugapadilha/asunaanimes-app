package com.guga.asunaanimes.util

import com.guga.asunaanimes.domain.model.Anime

fun anime(
    malId: Int,
    title: String = "Anime $malId",
    imageUrl: String = "https://cdn.example/$malId.jpg",
    synopsis: String? = null,
    score: Float? = null,
    episodes: Int? = null,
    rating: String? = null
) = Anime(
    malId = malId,
    title = title,
    imageUrl = imageUrl,
    synopsis = synopsis,
    score = score,
    episodes = episodes,
    rating = rating,
    detailsUrl = null,
    airedFrom = null,
    airedTo = null
)
