/*
 * Copyright © 2026 Sphere Solution Developers
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */

import com.android.build.api.dsl.ApplicationExtension
import org.gradle.api.JavaVersion
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.withType
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

/**
 * Convention plugin for the :app module.
 */
class AndroidApplicationConvention : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                apply("com.android.application")
                apply("com.google.dagger.hilt.android")
                apply("com.google.devtools.ksp")
                apply("org.jetbrains.kotlin.plugin.serialization")
                apply("org.jetbrains.kotlin.plugin.compose")
            }

            extensions.configure<ApplicationExtension> {
                compileSdk = 37

                defaultConfig {
                    applicationId = "com.orbits.app"
                    minSdk = 26
                    targetSdk = 37
                    versionCode = 1
                    versionName = "1.0.0"

                    testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
                    vectorDrawables {
                        useSupportLibrary = true
                    }
                }

                buildTypes {
                    getByName("debug") {
                        isMinifyEnabled = false
                    }
                    getByName("release") {
                        isMinifyEnabled = true
                        proguardFiles(
                            getDefaultProguardFile("proguard-android-optimize.txt"),
                            "proguard-rules.pro"
                        )
                    }
                }

                compileOptions {
                    sourceCompatibility = JavaVersion.VERSION_21
                    targetCompatibility = JavaVersion.VERSION_21
                }

                buildFeatures.apply {
                    compose = true
                    buildConfig = true
                }
            }

            dependencies {
                val bom = libsCatalog.findLibrary("compose-bom").get()
                add("implementation", platform(bom))
                add("androidTestImplementation", platform(bom))
                add("debugImplementation", platform(bom))
                add("implementation", libsCatalog.findBundle("compose").get())
                add("implementation", libsCatalog.findLibrary("androidx-activity").get())
                add("implementation", libsCatalog.findLibrary("navigation-compose").get())
                add("implementation", libsCatalog.findBundle("androidx").get())
                add("implementation", libsCatalog.findLibrary("hilt-android").get())
                add("implementation", libsCatalog.findLibrary("hilt-navigation-compose").get())
                add("implementation", libsCatalog.findLibrary("timber").get())
                add("implementation", libsCatalog.findLibrary("kt-stdlib").get())
                add("implementation", libsCatalog.findBundle("coroutines").get())
                add("ksp", libsCatalog.findLibrary("hilt-compiler").get())

                add("debugImplementation", libsCatalog.findLibrary("compose-ui-tooling").get())

                add("testImplementation", libsCatalog.findBundle("test").get())

                add("androidTestImplementation", libsCatalog.findBundle("androidTest").get())
                add("androidTestImplementation", libsCatalog.findLibrary("compose-ui-test").get())
            }

            tasks.withType<KotlinCompile>().configureEach {
                compilerOptions {
                    jvmTarget.set(JvmTarget.JVM_21)
                    freeCompilerArgs.addAll(
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
