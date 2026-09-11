package com.orbits.data.auth.di

import com.orbits.core.common.TokenProvider
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

    @Binds
    @Singleton
    abstract fun bindTokenProvider(
        impl: TokenManager
    ): TokenProvider
}
