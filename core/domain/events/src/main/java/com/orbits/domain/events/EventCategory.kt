package com.orbits.domain.events

/**
 * Event categories.
 */
enum class EventCategory(val displayName: String, val icon: String) {
    WEDDING("Wedding", "💒"),
    CONFERENCE("Conference", "🎤"),
    WORKSHOP("Workshop", "🛠️"),
    SEMINAR("Seminar", "📚"),
    NETWORKING("Networking", "🤝"),
    PARTY("Party", "🎉"),
    CONCERT("Concert", "🎵"),
    SPORTS("Sports", "🏆"),
    CHARITY("Charity", "❤️"),
    BUSINESS("Business", "💼"),
    PERSONAL("Personal", "👤"),
    OTHER("Other", "📌");

    companion object {
        fun fromString(value: String): EventCategory? {
            return values().find { it.name.equals(value, ignoreCase = true) }
        }

        fun fromDisplayName(value: String): EventCategory? {
            return values().find { it.displayName.equals(value, ignoreCase = true) }
        }
    }
}
