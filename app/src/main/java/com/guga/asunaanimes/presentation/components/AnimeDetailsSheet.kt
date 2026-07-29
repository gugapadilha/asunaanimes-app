package com.guga.asunaanimes.presentation.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.guga.asunaanimes.R
import com.guga.asunaanimes.core.util.AiredDateFormatter
import com.guga.asunaanimes.domain.model.Anime
import com.guga.asunaanimes.presentation.theme.AsunaLink
import com.guga.asunaanimes.presentation.theme.AsunaOnSurface
import com.guga.asunaanimes.presentation.theme.AsunaOnSurfaceMuted
import com.guga.asunaanimes.presentation.theme.AsunaOrange
import com.guga.asunaanimes.presentation.theme.AsunaOrangeSoft
import com.guga.asunaanimes.presentation.theme.AsunaScore
import com.guga.asunaanimes.presentation.theme.AsunaSurface
import com.guga.asunaanimes.presentation.theme.userScoreColor
import com.guga.asunaanimes.presentation.util.openInBrowser

/**
 * Detail sheet shared by the search screen (which adds an anime to a list) and the collection
 * screens (which remove it). Only the action button label and behaviour differ.
 */
@Composable
fun AnimeDetailsSheet(
    anime: Anime,
    actionLabel: String,
    onActionClick: () -> Unit,
    modifier: Modifier = Modifier,
    isDetailsLoading: Boolean = false,
    secondaryActionLabel: String? = null,
    onSecondaryActionClick: (() -> Unit)? = null
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(
                Brush.verticalGradient(
                    colors = listOf(AsunaSurface, Color(0xFF0D0B09))
                )
            )
            .navigationBarsPadding()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(scrollState)
                .padding(horizontal = 20.dp, vertical = 16.dp)
        ) {
            Box(
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .width(42.dp)
                    .height(4.dp)
                    .clip(RoundedCornerShape(50))
                    .background(AsunaOnSurfaceMuted.copy(alpha = 0.5f))
            )

            Spacer(modifier = Modifier.height(18.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Image(
                    painter = rememberAsyncImagePainter(model = anime.imageUrl),
                    contentDescription = stringResource(R.string.content_description_anime_picture),
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .height(210.dp)
                        .width(136.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color.DarkGray)
                )

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = anime.title,
                        color = AsunaOnSurface,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 10.dp)
                    )
                    DetailText(
                        text = stringResource(
                            R.string.anime_score,
                            anime.score?.toString() ?: stringResource(R.string.value_not_available)
                        ),
                        color = AsunaScore
                    )
                    DetailText(
                        text = anime.userScore?.let {
                            stringResource(R.string.anime_your_score, it)
                        } ?: stringResource(R.string.anime_your_score_unrated),
                        color = anime.userScore?.let { userScoreColor(it) } ?: AsunaOnSurfaceMuted
                    )
                    DetailText(text = airedLabel(anime))
                    DetailText(
                        text = stringResource(
                            R.string.anime_episodes,
                            anime.episodes?.toString() ?: stringResource(R.string.value_unknown)
                        )
                    )
                    DetailText(
                        text = stringResource(
                            R.string.anime_rating,
                            anime.rating ?: stringResource(R.string.value_not_available)
                        )
                    )
                    Text(
                        text = stringResource(R.string.anime_know_more),
                        color = AsunaLink,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold,
                        textDecoration = TextDecoration.Underline,
                        modifier = Modifier
                            .padding(top = 4.dp, bottom = 12.dp)
                            .clickable { context.openInBrowser(anime.detailsUrl) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (secondaryActionLabel != null && onSecondaryActionClick != null) {
                AsunaPrimaryButton(
                    text = secondaryActionLabel,
                    onClick = onSecondaryActionClick,
                    modifier = Modifier.fillMaxWidth(),
                    fontSize = 15.sp
                )
                Spacer(modifier = Modifier.height(10.dp))
            }

            AsunaPrimaryButton(
                text = actionLabel,
                onClick = onActionClick,
                modifier = Modifier.fillMaxWidth(),
                fontSize = 16.sp
            )

            Spacer(modifier = Modifier.height(18.dp))

            Text(
                text = stringResource(R.string.anime_synopsis_label),
                color = AsunaOrangeSoft,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            if (isDetailsLoading && anime.synopsis.isNullOrBlank()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    CircularProgressIndicator(
                        color = AsunaOrange,
                        strokeWidth = 2.dp,
                        modifier = Modifier.size(18.dp)
                    )
                    Text(
                        text = stringResource(R.string.anime_synopsis_loading),
                        color = AsunaOnSurfaceMuted,
                        fontSize = 15.sp
                    )
                }
            } else {
                Text(
                    text = anime.synopsis ?: stringResource(R.string.anime_synopsis_unavailable),
                    color = AsunaOnSurfaceMuted,
                    fontSize = 15.sp,
                    lineHeight = 22.sp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp)
                )
            }
        }
    }
}

@Composable
private fun DetailText(text: String, color: Color = AsunaOnSurfaceMuted) {
    Text(
        text = text,
        color = color,
        fontSize = 14.sp,
        modifier = Modifier.padding(bottom = 6.dp)
    )
}

@Composable
private fun airedLabel(anime: Anime): String {
    val unknownDate = stringResource(R.string.anime_aired_unknown_date)
    val start = anime.airedFrom
        ?.let { AiredDateFormatter.format(it) ?: unknownDate }
        ?: stringResource(R.string.anime_aired_unknown_start)
    return anime.airedTo
        ?.let {
            stringResource(
                R.string.anime_aired_range,
                start,
                AiredDateFormatter.format(it) ?: unknownDate
            )
        }
        ?: stringResource(R.string.anime_aired_ongoing, start)
}
