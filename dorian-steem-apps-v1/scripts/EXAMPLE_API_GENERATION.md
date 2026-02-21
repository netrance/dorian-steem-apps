# Example: Auto-Generated Code for get_trending_tags API

This example shows what code would be auto-generated for the `condenser_api.get_trending_tags` API.

---

## 1️⃣ Data Module Files

### GetTrendingTagsParamsDTO.kt
```kotlin
package lee.dorian.steem_data.model

/**
 * Request parameters for condenser_api.get_trending_tags
 *
 * @param jsonrpc JSON-RPC version (always "2.0")
 * @param method API method name
 * @param params Array containing [tag, limit]
 * @param id Request ID
 */
data class GetTrendingTagsParamsDTO(
    val jsonrpc: String = "2.0",
    val method: String = "condenser_api.get_trending_tags",
    val params: Array<Any>,  // [tag: String, limit: Int]
    val id: Int = 1
) {
    companion object {
        /**
         * Creates parameters for getting trending tags
         * @param tag Starting tag name (empty for all tags)
         * @param limit Maximum number of tags to return
         */
        fun create(tag: String = "", limit: Int = 100): GetTrendingTagsParamsDTO {
            return GetTrendingTagsParamsDTO(
                params = arrayOf(tag, limit)
            )
        }
    }
}
```

### GetTrendingTagsResponseDTO.kt
```kotlin
package lee.dorian.steem_data.model

import com.google.gson.annotations.SerializedName

/**
 * Response from condenser_api.get_trending_tags
 */
data class GetTrendingTagsResponseDTO(
    @SerializedName("jsonrpc") val jsonrpc: String,
    @SerializedName("result") val result: Array<TrendingTagDTO>,
    @SerializedName("id") val id: Int
)

/**
 * Individual trending tag data from API
 */
data class TrendingTagDTO(
    @SerializedName("name") val name: String,
    @SerializedName("comments") val comments: Int,
    @SerializedName("top_posts") val topPosts: Int,
    @SerializedName("total_payouts") val totalPayouts: String
)

/**
 * Extension function to convert DTO to Domain model
 */
fun TrendingTagDTO.toDomainModel() = lee.dorian.steem_domain.model.TrendingTag(
    name = name,
    commentCount = comments,
    topPostCount = topPosts,
    totalPayouts = totalPayouts
)
```

### SteemService.kt (Method Addition)
```kotlin
interface SteemService {
    // ... existing methods

    /**
     * Fetches trending tags from Steem blockchain
     * Runs condenser_api.get_trending_tags API
     *
     * @param params Request parameters containing tag and limit
     * @return Response with array of trending tags
     */
    @POST(".")
    suspend fun getTrendingTags(
        @Body params: GetTrendingTagsParamsDTO
    ): Response<GetTrendingTagsResponseDTO>
}
```

### SteemRepositoryImpl.kt (Implementation Addition)
```kotlin
class SteemRepositoryImpl @Inject constructor(
    private val steemService: SteemService,
    private val dispatcher: CoroutineDispatcher
) : SteemRepository {

    // ... existing methods

    override suspend fun readTrendingTags(
        tag: String,
        limit: Int
    ): ApiResult<List<TrendingTag>> = withContext(dispatcher) {
        try {
            val response = steemService.getTrendingTags(
                GetTrendingTagsParamsDTO.create(tag, limit)
            )

            if (response.isSuccessful && response.body() != null) {
                val tags = response.body()!!.result.map { it.toDomainModel() }
                ApiResult.Success(tags)
            } else {
                ApiResult.Error("Failed to fetch trending tags: ${response.code()}")
            }
        } catch (e: Exception) {
            ApiResult.Error("Error fetching trending tags: ${e.message}")
        }
    }
}
```

---

## 2️⃣ Domain Module Files

### TrendingTag.kt
```kotlin
package lee.dorian.steem_domain.model

/**
 * Domain model representing a trending tag on Steem blockchain
 *
 * @property name Tag name
 * @property commentCount Number of comments with this tag
 * @property topPostCount Number of top posts with this tag
 * @property totalPayouts Total payout amount for this tag
 */
data class TrendingTag(
    val name: String,
    val commentCount: Int,
    val topPostCount: Int,
    val totalPayouts: String
)
```

### SteemRepository.kt (Interface Addition)
```kotlin
interface SteemRepository {
    // ... existing methods

    /**
     * Reads trending tags from Steem blockchain
     *
     * @param tag Starting tag name (empty string for all tags)
     * @param limit Maximum number of tags to return
     * @return ApiResult containing list of trending tags
     */
    suspend fun readTrendingTags(
        tag: String = "",
        limit: Int = 100
    ): ApiResult<List<TrendingTag>>
}
```

### ReadTrendingTagsUseCase.kt
```kotlin
package lee.dorian.steem_domain.usecase

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import lee.dorian.steem_domain.model.ApiResult
import lee.dorian.steem_domain.model.TrendingTag
import lee.dorian.steem_domain.repository.SteemRepository
import javax.inject.Inject

/**
 * Use case for fetching trending tags from Steem blockchain
 *
 * This use case encapsulates the business logic for retrieving trending tags,
 * allowing the presentation layer to remain agnostic of data source details.
 *
 * @property steemRepository Repository for accessing Steem data
 * @property dispatcher Coroutine dispatcher for async operations
 */
class ReadTrendingTagsUseCase @Inject constructor(
    private val steemRepository: SteemRepository,
    private val dispatcher: CoroutineDispatcher
) {

    /**
     * Fetches trending tags from the blockchain
     *
     * @param tag Starting tag name (empty for all tags)
     * @param limit Maximum number of tags to return (default: 100)
     * @return ApiResult containing list of trending tags
     *
     * Example usage:
     * ```kotlin
     * val result = readTrendingTagsUseCase("", 50)
     * when (result) {
     *     is ApiResult.Success -> displayTags(result.data)
     *     is ApiResult.Error -> showError(result.message)
     * }
     * ```
     */
    suspend operator fun invoke(
        tag: String = "",
        limit: Int = 100
    ): ApiResult<List<TrendingTag>> = withContext(dispatcher) {
        steemRepository.readTrendingTags(tag, limit)
    }
}
```

---

## 3️⃣ Usage in ViewModel (Example)

```kotlin
@HiltViewModel
class TrendingTagsViewModel @Inject constructor(
    private val readTrendingTagsUseCase: ReadTrendingTagsUseCase
) : ViewModel() {

    private val _trendingTags = MutableLiveData<List<TrendingTag>>()
    val trendingTags: LiveData<List<TrendingTag>> = _trendingTags

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error

    fun loadTrendingTags(tag: String = "", limit: Int = 100) {
        viewModelScope.launch {
            when (val result = readTrendingTagsUseCase(tag, limit)) {
                is ApiResult.Success -> {
                    _trendingTags.value = result.data
                }
                is ApiResult.Error -> {
                    _error.value = result.message
                }
            }
        }
    }
}
```

---

## 📊 File Structure

```
dorian-steem-apps-v1/
├── dorian-steem-data/
│   └── src/main/java/lee/dorian/steem_data/
│       ├── model/
│       │   ├── GetTrendingTagsParamsDTO.kt      ✨ NEW
│       │   ├── GetTrendingTagsResponseDTO.kt    ✨ NEW
│       │   └── TrendingTagDTO.kt                ✨ NEW
│       ├── repository/
│       │   └── SteemRepositoryImpl.kt           📝 MODIFIED
│       └── retrofit/
│           └── SteemService.kt                  📝 MODIFIED
│
└── dorian-steem-domain/
    └── src/main/java/lee/dorian/steem_domain/
        ├── model/
        │   └── TrendingTag.kt                   ✨ NEW
        ├── repository/
        │   └── SteemRepository.kt               📝 MODIFIED
        └── usecase/
            └── ReadTrendingTagsUseCase.kt       ✨ NEW
```

---

## ✅ Verification Checklist

After code generation:

1. **Compilation**
   - [ ] All files compile without errors
   - [ ] No import errors
   - [ ] Hilt dependency injection works

2. **Functionality**
   - [ ] API call succeeds with valid parameters
   - [ ] DTO correctly deserializes API response
   - [ ] DTO to Domain mapping works correctly
   - [ ] Error handling works properly

3. **Code Quality**
   - [ ] All classes have KDoc comments
   - [ ] Naming follows project conventions
   - [ ] Code follows Kotlin style guide

4. **Testing** (Optional but recommended)
   - [ ] Unit test for UseCase
   - [ ] Unit test for Repository
   - [ ] Integration test for API call

---

## 🔄 API Call Flow

```
ViewModel.loadTrendingTags()
    ↓
ReadTrendingTagsUseCase.invoke(tag, limit)
    ↓
SteemRepository.readTrendingTags(tag, limit)
    ↓
SteemRepositoryImpl.readTrendingTags(tag, limit)
    ↓
SteemService.getTrendingTags(GetTrendingTagsParamsDTO)
    ↓
[HTTP POST to STEEM API]
    ↓
GetTrendingTagsResponseDTO (JSON response)
    ↓
TrendingTagDTO.toDomainModel()
    ↓
ApiResult.Success(List<TrendingTag>)
    ↓
ViewModel updates LiveData
    ↓
UI displays trending tags
```

---

**Generated by**: Claude Code Automation
**Date**: 2026-02-17
**API Reference**: https://developers.steem.io/apidefinitions/#condenser_api.get_trending_tags
