package com.orbits.feature.notes.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.orbits.feature.notes.NotesListScreen
import com.orbits.feature.notes.NoteEditorScreen

/**
 * Navigation constants for notes graph.
 */
object NotesRoutes {
    const val NOTES_GRAPH = "notes_graph"
    const val NOTES_LIST = "notes_list"
    const val NOTE_EDITOR = "note_editor/{noteId}"
    const val NOTE_CREATE = "note_create"

    fun noteEditor(noteId: String): String = "note_editor/$noteId"
}

/**
 * Notes navigation graph builder.
 */
fun NavGraphBuilder.notesNavGraph(
    navController: NavHostController
) {
    navigation(
        route = NotesRoutes.NOTES_GRAPH,
        startDestination = NotesRoutes.NOTES_LIST
    ) {
        composable(NotesRoutes.NOTES_LIST) {
            NotesListScreen(
                onNavigateToNoteEditor = { noteId ->
                    if (noteId != null) {
                        navController.navigate(NotesRoutes.noteEditor(noteId))
                    } else {
                        navController.navigate(NotesRoutes.NOTE_CREATE)
                    }
                },
                onNavigateToNewNote = {
                    navController.navigate(NotesRoutes.NOTE_CREATE)
                }
            )
        }

        composable(NotesRoutes.NOTE_EDITOR) { backStackEntry ->
            val noteId = backStackEntry.arguments?.getString("noteId") ?: ""
            NoteEditorScreen(
                noteId = noteId,
                onBack = { navController.popBackStack() },
                onSaveSuccess = { navController.popBackStack() }
            )
        }

        composable(NotesRoutes.NOTE_CREATE) {
            NoteEditorScreen(
                noteId = null,
                onBack = { navController.popBackStack() },
                onSaveSuccess = { navController.popBackStack() }
            )
        }
    }
}
