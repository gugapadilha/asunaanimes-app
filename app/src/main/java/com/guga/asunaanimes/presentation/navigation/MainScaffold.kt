package com.guga.asunaanimes.presentation.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.guga.asunaanimes.presentation.collection.FavoriteScreen
import com.guga.asunaanimes.presentation.collection.WatchedScreen
import com.guga.asunaanimes.presentation.search.SearchScreen
import com.guga.asunaanimes.presentation.theme.AsunaBlack
import com.guga.asunaanimes.presentation.theme.AsunaNavBar
import com.guga.asunaanimes.presentation.theme.AsunaOnSurfaceMuted
import com.guga.asunaanimes.presentation.theme.AsunaOrange

private const val TAB_ANIMATION_MS = 280

@Composable
fun MainScaffold() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    Scaffold(
        containerColor = AsunaBlack,
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        bottomBar = {
            NavigationBar(
                containerColor = AsunaNavBar,
                tonalElevation = 0.dp
            ) {
                BottomNavItem.entries.forEach { item ->
                    val selected = currentDestination
                        ?.hierarchy
                        ?.any { it.route == item.screen.route } == true

                    NavigationBarItem(
                        selected = selected,
                        onClick = { navController.navigateToTab(item.screen) },
                        icon = {
                            Icon(
                                imageVector = item.icon,
                                contentDescription = stringResource(item.labelRes)
                            )
                        },
                        label = {
                            Text(
                                text = stringResource(item.labelRes),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = AsunaOrange,
                            selectedTextColor = AsunaOrange,
                            unselectedIconColor = AsunaOnSurfaceMuted,
                            unselectedTextColor = AsunaOnSurfaceMuted,
                            indicatorColor = AsunaOrange.copy(alpha = 0.18f)
                        )
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Search.route,
            modifier = Modifier.padding(bottom = innerPadding.calculateBottomPadding()),
            enterTransition = {
                fadeIn(animationSpec = tween(TAB_ANIMATION_MS)) +
                    slideIntoContainer(
                        towards = AnimatedContentTransitionScope.SlideDirection.Up,
                        animationSpec = tween(TAB_ANIMATION_MS)
                    )
            },
            exitTransition = {
                fadeOut(animationSpec = tween(TAB_ANIMATION_MS))
            },
            popEnterTransition = {
                fadeIn(animationSpec = tween(TAB_ANIMATION_MS))
            },
            popExitTransition = {
                fadeOut(animationSpec = tween(TAB_ANIMATION_MS))
            }
        ) {
            composable(route = Screen.Search.route) {
                SearchScreen()
            }
            composable(route = Screen.Watched.route) {
                WatchedScreen()
            }
            composable(route = Screen.Favorite.route) {
                FavoriteScreen()
            }
        }
    }
}

private fun NavHostController.navigateToTab(screen: Screen) {
    navigate(screen.route) {
        popUpTo(graph.findStartDestination().id) {
            saveState = true
        }
        launchSingleTop = true
        restoreState = true
    }
}
