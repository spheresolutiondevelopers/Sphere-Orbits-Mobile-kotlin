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

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

/**
 * Shape system matching the UI/UX prototype.
 * Base unit: 8dp
 */
val SphereShapes = Shapes(
    extraSmall = RoundedCornerShape(4.dp),      // Tags, small badges
    small = RoundedCornerShape(8.dp),           // Buttons, chips, input fields
    medium = RoundedCornerShape(12.dp),         // Cards, dialogs
    large = RoundedCornerShape(16.dp),          // Bottom sheets, larger surfaces
    extraLarge = RoundedCornerShape(24.dp)      // Full-screen sheets, modals
)

// ─── Individual shape constants ───────────────────────────────
val ShapeNone = RoundedCornerShape(0.dp)
val ShapeExtraSmall = RoundedCornerShape(4.dp)
val ShapeSmall = RoundedCornerShape(8.dp)
val ShapeMedium = RoundedCornerShape(12.dp)
val ShapeLarge = RoundedCornerShape(16.dp)
val ShapeExtraLarge = RoundedCornerShape(24.dp)
val ShapePill = RoundedCornerShape(50.dp)

// ─── Spacing constants ────────────────────────────────────────
object Spacing {
    val none = 0.dp
    val xxs = 2.dp
    val xs = 4.dp
    val sm = 8.dp
    val md = 12.dp
    val lg = 16.dp
    val xl = 20.dp
    val xxl = 24.dp
    val xxxl = 32.dp
    val huge = 48.dp
    val massive = 64.dp
}