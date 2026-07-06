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

import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PUT

@kotlinx.serialization.Serializable
data class SettingsDto(
    val userId: String,
    val theme: String, // "light", "dark", "system"
    val language: String,
    val timezone: String,
    val notificationPreferences: NotificationPreferences,
    val updatedAt: String
)

@kotlinx.serialization.Serializable
data class NotificationPreferences(
    val pushEnabled: Boolean,
    val emailEnabled: Boolean,
    val meetingReminders: Boolean,
    val taskReminders: Boolean,
    val eventReminders: Boolean
)

@kotlinx.serialization.Serializable
data class UpdateSettingsRequest(
    val theme: String? = null,
    val language: String? = null,
    val timezone: String? = null,
    val notificationPreferences: NotificationPreferences? = null
)

interface SettingsApi {

    @GET("api/v1/settings")
    suspend fun getSettings(): SettingsDto

    @PUT("api/v1/settings")
    suspend fun updateSettings(
        @Body request: UpdateSettingsRequest
    ): SettingsDto
}