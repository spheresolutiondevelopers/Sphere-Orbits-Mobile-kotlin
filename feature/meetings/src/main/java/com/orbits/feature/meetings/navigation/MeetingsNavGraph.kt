package com.orbits.feature.meetings.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.orbits.feature.meetings.MeetingListScreen
import com.orbits.feature.meetings.MeetingDetailScreen

/**
 * Navigation constants for meetings graph.
 */
object MeetingsRoutes {
    const val MEETINGS_GRAPH = "meetings_graph"
    const val MEETINGS_LIST = "meetings_list"
    const val MEETING_DETAIL = "meeting_detail/{meetingId}"
    const val MEETING_CREATE = "meeting_create"
    const val MEETING_EDIT = "meeting_edit/{meetingId}"

    fun meetingDetail(meetingId: String): String = "meeting_detail/$meetingId"
    fun meetingEdit(meetingId: String): String = "meeting_edit/$meetingId"
}

/**
 * Meetings navigation graph builder.
 */
fun NavGraphBuilder.meetingsNavGraph(
    navController: NavHostController
) {
    navigation(
        route = MeetingsRoutes.MEETINGS_GRAPH,
        startDestination = MeetingsRoutes.MEETINGS_LIST
    ) {
        composable(MeetingsRoutes.MEETINGS_LIST) {
            MeetingListScreen(
                onNavigateToMeetingDetail = { meetingId ->
                    navController.navigate(MeetingsRoutes.meetingDetail(meetingId))
                },
                onNavigateToCreate = {
                    navController.navigate(MeetingsRoutes.MEETING_CREATE)
                }
            )
        }

        composable(MeetingsRoutes.MEETING_DETAIL) { backStackEntry ->
            val meetingId = backStackEntry.arguments?.getString("meetingId") ?: ""
            MeetingDetailScreen(
                meetingId = meetingId,
                onBack = { navController.popBackStack() }
            )
        }

        // Create and edit screens to be added later
    }
}
