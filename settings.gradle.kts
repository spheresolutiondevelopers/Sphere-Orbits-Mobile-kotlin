/*
 * Copyright © 2026 Sphere Solution Developers
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */

pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
    plugins {
        id("com.android.application") version "9.3.1"
        id("com.android.library") version "9.3.1"
        id("com.google.dagger.hilt.android") version "2.60.1"
        id("com.google.devtools.ksp") version "2.3.11"
        id("org.jetbrains.kotlin.kapt") version "2.4.10"
        id("org.jetbrains.kotlin.plugin.serialization") version "2.4.10"
        id("org.jetbrains.kotlin.plugin.compose") version "2.4.10"
    }
}
plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "sphere-android"

// ─── Application ───────────────────────────────────────────────
include(":app")

// ─── Build Logic ──────────────────────────────────────────────
includeBuild("build-logic")

// ─── Core Infrastructure ──────────────────────────────────────
include(
    ":core:common",
    ":core:model",
    ":core:network",
    ":core:database",
    ":core:sync",
    ":core:testing",
    ":core:theme"
)

// ─── Data Layer (Decentralized) ──────────────────────────────
include(
    ":core:data:auth",
    ":core:data:tasks",
    ":core:data:calendar",
    ":core:data:events",
    ":core:data:meetings",
    ":core:data:appointments",
    ":core:data:chat",
    ":core:data:notes",
    ":core:data:analytics",
    ":core:data:settings"
)

// ─── Domain Layer (Decentralized) ─────────────────────────────
include(
    ":core:domain:auth",
    ":core:domain:tasks",
    ":core:domain:calendar",
    ":core:domain:events",
    ":core:domain:meetings",
    ":core:domain:appointments",
    ":core:domain:chat",
    ":core:domain:notes",
    ":core:domain:analytics",
    ":core:domain:settings"
)

// ─── Feature Layer (Decentralized) ────────────────────────────
include(
    ":feature:auth",
    ":feature:dashboard",
    ":feature:tasks",
    ":feature:calendar",
    ":feature:events",
    ":feature:meetings",
    ":feature:appointments",
    ":feature:chat",
    ":feature:notes",
    ":feature:analytics",
    ":feature:settings"
)