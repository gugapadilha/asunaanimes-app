package com.guga.asunaanimes.di

import com.guga.asunaanimes.data.local.AnimeCollectionLocalDataSource
import com.guga.asunaanimes.data.local.SearchHistoryLocalDataSource
import com.guga.asunaanimes.data.local.SharedPrefsAnimeCollectionLocalDataSource
import com.guga.asunaanimes.data.local.SharedPrefsSearchHistoryLocalDataSource
import com.guga.asunaanimes.data.repository.AnimeCollectionRepositoryImpl
import com.guga.asunaanimes.data.repository.AnimeRepositoryImpl
import com.guga.asunaanimes.data.repository.SearchHistoryRepositoryImpl
import com.guga.asunaanimes.domain.repository.AnimeCollectionRepository
import com.guga.asunaanimes.domain.repository.AnimeRepository
import com.guga.asunaanimes.domain.repository.SearchHistoryRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class DataModule {

    @Binds
    @Singleton
    abstract fun bindAnimeRepository(impl: AnimeRepositoryImpl): AnimeRepository

    @Binds
    @Singleton
    abstract fun bindAnimeCollectionRepository(
        impl: AnimeCollectionRepositoryImpl
    ): AnimeCollectionRepository

    @Binds
    @Singleton
    abstract fun bindSearchHistoryRepository(
        impl: SearchHistoryRepositoryImpl
    ): SearchHistoryRepository

    @Binds
    @Singleton
    abstract fun bindAnimeCollectionLocalDataSource(
        impl: SharedPrefsAnimeCollectionLocalDataSource
    ): AnimeCollectionLocalDataSource

    @Binds
    @Singleton
    abstract fun bindSearchHistoryLocalDataSource(
        impl: SharedPrefsSearchHistoryLocalDataSource
    ): SearchHistoryLocalDataSource
}
