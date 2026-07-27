package com.guga.asunaanimes.presentation.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.guga.asunaanimes.R
import com.guga.asunaanimes.domain.model.AnimeCollectionType
import com.guga.asunaanimes.presentation.theme.AsunaBlack
import com.guga.asunaanimes.presentation.theme.AsunaOnSurface
import com.guga.asunaanimes.presentation.theme.AsunaOnSurfaceMuted
import com.guga.asunaanimes.presentation.theme.AsunaOrange
import com.guga.asunaanimes.presentation.theme.AsunaOrangeDark
import com.guga.asunaanimes.presentation.theme.AsunaSurfaceElevated

/**
 * Asks the user which personal list the selected anime should be added to.
 */
@Composable
fun AnimeCollectionDialog(
    onDismiss: () -> Unit,
    onCollectionSelected: (AnimeCollectionType) -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        AnimatedVisibility(
            visible = true,
            enter = fadeIn() + scaleIn(initialScale = 0.9f),
            exit = fadeOut() + scaleOut(targetScale = 0.9f)
        ) {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                color = AsunaSurfaceElevated,
                tonalElevation = 0.dp,
                shadowElevation = 12.dp
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(22.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = stringResource(R.string.collection_dialog_title),
                        color = AsunaOnSurface,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = stringResource(R.string.collection_dialog_subtitle),
                        color = AsunaOnSurfaceMuted,
                        fontSize = 14.sp,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    CollectionOption(
                        text = stringResource(R.string.collection_dialog_favorite),
                        icon = Icons.Filled.Favorite,
                        onClick = { onCollectionSelected(AnimeCollectionType.FAVORITE) }
                    )
                    CollectionOption(
                        text = stringResource(R.string.collection_dialog_watched),
                        icon = Icons.Filled.Visibility,
                        onClick = { onCollectionSelected(AnimeCollectionType.WATCHED) }
                    )
                }
            }
        }
    }
}

@Composable
private fun CollectionOption(
    text: String,
    icon: ImageVector,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(
                Brush.horizontalGradient(
                    colors = listOf(AsunaOrangeDark, AsunaOrange)
                )
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = AsunaBlack,
            modifier = Modifier.size(22.dp)
        )
        Text(
            text = text,
            color = AsunaBlack,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold
        )
    }
}
