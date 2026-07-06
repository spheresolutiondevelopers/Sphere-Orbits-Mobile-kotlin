/*
 * Copyright © 2026 Sphere Solution Developers
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */

package com.orbits.core.model

import kotlinx.serialization.Serializable

/**
 * Generic pagination wrapper for list responses.
 */
@Serializable
data class PaginatedResponse<T>(
    val items: List<T>,
    val total: Int,
    val page: Int,
    val pageSize: Int,
    val totalPages: Int,
    val hasNext: Boolean,
    val hasPrevious: Boolean
)

/**
 * Pagination request parameters.
 */
@Serializable
data class PaginationRequest(
    val page: Int = 1,
    val pageSize: Int = 20,
    val sortBy: String? = null,
    val sortDesc: Boolean = false
)

/**
 * Create a paginated response helper.
 */
fun <T> PaginatedResponse(
    items: List<T>,
    total: Int,
    page: Int,
    pageSize: Int
): PaginatedResponse<T> {
    val totalPages = if (pageSize > 0) (total + pageSize - 1) / pageSize else 0
    return PaginatedResponse(
        items = items,
        total = total,
        page = page,
        pageSize = pageSize,
        totalPages = totalPages,
        hasNext = page < totalPages,
        hasPrevious = page > 1
    )
}