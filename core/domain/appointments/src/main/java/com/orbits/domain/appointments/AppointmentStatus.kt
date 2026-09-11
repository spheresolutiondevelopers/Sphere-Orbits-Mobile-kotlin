package com.orbits.domain.appointments

/**
 * Appointment status values.
 */
enum class AppointmentStatus(val displayName: String) {
    SCHEDULED("Scheduled"),
    CONFIRMED("Confirmed"),
    CANCELLED("Cancelled"),
    COMPLETED("Completed"),
    RESCHEDULED("Rescheduled");

    companion object {
        fun fromString(value: String): AppointmentStatus? {
            return values().find { it.name.equals(value, ignoreCase = true) }
        }
    }
}
