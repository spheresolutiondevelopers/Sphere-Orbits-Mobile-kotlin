package com.orbits.data.notes.remote

import kotlinx.serialization.Serializable

@Serializable
internal data class NoteDto(
    val id: String,
    val userId: String,
    val title: String? = null,
    val content: String,
    val contentFormat: String = "markdown",
    val tags: List<String> = emptyList(),
    val isPinned: Boolean = false,
    val isArchived: Boolean = false,
    val color: String? = null,
    val taskId: String? = null,
    val eventId: String? = null,
    val appointmentId: String? = null,
    val meetingId: String? = null,
    val reminderAt: String? = null,
    val isDeleted: Boolean = false,
    val deletedAt: String? = null,
    val createdAt: String,
    val updatedAt: String
)

@Serializable
internal data class CreateNoteRequest(
    val title: String? = null,
    val content: String,
    val contentFormat: String = "markdown",
    val tags: List<String> = emptyList(),
    val isPinned: Boolean = false,
    val isArchived: Boolean = false,
    val color: String? = null,
    val taskId: String? = null,
    val eventId: String? = null,
    val appointmentId: String? = null,
    val meetingId: String? = null,
    val reminderAt: String? = null
)

@Serializable
internal data class UpdateNoteRequest(
    val title: String? = null,
    val content: String? = null,
    val contentFormat: String? = null,
    val tags: List<String>? = null,
    val isPinned: Boolean? = null,
    val isArchived: Boolean? = null,
    val color: String? = null,
    val taskId: String? = null,
    val eventId: String? = null,
    val appointmentId: String? = null,
    val meetingId: String? = null,
    val reminderAt: String? = null
)

@Serializable
internal data class NoteSearchRequest(
    val query: String,
    val tags: List<String>? = null,
    val isPinned: Boolean? = null,
    val isArchived: Boolean? = null,
    val taskId: String? = null,
    val eventId: String? = null,
    val appointmentId: String? = null,
    val meetingId: String? = null,
    val page: Int = 1,
    val pageSize: Int = 20
)

@Serializable
internal data class BulkDeleteRequest(
    val noteIds: List<String>,
    val permanent: Boolean = false
)

@Serializable
internal data class BulkUpdateRequest(
    val noteIds: List<String>,
    val tags: List<String>? = null,
    val isPinned: Boolean? = null,
    val isArchived: Boolean? = null,
    val color: String? = null
)