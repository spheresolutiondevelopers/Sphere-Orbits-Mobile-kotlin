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
import kotlinx.serialization.Serializable
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

@Serializable
data class EventDto(
    val id: String,
    val userId: String,
    val categoryId: String? = null,
    val taskId: String? = null,
    val name: String,
    val description: String? = null,
    val format: String? = null,
    val planningNotes: String? = null,
    val startDateTime: String? = null,
    val endDateTime: String? = null,
    val location: String? = null,
    val status: String = "planned",
    val isRecurring: Boolean = false,
    val recurrencePattern: String? = null,
    val budget: Double? = null,
    val currency: String = "USD",
    val maxAttendees: Int? = null,
    val isPublic: Boolean = false,
    val imageUrl: String? = null,
    val coverPhotoUrl: String? = null,
    val isDeleted: Boolean = false,
    val createdAt: String,
    val updatedAt: String,
    val participantCount: Int = 0,
    val confirmedCount: Int = 0
)

@Serializable
data class CreateEventRequest(
    val name: String,
    val categoryId: String? = null,
    val description: String? = null,
    val format: String? = null,
    val planningNotes: String? = null,
    val startDateTime: String? = null,
    val endDateTime: String? = null,
    val location: String? = null,
    val status: String = "planned",
    val isRecurring: Boolean = false,
    val recurrencePattern: String? = null,
    val budget: Double? = null,
    val currency: String = "USD",
    val maxAttendees: Int? = null,
    val isPublic: Boolean = false,
    val imageUrl: String? = null,
    val coverPhotoUrl: String? = null,
    val participants: List<EventParticipantDto> = emptyList()
)

@Serializable
data class UpdateEventRequest(
    val name: String? = null,
    val categoryId: String? = null,
    val description: String? = null,
    val format: String? = null,
    val planningNotes: String? = null,
    val startDateTime: String? = null,
    val endDateTime: String? = null,
    val location: String? = null,
    val status: String? = null,
    val isRecurring: Boolean? = null,
    val recurrencePattern: String? = null,
    val budget: Double? = null,
    val currency: String? = null,
    val maxAttendees: Int? = null,
    val isPublic: Boolean? = null,
    val imageUrl: String? = null,
    val coverPhotoUrl: String? = null,
    val participants: List<EventParticipantDto>? = null
)

@Serializable
data class EventParticipantDto(
    val id: String? = null,
    val eventId: String? = null,
    val userId: String? = null,
    val email: String,
    val fullName: String? = null,
    val invitationStatus: String = "pending",
    val participantRole: String = "attendee",
    val plusOnes: Int = 0,
    val dietaryRestrictions: String? = null,
    val specialRequests: String? = null,
    val checkedIn: Boolean = false,
    val checkInTime: String? = null,
    val createdAt: String? = null,
    val updatedAt: String? = null
)

interface EventApi {

    @GET("api/v1/events")
    suspend fun getEvents(
        @Query("status") status: String? = null,
        @Query("categoryId") categoryId: String? = null,
        @Query("startDate") startDate: String? = null,
        @Query("endDate") endDate: String? = null,
        @Query("page") page: Int = 1,
        @Query("pageSize") pageSize: Int = 20
    ): PaginatedResponse<EventDto>

    @GET("api/v1/events/{eventId}")
    suspend fun getEvent(
        @Path("eventId") eventId: String
    ): EventDto

    @POST("api/v1/events")
    suspend fun createEvent(
        @Body request: CreateEventRequest
    ): EventDto

    @PUT("api/v1/events/{eventId}")
    suspend fun updateEvent(
        @Path("eventId") eventId: String,
        @Body request: UpdateEventRequest
    ): EventDto

    @DELETE("api/v1/events/{eventId}")
    suspend fun deleteEvent(
        @Path("eventId") eventId: String
    )
}