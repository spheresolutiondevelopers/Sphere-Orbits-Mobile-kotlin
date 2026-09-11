package com.orbits.feature.analytics.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.orbits.feature.analytics.AnalyticsScreen

/**
 * Navigation constants for analytics graph.
 */
object AnalyticsRoutes {
    const val ANALYTICS_GRAPH = "analytics_graph"
    const val ANALYTICS = "analytics_screen"
}

/**
 * Analytics navigation graph builder.
 */
fun NavGraphBuilder.analyticsNavGraph(
    navController: NavHostController
) {
    navigation(
        route = AnalyticsRoutes.ANALYTICS_GRAPH,
        startDestination = AnalyticsRoutes.ANALYTICS
    ) {
        composable(AnalyticsRoutes.ANALYTICS) {
            AnalyticsScreen()
        }
    }
}
