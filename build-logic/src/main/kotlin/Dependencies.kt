/*
 * Copyright © 2026 Sphere Solution Developers
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */

import org.gradle.api.artifacts.dsl.DependencyHandler

/**
 * Centralized dependency helpers.
 * These methods are used by convention plugins to add common dependencies
 * without repeating the same configurations across modules.
 */
object Dependencies {

    // ─── Kotlin ──────────────────────────────────────────────────────
    fun DependencyHandler.kotlin() {
        add("implementation", libs.kotlin.stdlib)
        add("implementation", libs.kotlin.coroutines)
        add("implementation", libs.kotlin.serialization)
    }

    fun DependencyHandler.kotlinTest() {
        add("testImplementation", libs.kotlin.coroutines.test)
        add("testImplementation", libs.junit)
        add("testImplementation", libs.mockito.core)
        add("testImplementation", libs.mockito.kotlin)
    }

    // ─── Compose ──────────────────────────────────────────────────────
    fun DependencyHandler.compose() {
        val bom = libs.compose.bom
        add("implementation", platform(bom))
        add("implementation", libs.compose.ui)
        add("implementation", libs.compose.ui.graphics)
        add("implementation", libs.compose.ui.tooling.preview)
        add("implementation", libs.compose.material3)
        add("implementation", libs.compose.material.icons)
        add("implementation", libs.androidx.activity)
        add("implementation", libs.navigation.compose)
        add("implementation", libs.androidx.lifecycle.viewmodel)
    }

    fun DependencyHandler.composeDebug() {
        add("debugImplementation", libs.compose.ui.tooling)
    }

    fun DependencyHandler.composeTest() {
        add("testImplementation", libs.compose.ui.test)
    }

    // ─── Hilt ──────────────────────────────────────────────────────────
    fun DependencyHandler.hilt() {
        add("implementation", libs.hilt.android)
        add("implementation", libs.hilt.navigation.compose)
        add("kapt", libs.hilt.compiler)
    }

    fun DependencyHandler.hiltTest() {
        add("testImplementation", libs.hilt.android)
        add("testImplementation", libs.hilt.navigation.compose)
        add("kaptTest", libs.hilt.compiler)
    }

    // ─── Networking ──────────────────────────────────────────────────
    fun DependencyHandler.network() {
        add("implementation", libs.retrofit)
        add("implementation", libs.retrofit.moshi)
        add("implementation", libs.retrofit.scalars)
        add("implementation", libs.okhttp)
        add("implementation", libs.okhttp.logging)
        add("implementation", libs.moshi)
        add("implementation", libs.moshi.kotlin)
        add("implementation", libs.moshi.adapters)
    }

    // ─── Database ─────────────────────────────────────────────────────
    fun DependencyHandler.room() {
        add("implementation", libs.room.runtime)
        add("implementation", libs.room.ktx)
        add("kapt", libs.room.compiler)
    }

    fun DependencyHandler.roomTest() {
        add("testImplementation", libs.room.runtime)
        add("testImplementation", libs.room.ktx)
        add("kaptTest", libs.room.compiler)
    }

    // ─── DataStore ────────────────────────────────────────────────────
    fun DependencyHandler.datastore() {
        add("implementation", libs.datastore.preferences)
        add("implementation", libs.datastore.core)
    }

    // ─── WorkManager ──────────────────────────────────────────────────
    fun DependencyHandler.workmanager() {
        add("implementation", libs.workmanager)
    }

    // ─── Logging ──────────────────────────────────────────────────────
    fun DependencyHandler.timber() {
        add("implementation", libs.timber)
    }

    // ─── Common Core Dependencies ────────────────────────────────────
    fun DependencyHandler.coreCommon() {
        kotlin()
        timber()
        add("implementation", libs.androidx.core)
        add("implementation", libs.androidx.lifecycle.runtime)
    }

    fun DependencyHandler.coreCommonTest() {
        kotlinTest()
        add("testImplementation", libs.junit.ext)
        add("testImplementation", libs.espresso.core)
    }

    // ─── Feature Dependencies ────────────────────────────────────────
    fun DependencyHandler.featureDependencies() {
        kotlin()
        compose()
        hilt()
        timber()
        add("implementation", libs.androidx.core)
        add("implementation", libs.androidx.lifecycle.runtime)
        add("implementation", libs.androidx.lifecycle.viewmodel)
    }

    fun DependencyHandler.featureTest() {
        composeTest()
        kotlinTest()
        hiltTest()
    }

    // ─── Helper to get libs in Kotlin DSL ───────────────────────────
    private val libs: Libs by lazy { Libs() }

    class Libs {
        val compose = object {
            val bom get() = "androidx.compose:compose-bom:2024.10.00"
        }
        val kotlin = object {
            val stdlib get() = "org.jetbrains.kotlin:kotlin-stdlib:1.9.20"
            val coroutines get() = "org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3"
            val serialization get() = "org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.3"
            val coroutinesTest get() = "org.jetbrains.kotlinx:kotlinx-coroutines-test:1.7.3"
        }
        val android = object {
            val gradlePlugin get() = "com.android.tools.build:gradle:8.2.0"
            val core get() = "androidx.core:core-ktx:1.12.0"
            val lifecycle get() = object {
                val runtime get() = "androidx.lifecycle:lifecycle-runtime-ktx:2.7.0"
                val viewmodel get() = "androidx.lifecycle:lifecycle-viewmodel-ktx:2.7.0"
                val livedata get() = "androidx.lifecycle:lifecycle-livedata-ktx:2.7.0"
            }
            val activity get() = "androidx.activity:activity-compose:1.8.2"
            val appcompat get() = "androidx.appcompat:appcompat:1.6.1"
            val constraintlayout get() = "androidx.constraintlayout:constraintlayout:2.1.4"
        }
        val compose = object {
            val ui get() = "androidx.compose.ui:ui"
            val uiGraphics get() = "androidx.compose.ui:ui-graphics"
            val uiTooling get() = "androidx.compose.ui:ui-tooling"
            val uiToolingPreview get() = "androidx.compose.ui:ui-tooling-preview"
            val material3 get() = "androidx.compose.material3:material3"
            val materialIcons get() = "androidx.compose.material:material-icons-extended"
            val uiTest get() = "androidx.compose.ui:ui-test-junit4"
        }
        val navigation = object {
            val compose get() = "androidx.navigation:navigation-compose:2.7.7"
        }
        val hilt = object {
            val android get() = "com.google.dagger:hilt-android:2.48"
            val compiler get() = "com.google.dagger:hilt-compiler:2.48"
            val navigationCompose get() = "androidx.hilt:hilt-navigation-compose:1.2.0"
            val gradlePlugin get() = "com.google.dagger:hilt-android-gradle-plugin:2.48"
        }
        val network = object {
            val retrofit get() = "com.squareup.retrofit2:retrofit:2.9.0"
            val moshi get() = "com.squareup.retrofit2:converter-moshi:2.9.0"
            val scalars get() = "com.squareup.retrofit2:converter-scalars:2.9.0"
        }
        val okhttp = object {
            val core get() = "com.squareup.okhttp3:okhttp:4.12.0"
            val logging get() = "com.squareup.okhttp3:logging-interceptor:4.12.0"
        }
        val moshi = object {
            val core get() = "com.squareup.moshi:moshi:1.15.0"
            val kotlin get() = "com.squareup.moshi:moshi-kotlin:1.15.0"
            val adapters get() = "com.squareup.moshi:moshi-adapters:1.15.0"
        }
        val room = object {
            val runtime get() = "androidx.room:room-runtime:2.6.1"
            val ktx get() = "androidx.room:room-ktx:2.6.1"
            val compiler get() = "androidx.room:room-compiler:2.6.1"
        }
        val datastore = object {
            val preferences get() = "androidx.datastore:datastore-preferences:1.0.0"
            val core get() = "androidx.datastore:datastore-core:1.0.0"
        }
        val workmanager get() = "androidx.work:work-runtime-ktx:2.9.0"
        val timber get() = "com.jakewharton.timber:timber:5.0.1"
        val testing = object {
            val junit get() = "junit:junit:4.13.2"
            val junitExt get() = "androidx.test.ext:junit:1.1.5"
            val espresso get() = "androidx.test.espresso:espresso-core:3.5.1"
            val mockito get() = "org.mockito:mockito-core:5.11.0"
            val mockitoKotlin get() = "org.mockito.kotlin:mockito-kotlin:5.2.1"
        }
    }
}