# Dorian Steem Apps - 프로젝트 구조 분석
# Project Structure Analysis

> 분석 날짜 / Analysis Date: 2026-02-13

## 📋 목차 / Table of Contents

1. [프로젝트 개요](#프로젝트-개요)
2. [모듈 구조](#모듈-구조)
3. [패키지 구조](#패키지-구조)
4. [기술 스택](#기술-스택)
5. [의존성 그래프](#의존성-그래프)

---

## 프로젝트 개요

**프로젝트명**: Dorian Steem Apps v1
**타입**: Android 애플리케이션
**아키텍처**: Clean Architecture (Multi-Module)
**언어**: Kotlin 2.1.0
**최소 SDK**: 24 (Android 7.0)
**타겟 SDK**: 36

### 프로젝트 목적
Steemit 블록체인 플랫폼을 위한 Android 클라이언트 애플리케이션

---

## 모듈 구조

프로젝트는 6개의 모듈로 구성되어 있으며, Clean Architecture 원칙을 따릅니다.

### 1. dorian-steem-ui (Main Application Module)
**타입**: Android Application
**패키지**: `lee.dorian.steem_ui`
**Kotlin 파일 수**: 52개
**레이아웃 파일 수**: 20개

#### 주요 구성요소
```
dorian-steem-ui/
└── src/main/
    ├── java/lee/dorian/steem_ui/
    │   ├── MainActivity.kt              # Fragment 기반 메인 액티비티 (레거시)
    │   ├── Main2Activity.kt             # Compose 기반 메인 액티비티 (신규)
    │   ├── MainApplciation.kt           # Hilt Application 클래스
    │   ├── SplashActivity.kt            # 스플래시 스크린
    │   ├── BaseActivity.kt              # 기본 액티비티 클래스
    │   ├── MainViewModel.kt             # 메인 ViewModel
    │   ├── Colors.kt                    # 색상 정의
    │   │
    │   ├── di/                          # Dependency Injection
    │   │   ├── RepositoryModule.kt      # Repository 바인딩
    │   │   └── CoroutinesModule.kt      # Coroutine 설정
    │   │
    │   ├── ext/                         # 확장 함수
    │   │
    │   ├── model/
    │   │   └── navigation/              # Navigation 라우트
    │   │       ├── PostContentRoute.kt
    │   │       ├── ProfileScreenRoute.kt
    │   │       ├── TagsScreenRoute.kt
    │   │       └── WalletScreenRoute.kt
    │   │
    │   ├── ui/                          # UI 컴포넌트
    │   │   ├── account_details/         # 계정 상세 화면
    │   │   ├── base/                    # 기본 UI 컴포넌트
    │   │   ├── compose/                 # Compose 재사용 컴포넌트
    │   │   │   ├── ComposeUtil.kt
    │   │   │   ├── CustomTextField.kt
    │   │   │   └── InputForm.kt
    │   │   ├── history/                 # 히스토리 화면
    │   │   ├── post/                    # 포스트 관련 화면
    │   │   │   ├── PostComposable.kt
    │   │   │   ├── PostImagePagerActivity.kt
    │   │   │   ├── content/             # 포스트 내용 화면
    │   │   │   │   ├── PostContentFragment.kt
    │   │   │   │   ├── PostContentViewModel.kt
    │   │   │   │   ├── PostContentState.kt
    │   │   │   │   ├── PostContentWebChromeClient.kt
    │   │   │   │   └── ReplyListDialogFragment.kt
    │   │   │   └── list/                # 포스트 목록 화면
    │   │   │       ├── PostListFragment.kt
    │   │   │       └── PostListViewModel.kt
    │   │   ├── preview/                 # 미리보기 화면
    │   │   ├── profile/                 # 프로필 화면
    │   │   ├── tags/                    # 태그 화면
    │   │   │   ├── TagsFragment.kt
    │   │   │   ├── TagsState.kt
    │   │   │   └── TagScreenSortTabInfo.kt
    │   │   ├── voter/                   # 투표자 목록 화면
    │   │   │   ├── VoteListActivity.kt
    │   │   │   ├── VoteListViewModel.kt
    │   │   │   └── VoteListAdapter.kt
    │   │   └── wallet/                  # 지갑 화면
    │   │
    │   └── util/                        # 유틸리티 클래스
    │
    ├── res/                             # Android 리소스
    │   ├── drawable/                    # 드로어블 리소스
    │   ├── layout/                      # XML 레이아웃 (20개)
    │   ├── menu/                        # 메뉴 리소스
    │   ├── mipmap-*/                    # 앱 아이콘
    │   ├── navigation/                  # Navigation 그래프
    │   ├── values/                      # 문자열, 색상, 스타일
    │   └── xml/                         # XML 설정
    │
    └── AndroidManifest.xml              # 매니페스트 파일
```

#### 주요 특징
- **이중 네비게이션 시스템**:
  - `MainActivity`: Fragment + Navigation Component (레거시)
  - `Main2Activity`: Jetpack Compose Navigation (신규, 권장)
- **ViewBinding & DataBinding** 사용
- **Hilt Dependency Injection** 적용
- **MVVM 아키텍처 패턴**

---

### 2. dorian-steem-domain (Domain Layer)
**타입**: Java/Kotlin Library (Pure Kotlin)
**패키지**: `lee.dorian.steem_domain`
**Kotlin 파일 수**: 23개

#### 주요 구성요소
```
dorian-steem-domain/
└── src/main/java/lee/dorian/steem_domain/
    ├── model/                           # 도메인 모델 (13개)
    │   ├── AccountDetails.kt            # 계정 상세 정보
    │   ├── AccountHistory.kt            # 계정 히스토리
    │   ├── AccountHistoryItem.kt        # 히스토리 아이템
    │   ├── AccountHistoryItemLink.kt    # 히스토리 아이템 링크
    │   ├── ActiveVote.kt                # 투표 정보
    │   ├── ApiResult.kt                 # API 결과 래퍼
    │   ├── DynamicGlobalProperties.kt   # 글로벌 속성
    │   ├── FollowCount.kt               # 팔로우 수
    │   ├── Post.kt                      # 포스트
    │   ├── PostItem.kt                  # 포스트 아이템
    │   ├── PostListInfo.kt              # 포스트 목록 정보
    │   ├── SteemitProfile.kt            # Steemit 프로필
    │   └── SteemitWallet.kt             # Steemit 지갑
    │
    ├── repository/                      # Repository 인터페이스
    │   └── SteemRepository.kt           # Steem API Repository 인터페이스
    │
    ├── usecase/                         # Use Cases (8개)
    │   ├── ReadAccountDetailsUseCase.kt           # 계정 상세 조회
    │   ├── ReadAccountHistoryUseCase.kt           # 계정 히스토리 조회
    │   ├── ReadDynamicGlobalPropertiesUseCase.kt  # 글로벌 속성 조회
    │   ├── ReadPostAndRepliesUseCase.kt           # 포스트와 댓글 조회
    │   ├── ReadPostsUseCase.kt                    # 포스트 목록 조회
    │   ├── ReadRankedPostsUseCase.kt              # 순위별 포스트 조회
    │   ├── ReadSteemitProfileUseCase.kt           # 프로필 조회
    │   └── ReadSteemitWalletUseCase.kt            # 지갑 조회
    │
    ├── ext/                             # 확장 함수
    └── util/                            # 유틸리티
```

#### 주요 특징
- **Pure Kotlin 모듈** (Android 의존성 없음)
- **Single Responsibility Principle**: 각 UseCase는 단일 책임
- **명명 규칙**: `Read[Entity][Action]UseCase`
- **JSR-330** `@Inject` 어노테이션 사용

---

### 3. dorian-steem-data (Data Layer)
**타입**: Java/Kotlin Library (Pure Kotlin)
**패키지**: `lee.dorian.steem_data`
**Kotlin 파일 수**: 25개

#### 주요 구성요소
```
dorian-steem-data/
└── src/main/java/lee/dorian/steem_data/
    ├── model/                           # 데이터 모델 (DTO)
    │   ├── follow/                      # 팔로우 관련 DTO
    │   ├── history/                     # 히스토리 관련 DTO
    │   └── post/                        # 포스트 관련 DTO
    │
    ├── repository/                      # Repository 구현
    │   └── SteemRepositoryImpl.kt       # SteemRepository 구현체
    │
    └── retrofit/                        # Retrofit 설정
        ├── SteemClient.kt               # Retrofit 클라이언트
        └── SteemService.kt              # Steem API 서비스 인터페이스
```

#### 주요 특징
- **Retrofit2** 기반 네트워크 통신
- **Gson Converter** 사용
- **RxJava2 & Coroutines** 지원
- **DTO → Domain Model 변환** 책임

---

### 4. dorian-android-ktx
**타입**: Android Library
**패키지**: `lee.dorian.dorian_android_ktx`

#### 주요 구성요소
```
dorian-android-ktx/
└── src/main/java/lee/dorian/dorian_android_ktx/
    └── android/
        └── context/                     # Context 확장 함수
            └── (Android 관련 확장 함수들)
```

#### 주요 특징
- Android Framework 클래스에 대한 확장 함수
- 예: `hideKeyboard()`, Context 관련 유틸리티

---

### 5. dorian-ktx
**타입**: Java/Kotlin Library (Pure Kotlin)
**패키지**: `lee.dorian.dorian_ktx`

#### 주요 구성요소
```
dorian-ktx/
└── src/main/java/lee/dorian/dorian_ktx/
    └── (순수 Kotlin 확장 함수 및 유틸리티)
```

#### 주요 특징
- Pure Kotlin 유틸리티
- Android 의존성 없음
- 범용 확장 함수

---

### 6. dorian-steem-test
**타입**: Java/Kotlin Library
**패키지**: `lee.dorian.steem_test`

#### 주요 구성요소
```
dorian-steem-test/
└── src/main/java/lee/dorian/steem_test/
    └── (테스트 유틸리티 및 픽스처)
```

#### 주요 특징
- 공유 테스트 헬퍼
- 테스트 픽스처
- Mock 데이터

---

## 패키지 구조

### UI 모듈 패키지 구조 (lee.dorian.steem_ui)

```
steem_ui/
├── (root)                               # Activity, Application, ViewModel
├── di/                                  # Dependency Injection
├── ext/                                 # 확장 함수
├── model/navigation/                    # Navigation 라우트
├── ui/                                  # UI 컴포넌트
│   ├── account_details/                 # 기능별 패키지
│   ├── base/
│   ├── compose/                         # 재사용 Composable
│   ├── history/
│   ├── post/
│   │   ├── content/                     # 세부 기능
│   │   └── list/
│   ├── preview/
│   ├── profile/
│   ├── tags/
│   ├── voter/
│   └── wallet/
└── util/                                # 유틸리티
```

### Domain 모듈 패키지 구조 (lee.dorian.steem_domain)

```
steem_domain/
├── model/                               # 도메인 엔티티
├── repository/                          # Repository 인터페이스
├── usecase/                             # Business Logic
├── ext/                                 # 확장 함수
└── util/                                # 유틸리티
```

### Data 모듈 패키지 구조 (lee.dorian.steem_data)

```
steem_data/
├── model/                               # DTO
│   ├── follow/
│   ├── history/
│   └── post/
├── repository/                          # Repository 구현
└── retrofit/                            # API 클라이언트
```

---

## 기술 스택

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
- **Kotlin Coroutines**: 1.7.3 (권장)
- **RxJava2**: 2.2.20 (레거시)
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

## 의존성 그래프

### 모듈 간 의존성

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

### Layer 분리 원칙

```
┌──────────────────────────────────────────────────────────┐
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

## 파일 통계

| 모듈 | Kotlin 파일 | 레이아웃 파일 | 타입 |
|------|------------|-------------|------|
| dorian-steem-ui | 52 | 20 | Android App |
| dorian-steem-domain | 23 | 0 | Pure Kotlin |
| dorian-steem-data | 25 | 0 | Pure Kotlin |
| dorian-android-ktx | - | 0 | Android Library |
| dorian-ktx | - | 0 | Pure Kotlin |
| dorian-steem-test | - | 0 | Test Library |
| **총합** | **~147** | **20** | - |

---

## 주요 설정 파일

### Root Level
- `build.gradle` - 프로젝트 레벨 빌드 설정
- `settings.gradle` - 모듈 포함 설정
- `gradle.properties` - Gradle 속성
- `gradle/libs.versions.toml` - **Version Catalog** (의존성 중앙 관리)

### Module Level
- `dorian-steem-ui/build.gradle` - UI 모듈 빌드 설정
- `dorian-steem-domain/build.gradle` - Domain 모듈 빌드 설정
- `dorian-steem-data/build.gradle` - Data 모듈 빌드 설정

### Android Resources
- `dorian-steem-ui/src/main/AndroidManifest.xml` - 앱 매니페스트
- `dorian-steem-ui/src/main/res/navigation/` - Navigation 그래프
- `dorian-steem-ui/src/main/res/values/` - 문자열, 색상, 스타일

---

## 아키텍처 패턴

### Clean Architecture Layers

1. **Presentation Layer** (UI Module)
   - MVVM 패턴
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

- **Repository Pattern**: 데이터 소스 추상화
- **Use Case Pattern**: 비즈니스 로직 캡슐화
- **MVVM Pattern**: UI와 비즈니스 로직 분리
- **Dependency Injection**: Hilt/Dagger를 통한 의존성 주입
- **Observer Pattern**: LiveData/StateFlow를 통한 반응형 UI

---

## 마이그레이션 상태

### UI 프레임워크 마이그레이션

| 항목 | 레거시 | 신규 (진행 중) |
|------|--------|---------------|
| Main Activity | `MainActivity` (Fragment) | `Main2Activity` (Compose) |
| Navigation | Navigation Component + XML | Type-safe Compose Navigation |
| UI Components | XML Layouts + ViewBinding | Jetpack Compose |
| 상태 관리 | LiveData | State/StateFlow (Compose) |
| 권장 사항 | 유지보수만 | 신규 기능 개발 |

### 비동기 처리 마이그레이션

- **레거시**: RxJava2
- **신규**: Kotlin Coroutines (권장)

---

## 빌드 설정

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

## 참고 사항

### 명명 규칙
- **Packages**: snake_case (예: `steem_ui`, `steem_domain`)
- **Classes**: PascalCase (예: `MainActivity`, `PostViewModel`)
- **Functions**: camelCase (예: `readPosts()`, `hideKeyboard()`)
- **Use Cases**: `Read[Entity][Action]UseCase` 패턴

### Git Status (최근 변경사항)
- ✅ Main2Activity 생성 (Jetpack Compose Navigation)
- ✅ Hilt 의존성 주입 적용
- ✅ build.dependencies.gradle → libs.versions.toml 마이그레이션
- ✅ STEEM power 보상 표시 개선 (VEST → SP)
- ✅ 포스트 아이템 HTML → Plain text 변환

---

## 추가 문서

- [CLAUDE.md](./CLAUDE.md) - AI 개발 가이드
- [readme.md](./readme.md) - 프로젝트 소개

---

**문서 작성**: Claude Code
**마지막 업데이트**: 2026-02-13
