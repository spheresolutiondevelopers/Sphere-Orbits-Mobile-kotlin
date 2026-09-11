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

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
            excludes += "/META-INF/DEPENDENCIES"
            excludes += "/META-INF/LICENSE*"
            excludes += "/META-INF/NOTICE*"
            excludes += "/META-INF/*.kotlin_module"
            excludes += "/META-INF/io.netty.versions.properties"
            excludes += "/META-INF/INDEX.LIST"
        }
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
    implementation(project(":core:theme"))

    // WorkManager & Hilt Work
    implementation(libs.workmanager)
    implementation(libs.hilt.work)
    ksp(libs.hilt.work.compiler)
    ksp(libs.hilt.compiler)

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
    implementation(project(":feature:notes")) {
        exclude(group = "com.atlassian.commonmark")
    }
    implementation(project(":feature:analytics"))
    implementation(project(":feature:settings"))

    // ─── Splash Screen ─────────────────────────────────────────
    implementation("androidx.core:core-splashscreen:1.0.1")
}

configurations.all {
    exclude(group = "com.atlassian.commonmark")
}
