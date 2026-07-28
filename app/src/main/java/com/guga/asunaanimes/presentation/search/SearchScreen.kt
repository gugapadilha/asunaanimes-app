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
import com.guga.asunaanimes.presentation.components.DefaultSearchChromeClearance
import com.guga.asunaanimes.presentation.components.SearchBox
import com.guga.asunaanimes.presentation.theme.AsunaOrange
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.distinctUntilChanged

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
        canLoadMore = uiState.canLoadMore,
        isLoadingMore = uiState.isLoadingMore || uiState.isLoading,
        onEndReached = onLoadMore
    )

    AnimeDetailsBottomSheetLayout(
        selectedAnime = uiState.selectedAnime,
        actionLabel = stringResource(R.string.anime_add_to_list),
        onActionClick = onAddToListClick,
        onDismissed = onDetailsDismissed,
        isDetailsLoading = uiState.isDetailsLoading,
        topChromeClearance = DefaultSearchChromeClearance
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            AnimatedBackground(
                imageRes = R.drawable.search_screen
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
                            isLoadingMore = uiState.isLoadingMore,
                            emptyTitle = stringResource(R.string.empty_search_title),
                            emptySubtitle = stringResource(R.string.empty_search_subtitle)
                        )
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

/**
 * Fires [onEndReached] when the user is near the end of the grid and idle. Observing the loading
 * flags inside the flow means finishing a page can immediately request the next one while still
 * scrolled to the bottom. While still eligible after a no-op (e.g. ViewModel cooldown), we pulse
 * again so pagination does not stay stuck at the first page.
 */
@Composable
private fun EndOfGridEffect(
    listState: LazyGridState,
    canLoadMore: Boolean,
    isLoadingMore: Boolean,
    onEndReached: () -> Unit
) {
    val currentOnEndReached by rememberUpdatedState(onEndReached)

    LaunchedEffect(listState, canLoadMore, isLoadingMore) {
        snapshotFlow {
            val lastVisible = listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index
            val total = listState.layoutInfo.totalItemsCount
            val nearEnd = lastVisible != null && total > 0 && lastVisible >= total - 6
            nearEnd && canLoadMore && !isLoadingMore
        }
            .distinctUntilChanged()
            .collect { shouldLoad ->
                if (!shouldLoad) return@collect
                currentOnEndReached()
                // If still near the end after a rejected/no-op call (cooldown), try again shortly.
                delay(END_REACHED_RETRY_MS)
                val lastVisible = listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index
                val total = listState.layoutInfo.totalItemsCount
                val stillNearEnd = lastVisible != null && total > 0 && lastVisible >= total - 6
                if (stillNearEnd && canLoadMore && !isLoadingMore) {
                    currentOnEndReached()
                }
            }
    }
}

private const val END_REACHED_RETRY_MS = 2_600L
