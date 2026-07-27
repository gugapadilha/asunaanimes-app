package com.guga.asunaanimes.data.mapper

import com.guga.asunaanimes.data.remote.dto.AiredDto
import com.guga.asunaanimes.data.remote.dto.AnimeDto
import com.guga.asunaanimes.data.remote.dto.AnimePageDto
import com.guga.asunaanimes.data.remote.dto.ImageUrlsDto
import com.guga.asunaanimes.data.remote.dto.ImagesDto
import com.guga.asunaanimes.data.remote.dto.PaginationDto
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class AnimeMappersTest {

    @Test
    fun `maps a fully populated anime`() {
        val dto = animeDto(malId = 1, title = "Steins;Gate")

        val anime = dto.toDomainOrNull()

        requireNotNull(anime)
        assertEquals(1, anime.malId)
        assertEquals("Steins;Gate", anime.title)
        assertEquals("https://cdn.example/1.jpg", anime.imageUrl)
    }

    @Test
    fun `falls back to a placeholder title when the api omits it`() {
        val anime = animeDto(malId = 1, title = null).toDomainOrNull()

        assertEquals("Title not available", anime?.title)
    }

    @Test
    fun `drops entries without an id or artwork`() {
        assertNull(animeDto(malId = null).toDomainOrNull())
        assertNull(animeDto(malId = 1, images = null).toDomainOrNull())
    }

    @Test
    fun `keeps optional fields nullable instead of inventing values`() {
        val anime = animeDto(malId = 1, synopsis = "  ", airedTo = null).toDomainOrNull()

        requireNotNull(anime)
        assertNull(anime.synopsis)
        assertNull(anime.airedTo)
        assertNull(anime.score)
    }

    @Test
    fun `page mapping removes duplicated animes and reads pagination`() {
        val page = AnimePageDto(
            data = listOf(animeDto(malId = 1), animeDto(malId = 1), animeDto(malId = 2)),
            pagination = PaginationDto(currentPage = 3, hasNextPage = true, lastVisiblePage = 10)
        ).toDomain(requestedPage = 3)

        assertEquals(listOf(1, 2), page.animes.map { it.malId })
        assertEquals(3, page.currentPage)
        assertEquals(true, page.hasNextPage)
    }

    @Test
    fun `page mapping survives a payload without data or pagination`() {
        val page = AnimePageDto(data = null, pagination = null).toDomain(requestedPage = 7)

        assertEquals(emptyList<Int>(), page.animes.map { it.malId })
        assertEquals(7, page.currentPage)
        assertEquals(false, page.hasNextPage)
    }

    @Test
    fun `entity round trip preserves every field`() {
        val original = requireNotNull(animeDto(malId = 42, title = "Monster").toDomainOrNull())

        assertEquals(original, original.toEntity().toDomain())
    }

    private fun animeDto(
        malId: Int?,
        title: String? = "Anime",
        images: ImagesDto? = ImagesDto(
            jpg = ImageUrlsDto(
                imageUrl = "https://cdn.example/$malId.jpg",
                largeImageUrl = null,
                smallImageUrl = null
            ),
            webp = null
        ),
        synopsis: String? = null,
        airedTo: String? = "2011-09-14T00:00:00+00:00"
    ) = AnimeDto(
        malId = malId,
        title = title,
        images = images,
        synopsis = synopsis,
        score = null,
        episodes = null,
        rating = null,
        url = null,
        aired = AiredDto(from = "2011-04-06T00:00:00+00:00", to = airedTo)
    )
}
