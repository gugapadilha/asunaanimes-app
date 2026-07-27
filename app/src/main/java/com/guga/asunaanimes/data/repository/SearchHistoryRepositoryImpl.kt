package com.guga.asunaanimes.data.repository

import com.guga.asunaanimes.data.local.SearchHistoryLocalDataSource
import com.guga.asunaanimes.domain.repository.SearchHistoryRepository
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow

@Singleton
class SearchHistoryRepositoryImpl @Inject constructor(
    private val localDataSource: SearchHistoryLocalDataSource
) : SearchHistoryRepository {

    override fun observeRecentQueries(): Flow<List<String>> = localDataSource.observeRecentQueries()

    override suspend fun saveQuery(query: String) = localDataSource.saveQuery(query)
}
