package com.guga.asunaanimes.presentation.search

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.rememberAsyncImagePainter
import com.guga.asunaanimes.R
import com.guga.asunaanimes.domain.model.Anime
import com.guga.asunaanimes.domain.model.AnimeCollectionType
import com.guga.asunaanimes.presentation.common.UiMessageEffect
import com.guga.asunaanimes.presentation.components.ANIME_GRID_COLUMNS
import com.guga.asunaanimes.presentation.components.AnimeCollectionDialog
import com.guga.asunaanimes.presentation.components.AnimeDetailsBottomSheetLayout
import com.guga.asunaanimes.presentation.components.AnimeGrid
import com.guga.asunaanimes.presentation.components.SearchBox
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterNotNull

@Composable
fun SearchScreen(viewModel: SearchViewModel = hiltViewModel()) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    UiMessageEffect(messages = viewModel.messages)

    SearchContent(
        uiState = uiState,
        onSearch = viewModel::onSearch,
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
    onLoadMore: () -> Unit,
    onAnimeClick: (Anime) -> Unit,
    onDetailsDismissed: () -> Unit,
    onAddToListClick: () -> Unit,
    onCollectionDialogDismissed: () -> Unit,
    onCollectionSelected: (AnimeCollectionType) -> Unit
) {
    val painter = rememberAsyncImagePainter(R.drawable.search_screen)
    val listState = rememberLazyListState()

    EndOfListEffect(
        listState = listState,
        rowCount = rowCountFor(uiState.animes.size),
        onEndReached = onLoadMore
    )

    AnimeDetailsBottomSheetLayout(
        selectedAnime = uiState.selectedAnime,
        actionLabel = stringResource(R.string.anime_add_to_list),
        onActionClick = onAddToListClick,
        onDismissed = onDetailsDismissed
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Image(
                modifier = Modifier.fillMaxSize(),
                painter = painter,
                contentDescription = stringResource(R.string.content_description_background_image),
                contentScale = ContentScale.FillBounds
            )

            Column(modifier = Modifier.fillMaxSize()) {
                Spacer(modifier = Modifier.padding(top = 15.dp))

                SearchBox(
                    onSearch = onSearch,
                    previousSearches = uiState.recentSearches
                )

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 4.dp)
                ) {
                    if (uiState.isLoading) {
                        CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                    } else {
                        AnimeGrid(
                            animes = uiState.animes,
                            onAnimeClick = onAnimeClick,
                            listState = listState
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
 * Triggers [onEndReached] once the last grid row becomes visible.
 *
 * The previous implementation compared a row index against the anime count, a condition that
 * could never be true, so infinite scrolling never actually loaded a new page.
 */
@Composable
private fun EndOfListEffect(listState: LazyListState, rowCount: Int, onEndReached: () -> Unit) {
    val currentOnEndReached by rememberUpdatedState(onEndReached)

    LaunchedEffect(listState, rowCount) {
        if (rowCount == 0) return@LaunchedEffect
        snapshotFlow { listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index }
            .filterNotNull()
            .distinctUntilChanged()
            .collect { lastVisibleIndex ->
                if (lastVisibleIndex >= rowCount - 1) currentOnEndReached()
            }
    }
}

private fun rowCountFor(animeCount: Int): Int =
    (animeCount + ANIME_GRID_COLUMNS - 1) / ANIME_GRID_COLUMNS
