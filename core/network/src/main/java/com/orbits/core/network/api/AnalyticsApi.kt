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

import com.orbits.core.model.*
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface AnalyticsApi {

    @GET("api/v1/analytics/productivity")
    suspend fun getProductivityStats(
        @Query("startDate") startDate: String,
        @Query("endDate") endDate: String
    ): ProductivityStatsDto

    @GET("api/v1/analytics/trend")
    suspend fun getTaskCompletionTrend(
        @Query("startDate") startDate: String,
        @Query("endDate") endDate: String
    ): TaskCompletionTrendDto

    @GET("api/v1/analytics/categories")
    suspend fun getCategoryBreakdown(
        @Query("startDate") startDate: String,
        @Query("endDate") endDate: String
    ): List<CategoryBreakdownDto>

    @POST("api/v1/analytics/report")
    suspend fun generateReport(
        @Body request: AnalyticsReportRequest
    ): String // URL to generated report
}
