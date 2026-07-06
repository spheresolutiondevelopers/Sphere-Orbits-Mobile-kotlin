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
data class CalendarEventDto(
    val id: String,
    val userId: String,
    val title: String,
    val description: String?,
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
data class CreateCalendarEventRequest(
    val title: String,
    val description: String? = null,
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
    val notes: String? = null
)

@kotlinx.serialization.Serializable
data class UpdateCalendarEventRequest(
    val title: String? = null,
    val description: String? = null,
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
    val status: String? = null
)

interface CalendarApi {

    @GET("api/v1/calendar/events")
    suspend fun getCalendarEvents(
        @Query("startDate") startDate: String,
        @Query("endDate") endDate: String,
        @Query("status") status: String? = null,
        @Query("page") page: Int = 1,
        @Query("pageSize") pageSize: Int = 50
    ): PaginatedResponse<CalendarEventDto>

    @GET("api/v1/calendar/events/{eventId}")
    suspend fun getCalendarEvent(
        @Path("eventId") eventId: String
    ): CalendarEventDto

    @POST("api/v1/calendar/events")
    suspend fun createCalendarEvent(
        @Body request: CreateCalendarEventRequest
    ): CalendarEventDto

    @PUT("api/v1/calendar/events/{eventId}")
    suspend fun updateCalendarEvent(
        @Path("eventId") eventId: String,
        @Body request: UpdateCalendarEventRequest
    ): CalendarEventDto

    @DELETE("api/v1/calendar/events/{eventId}")
    suspend fun deleteCalendarEvent(
        @Path("eventId") eventId: String
    )

    @POST("api/v1/calendar/sync/google")
    suspend fun syncGoogleCalendar()

    @POST("api/v1/calendar/sync/outlook")
    suspend fun syncOutlookCalendar()
}