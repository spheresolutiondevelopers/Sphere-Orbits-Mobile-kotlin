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

import androidx.compose.ui.graphics.Color

// ─── HTML Design Tokens ─────────────────────────────────────────
val SphereBase = Color(0xFF7C6CF8)
val SphereBase2 = Color(0xFF5E4FF0)
val SphereBase3 = Color(0xFF9B8DFA) // --sphere3
val SphereGlow = Color(0x597C6CF8) // rgba(124,108,248,0.35)

val AccentBase = Color(0xFFFF6B8A)
val AccentBase2 = Color(0xFFFF9A6C)

val SphereGreen = Color(0xFF2DD4A0)
val SphereYellow = Color(0xFFFFD166)
val SphereRed = Color(0xFFFF6B6B)
val SphereCyan = Color(0xFF22D3EE)
val SpherePinkTask = Color(0xFFF472B6)
val SphereViolet = Color(0xFFA855F7)

// ─── Dark Theme (HTML Replica) ──────────────────────────────────
val BgDark = Color(0xFF000000) // --bg
val BgDark2 = Color(0xFF08022A) // --bg2
val BgDark3 = Color(0xFF0E0445) // --bg3
val CardDark = Color(0xFF0C0340) // --card
val CardDark2 = Color(0xFF130455) // --card2
val CardDark3 = Color(0xFF1A0568) // --card3
val BorderDark = Color(0xFF1E0A60) // --border
val BorderDark2 = Color(0xFF5050A0) // --border2
val TextDark = Color(0xFFFFFFFF) // --text
val TextDark2 = Color(0xFFA0A0C8) // --text2
val TextDark3 = Color(0xFF6868A0) // --text3
val NavBgDark = Color(0xF208022A) // rgba(8,2,42,0.95)
val ShadowDark = Color(0x73000000) // rgba(0,0,0,0.45)

// ─── Light Theme (HTML) ─────────────────────────────────────────
val BgLight = Color(0xFFF4F3FF)
val BgLight2 = Color(0xFFECEAFF)
val CardLight = Color(0xFFFFFFFF)
val CardLight2 = Color(0xFFF8F7FF)
val BorderLight = Color(0x1F7C6CF8) // rgba(124,108,248,0.12)
val TextLight = Color(0xFF1A1733)
val TextLight2 = Color(0xFF6B6897)
val TextLight3 = Color(0xFFB4B2D4)
val NavBgLight = Color(0xF7FFFFFF) // rgba(255,255,255,0.97)
val ShadowLight = Color(0x1A7C6CF8) // rgba(124,108,248,0.1)

// ─── Brand Palette (Legacy/Extended) ───────────────────────────
val SpherePurple = SphereBase
val SpherePink = Color(0xFFA855F7)
val SphereCoral = AccentBase

// ─── Semantic Colors ───────────────────────────────────────────
val StatusSuccess = SphereGreen
val StatusWarning = SphereYellow
val StatusError = SphereRed
val StatusInfo = SphereBase
