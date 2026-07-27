package com.guga.asunaanimes.presentation.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.guga.asunaanimes.R
import com.guga.asunaanimes.domain.model.AnimeCollectionType
import com.guga.asunaanimes.presentation.theme.AsunaGradient

/**
 * Asks the user which personal list the selected anime should be added to.
 */
@Composable
fun AnimeCollectionDialog(
    onDismiss: () -> Unit,
    onCollectionSelected: (AnimeCollectionType) -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(24.dp))
                .background(AsunaGradient)
                .padding(22.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                CollectionOption(
                    text = stringResource(R.string.collection_dialog_favorite),
                    onClick = { onCollectionSelected(AnimeCollectionType.FAVORITE) }
                )
                Spacer(modifier = Modifier.height(14.dp))
                CollectionOption(
                    text = stringResource(R.string.collection_dialog_watched),
                    onClick = { onCollectionSelected(AnimeCollectionType.WATCHED) }
                )
            }
        }
    }
}

@Composable
private fun CollectionOption(text: String, onClick: () -> Unit) {
    Text(
        text = text,
        color = Color.White,
        fontSize = 20.sp,
        textAlign = TextAlign.Center,
        modifier = Modifier
            .fillMaxWidth()
            .border(BorderStroke(1.dp, Color.White), RoundedCornerShape(12.dp))
            .clickable { onClick() }
            .padding(vertical = 12.dp, horizontal = 16.dp)
    )
}
