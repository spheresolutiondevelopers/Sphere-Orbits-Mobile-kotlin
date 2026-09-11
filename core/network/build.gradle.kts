/*
 * Copyright © 2026 Sphere Solution Developers
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */

plugins {
    id("sphere.android.library")
}

android {
    namespace = "com.orbits.core.network"

    buildFeatures {
        buildConfig = true
    }

    buildTypes {
        getByName("debug") {
            buildConfigField("String", "API_BASE_URL", "\"http://sphereschedule.runasp.net/\"")
        }
        getByName("release") {
            buildConfigField("String", "API_BASE_URL", "\"http://sphereschedule.runasp.net/\"")
        }
    }
}

dependencies {
    // Networking
    implementation(libs.bundles.network)
    implementation(libs.retrofit.scalars)
    implementation(libs.moshi)
    implementation(libs.moshi.kotlin)
    implementation(libs.moshi.adapters)

    // Internal modules
    implementation(project(":core:common"))
    implementation(project(":core:model"))

    // Testing
    testImplementation(libs.okhttp.mockwebserver)
}