package com.guga.asunaanimes.data.mapper

import com.guga.asunaanimes.data.local.model.AnimeEntity
import com.guga.asunaanimes.data.remote.dto.AnimeDto
import com.guga.asunaanimes.data.remote.dto.AnimePageDto
import com.guga.asunaanimes.domain.model.Anime
import com.guga.asunaanimes.domain.model.AnimePage

private const val UNKNOWN_TITLE = "Title not available"

/**
 * Entries without an id or artwork cannot be rendered nor persisted reliably, so they are dropped
 * instead of being turned into half-broken items.
 */
fun AnimeDto.toDomainOrNull(): Anime? {
    val id = malId ?: return null
    val image = images?.jpg?.imageUrl ?: images?.webp?.imageUrl ?: return null
    return Anime(
        malId = id,
        title = title?.takeIf { it.isNotBlank() } ?: UNKNOWN_TITLE,
        imageUrl = image,
        synopsis = synopsis?.takeIf { it.isNotBlank() },
        score = score,
        episodes = episodes,
        rating = rating?.takeIf { it.isNotBlank() },
        detailsUrl = url?.takeIf { it.isNotBlank() },
        airedFrom = aired?.from?.takeIf { it.isNotBlank() },
        airedTo = aired?.to?.takeIf { it.isNotBlank() }
    )
}

fun AnimePageDto.toDomain(requestedPage: Int): AnimePage = AnimePage(
    animes = data.orEmpty().mapNotNull { it.toDomainOrNull() }.distinctBy { it.malId },
    currentPage = pagination?.currentPage ?: requestedPage,
    hasNextPage = pagination?.hasNextPage ?: false
)

fun AnimeEntity.toDomain(): Anime = Anime(
    malId = malId,
    title = title,
    imageUrl = imageUrl,
    synopsis = synopsis,
    score = score,
    episodes = episodes,
    rating = rating,
    detailsUrl = detailsUrl,
    airedFrom = airedFrom,
    airedTo = airedTo
)

fun Anime.toEntity(): AnimeEntity = AnimeEntity(
    malId = malId,
    title = title,
    imageUrl = imageUrl,
    synopsis = synopsis,
    score = score,
    episodes = episodes,
    rating = rating,
    detailsUrl = detailsUrl,
    airedFrom = airedFrom,
    airedTo = airedTo
)
