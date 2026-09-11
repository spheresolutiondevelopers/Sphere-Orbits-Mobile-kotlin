package com.orbits.domain.meetings

import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Use case to get live meetings (currently ongoing).
 */
class GetLiveMeetingsUseCase @Inject constructor(
    private val meetingRepository: MeetingRepository
) {

    /**
     * Execute the use case.
     * @return Flow emitting the list of live meetings
     */
    operator fun invoke(): Flow<List<Meeting>> {
        return meetingRepository.getLiveMeetings()
    }
}
