/*
 * Copyright © 2026 Sphere Solution Developers
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */

package com.orbits.core.network.di

import com.orbits.core.network.ApiClient
import com.orbits.core.network.AuthInterceptor
import com.orbits.core.network.LoggingInterceptor
import com.orbits.core.network.NetworkMonitor
import com.orbits.core.network.api.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideAuthInterceptor(
        tokenProvider: com.orbits.core.common.TokenProvider
    ): AuthInterceptor {
        return AuthInterceptor(tokenProvider)
    }

    @Provides
    @Singleton
    fun provideLoggingInterceptor(): LoggingInterceptor {
        return LoggingInterceptor()
    }

    @Provides
    @Singleton
    fun provideNetworkMonitor(
        @dagger.hilt.android.qualifiers.ApplicationContext context: android.content.Context
    ): NetworkMonitor {
        return NetworkMonitor(context)
    }

    @Provides
    @Singleton
    fun provideApiClient(
        @dagger.hilt.android.qualifiers.ApplicationContext context: android.content.Context,
        authInterceptor: AuthInterceptor,
        loggingInterceptor: LoggingInterceptor,
        networkMonitor: NetworkMonitor
    ): ApiClient {
        return ApiClient(context, authInterceptor, loggingInterceptor, networkMonitor)
    }

    @Provides
    @Singleton
    fun provideJson(): kotlinx.serialization.json.Json {
        return kotlinx.serialization.json.Json {
            ignoreUnknownKeys = true
            coerceInputValues = true
            encodeDefaults = true
            prettyPrint = true
        }
    }

    // ─── API Services ────────────────────────────────────────────
    @Provides
    @Singleton
    fun provideAuthApi(apiClient: ApiClient): AuthApi {
        return apiClient.create()
    }

    @Provides
    @Singleton
    fun provideTaskApi(apiClient: ApiClient): TaskApi {
        return apiClient.create()
    }

    @Provides
    @Singleton
    fun provideCalendarApi(apiClient: ApiClient): CalendarApi {
        return apiClient.create()
    }

    @Provides
    @Singleton
    fun provideMeetingApi(apiClient: ApiClient): MeetingApi {
        return apiClient.create()
    }

    @Provides
    @Singleton
    fun provideAppointmentApi(apiClient: ApiClient): AppointmentApi {
        return apiClient.create()
    }

    @Provides
    @Singleton
    fun provideNoteApi(apiClient: ApiClient): NoteApi {
        return apiClient.create()
    }

    @Provides
    @Singleton
    fun provideChatApi(apiClient: ApiClient): ChatApi {
        return apiClient.create()
    }

    @Provides
    @Singleton
    fun provideAnalyticsApi(apiClient: ApiClient): AnalyticsApi {
        return apiClient.create()
    }

    @Provides
    @Singleton
    fun provideSettingsApi(apiClient: ApiClient): SettingsApi {
        return apiClient.create()
    }

    @Provides
    @Singleton
    fun provideSyncApi(apiClient: ApiClient): SyncApi {
        return apiClient.create()
    }
}