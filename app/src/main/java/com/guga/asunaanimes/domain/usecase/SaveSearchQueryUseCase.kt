package com.guga.asunaanimes.domain.usecase

import com.guga.asunaanimes.domain.repository.SearchHistoryRepository
import javax.inject.Inject

class SaveSearchQueryUseCase @Inject constructor(
    private val searchHistoryRepository: SearchHistoryRepository
) {

    /** Blank queries are never worth remembering. */
    suspend operator fun invoke(query: String) {
        val sanitizedQuery = query.trim()
        if (sanitizedQuery.isEmpty()) return
        searchHistoryRepository.saveQuery(sanitizedQuery)
    }
}
