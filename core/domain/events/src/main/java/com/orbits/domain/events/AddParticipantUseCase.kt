package com.orbits.domain.events

import com.orbits.core.common.Result
import com.orbits.core.common.extensions.nowUtc
import java.util.UUID
import javax.inject.Inject

/**
 * Use case to add a participant to an event.
 */
class AddParticipantUseCase @Inject constructor(
    private val eventRepository: EventRepository
) {

    /**
     * Execute the use case.
     * @param eventId The ID of the event
     * @param email Participant's email address
     * @param fullName Participant's full name (optional)
     * @param role Participant role (attendee by default)
     * @param plusOnes Number of plus-ones (0 by default)
     * @param dietaryRestrictions Optional dietary restrictions
     * @param specialRequests Optional special requests
     * @return Result containing the created participant, or error
     */
    suspend operator fun invoke(
        eventId: String,
        email: String,
        fullName: String? = null,
        role: String = "attendee",
        plusOnes: Int = 0,
        dietaryRestrictions: String? = null,
        specialRequests: String? = null
    ): Result<EventParticipant> {
        // Validate
        if (eventId.isBlank()) {
            return Result.Error(IllegalArgumentException("Event ID cannot be empty"))
        }
        if (email.isBlank()) {
            return Result.Error(IllegalArgumentException("Email cannot be empty"))
        }
        if (!email.matches(Regex("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"))) {
            return Result.Error(IllegalArgumentException("Invalid email format"))
        }
        if (role !in listOf("organizer", "attendee", "optional", "speaker", "vendor")) {
            return Result.Error(IllegalArgumentException("Invalid participant role"))
        }
        if (plusOnes < 0) {
            return Result.Error(IllegalArgumentException("Plus-ones cannot be negative"))
        }

        val participant = EventParticipant(
            id = UUID.randomUUID().toString(),
            eventId = eventId,
            userId = null,
            email = email,
            fullName = fullName,
            invitationStatus = "pending",
            participantRole = role,
            plusOnes = plusOnes,
            dietaryRestrictions = dietaryRestrictions,
            specialRequests = specialRequests,
            checkedIn = false,
            checkInTime = null,
            createdAt = nowUtc(),
            updatedAt = nowUtc()
        )

        return eventRepository.addParticipant(eventId, participant)
    }
}
