/*
 * Copyright © 2026 Sphere Solution Developers
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */

package com.orbits.app

import android.app.Application
import androidx.hilt.work.HiltWorkerFactory
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber
import javax.inject.Inject

/**
 * Main Application class.
 * - Hilt entry point
 * - Initializes Timber logging
 * - Provides HiltWorkerFactory for WorkManager
 */
@HiltAndroidApp
class OrbitsApplication : Application() {

    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    override fun onCreate() {
        super.onCreate()

        // Initialize Timber logging
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        } else {
            // In production, plant a Crashlytics or custom tree
            // Timber.plant(CrashlyticsTree())
        }

        // Initialize WorkManager with Hilt factory
        // This is done automatically if you set the factory in the manifest
        // or via WorkManager initialization
    }
}