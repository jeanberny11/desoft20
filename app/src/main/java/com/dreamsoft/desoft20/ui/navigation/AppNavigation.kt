package com.dreamsoft.desoft20.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.dreamsoft.desoft20.ui.screens.config.ConfigurationScreen
import com.dreamsoft.desoft20.ui.screens.main.MainScreen

object Routes {
    const val MAIN = "main"
    const val CONFIGURATION = "configuration"
}

@Composable
fun AppNavigation(
    navController: NavHostController
) {
    NavHost(
        navController = navController,
        startDestination = Routes.MAIN
    ) {
        composable(Routes.MAIN) {
            MainScreen(
            )
        }

        composable(Routes.CONFIGURATION) {
            ConfigurationScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}