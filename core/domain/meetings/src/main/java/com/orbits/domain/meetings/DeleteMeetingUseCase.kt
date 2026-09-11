package com.orbits.domain.meetings

import com.orbits.core.common.Result
import javax.inject.Inject

/**
 * Use case to delete a meeting.
 */
class DeleteMeetingUseCase @Inject constructor(
    private val meetingRepository: MeetingRepository
) {

    /**
     * Execute the use case.
     * @param meetingId The ID of the meeting to delete
     * @return Result indicating success or failure
     */
    suspend operator fun invoke(meetingId: String): Result<Unit> {
        if (meetingId.isBlank()) {
            return Result.Error(IllegalArgumentException("Meeting ID cannot be empty"))
        }
        return meetingRepository.deleteMeeting(meetingId)
    }
}
