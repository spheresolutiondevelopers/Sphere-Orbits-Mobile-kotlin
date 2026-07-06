/*
 * Copyright © 2026 Sphere Solution Developers
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */

package com.orbits.core.common

/**
 * Standard result wrapper for all operations.
 * Used throughout the app for network calls, database operations, and use cases.
 */
sealed class Result<out T> {
    data class Success<T>(val data: T) : Result<T>()
    data class Error(val exception: Throwable) : Result<Nothing>()
    object Loading : Result<Nothing>()
}

/**
 * Extension to map a Result to another type.
 */
fun <T, R> Result<T>.map(transform: (T) -> R): Result<R> {
    return when (this) {
        is Result.Success -> Result.Success(transform(data))
        is Result.Error -> Result.Error(exception)
        Result.Loading -> Result.Loading
    }
}

/**
 * Extension to fold a Result into a single value.
 */
fun <T, R> Result<T>.fold(
    onSuccess: (T) -> R,
    onError: (Throwable) -> R,
    onLoading: () -> R
): R {
    return when (this) {
        is Result.Success -> onSuccess(data)
        is Result.Error -> onError(exception)
        Result.Loading -> onLoading()
    }
}

/**
 * Check if Result is successful.
 */
fun <T> Result<T>.isSuccess(): Boolean = this is Result.Success

/**
 * Check if Result is an error.
 */
fun <T> Result<T>.isError(): Boolean = this is Result.Error

/**
 * Get data or null if not Success.
 */
fun <T> Result<T>.getOrNull(): T? = when (this) {
    is Result.Success -> data
    else -> null
}

/**
 * Get error or null if not Error.
 */
fun <T> Result<T>.errorOrNull(): Throwable? = when (this) {
    is Result.Error -> exception
    else -> null
}