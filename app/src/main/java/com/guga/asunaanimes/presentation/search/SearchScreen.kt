package com.guga.asunaanimes.presentation.search

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.guga.asunaanimes.R
import com.guga.asunaanimes.domain.model.Anime
import com.guga.asunaanimes.domain.model.AnimeBrowseMode
import com.guga.asunaanimes.domain.model.AnimeCollectionType
import com.guga.asunaanimes.presentation.common.UiMessageEffect
import com.guga.asunaanimes.presentation.components.AnimatedBackground
import com.guga.asunaanimes.presentation.components.AnimeCollectionDialog
import com.guga.asunaanimes.presentation.components.AnimeDetailsBottomSheetLayout
import com.guga.asunaanimes.presentation.components.AnimeGrid
import com.guga.asunaanimes.presentation.components.BrowseModeChips
import com.guga.asunaanimes.presentation.components.SearchBox
import com.guga.asunaanimes.presentation.theme.AsunaOrange
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterNotNull

@Composable
fun SearchScreen(viewModel: SearchViewModel = hiltViewModel()) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    UiMessageEffect(messages = viewModel.messages)

    SearchContent(
        uiState = uiState,
        onSearch = viewModel::onSearch,
        onQueryChange = viewModel::onQueryChanged,
        onBrowseModeSelected = viewModel::onBrowseModeSelected,
        onLoadMore = viewModel::onLoadMore,
        onAnimeClick = viewModel::onAnimeSelected,
        onDetailsDismissed = viewModel::onDetailsDismissed,
        onAddToListClick = viewModel::onAddToListClick,
        onCollectionDialogDismissed = viewModel::onCollectionDialogDismissed,
        onCollectionSelected = viewModel::onAddToCollection
    )
}

@Composable
private fun SearchContent(
    uiState: SearchUiState,
    onSearch: (String) -> Unit,
    onQueryChange: (String) -> Unit,
    onBrowseModeSelected: (AnimeBrowseMode) -> Unit,
    onLoadMore: () -> Unit,
    onAnimeClick: (Anime) -> Unit,
    onDetailsDismissed: () -> Unit,
    onAddToListClick: () -> Unit,
    onCollectionDialogDismissed: () -> Unit,
    onCollectionSelected: (AnimeCollectionType) -> Unit
) {
    val listState = rememberLazyGridState()

    EndOfGridEffect(
        listState = listState,
        itemCount = uiState.animes.size,
        onEndReached = onLoadMore
    )

    AnimeDetailsBottomSheetLayout(
        selectedAnime = uiState.selectedAnime,
        actionLabel = stringResource(R.string.anime_add_to_list),
        onActionClick = onAddToListClick,
        onDismissed = onDetailsDismissed
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            AnimatedBackground(
                imageRes = R.drawable.search_screen,
                overlayAlpha = 0.16f
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .statusBarsPadding()
            ) {
                key(uiState.searchBoxResetKey) {
                    SearchBox(
                        onSearch = onSearch,
                        previousSearches = uiState.recentSearches,
                        onQueryChange = onQueryChange,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }

                BrowseModeChips(
                    selectedMode = uiState.browseMode,
                    enabled = !uiState.isSearchActive,
                    onModeSelected = onBrowseModeSelected,
                    modifier = Modifier.padding(bottom = 4.dp, top = 2.dp)
                )

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(top = 2.dp),
                    contentAlignment = Alignment.Center
                ) {
                    if (uiState.isLoading && uiState.animes.isEmpty()) {
                        CircularProgressIndicator(
                            color = AsunaOrange,
                            strokeWidth = 3.dp,
                            modifier = Modifier.size(42.dp)
                        )
                    } else {
                        AnimeGrid(
                            animes = uiState.animes,
                            onAnimeClick = onAnimeClick,
                            listState = listState,
                            emptyTitle = stringResource(R.string.empty_search_title),
                            emptySubtitle = stringResource(R.string.empty_search_subtitle)
                        )
                        if (uiState.isLoading) {
                            CircularProgressIndicator(
                                color = AsunaOrange,
                                strokeWidth = 3.dp,
                                modifier = Modifier
                                    .align(Alignment.TopCenter)
                                    .padding(top = 12.dp)
                                    .size(28.dp)
                            )
                        }
                    }
                }
            }
        }
    }

    if (uiState.isCollectionDialogVisible) {
        AnimeCollectionDialog(
            onDismiss = onCollectionDialogDismissed,
            onCollectionSelected = onCollectionSelected
        )
    }
}

@Composable
private fun EndOfGridEffect(
    listState: LazyGridState,
    itemCount: Int,
    onEndReached: () -> Unit
) {
    val currentOnEndReached by rememberUpdatedState(onEndReached)

    LaunchedEffect(listState, itemCount) {
        if (itemCount == 0) return@LaunchedEffect
        snapshotFlow { listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index }
            .filterNotNull()
            .distinctUntilChanged()
            .collect { lastVisibleIndex ->
                if (lastVisibleIndex >= itemCount - 3) currentOnEndReached()
            }
    }
}
