# Dorian Steem Apps - Project Structure Analysis

> Analysis Date: 2026-02-13

## 📋 Table of Contents

1. [Project Overview](#project-overview)
2. [Module Structure](#module-structure)
3. [Package Structure](#package-structure)
4. [Technology Stack](#technology-stack)
5. [Dependency Graph](#dependency-graph)

---

## Project Overview

**Project Name**: Dorian Steem Apps v1
**Type**: Android Application
**Architecture**: Clean Architecture (Multi-Module)
**Language**: Kotlin 2.1.0
**Min SDK**: 24 (Android 7.0)
**Target SDK**: 36

### Purpose
Android client application for the Steemit blockchain platform

---

## Module Structure

The project consists of 6 modules following Clean Architecture principles.

### 1. dorian-steem-ui (Main Application Module)
**Type**: Android Application
**Package**: `lee.dorian.steem_ui`
**Kotlin Files**: 52
**Layout Files**: 20

#### Main Components
```
dorian-steem-ui/
└── src/main/
    ├── java/lee/dorian/steem_ui/
    │   ├── MainActivity.kt              # Fragment-based main activity (legacy)
    │   ├── Main2Activity.kt             # Compose-based main activity (new)
    │   ├── MainApplciation.kt           # Hilt Application class
    │   ├── SplashActivity.kt            # Splash screen
    │   ├── BaseActivity.kt              # Base activity class
    │   ├── MainViewModel.kt             # Main ViewModel
    │   ├── Colors.kt                    # Color definitions
    │   │
    │   ├── di/                          # Dependency Injection
    │   │   ├── RepositoryModule.kt      # Repository bindings
    │   │   └── CoroutinesModule.kt      # Coroutine configuration
    │   │
    │   ├── ext/                         # Extension functions
    │   │
    │   ├── model/
    │   │   └── navigation/              # Navigation routes
    │   │       ├── PostContentRoute.kt
    │   │       ├── ProfileScreenRoute.kt
    │   │       ├── TagsScreenRoute.kt
    │   │       └── WalletScreenRoute.kt
    │   │
    │   ├── ui/                          # UI Components
    │   │   ├── account_details/         # Account details screen
    │   │   ├── base/                    # Base UI components
    │   │   ├── compose/                 # Reusable Compose components
    │   │   │   ├── ComposeUtil.kt
    │   │   │   ├── CustomTextField.kt
    │   │   │   └── InputForm.kt
    │   │   ├── history/                 # History screen
    │   │   ├── post/                    # Post-related screens
    │   │   │   ├── PostComposable.kt
    │   │   │   ├── PostImagePagerActivity.kt
    │   │   │   ├── content/             # Post content screen
    │   │   │   │   ├── PostContentFragment.kt
    │   │   │   │   ├── PostContentViewModel.kt
    │   │   │   │   ├── PostContentState.kt
    │   │   │   │   ├── PostContentWebChromeClient.kt
    │   │   │   │   └── ReplyListDialogFragment.kt
    │   │   │   └── list/                # Post list screen
    │   │   │       ├── PostListFragment.kt
    │   │   │       └── PostListViewModel.kt
    │   │   ├── preview/                 # Preview screen
    │   │   ├── profile/                 # Profile screen
    │   │   ├── tags/                    # Tags screen
    │   │   │   ├── TagsFragment.kt
    │   │   │   ├── TagsState.kt
    │   │   │   └── TagScreenSortTabInfo.kt
    │   │   ├── voter/                   # Voter list screen
    │   │   │   ├── VoteListActivity.kt
    │   │   │   ├── VoteListViewModel.kt
    │   │   │   └── VoteListAdapter.kt
    │   │   └── wallet/                  # Wallet screen
    │   │
    │   └── util/                        # Utility classes
    │
    ├── res/                             # Android resources
    │   ├── drawable/                    # Drawable resources
    │   ├── layout/                      # XML layouts (20 files)
    │   ├── menu/                        # Menu resources
    │   ├── mipmap-*/                    # App icons
    │   ├── navigation/                  # Navigation graphs
    │   ├── values/                      # Strings, colors, styles
    │   └── xml/                         # XML configuration
    │
    └── AndroidManifest.xml              # Manifest file
```

#### Key Features
- **Dual Navigation System**:
  - `MainActivity`: Fragment + Navigation Component (legacy)
  - `Main2Activity`: Jetpack Compose Navigation (new, recommended)
- **ViewBinding & DataBinding** enabled
- **Hilt Dependency Injection**
- **MVVM Architecture Pattern**

---

### 2. dorian-steem-domain (Domain Layer)
**Type**: Java/Kotlin Library (Pure Kotlin)
**Package**: `lee.dorian.steem_domain`
**Kotlin Files**: 23

#### Main Components
```
dorian-steem-domain/
└── src/main/java/lee/dorian/steem_domain/
    ├── model/                           # Domain models (13 files)
    │   ├── AccountDetails.kt            # Account details
    │   ├── AccountHistory.kt            # Account history
    │   ├── AccountHistoryItem.kt        # History item
    │   ├── AccountHistoryItemLink.kt    # History item link
    │   ├── ActiveVote.kt                # Vote information
    │   ├── ApiResult.kt                 # API result wrapper
    │   ├── DynamicGlobalProperties.kt   # Global properties
    │   ├── FollowCount.kt               # Follow count
    │   ├── Post.kt                      # Post
    │   ├── PostItem.kt                  # Post item
    │   ├── PostListInfo.kt              # Post list info
    │   ├── SteemitProfile.kt            # Steemit profile
    │   └── SteemitWallet.kt             # Steemit wallet
    │
    ├── repository/                      # Repository interfaces
    │   └── SteemRepository.kt           # Steem API repository interface
    │
    ├── usecase/                         # Use Cases (8 files)
    │   ├── ReadAccountDetailsUseCase.kt           # Read account details
    │   ├── ReadAccountHistoryUseCase.kt           # Read account history
    │   ├── ReadDynamicGlobalPropertiesUseCase.kt  # Read global properties
    │   ├── ReadPostAndRepliesUseCase.kt           # Read post and replies
    │   ├── ReadPostsUseCase.kt                    # Read posts
    │   ├── ReadRankedPostsUseCase.kt              # Read ranked posts
    │   ├── ReadSteemitProfileUseCase.kt           # Read profile
    │   └── ReadSteemitWalletUseCase.kt            # Read wallet
    │
    ├── ext/                             # Extension functions
    └── util/                            # Utilities
```

#### Key Features
- **Pure Kotlin module** (no Android dependencies)
- **Single Responsibility Principle**: Each UseCase has a single responsibility
- **Naming Convention**: `Read[Entity][Action]UseCase`
- **JSR-330** `@Inject` annotations

---

### 3. dorian-steem-data (Data Layer)
**Type**: Java/Kotlin Library (Pure Kotlin)
**Package**: `lee.dorian.steem_data`
**Kotlin Files**: 25

#### Main Components
```
dorian-steem-data/
└── src/main/java/lee/dorian/steem_data/
    ├── model/                           # Data models (DTOs)
    │   ├── follow/                      # Follow-related DTOs
    │   ├── history/                     # History-related DTOs
    │   └── post/                        # Post-related DTOs
    │
    ├── repository/                      # Repository implementations
    │   └── SteemRepositoryImpl.kt       # SteemRepository implementation
    │
    └── retrofit/                        # Retrofit configuration
        ├── SteemClient.kt               # Retrofit client
        └── SteemService.kt              # Steem API service interface
```

#### Key Features
- **Retrofit2** based networking
- **Gson Converter**
- **RxJava2 & Coroutines** support
- **DTO → Domain Model conversion** responsibility

---

### 4. dorian-android-ktx
**Type**: Android Library
**Package**: `lee.dorian.dorian_android_ktx`

#### Main Components
```
dorian-android-ktx/
└── src/main/java/lee/dorian/dorian_android_ktx/
    └── android/
        └── context/                     # Context extension functions
            └── (Android-related extensions)
```

#### Key Features
- Extension functions for Android Framework classes
- Examples: `hideKeyboard()`, Context-related utilities

---

### 5. dorian-ktx
**Type**: Java/Kotlin Library (Pure Kotlin)
**Package**: `lee.dorian.dorian_ktx`

#### Main Components
```
dorian-ktx/
└── src/main/java/lee/dorian/dorian_ktx/
    └── (Pure Kotlin extension functions and utilities)
```

#### Key Features
- Pure Kotlin utilities
- No Android dependencies
- General-purpose extension functions

---

### 6. dorian-steem-test
**Type**: Java/Kotlin Library
**Package**: `lee.dorian.steem_test`

#### Main Components
```
dorian-steem-test/
└── src/main/java/lee/dorian/steem_test/
    └── (Test utilities and fixtures)
```

#### Key Features
- Shared test helpers
- Test fixtures
- Mock data

---

## Package Structure

### UI Module Package Structure (lee.dorian.steem_ui)

```
steem_ui/
├── (root)                               # Activity, Application, ViewModel
├── di/                                  # Dependency Injection
├── ext/                                 # Extension functions
├── model/navigation/                    # Navigation routes
├── ui/                                  # UI Components
│   ├── account_details/                 # Feature-based packages
│   ├── base/
│   ├── compose/                         # Reusable Composables
│   ├── history/
│   ├── post/
│   │   ├── content/                     # Sub-features
│   │   └── list/
│   ├── preview/
│   ├── profile/
│   ├── tags/
│   ├── voter/
│   └── wallet/
└── util/                                # Utilities
```

### Domain Module Package Structure (lee.dorian.steem_domain)

```
steem_domain/
├── model/                               # Domain entities
├── repository/                          # Repository interfaces
├── usecase/                             # Business logic
├── ext/                                 # Extension functions
└── util/                                # Utilities
```

### Data Module Package Structure (lee.dorian.steem_data)

```
steem_data/
├── model/                               # DTOs
│   ├── follow/
│   ├── history/
│   └── post/
├── repository/                          # Repository implementations
└── retrofit/                            # API client
```

---

## Technology Stack

### Core
- **Language**: Kotlin 2.1.0
- **Build System**: Gradle 8.9.1
- **JVM Target**: Java 17

### Android
- **Min SDK**: 24 (Android 7.0 Nougat)
- **Target SDK**: 36
- **Compile SDK**: 36

### UI Framework
- **Jetpack Compose**: 2024.10.01 BOM
  - Material3: 1.3.0
  - Navigation Compose: 2.8.0
  - Activity Compose: 1.9.0
- **Traditional Views**: ViewBinding, DataBinding
- **Navigation Component**: 2.7.7 / 2.8.5

### Architecture Components
- **Lifecycle**: 2.8.7
  - ViewModel
  - LiveData
- **Navigation**: 2.8.5

### Dependency Injection
- **Hilt**: 2.51.1
- **Hilt Navigation Compose**: 1.2.0
- **javax.inject**: 1

### Networking
- **Retrofit**: 2.9.0
- **Gson Converter**
- **RxJava Adapter**: 2.3.0

### Async Programming
- **Kotlin Coroutines**: 1.7.3 (recommended)
- **RxJava2**: 2.2.20 (legacy)
- **RxAndroid**: 2.0.1

### Image Loading
- **Coil (Compose)**: 2.7.0
- **Glide (Views)**: 4.15.1

### Markdown & HTML
- **CommonMark**: 0.20.0
- **Jsoup**: 1.16.1
- **Unbescape**: 1.1.6.RELEASE

### UI Components
- **Material Design**: 1.12.0
- **FlexboxLayout**: 3.0.0
- **SwipeRefreshLayout**: 1.1.0
- **ConstraintLayout**: 2.1.4

### Testing
- **JUnit**: 4.13.2
- **AndroidX Test**: 1.1.5
- **Espresso**: 3.5.1
- **Core Testing**: 2.2.0
- **Coroutines Test**: 1.7.3

### Other
- **kotlinx-serialization-json**: 1.6.3

---

## Dependency Graph

### Inter-Module Dependencies

```
┌─────────────────────────────────────────────────────────┐
│                  dorian-steem-ui                        │
│           (Android Application Module)                  │
│  - MainActivity (Fragment-based)                        │
│  - Main2Activity (Compose-based)                        │
│  - ViewModels, Fragments, Composables                   │
│  - Hilt Modules                                         │
└────────────┬────────────────────────┬──────────────────┘
             │                        │
             │ implements             │ depends on
             │                        │
             v                        v
┌────────────────────────┐  ┌────────────────────────────┐
│  dorian-steem-domain   │  │    dorian-android-ktx      │
│   (Pure Kotlin)        │  │   (Android Library)        │
│  - Use Cases           │  │  - Android Extensions      │
│  - Repository Interface│  │  - Context helpers         │
│  - Domain Models       │  └────────────┬───────────────┘
└───────────┬────────────┘               │
            │                            │
            │ interface                  │
            │                            │
            v                            v
┌────────────────────────┐  ┌────────────────────────────┐
│  dorian-steem-data     │  │      dorian-ktx            │
│   (Pure Kotlin)        │  │    (Pure Kotlin)           │
│  - Repository Impl     │  │  - General Extensions      │
│  - Retrofit Service    │  │  - Common Utilities        │
│  - Data Models (DTO)   │  └────────────────────────────┘
└───────────┬────────────┘
            │
            │ uses for testing
            │
            v
┌────────────────────────┐
│  dorian-steem-test     │
│   (Test Library)       │
│  - Test Utilities      │
│  - Mock Data           │
└────────────────────────┘
```

### Layer Separation Principles

```
┌───────────────────��──────────────────────────────────────┐
│                     Presentation Layer                    │
│                    (dorian-steem-ui)                      │
│  - Activities, Fragments, Composables                     │
│  - ViewModels                                             │
│  - UI State                                               │
└───────────────────────┬──────────────────────────────────┘
                        │
                        │ depends on
                        │
                        v
┌──────────────────────────────────────────────────────────┐
│                      Domain Layer                         │
│                 (dorian-steem-domain)                     │
│  - Use Cases (Business Logic)                             │
│  - Repository Interfaces                                  │
│  - Domain Models                                          │
│  ⚠️  NO Android dependencies                              │
└───────────────────────┬──────────────────────────────────┘
                        │
                        │ implements
                        │
                        v
┌──────────────────────────────────────────────────────────┐
│                       Data Layer                          │
│                  (dorian-steem-data)                      │
│  - Repository Implementations                             │
│  - Retrofit API Services                                  │
│  - Data Models (DTOs)                                     │
│  - Data Source (Remote)                                   │
│  ⚠️  NO Android dependencies                              │
└──────────────────────────────────────────────────────────┘
```

---

## File Statistics

| Module | Kotlin Files | Layout Files | Type |
|--------|-------------|--------------|------|
| dorian-steem-ui | 52 | 20 | Android App |
| dorian-steem-domain | 23 | 0 | Pure Kotlin |
| dorian-steem-data | 25 | 0 | Pure Kotlin |
| dorian-android-ktx | - | 0 | Android Library |
| dorian-ktx | - | 0 | Pure Kotlin |
| dorian-steem-test | - | 0 | Test Library |
| **Total** | **~147** | **20** | - |

---

## Key Configuration Files

### Root Level
- `build.gradle` - Project-level build configuration
- `settings.gradle` - Module inclusion settings
- `gradle.properties` - Gradle properties
- `gradle/libs.versions.toml` - **Version Catalog** (centralized dependency management)

### Module Level
- `dorian-steem-ui/build.gradle` - UI module build configuration
- `dorian-steem-domain/build.gradle` - Domain module build configuration
- `dorian-steem-data/build.gradle` - Data module build configuration

### Android Resources
- `dorian-steem-ui/src/main/AndroidManifest.xml` - App manifest
- `dorian-steem-ui/src/main/res/navigation/` - Navigation graphs
- `dorian-steem-ui/src/main/res/values/` - Strings, colors, styles

---

## Architecture Patterns

### Clean Architecture Layers

1. **Presentation Layer** (UI Module)
   - MVVM pattern
   - ViewModels + LiveData/StateFlow
   - Jetpack Compose & Traditional Views

2. **Domain Layer** (Domain Module)
   - Use Cases (Interactors)
   - Repository Interfaces
   - Business Logic
   - Pure Kotlin (No Android)

3. **Data Layer** (Data Module)
   - Repository Implementations
   - API Services (Retrofit)
   - Data Sources
   - DTO ↔ Domain Model Mapping

### Design Patterns

- **Repository Pattern**: Data source abstraction
- **Use Case Pattern**: Business logic encapsulation
- **MVVM Pattern**: Separation of UI and business logic
- **Dependency Injection**: Dependency injection via Hilt/Dagger
- **Observer Pattern**: Reactive UI via LiveData/StateFlow

---

## Migration Status

### UI Framework Migration

| Item | Legacy | New (In Progress) |
|------|--------|-------------------|
| Main Activity | `MainActivity` (Fragment) | `Main2Activity` (Compose) |
| Navigation | Navigation Component + XML | Type-safe Compose Navigation |
| UI Components | XML Layouts + ViewBinding | Jetpack Compose |
| State Management | LiveData | State/StateFlow (Compose) |
| Recommendation | Maintenance only | New feature development |

### Async Processing Migration

- **Legacy**: RxJava2
- **New**: Kotlin Coroutines (recommended)

---

## Build Configuration

### Compile Options
- **Source Compatibility**: Java 17
- **Target Compatibility**: Java 17
- **Kotlin JVM Target**: 17

### Build Features
- **Compose**: ✅ Enabled
- **ViewBinding**: ✅ Enabled
- **DataBinding**: ✅ Enabled

### ProGuard
- **Minify Enabled**: ❌ Disabled (Debug)
- **Minify Enabled**: ❌ Disabled (Release)

---

## Notes

### Naming Conventions
- **Packages**: snake_case (e.g., `steem_ui`, `steem_domain`)
- **Classes**: PascalCase (e.g., `MainActivity`, `PostViewModel`)
- **Functions**: camelCase (e.g., `readPosts()`, `hideKeyboard()`)
- **Use Cases**: `Read[Entity][Action]UseCase` pattern

### Git Status (Recent Changes)
- ✅ Created Main2Activity (Jetpack Compose Navigation)
- ✅ Applied Hilt dependency injection
- ✅ Migrated build.dependencies.gradle → libs.versions.toml
- ✅ Improved STEEM power rewards display (VEST → SP)
- ✅ Converted post item HTML → Plain text

---

## Additional Documentation

- [CLAUDE.md](./CLAUDE.md) - AI Development Guide
- [readme.md](./readme.md) - Project Introduction
- [PROJECT_STRUCTURE.md](./PROJECT_STRUCTURE.md) - Project Structure (Korean/English)

---

**Documentation by**: Claude Code
**Last Updated**: 2026-02-13
