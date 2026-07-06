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
 * Migration from version 1 to 2.
 * Adds calendar_sync_log table and sync_status columns.
 */
class Migration_1_2 : Migration(SchemaVersion.VERSION_1, SchemaVersion.VERSION_2) {

    override fun migrate(database: SupportSQLiteDatabase) {
        // Add sync_status column to tasks
        database.execSQL("""
            ALTER TABLE tasks 
            ADD COLUMN sync_status TEXT DEFAULT 'synced'
        """)

        // Add sync_status column to appointments
        database.execSQL("""
            ALTER TABLE appointments 
            ADD COLUMN sync_status TEXT DEFAULT 'synced'
        """)

        // Add sync_status column to events
        database.execSQL("""
            ALTER TABLE events 
            ADD COLUMN sync_status TEXT DEFAULT 'synced'
        """)

        // Add sync_status column to notes
        database.execSQL("""
            ALTER TABLE notes 
            ADD COLUMN sync_status TEXT DEFAULT 'synced'
        """)

        // Add sync_status column to meetings
        database.execSQL("""
            ALTER TABLE meetings 
            ADD COLUMN sync_status TEXT DEFAULT 'synced'
        """)

        // Create calendar_sync_log table
        database.execSQL("""
            CREATE TABLE calendar_sync_log (
                id TEXT PRIMARY KEY,
                user_id TEXT NOT NULL,
                calendar_type TEXT NOT NULL,
                sync_start TEXT NOT NULL,
                sync_end TEXT,
                status TEXT DEFAULT 'pending',
                items_synced INTEGER DEFAULT 0,
                error_message TEXT,
                created_at TEXT DEFAULT (datetime('now')),
                FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
            )
        """)

        // Add index on sync_status for faster queries
        database.execSQL("""
            CREATE INDEX idx_tasks_sync_status ON tasks(sync_status)
        """)
        database.execSQL("""
            CREATE INDEX idx_appointments_sync_status ON appointments(sync_status)
        """)
        database.execSQL("""
            CREATE INDEX idx_events_sync_status ON events(sync_status)
        """)
    }
}