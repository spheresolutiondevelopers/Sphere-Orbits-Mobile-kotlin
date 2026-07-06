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

/**
 * Database schema version constants.
 * Used for migrations and version tracking.
 */
object SchemaVersion {

    /**
     * Initial schema version.
     * Contains: users, tasks, subtasks, appointments, participants,
     * categories, event_categories, events, event_participants,
     * meetings, meeting_participants, reminders, notes,
     * user_connections, conversations, conversation_participants,
     * messages, notifications, sync_queue
     */
    const val VERSION_1 = 1

    /**
     * Added calendar_sync_log table for tracking external calendar syncs.
     * Added sync_status column to tasks, appointments, events, notes, meetings.
     */
    const val VERSION_2 = 2

    /**
     * Added index on updated_at for faster sync queries.
     * Added is_deleted index for soft-delete queries.
     */
    const val VERSION_3 = 3

    /**
     * Added attachments table for file attachments on tasks and appointments.
     * Added recurrence_exceptions table for modified occurrences of recurring events.
     */
    const val VERSION_4 = 4

    /**
     * Added analytics_events table for local analytics tracking.
     * Added last_sync_timestamp to users table for incremental sync.
     */
    const val VERSION_5 = 5

    /**
     * Current database version.
     */
    const val CURRENT_VERSION = VERSION_5
}