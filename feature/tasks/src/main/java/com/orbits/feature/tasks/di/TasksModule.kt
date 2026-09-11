package com.orbits.feature.tasks.di

import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object TasksModule {
    // No bindings needed — all dependencies are provided by the domain and data modules
    // The ViewModel is injected via Hilt automatically
}
