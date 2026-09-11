package com.orbits.domain.meetings

import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Use case to get upcoming meetings (scheduled and in the future).
 */
class GetUpcomingMeetingsUseCase @Inject constructor(
    private val meetingRepository: MeetingRepository
) {

    /**
     * Execute the use case.
     * @return Flow emitting the list of upcoming meetings
     */
    operator fun invoke(): Flow<List<Meeting>> {
        return meetingRepository.getUpcomingMeetings()
    }
}
