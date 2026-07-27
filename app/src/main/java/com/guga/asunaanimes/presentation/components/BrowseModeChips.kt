package com.guga.asunaanimes.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.guga.asunaanimes.R
import com.guga.asunaanimes.domain.model.AnimeBrowseMode
import com.guga.asunaanimes.presentation.theme.AsunaBlack
import com.guga.asunaanimes.presentation.theme.AsunaOnSurfaceMuted
import com.guga.asunaanimes.presentation.theme.AsunaOrange
import com.guga.asunaanimes.presentation.theme.AsunaSurfaceElevated

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BrowseModeChips(
    selectedMode: AnimeBrowseMode,
    enabled: Boolean,
    onModeSelected: (AnimeBrowseMode) -> Unit,
    modifier: Modifier = Modifier
) {
    val modes = listOf(
        AnimeBrowseMode.TOP to R.string.filter_top_anime,
        AnimeBrowseMode.SEASONAL to R.string.filter_seasonal_anime,
        AnimeBrowseMode.RECOMMENDATIONS to R.string.filter_recommendations
    )

    LazyRow(
        modifier = modifier.fillMaxWidth(),
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        items(modes, key = { it.first.name }) { (mode, labelRes) ->
            val selected = selectedMode == mode && enabled
            FilterChip(
                selected = selected,
                onClick = { onModeSelected(mode) },
                label = { Text(text = stringResource(labelRes)) },
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier.height(36.dp),
                colors = FilterChipDefaults.filterChipColors(
                    containerColor = AsunaSurfaceElevated.copy(alpha = 0.85f),
                    labelColor = AsunaOnSurfaceMuted,
                    selectedContainerColor = AsunaOrange,
                    selectedLabelColor = AsunaBlack
                )
            )
        }
    }
}
