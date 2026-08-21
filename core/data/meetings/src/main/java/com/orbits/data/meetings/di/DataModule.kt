package com.orbits.data.meetings.di

import com.orbits.data.meetings.MeetingRepositoryImpl
import com.orbits.data.meetings.remote.ZoomClient
import com.orbits.data.meetings.remote.TeamsClient
import com.orbits.domain.meetings.MeetingRepository
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
    abstract fun bindMeetingRepository(
        impl: MeetingRepositoryImpl
    ): MeetingRepository

    // ZoomClient and TeamsClient are instantiated directly
    // They don't need to be bound to interfaces
}