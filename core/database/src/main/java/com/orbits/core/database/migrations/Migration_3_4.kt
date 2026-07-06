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
 * Migration from version 3 to 4.
 * Adds attachments table and recurrence_exceptions table.
 */
class Migration_3_4 : Migration(SchemaVersion.VERSION_3, SchemaVersion.VERSION_4) {

    override fun migrate(database: SupportSQLiteDatabase) {
        // Attachments table
        database.execSQL("""
            CREATE TABLE attachments (
                id TEXT PRIMARY KEY,
                entity_type TEXT NOT NULL,
                entity_id TEXT NOT NULL,
                file_name TEXT NOT NULL,
                file_size INTEGER NOT NULL,
                file_path TEXT NOT NULL,
                mime_type TEXT,
                uploaded_at TEXT DEFAULT (datetime('now')),
                is_deleted INTEGER DEFAULT 0,
                FOREIGN KEY (entity_type) CHECK(entity_type IN ('task', 'appointment', 'event', 'note', 'meeting'))
            )
        """)

        // Index for faster attachment lookups
        database.execSQL("""
            CREATE INDEX idx_attachments_entity ON attachments(entity_type, entity_id)
        """)

        // Recurrence exceptions table
        database.execSQL("""
            CREATE TABLE recurrence_exceptions (
                id TEXT PRIMARY KEY,
                entity_type TEXT NOT NULL,
                entity_id TEXT NOT NULL,
                exception_date TEXT NOT NULL,
                status TEXT DEFAULT 'cancelled',
                created_at TEXT DEFAULT (datetime('now')),
                FOREIGN KEY (entity_type) CHECK(entity_type IN ('task', 'appointment', 'event', 'meeting'))
            )
        """)

        // Index on exception_date for faster lookups
        database.execSQL("""
            CREATE INDEX idx_recurrence_exceptions_date ON recurrence_exceptions(exception_date)
        """)
        database.execSQL("""
            CREATE INDEX idx_recurrence_exceptions_entity ON recurrence_exceptions(entity_type, entity_id)
        """)
    }
}