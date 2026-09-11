# Fix Circular Dependency between :core:database and :core:data modules

The project currently has a circular dependency:
- `:core:database` depends on `:core:data:analytics` (and others) to see `@Entity` classes for the `OrbitsDatabase` declaration.
- `:core:data:analytics` depends on `:core:database` to use the `AnalyticsDao`.

This plan moves all Room entities into the `:core:database` module to break the circle.

## Proposed Changes

### [core:database]

#### [NEW] [SyncQueueEntity.kt](file:///C:/Users/lenovo/Projects/sheduling and planning/sphere orbit/core/database/src/main/java/com/orbits/data/sync/SyncQueueEntity.kt)
Create the new sync queue entity as provided by the user.

#### [NEW] [AnalyticsEntity.kt](file:///C:/Users/lenovo/Projects/sheduling and planning/sphere orbit/core/database/src/main/java/com/orbits/data/analytics/AnalyticsEntity.kt)
#### [NEW] [AppointmentEntity.kt](file:///C:/Users/lenovo/Projects/sheduling and planning/sphere orbit/core/database/src/main/java/com/orbits/data/appointments/AppointmentEntity.kt)
#### [NEW] [AuthEntity.kt](file:///C:/Users/lenovo/Projects/sheduling and planning/sphere orbit/core/database/src/main/java/com/orbits/data/auth/AuthEntity.kt)
#### [NEW] [CalendarEventEntity.kt](file:///C:/Users/lenovo/Projects/sheduling and planning/sphere orbit/core/database/src/main/java/com/orbits/data/calendar/CalendarEventEntity.kt)
#### [NEW] [MessageEntity.kt](file:///C:/Users/lenovo/Projects/sheduling and planning/sphere orbit/core/database/src/main/java/com/orbits/data/chat/MessageEntity.kt)
#### [NEW] [EventEntity.kt](file:///C:/Users/lenovo/Projects/sheduling and planning/sphere orbit/core/database/src/main/java/com/orbits/data/events/EventEntity.kt)
#### [NEW] [MeetingEntity.kt](file:///C:/Users/lenovo/Projects/sheduling and planning/sphere orbit/core/database/src/main/java/com/orbits/data/meetings/MeetingEntity.kt)
#### [NEW] [NoteEntity.kt](file:///C:/Users/lenovo/Projects/sheduling and planning/sphere orbit/core/database/src/main/java/com/orbits/data/notes/NoteEntity.kt)
#### [NEW] [SettingsEntity.kt](file:///C:/Users/lenovo/Projects/sheduling and planning/sphere orbit/core/database/src/main/java/com/orbits/data/settings/SettingsEntity.kt)
#### [NEW] [TaskEntity.kt](file:///C:/Users/lenovo/Projects/sheduling and planning/sphere orbit/core/database/src/main/java/com/orbits/data/tasks/TaskEntity.kt)

#### [MODIFY] [OrbitsDatabase.kt](file:///C:/Users/lenovo/Projects/sheduling and planning/sphere orbit/core/database/src/main/java/com/orbits/core/database/OrbitsDatabase.kt)
Update imports and include `SyncQueueEntity` in the `@Database` annotation.

#### [MODIFY] [build.gradle.kts](file:///C:/Users/lenovo/Projects/sheduling and planning/sphere orbit/core/database/build.gradle.kts)
Remove dependencies on `:core:data:X` modules.

### [core:data:analytics] (and others)

#### [DELETE] [AnalyticsEntity.kt](file:///C:/Users/lenovo/Projects/sheduling and planning/sphere orbit/core/data/analytics/src/main/java/com/orbits/data/analytics/local/AnalyticsEntity.kt)
Remove the entity from the feature module.

#### [MODIFY] [AnalyticsRepositoryImpl.kt](file:///C:/Users/lenovo/Projects/sheduling and planning/sphere orbit/core/data/analytics/src/main/java/com/orbits/data/analytics/AnalyticsRepositoryImpl.kt)
Update imports to reference the entity in `:core:database`.

## Verification Plan

### Automated Tests
- Run `:core:database:assembleDebug` to verify the module builds without external dependencies.
- Run `:core:data:analytics:assembleDebug` to verify it can still see the entities and DAOs.
- Run a full project build `./gradlew assembleDebug` to ensure all circularities are gone.
