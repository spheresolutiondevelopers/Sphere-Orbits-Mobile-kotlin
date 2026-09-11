package com.orbits.domain.meetings

import com.orbits.core.common.Result
import javax.inject.Inject

/**
 * Use case to join a meeting.
 * Returns the meeting link.
 */
class JoinMeetingUseCase @Inject constructor(
    private val meetingRepository: MeetingRepository
) {

    /**
     * Execute the use case.
     * @param meetingId The ID of the meeting to join
     * @return Result containing the meeting link, or error
     */
    suspend operator fun invoke(meetingId: String): Result<String> {
        if (meetingId.isBlank()) {
            return Result.Error(IllegalArgumentException("Meeting ID cannot be empty"))
        }
        return meetingRepository.joinMeeting(meetingId)
    }
}
