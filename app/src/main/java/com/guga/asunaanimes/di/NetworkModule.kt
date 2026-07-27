package com.guga.asunaanimes.di

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.guga.asunaanimes.BuildConfig
import com.guga.asunaanimes.data.remote.AniListApi
import com.guga.asunaanimes.data.remote.AnimeApi
import com.guga.asunaanimes.data.remote.JikanRateLimitInterceptor
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import java.util.concurrent.TimeUnit
import javax.inject.Qualifier
import javax.inject.Singleton
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class JikanOkHttp

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class AniListOkHttp

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    private const val TIMEOUT_SECONDS = 20L

    @Provides
    @Singleton
    fun provideGson(): Gson = GsonBuilder().create()

    @Provides
    @Singleton
    @JikanOkHttp
    fun provideJikanOkHttpClient(): OkHttpClient = baseClientBuilder()
        .addInterceptor(JikanRateLimitInterceptor())
        .build()

    @Provides
    @Singleton
    @AniListOkHttp
    fun provideAniListOkHttpClient(): OkHttpClient = baseClientBuilder().build()

    @Provides
    @Singleton
    @JikanRetrofit
    fun provideJikanRetrofit(@JikanOkHttp okHttpClient: OkHttpClient, gson: Gson): Retrofit =
        Retrofit.Builder()
            .baseUrl(AnimeApi.BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()

    @Provides
    @Singleton
    @AniListRetrofit
    fun provideAniListRetrofit(@AniListOkHttp okHttpClient: OkHttpClient, gson: Gson): Retrofit =
        Retrofit.Builder()
            .baseUrl(AniListApi.BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()

    @Provides
    @Singleton
    fun provideAnimeApi(@JikanRetrofit retrofit: Retrofit): AnimeApi =
        retrofit.create(AnimeApi::class.java)

    @Provides
    @Singleton
    fun provideAniListApi(@AniListRetrofit retrofit: Retrofit): AniListApi =
        retrofit.create(AniListApi::class.java)

    private fun baseClientBuilder(): OkHttpClient.Builder = OkHttpClient.Builder()
        .connectTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
        .readTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
        .apply {
            if (BuildConfig.ENABLE_NETWORK_LOGGING) {
                addInterceptor(
                    HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BASIC }
                )
            }
        }
}
