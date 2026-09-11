/*
 * Copyright © 2026 Sphere Solution Developers
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */

package com.orbits.core.database

import androidx.room.TypeConverter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId

/**
 * Room type converters for common data types.
 */
class DatabaseConverters {

    private val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    // ─── String <-> List<String> ──────────────────────────────────

    @TypeConverter
    fun fromStringList(value: List<String>?): String? {
        return if (value == null || value.isEmpty()) null else {
            moshi.adapter<List<String>>(Types.newParameterizedType(List::class.java, String::class.java))
                .toJson(value)
        }
    }

    @TypeConverter
    fun toStringList(value: String?): List<String>? {
        return if (value == null || value.isEmpty()) null else {
            moshi.adapter<List<String>>(Types.newParameterizedType(List::class.java, String::class.java))
                .fromJson(value)
        }
    }

    // ─── String <-> Map<String, String> ──────────────────────────

    @TypeConverter
    fun fromStringMap(value: Map<String, String>?): String? {
        return if (value == null || value.isEmpty()) null else {
            moshi.adapter<Map<String, String>>(Types.newParameterizedType(Map::class.java, String::class.java, String::class.java))
                .toJson(value)
        }
    }

    @TypeConverter
    fun toStringMap(value: String?): Map<String, String>? {
        return if (value == null || value.isEmpty()) null else {
            moshi.adapter<Map<String, String>>(Types.newParameterizedType(Map::class.java, String::class.java, String::class.java))
                .fromJson(value)
        }
    }

    // ─── String <-> List<Map<String, String>> ─────────────────────

    @TypeConverter
    fun fromListOfMaps(value: List<Map<String, String>>?): String? {
        if (value == null || value.isEmpty()) return null
        val type = Types.newParameterizedType(
            List::class.java,
            Types.newParameterizedType(Map::class.java, String::class.java, String::class.java)
        )
        return moshi.adapter<List<Map<String, String>>>(type).toJson(value)
    }

    @TypeConverter
    fun toListOfMaps(value: String?): List<Map<String, String>>? {
        if (value == null || value.isEmpty()) return null
        val type = Types.newParameterizedType(
            List::class.java,
            Types.newParameterizedType(Map::class.java, String::class.java, String::class.java)
        )
        return moshi.adapter<List<Map<String, String>>>(type).fromJson(value)
    }

    // ─── Instant <-> String ──────────────────────────────────────

    @TypeConverter
    fun fromInstant(value: Instant?): String? {
        return value?.toString()
    }

    @TypeConverter
    fun toInstant(value: String?): Instant? {
        return value?.let { Instant.parse(it) }
    }

    // ─── LocalDateTime <-> String ──────────────────────────────

    @TypeConverter
    fun fromLocalDateTime(value: LocalDateTime?): String? {
        return value?.atZone(ZoneId.systemDefault())?.toInstant()?.toString()
    }

    @TypeConverter
    fun toLocalDateTime(value: String?): LocalDateTime? {
        return value?.let {
            Instant.parse(it).atZone(ZoneId.systemDefault()).toLocalDateTime()
        }
    }

    // ─── Boolean <-> Int ─────────────────────────────────────────

    @TypeConverter
    fun fromBoolean(value: Boolean): Int = if (value) 1 else 0

    @TypeConverter
    fun toBoolean(value: Int): Boolean = value == 1
}