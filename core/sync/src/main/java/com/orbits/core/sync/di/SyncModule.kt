package com.orbits.core.sync.di

import com.orbits.core.sync.strategy.LastWriteWinsStrategy
import com.orbits.core.sync.strategy.ServerWinsStrategy
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object SyncModule {

    @Provides
    @Singleton
    fun provideLastWriteWinsStrategy(): LastWriteWinsStrategy {
        return LastWriteWinsStrategy()
    }

    @Provides
    @Singleton
    fun provideServerWinsStrategy(): ServerWinsStrategy {
        return ServerWinsStrategy()
    }
}
