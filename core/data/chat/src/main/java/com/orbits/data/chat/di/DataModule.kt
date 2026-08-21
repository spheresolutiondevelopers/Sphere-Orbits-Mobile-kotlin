package com.orbits.data.chat.di

import com.orbits.data.chat.ChatRepositoryImpl
import com.orbits.data.chat.WebSocketClient
import com.orbits.domain.chat.ChatRepository
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
    abstract fun bindChatRepository(
        impl: ChatRepositoryImpl
    ): ChatRepository

    // WebSocketClient is instantiated directly
    // It doesn't need to be bound to an interface
}