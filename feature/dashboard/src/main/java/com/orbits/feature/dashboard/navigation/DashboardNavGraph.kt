package com.orbits.feature.dashboard.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.orbits.feature.dashboard.DashboardScreen

/**
 * Navigation constants for dashboard graph.
 */
object DashboardRoutes {
    const val DASHBOARD_GRAPH = "dashboard"
    const val DASHBOARD = "dashboard_screen"
}

/**
 * Dashboard navigation graph builder.
 */
fun NavGraphBuilder.dashboardNavGraph(
    navController: NavHostController,
    onNavigateToTasks: () -> Unit,
    onNavigateToCalendar: () -> Unit,
    onNavigateToMeetings: () -> Unit,
    onNavigateToAppointments: () -> Unit,
    onNavigateToChat: () -> Unit,
    onNavigateToNotes: () -> Unit,
    onNavigateToAnalytics: () -> Unit,
    onNavigateToTaskDetail: (String) -> Unit,
    onNavigateToSettings: () -> Unit
) {
    navigation(
        route = DashboardRoutes.DASHBOARD_GRAPH,
        startDestination = DashboardRoutes.DASHBOARD
    ) {
        composable(DashboardRoutes.DASHBOARD) {
            DashboardScreen(
                onNavigateToTasks = onNavigateToTasks,
                onNavigateToCalendar = onNavigateToCalendar,
                onNavigateToMeetings = onNavigateToMeetings,
                onNavigateToAppointments = onNavigateToAppointments,
                onNavigateToChat = onNavigateToChat,
                onNavigateToNotes = onNavigateToNotes,
                onNavigateToAnalytics = onNavigateToAnalytics,
                onNavigateToTaskDetail = onNavigateToTaskDetail,
                onNavigateToSettings = onNavigateToSettings
            )
        }
    }
}
