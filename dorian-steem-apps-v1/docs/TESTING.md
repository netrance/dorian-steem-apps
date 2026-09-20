# 테스트 가이드

이 프로젝트의 테스트가 어떻게 구성되어 있는지, 왜 그렇게 배치되어 있는지, 그리고
실행했을 때 무엇을 예상해야 하는지를 정리한다.

---

## ⚠️ 먼저 읽을 것: 대부분 통합 테스트다

이 저장소의 테스트는 대부분 **단위 테스트가 아니다.** 실제 계정으로 Steem API와
SteemWorld API를 네트워크로 호출한다.

따라서:

- **네트워크가 없으면 실패한다.**
- **코드가 아니라 원격 데이터가 바뀌어서 실패할 수 있다.**
- **느리다.** ViewModel 테스트는 케이스마다 5초를 고정으로 기다린다 (`WAITING_TIME_MSEC`).
- **mock 서버도, 녹화된 fixture도, `MockWebServer` 의존성도 없다.**

그러므로 테스트가 빨개졌다고 해서 자동으로 회귀인 것은 아니다. 코드를 고치기 전에
엔드포인트가 아직 테스트가 기대하는 대로 응답하는지부터 `curl`로 직접 확인한다.

---

## 실행 명령

```bash
# 전체
./gradlew test

# 모듈 단위
./gradlew :dorian-steem-data:test
./gradlew :dorian-steem-ui:test
./gradlew :dorian-steem-domain:test

# 클래스 단위
./gradlew :dorian-steem-data:test --tests "*ReadRewardsUseCaseTest*"

# 케이스 단위
./gradlew :dorian-steem-data:test --tests "*ReadRewardsUseCaseTest.readAuthorRewards_case1"

# 계측 테스트 (기기 또는 에뮬레이터 필요)
./gradlew connectedAndroidTest
```

오프라인이라면 네트워크를 쓰지 않는 테스트만 실행한다.

```bash
./gradlew :dorian-steem-domain:test :dorian-ktx:test
```

---

## 테스트 배치

| 위치 | 내용 | 네트워크 |
|------|------|:--------:|
| `dorian-steem-domain/src/test/` | `ConverterTest`, `StringExtTest` — 순수 로직 | ✗ |
| `dorian-ktx/src/test/` | `StringExtTest` — 순수 로직 | ✗ |
| `dorian-steem-data/src/test/.../steem_data/retrofit/` | `SteemServiceTest`, `SteemWorldServiceTest` — 원본 응답 형태 | ✓ |
| `dorian-steem-data/src/test/.../steem_data/repository/` | `SteemRepositoryImplTest` | ✓ |
| `dorian-steem-data/src/test/.../steem_domain/usecase/` | UseCase 테스트 (9개 클래스) | ✓ |
| `dorian-steem-ui/src/test/` | ViewModel 테스트 (7개 클래스) | ✓ |
| `dorian-steem-ui/src/androidTest/` | `ExampleInstrumentedTest` (템플릿만) | — |

### 도메인 UseCase 테스트가 data 모듈에 있는 이유

`ReadRewardsUseCaseTest`를 비롯한 UseCase 테스트는 다음 위치에 있다.

```
dorian-steem-data/src/test/java/lee/dorian/steem_domain/usecase/
```

패키지(`lee.dorian.steem_domain`)가 모듈(`dorian-steem-data`)과 어긋나 있는데, 이는
의도된 것이다. 이 테스트들은 **실제 Repository 구현체**로 UseCase를 만든다.

```kotlin
private val readAuthorRewardsUseCase = ReadAuthorRewardsUseCase(
    SteemWorldRepositoryImpl(dispatcher),
    dispatcher
)
```

`SteemWorldRepositoryImpl`은 data 모듈에 있으므로, 이를 생성하는 테스트도 data 모듈에
있어야 한다. domain 모듈에서는 보이지 않는다 — 의존성 방향이 그렇게 설계되었기 때문이다.
패키지 이름을 domain으로 유지한 것은 **어느 레이어를 테스트하는지** 드러내기 위해서다.

`dorian-steem-ui/build.gradle`에 `testImplementation project(':dorian-steem-data')`가 있는
이유도 같다. ViewModel 테스트 역시 실제 Repository를 만든다.

---

## 공용 테스트 헬퍼 (`dorian-steem-test`)

### `TestData`

모든 테스트 계정은 여기에만 선언한다.

```kotlin
val singleAccount = "dorian-mobileapp"
val singleAccount2 = "dorian-lee"
val singleAccount3 = "dorian-dev"
val invalidSingleAccount = "invalid10293845"
val invalidSingleAccount2 = "invalid76787654"
```

테스트는 이 **실계정의 현재 상태에 의존한다.** 목록이 비어 있지 않음을 단언하는 테스트는
해당 계정이 그 데이터를 더 이상 갖지 않게 되면 실패한다. 특정 값보다는 **형태와 불변식**을
단언하는 편이 낫다.

### `CommonPartOfViewModelTest`

ViewModel이 필요한 테스트의 기반 클래스다.

```kotlin
protected val dispatcher: CoroutineDispatcher = UnconfinedTestDispatcher()

@Before fun setUp() { Dispatchers.setMain(dispatcher) }
@After fun teardown() { Dispatchers.resetMain() }

companion object { const val WAITING_TIME_MSEC = 5000L }
```

여기의 `dispatcher`를 테스트가 생성하는 Repository와 UseCase에 그대로 넘겨, 전부 하나의
테스트 디스패처 위에서 돌게 한다.

---

## 테스트의 세 층위

### 1. Service 테스트 — API가 아직 우리가 아는 그 형태인가

`SteemServiceTest`, `SteemWorldServiceTest`는 **원본 응답**을 단언한다. row의 길이와 각 열의
타입을 확인하므로, 원격 스키마가 바뀌면 여기서 가장 먼저 걸린다.

```kotlin
for (row in rows) {
    assertEquals(4, row.size)
    assertTrue(row[0] is Double)   // time (Unix timestamp)
    assertTrue(row[1] is String)   // from (delegator)
    // ...
}
```

SDS의 숫자는 항상 `Double`로 들어온다. `Int`나 `Long`이 아니다 — Gson은
`List<List<Any>>`에 대한 타입 정보를 갖지 못하기 때문이다.

### 2. UseCase 테스트 — 데이터가 도메인까지 제대로 오는가

도메인 모델과, **화면이 의존하는 계약**을 단언한다. 정렬 순서, 시간 범위, 페이징, 그리고
어떤 `ApiResult` 분기가 선택되는지가 대상이다.

### 3. ViewModel 테스트 — 상태가 화면까지 가는가

ViewModel 함수를 호출하고, 기다린 뒤, `StateFlow`의 값을 확인한다.

```kotlin
walletViewModel.readSteemitWallet("dorian-mobileapp")
delay(WAITING_TIME_MSEC)
val walletState = walletViewModel.flowWalletState.value
assertTrue(walletState is State.Success<SteemitWallet>)
```

`runBlocking` + 고정 `delay`가 이 프로젝트의 관례다 (`runTest` + `Thread.sleep` 조합을
대체했다). UI 모듈 테스트가 수 분씩 걸리는 이유이기도 하다.

---

## 새 테스트를 쓸 때의 규약

- 프로덕션 클래스 하나당 테스트 클래스 하나, 이름은 `[테스트대상]Test`
- 케이스에 번호를 붙이고, 무엇을 다루는지 주석으로 적는다.
  ```kotlin
  // Test case 1: Valid account — every author reward belongs to a post of the account.
  @Test fun readAuthorRewards_case1() = runTest { ... }

  // Test case 2: Invalid account — SDS answers with HTTP 200 and a non-zero code,
  // which has to be reported as a failure.
  @Test fun readAuthorRewards_case2() = runTest { ... }
  ```
- **유효 계정과 무효 계정을 항상 함께 다룬다.** 아래에 적은 SDS 에러 관례를 잡아내는 것이
  case 2다.
- 행복 경로만이 아니라 계약을 검증한다. 화면이 "최신순"에 의존한다면 정렬을 단언한다.
- 계정은 `TestData`에서 가져온다. 새 계정을 하드코딩하지 않는다.

---

## 알려진 실패

### `SteemWorldServiceTest.get{Incoming,Outgoing,Expiring}Delegations_case2` (3건)

이 세 테스트는 무효 계정이 "성공 응답 + 빈 row 목록"으로 돌아온다고 단언한다.

```kotlin
assertEquals(0, body.code ?: -1)
assertNotNull(body.result)
assertEquals(0, body.result?.rows?.size ?: 0)
```

SDS는 더 이상 그렇게 응답하지 않는다. 직접 호출해 확인한 결과다.

```bash
$ curl -s "https://sds.steemworld.org/delegations_api/getIncomingDelegations/invalid10293845"
{"code":-1,"error":"Account id for 'invalid10293845' does not exist"}

$ curl -s "https://sds.steemworld.org/delegations_api/getIncomingDelegations/dorian-lee"
{"code":0,"result":{"cols":{},"rows":[]}}
```

세 delegations 엔드포인트 모두(그리고 `transfers_api`도) 알 수 없는 계정에 대해 HTTP 200과
`code: -1`, `error` 메시지를 반환하며 **`result`는 아예 없다.** 따라서 `code`는 `-1`이고
`result`는 null이라 단언이 깨진다.

**이 테스트들은 실패하는 것이 맞다.** 자기 문제가 아니라 프로덕션의 빈 구멍을 가리키고 있다.

1. `GetIncomingDelegationsResponseDTO`(및 outgoing / expiring 대응물)는 `code`와 `result`만
   선언하고 **`error` 필드가 없다.** transfer·reward DTO에는 있는데 여기에만 빠져 있어,
   설령 확인한다 해도 메시지를 읽을 수 없다.
2. `SteemWorldRepositoryImpl.readIncomingVestingDelegations()`는 `Response.isSuccessful`만
   확인하고 `body.code`는 확인하지 않는다. 그래서 알 수 없는 계정이
   `result?.toVestingDelegations(dgp) ?: listOf()`로 흘러가, 화면에는 오류 대신 **빈 위임
   목록**이 표시된다.

제대로 고치려면: delegations DTO 3개에 `error`를 추가하고, `toTransfersApiResult()`가 이미
하는 방식대로 Repository에서 `code`를 확인한 뒤, 이 세 케이스를 `Failure`를 기대하도록
바꾼다 — `readAuthorRewards_case2` 및 transfer 테스트와 같은 형태가 된다.

---

## 함정

**`cols`는 빈 객체일 수 있다.** 유효한 계정이라도 row가 없으면 `{"cols":{},"rows":[]}`로
응답한다. DTO 매퍼가 기본 열 인덱스로 폴백하는 이유가 바로 이것이다. `cols`에 키가 있다고
가정하면 안 된다.

**도메인 모델이 포맷된 문자열을 담고 있고, 테스트가 거기에 의존한다.**
`ReadRewardsUseCaseTest`는 정렬을 **문자열** 비교로 단언한다.

```kotlin
Assert.assertTrue(data[i - 1].time >= data[i].time)
```

이는 `Reward.time`이 `"yyyy-MM-dd HH:mm"` 형식이라 사전순이 곧 시간순이기 때문에만 성립한다.
이 포맷을 바꾸면 단언은 그대로 컴파일되고 통과하면서, 주장하는 바를 검증하는 일만 조용히
멈춘다.

**UI 모듈은 Android 프레임워크 호출에 기본값을 돌려준다.**

```groovy
testOptions {
    unitTests.returnDefaultValues = true
}
```

`Method getMainLooper in android.os.Looper not mocked` 때문에 추가되었다. 프레임워크 호출이
예외를 던지는 대신 `null`/`0`을 반환하므로, 실제 프레임워크 동작을 전혀 건드리지 않고도
테스트가 통과할 수 있다.

**`dorian-steem-test`가 테스트 클래스패스가 아니라 메인 클래스패스에 있다.** 세 모듈 모두
`implementation`으로 선언하고 있어, `TestData`의 실계정 이름을 포함한 픽스처가 프로덕션
APK에 컴파일되어 들어간다. `testImplementation`이어야 한다.

---

## 다루지 않는 영역

- `SteemWorldRepositoryImplTest`가 없다 — Repository는 UseCase 테스트를 통해서만 검증된다
- Compose UI 테스트가 없다. `dorian-steem-ui/src/androidTest/`에는 생성된 템플릿만 있다
- CI가 없다. `.github/` 워크플로가 없어 자동으로 실행되는 것이 없다
