/*
 * Copyright © 2026 Sphere Solution Developers
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */

package com.orbits.core.database

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import android.content.Context
import com.orbits.data.auth.AuthEntity
import com.orbits.data.auth.TokenEntity
import com.orbits.data.tasks.TaskEntity
import com.orbits.data.tasks.SubtaskEntity
import com.orbits.data.calendar.CalendarEventEntity
import com.orbits.data.events.EventEntity
import com.orbits.data.events.EventParticipantEntity
import com.orbits.data.meetings.MeetingEntity
import com.orbits.data.meetings.MeetingParticipantEntity
import com.orbits.data.appointments.AppointmentEntity
import com.orbits.data.appointments.ParticipantEntity
import com.orbits.data.chat.MessageEntity
import com.orbits.data.chat.ConversationEntity
import com.orbits.data.chat.ConversationParticipantEntity
import com.orbits.data.notes.NoteEntity
import com.orbits.data.analytics.AnalyticsEntity
import com.orbits.data.settings.SettingsEntity
import com.orbits.data.sync.SyncQueueEntity
import com.orbits.core.database.dao.TaskDao
import com.orbits.core.database.dao.CalendarEventDao
import com.orbits.core.database.dao.MeetingDao
import com.orbits.core.database.dao.AppointmentDao
import com.orbits.core.database.dao.NoteDao
import com.orbits.core.database.dao.UserDao
import com.orbits.core.database.dao.MessageDao
import com.orbits.core.database.dao.AnalyticsDao
import com.orbits.core.database.dao.SettingsDao
import com.orbits.core.database.dao.SyncQueueDao
import com.orbits.core.database.dao.EventDao

@Database(
    entities = [
        // Auth
        AuthEntity::class,
        TokenEntity::class,

        // Tasks
        TaskEntity::class,
        SubtaskEntity::class,

        // Calendar
        CalendarEventEntity::class,

        // Events
        EventEntity::class,
        EventParticipantEntity::class,

        // Meetings
        MeetingEntity::class,
        MeetingParticipantEntity::class,

        // Appointments
        AppointmentEntity::class,
        ParticipantEntity::class,

        // Chat
        MessageEntity::class,
        ConversationEntity::class,
        ConversationParticipantEntity::class,

        // Notes
        NoteEntity::class,

        // Analytics
        AnalyticsEntity::class,

        // Settings
        SettingsEntity::class,

        // Sync
        SyncQueueEntity::class
    ],
    version = SchemaVersion.CURRENT_VERSION,
    exportSchema = true
)
@TypeConverters(DatabaseConverters::class)
abstract class OrbitsDatabase : RoomDatabase() {

    // ─── DAOs ─────────────────────────────────────────────────────

    abstract fun taskDao(): TaskDao
    abstract fun calendarEventDao(): CalendarEventDao
    abstract fun meetingDao(): MeetingDao
    abstract fun appointmentDao(): AppointmentDao
    abstract fun noteDao(): NoteDao
    abstract fun userDao(): UserDao
    abstract fun messageDao(): MessageDao
    abstract fun analyticsDao(): AnalyticsDao
    abstract fun settingsDao(): SettingsDao
    abstract fun syncQueueDao(): SyncQueueDao
    abstract fun eventDao(): EventDao

    companion object {
        @Volatile
        private var INSTANCE: OrbitsDatabase? = null

        fun getInstance(context: Context): OrbitsDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: buildDatabase(context).also { INSTANCE = it }
            }
        }

        private fun buildDatabase(context: Context): OrbitsDatabase {
            return Room.databaseBuilder(
                context.applicationContext,
                OrbitsDatabase::class.java,
                "orbits_database.db"
            )
            .addMigrations(
                Migration_1_2(),
                Migration_2_3(),
                Migration_3_4(),
                Migration_4_5()
            )
            .fallbackToDestructiveMigrationOnDowngrade()
            .build()
        }
    }
}