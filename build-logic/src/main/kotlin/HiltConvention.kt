/*
 * Copyright © 2026 Sphere Solution Developers
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.get
import org.gradle.kotlin.dsl.withType
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

/**
 * Convention plugin for Hilt configuration.
 * Applied automatically by the other convention plugins.
 */
class HiltConvention : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            // Hilt is already applied by the other plugins.
            // This plugin is a placeholder for any future Hilt-specific configuration.
        }
    }
}