/*
 * Copyright © 2026 Sphere Solution Developers
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */

package com.orbits.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.orbits.data.chat.MessageEntity
import com.orbits.data.chat.ConversationEntity
import com.orbits.data.chat.ConversationParticipantEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MessageDao {

    // ─── Conversations ───────────────────────────────────────────

    @Query("SELECT * FROM conversations WHERE id = :conversationId")
    suspend fun getConversation(conversationId: String): ConversationEntity?

    @Query("""
        SELECT c.* FROM conversations c
        JOIN conversation_participants cp ON c.id = cp.conversation_id
        WHERE cp.user_id = :userId
        ORDER BY c.updated_at DESC
    """)
    fun getConversationsForUser(userId: String): Flow<List<ConversationEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertConversation(conversation: ConversationEntity)

    @Update
    suspend fun updateConversation(conversation: ConversationEntity)

    @Query("UPDATE conversations SET updated_at = datetime('now') WHERE id = :conversationId")
    suspend fun touchConversation(conversationId: String)

    // ─── Conversation Participants ──────────────────────────────

    @Query("""
        SELECT * FROM conversation_participants 
        WHERE conversation_id = :conversationId
    """)
    suspend fun getConversationParticipants(conversationId: String): List<ConversationParticipantEntity>

    @Query("""
        SELECT * FROM conversation_participants 
        WHERE conversation_id = :conversationId AND user_id = :userId
    """)
    suspend fun getConversationParticipant(conversationId: String, userId: String): ConversationParticipantEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertConversationParticipant(participant: ConversationParticipantEntity)

    @Query("DELETE FROM conversation_participants WHERE conversation_id = :conversationId AND user_id = :userId")
    suspend fun removeConversationParticipant(conversationId: String, userId: String)

    // ─── Messages ────────────────────────────────────────────────

    @Query("""
        SELECT * FROM messages 
        WHERE conversation_id = :conversationId
        ORDER BY sent_at DESC LIMIT :limit
    """)
    suspend fun getMessagesForConversation(conversationId: String, limit: Int): List<MessageEntity>

    @Query("""
        SELECT * FROM messages 
        WHERE conversation_id = :conversationId
        ORDER BY sent_at ASC
    """)
    fun getMessagesForConversationFlow(conversationId: String): Flow<List<MessageEntity>>

    @Query("""
        SELECT COUNT(*) FROM messages 
        WHERE conversation_id = :conversationId 
          AND is_read = 0 
          AND sender_user_id != :userId
    """)
    fun getUnreadCountForConversation(conversationId: String, userId: String): Flow<Int>

    @Query("""
        SELECT COUNT(*) FROM messages 
        WHERE conversation_id IN (
            SELECT conversation_id FROM conversation_participants WHERE user_id = :userId
        )
        AND is_read = 0 
        AND sender_user_id != :userId
    """)
    fun getTotalUnreadCountForUser(userId: String): Flow<Int>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessage(message: MessageEntity)

    @Query("""
        UPDATE messages 
        SET is_read = 1, read_at = datetime('now') 
        WHERE conversation_id = :conversationId 
          AND sender_user_id != :userId
          AND is_read = 0
    """)
    suspend fun markConversationRead(conversationId: String, userId: String)

    @Query("""
        UPDATE messages 
        SET is_read = 1, read_at = datetime('now') 
        WHERE id IN (:messageIds)
    """)
    suspend fun markMessagesRead(messageIds: List<String>)

    @Query("DELETE FROM messages WHERE conversation_id = :conversationId")
    suspend fun deleteAllMessagesForConversation(conversationId: String)

    @Query("DELETE FROM messages WHERE id = :messageId")
    suspend fun deleteMessage(messageId: String)
}