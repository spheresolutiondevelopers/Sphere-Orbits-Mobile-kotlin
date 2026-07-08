package com.orbits.data.calendar.di

import com.orbits.data.calendar.CalendarRepositoryImpl
import com.orbits.data.calendar.remote.GoogleCalendarClient
import com.orbits.data.calendar.remote.OutlookCalendarClient
import com.orbits.domain.calendar.CalendarRepository
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
    abstract fun bindCalendarRepository(
        impl: CalendarRepositoryImpl
    ): CalendarRepository

    // GoogleCalendarClient and OutlookCalendarClient are instantiated directly
    // They don't need to be bound to interfaces
}