# SteemWorld (SDS) API 연동 가이드

STEEM 공식 APPBASE API 연동 절차는 [generate-steem-api-integration.md](generate-steem-api-integration.md)에 정리되어 있다.
이 문서는 **SteemWorld(SDS) API**를 연동할 때의 절차를 다룬다. 두 API는 호출 방식
(JSON-RPC POST vs REST GET), 응답 구조, 에러 보고 방식이 모두 다르므로 코드 생성
패턴도 다르다.

- Base URL: `https://sds.steemworld.org/`
- 구현 기준 코드: `delegations_api`, `transfers_api`, `rewards_api` 연동분

---

## 1. 언제 SteemWorld API를 쓰는가

공식 Steem API가 지원하면 **Steem API 우선**, 지원하지 않거나 구현이 지나치게 복잡한
경우에만 SteemWorld API를 쓴다.

현재 SteemWorld로 구현된 기능:

| 기능 | 엔드포인트 | 선택 이유 |
|------|-----------|----------|
| 위임 내역 (incoming/outgoing/expiring) | `delegations_api/*` | 공식 API에 expiring 조회가 없음 |
| 송금 내역 (보낸/받은) | `transfers_api/getTransfersByTypeFrom`, `getTransfersByTypeTo` | `account_history_api`는 op 필터링 후 클라이언트 가공 부담이 큼 |
| 보상 내역 (author/curation) | `rewards_api/getRewards` | 공식 API는 전체 이력 조회가 사실상 불가능 |

---

## 2. SDS API 제약 (문서에 없는 사항, 직접 호출로 확인)

코드를 쓰기 전에 반드시 알고 있어야 하는 내용이다.

1. **에러를 HTTP 200으로 보고한다.**
   존재하지 않는 계정 등을 요청해도 `Response.isSuccessful`이 `true`이고, 본문이
   `{"code": -1, "error": "..."}`로 온다. → `isSuccessful`만 보면 안 되고 **`code`까지
   확인**해야 한다. (`SteemWorldRepositoryImpl.SDS_SUCCESS_CODE = 0`)

2. **응답은 `cols` / `rows` 구조다.**
   `result.rows`는 값 배열의 배열이고, 각 값의 위치는 `result.cols`가 알려준다.
   필드명이 있는 객체가 아니므로 **`cols` 인덱스를 참조해 `rows`를 매핑**해야 한다.

3. **op마다 `cols`가 다르다.**
   예: `author_reward` = time·author·permlink·sbd·steem·vests,
   `curation_reward` = time·vests·author·permlink.
   하나의 DTO로 여러 op를 받는다면 op별 매핑 함수를 따로 둔다.

4. **JSON 숫자는 Gson이 전부 `Double`로 역직렬화한다.**
   `rows`의 타입이 `List<List<Any>>`이므로 `row[i] as? Double` 후 `toLong()` / `toFloat()`
   으로 변환한다.

5. **API별로 보관 기간과 정렬 옵션이 다르다.**
   - `account_history_api`: **최근 90일치만 보관** (`store_days: 90`).
   - `transfers_api`: 전체 이력 보유. `orderBy`/`orderDir` 지원 → 최신순 조회 가능.
   - `rewards_api`: 전체 이력 보유(2017년 데이터까지). **정렬 파라미터가 없고 시간
     오름차순 고정**이며, limit을 넘는 행은 시간 범위의 **최신 쪽**을 잘라낸다.
     → 최신순 목록이 필요하면 시간 범위를 끝까지 읽은 뒤 클라이언트에서 뒤집어야 한다.
   - `rewards_api`는 `fromTime`에 `0`을 주면 에러 → 전체 범위는 `1-9999999999`.

6. **`transfers_api/getTransfers`의 `from`/`to`는 AND 조건이다.**
   한 계정의 송/수신을 합치려면 2회 호출 + 클라이언트 병합이 필요하다. 화면을
   보낸 내역 / 받은 내역으로 분리한 이유다.

7. **시간은 Unix timestamp(초) 단위다.** 도메인 모델로 넘길 때 로컬 시간 문자열
   (`yyyy-MM-dd HH:mm`)로 변환한다.

---

## 3. 레이어 흐름

```
Compose Screen  →  ViewModel  →  UseCase  →  SteemWorldRepository (interface)
   (ui)            (ui)          (domain)      (domain)
                                                   ↑ implements
                                     SteemWorldRepositoryImpl  (data)
                                                   ↓
                                       SteemWorldClient.apiService
                                                   ↓
                                          SteemWorldService  (data)
                                                   ↓
                                      Response DTO → Domain Model
```

VESTS로 지급되는 값(위임량, 보상)을 SP로 환산하려면 **Steem 공식 API의
`getDynamicGlobalProperties`를 함께 호출**해야 한다. 이 경우 `SteemClient`와
`SteemWorldClient`를 `async`로 병렬 호출한다.

---

## 4. 연동 절차

새 SDS 엔드포인트 하나를 화면까지 연결하는 순서. 각 단계는 기존 구현을 그대로
복제해 이름만 바꾸면 되도록 되어 있다.

### Step 0. 엔드포인트 직접 호출로 스펙 확인 (필수)

SDS 문서는 op 타입 *목록*만 제공하고 각 op의 payload 스키마는 문서화하지 않는다.
따라서 **먼저 호출해서 `cols`와 `rows`의 실제 형태를 확인**한다.

```bash
# 정상 계정
curl -s "https://sds.steemworld.org/rewards_api/getRewards/author_reward/dorian-lee/1-9999999999/5/0" | head -40

# 무효 계정 (에러 응답 형태 확인)
curl -s "https://sds.steemworld.org/rewards_api/getRewards/author_reward/invalid10293845/1-9999999999/5/0"
```

확인할 것: `cols`의 키와 순서 / 각 열의 타입 / limit 초과 시 잘리는 방향 /
정렬 방향 / 무효 입력 시의 `code`·`error`.

### Step 1. Response DTO 작성 (data 모듈)

`dorian-steem-data/src/main/java/lee/dorian/steem_data/model/{feature}/Get{Name}ResponseDTO.kt`

한 파일에 다음 4가지를 함께 둔다. (기존 `GetTransfersResponseDTO.kt`, `GetRewardsResponseDTO.kt` 참고)

```kotlin
// 1) 최상위 응답: code / error / result
data class GetXxxResponseDTO(
    val code: Int?,
    val error: String?,
    val result: XxxResultDTO?
)

// 2) result: cols + rows, 그리고 매핑 함수
data class XxxResultDTO(
    val cols: XxxColsDTO?,
    val rows: List<List<Any>>?
) {
    fun toXxxList(): List<XxxDTO> {
        // cols가 없을 때를 대비해 기본 인덱스를 상수로 둔다.
        val timeIdx = cols?.time ?: DEFAULT_TIME_INDEX
        // ...

        return rows?.mapNotNull { row ->
            XxxDTO(
                // 숫자는 전부 Double로 들어온다.
                time = (row.getOrNull(timeIdx) as? Double)?.toLong() ?: return@mapNotNull null,
                // ...
            )
        } ?: listOf()
    }

    fun toXxxs(): List<Xxx> = toXxxList().map { it.toXxx() }

    companion object {
        const val DEFAULT_TIME_INDEX = 0
        // ...
    }
}

// 3) cols: 열 이름 → 인덱스. 없을 수 있으므로 전부 nullable.
data class XxxColsDTO(
    val time: Int?,
    // ...
)

// 4) row 1건. 도메인 모델로의 변환은 여기서 한다.
data class XxxDTO(
    val time: Long,   // Unix timestamp (초)
    // ...
) {
    fun toXxx(): Xxx = Xxx(
        time = time.toLocalTimeString(),
        // ...
    )
}
```

파일 하단에 **성공 응답 / 실패 응답 실제 JSON 예시를 주석으로 남긴다.** 기존 두
파일이 모두 그렇게 되어 있고, 문서화되지 않은 스키마를 기억하는 유일한 수단이다.

VESTS → SP 환산이 필요하면 변환 함수가 `GetDynamicGlobalPropertiesDTO`를 인자로 받게 한다.

```kotlin
fun toXxx(dgp: GetDynamicGlobalPropertiesDTO): Xxx {
    val totalVestingShares = dgp.total_vesting_shares?.removeSubstring(" VESTS")?.toFloat() ?: 0f
    val totalVestingFundSteem = dgp.total_vesting_fund_steem?.removeSubstring(" STEEM")?.toFloat() ?: 0f
    val steemPower = Converter.toSteemPowerFromVest(vests, totalVestingShares, totalVestingFundSteem)
    // ...
}
```

### Step 2. 도메인 모델 작성 (domain 모듈)

`dorian-steem-domain/src/main/java/lee/dorian/steem_domain/model/Xxx.kt`

**화면이 그대로 그릴 수 있는 형태**로 만든다. 포맷팅(시간 문자열, 금액 + 단위)은
DTO 변환 시점에 끝내고, 도메인 모델에는 가공된 `String`을 담는다.

```kotlin
data class Xxx(
    val time: String = "",     // 로컬 시간, "yyyy-MM-dd HH:mm"
    val amount: String = "",   // 예: "18.868 STEEM"
    // ...
)
```

타입 구분이 필요하면 enum을 별도 파일로 둔다. (`RewardType.kt` 참고)

### Step 3. SteemWorldService에 함수 추가 (data 모듈)

`dorian-steem-data/src/main/java/lee/dorian/steem_data/retrofit/SteemWorldService.kt`

SDS는 **REST GET + 경로 파라미터**다. 쿼리 스트링이 아니므로 전부 `@Path`를 쓴다.

```kotlin
// 주석으로 정렬 방향·페이징 동작 등 호출자가 알아야 할 제약을 적는다.
@GET("xxx_api/getXxx/{account}/{orderBy}/{orderDir}/{limit}/{offset}")
suspend fun getXxx(
    @Path("account") account: String,
    @Path("offset") offset: Int = DEFAULT_OFFSET,
    @Path("limit") limit: Int = DEFAULT_XXX_LIMIT,
    @Path("orderBy") orderBy: String = ORDER_BY_TIME,
    @Path("orderDir") orderDir: String = ORDER_DIR_DESC
): Response<GetXxxResponseDTO>
```

- 반환 타입은 반드시 `Response<T>` (에러 본문을 읽어야 하므로).
- 파라미터 **선언 순서는 URL 순서와 무관**하게, 호출자가 자주 지정하는 것부터 둔다.
  기본값이 있는 파라미터를 뒤에 모아 호출부를 짧게 유지한다.
- 매직 넘버/문자열은 전부 `companion object` 상수로 뺀다. limit 기본값·최대값,
  op 타입 목록, 정렬 상수, 시간 범위 경계값 등.

### Step 4. SteemWorldRepository에 함수 추가 (domain 모듈)

`dorian-steem-domain/src/main/java/lee/dorian/steem_domain/repository/SteemWorldRepository.kt`

```kotlin
// 결과 순서 등 화면이 의존하는 계약을 주석으로 명시한다.
// Reads the xxx of the given account, latest first.
suspend fun readXxx(
    account: String,
    offset: Int,
    limit: Int
): ApiResult<List<Xxx>>
```

- 반환은 항상 `ApiResult<T>` (`Success` / `Failure` / `Error`).
- DTO나 Retrofit 타입은 절대 노출하지 않는다.

### Step 5. SteemWorldRepositoryImpl 구현 (data 모듈)

`dorian-steem-data/src/main/java/lee/dorian/steem_data/repository/SteemWorldRepositoryImpl.kt`

**(a) 단순 조회 — DGP가 필요 없는 경우**

```kotlin
override suspend fun readXxx(
    account: String,
    offset: Int,
    limit: Int
): ApiResult<List<Xxx>> = withContext(dispatcher) {
    try {
        val response = SteemWorldClient.apiService.getXxx(account, offset, limit)
        response.toXxxApiResult()
    } catch (e: Exception) {
        e.printStackTrace()
        ApiResult.Error(e)
    }
}

// SDS는 자체 에러를 HTTP 200 + 비-0 code로 보고하므로 본문까지 확인해야 한다.
private fun Response<GetXxxResponseDTO>.toXxxApiResult(): ApiResult<List<Xxx>> {
    if (!isSuccessful) {
        return ApiResult.Failure(errorBody()?.string() ?: "")
    }

    val body = body() ?: return ApiResult.Failure("The body of response is empty")
    if (body.code != SDS_SUCCESS_CODE) {
        return ApiResult.Failure(body.error ?: "")
    }

    return ApiResult.Success(body.result?.toXxxs() ?: listOf())
}
```

**(b) VESTS 환산이 필요한 경우 — DGP 병렬 호출**

```kotlin
val responseDGPAsync = async {
    SteemClient.apiService.getDynamicGlobalProperties(GetDynamicGlobalPropertiesParamsDTO(id = 1))
}
val responseXxxAsync = async {
    SteemWorldClient.apiService.getXxx(account)
}
val responseDGP = responseDGPAsync.await()
val responseXxx = responseXxxAsync.await()
// 각각 isSuccessful 확인 → dgp 추출 → result.toXxxs(dgp)
```

**(c) 페이징이 필요한 경우**

`rewards_api`처럼 정렬을 지정할 수 없고 limit 초과분이 잘리는 API는, 범위를 끝까지
읽은 뒤 뒤집는다. `readRewards`가 이 패턴의 구현체다.

```kotlin
while (true) {
    // isSuccessful 확인 → body의 code 확인 → rows 누적
    val rowCount = result.rows?.size ?: 0
    // 가득 차지 않은 페이지가 마지막 페이지다.
    if (rowCount < PAGE_SIZE) break
    offset += PAGE_SIZE
    response = SteemWorldClient.apiService.getXxx(..., offset, PAGE_SIZE)
}
ApiResult.Success(items.reversed())
```

작업량을 제한하는 것이 시간 범위뿐이라면, **호출자가 적절한 범위를 주는 것이
전제**라는 점을 주석에 남긴다.

체크리스트:
- [ ] `withContext(dispatcher)`로 감싼다 (dispatcher는 생성자 주입).
- [ ] 전체를 `try/catch`로 감싸고 예외는 `ApiResult.Error`로 변환한다.
- [ ] `isSuccessful` **와** `body.code` 둘 다 확인한다.
- [ ] 반복되는 응답 처리는 `private fun Response<T>.toXxxApiResult()` 확장으로 뺀다.

### Step 6. UseCase 작성 (domain 모듈)

`dorian-steem-domain/src/main/java/lee/dorian/steem_domain/usecase/ReadXxxUseCase.kt`

엔드포인트 하나(또는 화면 동작 하나)당 UseCase 하나. `readOutgoingTransfers` /
`readIncomingTransfers`처럼 **방향이 다르면 UseCase도 분리**한다.

```kotlin
class ReadXxxUseCase @Inject constructor(
    private val steemWorldRepository: SteemWorldRepository,
    private val dispatcher: CoroutineDispatcher
) {

    companion object {
        const val DEFAULT_OFFSET = 0
        const val DEFAULT_LIMIT = 100
    }

    suspend operator fun invoke(
        account: String,
        offset: Int = DEFAULT_OFFSET,
        limit: Int = DEFAULT_LIMIT
    ): ApiResult<List<Xxx>> = withContext(dispatcher) {
        try {
            steemWorldRepository.readXxx(account, offset, limit)
        } catch (e: Exception) {
            e.printStackTrace()
            ApiResult.Error(e)
        }
    }
}
```

- `operator fun invoke`로 선언해 `readXxxUseCase(account)` 형태로 호출한다.
- 화면 기본값(limit, 기본 시간 범위 등)은 Service 상수와 별개로 UseCase에 둔다.

### Step 7. DI 확인 (ui 모듈)

`SteemWorldRepository` 바인딩은 `di/RepositoryModule.kt`에 **이미 되어 있으므로
보통 수정할 필요가 없다.** UseCase는 `@Inject constructor`라 별도 등록이 불필요하다.

```kotlin
@Binds
abstract fun bindSteemWorldRepository(impl: SteemWorldRepositoryImpl): SteemWorldRepository
```

`CoroutineDispatcher`는 `di/CoroutinesModule.kt`가 `Dispatchers.IO`로 제공한다.
새 Repository를 만든 경우에만 `RepositoryModule`에 `@Binds`를 추가한다.

### Step 8. ViewModel 연결 (ui 모듈)

UseCase를 생성자 주입하고, 상태를 `StateFlow<State<T>>`로 노출한다.
(`ui/wallet/WalletViewModel.kt` 참고)

```kotlin
private val _flowXxxState = MutableStateFlow<State<List<Xxx>>>(State.Empty)
val flowXxxState = _flowXxxState.asStateFlow()

fun readXxx(account: String) = viewModelScope.launch {
    _flowXxxState.emit(State.Loading)
    val apiResult = readXxxUseCase(account)
    val newState = when (apiResult) {
        is ApiResult.Failure -> State.Failure(apiResult.content)
        is ApiResult.Error -> State.Error(apiResult.throwable)
        is ApiResult.Success -> State.Success(apiResult.data)
    }

    _flowXxxState.emit(newState)
}
```

`ApiResult` → `State` 변환의 `when`은 **세 분기를 모두 처리**한다.

### Step 9. Compose 화면 연결 (ui 모듈)

지갑 화면처럼 탭으로 붙는 경우 (`ui/wallet/WalletScreen.kt`, `WalletTabInfo.kt` 참고):

1. `WalletTabInfo`에 항목 추가
2. 탭 분기(`when (walletTabList[selectedTabIndex])`)에 `XxxTabContent` 추가
3. `XxxTabContent`에서 `hiltViewModel()` → `collectAsStateWithLifecycle()` →
   `LaunchedEffect(key)`로 조회 트리거
4. `State.Empty`/`Loading`은 `Loading()`, 실패는 `ErrorOrFailure()`, 성공은 리스트
5. `@Preview` 컴포저블을 함께 작성한다 (테스트 데이터 사용)

```kotlin
LaunchedEffect(account, selectedType) {
    if (account.isNotEmpty()) {
        viewModel.readXxx(account, ...)
    }
}
```

### Step 10. 테스트 작성

SDS 연동 테스트는 **실제 네트워크를 호출하는 통합 테스트**다. (mock 서버를 쓰지 않는다.)
계정은 `dorian-steem-test`의 `TestData`에서 가져온다.

**(a) Service 레벨** — `dorian-steem-data/src/test/java/lee/dorian/steem_data/retrofit/SteemWorldServiceTest.kt`

응답의 **형태**를 검증한다. row의 길이와 각 열의 타입을 확인해, SDS 스키마가
바뀌면 여기서 먼저 깨지도록 한다.

```kotlin
// Test case 1: Valid account — response is successful, rows are non-empty,
// and each row has the correct format: [time, from, to, vests].
@Test
fun getXxx_case1() = runTest {
    val response = SteemWorldClient.apiService.getXxx(TestData.singleAccount)
    assertTrue(response.isSuccessful)
    response.body()?.let { body ->
        assertEquals(0, body.code ?: -1)
        val rows = body.result?.rows ?: listOf()
        for (row in rows) {
            assertEquals(4, row.size)
            assertTrue(row[0] is Double)   // time
            // ...
        }
        return@runTest
    }

    fail("The body of response is empty!")
}

// Test case 2: Invalid account — HTTP 200 with a non-zero code.
```

**(b) UseCase 레벨** — `dorian-steem-data/src/test/java/lee/dorian/steem_domain/usecase/ReadXxxUseCaseTest.kt`

`CommonPartOfViewModelTest`를 상속하고 실제 `SteemWorldRepositoryImpl(dispatcher)`를
넣어 만든다. 최소한 다음 케이스를 다룬다.

- 정상 계정 → `ApiResult.Success`, 각 항목의 필드 형식 검증
- 무효 계정 → `ApiResult.Failure`, `content`가 비어 있지 않음
- 정렬 계약 검증 (최신순이면 `data[i-1].time >= data[i].time`)
- 범위/페이징이 있다면: 범위 밖 데이터가 없는지, 페이지 경계에서 유실이 없는지

```bash
./gradlew :dorian-steem-data:test --tests "*SteemWorldServiceTest*"
./gradlew :dorian-steem-data:test --tests "*ReadXxxUseCaseTest*"
```

---

## 5. 생성/수정 파일 요약

| # | 레이어 | 파일 | 작업 |
|---|--------|------|------|
| 1 | data | `model/{feature}/Get{Name}ResponseDTO.kt` | 신규 |
| 2 | domain | `model/{Name}.kt` | 신규 |
| 3 | data | `retrofit/SteemWorldService.kt` | 함수·상수 추가 |
| 4 | domain | `repository/SteemWorldRepository.kt` | 함수 추가 |
| 5 | data | `repository/SteemWorldRepositoryImpl.kt` | 구현 추가 |
| 6 | domain | `usecase/Read{Name}UseCase.kt` | 신규 |
| 7 | ui | `di/RepositoryModule.kt` | 보통 수정 없음 |
| 8 | ui | `ui/{feature}/{Feature}ViewModel.kt` | UseCase 주입·상태 추가 |
| 9 | ui | `ui/{feature}/{Feature}Screen.kt` | 화면 추가 |
| 10 | data(test) | `retrofit/SteemWorldServiceTest.kt`, `usecase/Read{Name}UseCaseTest.kt` | 테스트 추가 |

---

## 6. 최종 체크리스트

- [ ] 엔드포인트를 `curl`로 직접 호출해 `cols`/`rows`/에러 응답을 확인했다
- [ ] 응답 DTO에 `code`, `error`, `result`가 모두 있다
- [ ] `cols` 인덱스를 참조해 `rows`를 매핑했고, `cols`가 없을 때의 기본 인덱스 상수가 있다
- [ ] 숫자를 `as? Double` 후 변환했다
- [ ] DTO 파일 하단에 성공/실패 응답 JSON 예시 주석을 남겼다
- [ ] Repository에서 `isSuccessful`과 `body.code`를 **둘 다** 확인한다
- [ ] VESTS 값이 있다면 DGP를 `async`로 병렬 호출해 SP로 환산했다
- [ ] 시간을 로컬 시간 문자열로 변환했다
- [ ] Service / Repository / UseCase의 매직값을 `companion object` 상수로 뺐다
- [ ] 결과 정렬 순서·시간 범위 같은 호출 계약을 주석으로 명시했다
- [ ] 정상 계정 / 무효 계정 테스트를 모두 작성했다
- [ ] `./gradlew build`가 통과한다

---

## 7. 참고 파일

| 목적 | 파일 |
|------|------|
| 가장 단순한 예 | `delegations_api` 연동분 (`GetIncomingDelegationsResponseDTO.kt`) |
| 페이징·정렬 있는 예 | `GetTransfersResponseDTO.kt`, `readOutgoingTransfers` |
| op별 cols·전체 범위 페이징 예 | `GetRewardsResponseDTO.kt`, `readRewards` |
| 화면 연결 예 | `ui/wallet/WalletScreen.kt`, `WalletViewModel.kt` |
| STEEM 공식 API 절차 | [generate-steem-api-integration.md](generate-steem-api-integration.md) |
| UseCase를 쓰는 이유 | [../docs/WHY_USE_CASES.md](../docs/WHY_USE_CASES.md) |
