package com.orbits.feature.auth.navigation

import androidx.compose.material3.Text
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.orbits.feature.auth.LoginScreen
import com.orbits.feature.auth.SignupScreen

/**
 * Navigation constants for auth graph.
 */
object AuthRoutes {
    const val AUTH_GRAPH = "auth"
    const val LOGIN = "login"
    const val SIGNUP = "signup"
    const val FORGOT_PASSWORD = "forgot_password"
}

/**
 * Auth navigation graph builder.
 */
fun NavGraphBuilder.authNavGraph(
    navController: NavHostController,
    onLoginSuccess: () -> Unit,
    onSignupSuccess: () -> Unit
) {
    navigation(
        route = AuthRoutes.AUTH_GRAPH,
        startDestination = AuthRoutes.LOGIN
    ) {
        composable(AuthRoutes.LOGIN) {
            LoginScreen(
                onLoginSuccess = {
                    onLoginSuccess()
                },
                onNavigateToSignup = {
                    navController.navigate(AuthRoutes.SIGNUP) {
                        popUpTo(AuthRoutes.LOGIN) { inclusive = false }
                    }
                }
            )
        }

        composable(AuthRoutes.SIGNUP) {
            SignupScreen(
                onSignupSuccess = {
                    onSignupSuccess()
                },
                onNavigateToLogin = {
                    navController.navigate(AuthRoutes.LOGIN) {
                        popUpTo(AuthRoutes.SIGNUP) { inclusive = true }
                    }
                }
            )
        }

        composable(AuthRoutes.FORGOT_PASSWORD) {
            // Forgot password screen (placeholder)
            // In production, implement this screen
            Text("Forgot Password Screen")
        }
    }
}
