/*
 * Copyright © 2026 Sphere Solution Developers
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */

package com.orbits.core.network.api

import retrofit2.http.GET
import retrofit2.http.Query

@kotlinx.serialization.Serializable
data class ProductivityStats(
    val tasksCompleted: Int,
    val tasksCreated: Int,
    val tasksOverdue: Int,
    val meetingsAttended: Int,
    val totalFocusHours: Double,
    val productivityScore: Int,
    val completionRate: Double,
    val trend: String // "up", "down", "stable"
)

@kotlinx.serialization.Serializable
data class TaskCompletionTrend(
    val labels: List<String>, // dates
    val completed: List<Int>,
    val created: List<Int>
)

@kotlinx.serialization.Serializable
data class CategoryBreakdown(
    val category: String,
    val count: Int,
    val percentage: Double
)

@kotlinx.serialization.Serializable
data class ReportRequest(
    val startDate: String,
    val endDate: String,
    val type: String // "daily", "weekly", "monthly"
)

interface AnalyticsApi {

    @GET("api/v1/analytics/productivity")
    suspend fun getProductivityStats(
        @Query("startDate") startDate: String,
        @Query("endDate") endDate: String
    ): ProductivityStats

    @GET("api/v1/analytics/trend")
    suspend fun getTaskCompletionTrend(
        @Query("startDate") startDate: String,
        @Query("endDate") endDate: String
    ): TaskCompletionTrend

    @GET("api/v1/analytics/categories")
    suspend fun getCategoryBreakdown(
        @Query("startDate") startDate: String,
        @Query("endDate") endDate: String
    ): List<CategoryBreakdown>

    @POST("api/v1/analytics/report")
    suspend fun generateReport(
        @Body request: ReportRequest
    ): String // URL to generated report
}