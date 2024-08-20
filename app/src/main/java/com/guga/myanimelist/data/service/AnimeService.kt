package com.guga.myanimelist.data.service


import com.guga.myanimelist.domain.model.SearchedAnime
import com.guga.myanimelist.domain.model.TopAnime
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

interface AnimeService {

    @GET("top/anime")
    suspend fun getTopAnime(@Query("page") page: Int): TopAnime

    @GET("anime")
    suspend fun getSearchedAnime(@Query("q") queryString: String): SearchedAnime

    companion object {
        private const val BASE_URL = "https://api.jikan.moe/v4/"

        fun create(): AnimeService {
            val retrofit = Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(BASE_URL)
                .build()
            return retrofit.create(AnimeService::class.java)
        }
    }
}