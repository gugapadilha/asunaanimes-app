package com.guga.asunaanimes.data.remote

import com.guga.asunaanimes.data.remote.dto.AnimePageDto
import retrofit2.http.GET
import retrofit2.http.Query

interface AnimeApi {

    @GET("top/anime")
    suspend fun getTopAnime(@Query("page") page: Int): AnimePageDto

    @GET("seasons/now")
    suspend fun getSeasonalAnime(@Query("page") page: Int): AnimePageDto

    @GET("anime")
    suspend fun searchAnime(
        @Query("q") query: String,
        @Query("limit") limit: Int = DEFAULT_SEARCH_LIMIT
    ): AnimePageDto

    companion object {
        const val BASE_URL = "https://api.jikan.moe/v4/"
        const val DEFAULT_SEARCH_LIMIT = 25
    }
}
