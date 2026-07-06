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
    id("kotlin-kapt")
}

android {
    namespace = "com.orbits.core.database"
}

dependencies {
    // Kotlin
    implementation(libs.kotlin.stdlib)
    implementation(libs.kotlin.coroutines)

    // AndroidX
    implementation(libs.androidx.core)
    implementation(libs.androidx.lifecycle.runtime)

    // Room
    implementation(libs.room.runtime)
    implementation(libs.room.ktx)
    kapt(libs.room.compiler)

    // Hilt
    implementation(libs.hilt.android)
    kapt(libs.hilt.compiler)

    // Internal modules
    implementation(project(":core:common"))
    implementation(project(":core:model"))

    // All data modules (for Entity references in Database)
    implementation(project(":core:data:auth"))
    implementation(project(":core:data:tasks"))
    implementation(project(":core:data:calendar"))
    implementation(project(":core:data:events"))
    implementation(project(":core:data:meetings"))
    implementation(project(":core:data:appointments"))
    implementation(project(":core:data:chat"))
    implementation(project(":core:data:notes"))
    implementation(project(":core:data:analytics"))
    implementation(project(":core:data:settings"))

    // Testing
    testImplementation(libs.junit)
    testImplementation(libs.kotlin.coroutines.test)
    testImplementation(libs.mockito.core)
    testImplementation(libs.mockito.kotlin)
    testImplementation(libs.room.runtime) // for in-memory testing
}