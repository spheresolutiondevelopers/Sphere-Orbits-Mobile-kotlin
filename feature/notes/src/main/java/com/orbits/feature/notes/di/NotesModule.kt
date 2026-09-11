package com.orbits.feature.notes.di

import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object NotesModule {
    // No bindings needed — all dependencies are provided by the domain and data modules
}
