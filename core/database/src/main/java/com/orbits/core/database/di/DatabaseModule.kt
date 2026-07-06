/*
 * Copyright © 2026 Sphere Solution Developers
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */

package com.orbits.core.database.di

import android.content.Context
import androidx.room.Room
import com.orbits.core.database.OrbitsDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(
        @ApplicationContext context: Context
    ): OrbitsDatabase {
        return OrbitsDatabase.getInstance(context)
    }

    // ─── DAOs ─────────────────────────────────────────────────────

    @Provides
    @Singleton
    fun provideTaskDao(database: OrbitsDatabase) = database.taskDao()

    @Provides
    @Singleton
    fun provideCalendarEventDao(database: OrbitsDatabase) = database.calendarEventDao()

    @Provides
    @Singleton
    fun provideMeetingDao(database: OrbitsDatabase) = database.meetingDao()

    @Provides
    @Singleton
    fun provideAppointmentDao(database: OrbitsDatabase) = database.appointmentDao()

    @Provides
    @Singleton
    fun provideNoteDao(database: OrbitsDatabase) = database.noteDao()

    @Provides
    @Singleton
    fun provideUserDao(database: OrbitsDatabase) = database.userDao()

    @Provides
    @Singleton
    fun provideMessageDao(database: OrbitsDatabase) = database.messageDao()

    @Provides
    @Singleton
    fun provideAnalyticsDao(database: OrbitsDatabase) = database.analyticsDao()

    @Provides
    @Singleton
    fun provideSettingsDao(database: OrbitsDatabase) = database.settingsDao()

    @Provides
    @Singleton
    fun provideSyncQueueDao(database: OrbitsDatabase) = database.syncQueueDao()
}