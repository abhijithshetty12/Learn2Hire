package com.learn2hire.app.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.learn2hire.app.ui.auth.AuthViewModel
import com.learn2hire.app.ui.components.Learn2HireBottomBar
import com.learn2hire.app.ui.components.Learn2HireTopBrandBar
import com.learn2hire.app.ui.navigation.Learn2HireNavGraph
import com.learn2hire.app.ui.navigation.Screen
import com.learn2hire.app.ui.navigation.bottomNavItems
import com.learn2hire.app.ui.theme.ThemeMode

@Composable
fun Learn2HireApp(
    themeMode: ThemeMode,
    onThemeModeChange: (ThemeMode) -> Unit
) {

    val authViewModel: AuthViewModel = viewModel()
    val authState by authViewModel.uiState.collectAsState()

    val navController = rememberNavController()

    val backStackEntry by
    navController.currentBackStackEntryAsState()

    val currentRoute =
        backStackEntry?.destination?.route

    val showBottomBar =
        authState.isAuthenticated &&
                currentRoute in listOf(
            Screen.Home.route,
            Screen.Study.route,
            Screen.Interview.route,
            Screen.Progress.route,
            Screen.Profile.route
        )

    val showTopBrandBar =
        authState.isAuthenticated &&
                currentRoute != Screen.Login.route &&
                currentRoute != Screen.Register.route

    Scaffold(
        containerColor =
            androidx.compose.material3.MaterialTheme
                .colorScheme.background,

        topBar = {
            if (showTopBrandBar) {
                Learn2HireTopBrandBar(
                    onLogoClick = {
                        navController.navigate(Screen.Home.route) {
                            popUpTo(navController.graph.startDestinationId) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }
        },

        bottomBar = {

            if (showBottomBar) {

                Learn2HireBottomBar(
                    currentRoute = currentRoute,
                    items = bottomNavItems,
                    onNavigate = { route ->

                        navController.navigate(route) {

                            popUpTo(
                                navController.graph.startDestinationId
                            ) {
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

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {

            Learn2HireNavGraph(
                navController = navController,
                authViewModel = authViewModel,
                themeMode = themeMode,
                onThemeModeChange =
                    onThemeModeChange
            )
        }
    }
}