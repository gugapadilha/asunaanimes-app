package com.guga.asunaanimes.presentation.collection

import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.guga.asunaanimes.R
import com.guga.asunaanimes.domain.model.Anime
import com.guga.asunaanimes.presentation.components.AnimatedBackground
import com.guga.asunaanimes.presentation.components.AnimeDetailsBottomSheetLayout
import com.guga.asunaanimes.presentation.components.AnimeGrid
import com.guga.asunaanimes.presentation.components.CollectionScreenHeader
import com.guga.asunaanimes.presentation.components.CollectionSearchChromeClearance
import com.guga.asunaanimes.presentation.components.SearchBox

/**
 * Layout shared by the favorite and watched screens.
 */
@Composable
fun CollectionScreen(
    uiState: CollectionUiState,
    @DrawableRes backgroundRes: Int,
    headerText: String,
    emptyTitle: String,
    emptySubtitle: String,
    onSearch: (String) -> Unit,
    onQueryChange: (String) -> Unit,
    onAnimeClick: (Anime) -> Unit,
    onDetailsDismissed: () -> Unit,
    onRemoveClick: () -> Unit
) {
    AnimeDetailsBottomSheetLayout(
        selectedAnime = uiState.selectedAnime,
        actionLabel = stringResource(R.string.anime_remove),
        onActionClick = onRemoveClick,
        onDismissed = onDetailsDismissed,
        topChromeClearance = CollectionSearchChromeClearance
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.TopCenter
        ) {
            AnimatedBackground(imageRes = backgroundRes)

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .statusBarsPadding()
            ) {
                CollectionScreenHeader(title = headerText)

                SearchBox(
                    onSearch = onSearch,
                    previousSearches = uiState.recentSearches,
                    onQueryChange = onQueryChange
                )

                AnimeGrid(
                    animes = uiState.animes,
                    onAnimeClick = onAnimeClick,
                    emptyTitle = emptyTitle,
                    emptySubtitle = emptySubtitle
                )
            }
        }
    }
}
