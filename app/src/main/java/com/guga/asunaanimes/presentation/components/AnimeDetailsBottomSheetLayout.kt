package com.guga.asunaanimes.presentation.components

import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.guga.asunaanimes.domain.model.Anime
import com.guga.asunaanimes.presentation.theme.AsunaScrim
import com.guga.asunaanimes.presentation.theme.AsunaSurface
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.filter

/**
 * Wraps a screen with the anime detail sheet and keeps the sheet in sync with [selectedAnime],
 * which lives in the ViewModel. Swipe-to-dismiss is reported back through [onDismissed] so the
 * same anime can be opened again right after being closed.
 *
 * [topChromeClearance] is the height of UI that must stay visible above the sheet (typically the
 * search bar). Combined with the status bar inset it caps the sheet so it never covers that chrome.
 */
@OptIn(ExperimentalMaterialApi::class)
@Composable
fun AnimeDetailsBottomSheetLayout(
    selectedAnime: Anime?,
    actionLabel: String,
    onActionClick: () -> Unit,
    onDismissed: () -> Unit,
    isDetailsLoading: Boolean = false,
    secondaryActionLabel: String? = null,
    onSecondaryActionClick: (() -> Unit)? = null,
    topChromeClearance: Dp = DefaultSearchChromeClearance,
    content: @Composable () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(
        initialValue = ModalBottomSheetValue.Hidden,
        skipHalfExpanded = true
    )
    val currentOnDismissed by rememberUpdatedState(onDismissed)
    val statusBarPadding = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()

    LaunchedEffect(selectedAnime) {
        if (selectedAnime != null) sheetState.show() else sheetState.hide()
    }

    LaunchedEffect(sheetState) {
        snapshotFlow { sheetState.isVisible }
            .drop(1)
            .filter { isVisible -> !isVisible }
            .collect { currentOnDismissed() }
    }

    BoxWithConstraints {
        val maxSheetHeight = (maxHeight - statusBarPadding - topChromeClearance)
            .coerceAtLeast(MinSheetHeight)

        ModalBottomSheetLayout(
            sheetState = sheetState,
            sheetShape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
            sheetBackgroundColor = AsunaSurface,
            scrimColor = AsunaScrim,
            sheetContent = {
                if (selectedAnime == null) {
                    // A sheet without content has no anchors to animate to and would crash the layout.
                    Spacer(modifier = Modifier.height(1.dp))
                } else {
                    AnimeDetailsSheet(
                        anime = selectedAnime,
                        actionLabel = actionLabel,
                        onActionClick = onActionClick,
                        secondaryActionLabel = secondaryActionLabel,
                        onSecondaryActionClick = onSecondaryActionClick,
                        isDetailsLoading = isDetailsLoading,
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = maxSheetHeight)
                    )
                }
            },
            content = content
        )
    }
}

/**
 * SearchBox outer block ≈ 12dp padding + ~52dp field. SearchScreen also adds 8dp above it.
 * Keeping this under the sheet makes the grab handle and dismiss gesture obvious.
 */
val DefaultSearchChromeClearance = 80.dp

/** Collection screens stack a title header above the search field. */
val CollectionSearchChromeClearance = 140.dp

/**
 * Space reserved at the bottom of scrollable tab content so items clear the floating nav pill
 * (bar height + outer padding around it).
 */
val FloatingBottomNavClearance = 100.dp

private val MinSheetHeight = 280.dp
