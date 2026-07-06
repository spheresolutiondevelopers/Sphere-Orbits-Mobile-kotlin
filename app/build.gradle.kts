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
    id("sphere.android.application")  // Convention plugin from build-logic
}

android {
    namespace = "com.orbits.app"
    defaultConfig {
        applicationId = "com.orbits.app"
        versionCode = 1
        versionName = "1.0.0"
    }
}

// ─── Dependencies ──────────────────────────────────────────────

dependencies {
    // ─── Core Modules ──────────────────────────────────────────
    implementation(project(":core:common"))
    implementation(project(":core:model"))
    implementation(project(":core:network"))
    implementation(project(":core:database"))
    implementation(project(":core:sync"))

    // ─── Domain Modules ────────────────────────────────────────
    implementation(project(":core:domain:auth"))
    implementation(project(":core:domain:tasks"))
    implementation(project(":core:domain:calendar"))
    implementation(project(":core:domain:events"))
    implementation(project(":core:domain:meetings"))
    implementation(project(":core:domain:appointments"))
    implementation(project(":core:domain:chat"))
    implementation(project(":core:domain:notes"))
    implementation(project(":core:domain:analytics"))
    implementation(project(":core:domain:settings"))

    // ─── Feature Modules ───────────────────────────────────────
    implementation(project(":feature:auth"))
    implementation(project(":feature:dashboard"))
    implementation(project(":feature:tasks"))
    implementation(project(":feature:calendar"))
    implementation(project(":feature:events"))
    implementation(project(":feature:meetings"))
    implementation(project(":feature:appointments"))
    implementation(project(":feature:chat"))
    implementation(project(":feature:notes"))
    implementation(project(":feature:analytics"))
    implementation(project(":feature:settings"))

    // ─── Splash Screen ─────────────────────────────────────────
    implementation("androidx.core:core-splashscreen:1.0.1")
}