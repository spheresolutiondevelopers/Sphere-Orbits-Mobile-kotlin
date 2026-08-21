package com.orbits.data.chat.sync

import com.orbits.data.chat.local.MessageEntity
import com.orbits.data.chat.local.ConversationEntity
import javax.inject.Inject

internal class ChatSyncConflictResolver @Inject constructor() {

    // ─── Message Resolution ───────────────────────────────────────

    fun resolveMessageConflict(
        local: MessageEntity,
        server: MessageEntity
    ): ResolutionMessage {
        val localTime = local.sentAt
        val serverTime = server.sentAt

        return when {
            serverTime > localTime -> ResolutionMessage(ResolutionStrategy.SERVER_WINS, server)
            localTime > serverTime -> ResolutionMessage(ResolutionStrategy.LOCAL_WINS, local)
            else -> ResolutionMessage(ResolutionStrategy.SERVER_WINS, server)
        }
    }

    fun mergeMessageFields(
        local: MessageEntity,
        server: MessageEntity
    ): MessageEntity {
        return if (local.updatedAt > server.updatedAt) {
            // Local is newer: keep local fields
            local.copy(
                // Server might have delivery/read status updates
                isRead = server.isRead || local.isRead,
                readAt = if (server.isRead) server.readAt else local.readAt,
                isDelivered = server.isDelivered || local.isDelivered,
                deliveredAt = if (server.isDelivered) server.deliveredAt else local.deliveredAt,
                updatedAt = nowUtc()
            )
        } else {
            // Server is newer: server wins
            server.copy(
                updatedAt = nowUtc()
            )
        }
    }

    // ─── Conversation Resolution ──────────────────────────────────

    fun resolveConversationConflict(
        local: ConversationEntity,
        server: ConversationEntity
    ): ResolutionConversation {
        val localTime = local.updatedAt
        val serverTime = server.updatedAt

        return when {
            serverTime > localTime -> ResolutionConversation(ResolutionStrategy.SERVER_WINS, server)
            localTime > serverTime -> ResolutionConversation(ResolutionStrategy.LOCAL_WINS, local)
            else -> ResolutionConversation(ResolutionStrategy.SERVER_WINS, server)
        }
    }

    fun mergeConversationFields(
        local: ConversationEntity,
        server: ConversationEntity
    ): ConversationEntity {
        return if (local.updatedAt > server.updatedAt) {
            // Local is newer: keep local fields
            local.copy(
                // Server might have updated last message info
                lastMessageId = server.lastMessageId ?: local.lastMessageId,
                lastMessageContent = server.lastMessageContent ?: local.lastMessageContent,
                lastMessageSentAt = server.lastMessageSentAt ?: local.lastMessageSentAt,
                updatedAt = nowUtc()
            )
        } else {
            // Server is newer: server wins
            server.copy(
                updatedAt = nowUtc()
            )
        }
    }

    private fun nowUtc(): String {
        return java.time.Instant.now().toString()
    }
}

enum class ResolutionStrategy {
    LOCAL_WINS,
    SERVER_WINS
}

data class ResolutionMessage(
    val strategy: ResolutionStrategy,
    val entity: MessageEntity
)

data class ResolutionConversation(
    val strategy: ResolutionStrategy,
    val entity: ConversationEntity
)