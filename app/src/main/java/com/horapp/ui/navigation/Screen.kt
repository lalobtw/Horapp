package com.horapp.ui.navigation

sealed class Screen(val route: String) {
    data object Setup : Screen("setup")
    data object Dashboard : Screen("dashboard")
    data object LogHours : Screen("log_hours")
    data object History : Screen("history")
    data object Profile : Screen("profile")
}

// Bottom nav items
val bottomNavItems = listOf(
    Screen.Dashboard,
    Screen.History,
    Screen.Profile
)
