package com.orbits.feature.appointments.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.orbits.feature.appointments.AppointmentListScreen
import com.orbits.feature.appointments.AppointmentFormScreen

/**
 * Navigation constants for appointments graph.
 */
object AppointmentsRoutes {
    const val APPOINTMENTS_GRAPH = "appointments_graph"
    const val APPOINTMENTS_LIST = "appointments_list"
    const val APPOINTMENT_DETAIL = "appointment_detail/{appointmentId}"
    const val APPOINTMENT_CREATE = "appointment_create"
    const val APPOINTMENT_EDIT = "appointment_edit/{appointmentId}"

    fun appointmentDetail(appointmentId: String): String = "appointment_detail/$appointmentId"
    fun appointmentEdit(appointmentId: String): String = "appointment_edit/$appointmentId"
}

/**
 * Appointments navigation graph builder.
 */
fun NavGraphBuilder.appointmentsNavGraph(
    navController: NavHostController
) {
    navigation(
        route = AppointmentsRoutes.APPOINTMENTS_GRAPH,
        startDestination = AppointmentsRoutes.APPOINTMENTS_LIST
    ) {
        composable(AppointmentsRoutes.APPOINTMENTS_LIST) {
            AppointmentListScreen(
                onNavigateToAppointmentDetail = { appointmentId ->
                    navController.navigate(AppointmentsRoutes.appointmentDetail(appointmentId))
                },
                onNavigateToCreate = {
                    navController.navigate(AppointmentsRoutes.APPOINTMENT_CREATE)
                }
            )
        }

        composable(AppointmentsRoutes.APPOINTMENT_CREATE) {
            AppointmentFormScreen(
                appointmentId = null,
                onBack = { navController.popBackStack() },
                onSaveSuccess = { navController.popBackStack() }
            )
        }

        // Detail and edit screens to be added later
    }
}
