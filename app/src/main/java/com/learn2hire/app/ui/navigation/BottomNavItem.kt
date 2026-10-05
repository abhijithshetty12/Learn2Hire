package com.learn2hire.app.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.MenuBook
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.ShowChart
import androidx.compose.material.icons.outlined.WorkOutline
import androidx.compose.ui.graphics.vector.ImageVector

data class BottomNavItem(
    val label: String,
    val route: String,
    val icon: ImageVector
)

val bottomNavItems = listOf(

    BottomNavItem(
        label = "Home",
        route = Screen.Home.route,
        icon = Icons.Outlined.Home
    ),

    BottomNavItem(
        label = "Study",
        route = Screen.Study.route,
        icon = Icons.Outlined.MenuBook
    ),

    BottomNavItem(
        label = "Prep",
        route = Screen.Interview.route,
        icon = Icons.Outlined.WorkOutline
    ),

    BottomNavItem(
        label = "Progress",
        route = Screen.Progress.route,
        icon = Icons.Outlined.ShowChart
    ),

    BottomNavItem(
        label = "You",
        route = Screen.Profile.route,
        icon = Icons.Outlined.Person
    )
)