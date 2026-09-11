package com.orbits.core.sync

/**
 * Sync constants used throughout the sync engine.
 */
object SyncConstants {

    // ─── Sync Tags ──────────────────────────────────────────────────

    const val TAG_SYNC = "Sync"
    const val TAG_SYNC_WORKER = "SyncWorker"
    const val TAG_CONFLICT_RESOLVER = "ConflictResolver"
    const val TAG_SYNC_QUEUE = "SyncQueue"

    // ─── Entity Types ──────────────────────────────────────────────

    const val ENTITY_USER = "user"
    const val ENTITY_AUTH = "auth"
    const val ENTITY_TOKEN = "token"
    const val ENTITY_TASK = "task"
    const val ENTITY_SUBTASK = "subtask"
    const val ENTITY_CALENDAR_EVENT = "calendar_event"
    const val ENTITY_EVENT = "event"
    const val ENTITY_EVENT_PARTICIPANT = "event_participant"
    const val ENTITY_MEETING = "meeting"
    const val ENTITY_MEETING_PARTICIPANT = "meeting_participant"
    const val ENTITY_APPOINTMENT = "appointment"
    const val ENTITY_APPOINTMENT_PARTICIPANT = "appointment_participant"
    const val ENTITY_MESSAGE = "message"
    const val ENTITY_CONVERSATION = "conversation"
    const val ENTITY_CONVERSATION_PARTICIPANT = "conversation_participant"
    const val ENTITY_NOTE = "note"
    const val ENTITY_ANALYTICS_EVENT = "analytics_event"
    const val ENTITY_SETTINGS = "settings"

    // ─── Operations ─────────────────────────────────────────────────

    const val OP_CREATE = "create"
    const val OP_UPDATE = "update"
    const val OP_DELETE = "delete"
    const val OP_RESTORE = "restore"

    // ─── Sync Status ───────────────────────────────────────────────

    const val SYNC_STATUS_PENDING = "pending"
    const val SYNC_STATUS_SYNCED = "synced"
    const val SYNC_STATUS_ERROR = "error"
    const val SYNC_STATUS_CONFLICT = "conflict"

    // ─── Sync Timing ───────────────────────────────────────────────

    const val DEFAULT_SYNC_INTERVAL_MINUTES = 30
    const val MIN_SYNC_INTERVAL_MINUTES = 5
    const val MAX_SYNC_INTERVAL_MINUTES = 1440 // 24 hours

    // ─── Sync Limits ───────────────────────────────────────────────

    const val MAX_BATCH_SIZE = 100
    const val MAX_RETRY_COUNT = 3
    const val RETRY_BACKOFF_MS = 5000L // 5 seconds
    const val MAX_RETRY_BACKOFF_MS = 300000L // 5 minutes

    // ─── WorkManager Tags ──────────────────────────────────────────

    const val WORK_TAG_SYNC = "sync_work"
    const val WORK_TAG_SYNC_PERIODIC = "sync_periodic"
    const val WORK_TAG_SYNC_ONE_TIME = "sync_one_time"

    // ─── Sync Pref Keys ────────────────────────────────────────────

    const val PREF_LAST_SYNC_TIMESTAMP = "last_sync_timestamp"
    const val PREF_LAST_SYNC_SUCCESS = "last_sync_success"
    const val PREF_SYNC_QUEUE_SIZE = "sync_queue_size"
}
