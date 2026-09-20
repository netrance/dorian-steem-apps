# Dorian Steem Apps

An Android client for the [Steem](https://steem.com) blockchain, built with Jetpack Compose
and Clean Architecture.

`dorian-steem-apps-v1` is the project directory of the first version of the application.

> **Read-only**: the app reads from the blockchain. It does not sign transactions, and it
> never asks for a private key.

---

## Features

| Screen | What it does |
|--------|--------------|
| **Tags** | Browses posts by tag, sorted by Trending / Created / Payout |
| **Profile** | Shows an account's profile, and opens its blog, posts, comments, replies, details or history |
| **Wallet** | Balance, sent / received transfers, author & curation rewards, and vesting delegations |
| **Post** | Renders post content as Markdown, with images, replies and voter lists |
| **Account History** | The account's operation history, with links into profiles, posts and wallets |

The reward and transfer tabs read a date range that the user picks, and the delegation
lists cover incoming, outgoing and expiring delegations.

---

## Requirements

- Android Studio (AGP-compatible version) with **JDK 17**
- Android SDK **36** (compile / target); the app runs on **API 24+**
- No API key or account is required — every endpoint used is public

---

## Getting Started

```bash
git clone <repository-url>
cd dorian-steem-apps-v1

# Build
./gradlew build

# Install a debug build on a connected device or emulator
./gradlew installDebug
```

Open the project in Android Studio and run the `dorian-steem-ui` configuration to launch it
from the IDE.

---

## Module Structure

```
dorian-steem-ui       Compose UI, ViewModels, navigation, Hilt modules
dorian-steem-domain   Use cases, repository interfaces, domain models (pure Kotlin)
dorian-steem-data     Repository implementations, Retrofit services, DTOs (pure Kotlin)
dorian-steem-test     Shared test fixtures
dorian-android-ktx    Android-specific Kotlin extensions
dorian-ktx            Pure Kotlin extensions
```

Dependencies flow inward, following Clean Architecture:

```
dorian-steem-ui → dorian-steem-domain ← dorian-steem-data
                ↓                               ↓
        dorian-android-ktx              dorian-ktx
```

---

## Tech Stack

- **Kotlin** 2.1.0, Java 17
- **Jetpack Compose** (Material3) — the entire UI; there are no XML layouts
- **Type-safe Compose Navigation** with `@Serializable` route classes
- **Hilt** for dependency injection
- **Retrofit** + Gson for networking
- **Kotlin Coroutines** for async work
- **Coil** for image loading
- **CommonMark** (with GFM tables) for Markdown rendering

Dependencies are declared in the version catalog at `gradle/libs.versions.toml`.

---

## Backends

The app reads from two public APIs:

- **Steem API** (`https://api.steemit.com`) — posts, profiles, wallets, account history
- **SteemWorld SDS API** (`https://sds.steemworld.org/`) — delegations, transfers, rewards

The official Steem API is the default choice; SteemWorld fills in where it cannot serve the
data (for example, expiring delegations and reward history older than the official API keeps).

---

## Testing

```bash
# All tests
./gradlew test

# One module
./gradlew :dorian-steem-data:test

# One test class
./gradlew :dorian-steem-data:test --tests "*ReadRewardsUseCaseTest*"
```

Most tests in `dorian-steem-data` are **integration tests that call the live APIs** using the
accounts in `lee.dorian.steem_test.TestData`. They require a network connection, and they can
fail when the remote data changes. There is no mock server.

See [docs/TESTING.md](docs/TESTING.md) for the test layout, conventions and known failures.

---

## Documentation

| Document | Contents |
|----------|----------|
| [CLAUDE.md](CLAUDE.md) | Architecture, conventions and workflows (also the guide for AI coding agents) |
| [docs/TESTING.md](docs/TESTING.md) | Test layout, conventions and known failures |
| [docs/WHY_USE_CASES.md](docs/WHY_USE_CASES.md) | Why the project has a use case layer |
| [scripts/generate-steem-api-integration.md](scripts/generate-steem-api-integration.md) | Adding an official Steem API endpoint |
| [scripts/integrate-steemworld-api.md](scripts/integrate-steemworld-api.md) | Adding a SteemWorld (SDS) API endpoint |
