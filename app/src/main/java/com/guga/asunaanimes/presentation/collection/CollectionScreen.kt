package com.guga.asunaanimes.presentation.collection

import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.guga.asunaanimes.R
import com.guga.asunaanimes.domain.model.Anime
import com.guga.asunaanimes.presentation.components.AnimatedBackground
import com.guga.asunaanimes.presentation.components.AnimeDetailsBottomSheetLayout
import com.guga.asunaanimes.presentation.components.AnimeGrid
import com.guga.asunaanimes.presentation.components.SearchBox
import com.guga.asunaanimes.presentation.theme.AsunaOnSurface
import com.guga.asunaanimes.presentation.theme.AsunaSurfaceElevated

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
    onAnimeClick: (Anime) -> Unit,
    onDetailsDismissed: () -> Unit,
    onRemoveClick: () -> Unit
) {
    AnimeDetailsBottomSheetLayout(
        selectedAnime = uiState.selectedAnime,
        actionLabel = stringResource(R.string.anime_remove),
        onActionClick = onRemoveClick,
        onDismissed = onDetailsDismissed
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
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 10.dp),
                    shape = RoundedCornerShape(18.dp),
                    color = AsunaSurfaceElevated.copy(alpha = 0.88f),
                    tonalElevation = 0.dp
                ) {
                    Text(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp),
                        text = headerText,
                        textAlign = TextAlign.Center,
                        color = AsunaOnSurface,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                SearchBox(
                    onSearch = onSearch,
                    previousSearches = uiState.recentSearches
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
