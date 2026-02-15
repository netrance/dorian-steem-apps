# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

Dorian Steem Apps is an Android application for the Steemit blockchain platform. The project uses Clean Architecture with multi-module structure and is currently migrating from traditional Fragment-based UI to Jetpack Compose.

## Build and Run Commands

### Build
```bash
./gradlew build
```

### Run Tests
```bash
# Run all tests
./gradlew test

# Run tests for a specific module
./gradlew :dorian-steem-ui:test
./gradlew :dorian-steem-domain:test
./gradlew :dorian-steem-data:test

# Run UI tests
./gradlew connectedAndroidTest
```

### Clean Build
```bash
./gradlew clean build
```

### Install Debug APK
```bash
./gradlew installDebug
```

### Check for Lint Issues
```bash
./gradlew lint
```

## Architecture

This project follows Clean Architecture principles with clear separation of concerns across modules:

### Module Structure

- **dorian-steem-ui**: Main application module (UI layer)
  - Contains Activities, Fragments, Composables, ViewModels
  - Uses both traditional Views (XML + ViewBinding/DataBinding) and Jetpack Compose
  - Package: `lee.dorian.steem_ui`
  - Entry points: `MainActivity` (traditional), `Main2Activity` (Compose-based, newer)

- **dorian-steem-domain**: Domain layer (pure Kotlin)
  - Contains use cases, repository interfaces, and domain models
  - No Android dependencies
  - Use cases follow single responsibility (one action per use case)
  - Package: `lee.dorian.steem_domain`

- **dorian-steem-data**: Data layer (pure Kotlin)
  - Contains repository implementations and API services
  - Uses Retrofit for network calls to Steemit blockchain
  - No Android dependencies
  - Package: `lee.dorian.steem_data`

- **dorian-android-ktx**: Android-specific Kotlin extensions
  - Helper functions for Android framework classes
  - Package: `lee.dorian.dorian_android_ktx`

- **dorian-ktx**: General Kotlin extensions
  - Pure Kotlin utilities (no Android dependencies)
  - Package: `lee.dorian.dorian_ktx`

- **dorian-steem-test**: Test utilities
  - Shared test helpers and fixtures
  - Package: `lee.dorian.steem_test`

### Dependency Flow

The dependency flow follows Clean Architecture:
```
dorian-steem-ui → dorian-steem-domain ← dorian-steem-data
                ↓                               ↓
        dorian-android-ktx              dorian-ktx
```

### Navigation Architecture

The app supports two navigation implementations:

1. **MainActivity** (Traditional): Fragment-based navigation using Navigation Component with XML navigation graphs and SafeArgs
2. **Main2Activity** (New): Jetpack Compose navigation with type-safe navigation routes using serializable data classes (e.g., `TagsScreenRoute`, `ProfileScreenRoute`, `PostContentRoute`)

When working with navigation:
- For Compose screens: Use sealed route classes in `lee.dorian.steem_ui.model.navigation`
- For Fragment screens: Use SafeArgs-generated directions classes
- New features should prefer Compose-based navigation in Main2Activity

## Technology Stack

- **Language**: Kotlin 2.1.0
- **Build System**: Gradle with version catalog (libs.versions.toml)
- **Dependency Injection**: Hilt/Dagger
  - Application class: `MainApplication` (annotated with `@HiltAndroidApp`)
  - Modules located in `dorian-steem-ui/src/main/java/lee/dorian/steem_ui/di/`
  - ViewModels use `@HiltViewModel`
  - Activities/Fragments use `@AndroidEntryPoint`
- **UI**:
  - Jetpack Compose (Material3) - preferred for new screens
  - Traditional Views with ViewBinding/DataBinding - legacy screens
- **Networking**: Retrofit with Gson converter
- **Async**: Kotlin Coroutines (preferred) and RxJava2 (legacy)
- **Image Loading**: Coil (Compose) and Glide (traditional Views)
- **Markdown Rendering**: CommonMark with GFM tables extension

## Key Patterns and Conventions

### ViewModels
- Use `@HiltViewModel` annotation
- Inject use cases via constructor
- Expose UI state using LiveData or Compose State
- Located in same package as corresponding UI component

### Use Cases
- Single responsibility per use case
- Named with pattern: `Read[Entity][Action]UseCase` (e.g., `ReadPostsUseCase`)
- Return domain models, not data models
- Use constructor injection for dependencies

### Repository Pattern
- Interfaces defined in domain module (`lee.dorian.steem_domain.repository`)
- Implementations in data module (`lee.dorian.steem_data.repository`)
- Currently: `SteemRepository` interface and `SteemRepositoryImpl`
- Bound in `RepositoryModule` using Hilt

### API Integration
- Steemit blockchain API accessed via Retrofit
- Service interface: `SteemService` in data module
- API endpoints use Steem RPC protocol
- Responses converted to domain models in repository layer

## Dependency Management

Dependencies are managed in `gradle/libs.versions.toml`:
- **Versions section**: Centralized version numbers
- **Libraries section**: Dependency declarations
- **Bundles section**: Grouped dependencies (e.g., `androidx-compose`, `retrofit`)
- **Plugins section**: Gradle plugins

When adding dependencies, update `libs.versions.toml` first, then reference in module build files.

## Testing

- Unit tests for domain layer: Pure Kotlin tests without Android framework
- Unit tests for ViewModels: Use `androidx-core-testing` for LiveData testing
- Integration tests for data layer: Test repository implementations
- UI tests: Espresso for traditional views, Compose testing for Compose screens

Test configuration in UI module includes:
```groovy
testOptions {
    unitTests.returnDefaultValues = true  // Mocks Android framework
}
```

## Common Development Workflows

### Adding a New Screen

1. **For Compose screens** (preferred):
   - Create route data class in `lee.dorian.steem_ui.model.navigation`
   - Create Composable in `lee.dorian.steem_ui.ui.[feature]`
   - Create ViewModel with `@HiltViewModel`
   - Add route to `AppNavigation` in `Main2Activity`

2. **For Fragment screens** (legacy):
   - Create Fragment extending `BaseActivity` with ViewBinding
   - Create ViewModel with `@HiltViewModel`
   - Add to navigation graph XML
   - Update `MainActivity` navigation setup if needed

### Adding a New Use Case

1. Create use case interface in domain module
2. Implement use case with constructor-injected repository
3. Inject use case into ViewModel
4. Repository methods should already exist or be added as needed

### Adding API Endpoint

1. Add method to `SteemService` interface in data module
2. Add corresponding method to `SteemRepository` interface in domain module
3. Implement in `SteemRepositoryImpl` in data module
4. Create or update use case to expose functionality

## Migration Notes

The project is actively migrating from traditional Android Views to Jetpack Compose:
- `MainActivity`: Legacy Fragment-based UI
- `Main2Activity`: New Compose-based UI
- Both activities coexist during migration
- New features should use `Main2Activity` and Jetpack Compose
- When refactoring existing screens, migrate to Compose incrementally
