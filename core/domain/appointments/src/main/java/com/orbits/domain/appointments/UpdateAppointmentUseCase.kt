package com.orbits.domain.appointments

import com.orbits.core.common.Result
import javax.inject.Inject

/**
 * Use case to update an existing appointment.
 */
class UpdateAppointmentUseCase @Inject constructor(
    private val appointmentRepository: AppointmentRepository
) {

    /**
     * Execute the use case.
     * @param appointment The updated appointment
     * @return Result containing the updated appointment, or error
     */
    suspend operator fun invoke(appointment: Appointment): Result<Appointment> {
        // Validate appointment
        val validationResult = AppointmentValidation.validate(
            title = appointment.title,
            description = appointment.description,
            appointmentType = appointment.appointmentType,
            startDateTime = appointment.startDateTime,
            endDateTime = appointment.endDateTime,
            allDayEvent = appointment.allDayEvent,
            location = appointment.location,
            isVirtual = appointment.isVirtual,
            meetingLink = appointment.meetingLink,
            meetingPlatform = appointment.meetingPlatform,
            reminderMinutesBefore = appointment.reminderMinutesBefore,
            isRecurring = appointment.isRecurring,
            recurrencePattern = appointment.recurrencePattern,
            calendarColor = appointment.calendarColor,
            notes = appointment.notes,
            status = appointment.status
        )

        if (!validationResult.isValid) {
            return Result.Error(
                IllegalArgumentException(
                    validationResult.errors.joinToString { it.message }
                )
            )
        }

        return appointmentRepository.updateAppointment(appointment)
    }
}
