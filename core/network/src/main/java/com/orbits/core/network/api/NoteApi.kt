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
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

@kotlinx.serialization.Serializable
data class NoteDto(
    val id: String,
    val userId: String,
    val title: String?,
    val content: String,
    val taskId: String?,
    val eventId: String?,
    val appointmentId: String?,
    val meetingId: String?,
    val isDeleted: Boolean,
    val createdAt: String,
    val updatedAt: String
)

@kotlinx.serialization.Serializable
data class CreateNoteRequest(
    val title: String? = null,
    val content: String,
    val taskId: String? = null,
    val eventId: String? = null,
    val appointmentId: String? = null,
    val meetingId: String? = null
)

@kotlinx.serialization.Serializable
data class UpdateNoteRequest(
    val title: String? = null,
    val content: String? = null,
    val taskId: String? = null,
    val eventId: String? = null,
    val appointmentId: String? = null,
    val meetingId: String? = null
)

interface NoteApi {

    @GET("api/v1/notes")
    suspend fun getNotes(
        @Query("taskId") taskId: String? = null,
        @Query("eventId") eventId: String? = null,
        @Query("appointmentId") appointmentId: String? = null,
        @Query("meetingId") meetingId: String? = null,
        @Query("search") search: String? = null,
        @Query("page") page: Int = 1,
        @Query("pageSize") pageSize: Int = 20
    ): PaginatedResponse<NoteDto>

    @GET("api/v1/notes/{noteId}")
    suspend fun getNote(
        @Path("noteId") noteId: String
    ): NoteDto

    @POST("api/v1/notes")
    suspend fun createNote(
        @Body request: CreateNoteRequest
    ): NoteDto

    @PUT("api/v1/notes/{noteId}")
    suspend fun updateNote(
        @Path("noteId") noteId: String,
        @Body request: UpdateNoteRequest
    ): NoteDto

    @DELETE("api/v1/notes/{noteId}")
    suspend fun deleteNote(
        @Path("noteId") noteId: String
    )
}