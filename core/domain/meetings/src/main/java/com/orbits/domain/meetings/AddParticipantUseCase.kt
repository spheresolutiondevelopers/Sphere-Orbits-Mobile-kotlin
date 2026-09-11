package com.orbits.domain.meetings

import com.orbits.core.common.Result
import com.orbits.core.common.extensions.nowUtc
import java.util.UUID
import javax.inject.Inject

/**
 * Use case to add a participant to a meeting.
 */
class AddParticipantUseCase @Inject constructor(
    private val meetingRepository: MeetingRepository
) {

    /**
     * Execute the use case.
     * @param meetingId The ID of the meeting
     * @param email Participant's email address
     * @param fullName Participant's full name (optional)
     * @param role Participant role (attendee by default)
     * @return Result containing the created participant, or error
     */
    suspend operator fun invoke(
        meetingId: String,
        email: String,
        fullName: String? = null,
        role: String = "attendee"
    ): Result<MeetingParticipant> {
        // Validate
        if (meetingId.isBlank()) {
            return Result.Error(IllegalArgumentException("Meeting ID cannot be empty"))
        }
        if (email.isBlank()) {
            return Result.Error(IllegalArgumentException("Email cannot be empty"))
        }
        if (!email.matches(Regex("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"))) {
            return Result.Error(IllegalArgumentException("Invalid email format"))
        }
        if (role !in listOf("organizer", "presenter", "attendee")) {
            return Result.Error(IllegalArgumentException("Invalid participant role"))
        }

        val participant = MeetingParticipant(
            id = UUID.randomUUID().toString(),
            meetingId = meetingId,
            userId = null,
            email = email,
            fullName = fullName,
            invitationStatus = "pending",
            participantRole = role,
            joinedAt = null,
            leftAt = null,
            durationSeconds = null,
            createdAt = nowUtc(),
            updatedAt = nowUtc()
        )

        return meetingRepository.addParticipant(meetingId, participant)
    }
}
