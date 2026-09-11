package com.orbits.domain.meetings

import com.orbits.core.common.Result
import javax.inject.Inject

/**
 * Use case to remove a participant from a meeting.
 */
class RemoveParticipantUseCase @Inject constructor(
    private val meetingRepository: MeetingRepository
) {

    /**
     * Execute the use case.
     * @param meetingId The ID of the meeting
     * @param participantId The ID of the participant to remove
     * @return Result indicating success or failure
     */
    suspend operator fun invoke(meetingId: String, participantId: String): Result<Unit> {
        if (meetingId.isBlank()) {
            return Result.Error(IllegalArgumentException("Meeting ID cannot be empty"))
        }
        if (participantId.isBlank()) {
            return Result.Error(IllegalArgumentException("Participant ID cannot be empty"))
        }
        return meetingRepository.removeParticipant(meetingId, participantId)
    }
}
