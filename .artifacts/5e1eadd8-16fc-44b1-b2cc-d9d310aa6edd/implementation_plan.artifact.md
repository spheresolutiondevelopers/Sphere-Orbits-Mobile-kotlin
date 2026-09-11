# API Integration and Offline/Local User Support

This plan implements the integration with the new API hosted at `http://sphereschedule.runasp.net` and adds support for local user profiles. The app will prioritize local functionality and allow users to register officially later in the settings.

## User Review Required

> [!IMPORTANT]
> The `API_BASE_URL` is being updated to `http://sphereschedule.runasp.net`. Please ensure this URL is correct and accessible from the device/emulator.

> [!NOTE]
> Synchronization logic will now skip remote calls if the user is only a "Local" user. Local data will remain on the device until the user registers officially.

## Proposed Changes

### [Component] Core Networking
Update the base URL to the new host.

#### [MODIFY] [core/network/build.gradle.kts](file:///C:/Users/lenovo/Projects/sheduling%20and%20planning/sphere%20orbit/core/network/build.gradle.kts)
- Update `API_BASE_URL` to `http://sphereschedule.runasp.net`.

---

### [Component] Authentication & User Domain
Add support for local user flagging.

#### [MODIFY] [core/domain/auth/src/main/java/com/orbits/domain/auth/AuthUser.kt](file:///C:/Users/lenovo/Projects/sheduling%20and%20planning/sphere%20orbit/core/domain/auth/src/main/java/com/orbits/domain/auth/AuthUser.kt)
- Add `val isLocal: Boolean = false` to `AuthUser`.

#### [MODIFY] [core/domain/auth/src/main/java/com/orbits/domain/auth/AuthRepository.kt](file:///C:/Users/lenovo/Projects/sheduling%20and%20planning/sphere%20orbit/core/domain/auth/src/main/java/com/orbits/domain/auth/AuthRepository.kt)
- Add `suspend fun createLocalUser(data: RegistrationData): Result<AuthUser>` method.

---

### [Component] Authentication Data Layer
Implement local user persistence and logic.

#### [MODIFY] [core/database/src/main/java/com/orbits/data/auth/AuthEntity.kt](file:///C:/Users/lenovo/Projects/sheduling%20and%20planning/sphere%20orbit/core/database/src/main/java/com/orbits/data/auth/AuthEntity.kt)
- Add `@ColumnInfo(name = "is_local") val isLocal: Boolean = false` to `AuthEntity`.

#### [MODIFY] [core/data/auth/src/main/java/com/orbits/data/auth/mappers/AuthMapper.kt](file:///C:/Users/lenovo/Projects/sheduling%20and%20planning/sphere%20orbit/core/data/auth/src/main/java/com/orbits/data/auth/mappers/AuthMapper.kt)
- Update mapping methods to include the `isLocal` flag.

#### [MODIFY] [core/data/auth/src/main/java/com/orbits/data/auth/AuthRepositoryImpl.kt](file:///C:/Users/lenovo/Projects/sheduling%20and%20planning/sphere%20orbit/core/data/auth/src/main/java/com/orbits/data/auth/AuthRepositoryImpl.kt)
- Implement `createLocalUser`.
- Update `isAuthenticated` to return true if a local user exists (allowing app usage).

---

### [Component] Synchronization
Ensure sync logic respects local-only status.

#### [MODIFY] [core/sync/src/main/java/com/orbits/core/sync/SyncService.kt](file:///C:/Users/lenovo/Projects/sheduling%20and%20planning/sphere%20orbit/core/sync/src/main/java/com/orbits/core/sync/SyncService.kt)
- Update `sync()` to check `AuthRepository.getCurrentUser()?.isLocal`. If local, skip remote sync steps and return success (as it's "synced" locally).

## Verification Plan

### Automated Tests
- Unit tests for `AuthRepositoryImpl` ensuring `createLocalUser` doesn't call the API.
- Unit tests for `SyncService` ensuring it skips remote calls for local users.

### Manual Verification
1.  Wipe app data.
2.  Launch app and create a "Local Profile".
3.  Verify the app functions (creating tasks, notes, etc.) without API calls.
4.  Go to Settings and verify an option to "Register" exists (UI part might need a separate task if not present, but the backend support will be there).
5.  Register the user and verify sync begins working.
