package com.orbits.data.notes.di

import com.orbits.data.notes.NotesRepositoryImpl
import com.orbits.data.notes.MarkdownParser
import com.orbits.domain.notes.NotesRepository
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
    abstract fun bindNotesRepository(
        impl: NotesRepositoryImpl
    ): NotesRepository

    // MarkdownParser is instantiated directly
    // It doesn't need to be bound to an interface
}