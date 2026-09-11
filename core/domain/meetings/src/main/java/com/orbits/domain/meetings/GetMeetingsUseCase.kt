package com.orbits.domain.meetings

import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Use case to get all meetings for the current user.
 */
class GetMeetingsUseCase @Inject constructor(
    private val meetingRepository: MeetingRepository
) {

    /**
     * Execute the use case.
     * @return Flow emitting the list of meetings
     */
    operator fun invoke(): Flow<List<Meeting>> {
        return meetingRepository.getMeetings()
    }
}
