# STEEM API Integration Generator

This document describes the automated process for integrating STEEM APPBASE APIs into the project.

## Overview

This automation generates all necessary boilerplate code across the clean architecture layers:
- **Data Layer**: DTOs, Service methods, Repository implementation
- **Domain Layer**: Domain models, Repository interface, Use cases

## Pattern Analysis

### Current Architecture Pattern

```
┌─────────────────────────────────────────────────────────┐
│                     Presentation Layer                   │
│                  (dorian-steem-ui)                       │
│                      ViewModel                           │
└────────────────────────┬────────────────────────────────┘
                         │
                         │ calls
                         ↓
┌─────────────────────────────────────────────────────────┐
│                      Domain Layer                        │
│                (dorian-steem-domain)                     │
│                                                          │
│  UseCase (e.g., ReadAccountDetailsUseCase)              │
│    ↓                                                     │
│  Repository Interface (SteemRepository)                  │
│  Domain Models (AccountDetails, Post, etc.)              │
└────────────────────────┬────────────────────────────────┘
                         │
                         │ implements
                         ↓
┌─────────────────────────────────────────────────────────┐
│                       Data Layer                         │
│                  (dorian-steem-data)                     │
│                                                          │
│  Repository Impl (SteemRepositoryImpl)                   │
│    ↓                                                     │
│  Service Interface (SteemService)                        │
│    ↓                                                     │
│  DTO Models (GetAccountsParamsDTO, etc.)                 │
└─────────────────────────────────────────────────────────┘
```

## File Generation Template

### 1. Data Module Files

#### Request DTO Pattern
```kotlin
// File: dorian-steem-data/src/main/java/lee/dorian/steem_data/model/{ApiName}ParamsDTO.kt
package lee.dorian.steem_data.model

data class {ApiName}ParamsDTO(
    val jsonrpc: String = "2.0",
    val method: String = "{api_method}",  // e.g., "condenser_api.get_accounts"
    val params: {ParamsType},              // e.g., Array<Array<String>>
    val id: Int = 1
)
```

#### Response DTO Pattern
```kotlin
// File: dorian-steem-data/src/main/java/lee/dorian/steem_data/model/{ApiName}ResponseDTO.kt
package lee.dorian.steem_data.model

import com.google.gson.annotations.SerializedName

data class {ApiName}ResponseDTO(
    @SerializedName("jsonrpc") val jsonrpc: String,
    @SerializedName("result") val result: {ResultType},
    @SerializedName("id") val id: Int
)

// Response data class
data class {ApiName}ResultDTO(
    // Fields based on API documentation
)
```

#### Service Interface Addition
```kotlin
// File: dorian-steem-data/src/main/java/lee/dorian/steem_data/retrofit/SteemService.kt
interface SteemService {
    // ... existing methods

    /**
     * Runs {api_method} API
     * @param params Request parameters
     * @return Response containing {result_description}
     */
    @POST(".")
    suspend fun {methodName}(
        @Body params: {ApiName}ParamsDTO
    ): Response<{ApiName}ResponseDTO>
}
```

#### Repository Implementation Addition
```kotlin
// File: dorian-steem-data/src/main/java/lee/dorian/steem_data/repository/SteemRepositoryImpl.kt
class SteemRepositoryImpl @Inject constructor(
    private val steemService: SteemService,
    private val dispatcher: CoroutineDispatcher
) : SteemRepository {

    // ... existing methods

    override suspend fun {methodName}(
        {parameters}
    ): ApiResult<{DomainModel}> = withContext(dispatcher) {
        try {
            val response = steemService.{methodName}(
                {ApiName}ParamsDTO(
                    params = {buildParams}
                )
            )

            if (response.isSuccessful && response.body() != null) {
                val result = response.body()!!.result
                // Map DTO to Domain Model
                ApiResult.Success(result.toDomainModel())
            } else {
                ApiResult.Error("API call failed")
            }
        } catch (e: Exception) {
            ApiResult.Error(e.message ?: "Unknown error")
        }
    }
}
```

### 2. Domain Module Files

#### Domain Model Pattern
```kotlin
// File: dorian-steem-domain/src/main/java/lee/dorian/steem_domain/model/{DomainModel}.kt
package lee.dorian.steem_domain.model

/**
 * Domain model for {description}
 */
data class {DomainModel}(
    // Fields matching business requirements
    // (may differ from DTO structure)
)
```

#### Repository Interface Addition
```kotlin
// File: dorian-steem-domain/src/main/java/lee/dorian/steem_domain/repository/SteemRepository.kt
interface SteemRepository {
    // ... existing methods

    /**
     * {Description of what this method does}
     * @param {param1} {description}
     * @return ApiResult containing {DomainModel}
     */
    suspend fun {methodName}(
        {parameters}
    ): ApiResult<{DomainModel}>
}
```

#### UseCase Pattern
```kotlin
// File: dorian-steem-domain/src/main/java/lee/dorian/steem_domain/usecase/{UseCaseName}.kt
package lee.dorian.steem_domain.usecase

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import lee.dorian.steem_domain.model.{DomainModel}
import lee.dorian.steem_domain.model.ApiResult
import lee.dorian.steem_domain.repository.SteemRepository
import javax.inject.Inject

/**
 * Use case for {description}
 */
class {UseCaseName} @Inject constructor(
    private val steemRepository: SteemRepository,
    private val dispatcher: CoroutineDispatcher
) {

    /**
     * {Method description}
     * @param {param1} {description}
     * @return ApiResult containing {DomainModel}
     */
    suspend operator fun invoke(
        {parameters}
    ): ApiResult<{DomainModel}> = withContext(dispatcher) {
        steemRepository.{methodName}({parameterNames})
    }
}
```

## Usage Example

### Input Configuration (YAML or JSON)
```yaml
api_name: GetTrendingTags
api_method: condenser_api.get_trending_tags
description: Fetches trending tags from Steem blockchain

parameters:
  - name: tag
    type: String
    description: Starting tag name (empty string for all tags)
  - name: limit
    type: Int
    default: 100
    description: Maximum number of tags to return

response_type: Array<TrendingTag>

response_fields:
  - name: name
    type: String
    description: Tag name
  - name: comments
    type: Int
    description: Number of comments
  - name: top_posts
    type: Int
    description: Number of top posts
  - name: total_payouts
    type: String
    description: Total payout amount
```

### Generated Files Checklist

For API: `condenser_api.get_trending_tags`

#### Data Module (dorian-steem-data)
- [ ] `model/GetTrendingTagsParamsDTO.kt`
- [ ] `model/GetTrendingTagsResponseDTO.kt`
- [ ] `model/TrendingTagDTO.kt`
- [ ] Add method to `retrofit/SteemService.kt`
- [ ] Add implementation to `repository/SteemRepositoryImpl.kt`

#### Domain Module (dorian-steem-domain)
- [ ] `model/TrendingTag.kt`
- [ ] Add method to `repository/SteemRepository.kt`
- [ ] `usecase/ReadTrendingTagsUseCase.kt`

## Naming Conventions

### API Method Naming
```
API: condenser_api.get_accounts
↓
Method Name: getAccounts
DTO Prefix: GetAccounts
UseCase Name: ReadAccountDetailsUseCase
Domain Model: AccountDetails
```

### UseCase Naming Pattern
- `Read{Entity}UseCase` - For read operations (most common)
- `Create{Entity}UseCase` - For create operations
- `Update{Entity}UseCase` - For update operations
- `Delete{Entity}UseCase` - For delete operations

## Automation Scripts

### Option 1: Kotlin Script (KTS)
Create a Kotlin script that reads YAML config and generates files.

### Option 2: Python Script
Create a Python script with Jinja2 templates.

### Option 3: Gradle Task
Create a custom Gradle task for code generation.

### Option 4: Claude Code Interactive
Simply provide API details to Claude Code and let it generate all files interactively.

## API Documentation Reference

STEEM APPBASE API Documentation:
- Main: https://developers.steem.io/apidefinitions/
- Condenser API: https://developers.steem.io/apidefinitions/#apidefinitions-condenser-api
- Bridge API: https://developers.steem.io/apidefinitions/#apidefinitions-bridge-api

## Testing Checklist

After generating code for a new API:

1. [ ] Verify DTO classes match API schema
2. [ ] Test API call with Postman or curl
3. [ ] Verify DTO to Domain model mapping
4. [ ] Write unit tests for UseCase
5. [ ] Write unit tests for Repository
6. [ ] Test integration in ViewModel

---

**Last Updated**: 2026-02-17
**Maintained By**: Claude Code
