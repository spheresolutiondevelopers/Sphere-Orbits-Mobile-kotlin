package com.orbits.feature.tasks.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.orbits.feature.tasks.TaskListScreen
import com.orbits.feature.tasks.TaskDetailScreen
import com.orbits.feature.tasks.TaskFormScreen

/**
 * Navigation constants for tasks graph.
 */
object TasksRoutes {
    const val TASKS_GRAPH = "tasks_graph"
    const val TASKS_LIST = "tasks_list"
    const val TASK_DETAIL = "task_detail/{taskId}"
    const val TASK_CREATE = "task_create"
    const val TASK_EDIT = "task_edit/{taskId}"

    fun taskDetail(taskId: String): String = "task_detail/$taskId"
    fun taskEdit(taskId: String): String = "task_edit/$taskId"
}

/**
 * Tasks navigation graph builder.
 */
fun NavGraphBuilder.tasksNavGraph(
    navController: NavHostController
) {
    navigation(
        route = TasksRoutes.TASKS_GRAPH,
        startDestination = TasksRoutes.TASKS_LIST
    ) {
        composable(TasksRoutes.TASKS_LIST) {
            TaskListScreen(
                onNavigateToTaskDetail = { taskId ->
                    navController.navigate(TasksRoutes.taskDetail(taskId))
                },
                onNavigateToCreate = {
                    navController.navigate(TasksRoutes.TASK_CREATE)
                }
            )
        }

        composable(TasksRoutes.TASK_DETAIL) { backStackEntry ->
            val taskId = backStackEntry.arguments?.getString("taskId") ?: ""
            TaskDetailScreen(
                taskId = taskId,
                onBack = { navController.popBackStack() },
                onNavigateToEdit = { task ->
                    navController.navigate(TasksRoutes.taskEdit(task.id))
                }
            )
        }

        composable(TasksRoutes.TASK_CREATE) {
            TaskFormScreen(
                onBack = { navController.popBackStack() },
                onSaveSuccess = { navController.popBackStack() }
            )
        }

        composable(TasksRoutes.TASK_EDIT) { backStackEntry ->
            val taskId = backStackEntry.arguments?.getString("taskId") ?: ""
            TaskFormScreen(
                onBack = { navController.popBackStack() },
                onSaveSuccess = { navController.popBackStack() }
            )
        }
    }
}
