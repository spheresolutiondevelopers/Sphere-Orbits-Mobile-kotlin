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
import androidx.compose.material3.ColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

/**
 * Custom design tokens that extend Material3 ColorScheme.
 */
@Immutable
data class SphereColors(
    val glow: Color,
    val border: Color,
    val borderSecondary: Color,
    val backgroundSecondary: Color,
    val backgroundTertiary: Color,
    val cardSecondary: Color,
    val cardTertiary: Color,
    val textSecondary: Color,
    val textTertiary: Color,
    val navBackground: Color,
    val shadow: Color
)

val LocalSphereColors = staticCompositionLocalOf {
    SphereColors(
        glow = Color.Unspecified,
        border = Color.Unspecified,
        borderSecondary = Color.Unspecified,
        backgroundSecondary = Color.Unspecified,
        backgroundTertiary = Color.Unspecified,
        cardSecondary = Color.Unspecified,
        cardTertiary = Color.Unspecified,
        textSecondary = Color.Unspecified,
        textTertiary = Color.Unspecified,
        navBackground = Color.Unspecified,
        shadow = Color.Unspecified
    )
}

/**
 * Theme mode enum.
 */
enum class ThemeMode {
    LIGHT,
    DARK,
    SYSTEM
}

/**
 * Theme palette enum for dynamic branding.
 */
enum class ThemePalette {
    SPHERE,
    DEEP_SEA,
    FOREST,
    SUNSET
}

private val DarkColorScheme = darkColorScheme(
    primary = SphereBase,
    onPrimary = Color.White,
    primaryContainer = BgDark2,
    onPrimaryContainer = TextDark,
    secondary = SpherePink,
    onSecondary = Color.White,
    secondaryContainer = CardDark2,
    onSecondaryContainer = TextDark2,
    tertiary = AccentBase,
    onTertiary = Color.White,
    background = BgDark,
    onBackground = TextDark,
    surface = CardDark,
    onSurface = TextDark,
    surfaceVariant = CardDark2,
    onSurfaceVariant = TextDark2,
    outline = BorderDark,
    outlineVariant = BorderDark2,
    error = SphereRed,
    onError = Color.White
)

private val LightColorScheme = lightColorScheme(
    primary = SphereBase,
    onPrimary = Color.White,
    primaryContainer = BgLight2,
    onPrimaryContainer = TextLight,
    secondary = SpherePink,
    onSecondary = Color.White,
    secondaryContainer = CardLight2,
    onSecondaryContainer = TextLight2,
    tertiary = AccentBase,
    onTertiary = Color.White,
    background = BgLight,
    onBackground = TextLight,
    surface = CardLight,
    onSurface = TextLight,
    surfaceVariant = CardLight2,
    onSurfaceVariant = TextLight2,
    outline = BorderLight,
    error = SphereRed,
    onError = Color.White
)

/**
 * Sphere Theme composable.
 * Supports dynamic color on Android 12+ and dynamic palettes.
 */
@Composable
fun SphereTheme(
    themeMode: ThemeMode = ThemeMode.SYSTEM,
    palette: ThemePalette = ThemePalette.SPHERE,
    dynamicColor: Boolean = false, // Default to false to preserve brand identity
    content: @Composable () -> Unit
) {
    val darkTheme = when (themeMode) {
        ThemeMode.LIGHT -> false
        ThemeMode.DARK -> true
        ThemeMode.SYSTEM -> isSystemInDarkTheme()
    }

    val context = LocalContext.current
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> getDarkColorScheme(palette)
        else -> getLightColorScheme(palette)
    }

    val sphereColors = if (darkTheme) {
        SphereColors(
            glow = SphereGlow,
            border = BorderDark,
            borderSecondary = BorderDark2,
            backgroundSecondary = BgDark2,
            backgroundTertiary = BgDark3,
            cardSecondary = CardDark2,
            cardTertiary = CardDark3,
            textSecondary = TextDark2,
            textTertiary = TextDark3,
            navBackground = NavBgDark,
            shadow = ShadowDark
        )
    } else {
        SphereColors(
            glow = SphereGlow.copy(alpha = 0.1f),
            border = BorderLight,
            borderSecondary = BorderLight,
            backgroundSecondary = BgLight2,
            backgroundTertiary = BgLight2,
            cardSecondary = CardLight2,
            cardTertiary = CardLight2,
            textSecondary = TextLight2,
            textTertiary = TextLight3,
            navBackground = NavBgLight,
            shadow = ShadowLight
        )
    }

    CompositionLocalProvider(
        LocalSphereColors provides sphereColors
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = SphereTypography,
            shapes = SphereShapes,
            content = content
        )
    }
}

private fun getDarkColorScheme(palette: ThemePalette): ColorScheme {
    return when (palette) {
        ThemePalette.SPHERE -> DarkColorScheme
        ThemePalette.DEEP_SEA -> DarkColorScheme.copy(primary = Color(0xFF0EA5E9))
        ThemePalette.FOREST -> DarkColorScheme.copy(primary = Color(0xFF10B981))
        ThemePalette.SUNSET -> DarkColorScheme.copy(primary = Color(0xFFF59E0B))
    }
}

private fun getLightColorScheme(palette: ThemePalette): ColorScheme {
    return when (palette) {
        ThemePalette.SPHERE -> LightColorScheme
        ThemePalette.DEEP_SEA -> LightColorScheme.copy(primary = Color(0xFF0284C7))
        ThemePalette.FOREST -> LightColorScheme.copy(primary = Color(0xFF059669))
        ThemePalette.SUNSET -> LightColorScheme.copy(primary = Color(0xFFD97706))
    }
}

object SphereTheme {
    val colors: SphereColors
        @Composable
        @ReadOnlyComposable
        get() = LocalSphereColors.current
}
