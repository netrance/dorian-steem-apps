# 백로그

작업하다 발견했지만 그 자리에서 고치지 않은 것들. 코드에 `TODO` 주석을 흩어 놓는 대신
여기 모은다. 주석은 해당 파일을 열어야 보이고, 다음에 무엇을 할지 고르는 데는 쓸 수 없다.

적는 기준은 **지금 확인된 사실**이다. 확인하지 않은 추측, "언젠가 하면 좋을 것"은 적지
않는다. 항목을 해결하면 지운다.

각 항목은 왜 아직 안 했는지를 함께 적는다. 미루기로 한 판단에도 이유가 있고, 그것을
적어두지 않으면 다음에 같은 판단을 처음부터 다시 하게 된다.

---

## 진행 중인 작업의 남은 단계

### ContentHistory 화면 (Step 9)

`content_history_api` 연동은 ViewModel까지 끝났다 —
`PostContentViewModel.flowContentHistoryState` / `readContentHistory()`이며
`PostContentViewModelTest.readContentHistory`가 이를 검증한다. 남은 것은 Compose 화면이다.

- 포스트 내용 화면에 개정판 목록(수정 일시)과 선택한 개정판 렌더링을 붙인다
- 답글이 쓰는 `ModalBottomSheet`와 같은 구조를 따르면 된다 (`PostContentFragment.kt`)
- 절차는 [integrate-steemworld-api.md](../scripts/integrate-steemworld-api.md) Step 9

**먼저 결정해야 할 것:** 개정판이 1건일 때 UI를 어떻게 할지. 수정 이력이 있는 글은
드물다 — 본인 계정(`dorian-lee`/`dorian-mobileapp`/`dorian-dev`)의 최근 글 60건을
조회했을 때 **개정판이 2건 이상인 글이 하나도 없었다.** 진입점을 숨길지 "수정 없음"으로
보여줄지에 따라 화면 구조가 갈린다.

---

## 알려진 결함

### 위임 조회가 무효 계정을 빈 목록으로 표시한다

`delegations_api` DTO 3개에 `error` 필드가 없고, `SteemWorldRepositoryImpl`의 위임 조회
3개가 `body.code`를 확인하지 않는다. 그래서 존재하지 않는 계정이 `ApiResult.Failure`가
아니라 **`Success` + 빈 목록**으로 흘러가, 화면에 오류 대신 "위임 없음"이 나온다.

전체 분석과 수정 방향은 [TESTING.md의 "알려진 실패"](TESTING.md#알려진-실패)에 있다.
`SteemWorldServiceTest`의 실패 3건이 이것을 가리키고 있다.

**아직 안 한 이유:** transfer·reward·content_history 쪽은 `isSuccessful`과 `body.code`를
모두 확인하도록 되어 있어서, 위임 3개만 과거 패턴으로 남아 있는 상태다. 고치는 방법은
이미 정해져 있고(`toTransfersApiResult()`와 같은 형태), 막혀 있는 것은 없다.

### `TestData`가 프로덕션 APK에 포함된다

세 모듈이 `dorian-steem-test`를 `implementation`으로 선언하고 있다.

```
dorian-steem-ui/build.gradle:58
dorian-steem-data/build.gradle:16
dorian-steem-domain/build.gradle:14
```

`testImplementation`이어야 한다. 현재는 실계정 이름이 담긴 테스트 픽스처가 배포
APK까지 따라간다.

**주의:** 도메인 UseCase 테스트가 data 모듈에 있는 이유
([TESTING.md](TESTING.md#도메인-usecase-테스트가-data-모듈에-있는-이유))와 맞물려 있으므로,
바꾼 뒤 세 모듈의 테스트가 모두 컴파일되는지 확인해야 한다.

---

## 테스트 공백

### `SteemRepositoryImpl.readDynamicGlobalProperties()` 테스트가 없다

`SteemRepositoryImpl`의 8개 함수 중 유일하게 테스트가 없다. 23c118d에서 추가된 뒤로
`SteemRepositoryImplTest`에 대응 케이스가 들어오지 않았다.

### `SteemRepositoryImplTest`의 무효 계정 케이스가 일부만 있다

`readAccountDetails` / `readSteemitProfile` / `readSteemitWallet`에는 `case2`(무효 계정)가
있지만, 다음 4개에는 유효 계정 케이스만 있다.

- `readPosts`
- `readRankedPosts`
- `readPostAndReplies`
- `readAccountHistory`

[CLAUDE.md](../CLAUDE.md)의 "For every API-backed feature, cover both a valid account and an
invalid account" 규약과 어긋난다.

### ViewModel 4개에 테스트가 없다

`IncomingDelegationListViewModel`, `OutgoingDelegationListViewModel`,
`PostImagePagerViewModel`, `VoteListViewModel`. ViewModel 11개 중 7개에만 테스트가 있다.

### Compose UI 테스트가 없다

`dorian-steem-ui/src/androidTest/`에는 생성된 템플릿만 있다.

### CI가 없다

`.github/` 워크플로가 없어 자동으로 실행되는 것이 없다. 테스트 대부분이 실제 네트워크를
호출하는 통합 테스트라([TESTING.md](TESTING.md)), CI를 붙이면 원격 API 상태에 따라
빨간불이 켜진다는 점을 먼저 정리해야 한다.

---

## 정리거리

### RxJava2 잔재

`io.reactivex`를 실제로 쓰는 코드는 없는데 다음 셋이 남아 있다.

| 파일 | 내용 |
|---|---|
| `SteemClient.kt:4,14` | `RxJava2CallAdapterFactory` import과 `addCallAdapterFactory()` 호출 |
| `SteemService.kt:3` | 미사용 `import io.reactivex.Single` |
| `ReadSteemitWalletUseCase.kt:3` | 미사용 `import io.reactivex.Single` |

지우면 `libs.versions.toml`의 RxJava 의존도 함께 정리할 수 있다.

### iCloud 충돌 사본이 `build/` 안에서 빌드를 깨뜨린다

0e50bf4이 `.gitignore`에 `* [0-9].*` 규칙을 추가했지만, 그것은 **git에만** 적용된다.
iCloud가 `build/` 안에 만든 사본은 그대로 빌드에 들어간다. 실제로 겪은 두 가지:

- `kapt`가 생성한 `Xxx 2.java` → `javac`가
  `class Xxx is public, should be declared in a file named Xxx.java`로 거부
- `R$drawable 2.class` → `bundleDebugClassesToRuntimeJar`가
  `Zip file ... already contains entry`로 거부

`./gradlew clean`이면 해소되지만, 근본적으로는 프로젝트를 iCloud가 동기화하지 않는
위치로 옮기거나 `~/Documents` 동기화에서 제외하는 것이 해법이다.

### `.kotlin/`이 `.gitignore`에 없다

Kotlin 빌드 산출물인데 untracked로 남아 `git status`에 계속 나타난다. 루트
`.gitignore`에 추가하면 된다.
