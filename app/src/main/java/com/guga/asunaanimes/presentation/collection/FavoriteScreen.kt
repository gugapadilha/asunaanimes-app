package com.guga.asunaanimes.presentation.collection

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.guga.asunaanimes.R
import com.guga.asunaanimes.presentation.common.UiMessageEffect

@Composable
fun FavoriteScreen(viewModel: FavoriteViewModel = hiltViewModel()) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    UiMessageEffect(messages = viewModel.messages)

    CollectionScreen(
        uiState = uiState,
        backgroundRes = R.drawable.favorite_screen,
        headerText = stringResource(R.string.favorite_screen_header),
        emptyTitle = stringResource(R.string.empty_favorites_title),
        emptySubtitle = stringResource(R.string.empty_favorites_subtitle),
        onSearch = viewModel::onSearch,
        onQueryChange = viewModel::onQueryChanged,
        onAnimeClick = viewModel::onAnimeSelected,
        onDetailsDismissed = viewModel::onDetailsDismissed,
        onRemoveClick = viewModel::onRemoveSelectedAnime
    )
}
