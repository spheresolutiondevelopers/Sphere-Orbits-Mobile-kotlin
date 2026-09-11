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
    id("com.google.devtools.ksp")
}

android {
    namespace = "com.orbits.core.database"
}

ksp {
    arg("room.schemaLocation", "$projectDir/schemas")
}

dependencies {
    // Kotlin
    implementation(libs.kt.stdlib)
    implementation(libs.kt.coroutines)

    // AndroidX
    implementation(libs.androidx.core)
    implementation(libs.androidx.lifecycle.runtime)

    // Moshi
    implementation(libs.moshi)
    implementation(libs.moshi.kotlin)

    // Room
    implementation(libs.room.runtime)
    implementation(libs.room.ktx)
    ksp(libs.room.compiler)

    // Hilt (already handled by convention plugin, but ksp dependency is here)
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)

    // Internal modules
    implementation(project(":core:common"))
    implementation(project(":core:model"))

    // Testing
    testImplementation(libs.bundles.test)
    testImplementation(libs.room.runtime) // for in-memory testing
}