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
    `kotlin-dsl`
}

repositories {
    gradlePluginPortal()
    google()
    mavenCentral()
}

dependencies {
    implementation(libs.android.gradlePlugin)
    implementation(libs.kt.gradlePlugin)
    implementation(libs.kt.serializationPlugin)
    implementation(libs.kt.composePlugin)
    implementation(libs.hilt.gradlePlugin)
    implementation("com.google.devtools.ksp:com.google.devtools.ksp.gradle.plugin:2.3.11")
}

gradlePlugin {
    plugins {
        register("androidApplication") {
            id = "sphere.android.application"
            implementationClass = "AndroidApplicationConvention"
        }
        register("androidLibrary") {
            id = "sphere.android.library"
            implementationClass = "AndroidLibraryConvention"
        }
        register("androidFeature") {
            id = "sphere.android.feature"
            implementationClass = "AndroidFeatureConvention"
        }
        register("hilt") {
            id = "sphere.android.hilt"
            implementationClass = "HiltConvention"
        }
    }
}
