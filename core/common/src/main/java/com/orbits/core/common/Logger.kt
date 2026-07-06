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

import timber.log.Timber

/**
 * Application-wide logger wrapper.
 * Use this instead of direct Timber calls to allow for future logging customization.
 */
object Logger {

    fun v(tag: String? = null, message: String) {
        if (tag != null) Timber.tag(tag).v(message) else Timber.v(message)
    }

    fun d(tag: String? = null, message: String) {
        if (tag != null) Timber.tag(tag).d(message) else Timber.d(message)
    }

    fun i(tag: String? = null, message: String) {
        if (tag != null) Timber.tag(tag).i(message) else Timber.i(message)
    }

    fun w(tag: String? = null, message: String) {
        if (tag != null) Timber.tag(tag).w(message) else Timber.w(message)
    }

    fun e(tag: String? = null, message: String, throwable: Throwable? = null) {
        if (tag != null) {
            if (throwable != null) Timber.tag(tag).e(throwable, message) else Timber.tag(tag).e(message)
        } else {
            if (throwable != null) Timber.e(throwable, message) else Timber.e(message)
        }
    }

    fun e(tag: String? = null, throwable: Throwable) {
        if (tag != null) Timber.tag(tag).e(throwable) else Timber.e(throwable)
    }

    fun wtf(tag: String? = null, message: String, throwable: Throwable? = null) {
        if (tag != null) {
            if (throwable != null) Timber.tag(tag).wtf(throwable, message) else Timber.tag(tag).wtf(message)
        } else {
            if (throwable != null) Timber.wtf(throwable, message) else Timber.wtf(message)
        }
    }
}