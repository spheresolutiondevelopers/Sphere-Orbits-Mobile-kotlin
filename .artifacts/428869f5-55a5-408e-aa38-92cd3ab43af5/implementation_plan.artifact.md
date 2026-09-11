# Build Fix Implementation Plan

This plan addresses several compilation errors in the `:core:network` and `:core:theme` modules.

## User Review Required

> [!IMPORTANT]
> **Missing Font Resources**: The `Typography.kt` file references custom fonts (Outfit and DM Sans) that are not present in the project's resource directories. I will revert to `FontFamily.Default` to allow the build to pass. Please provide the font files in `core/theme/src/main/res/font/` if you wish to use them.

> [!NOTE]
> **API Base URL**: I am adding a default `API_BASE_URL` to the `:core:network` build configuration as it was referenced but not defined.

## Proposed Changes

### Core Network Module

#### [MODIFY] [build.gradle.kts](file:///C:/Users/lenovo/Projects/sheduling%20and%20planning/sphere%20orbit/core/network/build.gradle.kts)
- Enable `buildConfig`.
- Add `API_BASE_URL` build config field.

#### [MODIFY] [ChatApi.kt](file:///C:/Users/lenovo/Projects/sheduling%20and%20planning/sphere%20orbit/core/network/src/main/java/com/orbits/core/network/api/ChatApi.kt)
- Add missing `import com.orbits.core.model.UserDto`.

#### [MODIFY] [AnalyticsApi.kt](file:///C:/Users/lenovo/Projects/sheduling%20and%20planning/sphere%20orbit/core/network/src/main/java/com/orbits/core/network/api/AnalyticsApi.kt)
- Add missing `import retrofit2.http.POST` and `import retrofit2.http.Body`.

#### [MODIFY] [ApiClient.kt](file:///C:/Users/lenovo/Projects/sheduling%20and%20planning/sphere%20orbit/core/network/src/main/java/com/orbits/core/network/ApiClient.kt)
- Inject `@ApplicationContext context: Context`.
- Import `com.orbits.core.network.BuildConfig`.
- Fix `context.cacheDir` usage.

#### [MODIFY] [LoggingInterceptor.kt](file:///C:/Users/lenovo/Projects/sheduling%20and%20planning/sphere%20orbit/core/network/src/main/java/com/orbits/core/network/LoggingInterceptor.kt)
- Import `com.orbits.core.network.BuildConfig`.

---

### Core Theme Module

#### [MODIFY] [Typography.kt](file:///C:/Users/lenovo/Projects/sheduling%20and%20planning/sphere%20orbit/core/theme/src/main/java/com/orbits/core/theme/Typography.kt)
- Use `FontFamily.Default` instead of missing font resources.

## Verification Plan

### Automated Tests
- Run `:core:network:compileDebugKotlin`
- Run `:core:theme:compileDebugKotlin`
- Run `:app:assembleDebug`

### Manual Verification
- None required for build fixes.
