/*
 * Copyright © 2026 Sphere Solution Developers
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */

package com.orbits.core.network.api

import com.orbits.core.model.PaginatedResponse
import com.orbits.core.model.UserDto
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

@kotlinx.serialization.Serializable
data class ConversationDto(
    val id: String,
    val type: String,
    val name: String?,
    val participants: List<UserDto>,
    val lastMessage: MessageDto?,
    val unreadCount: Int,
    val createdAt: String,
    val updatedAt: String
)

@kotlinx.serialization.Serializable
data class MessageDto(
    val id: String,
    val conversationId: String,
    val senderUserId: String,
    val sender: UserDto,
    val content: String,
    val sentAt: String,
    val isRead: Boolean,
    val readAt: String?
)

@kotlinx.serialization.Serializable
data class SendMessageRequest(
    val content: String
)

@kotlinx.serialization.Serializable
data class CreateConversationRequest(
    val participantIds: List<String>,
    val name: String? = null,
    val type: String = "direct" // "direct" or "group"
)

interface ChatApi {

    @GET("api/v1/chat/conversations")
    suspend fun getConversations(
        @Query("page") page: Int = 1,
        @Query("pageSize") pageSize: Int = 20
    ): PaginatedResponse<ConversationDto>

    @POST("api/v1/chat/conversations")
    suspend fun createConversation(
        @Body request: CreateConversationRequest
    ): ConversationDto

    @GET("api/v1/chat/conversations/{conversationId}/messages")
    suspend fun getMessages(
        @Path("conversationId") conversationId: String,
        @Query("page") page: Int = 1,
        @Query("pageSize") pageSize: Int = 50
    ): PaginatedResponse<MessageDto>

    @POST("api/v1/chat/conversations/{conversationId}/messages")
    suspend fun sendMessage(
        @Path("conversationId") conversationId: String,
        @Body request: SendMessageRequest
    ): MessageDto

    @POST("api/v1/chat/conversations/{conversationId}/read")
    suspend fun markConversationRead(
        @Path("conversationId") conversationId: String
    )
}