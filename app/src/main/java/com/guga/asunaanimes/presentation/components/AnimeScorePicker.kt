package com.guga.asunaanimes.presentation.components

import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.guga.asunaanimes.R
import com.guga.asunaanimes.domain.model.UserScoreStats
import com.guga.asunaanimes.presentation.theme.AsunaBlack
import com.guga.asunaanimes.presentation.theme.AsunaOnSurface
import com.guga.asunaanimes.presentation.theme.AsunaOnSurfaceMuted
import com.guga.asunaanimes.presentation.theme.AsunaSurfaceElevated
import com.guga.asunaanimes.presentation.theme.userScoreColor
import com.guga.asunaanimes.presentation.theme.userScoreContentColor

@StringRes
fun userScoreLabelRes(score: Int): Int = when (score) {
    10 -> R.string.score_label_10
    9 -> R.string.score_label_9
    8 -> R.string.score_label_8
    7 -> R.string.score_label_7
    6 -> R.string.score_label_6
    5 -> R.string.score_label_5
    4 -> R.string.score_label_4
    3 -> R.string.score_label_3
    2 -> R.string.score_label_2
    else -> R.string.score_label_1
}

/**
 * Vertical MAL-style score list (10 → 1) with optional Skip.
 */
@Composable
fun AnimeScoreOptionList(
    selectedScore: Int?,
    onScoreSelected: (Int) -> Unit,
    modifier: Modifier = Modifier,
    allowSkip: Boolean = false,
    onSkip: (() -> Unit)? = null
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .heightIn(max = 340.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        (UserScoreStats.MAX_SCORE downTo UserScoreStats.MIN_SCORE).forEach { score ->
            val selected = selectedScore == score
            val background = if (selected) userScoreColor(score) else AsunaBlack.copy(alpha = 0.35f)
            val content = if (selected) userScoreContentColor(score) else AsunaOnSurface
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(background)
                    .border(
                        width = 1.dp,
                        color = if (selected) {
                            userScoreColor(score)
                        } else {
                            AsunaOnSurfaceMuted.copy(alpha = 0.25f)
                        },
                        shape = RoundedCornerShape(12.dp)
                    )
                    .clickable { onScoreSelected(score) }
                    .padding(horizontal = 14.dp, vertical = 11.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(
                        R.string.score_option_format,
                        score,
                        stringResource(userScoreLabelRes(score))
                    ),
                    color = content,
                    fontSize = 15.sp,
                    fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium
                )
            }
        }
        if (allowSkip && onSkip != null) {
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = stringResource(R.string.collection_dialog_skip_rating),
                color = AsunaOnSurfaceMuted,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .clickable(onClick = onSkip)
                    .padding(vertical = 12.dp)
            )
        }
    }
}

@Composable
fun AnimeScorePickerDialog(
    currentScore: Int?,
    onDismiss: () -> Unit,
    onConfirm: (Int?) -> Unit,
    allowSkip: Boolean = true
) {
    var selected by remember { mutableStateOf(currentScore) }

    Dialog(onDismissRequest = onDismiss) {
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
                    text = stringResource(R.string.collection_dialog_rating_title),
                    color = AsunaOnSurface,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
                Text(
                    text = stringResource(R.string.collection_dialog_rating_subtitle),
                    color = AsunaOnSurfaceMuted,
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center
                )
                AnimeScoreOptionList(
                    selectedScore = selected,
                    onScoreSelected = { selected = it },
                    allowSkip = allowSkip,
                    onSkip = { onConfirm(null) }
                )
                AsunaPrimaryButton(
                    text = stringResource(R.string.collection_dialog_confirm_rating),
                    onClick = { selected?.let(onConfirm) },
                    fontSize = 14.sp,
                    minHeight = 44.dp,
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .alpha(if (selected != null) 1f else 0.45f)
                )
                Text(
                    text = stringResource(R.string.collection_dialog_back),
                    color = AsunaOnSurfaceMuted,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier
                        .clickable(onClick = onDismiss)
                        .padding(vertical = 6.dp)
                )
            }
        }
    }
}
