# Task List

- `[x]` Core: Refactor Theme and Colors
    - `[x]` Update `Color.kt` with HTML design tokens
    - `[x]` Refactor `Theme.kt` for dynamic palettes and custom tokens
- `[x]` App Layout: Centralize Navigation and UI
    - `[x]` Create `SphereApp.kt` (Top Bar, Bottom Bar, Drawer)
    - `[x]` Update `MainActivity.kt` to use `SphereApp`
- `[x]` Features: Clean up nested Scaffolds
    - `[x]` Remove Scaffolds from `DashboardScreen.kt`
    - `[x]` Remove Scaffolds from `SettingsScreen.kt`
    - `[x]` Remove Scaffolds from other list screens (Analytics, Tasks, etc.)
- `[x]` Verification
    - `[x]` Manual check of theme switching (implemented UI selectors)
    - `[x]` Verify navigation and layout consistency
    - `[x]` Ensure detail screens have back buttons if needed (handled by parents TopAppBar)
