package com.orbits.domain.meetings

import com.orbits.core.common.Result
import javax.inject.Inject

/**
 * Use case to get a single meeting by ID.
 */
class GetMeetingUseCase @Inject constructor(
    private val meetingRepository: MeetingRepository
) {

    /**
     * Execute the use case.
     * @param meetingId The ID of the meeting to retrieve
     * @return Result containing the meeting, or error
     */
    suspend operator fun invoke(meetingId: String): Result<Meeting> {
        if (meetingId.isBlank()) {
            return Result.Error(IllegalArgumentException("Meeting ID cannot be empty"))
        }
        return meetingRepository.getMeeting(meetingId)
    }

    /**
     * Get a meeting by its associated task ID.
     */
    suspend fun byTaskId(taskId: String): Result<Meeting> {
        if (taskId.isBlank()) {
            return Result.Error(IllegalArgumentException("Task ID cannot be empty"))
        }
        return meetingRepository.getMeetingByTaskId(taskId)
    }
}
