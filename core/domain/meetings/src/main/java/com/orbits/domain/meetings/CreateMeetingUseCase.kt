package com.orbits.domain.meetings

import com.orbits.core.common.Result
import com.orbits.core.common.extensions.nowUtc
import java.util.UUID
import javax.inject.Inject

/**
 * Use case to create a new meeting.
 */
class CreateMeetingUseCase @Inject constructor(
    private val meetingRepository: MeetingRepository
) {

    /**
     * Execute the use case.
     * @param taskId The ID of the associated task
     * @param title Meeting title
     * @param description Optional description
     * @param startDateTime Start date and time (ISO 8601)
     * @param endDateTime End date and time (ISO 8601)
     * @param meetingPlatform Meeting platform (zoom, teams, google_meet, custom)
     * @param agenda Optional agenda
     * @param isRecurring Whether this meeting recurs
     * @param recurrencePattern Recurrence rule (RRULE)
     * @param organizerUserId User ID of the organizer (if null, uses current user)
     * @return Result containing the created meeting, or error
     */
    suspend operator fun invoke(
        taskId: String,
        title: String,
        description: String? = null,
        startDateTime: String,
        endDateTime: String,
        meetingPlatform: String? = null,
        agenda: String? = null,
        isRecurring: Boolean = false,
        recurrencePattern: String? = null,
        organizerUserId: String? = null
    ): Result<Meeting> {
        // Validate input
        val validationResult = MeetingValidation.validate(
            taskId = taskId,
            title = title,
            description = description,
            startDateTime = startDateTime,
            endDateTime = endDateTime,
            meetingPlatform = meetingPlatform,
            agenda = agenda,
            isRecurring = isRecurring,
            recurrencePattern = recurrencePattern
        )

        if (!validationResult.isValid) {
            return Result.Error(
                IllegalArgumentException(
                    validationResult.errors.joinToString { it.message }
                )
            )
        }

        // Create meeting
        val now = nowUtc()
        val meeting = Meeting(
            id = UUID.randomUUID().toString(),
            taskId = taskId,
            organizerUserId = organizerUserId ?: getCurrentUserId(),
            title = title,
            description = description,
            startDateTime = startDateTime,
            endDateTime = endDateTime,
            meetingLink = null, // Will be set by platform integration later
            meetingPlatform = meetingPlatform,
            meetingId = null,
            passcode = null,
            agenda = agenda,
            minutes = null,
            actionItems = emptyList(),
            isRecurring = isRecurring,
            recurrencePattern = recurrencePattern,
            status = "scheduled",
            recordingUrl = null,
            transcriptUrl = null,
            isDeleted = false,
            createdAt = now,
            updatedAt = now
        )

        return meetingRepository.createMeeting(meeting)
    }

    // This should come from auth state
    private fun getCurrentUserId(): String {
        return "test_user_id"
    }
}
