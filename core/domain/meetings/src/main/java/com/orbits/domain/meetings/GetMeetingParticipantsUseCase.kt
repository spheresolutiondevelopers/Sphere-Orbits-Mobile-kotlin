package com.orbits.domain.meetings

import com.orbits.core.common.Result
import javax.inject.Inject

/**
 * Use case to get participants for a meeting.
 */
class GetMeetingParticipantsUseCase @Inject constructor(
    private val meetingRepository: MeetingRepository
) {

    /**
     * Execute the use case.
     * @param meetingId The ID of the meeting
     * @return Result containing the list of participants, or error
     */
    suspend operator fun invoke(meetingId: String): Result<List<MeetingParticipant>> {
        if (meetingId.isBlank()) {
            return Result.Error(IllegalArgumentException("Meeting ID cannot be empty"))
        }
        return meetingRepository.getParticipants(meetingId)
    }

    /**
     * Execute the use case for accepted participants only.
     * @param meetingId The ID of the meeting
     * @return Result containing the list of accepted participants, or error
     */
    suspend fun getAccepted(meetingId: String): Result<List<MeetingParticipant>> {
        if (meetingId.isBlank()) {
            return Result.Error(IllegalArgumentException("Meeting ID cannot be empty"))
        }
        return meetingRepository.getAcceptedParticipants(meetingId)
    }
}
