package com.guga.asunaanimes.presentation.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.MovieFilter
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.guga.asunaanimes.R
import com.guga.asunaanimes.domain.model.Anime
import com.guga.asunaanimes.presentation.theme.AsunaOnSurface
import com.guga.asunaanimes.presentation.theme.AsunaOnSurfaceMuted
import com.guga.asunaanimes.presentation.theme.AsunaOrange

const val ANIME_GRID_COLUMNS = 3

/**
 * Three column grid of anime posters. Built on top of [LazyColumn] with chunked rows so it keeps
 * the exact layout the app shipped with.
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun AnimeGrid(
    animes: List<Anime>,
    onAnimeClick: (Anime) -> Unit,
    modifier: Modifier = Modifier,
    listState: LazyListState = rememberLazyListState(),
    emptyTitle: String = stringResource(R.string.empty_list_title),
    emptySubtitle: String = stringResource(R.string.empty_list_subtitle)
) {
    if (animes.isEmpty()) {
        AnimatedVisibility(
            visible = true,
            enter = fadeIn() + scaleIn(initialScale = 0.96f),
            exit = fadeOut(),
            modifier = modifier.fillMaxSize()
        ) {
            EmptyAnimeState(
                title = emptyTitle,
                subtitle = emptySubtitle
            )
        }
        return
    }

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        state = listState,
        contentPadding = PaddingValues(start = 12.dp, end = 12.dp, bottom = 20.dp, top = 4.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        items(
            items = animes.chunked(ANIME_GRID_COLUMNS),
            key = { rowItems -> rowItems.first().malId }
        ) { rowItems ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .animateItemPlacement(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                rowItems.forEach { anime ->
                    Box(modifier = Modifier.weight(1f)) {
                        AnimeItem(
                            anime = anime,
                            onClick = { onAnimeClick(anime) }
                        )
                    }
                }
                repeat(ANIME_GRID_COLUMNS - rowItems.size) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
fun EmptyAnimeState(
    title: String,
    subtitle: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Outlined.MovieFilter,
            contentDescription = null,
            tint = AsunaOrange,
            modifier = Modifier.size(56.dp)
        )
        Spacer(modifier = Modifier.size(16.dp))
        Text(
            text = title,
            color = AsunaOnSurface,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.size(8.dp))
        Text(
            text = subtitle,
            color = AsunaOnSurfaceMuted,
            textAlign = TextAlign.Center
        )
    }
}
