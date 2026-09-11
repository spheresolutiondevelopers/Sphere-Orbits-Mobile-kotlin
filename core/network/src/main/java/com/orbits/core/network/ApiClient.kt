/*
 * Copyright © 2026 Sphere Solution Developers
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */

package com.orbits.core.network

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import android.content.Context
import com.orbits.core.network.BuildConfig
import dagger.hilt.android.qualifiers.ApplicationContext
import okhttp3.Cache
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.io.File
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

/**
 * API client builder.
 * Provides a configured Retrofit instance with authentication, logging, and caching.
 */
@Singleton
class ApiClient @Inject constructor(
    @ApplicationContext private val context: Context,
    private val authInterceptor: AuthInterceptor,
    private val loggingInterceptor: LoggingInterceptor,
    private val networkMonitor: NetworkMonitor
) {

    private val baseUrl = BuildConfig.API_BASE_URL

    private val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    private val okHttpClient: OkHttpClient by lazy {
        val builder = OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .addInterceptor(authInterceptor)
            .addInterceptor(loggingInterceptor)
            .addInterceptor { chain ->
                // Offline support: if no network, serve cache if available
                val request = chain.request()
                if (!networkMonitor.isConnected()) {
                    request.newBuilder()
                        .header("Cache-Control", "only-if-cached, max-stale=604800")
                        .build()
                } else {
                    request.newBuilder()
                        .header("Cache-Control", "public, max-age=60")
                        .build()
                }.let { chain.proceed(it) }
            }

        // Cache directory (10 MB)
        val cacheDir = File(context.cacheDir, "http_cache")
        val cacheSize = 10L * 1024 * 1024 // 10 MB
        builder.cache(Cache(cacheDir, cacheSize))

        builder.build()
    }

    @PublishedApi
    internal val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
    }

    /**
     * Create an API service instance.
     */
    inline fun <reified T> create(): T = retrofit.create(T::class.java)
}