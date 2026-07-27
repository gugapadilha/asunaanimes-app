package com.guga.asunaanimes.data.local

import kotlinx.coroutines.flow.Flow

interface SearchHistoryLocalDataSource {

    fun observeRecentQueries(): Flow<List<String>>

    suspend fun saveQuery(query: String)
}
