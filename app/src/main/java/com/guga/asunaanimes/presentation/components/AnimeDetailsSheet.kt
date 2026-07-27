package com.guga.asunaanimes.presentation.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.guga.asunaanimes.R
import com.guga.asunaanimes.core.util.AiredDateFormatter
import com.guga.asunaanimes.domain.model.Anime
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
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val backgroundPainter = rememberAsyncImagePainter(R.drawable.bottomsheet_screen)

    Box(modifier = modifier.fillMaxSize()) {
        Image(
            modifier = Modifier.fillMaxSize(),
            painter = backgroundPainter,
            contentDescription = stringResource(R.string.content_description_background_image),
            contentScale = ContentScale.FillBounds
        )
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(modifier = Modifier.fillMaxWidth()) {
                Image(
                    painter = rememberAsyncImagePainter(model = anime.imageUrl),
                    contentDescription = stringResource(R.string.content_description_anime_picture),
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .height(200.dp)
                        .width(130.dp)
                        .clip(shape = RoundedCornerShape(8.dp))
                        .background(Color.Gray)
                )

                Spacer(modifier = Modifier.width(16.dp))

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                ) {
                    Text(
                        text = anime.title,
                        color = Color.White,
                        fontSize = 20.sp,
                        fontFamily = FontFamily.SansSerif,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Start,
                        modifier = Modifier.padding(bottom = 14.dp)
                    )
                    DetailText(
                        text = stringResource(
                            R.string.anime_score,
                            anime.score?.toString() ?: stringResource(R.string.value_not_available)
                        ),
                        color = Color.Yellow
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
                        color = Color.Cyan,
                        fontSize = 15.sp,
                        fontFamily = FontFamily.SansSerif,
                        textAlign = TextAlign.Start,
                        textDecoration = TextDecoration.Underline,
                        modifier = Modifier
                            .padding(bottom = 4.dp)
                            .clickable { context.openInBrowser(anime.detailsUrl) }
                    )
                    GradientBorderButton(
                        text = actionLabel,
                        onClick = onActionClick,
                        modifier = Modifier
                            .height(45.dp)
                            .padding(top = 10.dp),
                        textTopPadding = 2.dp
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = anime.synopsis ?: stringResource(R.string.anime_synopsis_unavailable),
                color = Color.White,
                fontSize = 15.sp,
                fontFamily = FontFamily.SansSerif,
                textAlign = TextAlign.Start,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 4.dp)
            )
        }
    }
}

@Composable
private fun DetailText(text: String, color: Color = Color.White) {
    Text(
        text = text,
        color = color,
        fontSize = 15.sp,
        fontFamily = FontFamily.SansSerif,
        textAlign = TextAlign.Start,
        modifier = Modifier.padding(bottom = 8.dp)
    )
}

@Composable
private fun airedLabel(anime: Anime): String {
    val start = anime.airedFrom
        ?.let { AiredDateFormatter.format(it) }
        ?: stringResource(R.string.anime_aired_unknown_start)
    return anime.airedTo
        ?.let { stringResource(R.string.anime_aired_range, start, AiredDateFormatter.format(it)) }
        ?: stringResource(R.string.anime_aired_ongoing, start)
}
