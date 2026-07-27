package com.guga.asunaanimes.domain.model

/**
 * Anime as the app understands it, free of any transport or storage concern.
 *
 * Every field the remote API may omit is modelled as nullable so the presentation layer is
 * forced to handle missing data instead of crashing on it.
 */
data class Anime(
    val malId: Int,
    val title: String,
    val imageUrl: String,
    val synopsis: String?,
    val score: Float?,
    val episodes: Int?,
    val rating: String?,
    val detailsUrl: String?,
    val airedFrom: String?,
    val airedTo: String?
)
