package com.orbits.domain.meetings

import com.orbits.core.common.Result
import com.orbits.core.common.extensions.nowUtc
import javax.inject.Inject

/**
 * Use case to update an existing meeting.
 */
class UpdateMeetingUseCase @Inject constructor(
    private val meetingRepository: MeetingRepository
) {

    /**
     * Execute the use case.
     * @param meeting The updated meeting
     * @return Result containing the updated meeting, or error
     */
    suspend operator fun invoke(meeting: Meeting): Result<Meeting> {
        // Validate meeting
        val validationResult = MeetingValidation.validate(
            taskId = meeting.taskId,
            title = meeting.title,
            description = meeting.description,
            startDateTime = meeting.startDateTime,
            endDateTime = meeting.endDateTime,
            meetingPlatform = meeting.meetingPlatform,
            agenda = meeting.agenda,
            isRecurring = meeting.isRecurring,
            recurrencePattern = meeting.recurrencePattern,
            status = meeting.status
        )

        if (!validationResult.isValid) {
            return Result.Error(
                IllegalArgumentException(
                    validationResult.errors.joinToString { it.message }
                )
            )
        }

        return meetingRepository.updateMeeting(meeting)
    }
}
