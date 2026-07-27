package com.guga.asunaanimes.presentation.collection

import com.guga.asunaanimes.R
import com.guga.asunaanimes.domain.model.AnimeCollectionType
import com.guga.asunaanimes.domain.usecase.FilterAnimesByTitleUseCase
import com.guga.asunaanimes.domain.usecase.ObserveAnimeCollectionUseCase
import com.guga.asunaanimes.domain.usecase.ObserveSearchHistoryUseCase
import com.guga.asunaanimes.domain.usecase.RemoveAnimeFromCollectionUseCase
import com.guga.asunaanimes.domain.usecase.SaveSearchQueryUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class FavoriteViewModel @Inject constructor(
    filterAnimesByTitle: FilterAnimesByTitleUseCase,
    removeAnimeFromCollection: RemoveAnimeFromCollectionUseCase,
    saveSearchQuery: SaveSearchQueryUseCase,
    observeAnimeCollection: ObserveAnimeCollectionUseCase,
    observeSearchHistory: ObserveSearchHistoryUseCase
) : CollectionViewModel(
    collectionType = AnimeCollectionType.FAVORITE,
    removedMessageResId = R.string.message_removed_from_favorites,
    filterAnimesByTitle = filterAnimesByTitle,
    removeAnimeFromCollection = removeAnimeFromCollection,
    saveSearchQuery = saveSearchQuery,
    observeAnimeCollection = observeAnimeCollection,
    observeSearchHistory = observeSearchHistory
)
