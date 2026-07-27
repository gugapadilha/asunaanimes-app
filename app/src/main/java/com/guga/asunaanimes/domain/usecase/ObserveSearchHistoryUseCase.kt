package com.guga.asunaanimes.domain.usecase

import com.guga.asunaanimes.domain.repository.SearchHistoryRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

class ObserveSearchHistoryUseCase @Inject constructor(
    private val searchHistoryRepository: SearchHistoryRepository
) {

    operator fun invoke(): Flow<List<String>> = searchHistoryRepository.observeRecentQueries()
}
