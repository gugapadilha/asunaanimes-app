package com.guga.asunaanimes.presentation.components

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.guga.asunaanimes.domain.model.Anime
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.filter

/**
 * Wraps a screen with the anime detail sheet and keeps the sheet in sync with [selectedAnime],
 * which lives in the ViewModel. Swipe-to-dismiss is reported back through [onDismissed] so the
 * same anime can be opened again right after being closed.
 */
@OptIn(ExperimentalMaterialApi::class)
@Composable
fun AnimeDetailsBottomSheetLayout(
    selectedAnime: Anime?,
    actionLabel: String,
    onActionClick: () -> Unit,
    onDismissed: () -> Unit,
    content: @Composable () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(initialValue = ModalBottomSheetValue.Hidden)
    val currentOnDismissed by rememberUpdatedState(onDismissed)

    LaunchedEffect(selectedAnime) {
        if (selectedAnime != null) sheetState.show() else sheetState.hide()
    }

    LaunchedEffect(sheetState) {
        snapshotFlow { sheetState.isVisible }
            .drop(1)
            .filter { isVisible -> !isVisible }
            .collect { currentOnDismissed() }
    }

    ModalBottomSheetLayout(
        sheetState = sheetState,
        sheetContent = {
            if (selectedAnime == null) {
                // A sheet without content has no anchors to animate to and would crash the layout.
                Spacer(modifier = Modifier.height(1.dp))
            } else {
                AnimeDetailsSheet(
                    anime = selectedAnime,
                    actionLabel = actionLabel,
                    onActionClick = onActionClick
                )
            }
        },
        content = content
    )
}
