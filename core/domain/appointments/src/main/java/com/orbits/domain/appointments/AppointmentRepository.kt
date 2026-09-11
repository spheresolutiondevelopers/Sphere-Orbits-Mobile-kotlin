package com.orbits.domain.appointments

import com.orbits.core.common.Result
import kotlinx.coroutines.flow.Flow

/**
 * Appointment repository interface.
 * Defines all appointment operations.
 * Implemented by :core:data:appointments.
 */
interface AppointmentRepository {

    // ─── Read Operations ──────────────────────────────────────────

    /**
     * Get all appointments for the current user.
     */
    fun getAppointments(): Flow<List<Appointment>>

    /**
     * Get appointments in a date range.
     */
    fun getAppointmentsInDateRange(startDate: String, endDate: String): Flow<List<Appointment>>

    /**
     * Get appointments by status.
     */
    fun getAppointmentsByStatus(status: String): Flow<List<Appointment>>

    /**
     * Get appointments by type.
     */
    fun getAppointmentsByType(type: String): Flow<List<Appointment>>

    /**
     * Get appointments for a specific date.
     */
    fun getAppointmentsForDate(date: String): Flow<List<Appointment>>

    /**
     * Get a single appointment by ID.
     */
    suspend fun getAppointment(appointmentId: String): Result<Appointment>

    /**
     * Get an appointment by external calendar ID.
     */
    suspend fun getAppointmentByExternalId(externalId: String): Result<Appointment>

    // ─── Participants ─────────────────────────────────────────────

    /**
     * Get participants for an appointment.
     */
    suspend fun getParticipants(appointmentId: String): Result<List<AppointmentParticipant>>

    /**
     * Get accepted participants for an appointment.
     */
    suspend fun getAcceptedParticipants(appointmentId: String): Result<List<AppointmentParticipant>>

    /**
     * Add a participant to an appointment.
     */
    suspend fun addParticipant(appointmentId: String, participant: AppointmentParticipant): Result<AppointmentParticipant>

    /**
     * Add multiple participants to an appointment.
     */
    suspend fun addParticipants(appointmentId: String, participants: List<AppointmentParticipant>): Result<List<AppointmentParticipant>>

    /**
     * Update a participant's status.
     */
    suspend fun updateParticipantStatus(appointmentId: String, email: String, status: String): Result<Unit>

    /**
     * Remove a participant from an appointment.
     */
    suspend fun removeParticipant(appointmentId: String, participantId: String): Result<Unit>

    // ─── Write Operations ─────────────────────────────────────────

    /**
     * Create a new appointment.
     */
    suspend fun createAppointment(appointment: Appointment): Result<Appointment>

    /**
     * Update an existing appointment.
     */
    suspend fun updateAppointment(appointment: Appointment): Result<Appointment>

    /**
     * Update appointment status.
     */
    suspend fun updateAppointmentStatus(appointmentId: String, status: String): Result<Appointment>

    /**
     * Cancel an appointment.
     */
    suspend fun cancelAppointment(appointmentId: String): Result<Appointment>

    /**
     * Delete an appointment (soft delete).
     */
    suspend fun deleteAppointment(appointmentId: String): Result<Unit>

    /**
     * Restore a deleted appointment.
     */
    suspend fun restoreAppointment(appointmentId: String): Result<Appointment>

    // ─── Availability ─────────────────────────────────────────────

    /**
     * Check if a time slot is available.
     */
    suspend fun checkAvailability(
        startDateTime: String,
        endDateTime: String,
        ignoreAppointmentId: String? = null
    ): Result<Boolean>

    /**
     * Find available time slots for a given date.
     */
    suspend fun findAvailableTimeSlots(
        date: String,
        durationMinutes: Int,
        startTime: String? = null,
        endTime: String? = null
    ): Result<List<String>>

    // ─── Sync ──────────────────────────────────────────────────────

    /**
     * Sync appointments with external calendar.
     */
    suspend fun syncWithExternalCalendar(accessToken: String): Result<Unit>
}
