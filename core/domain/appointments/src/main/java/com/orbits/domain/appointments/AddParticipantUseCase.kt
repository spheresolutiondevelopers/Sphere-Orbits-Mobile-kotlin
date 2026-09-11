package com.orbits.domain.appointments

import com.orbits.core.common.Result
import com.orbits.core.common.extensions.nowUtc
import java.util.UUID
import javax.inject.Inject

/**
 * Use case to add a participant to an appointment.
 */
class AddParticipantUseCase @Inject constructor(
    private val appointmentRepository: AppointmentRepository
) {

    /**
     * Execute the use case.
     * @param appointmentId The ID of the appointment
     * @param email Participant's email address
     * @param fullName Participant's full name (optional)
     * @param role Participant role (attendee by default)
     * @return Result containing the created participant, or error
     */
    suspend operator fun invoke(
        appointmentId: String,
        email: String,
        fullName: String? = null,
        role: String = "attendee"
    ): Result<AppointmentParticipant> {
        // Validate
        if (appointmentId.isBlank()) {
            return Result.Error(IllegalArgumentException("Appointment ID cannot be empty"))
        }
        if (email.isBlank()) {
            return Result.Error(IllegalArgumentException("Email cannot be empty"))
        }
        if (!email.matches(Regex("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"))) {
            return Result.Error(IllegalArgumentException("Invalid email format"))
        }
        if (role !in listOf("organizer", "attendee", "optional")) {
            return Result.Error(IllegalArgumentException("Invalid participant role"))
        }

        val participant = AppointmentParticipant(
            id = UUID.randomUUID().toString(),
            appointmentId = appointmentId,
            userId = null,
            email = email,
            fullName = fullName,
            invitationStatus = "pending",
            participantRole = role,
            createdAt = nowUtc(),
            updatedAt = nowUtc()
        )

        return appointmentRepository.addParticipant(appointmentId, participant)
    }

    /**
     * Add multiple participants to an appointment.
     */
    suspend fun addMultiple(
        appointmentId: String,
        participants: List<Pair<String, String?>> // email to fullName pairs
    ): Result<List<AppointmentParticipant>> {
        if (appointmentId.isBlank()) {
            return Result.Error(IllegalArgumentException("Appointment ID cannot be empty"))
        }
        if (participants.isEmpty()) {
            return Result.Error(IllegalArgumentException("At least one participant required"))
        }

        val participantsList = participants.map { (email, fullName) ->
            AppointmentParticipant(
                id = UUID.randomUUID().toString(),
                appointmentId = appointmentId,
                userId = null,
                email = email,
                fullName = fullName,
                invitationStatus = "pending",
                participantRole = "attendee",
                createdAt = nowUtc(),
                updatedAt = nowUtc()
            )
        }

        return appointmentRepository.addParticipants(appointmentId, participantsList)
    }
}
