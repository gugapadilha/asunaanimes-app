package com.guga.asunaanimes.presentation.collection

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.guga.asunaanimes.R
import com.guga.asunaanimes.domain.model.Anime
import com.guga.asunaanimes.presentation.components.AnimatedBorderCard
import com.guga.asunaanimes.presentation.components.AnimeDetailsBottomSheetLayout
import com.guga.asunaanimes.presentation.components.AnimeGrid
import com.guga.asunaanimes.presentation.components.SearchBox

/**
 * Layout shared by the favorite and watched screens.
 */
@Composable
fun CollectionScreen(
    uiState: CollectionUiState,
    @DrawableRes backgroundRes: Int,
    headerText: String,
    onSearch: (String) -> Unit,
    onAnimeClick: (Anime) -> Unit,
    onDetailsDismissed: () -> Unit,
    onRemoveClick: () -> Unit
) {
    val painter = rememberAsyncImagePainter(backgroundRes)

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
            Image(
                modifier = Modifier.fillMaxSize(),
                painter = painter,
                contentDescription = stringResource(R.string.content_description_background_image),
                contentScale = ContentScale.FillBounds
            )
            Column(modifier = Modifier.fillMaxSize()) {
                AnimatedBorderCard(
                    modifier = Modifier
                        .height(80.dp)
                        .fillMaxWidth()
                        .padding(top = 25.dp, start = 10.dp, end = 10.dp),
                    shape = RoundedCornerShape(4.dp),
                    borderWidth = 3.dp
                ) {
                    Text(
                        modifier = Modifier.padding(top = 10.dp),
                        text = headerText,
                        textAlign = TextAlign.Center,
                        color = Color.White,
                        fontSize = 18.sp
                    )
                }

                SearchBox(
                    onSearch = onSearch,
                    previousSearches = uiState.recentSearches
                )

                AnimeGrid(
                    animes = uiState.animes,
                    onAnimeClick = onAnimeClick,
                    modifier = Modifier.padding(horizontal = 4.dp)
                )
            }
        }
    }
}
