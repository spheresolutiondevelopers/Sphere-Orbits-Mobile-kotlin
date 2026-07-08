# 📱 Sphere Android

> **Multi-Module Architecture · Offline-First · Production-Ready**

## 📋 Table of Contents
- [Overview](#overview)
- [Why This Architecture](#why-this-architecture)
- [Project Structure](#project-structure)
- [Tech Stack](#tech-stack)
- [Setup Instructions](#setup-instructions)
- [Module Descriptions](#module-descriptions)
- [Coding Standards](#coding-standards)
- [Build & CI/CD](#build--cicd)
- [Testing Strategy](#testing-strategy)
- [Contribution Guidelines](#contribution-guidelines)
- [License](#license)

---

## Overview

Sphere Android is a modern, production-grade Android application built with a **decentralized multi-module architecture** designed to support teams of 50+ engineers and millions of users. The architecture prioritizes:

- **Compilation isolation** — Changes in one feature don't trigger recompilation of others
- **Maximum build parallelization** — Gradle compiles independent modules concurrently
- **Offline-first capability** — Full CRUD operations available without network connectivity
- **Scalable codebase** — Clear separation of concerns enabling independent team ownership

---

## Why This Architecture

The traditional monolithic `:core:model` approach creates a **massive compilation bottleneck**:

- Changing a single field in `TaskEntity` would force `:feature:chat`, `:feature:settings`, and everything in between to recompile
- Incremental build times degrade exponentially as the team grows
- Merge conflicts become frequent as multiple engineers touch the same central models

### The Solution: Decentralized Data & Domain Modules

```
:core:data:tasks/
├── local/TaskEntity.kt        ← Changes here only recompile :core:data:tasks + :feature:tasks
├── remote/TaskDto.kt          ← Changes here only recompile :core:data:tasks
└── mappers/TaskMapper.kt      ← Changes here only recompile :core:data:tasks
```

**Key Benefits:**
- ✅ Build times remain fast regardless of team size
- ✅ Engineers own their feature modules end-to-end
- ✅ Domain models remain pure — no Android dependencies
- ✅ KMP-ready core business logic

---

## Project Structure

```
sphere-android/
├── build.gradle.kts                                      # Root build file: applies plugins, defines classpath
├── settings.gradle.kts                                   # Declares all modules to include in the build
├── gradle/
│   ├── libs.versions.toml                                # Central version catalog (all dependencies in one place)
│   └── wrapper/
│       ├── gradle-wrapper.jar
│       └── gradle-wrapper.properties                     # Gradle distribution version
├── build-logic/                                          # Custom convention plugins (replaces buildSrc for better caching)
│   ├── build.gradle.kts
│   ├── settings.gradle.kts
│   └── src/main/kotlin/
│       ├── Dependencies.kt                               # Helper functions to apply dependency groups
│       ├── AndroidApplicationConvention.kt               # Plugin for :app (applies Android, Hilt, Compose)
│       ├── AndroidFeatureConvention.kt                   # Plugin for feature/* (applies library, Compose, Hilt, auto-adds common + domain deps)
│       ├── AndroidLibraryConvention.kt                   # Plugin for core/* (applies library, Kotlin, Hilt)
│       └── HiltConvention.kt                             # Applies Hilt and Kapt plugins
├── app/                                                  # Main Android application entry point
│   ├── build.gradle.kts                                  # Applies ApplicationConvention, depends on features
│   └── src/main/java/com/orbits/app/
│       ├── OrbitsApplication.kt                          # Application class (Hilt entry point, initializes Timber)
│       ├── MainActivity.kt                               # Single Activity host for Compose. Sets up NavHost.
│       └── navigation/
│           └── AppNavGraph.kt                            # Defines the root navigation graph (auth, main, settings)
├── core/                                                 # Shared infrastructure modules (no business logic)
│   ├── common/                                           # @orbits/core-common
│   │   ├── build.gradle.kts
│   │   └── src/main/java/com/orbits/core/common/
│   │       ├── Result.kt                                 # Sealed class for Success/Error handling
│   │       ├── Logger.kt                                 # Timber/Kermit wrapper
│   │       ├── extensions/
│   │       │   ├── DateExtensions.kt                     # Formatting, ISO conversions
│   │       │   └── StringExtensions.kt                   # Validation helpers
│   │       └── utils/
│   │           └── IdGenerator.kt                        # Generates unique IDs (UUID)
│   │
│   ├── model/                                            # @orbits/core-model
│   │   ├── build.gradle.kts
│   │   └── src/main/java/com/orbits/core/model/
│   │       └── UserDto.kt                                # ONLY GLOBALLY SHARED MODELS (e.g., User, ApiError, Pagination)
│   │
│   ├── network/                                          # @orbits/core-network
│   │   ├── build.gradle.kts
│   │   └── src/main/java/com/orbits/core/network/
│   │       ├── ApiClient.kt                              # Retrofit builder
│   │       ├── AuthInterceptor.kt                        # Adds Bearer token to requests
│   │       ├── LoggingInterceptor.kt                     # Logs HTTP traffic
│   │       ├── NetworkMonitor.kt                         # Checks connectivity status
│   │       ├── api/                                      # Retrofit service definitions
│   │       │   ├── AuthApi.kt
│   │       │   ├── TaskApi.kt
│   │       │   ├── CalendarApi.kt
│   │       │   ├── MeetingApi.kt
│   │       │   ├── AppointmentApi.kt
│   │       │   ├── NoteApi.kt
│   │       │   ├── ChatApi.kt
│   │       │   ├── AnalyticsApi.kt
│   │       │   ├── SettingsApi.kt
│   │       │   └── SyncApi.kt
│   │       └── di/
│   │           └── NetworkModule.kt                      # Hilt module: provides Retrofit, OkHttp, interceptors
│   │
│   ├── database/                                         # @orbits/core-database
│   │   ├── build.gradle.kts
│   │   └── src/main/java/com/orbits/core/database/
│   │       ├── OrbitsDatabase.kt                         # Abstract RoomDatabase class (references Entities from :core:data)
│   │       ├── SchemaVersion.kt
│   │       ├── migrations/                               # SQL migration files (shipped with the app)
│   │       │   ├── 001_init.sql
│   │       │   ├── 002_add_events.sql
│   │       │   ├── 003_sync_columns.sql
│   │       │   └── 004_sync_queue.sql
│   │       ├── dao/                                      # Room DAO interfaces (generated implementations by Room)
│   │       │   ├── TaskDao.kt
│   │       │   ├── CalendarEventDao.kt
│   │       │   ├── MeetingDao.kt
│   │       │   ├── AppointmentDao.kt
│   │       │   ├── NoteDao.kt
│   │       │   ├── UserDao.kt
│   │       │   ├── MessageDao.kt
│   │       │   ├── AnalyticsDao.kt
│   │       │   ├── SettingsDao.kt
│   │       │   └── SyncQueueDao.kt
│   │       └── di/
│   │           └── DatabaseModule.kt                     # Hilt module: provides AppDatabase, DAOs
│   │
│   ├── sync/                                             # @orbits/core-sync (Offline-first sync engine)
│   │   ├── build.gradle.kts
│   │   └── src/main/java/com/orbits/core/sync/
│   │       ├── SyncService.kt                            # Orchestrates pull/push logic
│   │       ├── SyncWorker.kt                             # WorkManager background worker
│   │       ├── ConflictResolver.kt                       # Handles conflict resolution strategies (LWW, Server wins)
│   │       ├── SyncQueueManager.kt                       # Manages the sync_queue table
│   │       ├── SyncStatus.kt                             # Sealed class representing sync states
│   │       ├── SyncScheduler.kt                          # Schedules periodic syncs using WorkManager
│   │       └── di/
│   │           └── SyncModule.kt                         # Hmit module: provides SyncService, SyncScheduler
│   │
│   ├── testing/                                          # @orbits/core-testing (shared test helpers)
│   │   ├── build.gradle.kts
│   │   └── src/main/java/com/orbits/core/testing/
│   │       ├── TestCoroutineRule.kt                      # JUnit rule for coroutines
│   │       ├── TestData.kt                               # Static test data (lists of models)
│   │       └── TestDispatcher.kt                         # Provides test dispatchers
│   │
│   ├── data/                                             # Decentralised data modules (one per feature)
│   │   ├── auth/
│   │   │   ├── build.gradle.kts
│   │   │   └── src/main/java/com/orbits/data/auth/
│   │   │       ├── AuthRepositoryImpl.kt                 # Implements AuthRepository
│   │   │       ├── TokenManager.kt                       # DataStore for JWT
│   │   │       ├── OAuthClient.kt                        # Handles Google/Microsoft OAuth
│   │   │       └── di/
│   │   │           └── DataModule.kt                     # Binds AuthRepository to implementation
│   │   ├── tasks/
│   │   │   ├── build.gradle.kts
│   │   │   └── src/main/java/com/orbits/data/tasks/
│   │   │       ├── TaskRepositoryImpl.kt                 # Implements TaskRepository (injects TaskDao, TaskApi)
│   │   │       ├── local/
│   │   │       │   └── TaskEntity.kt                     # Room Entity. ISOLATED HERE. Changes won't recompile chat.
│   │   │       ├── remote/
│   │   │       │   └── TaskDto.kt                        # API DTO. ISOLATED HERE.
│   │   │       ├── mappers/
│   │   │       │   └── TaskMapper.kt                     # Maps TaskEntity <-> TaskDomain & TaskDto <-> TaskDomain
│   │   │       └── di/
│   │   │           └── DataModule.kt                     # Binds TaskRepository
│   │   ├── calendar/
│   │   │   ├── build.gradle.kts
│   │   │   └── src/main/java/com/orbits/data/calendar/
│   │   │       ├── CalendarRepositoryImpl.kt
│   │   │       ├── local/
│   │   │       │   └── CalendarEventEntity.kt
│   │   │       ├── remote/
│   │   │       │   ├── CalendarEventDto.kt
│   │   │       │   ├── GoogleCalendarClient.kt
│   │   │       │   └── OutlookCalendarClient.kt
│   │   │       ├── mappers/
│   │   │       │   └── CalendarEventMapper.kt
│   │   │       └── di/
│   │   │           └── DataModule.kt
│   │   ├── events/
│   │   │   ├── build.gradle.kts
│   │   │   └── src/main/java/com/orbits/data/events/
│   │   │       ├── EventRepositoryImpl.kt
│   │   │       ├── local/
│   │   │       │   └── EventEntity.kt
│   │   │       ├── remote/
│   │   │       │   └── EventDto.kt
│   │   │       ├── mappers/
│   │   │       │   └── EventMapper.kt
│   │   │       └── di/
│   │   │           └── DataModule.kt
│   │   ├── meetings/
│   │   │   ├── build.gradle.kts
│   │   │   └── src/main/java/com/orbits/data/meetings/
│   │   │       ├── MeetingRepositoryImpl.kt
│   │   │       ├── local/
│   │   │       │   └── MeetingEntity.kt
│   │   │       ├── remote/
│   │   │       │   ├── MeetingDto.kt
│   │   │       │   ├── ZoomClient.kt
│   │   │       │   └── TeamsClient.kt
│   │   │       ├── mappers/
│   │   │       │   └── MeetingMapper.kt
│   │   │       └── di/
│   │   │           └── DataModule.kt
│   │   ├── appointments/
│   │   │   ├── build.gradle.kts
│   │   │   └── src/main/java/com/orbits/data/appointments/
│   │   │       ├── AppointmentRepositoryImpl.kt
│   │   │       ├── local/
│   │   │       │   └── AppointmentEntity.kt
│   │   │       ├── remote/
│   │   │       │   └── AppointmentDto.kt
│   │   │       ├── mappers/
│   │   │       │   └── AppointmentMapper.kt
│   │   │       └── di/
│   │   │           └── DataModule.kt
│   │   ├── chat/
│   │   │   ├── build.gradle.kts
│   │   │   └── src/main/java/com/orbits/data/chat/
│   │   │       ├── ChatRepositoryImpl.kt
│   │   │       ├── WebSocketClient.kt
│   │   │       ├── local/
│   │   │       │   └── MessageEntity.kt
│   │   │       ├── remote/
│   │   │       │   └── MessageDto.kt
│   │   │       ├── mappers/
│   │   │       │   └── MessageMapper.kt
│   │   │       └── di/
│   │   │           └── DataModule.kt
│   │   ├── notes/
│   │   │   ├── build.gradle.kts
│   │   │   └── src/main/java/com/orbits/data/notes/
│   │   │       ├── NotesRepositoryImpl.kt
│   │   │       ├── MarkdownParser.kt
│   │   │       ├── local/
│   │   │       │   └── NoteEntity.kt
│   │   │       ├── remote/
│   │   │       │   └── NoteDto.kt
│   │   │       ├── mappers/
│   │   │       │   └── NoteMapper.kt
│   │   │       └── di/
│   │   │           └── DataModule.kt
│   │   ├── analytics/
│   │   │   ├── build.gradle.kts
│   │   │   └── src/main/java/com/orbits/data/analytics/
│   │   │       ├── AnalyticsRepositoryImpl.kt
│   │   │       ├── ReportGenerator.kt
│   │   │       ├── local/
│   │   │       │   └── AnalyticsEntity.kt
│   │   │       ├── remote/
│   │   │       │   └── AnalyticsDto.kt
│   │   │       ├── mappers/
│   │   │       │   └── AnalyticsMapper.kt
│   │   │       └── di/
│   │   │           └── DataModule.kt
│   │   └── settings/
│   │       ├── build.gradle.kts
│   │       └── src/main/java/com/orbits/data/settings/
│   │           ├── SettingsRepositoryImpl.kt
│   │           ├── local/
│   │           │   └── SettingsEntity.kt
│   │           ├── remote/
│   │           │   └── SettingsDto.kt
│   │           ├── mappers/
│   │           │   └── SettingsMapper.kt
│   │           └── di/
│   │               └── DataModule.kt
│   │
│   └── domain/                                           # Decentralised Domain modules (Pure Business Logic)
│       ├── auth/
│       │   ├── build.gradle.kts
│       │   └── src/main/java/com/orbits/domain/auth/
│       │       ├── AuthRepository.kt                     # Interface
│       │       ├── LoginUseCase.kt
│       │       ├── LogoutUseCase.kt
│       │       ├── ValidateTokenUseCase.kt
│       │       └── RefreshTokenUseCase.kt
│       ├── tasks/
│       │   ├── build.gradle.kts
│       │   └── src/main/java/com/orbits/domain/tasks/
│       │       ├── Task.kt                               # PURE DOMAIN MODEL (UI-safe). ISOLATED HERE.
│       │       ├── TaskRepository.kt                     # Interface
│       │       ├── GetTasksUseCase.kt
│       │       ├── CreateTaskUseCase.kt
│       │       ├── UpdateTaskUseCase.kt
│       │       ├── CompleteTaskUseCase.kt
│       │       └── DeleteTaskUseCase.kt
│       ├── calendar/
│       │   ├── build.gradle.kts
│       │   └── src/main/java/com/orbits/domain/calendar/
│       │       ├── CalendarEvent.kt                      # Domain Model
│       │       ├── CalendarRepository.kt
│       │       ├── GetEventsUseCase.kt
│       │       ├── CreateEventUseCase.kt
│       │       ├── UpdateEventUseCase.kt
│       │       └── SyncCalendarUseCase.kt
│       ├── events/
│       │   ├── build.gradle.kts
│       │   └── src/main/java/com/orbits/domain/events/
│       │       ├── Event.kt                              # Domain Model
│       │       ├── EventRepository.kt
│       │       ├── GetEventsUseCase.kt
│       │       ├── CreateEventUseCase.kt
│       │       ├── UpdateEventUseCase.kt
│       │       └── DeleteEventUseCase.kt
│       ├── meetings/
│       │   ├── build.gradle.kts
│       │   └── src/main/java/com/orbits/domain/meetings/
│       │       ├── Meeting.kt                            # Domain Model
│       │       ├── MeetingRepository.kt
│       │       ├── GetMeetingsUseCase.kt
│       │       ├── CreateMeetingUseCase.kt
│       │       ├── UpdateMeetingUseCase.kt
│       │       └── JoinMeetingUseCase.kt
│       ├── appointments/
│       │   ├── build.gradle.kts
│       │   └── src/main/java/com/orbits/domain/appointments/
│       │       ├── Appointment.kt                        # Domain Model
│       │       ├── AppointmentRepository.kt
│       │       ├── GetAppointmentsUseCase.kt
│       │       ├── BookAppointmentUseCase.kt
│       │       ├── UpdateAppointmentUseCase.kt
│       │       └── CancelAppointmentUseCase.kt
│       ├── chat/
│       │   ├── build.gradle.kts
│       │   └── src/main/java/com/orbits/domain/chat/
│       │       ├── Message.kt                            # Domain Model
│       │       ├── ChatRepository.kt
│       │       ├── GetMessagesUseCase.kt
│       │       ├── SendMessageUseCase.kt
│       │       ├── ObserveMessagesUseCase.kt
│       │       └── MarkMessageReadUseCase.kt
│       ├── notes/
│       │   ├── build.gradle.kts
│       │   └── src/main/java/com/orbits/domain/notes/
│       │       ├── Note.kt                               # Domain Model
│       │       ├── NotesRepository.kt
│       │       ├── GetNotesUseCase.kt
│       │       ├── SaveNoteUseCase.kt
│       │       ├── PinNoteUseCase.kt
│       │       └── DeleteNoteUseCase.kt
│       ├── analytics/
│       │   ├── build.gradle.kts
│       │   └── src/main/java/com/orbits/domain/analytics/
│       │       ├── AnalyticsData.kt                      # Domain Model
│       │       ├── AnalyticsRepository.kt
│       │       ├── GetProductivityScoreUseCase.kt
│       │       ├── GenerateReportUseCase.kt
│       │       └── GetTaskCompletionTrendUseCase.kt
│       └── settings/
│           ├── build.gradle.kts
│           └── src/main/java/com/orbits/domain/settings/
│               ├── Settings.kt                           # Domain Model
│               ├── SettingsRepository.kt
│               ├── GetSettingsUseCase.kt
│               ├── UpdateProfileUseCase.kt
│               ├── UpdateThemeUseCase.kt
│               └── UpdateNotificationPreferencesUseCase.kt
│
└── feature/                                             # Presentation modules (UI + ViewModels)
    ├── auth/
    │   ├── build.gradle.kts
    │   └── src/main/java/com/orbits/feature/auth/
    │       ├── LoginScreen.kt                            # Composable for login
    │       ├── SignupScreen.kt                           # Composable for registration
    │       ├── AuthViewModel.kt                          # Calls LoginUseCase, LogoutUseCase
    │       ├── AuthState.kt                               # UI State (Loading, Success, Error)
    │       ├── AuthEvent.kt                               # UI Events (User interactions)
    │       └── navigation/
    │           └── AuthNavGraph.kt                       # Navigation graph for auth screens
    ├── dashboard/
    │   ├── build.gradle.kts
    │   └── src/main/java/com/orbits/feature/dashboard/
    │       ├── DashboardScreen.kt
    │       ├── DashboardViewModel.kt                     # Aggregates stats from various UseCases
    │       ├── DashboardState.kt
    │       ├── DashboardEvent.kt
    │       ├── components/
    │       │   ├── StatCard.kt
    │       │   ├── QuickActionChip.kt
    │       │   └── ProgressRing.kt
    │       └── navigation/
    │           └── DashboardNavGraph.kt
    ├── tasks/
    │   ├── build.gradle.kts
    │   └── src/main/java/com/orbits/feature/tasks/
    │       ├── TaskListScreen.kt
    │       ├── TaskDetailScreen.kt
    │       ├── TaskViewModel.kt                          # Uses GetTasks, CreateTask, CompleteTask UseCases
    │       ├── TaskState.kt
    │       ├── TaskEvent.kt
    │       ├── components/
    │       │   ├── TaskFilterBar.kt
    │       │   ├── TaskList.kt
    │       │   └── TaskItem.kt
    │       └── navigation/
    │           └── TasksNavGraph.kt
    ├── calendar/
    │   ├── build.gradle.kts
    │   └── src/main/java/com/orbits/feature/calendar/
    │       ├── CalendarScreen.kt
    │       ├── CalendarViewModel.kt
    │       ├── CalendarState.kt
    │       ├── CalendarEvent.kt
    │       ├── components/
    │       │   ├── MonthView.kt
    │       │   ├── WeekView.kt
    │       │   ├── DayView.kt
    │       │   └── AgendaView.kt
    │       └── navigation/
    │           └── CalendarNavGraph.kt
    ├── events/
    │   ├── build.gradle.kts
    │   └── src/main/java/com/orbits/feature/events/
    │       ├── EventListScreen.kt
    │       ├── EventDetailScreen.kt
    │       ├── EventFormScreen.kt
    │       ├── EventViewModel.kt
    │       ├── EventState.kt
    │       ├── EventEvent.kt
    │       ├── components/
    │       │   ├── EventCard.kt
    │       │   ├── EventTimeline.kt
    │       │   ├── BudgetTracker.kt
    │       │   └── RSVPList.kt
    │       └── navigation/
    │           └── EventsNavGraph.kt
    ├── meetings/
    │   ├── build.gradle.kts
    │   └── src/main/java/com/orbits/feature/meetings/
    │       ├── MeetingListScreen.kt
    │       ├── MeetingDetailScreen.kt
    │       ├── MeetingViewModel.kt
    │       ├── MeetingState.kt
    │       ├── MeetingEvent.kt
    │       ├── components/
    │       │   ├── MeetingCard.kt
    │       │   ├── MeetingFilter.kt
    │       │   └── JoinMeetingButton.kt
    │       └── navigation/
    │           └── MeetingsNavGraph.kt
    ├── appointments/
    │   ├── build.gradle.kts
    │   └── src/main/java/com/orbits/feature/appointments/
    │       ├── AppointmentListScreen.kt
    │       ├── AppointmentFormScreen.kt
    │       ├── AppointmentViewModel.kt
    │       ├── AppointmentState.kt
    │       ├── AppointmentEvent.kt
    │       ├── components/
    │       │   ├── AppointmentCard.kt
    │       │   └── BookingForm.kt
    │       └── navigation/
    │           └── AppointmentsNavGraph.kt
    ├── chat/
    │   ├── build.gradle.kts
    │   └── src/main/java/com/orbits/feature/chat/
    │       ├── ChatListScreen.kt
    │       ├── ConversationScreen.kt
    │       ├── ChatViewModel.kt
    │       ├── ChatState.kt
    │       ├── ChatEvent.kt
    │       ├── components/
    │       │   ├── ChatList.kt
    │       │   ├── MessageItem.kt
    │       │   ├── MessageInput.kt
    │       │   └── SharedMedia.kt
    │       └── navigation/
    │           └── ChatNavGraph.kt
    ├── notes/
    │   ├── build.gradle.kts
    │   └── src/main/java/com/orbits/feature/notes/
    │       ├── NotesListScreen.kt
    │       ├── NoteEditorScreen.kt
    │       ├── NotesViewModel.kt
    │       ├── NotesState.kt
    │       ├── NotesEvent.kt
    │       ├── components/
    │       │   ├── NoteCard.kt
    │       │   ├── NoteFilter.kt
    │       │   └── RichTextEditor.kt
    │       └── navigation/
    │           └── NotesNavGraph.kt
    ├── analytics/
    │   ├── build.gradle.kts
    │   └── src/main/java/com/orbits/feature/analytics/
    │       ├── AnalyticsScreen.kt
    │       ├── AnalyticsViewModel.kt
    │       ├── AnalyticsState.kt
    │       ├── AnalyticsEvent.kt
    │       ├── components/
    │       │   ├── StatCard.kt
    │       │   ├── BarChart.kt
    │       │   ├── PieChart.kt
    │       │   ├── LineChart.kt
    │       │   └── ReportExportButton.kt
    │       └── navigation/
    │           └── AnalyticsNavGraph.kt
    └── settings/
        ├── build.gradle.kts
        └── src/main/java/com/orbits/feature/settings/
            ├── SettingsScreen.kt
            ├── ProfileEditScreen.kt
            ├── IntegrationsScreen.kt
            ├── SettingsViewModel.kt
            ├── SettingsState.kt
            ├── SettingsEvent.kt
            ├── components/
            │   ├── ProfileForm.kt
            │   ├── ThemeToggle.kt
            │   └── IntegrationList.kt
            └── navigation/
                └── SettingsNavGraph.kt

└── README.md
```

---

## Tech Stack

| Layer | Technology | Version | Purpose |
|-------|------------|---------|---------|
| **UI** | Jetpack Compose | 2024.10.00 | Modern declarative UI toolkit |
| **Navigation** | Compose Navigation | 2.8.0 | Type-safe navigation between screens |
| **DI** | Dagger Hilt | 2.51.1 | Dependency injection across all modules |
| **Networking** | Retrofit | 2.9.0 | Type-safe HTTP client for API calls |
| **Serialization** | Kotlinx.serialization | 1.6.3 | JSON serialization/deserialization |
| **Database** | Room | 2.6.1 | SQLite ORM with compile-time verification |
| **Async** | Kotlin Coroutines | 1.7.3 | Structured concurrency for background tasks |
| **State Management** | Kotlin Flow | 1.7.3 | Reactive streams for UI state |
| **Background Work** | WorkManager | 2.9.0 | Deferred background tasks (sync) |
| **Logging** | Timber | 5.0.1 | Structured logging |
| **Testing** | JUnit4 + Espresso | 4.13.2 + 3.5.1 | Unit and UI testing |
| **Build** | Gradle + KTS | 8.2.0 | Kotlin DSL for build scripts |
| **Version Catalog** | TOML | - | Centralized dependency management |

---

## Setup Instructions

### Prerequisites

- Android Studio Hedgehog (2023.1.1) or later
- JDK 17 or later
- Android SDK 34 (API level 34)
- Gradle 8.2+

### Clone & Build

```bash
git clone https://github.com/sphere-solutions/sphere-android.git
cd sphere-android
./gradlew clean build
```

### Run the App

1. Open the project in Android Studio
2. Select a device (emulator or physical)
3. Click Run (▶) or use the terminal:
   ```bash
   ./gradlew :app:assembleDebug
   ./gradlew :app:installDebug
   ```

### Generate Signed APK/Bundle

```bash
./gradlew :app:assembleRelease
```

---

## Module Descriptions

### 🧱 Build Logic (`/build-logic`)

Custom convention plugins that replace buildSrc for better caching. Defines standardized build configurations:

- `AndroidApplicationConvention.kt` — Configures the `:app` module
- `AndroidFeatureConvention.kt` — Configures all `:feature` modules
- `AndroidLibraryConvention.kt` — Configures all `:core` modules
- `HiltConvention.kt` — Applies Hilt & Kapt consistently

### 📱 App (`/app`)

The application entry point. Thin shell that composes the navigation graph and injects all feature modules.

### 🧩 Core (`/core`)

#### `:core:common`
Shared utilities: Result wrapper, logging, extension functions, ID generation.

#### `:core:model`
ONLY globally shared models (e.g., `UserDto`, `ApiError`, `Pagination`). All feature-specific models live in their respective `:core:data` modules.

#### `:core:network`
Retrofit configuration, interceptors, and API service definitions. Provides:

- `ApiClient` — Retrofit instance with OkHttp
- `AuthInterceptor` — Adds Bearer token
- `LoggingInterceptor` — HTTP logging
- `NetworkMonitor` — Connectivity status

#### `:core:database`
Room database configuration, DAOs, and migration files.

- `OrbitsDatabase` — Abstract RoomDatabase class
- DAOs: `TaskDao`, `CalendarEventDao`, `MeetingDao`, etc.
- Migrations: SQL scripts for version upgrades

> **Important:** This module must include implementation dependencies on each `:core:data:X` module to reference entities in the `@Database` annotation.

#### `:core:sync`
Offline-first sync engine. Manages background sync using WorkManager and conflict resolution.

#### `:core:testing`
Shared test utilities: coroutine test rules, test dispatchers, and test data factories.

### 🗄️ Data Layer (`/core/data`)

Decentralized by feature. Each submodule contains:

- `local/` — Room Entity (marked `internal`)
- `remote/` — DTO for API responses (marked `internal`)
- `mappers/` — Maps Entity ↔ Domain ↔ DTO (marked `internal`)
- `*RepositoryImpl.kt` — Concrete repository implementation (marked `internal`)
- `di/DataModule.kt` — Hilt module binding the interface to implementation

Example: `:core:data:tasks` contains all task-specific data logic.

### 🧠 Domain Layer (`/core/domain`)

Decentralized by feature. Contains pure business logic with zero Android dependencies:

- `Task.kt` — Pure domain model (UI-safe)
- `TaskRepository.kt` — Interface (public)
- `GetTasksUseCase.kt`, `CreateTaskUseCase.kt`, etc. — Use cases encapsulating business logic

> **Key Principle:** Domain modules depend only on the Kotlin Standard Library.

### 🎨 Feature Layer (`/feature`)

Decentralized by feature. Contains UI and ViewModels:

- `*Screen.kt` — Composable functions (UI)
- `*ViewModel.kt` — Uses UseCases to manage UI state
- `*State.kt` — UI state (data class)
- `*Event.kt` — UI events (sealed class)
- `components/` — Reusable UI components
- `navigation/` — Feature-specific navigation graphs

---

## Coding Standards

### Package Structure

```
com.orbits.core.<layer>.<feature>.<component>
```

### Naming Conventions

| Type | Convention | Example |
|------|------------|---------|
| Entities | `*Entity` | `TaskEntity` |
| DTOs | `*Dto` | `TaskDto` |
| Domain Models | `*` (plain) | `Task` |
| Mappers | `*Mapper` | `TaskMapper` |
| Repositories (Interface) | `*Repository` | `TaskRepository` |
| Repositories (Impl) | `*RepositoryImpl` | `TaskRepositoryImpl` |
| Use Cases | `*UseCase` | `GetTasksUseCase` |
| ViewModels | `*ViewModel` | `TaskViewModel` |
| Composables | `*Screen` | `TaskListScreen` |

### Visibility Rules

| Module Type | Default Visibility |
|-------------|---------------------|
| Domain Models | `public` |
| Repository Interfaces | `public` |
| Entity/DTO/Mapper | `internal` |
| Repository Impl | `internal` |
| Use Cases | `public` |
| ViewModels | `public` |
| UI Composables | `public` |

### Dependency Direction

```
Feature → Domain → Data/Database/Network/Common
         ↑
         Data (implements Domain interfaces)
         ↑
         Database/Network (implementation details)
```

- **Never:** Feature → Data (bypassing Domain)
- **Never:** Data → Feature

---

## Build & CI/CD

### Gradle Version Catalog

All dependencies are defined in `gradle/libs.versions.toml`:

```toml
[versions]
kotlin = "1.9.20"
coroutines = "1.7.3"
room = "2.6.1"

[libraries]
kotlin-coroutines = { group = "org.jetbrains.kotlinx", name = "kotlinx-coroutines-android", version.ref = "coroutines" }
room-runtime = { group = "androidx.room", name = "room-runtime", version.ref = "room" }
```

### Convention Plugins

Use standardized plugins to reduce boilerplate:

```kotlin
// In any module's build.gradle.kts
plugins {
    id("orbits.android.feature")  // or .library, .application
}

// Dependencies are added automatically based on module type
dependencies {
    // Only add feature-specific dependencies
    implementation(project(":core:domain:tasks"))
}
```

### Build Types

- **Debug** — Fast builds, logging enabled
- **Release** — R8 optimization, proguard, logging disabled
- **Staging** — QA builds with debug logging

---

## Testing Strategy

### Unit Tests

```kotlin
@Test
fun `getTasks returns success when repository succeeds`() = runTest {
    val useCase = GetTasksUseCase(mockRepository)
    val result = useCase()
    assert(result is Result.Success)
}
```

### Integration Tests

Test database operations and API clients:

```kotlin
@Test
fun `taskDao inserts and retrieves task correctly`() {
    val task = TaskEntity(...)
    taskDao.insert(task)
    val retrieved = taskDao.getById(task.id)
    assertEquals(task, retrieved)
}
```

### UI Tests (Compose)

```kotlin
@Test
fun `task list displays tasks`() {
    composeTestRule.setContent {
        TaskListScreen(...)
    }
    composeTestRule.onNodeWithText("My Task").assertIsDisplayed()
}
```

### Test Coverage Targets

| Layer | Minimum Coverage |
|-------|-------------------|
| Domain | 95% |
| Data | 85% |
| UI | 70% |

---

## Contribution Guidelines

### Branching Strategy

```
main
  ├── develop
  │   ├── feature/task-crud
  │   ├── feature/calendar-sync
  │   └── bugfix/sync-worker
  └── release/v1.0.0
```

### Commit Message Format

```
feat(calendar): add two-way sync with Google Calendar
fix(sync): resolve conflict resolution for deleted entities
docs(readme): update setup instructions
test(domain): add unit tests for CreateTaskUseCase
```

### PR Checklist

- [ ] All tests pass locally
- [ ] Code follows project style
- [ ] Documentation updated (if applicable)
- [ ] No new compilation warnings
- [ ] CI/CD pipeline passes

---

## License

```
Copyright © 2026 Sphere Solution Developers

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```

---

## 👥 Contributors

| Name | Role |
|------|------|
| Sphere Solution Developers | Core Team |

---

*Documentation Version: 1.0.0 · Last Updated: April 2026*

---

**This README is the single source of truth for the project architecture and should be the first file any new engineer reads. It covers:**

- ✅ Complete module tree with inline descriptions
- ✅ Why this architecture exists (the compilation bottleneck problem)
- ✅ Full tech stack with versions
- ✅ Setup instructions
- ✅ Coding standards & naming conventions
- ✅ Visibility rules to prevent dependency violations
- ✅ Build & CI/CD setup
- ✅ Testing strategy with coverage targets
- ✅ Contribution guidelines

