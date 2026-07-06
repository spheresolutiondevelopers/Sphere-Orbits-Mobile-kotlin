/*
 * Copyright © 2026 Sphere Solution Developers
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */

package com.orbits.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.orbits.data.appointments.AppointmentEntity
import com.orbits.data.appointments.ParticipantEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AppointmentDao {

    // ─── Appointment Queries ─────────────────────────────────────

    @Query("SELECT * FROM appointments WHERE id = :appointmentId AND is_deleted = 0")
    suspend fun getAppointment(appointmentId: String): AppointmentEntity?

    @Query("""
        SELECT * FROM appointments 
        WHERE user_id = :userId AND is_deleted = 0
        ORDER BY start_datetime DESC
    """)
    fun getAppointmentsForUser(userId: String): Flow<List<AppointmentEntity>>

    @Query("""
        SELECT * FROM appointments 
        WHERE user_id = :userId 
          AND is_deleted = 0 
          AND start_datetime BETWEEN :startDate AND :endDate
        ORDER BY start_datetime ASC
    """)
    fun getAppointmentsInDateRange(
        userId: String,
        startDate: String,
        endDate: String
    ): Flow<List<AppointmentEntity>>

    @Query("""
        SELECT * FROM appointments 
        WHERE user_id = :userId 
          AND is_deleted = 0 
          AND status = :status
        ORDER BY start_datetime ASC
    """)
    fun getAppointmentsByStatus(userId: String, status: String): Flow<List<AppointmentEntity>>

    @Query("""
        SELECT * FROM appointments 
        WHERE user_id = :userId 
          AND is_deleted = 0 
          AND date(start_datetime) = date(:date)
        ORDER BY start_datetime ASC
    """)
    fun getAppointmentsForDate(userId: String, date: String): Flow<List<AppointmentEntity>>

    @Query("""
        SELECT * FROM appointments 
        WHERE user_id = :userId 
          AND is_deleted = 0 
          AND appointment_type = :type
        ORDER BY start_datetime ASC
    """)
    fun getAppointmentsByType(userId: String, type: String): Flow<List<AppointmentEntity>>

    @Query("SELECT * FROM appointments WHERE external_event_id = :externalId AND user_id = :userId")
    suspend fun getAppointmentByExternalId(userId: String, externalId: String): AppointmentEntity?

    // ─── Appointment CRUD ────────────────────────────────────────

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAppointment(appointment: AppointmentEntity)

    @Update
    suspend fun updateAppointment(appointment: AppointmentEntity)

    @Query("""
        UPDATE appointments 
        SET status = :status, updated_at = datetime('now') 
        WHERE id = :appointmentId
    """)
    suspend fun updateAppointmentStatus(appointmentId: String, status: String)

    @Query("UPDATE appointments SET is_deleted = 1, updated_at = datetime('now') WHERE id = :appointmentId")
    suspend fun softDeleteAppointment(appointmentId: String)

    @Query("DELETE FROM appointments WHERE id = :appointmentId")
    suspend fun permanentlyDeleteAppointment(appointmentId: String)

    @Query("""
        UPDATE appointments 
        SET synced_at = datetime('now') 
        WHERE id = :appointmentId
    """)
    suspend fun markAppointmentSynced(appointmentId: String)

    @Query("""
        SELECT * FROM appointments 
        WHERE synced_at IS NULL OR synced_at < updated_at
    """)
    suspend fun getUnsyncedAppointments(): List<AppointmentEntity>

    @Query("""
        UPDATE appointments 
        SET external_sync_status = :status, 
            updated_at = datetime('now') 
        WHERE id = :appointmentId
    """)
    suspend fun updateSyncStatus(appointmentId: String, status: String)

    // ─── Participants ────────────────────────────────────────────

    @Query("SELECT * FROM participants WHERE appointment_id = :appointmentId")
    suspend fun getParticipantsForAppointment(appointmentId: String): List<ParticipantEntity>

    @Query("""
        SELECT * FROM participants 
        WHERE appointment_id = :appointmentId AND invitation_status = 'accepted'
    """)
    suspend fun getAcceptedParticipantsForAppointment(appointmentId: String): List<ParticipantEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertParticipant(participant: ParticipantEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertParticipants(participants: List<ParticipantEntity>)

    @Query("""
        UPDATE participants 
        SET invitation_status = :status, 
            updated_at = datetime('now') 
        WHERE appointment_id = :appointmentId AND email = :email
    """)
    suspend fun updateParticipantStatus(appointmentId: String, email: String, status: String)

    @Query("DELETE FROM participants WHERE appointment_id = :appointmentId")
    suspend fun deleteAllParticipantsForAppointment(appointmentId: String)
}