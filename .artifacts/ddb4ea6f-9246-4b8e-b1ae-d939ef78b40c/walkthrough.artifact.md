# Walkthrough: New Sphere Layout and Dynamic Branding

I have successfully updated the app's design system and layout to match the provided HTML design reference and implemented a dynamic theme selection system.

## Changes Made

### 1. Centralized App Layout (`SphereApp.kt`)
- Created a top-level `Scaffold` that manages the custom **Sphere Top App Bar** and **Sphere Bottom Navigation**.
- **Top App Bar**: Features the gradient "Sphere" logo, brand toggle for dark/light mode, notification bell, and a hamburger menu for the navigation drawer.
- **Bottom Navigation**: Implements the mobile design with 4 main navigation items, a central Floating Action Button (FAB) for quick actions, and an active state indicator (pip).
- **Navigation Drawer**: Centralized user profile information and quick access to secondary screens like Appointments, Notes, and Events.

### 2. Modern Branding & Theme System
- Refactored `Theme.kt` and `Color.kt` to use the design tokens from your HTML reference (e.g., `SphereGlow`, specific background gradients).
- Added `ThemePalette` support, allowing the app primary colors to change dynamically (Sphere Default, Deep Sea, Forest, Sunset).
- Updated typography to use **Outfit** for headlines/titles and **DM Sans** for body text.

### 3. Settings-Driven Appearance
- Updated the **Settings Screen** with dedicated **Theme Mode** and **Color Palette** selectors.
- Users can now switch between Light, Dark, or System themes and select their preferred brand color palette directly from the UI.
- All brand colors across the app update instantly when a new palette is selected.

### 4. Clean Architecture (UI)
- Refactored 15+ screens to remove nested `Scaffold` and `TopAppBar` components.
- Screen-specific actions (like "Edit" or "Delete" in Task Details) have been moved into the screen content for a cleaner, unified look.
- Auth screens (Login/Signup) automatically hide the main navigation bars for a focused onboarding experience.

## Verification Results
- **Theme Switching**: Verified that clicking the toggle in the top bar or selecting a palette in settings updates the entire app's color scheme.
- **Navigation**: Verified that the bottom bar correctly highlights the active screen and the back button appears automatically on detail screens.
- **Branding**: Typography and icons now match the "Sphere" branding style.
