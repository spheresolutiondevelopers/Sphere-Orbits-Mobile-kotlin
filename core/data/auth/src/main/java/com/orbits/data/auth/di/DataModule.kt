package com.orbits.data.auth.di

import com.orbits.data.auth.AuthRepositoryImpl
import com.orbits.data.auth.TokenManager
import com.orbits.domain.auth.AuthRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal abstract class DataModule {

    @Binds
    @Singleton
    abstract fun bindAuthRepository(
        impl: AuthRepositoryImpl
    ): AuthRepository

    // TokenManager is also exposed as TokenProvider via its interface
    // No need to bind separately, it's already a Singleton
}