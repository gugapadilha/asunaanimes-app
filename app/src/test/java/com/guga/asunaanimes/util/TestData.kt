package com.guga.asunaanimes.util

import com.guga.asunaanimes.domain.model.Anime

fun anime(
    malId: Int,
    title: String = "Anime $malId",
    imageUrl: String = "https://cdn.example/$malId.jpg"
) = Anime(
    malId = malId,
    title = title,
    imageUrl = imageUrl,
    synopsis = null,
    score = null,
    episodes = null,
    rating = null,
    detailsUrl = null,
    airedFrom = null,
    airedTo = null
)
