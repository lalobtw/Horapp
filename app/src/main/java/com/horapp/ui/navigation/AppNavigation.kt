package com.horapp.ui.navigation

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.Dashboard
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.horapp.ui.screens.dashboard.DashboardScreen
import com.horapp.ui.screens.history.HistoryScreen
import com.horapp.ui.screens.loghours.LogHoursScreen
import com.horapp.ui.screens.profile.ProfileScreen
import com.horapp.ui.screens.setup.SetupScreen
import com.horapp.ui.theme.OnSurface
import com.horapp.ui.theme.Primary
import com.horapp.ui.theme.SurfaceContainerLowest

data class BottomNavItem(
    val screen: Screen,
    val labelResId: Int,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
)

private val navItems = listOf(
    BottomNavItem(Screen.Dashboard, com.horapp.R.string.dashboard, Icons.Filled.Dashboard, Icons.Outlined.Dashboard),
    BottomNavItem(Screen.History, com.horapp.R.string.history, Icons.Filled.History, Icons.Outlined.History),
    BottomNavItem(Screen.Profile, com.horapp.R.string.profile, Icons.Filled.Person, Icons.Outlined.Person),
)

@Composable
fun AppNavigation(startDestination: String) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val showBottomBar = currentRoute in listOf(
        Screen.Dashboard.route, Screen.History.route, Screen.Profile.route
    )

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            if (showBottomBar) {
                HorappBottomBar(
                    currentRoute = currentRoute,
                    onNavigate = { screen ->
                        navController.navigate(screen.route) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = startDestination,
            modifier = Modifier.padding(bottom = innerPadding.calculateBottomPadding()),
            enterTransition = { fadeIn(tween(200)) },
            exitTransition = { fadeOut(tween(200)) }
        ) {
            composable(Screen.Setup.route) {
                SetupScreen(
                    onSetupComplete = {
                        navController.navigate(Screen.Dashboard.route) {
                            popUpTo(Screen.Setup.route) { inclusive = true }
                        }
                    }
                )
            }
            composable(Screen.Dashboard.route) {
                DashboardScreen(
                    onLogHours = { navController.navigate(Screen.LogHours.route) }
                )
            }
            composable(Screen.LogHours.route) {
                LogHoursScreen(
                    onBack = { navController.popBackStack() }
                )
            }
            composable(Screen.History.route) {
                HistoryScreen()
            }
            composable(Screen.Profile.route) {
                ProfileScreen()
            }
        }
    }
}

@Composable
private fun HorappBottomBar(
    currentRoute: String?,
    onNavigate: (Screen) -> Unit
) {
    NavigationBar(
        containerColor = SurfaceContainerLowest.copy(alpha = 0.92f),
        tonalElevation = androidx.compose.ui.unit.Dp(0f)
    ) {
        navItems.forEach { item ->
            val selected = currentRoute == item.screen.route
            val label = stringResource(item.labelResId)
            NavigationBarItem(
                selected = selected,
                onClick = { onNavigate(item.screen) },
                icon = {
                    Icon(
                        imageVector = if (selected) item.selectedIcon else item.unselectedIcon,
                        contentDescription = label
                    )
                },
                label = { Text(text = label) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = Primary,
                    selectedTextColor = Primary,
                    unselectedIconColor = OnSurface.copy(alpha = 0.5f),
                    unselectedTextColor = OnSurface.copy(alpha = 0.5f),
                    indicatorColor = Primary.copy(alpha = 0.12f)
                )
            )
        }
    }
}
