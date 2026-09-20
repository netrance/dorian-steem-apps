# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

Dorian Steem Apps is an Android application for the Steemit blockchain platform. The project
uses Clean Architecture with a multi-module structure, and its UI is **fully Jetpack Compose**
(the migration away from Fragments and XML layouts is complete).

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

# Run a single test class
./gradlew :dorian-steem-data:test --tests "*ReadRewardsUseCaseTest*"

# Run UI tests
./gradlew connectedAndroidTest
```

**Important**: most tests in `dorian-steem-data` are integration tests that call the live
Steem and SteemWorld APIs with the real accounts in `lee.dorian.steem_test.TestData`.
They fail without a network connection, and they can fail when the remote API or the test
account's data changes. There is no mock server.

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
  - Activities, Composables, ViewModels
  - 100% Jetpack Compose — every Activity is a `ComponentActivity` that calls `setContent`
  - Package: `lee.dorian.steem_ui`
  - Launcher: `SplashActivity` → `Main2Activity` (hosts the whole navigation graph)

- **dorian-steem-domain**: Domain layer (pure Kotlin)
  - Use cases, repository interfaces, and domain models
  - No Android dependencies
  - Use cases follow single responsibility (one action per use case)
  - Package: `lee.dorian.steem_domain`

- **dorian-steem-data**: Data layer (pure Kotlin)
  - Repository implementations and API services
  - Retrofit clients for two backends: `SteemClient` and `SteemWorldClient`
  - No Android dependencies
  - Package: `lee.dorian.steem_data`

- **dorian-android-ktx**: Android-specific Kotlin extensions
  - Package: `lee.dorian.dorian_android_ktx`

- **dorian-ktx**: General Kotlin extensions (no Android dependencies)
  - Package: `lee.dorian.dorian_ktx`

- **dorian-steem-test**: Shared test helpers and fixtures (`TestData`, `CommonPartOfViewModelTest`)
  - Package: `lee.dorian.steem_test`

### Dependency Flow

```
dorian-steem-ui → dorian-steem-domain ← dorian-steem-data
                ↓                               ↓
        dorian-android-ktx              dorian-ktx
```

### Package Structure

```
steem_ui/                    steem_domain/            steem_data/
├── di/                      ├── model/               ├── model/
├── ext/                     ├── repository/          │   ├── delegation/
├── main/                    ├── usecase/             │   ├── follow/
├── model/                   ├── ext/                 │   ├── history/
│   └── navigation/          └── util/                │   ├── post/
├── ui/                                               │   ├── reward/
│   ├── account_details/                              │   └── transfer/
│   ├── base/                                         ├── repository/
│   ├── compose/                                      └── retrofit/
│   ├── history/
│   ├── post/{content,list}/
│   ├── preview/
│   ├── profile/
│   ├── tags/
│   ├── voter/
│   └── wallet/
└── util/
```

UI packages are organised by feature, and a screen's Composable, its ViewModel and its
previews live together in the same package. `ui/compose/` holds the Composables shared
across features; `ui/preview/` holds the sample data the previews use.

### Navigation Architecture

There is a single navigation implementation: type-safe Compose Navigation.

- Route classes are `@Serializable` data classes in `lee.dorian.steem_ui.model.navigation`
  (e.g. `TagsScreenRoute`, `ProfileScreenRoute`, `PostContentRoute`, `WalletScreenRoute`)
- The graph is declared in `AppNavigation` in `main/Main2Navigation.kt`, using
  `composable<RouteType>` and `backStackEntry.toRoute()` to read arguments
- The `Screen` sealed class in the same file declares the three bottom-bar destinations
  (Tags, Profile, Wallet)
- Screens that are not part of the graph (image viewer, vote list) are separate
  `ComponentActivity`s started with an `Intent`

Navigation callbacks are passed **into** a screen as lambdas; screens do not hold the
`NavHostController` themselves.

## Technology Stack

- **Language**: Kotlin 2.1.0 (Java 17 toolchain)
- **SDK**: minSdk 24, compileSdk / targetSdk 36
- **Build System**: Gradle with version catalog (`gradle/libs.versions.toml`)
- **Dependency Injection**: Hilt/Dagger
  - Application class: `MainApplication` (`@HiltAndroidApp`)
  - Modules in `dorian-steem-ui/src/main/java/lee/dorian/steem_ui/di/`
  - ViewModels use `@HiltViewModel`, Activities use `@AndroidEntryPoint`
- **UI**: Jetpack Compose (Material3). The only enabled build feature is `compose`;
  ViewBinding and DataBinding are **not** enabled and there are no XML layouts.
- **Networking**: Retrofit with Gson converter
- **Async**: Kotlin Coroutines. RxJava2 is effectively gone — only `RxJava2CallAdapterFactory`
  in `SteemClient` and two unused `io.reactivex.Single` imports remain. Do not add new RxJava code.
- **Image Loading**: Coil (`coil-compose`). Glide is no longer used.
- **Markdown Rendering**: CommonMark with the GFM tables extension. Post bodies are converted
  to an HTML document by `String.convertMarkdownToHtmlDocument()` in `dorian-ktx`, then shown
  with `WebView.loadMarkdown()` from `dorian-android-ktx`, wrapped in an `AndroidView`. This is
  the one place a platform View appears inside the Compose UI.

## Backends

The app reads from two different backends. **Prefer the official Steem API**; use SteemWorld
only when the official API cannot do the job or would be unreasonably complex.

| | Steem (official) | SteemWorld (SDS) |
|---|---|---|
| Client | `SteemClient` | `SteemWorldClient` |
| Base URL | `https://api.steemit.com` | `https://sds.steemworld.org/` |
| Service | `SteemService` | `SteemWorldService` |
| Protocol | JSON-RPC over POST | REST with path parameters (GET) |
| Repository | `SteemRepository` / `SteemRepositoryImpl` | `SteemWorldRepository` / `SteemWorldRepositoryImpl` |
| Used for | posts, profiles, wallets, account history, global properties | delegations, transfers, rewards |

SteemWorld responses have their own shape (`{code, error, result{cols, rows}}`) and report
errors with HTTP 200 plus a non-zero `code`. The full procedure for adding a SteemWorld
endpoint is in [scripts/integrate-steemworld-api.md](scripts/integrate-steemworld-api.md);
the official-API generator templates are in
[scripts/generate-steem-api-integration.md](scripts/generate-steem-api-integration.md).

## Key Patterns and Conventions

### Result and State types

Two distinct types carry results, and they are not interchangeable:

- `ApiResult<T>` (domain): `Success` / `Failure` (API-level error message) / `Error` (exception).
  Returned by repositories and use cases.
- `State<T>` (ui, `lee.dorian.steem_ui.model.State`): `Empty` / `Loading` / `Success` /
  `Failure` / `Error`. Exposed by ViewModels to Composables.

ViewModels convert one into the other, and the `when` must handle all three `ApiResult` branches.

### ViewModels
- Annotate with `@HiltViewModel`, extend `BaseViewModel`, inject use cases via constructor
- Expose state as `MutableStateFlow<State<T>>` behind an `asStateFlow()` property
- Emit `State.Loading` before the call, then the converted result
- Located in the same package as the corresponding screen

### Composable screens
- Obtain the ViewModel with `hiltViewModel()`, collect with `collectAsStateWithLifecycle()`
- Trigger loading from `LaunchedEffect(key)` where the key is what should re-trigger it
- Render `State.Empty`/`State.Loading` as `Loading()`, failures as `ErrorOrFailure()`
- Accept navigation and click handling as lambda parameters
- Add a `@Preview` composable next to each screen-level composable

### Use Cases
- One action per use case; opposite directions get separate use cases
  (e.g. `ReadIncomingTransfersUseCase` / `ReadOutgoingTransfersUseCase`)
- Named `Read[Entity][Action]UseCase`
- Declared as `suspend operator fun invoke(...)`, wrapped in `withContext(dispatcher)` and `try/catch`
- Return domain models inside `ApiResult`, never DTOs
- Constructor injection; no Hilt module entry needed

### Repository Pattern
- Interfaces in `lee.dorian.steem_domain.repository`, implementations in `lee.dorian.steem_data.repository`
- Both `SteemRepository` and `SteemWorldRepository` are bound with `@Binds` in `di/RepositoryModule.kt`
- `CoroutineDispatcher` comes from `di/CoroutinesModule.kt` (`Dispatchers.IO`)
- Repositories map DTOs to domain models; Retrofit types never leave the data module

### Naming
- Packages: snake_case (`steem_ui`, `steem_domain`)
- Classes: PascalCase — note that `PostContentFragment.kt` and `ReplyListDialogFragment.kt`
  are legacy *file names* containing only Composables, not Fragments
- DTOs end in `DTO`; screen files end in `Screen.kt`

## Dependency Management

Dependencies are managed in `gradle/libs.versions.toml` (versions / libraries / bundles / plugins).
Add the entry there first, then reference it from the module's `build.gradle`.

## Testing

See [docs/TESTING.md](docs/TESTING.md) for the full guide, including the known failures.

- **Domain use case tests live in the data module**, at
  `dorian-steem-data/src/test/java/lee/dorian/steem_domain/usecase/`. The package does not
  match the module on purpose: these tests construct a real `RepositoryImpl`, which is only
  visible from the data module.
- Pure unit tests (`ConverterTest`, `StringExtTest`) live in their own module's `src/test`.
- ViewModel tests are in `dorian-steem-ui/src/test`, using `androidx-core-testing`.
- Test accounts come from `lee.dorian.steem_test.TestData`; shared setup from `CommonPartOfViewModelTest`.
- For every API-backed feature, cover both a valid account and an invalid account.

The UI module mocks the Android framework in unit tests:
```groovy
testOptions {
    unitTests.returnDefaultValues = true
}
```

## Common Development Workflows

### Adding a New Screen

1. Create an `@Serializable` route data class in `lee.dorian.steem_ui.model.navigation`
2. Create the Composable in `lee.dorian.steem_ui.ui.[feature]` as `[Name]Screen.kt`, plus a `@Preview`
3. Create the ViewModel with `@HiltViewModel`, exposing `StateFlow<State<T>>`
4. Register the destination with `composable<Route>` in `AppNavigation` (`main/Main2Navigation.kt`)
5. If it belongs on the bottom bar, add it to the `Screen` sealed class

### Adding a New Use Case

1. Create the use case in the domain module with constructor-injected repository and dispatcher
2. Inject it into the ViewModel
3. Add the repository method if it does not exist yet

### Adding an API Endpoint

- Official Steem API: follow [scripts/generate-steem-api-integration.md](scripts/generate-steem-api-integration.md)
- SteemWorld (SDS) API: follow [scripts/integrate-steemworld-api.md](scripts/integrate-steemworld-api.md)

Both end with the same shape: Service method → Repository interface method →
Repository implementation → Use case → ViewModel → Screen, plus tests.

## Documentation

### Language

`readme.md` is written in English, since its readers are visitors to the repository, and it
may carry a `readme.ko.md` translation. Every other document is written in **one language
only**: Korean for internal working documents, English where an external reader is the
audience. Do not maintain two language versions of the same internal document.

This is not a style preference. `PROJECT_STRUCTURE.md` and `PROJECT_STRUCTURE_EN.md` were
kept as such a pair: both were created in a single commit, neither was ever updated again,
and they went stale together while the code moved on. Doubling the cost of an update is
what stops the update from happening.

### Documents

- [readme.md](readme.md) — project introduction and setup
- [docs/TESTING.md](docs/TESTING.md) — test layout, conventions, known failures (Korean)
- [docs/WHY_USE_CASES.md](docs/WHY_USE_CASES.md) — rationale for the use case layer
- [scripts/generate-steem-api-integration.md](scripts/generate-steem-api-integration.md) — adding an official Steem API endpoint
- [scripts/integrate-steemworld-api.md](scripts/integrate-steemworld-api.md) — adding a SteemWorld (SDS) endpoint (Korean)

This file is the single source of truth for the project's structure and conventions. Keep it
current with the code rather than describing the structure a second time somewhere else.
