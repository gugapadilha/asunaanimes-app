package com.guga.asunaanimes.domain.repository

import kotlinx.coroutines.flow.Flow

interface SearchHistoryRepository {

    fun observeRecentQueries(): Flow<List<String>>

    suspend fun saveQuery(query: String)
}
