package com.orbits.data.notes

import com.orbits.core.common.Result
import com.orbits.core.common.Logger
import com.orbits.core.common.TokenProvider
import com.orbits.core.common.extensions.nowUtc
import com.orbits.core.database.dao.NoteDao
import com.orbits.core.database.dao.SyncQueueDao
import com.orbits.core.network.api.NoteApi
import com.orbits.core.network.error.ApiErrorParser
import com.orbits.data.sync.SyncQueueEntity
import com.orbits.data.notes.local.NoteEntity
import com.orbits.data.notes.mappers.NoteMapper
import com.orbits.domain.notes.Note
import com.orbits.domain.notes.NotesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.combine
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class NotesRepositoryImpl @Inject constructor(
    private val noteDao: NoteDao,
    private val syncQueueDao: SyncQueueDao,
    private val noteApi: NoteApi,
    private val noteMapper: NoteMapper,
    private val markdownParser: MarkdownParser,
    private val tokenProvider: TokenProvider
) : NotesRepository {

    companion object {
        private const val TAG = "NotesRepository"
    }

    // ─── Read Operations ──────────────────────────────────────────

    override fun getNotes(): Flow<List<Note>> {
        return noteDao.getNotesForUser(getUserId())
            .map { entities -> noteMapper.toDomainList(entities) }
    }

    override fun searchNotes(query: String): Flow<List<Note>> {
        return noteDao.searchNotes(getUserId(), query)
            .map { entities -> noteMapper.toDomainList(entities) }
    }

    override fun getNotesForTask(taskId: String): Flow<List<Note>> {
        return noteDao.getNotesForTask(getUserId(), taskId)
            .map { entities -> noteMapper.toDomainList(entities) }
    }

    override fun getNotesForEvent(eventId: String): Flow<List<Note>> {
        return noteDao.getNotesForEvent(getUserId(), eventId)
            .map { entities -> noteMapper.toDomainList(entities) }
    }

    override fun getNotesForAppointment(appointmentId: String): Flow<List<Note>> {
        return noteDao.getNotesForAppointment(getUserId(), appointmentId)
            .map { entities -> noteMapper.toDomainList(entities) }
    }

    override fun getNotesForMeeting(meetingId: String): Flow<List<Note>> {
        return noteDao.getNotesForMeeting(getUserId(), meetingId)
            .map { entities -> noteMapper.toDomainList(entities) }
    }

    override suspend fun getNote(noteId: String): Result<Note> {
        return try {
            val entity = noteDao.getNote(noteId)
            if (entity != null) {
                Result.Success(noteMapper.toDomain(entity))
            } else {
                Result.Error(IllegalStateException("Note not found: $noteId"))
            }
        } catch (e: Exception) {
            Logger.e(TAG, "Error fetching note $noteId", e)
            Result.Error(e)
        }
    }

    // ─── Write Operations ─────────────────────────────────────────

    override suspend fun createNote(note: Note): Result<Note> {
        return try {
            validateNote(note)

            // Parse and validate markdown
            if (note.contentFormat == "markdown") {
                val html = markdownParser.parseToHtml(note.content)
                // Store HTML in a separate field if needed
            }

            val entity = noteMapper.toEntity(note)
            noteDao.insertNote(entity)

            enqueueSync(SyncQueueEntity(
                entityType = "note",
                operation = "create",
                entityId = note.id,
                payloadJson = noteMapper.toCreateRequest(note).toString()
            ))

            Logger.d(TAG, "Note created locally: ${note.id}")
            Result.Success(note)
        } catch (e: Exception) {
            Logger.e(TAG, "Error creating note", e)
            Result.Error(e)
        }
    }

    override suspend fun updateNote(note: Note): Result<Note> {
        return try {
            validateNote(note)

            val existing = noteDao.getNote(note.id)
            if (existing == null) {
                return Result.Error(IllegalStateException("Note not found: ${note.id}"))
            }

            val entity = noteMapper.toEntity(note).copy(updatedAt = nowUtc())
            noteDao.updateNote(entity)

            enqueueSync(SyncQueueEntity(
                entityType = "note",
                operation = "update",
                entityId = note.id,
                payloadJson = noteMapper.toUpdateRequest(note).toString()
            ))

            Logger.d(TAG, "Note updated locally: ${note.id}")
            Result.Success(note)
        } catch (e: Exception) {
            Logger.e(TAG, "Error updating note ${note.id}", e)
            Result.Error(e)
        }
    }

    override suspend fun deleteNote(noteId: String): Result<Unit> {
        return try {
            noteDao.softDeleteNote(noteId)

            enqueueSync(SyncQueueEntity(
                entityType = "note",
                operation = "delete",
                entityId = noteId,
                payloadJson = null
            ))

            Logger.d(TAG, "Note deleted locally: $noteId")
            Result.Success(Unit)
        } catch (e: Exception) {
            Logger.e(TAG, "Error deleting note $noteId", e)
            Result.Error(e)
        }
    }

    override suspend fun permanentlyDeleteNote(noteId: String): Result<Unit> {
        return try {
            noteDao.permanentlyDeleteNote(noteId)

            // Remove from sync queue if pending
            syncQueueDao.dequeueForEntity("note", noteId)

            Logger.d(TAG, "Note permanently deleted: $noteId")
            Result.Success(Unit)
        } catch (e: Exception) {
            Logger.e(TAG, "Error permanently deleting note $noteId", e)
            Result.Error(e)
        }
    }

    override suspend fun restoreNote(noteId: String): Result<Note> {
        return try {
            // Restore is not directly supported in DAO, but we can update is_deleted flag
            // For simplicity, we'll use a manual update approach
            // Since we don't have a restore method, we'll need to use the update path
            // In a real implementation, we'd add a restore method to the DAO
            Result.Error(UnsupportedOperationException("Restore not implemented"))
        } catch (e: Exception) {
            Logger.e(TAG, "Error restoring note $noteId", e)
            Result.Error(e)
        }
    }

    // ─── Pin/Archive Operations ───────────────────────────────────

    override suspend fun pinNote(noteId: String, pinned: Boolean): Result<Note> {
        return try {
            val entity = noteDao.getNote(noteId)
            if (entity == null) {
                return Result.Error(IllegalStateException("Note not found: $noteId"))
            }

            val updated = entity.copy(
                isPinned = pinned,
                updatedAt = nowUtc()
            )
            noteDao.updateNote(updated)

            enqueueSync(SyncQueueEntity(
                entityType = "note",
                operation = "update",
                entityId = noteId,
                payloadJson = """{"isPinned":$pinned}"""
            ))

            Logger.d(TAG, "Note ${if (pinned) "pinned" else "unpinned"}: $noteId")
            Result.Success(noteMapper.toDomain(updated))
        } catch (e: Exception) {
            Logger.e(TAG, "Error updating pin status for note $noteId", e)
            Result.Error(e)
        }
    }

    override suspend fun archiveNote(noteId: String, archived: Boolean): Result<Note> {
        return try {
            val entity = noteDao.getNote(noteId)
            if (entity == null) {
                return Result.Error(IllegalStateException("Note not found: $noteId"))
            }

            val updated = entity.copy(
                isArchived = archived,
                updatedAt = nowUtc()
            )
            noteDao.updateNote(updated)

            enqueueSync(SyncQueueEntity(
                entityType = "note",
                operation = "update",
                entityId = noteId,
                payloadJson = """{"isArchived":$archived}"""
            ))

            Logger.d(TAG, "Note ${if (archived) "archived" else "unarchived"}: $noteId")
            Result.Success(noteMapper.toDomain(updated))
        } catch (e: Exception) {
            Logger.e(TAG, "Error updating archive status for note $noteId", e)
            Result.Error(e)
        }
    }

    // ─── Tags ──────────────────────────────────────────────────────

    override suspend fun addTags(noteId: String, tags: List<String>): Result<Note> {
        return try {
            val entity = noteDao.getNote(noteId)
            if (entity == null) {
                return Result.Error(IllegalStateException("Note not found: $noteId"))
            }

            val existingTags = if (entity.tags.isNullOrBlank()) {
                emptyList()
            } else {
                entity.tags.split(",").map { it.trim() }.filter { it.isNotBlank() }
            }

            val updatedTags = (existingTags + tags).distinct()
            val updated = entity.copy(
                tags = if (updatedTags.isEmpty()) null else updatedTags.joinToString(","),
                updatedAt = nowUtc()
            )
            noteDao.updateNote(updated)

            enqueueSync(SyncQueueEntity(
                entityType = "note",
                operation = "update",
                entityId = noteId,
                payloadJson = """{"tags":${updatedTags}}"""
            ))

            Result.Success(noteMapper.toDomain(updated))
        } catch (e: Exception) {
            Logger.e(TAG, "Error adding tags to note $noteId", e)
            Result.Error(e)
        }
    }

    override suspend fun removeTags(noteId: String, tags: List<String>): Result<Note> {
        return try {
            val entity = noteDao.getNote(noteId)
            if (entity == null) {
                return Result.Error(IllegalStateException("Note not found: $noteId"))
            }

            val existingTags = if (entity.tags.isNullOrBlank()) {
                emptyList()
            } else {
                entity.tags.split(",").map { it.trim() }.filter { it.isNotBlank() }
            }

            val updatedTags = existingTags.filter { it !in tags }
            val updated = entity.copy(
                tags = if (updatedTags.isEmpty()) null else updatedTags.joinToString(","),
                updatedAt = nowUtc()
            )
            noteDao.updateNote(updated)

            enqueueSync(SyncQueueEntity(
                entityType = "note",
                operation = "update",
                entityId = noteId,
                payloadJson = """{"tags":${updatedTags}}"""
            ))

            Result.Success(noteMapper.toDomain(updated))
        } catch (e: Exception) {
            Logger.e(TAG, "Error removing tags from note $noteId", e)
            Result.Error(e)
        }
    }

    override fun getNotesByTag(tag: String): Flow<List<Note>> {
        return noteDao.searchNotes(getUserId(), tag)
            .map { entities ->
                entities.filter { entity ->
                    entity.tags?.split(",")?.map { it.trim() }?.contains(tag) == true
                }
            }
            .map { entities -> noteMapper.toDomainList(entities) }
    }

    override suspend fun getAllTags(): Result<List<String>> {
        return try {
            val notes = noteDao.getNotesForUser(getUserId()).first()
            val allTags = notes.mapNotNull { entity ->
                entity.tags?.split(",")?.map { it.trim() }?.filter { it.isNotBlank() }
            }.flatten().distinct().sorted()
            Result.Success(allTags)
        } catch (e: Exception) {
            Logger.e(TAG, "Error getting all tags", e)
            Result.Error(e)
        }
    }

    // ─── Bulk Operations ──────────────────────────────────────────

    override suspend fun bulkDelete(noteIds: List<String>, permanent: Boolean): Result<Unit> {
        return try {
            noteIds.forEach { noteId ->
                if (permanent) {
                    noteDao.permanentlyDeleteNote(noteId)
                } else {
                    noteDao.softDeleteNote(noteId)
                }
            }

            enqueueSync(SyncQueueEntity(
                entityType = "note",
                operation = "bulk_delete",
                entityId = noteIds.joinToString(","),
                payloadJson = """{"noteIds":${noteIds},"permanent":$permanent}"""
            ))

            Result.Success(Unit)
        } catch (e: Exception) {
            Logger.e(TAG, "Error performing bulk delete", e)
            Result.Error(e)
        }
    }

    override suspend fun bulkUpdate(
        noteIds: List<String>,
        tags: List<String>?,
        isPinned: Boolean?,
        isArchived: Boolean?
    ): Result<Unit> {
        return try {
            noteIds.forEach { noteId ->
                val entity = noteDao.getNote(noteId)
                if (entity != null) {
                    var updated = entity.copy(updatedAt = nowUtc())
                    tags?.let {
                        updated = updated.copy(
                            tags = if (it.isEmpty()) null else it.joinToString(",")
                        )
                    }
                    isPinned?.let { updated = updated.copy(isPinned = it) }
                    isArchived?.let { updated = updated.copy(isArchived = it) }
                    noteDao.updateNote(updated)
                }
            }

            enqueueSync(SyncQueueEntity(
                entityType = "note",
                operation = "bulk_update",
                entityId = noteIds.joinToString(","),
                payloadJson = """{"noteIds":${noteIds},"tags":${tags},"isPinned":$isPinned,"isArchived":$isArchived}"""
            ))

            Result.Success(Unit)
        } catch (e: Exception) {
            Logger.e(TAG, "Error performing bulk update", e)
            Result.Error(e)
        }
    }

    // ─── Private Helpers ──────────────────────────────────────────

    private fun getUserId(): String {
        // This should come from auth state
        return "test_user_id"
    }

    private suspend fun enqueueSync(item: SyncQueueEntity) {
        syncQueueDao.enqueue(item)
    }

    private fun validateNote(note: Note) {
        require(note.content.isNotBlank()) { "Note content cannot be empty" }
        require(note.contentFormat in listOf("markdown", "plain", "html")) {
            "Invalid content format"
        }
        if (note.content.length > 100000) {
            require(note.content.length <= 100000) {
                "Note content must be <= 100,000 characters"
            }
        }
        if (note.title != null && note.title.length > 255) {
            require(note.title.length <= 255) {
                "Note title must be <= 255 characters"
            }
        }
        if (note.color != null && !note.color.matches(Regex("^#[0-9A-Fa-f]{6}$"))) {
            require(false) { "Invalid color format. Must be #RRGGBB" }
        }
        // Ensure at most one linked entity
        val linkedCount = listOf(
            note.taskId != null,
            note.eventId != null,
            note.appointmentId != null,
            note.meetingId != null
        ).count { it }
        require(linkedCount <= 1) {
            "Note can only be linked to one entity at a time"
        }
    }
}