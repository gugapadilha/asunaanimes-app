package com.guga.asunaanimes.data.mapper

import com.guga.asunaanimes.data.local.model.AnimeEntity
import com.guga.asunaanimes.data.remote.dto.AniListMediaDto
import com.guga.asunaanimes.data.remote.dto.AniListSearchResponse
import com.guga.asunaanimes.data.remote.dto.AnimeDto
import com.guga.asunaanimes.data.remote.dto.AnimePageDto
import com.guga.asunaanimes.data.remote.dto.RecommendationEntryDto
import com.guga.asunaanimes.data.remote.dto.RecommendationsPageDto
import com.guga.asunaanimes.domain.model.Anime
import com.guga.asunaanimes.domain.model.AnimePage

private const val UNKNOWN_TITLE = "Title not available"
private const val FULL_PAGE_SIZE = 25

/**
 * Entries without an id or artwork cannot be rendered nor persisted reliably, so they are dropped
 * instead of being turned into half-broken items.
 */
fun AnimeDto.toDomainOrNull(): Anime? {
    val id = malId ?: return null
    val image = images?.jpg?.imageUrl
        ?: images?.jpg?.largeImageUrl
        ?: images?.webp?.imageUrl
        ?: images?.webp?.largeImageUrl
        ?: return null
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

fun AnimePageDto.toDomain(requestedPage: Int): AnimePage {
    val animes = data.orEmpty().mapNotNull { it.toDomainOrNull() }.distinctBy { it.malId }
    return AnimePage(
        animes = animes,
        currentPage = pagination?.currentPage ?: requestedPage,
        // Prefer the API flag, but keep scrolling alive when a full page arrives without pagination.
        hasNextPage = pagination?.hasNextPage ?: (animes.size >= FULL_PAGE_SIZE)
    )
}

fun RecommendationEntryDto.toDomainOrNull(): Anime? {
    val id = malId ?: return null
    val image = images?.jpg?.imageUrl
        ?: images?.jpg?.largeImageUrl
        ?: images?.webp?.imageUrl
        ?: images?.webp?.largeImageUrl
        ?: return null
    return Anime(
        malId = id,
        title = title?.takeIf { it.isNotBlank() } ?: UNKNOWN_TITLE,
        imageUrl = image,
        synopsis = null,
        score = null,
        episodes = null,
        rating = null,
        detailsUrl = url?.takeIf { it.isNotBlank() },
        airedFrom = null,
        airedTo = null
    )
}

fun RecommendationsPageDto.toDomain(requestedPage: Int): AnimePage {
    val animes = data.orEmpty()
        .flatMap { recommendation -> recommendation.entry.orEmpty() }
        .mapNotNull { it.toDomainOrNull() }
        .distinctBy { it.malId }
    return AnimePage(
        animes = animes,
        currentPage = pagination?.currentPage ?: requestedPage,
        hasNextPage = pagination?.hasNextPage ?: (animes.isNotEmpty())
    )
}

fun AniListMediaDto.toDomainOrNull(): Anime? {
    val id = idMal ?: return null
    val image = coverImage?.large ?: coverImage?.medium ?: return null
    val resolvedTitle = title?.english?.takeIf { it.isNotBlank() }
        ?: title?.romaji?.takeIf { it.isNotBlank() }
        ?: return null
    return Anime(
        malId = id,
        title = resolvedTitle,
        imageUrl = image,
        synopsis = description?.replace(Regex("<[^>]*>"), " ")?.trim()?.takeIf { it.isNotBlank() },
        score = averageScore?.let { it / 10f },
        episodes = episodes,
        rating = null,
        detailsUrl = siteUrl?.takeIf { it.isNotBlank() },
        airedFrom = null,
        airedTo = null
    )
}

fun AniListSearchResponse.toDomain(requestedPage: Int): AnimePage {
    val page = data?.page
    val animes = page?.media.orEmpty().mapNotNull { it.toDomainOrNull() }.distinctBy { it.malId }
    return AnimePage(
        animes = animes,
        currentPage = page?.pageInfo?.currentPage ?: requestedPage,
        hasNextPage = page?.pageInfo?.hasNextPage ?: false
    )
}

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
