/*
 * Copyright © 2026 Sphere Solution Developers
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */

import com.android.build.gradle.internal.dsl.BaseAppModuleExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.dsl.DependencyHandler
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.get
import org.gradle.kotlin.dsl.withType
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

/**
 * Convention plugin for all :core modules.
 * Configures library modules with Kotlin, Hilt, and core dependencies.
 *
 * Core modules are NOT allowed to have Compose or Android UI dependencies.
 * They are pure infrastructure layers.
 */
class AndroidLibraryConvention : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                apply("com.android.library")
                apply("org.jetbrains.kotlin.android")
                apply("com.google.dagger.hilt.android")
                apply("org.jetbrains.kotlin.kapt")
                apply("org.jetbrains.kotlin.plugin.serialization")
            }

            extensions.configure<BaseAppModuleExtension> {
                compileSdk = 34

                defaultConfig {
                    minSdk = 24
                    targetSdk = 34

                    testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
                }

                compileOptions {
                    sourceCompatibility = JavaVersion.VERSION_17
                    targetCompatibility = JavaVersion.VERSION_17
                }

                buildFeatures {
                    // No Compose for core modules
                    compose = false
                    buildConfig = false
                }
            }

            // ─── Dependencies ──────────────────────────────────────────
            dependencies {
                add("implementation", project.libs.findLibrary("androidx-core").get())
                add("implementation", project.libs.findLibrary("androidx-lifecycle-runtime").get())
                add("implementation", project.libs.findLibrary("kotlin-stdlib").get())
                add("implementation", project.libs.findLibrary("kotlin-coroutines").get())
                add("implementation", project.libs.findLibrary("kotlin-serialization").get())
                add("implementation", project.libs.findLibrary("timber").get())

                // Hilt
                add("implementation", project.libs.findLibrary("hilt-android").get())
                add("kapt", project.libs.findLibrary("hilt-compiler").get())

                // TEST
                add("testImplementation", project.libs.findLibrary("junit").get())
                add("testImplementation", project.libs.findLibrary("kotlin-coroutines-test").get())
                add("testImplementation", project.libs.findLibrary("mockito-core").get())
                add("testImplementation", project.libs.findLibrary("mockito-kotlin").get())

                add("androidTestImplementation", project.libs.findLibrary("junit-ext").get())
                add("androidTestImplementation", project.libs.findLibrary("espresso-core").get())
            }

            // ─── Kotlin Compiler ───────────────────────────────────────
            tasks.withType<KotlinCompile>().configureEach {
                kotlinOptions {
                    jvmTarget = "17"
                    freeCompilerArgs = listOf(
                        "-Xjsr305=strict",
                        "-opt-in=kotlin.RequiresOptIn"
                    )
                }
            }
        }
    }
}