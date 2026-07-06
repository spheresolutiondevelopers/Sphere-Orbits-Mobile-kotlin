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

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

/**
 * Migration from version 2 to 3.
 * Adds performance indexes on updated_at and is_deleted columns.
 */
class Migration_2_3 : Migration(SchemaVersion.VERSION_2, SchemaVersion.VERSION_3) {

    override fun migrate(database: SupportSQLiteDatabase) {
        // Index on updated_at for sync queries
        database.execSQL("CREATE INDEX idx_tasks_updated_at ON tasks(updated_at)")
        database.execSQL("CREATE INDEX idx_appointments_updated_at ON appointments(updated_at)")
        database.execSQL("CREATE INDEX idx_events_updated_at ON events(updated_at)")
        database.execSQL("CREATE INDEX idx_notes_updated_at ON notes(updated_at)")
        database.execSQL("CREATE INDEX idx_meetings_updated_at ON meetings(updated_at)")
        database.execSQL("CREATE INDEX idx_calendar_events_updated_at ON calendar_events(updated_at)")

        // Index on is_deleted for soft-delete queries
        database.execSQL("CREATE INDEX idx_tasks_is_deleted ON tasks(is_deleted)")
        database.execSQL("CREATE INDEX idx_appointments_is_deleted ON appointments(is_deleted)")
        database.execSQL("CREATE INDEX idx_events_is_deleted ON events(is_deleted)")
        database.execSQL("CREATE INDEX idx_notes_is_deleted ON notes(is_deleted)")
        database.execSQL("CREATE INDEX idx_meetings_is_deleted ON meetings(is_deleted)")

        // Composite index for common queries
        database.execSQL("""
            CREATE INDEX idx_tasks_user_status_date ON tasks(user_id, status, due_date)
        """)
        database.execSQL("""
            CREATE INDEX idx_appointments_user_date ON appointments(user_id, start_datetime)
        """)
        database.execSQL("""
            CREATE INDEX idx_notes_user_updated ON notes(user_id, updated_at)
        """)
    }
}