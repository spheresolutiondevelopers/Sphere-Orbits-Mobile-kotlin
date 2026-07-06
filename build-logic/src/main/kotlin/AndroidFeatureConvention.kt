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
 * Convention plugin for all :feature modules.
 * Configures library modules with Compose, Hilt, and feature-specific dependencies.
 *
 * Each feature module automatically gets access to:
 *   - All Compose and Hilt dependencies
 *   - The core common module
 *   - Its corresponding domain module
 */
class AndroidFeatureConvention : Plugin<Project> {
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
                    compose = true
                    buildConfig = false
                }

                composeOptions {
                    kotlinCompilerExtensionVersion = "1.5.4"
                }
            }

            // ─── Dependencies ──────────────────────────────────────────
            dependencies {
                // Compose
                add("implementation", platform(project.libs.findLibrary("compose-bom").get()))
                add("implementation", project.libs.findLibrary("compose-ui").get())
                add("implementation", project.libs.findLibrary("compose-ui-graphics").get())
                add("implementation", project.libs.findLibrary("compose-ui-tooling-preview").get())
                add("implementation", project.libs.findLibrary("compose-material3").get())
                add("implementation", project.libs.findLibrary("compose-material-icons").get())
                add("implementation", project.libs.findLibrary("androidx-activity").get())
                add("implementation", project.libs.findLibrary("navigation-compose").get())
                add("implementation", project.libs.findLibrary("androidx-lifecycle-viewmodel").get())
                add("implementation", project.libs.findLibrary("androidx-lifecycle-runtime").get())

                // Hilt
                add("implementation", project.libs.findLibrary("hilt-android").get())
                add("implementation", project.libs.findLibrary("hilt-navigation-compose").get())
                add("kapt", project.libs.findLibrary("hilt-compiler").get())

                // Core
                add("implementation", project.libs.findLibrary("androidx-core").get())
                add("implementation", project.libs.findLibrary("timber").get())

                // Project dependencies — automatically added
                add("implementation", project(":core:common"))
                add("implementation", project(":core:model"))

                // DEBUG
                add("debugImplementation", project.libs.findLibrary("compose-ui-tooling").get())

                // TEST
                add("testImplementation", project.libs.findLibrary("junit").get())
                add("testImplementation", project.libs.findLibrary("kotlin-coroutines-test").get())
                add("testImplementation", project.libs.findLibrary("mockito-core").get())
                add("testImplementation", project.libs.findLibrary("mockito-kotlin").get())
                add("testImplementation", project.libs.findLibrary("compose-ui-test").get())

                add("androidTestImplementation", project.libs.findLibrary("junit-ext").get())
                add("androidTestImplementation", project.libs.findLibrary("espresso-core").get())
                add("androidTestImplementation", project.libs.findLibrary("compose-ui-test").get())
            }

            // ─── Kotlin Compiler ───────────────────────────────────────
            tasks.withType<KotlinCompile>().configureEach {
                kotlinOptions {
                    jvmTarget = "17"
                    freeCompilerArgs = listOf(
                        "-Xjsr305=strict",
                        "-opt-in=kotlin.RequiresOptIn",
                        "-opt-in=androidx.compose.material3.ExperimentalMaterial3Api",
                        "-opt-in=androidx.compose.foundation.ExperimentalFoundationApi"
                    )
                }
            }
        }
    }
}