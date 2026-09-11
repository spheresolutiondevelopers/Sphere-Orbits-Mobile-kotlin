package com.orbits.feature.chat

import com.orbits.domain.chat.Conversation
import com.orbits.domain.chat.Message
import com.orbits.domain.chat.ConversationParticipant

/**
 * UI state for the chat feature.
 */
data class ChatUiState(
    val isLoading: Boolean = true,
    val conversations: List<Conversation> = emptyList(),
    val filteredConversations: List<Conversation> = emptyList(),
    val selectedConversationId: String? = null,
    val selectedFilter: ChatFilter = ChatFilter.ALL,
    val searchQuery: String = "",
    val errorMessage: String? = null,
    val isRefreshing: Boolean = false,
    val unreadCount: Int = 0
)

/**
 * Conversation detail UI state.
 */
data class ConversationUiState(
    val isLoading: Boolean = true,
    val conversationId: String = "",
    val conversation: Conversation? = null,
    val participants: List<ConversationParticipant> = emptyList(),
    val messages: List<Message> = emptyList(),
    val unreadCount: Int = 0,
    val isTyping: Boolean = false,
    val typingUserId: String? = null,
    val errorMessage: String? = null,
    val isSending: Boolean = false
)

/**
 * Chat filter options.
 */
enum class ChatFilter(val displayName: String) {
    ALL("All"),
    UNREAD("Unread"),
    DIRECT("Direct"),
    GROUPS("Groups"),
    ARCHIVED("Archived")
}
