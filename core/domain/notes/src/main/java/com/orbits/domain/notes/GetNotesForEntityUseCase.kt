package com.orbits.domain.notes

import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Use case to get notes linked to a specific entity.
 */
class GetNotesForEntityUseCase @Inject constructor(
    private val notesRepository: NotesRepository
) {

    /**
     * Get notes linked to a task.
     */
    fun forTask(taskId: String): Flow<List<Note>> {
        require(taskId.isNotBlank()) { "Task ID cannot be empty" }
        return notesRepository.getNotesForTask(taskId)
    }

    /**
     * Get notes linked to an event.
     */
    fun forEvent(eventId: String): Flow<List<Note>> {
        require(eventId.isNotBlank()) { "Event ID cannot be empty" }
        return notesRepository.getNotesForEvent(eventId)
    }

    /**
     * Get notes linked to an appointment.
     */
    fun forAppointment(appointmentId: String): Flow<List<Note>> {
        require(appointmentId.isNotBlank()) { "Appointment ID cannot be empty" }
        return notesRepository.getNotesForAppointment(appointmentId)
    }

    /**
     * Get notes linked to a meeting.
     */
    fun forMeeting(meetingId: String): Flow<List<Note>> {
        require(meetingId.isNotBlank()) { "Meeting ID cannot be empty" }
        return notesRepository.getNotesForMeeting(meetingId)
    }
}
