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
    namespace = "com.orbits.core.theme"
}

dependencies {
    // Compose — required for MaterialTheme
    implementation(platform(libs.compose.bom))
    implementation(libs.compose.ui)
    implementation(libs.compose.ui.graphics)
    implementation(libs.compose.ui.tooling.preview)
    implementation(libs.compose.material3)
    implementation(libs.compose.material.icons)

    // DataStore — for theme preference persistence
    implementation(libs.datastore.preferences)
    implementation(libs.datastore.core)

    // Kotlin coroutines
    implementation(libs.kotlin.coroutines)

    // Testing
    testImplementation(libs.junit)
    testImplementation(libs.kotlin.coroutines.test)
}

// Compose compiler plugin is applied via the library convention plugin