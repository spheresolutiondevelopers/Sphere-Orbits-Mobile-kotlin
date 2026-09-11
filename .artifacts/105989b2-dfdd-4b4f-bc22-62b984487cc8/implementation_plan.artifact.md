# Build Fixes and Version Updates

This plan addresses several build warnings and errors, including the Jetifier deprecation, Kotlin plugin duplication, outdated `compileSdk` version, and Room/KSP compilation errors.

## Proposed Changes

### Build Configuration

#### [MODIFY] [gradle.properties](file:///C:/Users/lenovo/Projects/sheduling%20and%20planning/sphere%20orbit/gradle.properties)
- Remove `android.enableJetifier=true` as it is deprecated and no longer needed for modern AndroidX-based projects.

#### [MODIFY] [build.gradle.kts](file:///C:/Users/lenovo/Projects/sheduling%20and%20planning/sphere%20orbit/build.gradle.kts)
- Apply core plugins (Android, Kotlin, KSP, Hilt) with `apply false` to prevent multiple loadings of the Kotlin plugin across subprojects.

---

### Convention Plugins (build-logic)

#### [MODIFY] [AndroidApplicationConvention.kt](file:///C:/Users/lenovo/Projects/sheduling%20and%20planning/sphere%20orbit/build-logic/src/main/kotlin/AndroidApplicationConvention.kt)
- Update `compileSdk` and `targetSdk` from `35` to `37`.

#### [MODIFY] [AndroidLibraryConvention.kt](file:///C:/Users/lenovo/Projects/sheduling%20and%20planning/sphere%20orbit/build-logic/src/main/kotlin/AndroidLibraryConvention.kt)
- Update `compileSdk` from `35` to `37`.

#### [MODIFY] [AndroidFeatureConvention.kt](file:///C:/Users/lenovo/Projects/sheduling%20and%20planning/sphere%20orbit/build-logic/src/main/kotlin/AndroidFeatureConvention.kt)
- Update `compileSdk` from `35` to `37`.

---

### Database Module (:core:database)

#### [MODIFY] [build.gradle.kts](file:///C:/Users/lenovo/Projects/sheduling%20and%20planning/sphere%20orbit/core/database/build.gradle.kts)
- Configure Room schema location for KSP to resolve the "Schema export directory was not provided" warning.

#### [MODIFY] [TypeConverters.kt](file:///C:/Users/lenovo/Projects/sheduling%20and%20planning/sphere%20orbit/core/database/src/main/java/com/orbits/core/database/TypeConverters.kt)
- Rename the class `TypeConverters` to `DatabaseConverters` to avoid name collision with Room's `@TypeConverters` annotation.

#### [MODIFY] [OrbitsDatabase.kt](file:///C:/Users/lenovo/Projects/sheduling%20and%20planning/sphere%20orbit/core/database/src/main/java/com/orbits/core/database/OrbitsDatabase.kt)
- Update the annotation to `@TypeConverters(DatabaseConverters::class)`.

## Verification Plan

### Automated Tests
- Run `./gradlew :core:database:assembleDebug` to verify that the database module compiles successfully.
- Run `./gradlew assembleDebug` to ensure the entire project builds.

### Manual Verification
- Check the Gradle build log for any remaining warnings regarding Jetifier or duplicate plugins.
