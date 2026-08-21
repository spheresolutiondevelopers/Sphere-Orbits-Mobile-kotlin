package com.orbits.data.appointments

import com.orbits.core.common.Result
import com.orbits.core.common.Logger
import com.orbits.core.common.TokenProvider
import com.orbits.core.common.extensions.nowUtc
import com.orbits.core.database.dao.AppointmentDao
import com.orbits.core.database.dao.SyncQueueDao
import com.orbits.core.network.api.AppointmentApi
import com.orbits.core.network.error.ApiErrorParser
import com.orbits.data.sync.SyncQueueEntity
import com.orbits.data.appointments.local.AppointmentEntity
import com.orbits.data.appointments.local.ParticipantEntity
import com.orbits.data.appointments.mappers.AppointmentMapper
import com.orbits.data.appointments.mappers.ParticipantMapper
import com.orbits.domain.appointments.Appointment
import com.orbits.domain.appointments.AppointmentParticipant
import com.orbits.domain.appointments.AppointmentRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class AppointmentRepositoryImpl @Inject constructor(
    private val appointmentDao: AppointmentDao,
    private val syncQueueDao: SyncQueueDao,
    private val appointmentApi: AppointmentApi,
    private val appointmentMapper: AppointmentMapper,
    private val participantMapper: ParticipantMapper,
    private val tokenProvider: TokenProvider
) : AppointmentRepository {

    companion object {
        private const val TAG = "AppointmentRepository"
    }

    // ─── Read Operations ──────────────────────────────────────────

    override fun getAppointments(): Flow<List<Appointment>> {
        return appointmentDao.getAppointmentsForUser(getUserId())
            .map { entities -> appointmentMapper.toDomainList(entities) }
    }

    override fun getAppointmentsInDateRange(startDate: String, endDate: String): Flow<List<Appointment>> {
        return appointmentDao.getAppointmentsInDateRange(getUserId(), startDate, endDate)
            .map { entities -> appointmentMapper.toDomainList(entities) }
    }

    override fun getAppointmentsByStatus(status: String): Flow<List<Appointment>> {
        return appointmentDao.getAppointmentsByStatus(getUserId(), status)
            .map { entities -> appointmentMapper.toDomainList(entities) }
    }

    override fun getAppointmentsByType(type: String): Flow<List<Appointment>> {
        return appointmentDao.getAppointmentsByType(getUserId(), type)
            .map { entities -> appointmentMapper.toDomainList(entities) }
    }

    override fun getAppointmentsForDate(date: String): Flow<List<Appointment>> {
        return appointmentDao.getAppointmentsForDate(getUserId(), date)
            .map { entities -> appointmentMapper.toDomainList(entities) }
    }

    override suspend fun getAppointment(appointmentId: String): Result<Appointment> {
        return try {
            val entity = appointmentDao.getAppointment(appointmentId)
            if (entity != null) {
                Result.Success(appointmentMapper.toDomain(entity))
            } else {
                Result.Error(IllegalStateException("Appointment not found: $appointmentId"))
            }
        } catch (e: Exception) {
            Logger.e(TAG, "Error fetching appointment $appointmentId", e)
            Result.Error(e)
        }
    }

    override suspend fun getAppointmentByExternalId(externalId: String): Result<Appointment> {
        return try {
            val entity = appointmentDao.getAppointmentByExternalId(getUserId(), externalId)
            if (entity != null) {
                Result.Success(appointmentMapper.toDomain(entity))
            } else {
                Result.Error(IllegalStateException("Appointment not found for external ID: $externalId"))
            }
        } catch (e: Exception) {
            Logger.e(TAG, "Error fetching appointment by external ID $externalId", e)
            Result.Error(e)
        }
    }

    // ─── Participants ─────────────────────────────────────────────

    override suspend fun getParticipants(appointmentId: String): Result<List<AppointmentParticipant>> {
        return try {
            val entities = appointmentDao.getParticipantsForAppointment(appointmentId)
            Result.Success(participantMapper.toDomainList(entities))
        } catch (e: Exception) {
            Logger.e(TAG, "Error fetching participants for appointment $appointmentId", e)
            Result.Error(e)
        }
    }

    override suspend fun getAcceptedParticipants(appointmentId: String): Result<List<AppointmentParticipant>> {
        return try {
            val entities = appointmentDao.getAcceptedParticipantsForAppointment(appointmentId)
            Result.Success(participantMapper.toDomainList(entities))
        } catch (e: Exception) {
            Logger.e(TAG, "Error fetching accepted participants for appointment $appointmentId", e)
            Result.Error(e)
        }
    }

    override suspend fun addParticipant(appointmentId: String, participant: AppointmentParticipant): Result<AppointmentParticipant> {
        return try {
            val entity = participantMapper.toEntity(participant)
            appointmentDao.insertParticipant(entity)

            enqueueSync(SyncQueueEntity(
                entityType = "appointment_participant",
                operation = "create",
                entityId = participant.id,
                payloadJson = """{"appointmentId":"$appointmentId","email":"${participant.email}"}"""
            ))

            Logger.d(TAG, "Participant added to appointment $appointmentId")
            Result.Success(participant)
        } catch (e: Exception) {
            Logger.e(TAG, "Error adding participant to appointment $appointmentId", e)
            Result.Error(e)
        }
    }

    override suspend fun addParticipants(appointmentId: String, participants: List<AppointmentParticipant>): Result<List<AppointmentParticipant>> {
        return try {
            val entities = participants.map { participantMapper.toEntity(it) }
            appointmentDao.insertParticipants(entities)

            participants.forEach { participant ->
                enqueueSync(SyncQueueEntity(
                    entityType = "appointment_participant",
                    operation = "create",
                    entityId = participant.id,
                    payloadJson = """{"appointmentId":"$appointmentId","email":"${participant.email}"}"""
                ))
            }

            Logger.d(TAG, "${participants.size} participants added to appointment $appointmentId")
            Result.Success(participants)
        } catch (e: Exception) {
            Logger.e(TAG, "Error adding participants to appointment $appointmentId", e)
            Result.Error(e)
        }
    }

    override suspend fun updateParticipantStatus(
        appointmentId: String,
        email: String,
        status: String
    ): Result<Unit> {
        return try {
            appointmentDao.updateParticipantStatus(appointmentId, email, status)

            enqueueSync(SyncQueueEntity(
                entityType = "appointment_participant",
                operation = "update",
                entityId = email,
                payloadJson = """{"invitationStatus":"$status"}"""
            ))

            Logger.d(TAG, "Participant $email status updated to $status")
            Result.Success(Unit)
        } catch (e: Exception) {
            Logger.e(TAG, "Error updating participant status $email", e)
            Result.Error(e)
        }
    }

    override suspend fun removeParticipant(appointmentId: String, participantId: String): Result<Unit> {
        return try {
            enqueueSync(SyncQueueEntity(
                entityType = "appointment_participant",
                operation = "delete",
                entityId = participantId,
                payloadJson = null
            ))
            Result.Success(Unit)
        } catch (e: Exception) {
            Logger.e(TAG, "Error removing participant $participantId", e)
            Result.Error(e)
        }
    }

    // ─── Write Operations ─────────────────────────────────────────

    override suspend fun createAppointment(appointment: Appointment): Result<Appointment> {
        return try {
            validateAppointment(appointment)

            val entity = appointmentMapper.toEntity(appointment)
            appointmentDao.insertAppointment(entity)

            // Add participants if any
            // Participants are handled separately

            enqueueSync(SyncQueueEntity(
                entityType = "appointment",
                operation = "create",
                entityId = appointment.id,
                payloadJson = appointmentMapper.toCreateRequest(appointment).toString()
            ))

            Logger.d(TAG, "Appointment created locally: ${appointment.id}")
            Result.Success(appointment)
        } catch (e: Exception) {
            Logger.e(TAG, "Error creating appointment", e)
            Result.Error(e)
        }
    }

    override suspend fun updateAppointment(appointment: Appointment): Result<Appointment> {
        return try {
            validateAppointment(appointment)

            val existing = appointmentDao.getAppointment(appointment.id)
            if (existing == null) {
                return Result.Error(IllegalStateException("Appointment not found: ${appointment.id}"))
            }

            val entity = appointmentMapper.toEntity(appointment).copy(updatedAt = nowUtc())
            appointmentDao.updateAppointment(entity)

            enqueueSync(SyncQueueEntity(
                entityType = "appointment",
                operation = "update",
                entityId = appointment.id,
                payloadJson = appointmentMapper.toUpdateRequest(appointment).toString()
            ))

            Logger.d(TAG, "Appointment updated locally: ${appointment.id}")
            Result.Success(appointment)
        } catch (e: Exception) {
            Logger.e(TAG, "Error updating appointment ${appointment.id}", e)
            Result.Error(e)
        }
    }

    override suspend fun updateAppointmentStatus(appointmentId: String, status: String): Result<Appointment> {
        return try {
            appointmentDao.updateAppointmentStatus(appointmentId, status)

            val entity = appointmentDao.getAppointment(appointmentId)
            if (entity != null) {
                enqueueSync(SyncQueueEntity(
                    entityType = "appointment",
                    operation = "update",
                    entityId = appointmentId,
                    payloadJson = """{"status":"$status"}"""
                ))
                Result.Success(appointmentMapper.toDomain(entity))
            } else {
                Result.Error(IllegalStateException("Appointment not found: $appointmentId"))
            }
        } catch (e: Exception) {
            Logger.e(TAG, "Error updating appointment status $appointmentId", e)
            Result.Error(e)
        }
    }

    override suspend fun cancelAppointment(appointmentId: String): Result<Appointment> {
        return updateAppointmentStatus(appointmentId, "cancelled")
    }

    override suspend fun deleteAppointment(appointmentId: String): Result<Unit> {
        return try {
            appointmentDao.softDeleteAppointment(appointmentId)

            enqueueSync(SyncQueueEntity(
                entityType = "appointment",
                operation = "delete",
                entityId = appointmentId,
                payloadJson = null
            ))

            // Also delete participants
            appointmentDao.deleteAllParticipantsForAppointment(appointmentId)

            Logger.d(TAG, "Appointment deleted locally: $appointmentId")
            Result.Success(Unit)
        } catch (e: Exception) {
            Logger.e(TAG, "Error deleting appointment $appointmentId", e)
            Result.Error(e)
        }
    }

    override suspend fun restoreAppointment(appointmentId: String): Result<Appointment> {
        return try {
            // Restore is not directly supported in DAO, but we can update is_deleted flag
            // We'll need to add a restore method or use update
            // For now, we'll just reinsert
            Result.Error(UnsupportedOperationException("Restore not implemented"))
        } catch (e: Exception) {
            Logger.e(TAG, "Error restoring appointment $appointmentId", e)
            Result.Error(e)
        }
    }

    // ─── Availability ─────────────────────────────────────────────

    override suspend fun checkAvailability(
        startDateTime: String,
        endDateTime: String,
        ignoreAppointmentId: String?
    ): Result<Boolean> {
        return try {
            // Check for overlapping appointments
            val existingAppointments = appointmentDao.getAppointmentsInDateRange(
                getUserId(), startDateTime, endDateTime
            ).first()

            val hasConflict = existingAppointments.any { entity ->
                entity.id != ignoreAppointmentId &&
                entity.status != "cancelled" &&
                entity.status != "completed" &&
                (entity.startDateTime < endDateTime && entity.endDateTime > startDateTime)
            }

            Result.Success(!hasConflict)
        } catch (e: Exception) {
            Logger.e(TAG, "Error checking availability", e)
            Result.Error(e)
        }
    }

    override suspend fun findAvailableTimeSlots(
        date: String,
        durationMinutes: Int,
        startTime: String?,
        endTime: String?
    ): Result<List<String>> {
        return try {
            // For simplicity, just return the given time if available
            val startDateTime = "$date ${startTime ?: "09:00"}"
            val endDateTime = "$date ${endTime ?: "17:00"}"

            val isAvailable = checkAvailability(startDateTime, endDateTime)
            if (isAvailable is Result.Success && isAvailable.data) {
                Result.Success(listOf(startDateTime))
            } else {
                Result.Success(emptyList())
            }
        } catch (e: Exception) {
            Logger.e(TAG, "Error finding available time slots", e)
            Result.Error(e)
        }
    }

    // ─── Sync Operations ───────────────────────────────────────────

    override suspend fun syncWithExternalCalendar(accessToken: String): Result<Unit> {
        return try {
            // This would integrate with Google Calendar, Outlook, etc.
            // For now, just sync local appointments with the server
            val unsynced = appointmentDao.getUnsyncedAppointments()
            if (unsynced.isNotEmpty()) {
                unsynced.forEach { entity ->
                    // Push to server
                    // This is a simplified placeholder
                    val dto = appointmentMapper.toDto(entity)
                    // Would call appointmentApi.createAppointment(dto)
                }
            }
            Result.Success(Unit)
        } catch (e: Exception) {
            Logger.e(TAG, "Sync error", e)
            Result.Error(e)
        }
    }

    // ─── Private Helpers ──────────────────────────────────────────

    private fun getUserId(): String {
        // This should come from auth state
        return "test_user_id"
    }

    private suspend fun enqueueSync(item: SyncQueueEntity) {
        syncQueueDao.enqueue(item)
    }

    private fun validateAppointment(appointment: Appointment) {
        require(appointment.title.isNotBlank()) { "Appointment title cannot be empty" }
        require(appointment.title.length <= 255) { "Appointment title must be <= 255 characters" }
        require(appointment.appointmentType in listOf("general", "doctor", "business", "personal", "meeting")) {
            "Invalid appointment type"
        }
        require(appointment.status in listOf("scheduled", "confirmed", "cancelled", "completed", "rescheduled")) {
            "Invalid appointment status"
        }
        require(appointment.startDateTime < appointment.endDateTime) {
            "End time must be after start time"
        }
        require(appointment.reminderMinutesBefore in 0..1440) {
            "Reminder minutes must be 0-1440"
        }
        if (appointment.isVirtual && appointment.meetingLink != null) {
            require(appointment.meetingLink.startsWith("http")) {
                "Meeting link must be a valid URL"
            }
        }
        // Additional validation as needed
    }
}