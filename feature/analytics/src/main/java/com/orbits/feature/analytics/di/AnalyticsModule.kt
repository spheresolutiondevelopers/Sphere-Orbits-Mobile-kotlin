package com.orbits.feature.analytics.di

import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object AnalyticsModule {
    // No bindings needed — all dependencies are provided by the domain and data modules
}
