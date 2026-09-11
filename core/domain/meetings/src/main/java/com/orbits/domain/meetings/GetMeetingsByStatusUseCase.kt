package com.orbits.domain.meetings

import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Use case to get meetings by status.
 */
class GetMeetingsByStatusUseCase @Inject constructor(
    private val meetingRepository: MeetingRepository
) {

    /**
     * Execute the use case.
     * @param status The status to filter by (scheduled, live, ended, cancelled)
     * @return Flow emitting the filtered list of meetings
     */
    operator fun invoke(status: String): Flow<List<Meeting>> {
        require(status.isNotBlank()) { "Status cannot be empty" }
        require(status in listOf("scheduled", "live", "ended", "cancelled")) {
            "Invalid status. Must be: scheduled, live, ended, or cancelled"
        }
        return meetingRepository.getMeetingsByStatus(status)
    }
}
