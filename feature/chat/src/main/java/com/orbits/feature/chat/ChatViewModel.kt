package com.orbits.feature.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.orbits.core.common.Result
import com.orbits.core.common.Logger
import com.orbits.core.common.extensions.nowUtc
import com.orbits.domain.auth.AuthRepository
import com.orbits.domain.chat.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val chatRepository: ChatRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _state = MutableStateFlow(ChatUiState())
    val state: StateFlow<ChatUiState> = _state.asStateFlow()

    private val _conversationState = MutableStateFlow(ConversationUiState())
    val conversationState: StateFlow<ConversationUiState> = _conversationState.asStateFlow()

    private var allConversations: List<Conversation> = emptyList()
    private var currentFilter = ChatFilter.ALL

    init {
        loadConversations()
        loadUnreadCount()
    }

    fun handleEvent(event: ChatEvent) {
        when (event) {
            ChatEvent.LoadConversations -> loadConversations()
            ChatEvent.Refresh -> refresh()
            is ChatEvent.SelectFilter -> applyFilter(event.filter)
            is ChatEvent.Search -> search(event.query)
            is ChatEvent.SelectConversation -> selectConversation(event.conversationId)
            is ChatEvent.NavigateToConversation -> { /* Navigation handled by NavGraph */ }
            ChatEvent.NavigateToCreateConversation -> { /* Navigation handled by NavGraph */ }
            ChatEvent.DismissError -> dismissError()

            is ChatEvent.LoadMessages -> loadMessages(event.conversationId)
            is ChatEvent.SendMessage -> sendMessage(event.conversationId, event.content)
            is ChatEvent.SendMessageWithType -> sendMessageWithType(event.conversationId, event.content, event.type)
            is ChatEvent.MarkConversationRead -> markConversationRead(event.conversationId)
            is ChatEvent.MarkMessageRead -> markMessageRead(event.messageId)
            is ChatEvent.DeleteMessage -> deleteMessage(event.messageId)
            is ChatEvent.TypingIndicator -> sendTypingIndicator(event.conversationId, event.isTyping)

            is ChatEvent.ArchiveConversation -> archiveConversation(event.conversationId)
            is ChatEvent.UnarchiveConversation -> unarchiveConversation(event.conversationId)
            is ChatEvent.MuteConversation -> muteConversation(event.conversationId, event.until)
            is ChatEvent.UnmuteConversation -> unmuteConversation(event.conversationId)
            is ChatEvent.CreateConversation -> createConversation(event.participantIds, event.name)
            is ChatEvent.AddParticipants -> addParticipants(event.conversationId, event.userIds)
            is ChatEvent.RemoveParticipant -> removeParticipant(event.conversationId, event.userId)

            ChatEvent.SyncChat -> syncChat()
        }
    }

    // ─── Load Operations ─────────────────────────────────────────

    private fun loadConversations() {
        _state.update { it.copy(isLoading = true, errorMessage = null) }

        viewModelScope.launch {
            try {
                chatRepository.getConversations().collect { conversations ->
                    allConversations = conversations
                    applyFilterToState(currentFilter)
                    _state.update { it.copy(isLoading = false) }
                }
            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = e.message ?: "Failed to load conversations"
                    )
                }
            }
        }
    }

    private fun loadUnreadCount() {
        viewModelScope.launch {
            try {
                chatRepository.getUnreadCount().collect { count ->
                    _state.update { it.copy(unreadCount = count) }
                }
            } catch (e: Exception) {
                Logger.e("Chat", "Failed to load unread count", e)
            }
        }
    }

    private fun refresh() {
        _state.update { it.copy(isRefreshing = true) }
        loadConversations()
        loadUnreadCount()
        _state.update { it.copy(isRefreshing = false) }
    }

    // ─── Filter & Search ─────────────────────────────────────────

    private fun applyFilter(filter: ChatFilter) {
        currentFilter = filter
        _state.update { it.copy(selectedFilter = filter) }
        applyFilterToState(filter)
    }

    private fun applyFilterToState(filter: ChatFilter) {
        val filtered = when (filter) {
            ChatFilter.ALL -> allConversations
            ChatFilter.UNREAD -> allConversations.filter { conv ->
                // Check if conversation has unread messages
                // This would require looking at unread counts per conversation
                false // Simplified
            }
            ChatFilter.DIRECT -> allConversations.filter { it.type == "direct" }
            ChatFilter.GROUPS -> allConversations.filter { it.type == "group" }
            ChatFilter.ARCHIVED -> allConversations.filter { it.isArchived }
        }

        // Apply search if there's a query
        val query = _state.value.searchQuery
        val finalFiltered = if (query.isNotBlank()) {
            filtered.filter { conv ->
                conv.name?.contains(query, ignoreCase = true) ?: false
            }
        } else {
            filtered
        }

        _state.update { it.copy(filteredConversations = finalFiltered) }
    }

    private fun search(query: String) {
        _state.update { it.copy(searchQuery = query) }
        applyFilterToState(currentFilter)
    }

    private fun selectConversation(conversationId: String) {
        _state.update { it.copy(selectedConversationId = conversationId) }
    }

    private fun dismissError() {
        _state.update { it.copy(errorMessage = null) }
        _conversationState.update { it.copy(errorMessage = null) }
    }

    // ─── Conversation Events ─────────────────────────────────────

    private fun loadMessages(conversationId: String) {
        _conversationState.update {
            it.copy(
                conversationId = conversationId,
                isLoading = true,
                errorMessage = null
            )
        }

        viewModelScope.launch {
            try {
                // Load conversation details
                val conversationResult = chatRepository.getConversation(conversationId)
                when (conversationResult) {
                    is Result.Success -> {
                        _conversationState.update {
                            it.copy(conversation = conversationResult.data)
                        }
                    }
                    is Result.Error -> {
                        _conversationState.update {
                            it.copy(
                                isLoading = false,
                                errorMessage = conversationResult.exception.message
                            )
                        }
                        return@launch
                    }
                    Result.Loading -> { /* Ignore */ }
                }

                // Load participants
                val participantsResult = chatRepository.getParticipants(conversationId)
                when (participantsResult) {
                    is Result.Success -> {
                        _conversationState.update {
                            it.copy(participants = participantsResult.data)
                        }
                    }
                    is Result.Error -> {
                        Logger.e("Chat", "Failed to load participants", participantsResult.exception)
                    }
                    Result.Loading -> { /* Ignore */ }
                }

                // Load messages
                chatRepository.getMessages(conversationId).collect { messages ->
                    _conversationState.update {
                        it.copy(
                            messages = messages,
                            isLoading = false
                        )
                    }
                    // Mark conversation read when messages are loaded
                    markConversationRead(conversationId)
                }
            } catch (e: Exception) {
                _conversationState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = e.message ?: "Failed to load messages"
                    )
                }
            }
        }
    }

    private fun sendMessage(conversationId: String, content: String) {
        if (content.isBlank()) return

        _conversationState.update { it.copy(isSending = true, errorMessage = null) }

        viewModelScope.launch {
            val currentUser = authRepository.getCurrentUser()
            val userId = currentUser?.id ?: "unknown"

            val message = Message(
                id = UUID.randomUUID().toString(),
                conversationId = conversationId,
                senderUserId = userId,
                content = content,
                contentType = "text",
                sentAt = nowUtc(),
                isRead = false,
                readAt = null,
                isDelivered = false,
                deliveredAt = null,
                isDeleted = false,
                createdAt = nowUtc(),
                updatedAt = nowUtc()
            )

            val result = chatRepository.sendMessage(message)
            _conversationState.update { it.copy(isSending = false) }

            when (result) {
                is Result.Success -> {
                    // Message sent successfully
                    // Update will come from the messages flow
                }
                is Result.Error -> {
                    _conversationState.update {
                        it.copy(
                            errorMessage = result.exception.message ?: "Failed to send message"
                        )
                    }
                }
                Result.Loading -> { /* Ignore */ }
            }
        }
    }

    private fun sendMessageWithType(conversationId: String, content: String, type: String) {
        // Similar to sendMessage but with a content type
        sendMessage(conversationId, content)
    }

    private fun markConversationRead(conversationId: String) {
        viewModelScope.launch {
            try {
                chatRepository.markConversationRead(conversationId)
                // Update unread count
                loadUnreadCount()
                // Update conversation state
                _conversationState.update {
                    it.copy(unreadCount = 0)
                }
            } catch (e: Exception) {
                Logger.e("Chat", "Failed to mark conversation read", e)
            }
        }
    }

    private fun markMessageRead(messageId: String) {
        viewModelScope.launch {
            try {
                chatRepository.markMessageRead(messageId)
                loadUnreadCount()
            } catch (e: Exception) {
                Logger.e("Chat", "Failed to mark message read", e)
            }
        }
    }

    private fun deleteMessage(messageId: String) {
        viewModelScope.launch {
            val result = chatRepository.deleteMessage(messageId)
            if (result is Result.Error) {
                _conversationState.update {
                    it.copy(
                        errorMessage = result.exception.message ?: "Failed to delete message"
                    )
                }
            }
        }
    }

    private fun sendTypingIndicator(conversationId: String, isTyping: Boolean) {
        viewModelScope.launch {
            try {
                chatRepository.sendTypingIndicator(conversationId, isTyping)
            } catch (e: Exception) {
                // Ignore typing indicator errors
            }
        }
    }

    // ─── Conversation Management ─────────────────────────────────

    private fun archiveConversation(conversationId: String) {
        viewModelScope.launch {
            val result = chatRepository.archiveConversation(conversationId)
            if (result is Result.Error) {
                _state.update {
                    it.copy(
                        errorMessage = result.exception.message ?: "Failed to archive conversation"
                    )
                }
            } else {
                loadConversations()
            }
        }
    }

    private fun unarchiveConversation(conversationId: String) {
        // Unarchive is not directly supported in the repository
        // We need to update the conversation
        viewModelScope.launch {
            val convResult = chatRepository.getConversation(conversationId)
            when (convResult) {
                is Result.Success -> {
                    val updated = convResult.data.copy(
                        isArchived = false,
                        updatedAt = nowUtc()
                    )
                    chatRepository.updateConversation(updated)
                    loadConversations()
                }
                is Result.Error -> {
                    _state.update {
                        it.copy(
                            errorMessage = convResult.exception.message ?: "Failed to unarchive conversation"
                        )
                    }
                }
                Result.Loading -> { /* Ignore */ }
            }
        }
    }

    private fun muteConversation(conversationId: String, until: String?) {
        viewModelScope.launch {
            val result = chatRepository.muteConversation(conversationId, until)
            if (result is Result.Error) {
                _state.update {
                    it.copy(
                        errorMessage = result.exception.message ?: "Failed to mute conversation"
                    )
                }
            } else {
                loadConversations()
            }
        }
    }

    private fun unmuteConversation(conversationId: String) {
        viewModelScope.launch {
            val result = chatRepository.unmuteConversation(conversationId)
            if (result is Result.Error) {
                _state.update {
                    it.copy(
                        errorMessage = result.exception.message ?: "Failed to unmute conversation"
                    )
                }
            } else {
                loadConversations()
            }
        }
    }

    private fun createConversation(participantIds: List<String>, name: String?) {
        viewModelScope.launch {
            val result = chatRepository.createConversation(
                participantIds = participantIds,
                name = name,
                type = if (participantIds.size > 1) "group" else "direct"
            )
            when (result) {
                is Result.Success -> {
                    loadConversations()
                    // Navigate to the new conversation
                }
                is Result.Error -> {
                    _state.update {
                        it.copy(
                            errorMessage = result.exception.message ?: "Failed to create conversation"
                        )
                    }
                }
                Result.Loading -> { /* Ignore */ }
            }
        }
    }

    private fun addParticipants(conversationId: String, userIds: List<String>) {
        viewModelScope.launch {
            val result = chatRepository.addParticipants(conversationId, userIds)
            when (result) {
                is Result.Success -> {
                    loadConversations()
                    // Reload participants in conversation
                    loadMessages(conversationId)
                }
                is Result.Error -> {
                    _conversationState.update {
                        it.copy(
                            errorMessage = result.exception.message ?: "Failed to add participants"
                        )
                    }
                }
                Result.Loading -> { /* Ignore */ }
            }
        }
    }

    private fun removeParticipant(conversationId: String, userId: String) {
        viewModelScope.launch {
            val result = chatRepository.removeParticipant(conversationId, userId)
            when (result) {
                is Result.Success -> {
                    loadConversations()
                    loadMessages(conversationId)
                }
                is Result.Error -> {
                    _conversationState.update {
                        it.copy(
                            errorMessage = result.exception.message ?: "Failed to remove participant"
                        )
                    }
                }
                Result.Loading -> { /* Ignore */ }
            }
        }
    }

    // ─── Sync ─────────────────────────────────────────────────────

    private fun syncChat() {
        viewModelScope.launch {
            val result = chatRepository.syncWithServer()
            when (result) {
                is Result.Success -> {
                    loadConversations()
                }
                is Result.Error -> {
                    _state.update {
                        it.copy(
                            errorMessage = result.exception.message ?: "Failed to sync chat"
                        )
                    }
                }
                Result.Loading -> { /* Ignore */ }
            }
        }
    }

    // ─── Helper ──────────────────────────────────────────────────

    fun getCurrentUserId(): String {
        return kotlinx.coroutines.runBlocking { authRepository.getCurrentUser()?.id ?: "unknown" }
    }
}
