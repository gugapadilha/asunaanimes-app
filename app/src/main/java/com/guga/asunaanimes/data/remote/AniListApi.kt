package com.guga.asunaanimes.data.remote

import com.guga.asunaanimes.data.remote.dto.AniListSearchRequest
import com.guga.asunaanimes.data.remote.dto.AniListSearchResponse
import retrofit2.http.Body
import retrofit2.http.POST

interface AniListApi {

    @POST("/")
    suspend fun search(@Body body: AniListSearchRequest): AniListSearchResponse

    companion object {
        const val BASE_URL = "https://graphql.anilist.co/"

        const val SEARCH_QUERY = """
            query (${'$'}search: String, ${'$'}page: Int, ${'$'}perPage: Int) {
              Page(page: ${'$'}page, perPage: ${'$'}perPage) {
                pageInfo { hasNextPage currentPage }
                media(search: ${'$'}search, type: ANIME, sort: SEARCH_MATCH) {
                  idMal
                  title { romaji english }
                  coverImage { large medium }
                  description
                  episodes
                  averageScore
                  siteUrl
                }
              }
            }
        """

        /** Paginated popular catalog — used for Recommendations browse (unique pages, no Jikan quota). */
        const val POPULAR_QUERY = """
            query (${'$'}page: Int, ${'$'}perPage: Int) {
              Page(page: ${'$'}page, perPage: ${'$'}perPage) {
                pageInfo { hasNextPage currentPage }
                media(type: ANIME, sort: POPULARITY_DESC) {
                  idMal
                  title { romaji english }
                  coverImage { large medium }
                  description
                  episodes
                  averageScore
                  siteUrl
                }
              }
            }
        """
    }
}
