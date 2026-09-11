package com.orbits.feature.chat.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.orbits.feature.chat.ChatListScreen
import com.orbits.feature.chat.ConversationScreen

/**
 * Navigation constants for chat graph.
 */
object ChatRoutes {
    const val CHAT_GRAPH = "chat_graph"
    const val CHAT_LIST = "chat_list"
    const val CONVERSATION = "conversation/{conversationId}"
    const val NEW_CHAT = "new_chat"

    fun conversation(conversationId: String): String = "conversation/$conversationId"
}

/**
 * Chat navigation graph builder.
 */
fun NavGraphBuilder.chatNavGraph(
    navController: NavHostController
) {
    navigation(
        route = ChatRoutes.CHAT_GRAPH,
        startDestination = ChatRoutes.CHAT_LIST
    ) {
        composable(ChatRoutes.CHAT_LIST) {
            ChatListScreen(
                onNavigateToConversation = { conversationId ->
                    navController.navigate(ChatRoutes.conversation(conversationId))
                },
                onNavigateToNewChat = {
                    navController.navigate(ChatRoutes.NEW_CHAT)
                }
            )
        }

        composable(ChatRoutes.CONVERSATION) { backStackEntry ->
            val conversationId = backStackEntry.arguments?.getString("conversationId") ?: ""
            ConversationScreen(
                conversationId = conversationId,
                onBack = { navController.popBackStack() }
            )
        }

        // New chat screen (to be implemented)
    }
}
