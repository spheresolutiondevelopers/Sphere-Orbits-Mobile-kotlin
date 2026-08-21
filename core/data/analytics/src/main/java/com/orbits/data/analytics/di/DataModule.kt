package com.orbits.data.analytics.di

import com.orbits.data.analytics.AnalyticsRepositoryImpl
import com.orbits.data.analytics.ReportGenerator
import com.orbits.domain.analytics.AnalyticsRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal abstract class DataModule {

    @Binds
    @Singleton
    abstract fun bindAnalyticsRepository(
        impl: AnalyticsRepositoryImpl
    ): AnalyticsRepository

    // ReportGenerator is instantiated directly
    // It doesn't need to be bound to an interface
}