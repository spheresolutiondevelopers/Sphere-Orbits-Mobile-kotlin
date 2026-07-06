/*
 * Copyright © 2026 Sphere Solution Developers
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */

package com.orbits.app.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navigation
import com.orbits.feature.auth.LoginScreen
import com.orbits.feature.auth.SignupScreen
import com.orbits.feature.auth.AuthViewModel
import com.orbits.feature.dashboard.DashboardScreen
import com.orbits.feature.dashboard.DashboardViewModel
import com.orbits.feature.tasks.TaskListScreen
import com.orbits.feature.tasks.TaskDetailScreen
import com.orbits.feature.tasks.TaskViewModel
import com.orbits.feature.calendar.CalendarScreen
import com.orbits.feature.calendar.CalendarViewModel
import com.orbits.feature.events.EventListScreen
import com.orbits.feature.events.EventDetailScreen
import com.orbits.feature.events.EventViewModel
import com.orbits.feature.meetings.MeetingListScreen
import com.orbits.feature.meetings.MeetingDetailScreen
import com.orbits.feature.meetings.MeetingViewModel
import com.orbits.feature.appointments.AppointmentListScreen
import com.orbits.feature.appointments.AppointmentFormScreen
import com.orbits.feature.appointments.AppointmentViewModel
import com.orbits.feature.chat.ChatListScreen
import com.orbits.feature.chat.ConversationScreen
import com.orbits.feature.chat.ChatViewModel
import com.orbits.feature.notes.NotesListScreen
import com.orbits.feature.notes.NoteEditorScreen
import com.orbits.feature.notes.NotesViewModel
import com.orbits.feature.analytics.AnalyticsScreen
import com.orbits.feature.analytics.AnalyticsViewModel
import com.orbits.feature.settings.SettingsScreen
import com.orbits.feature.settings.ProfileEditScreen
import com.orbits.feature.settings.IntegrationsScreen
import com.orbits.feature.settings.SettingsViewModel

/**
 * Navigation constants
 */
object Routes {
    // Auth
    const val AUTH_GRAPH = "auth"
    const val LOGIN = "login"
    const val SIGNUP = "signup"

    // Main
    const val MAIN_GRAPH = "main"
    const val DASHBOARD = "dashboard"
    const val TASKS = "tasks"
    const val TASK_DETAIL = "task_detail/{taskId}"
    const val CALENDAR = "calendar"
    const val EVENTS = "events"
    const val EVENT_DETAIL = "event_detail/{eventId}"
    const val MEETINGS = "meetings"
    const val MEETING_DETAIL = "meeting_detail/{meetingId}"
    const val APPOINTMENTS = "appointments"
    const val APPOINTMENT_FORM = "appointment_form/{appointmentId?}"
    const val CHAT = "chat"
    const val CONVERSATION = "conversation/{conversationId}"
    const val NOTES = "notes"
    const val NOTE_EDITOR = "note_editor/{noteId?}"
    const val ANALYTICS = "analytics"
    const val SETTINGS = "settings"
    const val PROFILE_EDIT = "profile_edit"
    const val INTEGRATIONS = "integrations"

    // Parameter extraction
    fun taskId(route: String): String = route.substringAfter("task_detail/")
    fun eventId(route: String): String = route.substringAfter("event_detail/")
    fun meetingId(route: String): String = route.substringAfter("meeting_detail/")
    fun conversationId(route: String): String = route.substringAfter("conversation/")
    fun appointmentId(route: String): String? {
        val id = route.substringAfter("appointment_form/")
        return if (id.isEmpty() || id == "null") null else id
    }
    fun noteId(route: String): String? {
        val id = route.substringAfter("note_editor/")
        return if (id.isEmpty() || id == "null") null else id
    }
}

/**
 * Root navigation graph for the application.
 * Contains auth graph and main graph, with conditional navigation based on auth state.
 */
@Composable
fun AppNavGraph(
    navController: NavHostController = rememberNavController(),
    startDestination: String = Routes.AUTH_GRAPH
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        builder = {
            authGraph(navController)
            mainGraph(navController)
        }
    )
}

/**
 * Auth navigation graph
 * - Login screen
 * - Signup screen
 * All unauthenticated routes live here.
 */
private fun NavGraphBuilder.authGraph(navController: NavHostController) {
    navigation(
        route = Routes.AUTH_GRAPH,
        startDestination = Routes.LOGIN
    ) {
        composable(Routes.LOGIN) {
            val viewModel: AuthViewModel = hiltViewModel()
            LoginScreen(
                viewModel = viewModel,
                onLoginSuccess = {
                    navController.navigate(Routes.MAIN_GRAPH) {
                        popUpTo(Routes.AUTH_GRAPH) { inclusive = true }
                    }
                },
                onNavigateToSignup = {
                    navController.navigate(Routes.SIGNUP)
                }
            )
        }

        composable(Routes.SIGNUP) {
            val viewModel: AuthViewModel = hiltViewModel()
            SignupScreen(
                viewModel = viewModel,
                onSignupSuccess = {
                    navController.navigate(Routes.MAIN_GRAPH) {
                        popUpTo(Routes.AUTH_GRAPH) { inclusive = true }
                    }
                },
                onNavigateToLogin = {
                    navController.navigate(Routes.LOGIN) {
                        popUpTo(Routes.SIGNUP) { inclusive = true }
                    }
                }
            )
        }
    }
}

/**
 * Main navigation graph
 * All authenticated routes live here.
 */
private fun NavGraphBuilder.mainGraph(navController: NavHostController) {
    navigation(
        route = Routes.MAIN_GRAPH,
        startDestination = Routes.DASHBOARD
    ) {
        // ─── Dashboard ──────────────────────────────────────────
        composable(Routes.DASHBOARD) {
            val viewModel: DashboardViewModel = hiltViewModel()
            DashboardScreen(
                viewModel = viewModel,
                onNavigateToTasks = {
                    navController.navigate(Routes.TASKS)
                },
                onNavigateToCalendar = {
                    navController.navigate(Routes.CALENDAR)
                },
                onNavigateToMeetings = {
                    navController.navigate(Routes.MEETINGS)
                },
                onNavigateToAppointments = {
                    navController.navigate(Routes.APPOINTMENTS)
                },
                onNavigateToChat = {
                    navController.navigate(Routes.CHAT)
                },
                onNavigateToNotes = {
                    navController.navigate(Routes.NOTES)
                },
                onNavigateToAnalytics = {
                    navController.navigate(Routes.ANALYTICS)
                }
            )
        }

        // ─── Tasks ──────────────────────────────────────────────
        composable(Routes.TASKS) {
            val viewModel: TaskViewModel = hiltViewModel()
            TaskListScreen(
                viewModel = viewModel,
                onNavigateToTaskDetail = { taskId ->
                    navController.navigate("task_detail/$taskId")
                }
            )
        }

        composable(Routes.TASK_DETAIL) { backStackEntry ->
            val taskId = Routes.taskId(backStackEntry.route ?: "")
            val viewModel: TaskViewModel = hiltViewModel()
            TaskDetailScreen(
                viewModel = viewModel,
                taskId = taskId,
                onBack = { navController.popBackStack() },
                onNavigateToEdit = {
                    // Navigate to edit task screen (future)
                }
            )
        }

        // ─── Calendar ────────────────────────────────────────────
        composable(Routes.CALENDAR) {
            val viewModel: CalendarViewModel = hiltViewModel()
            CalendarScreen(
                viewModel = viewModel,
                onNavigateToEventDetail = { eventId ->
                    navController.navigate("event_detail/$eventId")
                }
            )
        }

        // ─── Events ──────────────────────────────────────────────
        composable(Routes.EVENTS) {
            val viewModel: EventViewModel = hiltViewModel()
            EventListScreen(
                viewModel = viewModel,
                onNavigateToEventDetail = { eventId ->
                    navController.navigate("event_detail/$eventId")
                }
            )
        }

        composable(Routes.EVENT_DETAIL) { backStackEntry ->
            val eventId = Routes.eventId(backStackEntry.route ?: "")
            val viewModel: EventViewModel = hiltViewModel()
            EventDetailScreen(
                viewModel = viewModel,
                eventId = eventId,
                onBack = { navController.popBackStack() }
            )
        }

        // ─── Meetings ────────────────────────────────────────────
        composable(Routes.MEETINGS) {
            val viewModel: MeetingViewModel = hiltViewModel()
            MeetingListScreen(
                viewModel = viewModel,
                onNavigateToMeetingDetail = { meetingId ->
                    navController.navigate("meeting_detail/$meetingId")
                }
            )
        }

        composable(Routes.MEETING_DETAIL) { backStackEntry ->
            val meetingId = Routes.meetingId(backStackEntry.route ?: "")
            val viewModel: MeetingViewModel = hiltViewModel()
            MeetingDetailScreen(
                viewModel = viewModel,
                meetingId = meetingId,
                onBack = { navController.popBackStack() }
            )
        }

        // ─── Appointments ────────────────────────────────────────
        composable(Routes.APPOINTMENTS) {
            val viewModel: AppointmentViewModel = hiltViewModel()
            AppointmentListScreen(
                viewModel = viewModel,
                onNavigateToAppointmentForm = { appointmentId ->
                    navController.navigate("appointment_form/$appointmentId")
                },
                onNavigateToNewAppointment = {
                    navController.navigate("appointment_form/null")
                }
            )
        }

        composable(Routes.APPOINTMENT_FORM) { backStackEntry ->
            val appointmentId = Routes.appointmentId(backStackEntry.route ?: "")
            val viewModel: AppointmentViewModel = hiltViewModel()
            AppointmentFormScreen(
                viewModel = viewModel,
                appointmentId = appointmentId,
                onBack = { navController.popBackStack() },
                onSaveSuccess = { navController.popBackStack() }
            )
        }

        // ─── Chat ────────────────────────────────────────────────
        composable(Routes.CHAT) {
            val viewModel: ChatViewModel = hiltViewModel()
            ChatListScreen(
                viewModel = viewModel,
                onNavigateToConversation = { conversationId ->
                    navController.navigate("conversation/$conversationId")
                }
            )
        }

        composable(Routes.CONVERSATION) { backStackEntry ->
            val conversationId = Routes.conversationId(backStackEntry.route ?: "")
            val viewModel: ChatViewModel = hiltViewModel()
            ConversationScreen(
                viewModel = viewModel,
                conversationId = conversationId,
                onBack = { navController.popBackStack() }
            )
        }

        // ─── Notes ───────────────────────────────────────────────
        composable(Routes.NOTES) {
            val viewModel: NotesViewModel = hiltViewModel()
            NotesListScreen(
                viewModel = viewModel,
                onNavigateToNoteEditor = { noteId ->
                    navController.navigate("note_editor/$noteId")
                },
                onNavigateToNewNote = {
                    navController.navigate("note_editor/null")
                }
            )
        }

        composable(Routes.NOTE_EDITOR) { backStackEntry ->
            val noteId = Routes.noteId(backStackEntry.route ?: "")
            val viewModel: NotesViewModel = hiltViewModel()
            NoteEditorScreen(
                viewModel = viewModel,
                noteId = noteId,
                onBack = { navController.popBackStack() },
                onSaveSuccess = { navController.popBackStack() }
            )
        }

        // ─── Analytics ───────────────────────────────────────────
        composable(Routes.ANALYTICS) {
            val viewModel: AnalyticsViewModel = hiltViewModel()
            AnalyticsScreen(
                viewModel = viewModel
            )
        }

        // ─── Settings ────────────────────────────────────────────
        composable(Routes.SETTINGS) {
            val viewModel: SettingsViewModel = hiltViewModel()
            SettingsScreen(
                viewModel = viewModel,
                onNavigateToProfileEdit = {
                    navController.navigate(Routes.PROFILE_EDIT)
                },
                onNavigateToIntegrations = {
                    navController.navigate(Routes.INTEGRATIONS)
                },
                onNavigateToHelp = {
                    // Open help/feedback screen (future)
                },
                onNavigateToAbout = {
                    // Open about screen (future)
                },
                onSignOut = {
                    navController.navigate(Routes.AUTH_GRAPH) {
                        popUpTo(Routes.MAIN_GRAPH) { inclusive = true }
                    }
                }
            )
        }

        composable(Routes.PROFILE_EDIT) {
            val viewModel: SettingsViewModel = hiltViewModel()
            ProfileEditScreen(
                viewModel = viewModel,
                onBack = { navController.popBackStack() }
            )
        }

        composable(Routes.INTEGRATIONS) {
            val viewModel: SettingsViewModel = hiltViewModel()
            IntegrationsScreen(
                viewModel = viewModel,
                onBack = { navController.popBackStack() }
            )
        }
    }
}