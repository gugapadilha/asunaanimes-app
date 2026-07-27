package com.guga.asunaanimes.presentation.navigation

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.ui.graphics.vector.ImageVector
import com.guga.asunaanimes.R

enum class BottomNavItem(
    val screen: Screen,
    @StringRes val labelRes: Int,
    val icon: ImageVector
) {
    Search(
        screen = Screen.Search,
        labelRes = R.string.nav_search,
        icon = Icons.Filled.Search
    ),
    Watched(
        screen = Screen.Watched,
        labelRes = R.string.nav_watched,
        icon = Icons.Filled.Visibility
    ),
    Favorite(
        screen = Screen.Favorite,
        labelRes = R.string.nav_favorite,
        icon = Icons.Filled.Favorite
    )
}
