# Why Use Cases? - Understanding the Benefits

This document explains why the Use Case pattern is beneficial in Clean Architecture, with examples from this project.

---

## 🤔 The Question

> "Use Cases are not mandatory in Clean Architecture. Why use them?"

**Short Answer**: While not strictly mandatory, Use Cases provide significant benefits in maintainability, testability, and code organization.

---

## 📊 Comparison: With vs Without Use Cases

### ❌ Without Use Cases (Direct Repository Access)

```kotlin
@HiltViewModel
class TagsViewModel @Inject constructor(
    private val steemRepository: SteemRepository,
    private val dispatcher: CoroutineDispatcher
) : ViewModel() {

    fun readRankedPosts(tag: String, sort: String) {
        viewModelScope.launch {
            _state.value = State.Loading

            // Business logic mixed with presentation logic
            val result = withContext(dispatcher) {
                try {
                    steemRepository.readRankedPosts(
                        sort = sort,
                        tag = tag,
                        observer = "",
                        limit = 20,
                        existingList = _existingPosts
                    )
                } catch (e: Exception) {
                    e.printStackTrace()
                    ApiResult.Error(e)
                }
            }

            when (result) {
                is ApiResult.Success -> _state.value = State.Success(result.data)
                is ApiResult.Error -> _state.value = State.Error(result.message)
            }
        }
    }
}
```

### ✅ With Use Cases (Current Project Pattern)

```kotlin
@HiltViewModel
class TagsViewModel @Inject constructor(
    private val readRankedPostsUseCase: ReadRankedPostsUseCase
) : ViewModel() {

    fun readRankedPosts(tag: String, sort: String) {
        viewModelScope.launch {
            _state.value = State.Loading

            // Clean, focused presentation logic
            when (val result = readRankedPostsUseCase(sort, tag)) {
                is ApiResult.Success -> _state.value = State.Success(result.data)
                is ApiResult.Error -> _state.value = State.Error(result.message)
            }
        }
    }
}
```

**Notice**: The ViewModel is now **simpler and focused** on presentation concerns.

---

## 💡 7 Key Benefits of Use Cases

### 1️⃣ Single Responsibility Principle

**Each Use Case = One Business Action**

```kotlin
// ✅ Clear, single purpose
class ReadAccountDetailsUseCase @Inject constructor(...)
class ReadSteemitProfileUseCase @Inject constructor(...)
class ReadSteemitWalletUseCase @Inject constructor(...)
class ReadRankedPostsUseCase @Inject constructor(...)

// vs.

// ❌ Repository with multiple responsibilities
interface SteemRepository {
    suspend fun readAccountDetails(...)
    suspend fun readSteemitProfile(...)
    suspend fun readSteemitWallet(...)
    suspend fun readRankedPosts(...)
    suspend fun readPosts(...)
    suspend fun readPostAndReplies(...)
    // ... 20 more methods
}
```

**Benefit**: Easy to find, understand, and modify specific business logic.

---

### 2️⃣ Encapsulation of Business Logic

Use Cases **encapsulate complex business rules** in one place.

#### Example: Complex Business Logic

Imagine you need to add validation or business rules:

```kotlin
class ReadRankedPostsUseCase @Inject constructor(
    private val steemRepository: SteemRepository,
    private val dispatcher: CoroutineDispatcher
) {
    suspend operator fun invoke(
        sort: String,
        tag: String,
        observer: String = "",
        limit: Int = 20,
        existingList: List<PostItem> = listOf()
    ): ApiResult<List<PostItem>> = withContext(dispatcher) {

        // ✅ Business validation in one place
        if (limit > 100) {
            return@withContext ApiResult.Error("Limit cannot exceed 100")
        }

        if (sort !in listOf("trending", "hot", "created", "promoted")) {
            return@withContext ApiResult.Error("Invalid sort type")
        }

        // ✅ Complex business logic
        val result = try {
            val posts = steemRepository.readRankedPosts(
                sort, tag, observer, limit, existingList
            )

            // Post-processing logic
            when (posts) {
                is ApiResult.Success -> {
                    // Filter out blocked users
                    val filtered = posts.data.filter { !isUserBlocked(it.author) }
                    // Add default values
                    val processed = filtered.map { addDefaultThumbnail(it) }
                    ApiResult.Success(processed)
                }
                is ApiResult.Error -> posts
            }
        } catch (e: Exception) {
            e.printStackTrace()
            ApiResult.Error(e)
        }

        result
    }

    private fun isUserBlocked(author: String): Boolean {
        // Business logic for blocked users
        return false
    }

    private fun addDefaultThumbnail(post: PostItem): PostItem {
        // Business logic for default thumbnails
        return post
    }
}
```

**Without Use Cases**: This logic would be scattered across multiple ViewModels! 😱

---

### 3️⃣ Reusability Across Multiple Presenters

One Use Case can be **reused by multiple ViewModels or screens**.

```kotlin
// TagsViewModel uses it
@HiltViewModel
class TagsViewModel @Inject constructor(
    private val readRankedPostsUseCase: ReadRankedPostsUseCase
) { ... }

// TrendingViewModel also uses the SAME use case
@HiltViewModel
class TrendingViewModel @Inject constructor(
    private val readRankedPostsUseCase: ReadRankedPostsUseCase
) { ... }

// SearchViewModel also uses it
@HiltViewModel
class SearchViewModel @Inject constructor(
    private val readRankedPostsUseCase: ReadRankedPostsUseCase
) { ... }
```

**Benefit**: Business logic is written **once** and reused everywhere.

---

### 4️⃣ Easy Testing

Use Cases are **pure business logic** → Easy to unit test!

```kotlin
class ReadRankedPostsUseCaseTest {

    private lateinit var useCase: ReadRankedPostsUseCase
    private lateinit var mockRepository: SteemRepository
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        mockRepository = mockk()
        useCase = ReadRankedPostsUseCase(mockRepository, testDispatcher)
    }

    @Test
    fun `invoke returns success when repository succeeds`() = runTest {
        // Given
        val expectedPosts = listOf(/* mock posts */)
        coEvery {
            mockRepository.readRankedPosts(any(), any(), any(), any(), any())
        } returns ApiResult.Success(expectedPosts)

        // When
        val result = useCase("trending", "bitcoin")

        // Then
        assertThat(result).isInstanceOf(ApiResult.Success::class.java)
        assertThat((result as ApiResult.Success).data).isEqualTo(expectedPosts)
    }

    @Test
    fun `invoke returns error when limit exceeds 100`() = runTest {
        // When
        val result = useCase("trending", "bitcoin", limit = 150)

        // Then
        assertThat(result).isInstanceOf(ApiResult.Error::class.java)
    }
}
```

**Benefit**: Test business logic **without** Android framework dependencies!

---

### 5️⃣ Clear Dependencies

Use Cases make dependencies **explicit and clear**.

```kotlin
// ✅ Clear: This ViewModel needs these specific use cases
@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val readSteemitProfileUseCase: ReadSteemitProfileUseCase,
    private val readAccountDetailsUseCase: ReadAccountDetailsUseCase
) : ViewModel() { ... }

// vs.

// ❌ Unclear: Which repository methods does this ViewModel use?
@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val steemRepository: SteemRepository  // Which methods? 🤷
) : ViewModel() { ... }
```

**Benefit**: Easy to understand what a ViewModel does just by looking at its constructor.

---

### 6️⃣ Threading/Dispatcher Management

Use Cases centralize **threading logic**.

```kotlin
class ReadRankedPostsUseCase @Inject constructor(
    private val steemRepository: SteemRepository,
    private val dispatcher: CoroutineDispatcher  // ✅ Centralized threading
) {
    suspend operator fun invoke(...): ApiResult<...> = withContext(dispatcher) {
        // All business logic runs on correct dispatcher
        steemRepository.readRankedPosts(...)
    }
}
```

**Benefit**: ViewModels don't need to worry about which dispatcher to use.

---

### 7️⃣ Composability

Use Cases can **combine multiple repository calls**.

```kotlin
class LoadUserDashboardUseCase @Inject constructor(
    private val steemRepository: SteemRepository,
    private val dispatcher: CoroutineDispatcher
) {
    suspend operator fun invoke(account: String): ApiResult<UserDashboard> {
        return withContext(dispatcher) {
            // ✅ Combine multiple data sources
            val profile = steemRepository.readSteemitProfile(account)
            val wallet = steemRepository.readSteemitWallet(account)
            val posts = steemRepository.readPosts(account, "blog", "", 10, emptyList())

            // Combine into single business object
            when {
                profile is ApiResult.Success &&
                wallet is ApiResult.Success &&
                posts is ApiResult.Success -> {
                    ApiResult.Success(
                        UserDashboard(
                            profile = profile.data,
                            wallet = wallet.data,
                            recentPosts = posts.data
                        )
                    )
                }
                else -> ApiResult.Error("Failed to load dashboard")
            }
        }
    }
}
```

**Benefit**: Complex workflows are encapsulated in one Use Case.

---

## 📐 Real-World Example from This Project

Let's look at a real use case from the codebase:

### ReadRankedPostsUseCase

```kotlin
class ReadRankedPostsUseCase @Inject constructor(
    private val steemRepository: SteemRepository,
    private val dispatcher: CoroutineDispatcher
) {
    operator suspend fun invoke(
        sort: String,
        tag: String,
        observer: String = "",
        limit: Int = 20,
        existingList: List<PostItem> = listOf()
    ): ApiResult<List<PostItem>> = withContext(dispatcher) {
        try {
            steemRepository.readRankedPosts(sort, tag, observer, limit, existingList)
        } catch (e: Exception) {
            e.printStackTrace()
            ApiResult.Error(e)
        }
    }
}
```

**What it provides**:
1. ✅ Default values for common parameters
2. ✅ Exception handling logic
3. ✅ Correct dispatcher usage
4. ✅ Clear, testable interface
5. ✅ Reusable across TagsViewModel, SearchViewModel, etc.

---

## ⚖️ When Use Cases Might Be Overkill

### Simple CRUD Operations

For very simple apps with basic CRUD:
```kotlin
// Maybe overkill for this:
class GetUserByIdUseCase(private val userRepository: UserRepository) {
    suspend operator fun invoke(id: String) = userRepository.getUserById(id)
}

// Could just use repository directly:
viewModel.getUser(id) // directly calls repository
```

### Single Screen Apps

If you only have 1-2 screens and no business logic, Use Cases might add unnecessary complexity.

### Prototypes

For quick prototypes or MVPs, direct repository access is faster.

---

## 🎯 When to Use Use Cases (Recommended)

### ✅ Use Cases are beneficial when:

1. **Multiple screens** use the same business logic
2. **Complex business rules** need to be enforced
3. **Testing** is important
4. **Team collaboration** - multiple developers
5. **Long-term maintenance** - project will grow
6. **Combining multiple data sources**
7. **Need clear separation** between layers

### ❌ Skip Use Cases when:

1. **Prototype/MVP** - speed is critical
2. **Very simple CRUD** - no business logic
3. **Single screen** - no reusability needed
4. **Learning project** - Keep it simple

---

## 🏗️ Architecture Layers Comparison

### With Use Cases (Current Project)

```
┌─────────────────────────────────────────┐
│         Presentation Layer              │
│           (ViewModels)                  │
│   - UI State Management                 │
│   - User Input Handling                 │
└──────────────┬──────────────────────────┘
               │ depends on
               ↓
┌─────────────────────────────────────────┐
│         Domain Layer                    │
│         (Use Cases)                     │
│   - Business Logic                      │
│   - Validation                          │
│   - Data Combination                    │
│   - Error Handling                      │
└──────────────┬──────────────────────────┘
               │ depends on
               ↓
┌─────────────────────────────────────────┐
│         Domain Layer                    │
│    (Repository Interface)               │
│   - Data Operations Contract            │
└──────────────┬──────────────────────────┘
               │ implemented by
               ↓
┌─────────────────────────────────────────┐
│          Data Layer                     │
│    (Repository Implementation)          │
│   - Data Fetching                       │
│   - Caching                             │
│   - DTO Mapping                         │
└─────────────────────────────────────────┘
```

**Responsibility is clearly distributed!**

### Without Use Cases

```
┌─────────────────────────────────────────┐
│         Presentation Layer              │
│           (ViewModels)                  │
│   - UI State Management                 │
│   - User Input Handling                 │
│   - ⚠️ Business Logic (mixed in!)       │
│   - ⚠️ Validation (scattered!)          │
│   - ⚠️ Error Handling (duplicated!)     │
└──────────────┬──────────────────────────┘
               │ depends on
               ↓
┌─────────────────────────────────────────┐
│         Domain Layer                    │
│    (Repository Interface)               │
│   - Data Operations Contract            │
└──────────────┬──────────────────────────┘
               │ implemented by
               ↓
┌─────────────────────────────────────────┐
│          Data Layer                     │
│    (Repository Implementation)          │
│   - Data Fetching                       │
│   - Caching                             │
│   - DTO Mapping                         │
└─────────────────────────────────────────┘
```

**Business logic is mixed with presentation logic! 😱**

---

## 📊 Summary Table

| Aspect | Without Use Cases | With Use Cases |
|--------|------------------|----------------|
| **Code Location** | Business logic in ViewModels | Business logic in Use Cases |
| **Reusability** | Copy-paste across ViewModels | Single Use Case, many callers |
| **Testing** | Must test with Android framework | Pure Kotlin unit tests |
| **Clarity** | Mixed concerns | Clear separation |
| **Maintenance** | Hard to find logic | Easy to locate |
| **Complexity** | Simple for small apps | Better for growing apps |

---

## 🎓 Conclusion

### For This Project: **Use Cases are the Right Choice** ✅

This project benefits from Use Cases because:

1. ✅ **Multiple screens** (Tags, Profile, Wallet, Posts, etc.)
2. ✅ **Complex API interactions** (STEEM blockchain)
3. ✅ **Shared business logic** (post loading, pagination, etc.)
4. ✅ **Long-term maintenance** (growing feature set)
5. ✅ **Testability matters** (business logic needs testing)

### The Trade-off is Worth It

**Small cost**: One extra layer (Use Cases)
**Big benefit**: Cleaner, more maintainable, more testable code

---

## 📚 Further Reading

- [Clean Architecture by Uncle Bob](https://blog.cleancoder.com/uncle-bob/2012/08/13/the-clean-architecture.html)
- [Android Clean Architecture Guide](https://developer.android.com/topic/architecture)
- [Use Cases in Android](https://proandroiddev.com/why-you-need-use-cases-interactors-142e8a6fe576)

---

**Last Updated**: 2026-02-17
**Author**: Claude Code
