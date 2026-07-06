/*
 * Copyright © 2026 Sphere Solution Developers
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */

package com.orbits.data.tasks.di

import com.orbits.data.tasks.TaskRepositoryImpl
import com.orbits.data.tasks.mappers.TaskMapper
import com.orbits.data.tasks.mappers.SubtaskMapper
import com.orbits.domain.tasks.TaskRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal abstract class DataModule {

    @Binds
    @Singleton
    abstract fun bindTaskRepository(
        impl: TaskRepositoryImpl
    ): TaskRepository

    // Mappers are not bound as interfaces; they are injected directly
    // TaskMapper and SubtaskMapper are internal and injected where needed
}