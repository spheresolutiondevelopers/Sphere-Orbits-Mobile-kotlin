package com.orbits.feature.chat

import com.orbits.domain.chat.Message
import com.orbits.domain.chat.Conversation

/**
 * UI events for the chat feature.
 */
sealed class ChatEvent {
    // ─── List Events ─────────────────────────────────────────────

    data object LoadConversations : ChatEvent()
    data object Refresh : ChatEvent()
    data class SelectFilter(val filter: ChatFilter) : ChatEvent()
    data class Search(val query: String) : ChatEvent()
    data class SelectConversation(val conversationId: String) : ChatEvent()
    data class NavigateToConversation(val conversationId: String) : ChatEvent()
    data object NavigateToCreateConversation : ChatEvent()
    data object DismissError : ChatEvent()

    // ─── Conversation Events ─────────────────────────────────────

    data class LoadMessages(val conversationId: String) : ChatEvent()
    data class SendMessage(val conversationId: String, val content: String) : ChatEvent()
    data class SendMessageWithType(val conversationId: String, val content: String, val type: String) : ChatEvent()
    data class MarkConversationRead(val conversationId: String) : ChatEvent()
    data class MarkMessageRead(val messageId: String) : ChatEvent()
    data class DeleteMessage(val messageId: String) : ChatEvent()
    data class TypingIndicator(val conversationId: String, val isTyping: Boolean) : ChatEvent()

    // ─── Conversation Management ─────────────────────────────────

    data class ArchiveConversation(val conversationId: String) : ChatEvent()
    data class UnarchiveConversation(val conversationId: String) : ChatEvent()
    data class MuteConversation(val conversationId: String, val until: String? = null) : ChatEvent()
    data class UnmuteConversation(val conversationId: String) : ChatEvent()
    data class CreateConversation(val participantIds: List<String>, val name: String? = null) : ChatEvent()
    data class AddParticipants(val conversationId: String, val userIds: List<String>) : ChatEvent()
    data class RemoveParticipant(val conversationId: String, val userId: String) : ChatEvent()

    // ─── Sync Events ─────────────────────────────────────────────

    data object SyncChat : ChatEvent()
}
