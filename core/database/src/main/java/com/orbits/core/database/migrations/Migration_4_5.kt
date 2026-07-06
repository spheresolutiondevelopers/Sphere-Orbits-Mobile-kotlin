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
 * Migration from version 4 to 5.
 * Adds analytics_events table and last_sync_timestamp to users.
 */
class Migration_4_5 : Migration(SchemaVersion.VERSION_4, SchemaVersion.VERSION_5) {

    override fun migrate(database: SupportSQLiteDatabase) {
        // Add last_sync_timestamp to users
        database.execSQL("""
            ALTER TABLE users 
            ADD COLUMN last_sync_timestamp TEXT
        """)

        // Analytics events table
        database.execSQL("""
            CREATE TABLE analytics_events (
                id TEXT PRIMARY KEY,
                user_id TEXT NOT NULL,
                event_type TEXT NOT NULL,
                event_data TEXT,
                event_timestamp TEXT NOT NULL,
                session_id TEXT,
                created_at TEXT DEFAULT (datetime('now')),
                FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
            )
        """)

        // Indexes for analytics
        database.execSQL("""
            CREATE INDEX idx_analytics_user_timestamp ON analytics_events(user_id, event_timestamp)
        """)
        database.execSQL("""
            CREATE INDEX idx_analytics_event_type ON analytics_events(event_type)
        """)
        database.execSQL("""
            CREATE INDEX idx_analytics_session ON analytics_events(session_id)
        """)

        // Add index on sync_queue created_at
        database.execSQL("""
            CREATE INDEX idx_sync_queue_created_at ON sync_queue(created_at)
        """)
    }
}