package com.orbits.feature.calendar.di

import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object CalendarModule {
    // No bindings needed — all dependencies are provided by the domain and data modules
}
