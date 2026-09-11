package com.orbits.feature.chat.di

import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object ChatModule {
    // No bindings needed — all dependencies are provided by the domain and data modules
}
