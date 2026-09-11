package com.orbits.feature.dashboard.di

import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object DashboardModule {
    // No bindings needed — all dependencies are provided by the domain and data modules
    // The ViewModel is injected via Hilt automatically
}
