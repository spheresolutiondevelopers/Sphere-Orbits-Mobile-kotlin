package com.orbits.feature.calendar.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.orbits.feature.calendar.CalendarScreen

/**
 * Navigation constants for calendar graph.
 */
object CalendarRoutes {
    const val CALENDAR_GRAPH = "calendar_graph"
    const val CALENDAR = "calendar_screen"
    const val EVENT_DETAIL = "event_detail/{eventId}"
}

/**
 * Calendar navigation graph builder.
 */
fun NavGraphBuilder.calendarNavGraph(
    navController: NavHostController,
    onNavigateToEventDetail: (String) -> Unit
) {
    navigation(
        route = CalendarRoutes.CALENDAR_GRAPH,
        startDestination = CalendarRoutes.CALENDAR
    ) {
        composable(CalendarRoutes.CALENDAR) {
            CalendarScreen(
                onNavigateToEventDetail = { eventId ->
                    navController.navigate(CalendarRoutes.EVENT_DETAIL.replace("{eventId}", eventId))
                }
            )
        }
        // Event detail screen (to be implemented in a separate feature)
    }
}
