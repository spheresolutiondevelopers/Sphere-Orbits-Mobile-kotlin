# Implementation Plan: Update Layout, Branding, and Dynamic Theme

This plan outlines the steps to refactor the app's layout (Top Bar and Bottom Navigation) to match the provided HTML design reference (`sphere_schedule_mobile_v4-1.html`) and implement a dynamic theme system that allows users to select their preferred colors.

## Goal Description
1.  **Refactor Main Layout:** Implement a unified `Scaffold` with a custom `SphereTopAppBar` and `SphereBottomNavigationBar` that matches the HTML design (gradient logo, central FAB, active state pip, etc.).
2.  **Branding & Styling:** Update the app's typography and component styles to follow the "Sphere" branding (Outfit font for titles, DM Sans for body).
3.  **Dynamic Theme System:** Refactor `SphereTheme` to support multiple color palettes (starting with the default HTML design) and allow user selection in the Settings.
4.  **Dark/Light Mode:** Ensure full support for both modes as defined in the HTML design.

## User Review Required
> [!IMPORTANT]
> The architectural change will move the `Scaffold` from individual screens to a top-level `SphereApp` container. This will require removing `Scaffold` and `TopAppBar` from all existing feature screens to avoid "nested scaffolds" and ensure the top/bottom bars are consistent across the entire app.

> [!NOTE]
> I will add a `ThemePalette` enum to support multiple color sets (e.g., "Sphere Default", "Deep Sea", "Forest", "Sunset").

## Proposed Changes

### Core: Theme & Design System
Refactor the theme module to support dynamic palettes and specific design tokens from the HTML.

#### [MODIFY] [Color.kt](file:///C:/Users/lenovo/Projects/sheduling and planning/sphere orbit/core/theme/src/main/java/com/orbits/core/theme/Color.kt)
- Add specific color tokens from the HTML design (e.g., specific card backgrounds, text variants).
- Define different color palettes (Primary, Secondary, Accent).

#### [MODIFY] [Theme.kt](file:///C:/Users/lenovo/Projects/sheduling and planning/sphere orbit/core/theme/src/main/java/com/orbits/core/theme/Theme.kt)
- Update `SphereTheme` to accept a `ThemePalette` and `ThemeMode`.
- Provide a `LocalSphereColors` CompositionLocal for accessing custom branding colors (like the "Sphere" purple glow) not standard in Material3.

---

### App: Main Layout Refactoring
Centralize the layout to provide a consistent experience.

#### [NEW] [SphereApp.kt](file:///C:/Users/lenovo/Projects/sheduling and planning/sphere orbit/app/src/main/java/com/orbits/app/SphereApp.kt)
- Create a top-level `Scaffold` containing the `AppNavGraph`.
- Implement the custom `SphereTopAppBar` (gradient logo, brand name "Sphere.", theme toggle, notifications, drawer button).
- Implement the custom `SphereBottomBar` (icons with labels, active pip, central FAB).
- Add a `ModalNavigationDrawer` for the hamburger menu functionality.

#### [MODIFY] [MainActivity.kt](file:///C:/Users/lenovo/Projects/sheduling and planning/sphere orbit/app/src/main/java/com/orbits/app/MainActivity.kt)
- Replace `AppNavGraph()` call with `SphereApp()`.

---

### Feature: Clean Up Scaffolds
Remove individual bars from screens to let the main `SphereApp` handle them.

#### [MODIFY] [DashboardScreen.kt](file:///C:/Users/lenovo/Projects/sheduling and planning/sphere orbit/feature/dashboard/src/main/java/com/orbits/feature/dashboard/DashboardScreen.kt)
- Remove `Scaffold` and `TopAppBar`.
- Use a simplified `Column` or `LazyColumn` for content.

#### [MODIFY] [SettingsScreen.kt](file:///C:/Users/lenovo/Projects/sheduling and planning/sphere orbit/feature/settings/src/main/java/com/orbits/feature/settings/SettingsScreen.kt)
- Remove `Scaffold` and `TopAppBar`.
- Update the UI to include a "Theme Selection" section that allows choosing both `ThemeMode` (Light/Dark/System) and `ThemePalette`.

---

### Domain: Settings Persistence
Ensure theme preferences are stored correctly.

#### [MODIFY] [Settings.kt](file:///C:/Users/lenovo/Projects/sheduling and planning/sphere orbit/core/domain/settings/src/main/java/com/orbits/domain/settings/Settings.kt)
- Ensure `accentColor` or a new `themePalette` field is used to store the selected palette.

## Verification Plan

### Automated Tests
- Run existing UI tests to ensure navigation still works after refactoring the `Scaffold`.
- Add a unit test for `ThemePalette` color resolution.

### Manual Verification
1.  **Layout Check:** Compare the app's Top Bar and Bottom Bar against the HTML file screenshots/design.
2.  **Theme Toggle:** Use the toggle in the Top Bar and verify it updates the app immediately.
3.  **Palette Selection:** Go to Settings, change the theme palette, and verify that all brand colors (primary, accents) update throughout the app.
4.  **Drawer:** Verify the hamburger menu opens the drawer with the profile information.
5.  **FAB:** Verify the central "+" button in the bottom bar triggers a "Quick Add" action.
