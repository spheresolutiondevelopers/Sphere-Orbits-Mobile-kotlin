package com.orbits.domain.appointments

/**
 * Appointment types.
 */
enum class AppointmentType(val displayName: String, val icon: String) {
    GENERAL("General", "📌"),
    DOCTOR("Doctor", "🏥"),
    BUSINESS("Business", "💼"),
    PERSONAL("Personal", "👤"),
    MEETING("Meeting", "🤝"),
    CONSULTATION("Consultation", "💬"),
    INTERVIEW("Interview", "🎯"),
    DENTIST("Dentist", "🦷"),
    THERAPY("Therapy", "🧠"),
    LEGAL("Legal", "⚖️");

    companion object {
        fun fromString(value: String): AppointmentType? {
            return values().find { it.name.equals(value, ignoreCase = true) }
        }

        fun fromDisplayName(value: String): AppointmentType? {
            return values().find { it.displayName.equals(value, ignoreCase = true) }
        }
    }
}
