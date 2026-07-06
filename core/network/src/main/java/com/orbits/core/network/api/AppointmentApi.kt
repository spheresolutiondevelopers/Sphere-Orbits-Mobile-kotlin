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
data class AppointmentDto(
    val id: String,
    val userId: String,
    val title: String,
    val description: String?,
    val appointmentType: String,
    val startDateTime: String,
    val endDateTime: String,
    val allDayEvent: Boolean,
    val location: String?,
    val isVirtual: Boolean,
    val meetingLink: String?,
    val meetingPlatform: String?,
    val status: String,
    val reminderMinutesBefore: Int,
    val isRecurring: Boolean,
    val recurrencePattern: String?,
    val calendarColor: String?,
    val notes: String?,
    val externalEventId: String?,
    val externalSyncStatus: String,
    val isDeleted: Boolean,
    val createdAt: String,
    val updatedAt: String
)

@kotlinx.serialization.Serializable
data class CreateAppointmentRequest(
    val title: String,
    val description: String? = null,
    val appointmentType: String = "general",
    val startDateTime: String,
    val endDateTime: String,
    val allDayEvent: Boolean = false,
    val location: String? = null,
    val isVirtual: Boolean = false,
    val meetingLink: String? = null,
    val meetingPlatform: String? = null,
    val reminderMinutesBefore: Int = 15,
    val isRecurring: Boolean = false,
    val recurrencePattern: String? = null,
    val calendarColor: String? = null,
    val notes: String? = null,
    val participants: List<ParticipantDto> = emptyList()
)

@kotlinx.serialization.Serializable
data class UpdateAppointmentRequest(
    val title: String? = null,
    val description: String? = null,
    val appointmentType: String? = null,
    val startDateTime: String? = null,
    val endDateTime: String? = null,
    val allDayEvent: Boolean? = null,
    val location: String? = null,
    val isVirtual: Boolean? = null,
    val meetingLink: String? = null,
    val meetingPlatform: String? = null,
    val reminderMinutesBefore: Int? = null,
    val isRecurring: Boolean? = null,
    val recurrencePattern: String? = null,
    val calendarColor: String? = null,
    val notes: String? = null,
    val status: String? = null,
    val participants: List<ParticipantDto>? = null
)

@kotlinx.serialization.Serializable
data class ParticipantDto(
    val id: String? = null,
    val email: String,
    val fullName: String? = null,
    val invitationStatus: String = "pending",
    val participantRole: String = "attendee"
)

interface AppointmentApi {

    @GET("api/v1/appointments")
    suspend fun getAppointments(
        @Query("status") status: String? = null,
        @Query("startDate") startDate: String? = null,
        @Query("endDate") endDate: String? = null,
        @Query("type") type: String? = null,
        @Query("page") page: Int = 1,
        @Query("pageSize") pageSize: Int = 20
    ): PaginatedResponse<AppointmentDto>

    @GET("api/v1/appointments/{appointmentId}")
    suspend fun getAppointment(
        @Path("appointmentId") appointmentId: String
    ): AppointmentDto

    @POST("api/v1/appointments")
    suspend fun createAppointment(
        @Body request: CreateAppointmentRequest
    ): AppointmentDto

    @PUT("api/v1/appointments/{appointmentId}")
    suspend fun updateAppointment(
        @Path("appointmentId") appointmentId: String,
        @Body request: UpdateAppointmentRequest
    ): AppointmentDto

    @DELETE("api/v1/appointments/{appointmentId}")
    suspend fun deleteAppointment(
        @Path("appointmentId") appointmentId: String
    )

    @POST("api/v1/appointments/{appointmentId}/cancel")
    suspend fun cancelAppointment(
        @Path("appointmentId") appointmentId: String
    ): AppointmentDto
}