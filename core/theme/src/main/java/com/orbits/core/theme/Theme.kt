/*
 * Copyright © 2026 Sphere Solution Developers
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */

package com.orbits.core.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext

/**
 * Theme mode enum.
 */
enum class ThemeMode {
    LIGHT,
    DARK,
    SYSTEM
}

/**
 * CompositionLocal for theme mode preference.
 * Used to read theme mode from any composable.
 */
val LocalThemeMode = compositionLocalOf { ThemeMode.SYSTEM }

/**
 * Color schemes.
 */
private val DarkColorScheme = darkColorScheme(
    primary = SpherePurple,
    onPrimary = Color.White,
    primaryContainer = Color(0xFF1C1B3A),
    onPrimaryContainer = Color(0xFFDDD9FF),
    secondary = SpherePink,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFF2D1B3A),
    onSecondaryContainer = Color(0xFFE9D5FF),
    tertiary = SphereCoral,
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFF3D1B2E),
    onTertiaryContainer = Color(0xFFFFD6E0),
    error = SphereRed,
    onError = Color.White,
    errorContainer = Color(0xFF3D1B1B),
    onErrorContainer = Color(0xFFFFD6D6),
    background = BackgroundDark,
    onBackground = OnBackgroundDark,
    surface = SurfaceDark,
    onSurface = OnSurfaceDark,
    surfaceVariant = SurfaceDarkSecondary,
    onSurfaceVariant = OnSurfaceVariantDark,
    outline = OutlineDark,
    outlineVariant = OutlineVariantDark
)

private val LightColorScheme = lightColorScheme(
    primary = SpherePurple,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFE8E3FF),
    onPrimaryContainer = Color(0xFF2D1A8A),
    secondary = SpherePink,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFF3E5FF),
    onSecondaryContainer = Color(0xFF3A1A5E),
    tertiary = SphereCoral,
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFFFFE0E6),
    onTertiaryContainer = Color(0xFF6B1A30),
    error = SphereRed,
    onError = Color.White,
    errorContainer = Color(0xFFFFD6D6),
    onErrorContainer = Color(0xFF6B1A1A),
    background = BackgroundLight,
    onBackground = OnBackgroundLight,
    surface = SurfaceLight,
    onSurface = OnSurfaceLight,
    surfaceVariant = SurfaceLightSecondary,
    onSurfaceVariant = OnSurfaceVariantLight,
    outline = OutlineLight,
    outlineVariant = OutlineVariantLight
)

/**
 * Sphere Theme composable.
 * Wraps MaterialTheme with custom colors, typography, and shapes.
 * Supports dynamic color on Android 12+ when enabled.
 *
 * @param darkTheme Whether to use dark theme. If null, uses system setting.
 * @param dynamicColor Whether to use Material You dynamic color.
 * @param content Content to render with the theme.
 */
@Composable
fun SphereTheme(
    darkTheme: Boolean? = null,
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    // Determine if dark theme should be used
    val isDark = darkTheme ?: isSystemInDarkTheme()

    // Color scheme
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (isDark) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        isDark -> DarkColorScheme
        else -> LightColorScheme
    }

    // Apply theme
    MaterialTheme(
        colorScheme = colorScheme,
        typography = SphereTypography,
        shapes = SphereShapes,
        content = content
    )
}

/**
 * Extension to get the current color scheme.
 */
val MaterialTheme.colorScheme: ColorScheme
    @Composable
    @ReadOnlyComposable
    get() = MaterialTheme.colorScheme

/**
 * Extension to get the current typography.
 */
val MaterialTheme.typography: Typography
    @Composable
    @ReadOnlyComposable
    get() = MaterialTheme.typography

/**
 * Extension to get the current shapes.
 */
val MaterialTheme.shapes: Shapes
    @Composable
    @ReadOnlyComposable
    get() = MaterialTheme.shapes