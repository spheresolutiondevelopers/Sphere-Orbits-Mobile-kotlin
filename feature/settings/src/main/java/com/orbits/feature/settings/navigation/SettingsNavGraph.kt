package com.orbits.feature.settings.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.orbits.feature.settings.SettingsScreen
import com.orbits.feature.settings.ProfileEditScreen
import com.orbits.feature.settings.IntegrationsScreen

/**
 * Navigation constants for settings graph.
 */
object SettingsRoutes {
    const val SETTINGS_GRAPH = "settings_graph"
    const val SETTINGS = "settings_screen"
    const val PROFILE_EDIT = "profile_edit"
    const val INTEGRATIONS = "integrations"
    const val HELP = "help"
    const val ABOUT = "about"
}

/**
 * Settings navigation graph builder.
 */
fun NavGraphBuilder.settingsNavGraph(
    navController: NavHostController,
    onNavigateToSignup: () -> Unit,
    onSignOut: () -> Unit
) {
    navigation(
        route = SettingsRoutes.SETTINGS_GRAPH,
        startDestination = SettingsRoutes.SETTINGS
    ) {
        composable(SettingsRoutes.SETTINGS) {
            SettingsScreen(
                onNavigateToProfileEdit = {
                    navController.navigate(SettingsRoutes.PROFILE_EDIT)
                },
                onNavigateToIntegrations = {
                    navController.navigate(SettingsRoutes.INTEGRATIONS)
                },
                onNavigateToHelp = {
                    // Help screen (to be implemented)
                },
                onNavigateToAbout = {
                    // About screen (to be implemented)
                },
                onNavigateToSignup = onNavigateToSignup,
                onSignOut = {
                    navController.navigate("auth") {
                        popUpTo(SettingsRoutes.SETTINGS_GRAPH) { inclusive = true }
                    }
                    onSignOut()
                }
            )
        }

        composable(SettingsRoutes.PROFILE_EDIT) {
            ProfileEditScreen(
                onBack = { navController.popBackStack() },
                onSaveSuccess = { navController.popBackStack() }
            )
        }

        composable(SettingsRoutes.INTEGRATIONS) {
            IntegrationsScreen(
                onBack = { navController.popBackStack() }
            )
        }
    }
}
