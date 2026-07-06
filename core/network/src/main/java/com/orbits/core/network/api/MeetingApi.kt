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
data class MeetingDto(
    val id: String,
    val taskId: String,
    val organizerUserId: String,
    val title: String,
    val description: String?,
    val startDateTime: String,
    val endDateTime: String,
    val meetingLink: String?,
    val meetingPlatform: String?,
    val isRecurring: Boolean,
    val recurrencePattern: String?,
    val status: String,
    val isDeleted: Boolean,
    val createdAt: String,
    val updatedAt: String
)

@kotlinx.serialization.Serializable
data class CreateMeetingRequest(
    val taskId: String,
    val title: String,
    val description: String? = null,
    val startDateTime: String,
    val endDateTime: String,
    val meetingLink: String? = null,
    val meetingPlatform: String? = null,
    val isRecurring: Boolean = false,
    val recurrencePattern: String? = null
)

@kotlinx.serialization.Serializable
data class UpdateMeetingRequest(
    val title: String? = null,
    val description: String? = null,
    val startDateTime: String? = null,
    val endDateTime: String? = null,
    val meetingLink: String? = null,
    val meetingPlatform: String? = null,
    val isRecurring: Boolean? = null,
    val recurrencePattern: String? = null,
    val status: String? = null
)

interface MeetingApi {

    @GET("api/v1/meetings")
    suspend fun getMeetings(
        @Query("status") status: String? = null,
        @Query("startDate") startDate: String? = null,
        @Query("endDate") endDate: String? = null,
        @Query("page") page: Int = 1,
        @Query("pageSize") pageSize: Int = 20
    ): PaginatedResponse<MeetingDto>

    @GET("api/v1/meetings/{meetingId}")
    suspend fun getMeeting(
        @Path("meetingId") meetingId: String
    ): MeetingDto

    @POST("api/v1/meetings")
    suspend fun createMeeting(
        @Body request: CreateMeetingRequest
    ): MeetingDto

    @PUT("api/v1/meetings/{meetingId}")
    suspend fun updateMeeting(
        @Path("meetingId") meetingId: String,
        @Body request: UpdateMeetingRequest
    ): MeetingDto

    @DELETE("api/v1/meetings/{meetingId}")
    suspend fun deleteMeeting(
        @Path("meetingId") meetingId: String
    )

    @POST("api/v1/meetings/{meetingId}/join")
    suspend fun joinMeeting(
        @Path("meetingId") meetingId: String
    )
}