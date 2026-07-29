package com.guga.asunaanimes.presentation.collection

import com.guga.asunaanimes.R
import com.guga.asunaanimes.domain.model.AnimeCollectionType
import com.guga.asunaanimes.domain.usecase.FilterAnimesByTitleUseCase
import com.guga.asunaanimes.domain.usecase.ObserveAnimeCollectionUseCase
import com.guga.asunaanimes.domain.usecase.ObserveSearchHistoryUseCase
import com.guga.asunaanimes.domain.usecase.RemoveAnimeFromCollectionUseCase
import com.guga.asunaanimes.domain.usecase.SaveSearchQueryUseCase
import com.guga.asunaanimes.domain.usecase.UpdateAnimeUserScoreUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class WatchedViewModel @Inject constructor(
    filterAnimesByTitle: FilterAnimesByTitleUseCase,
    removeAnimeFromCollection: RemoveAnimeFromCollectionUseCase,
    updateAnimeUserScore: UpdateAnimeUserScoreUseCase,
    saveSearchQuery: SaveSearchQueryUseCase,
    observeAnimeCollection: ObserveAnimeCollectionUseCase,
    observeSearchHistory: ObserveSearchHistoryUseCase
) : CollectionViewModel(
    collectionType = AnimeCollectionType.WATCHED,
    removedMessageResId = R.string.message_removed_from_watched,
    filterAnimesByTitle = filterAnimesByTitle,
    removeAnimeFromCollection = removeAnimeFromCollection,
    updateAnimeUserScore = updateAnimeUserScore,
    saveSearchQuery = saveSearchQuery,
    observeAnimeCollection = observeAnimeCollection,
    observeSearchHistory = observeSearchHistory
)
