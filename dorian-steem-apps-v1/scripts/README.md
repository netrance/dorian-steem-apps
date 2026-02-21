# STEEM API Integration Automation

This directory contains documentation and tools for automating STEEM APPBASE API integration into the project.

## 📚 Documentation Files

| File | Purpose |
|------|---------|
| `generate-api.md` | Complete guide and templates for API code generation |
| `EXAMPLE_API_GENERATION.md` | Full working example of generated code |
| This `README.md` | Quick start guide |

---

## 🚀 Quick Start

### Method 1: Claude Code Interactive (Recommended) ⭐

Simply provide API details to Claude Code:

```
"Add the condenser_api.get_trending_tags API.

Spec:
- Method: condenser_api.get_trending_tags
- Parameters:
  - tag: String (starting tag, empty for all)
  - limit: Int (default 100)
- Response: Array of:
  - name: String
  - comments: Int
  - top_posts: Int
  - total_payouts: String

Reference: https://developers.steem.io/apidefinitions/#condenser_api.get_trending_tags"
```

Claude Code will automatically generate all 8 files across the architecture layers.

### Method 2: Manual Generation (Using Templates)

1. Read `generate-api.md` for templates
2. Replace placeholders with your API details
3. Create files manually

### Method 3: Script-based Generation (Future)

Coming soon: Kotlin/Python script for automated generation from YAML config.

---

## 📋 What Gets Generated

For each API, 8 files are created/modified:

### Data Module (dorian-steem-data)
✅ 3 new files + 2 modifications
1. `model/{ApiName}ParamsDTO.kt` - Request parameters
2. `model/{ApiName}ResponseDTO.kt` - Response wrapper
3. `model/{ResultName}DTO.kt` - Response data structure
4. `retrofit/SteemService.kt` - Add API method (modified)
5. `repository/SteemRepositoryImpl.kt` - Add implementation (modified)

### Domain Module (dorian-steem-domain)
✅ 2 new files + 1 modification
6. `model/{DomainModel}.kt` - Domain model
7. `repository/SteemRepository.kt` - Add interface method (modified)
8. `usecase/Read{Entity}UseCase.kt` - Use case

---

## 🎯 API Information Template

When requesting API integration, provide:

```yaml
API Name: [CamelCase name, e.g., GetTrendingTags]
API Method: [Full method path, e.g., condenser_api.get_trending_tags]
Description: [What this API does]

Parameters:
  - name: [parameter name]
    type: [Kotlin type]
    default: [default value, if any]
    description: [what this parameter does]

Response Type: [e.g., Array, Object, List]
Response Fields:
  - name: [field name]
    type: [Kotlin type]
    json_name: [JSON field name if different]
    description: [field description]

Reference URL: [Link to API documentation]
```

---

## 🔍 API Documentation Sources

### Official Documentation
- **Main**: https://developers.steem.io/apidefinitions/
- **Condenser API**: https://developers.steem.io/apidefinitions/#apidefinitions-condenser-api
- **Bridge API**: https://developers.steem.io/apidefinitions/#apidefinitions-bridge-api

### Useful References
- **JSON-RPC Format**: All APIs use JSON-RPC 2.0 protocol
- **Testing**: Use https://api.steemit.com for testing
- **Examples**: Check existing code in the project

---

## 📐 Architecture Pattern

The generated code follows Clean Architecture:

```
Presentation (UI) → Domain (Use Cases) → Data (Repository)
                         ↓                      ↓
                    Domain Models          DTO Models
```

### Naming Conventions

| Component | Pattern | Example |
|-----------|---------|---------|
| API Method | camelCase | `getTrendingTags` |
| Params DTO | `{ApiName}ParamsDTO` | `GetTrendingTagsParamsDTO` |
| Response DTO | `{ApiName}ResponseDTO` | `GetTrendingTagsResponseDTO` |
| Result DTO | `{Entity}DTO` | `TrendingTagDTO` |
| Domain Model | PascalCase noun | `TrendingTag` |
| Use Case | `Read{Entity}UseCase` | `ReadTrendingTagsUseCase` |
| Repository Method | `read{Entity}` | `readTrendingTags` |

---

## ✅ Post-Generation Checklist

After generating code:

### Build & Compile
- [ ] Project builds successfully (`./gradlew build`)
- [ ] No compilation errors
- [ ] All imports resolved

### Code Quality
- [ ] All classes have KDoc comments
- [ ] Naming follows conventions
- [ ] Code formatted properly

### Functionality
- [ ] Test API call with actual data
- [ ] Verify DTO deserialization
- [ ] Verify DTO → Domain mapping
- [ ] Check error handling

### Integration (Optional)
- [ ] Create ViewModel (if needed)
- [ ] Create UI screen (if needed)
- [ ] Add navigation (if needed)

---

## 🛠️ Troubleshooting

### Common Issues

**Issue**: Import errors for generated files
- **Solution**: Sync Gradle project in Android Studio

**Issue**: API call returns error
- **Solution**: Check API method name and parameters match documentation

**Issue**: Deserialization fails
- **Solution**: Verify DTO field names match JSON response (use @SerializedName if needed)

**Issue**: Hilt injection fails
- **Solution**: Ensure UseCase has @Inject constructor

---

## 📈 Future Enhancements

Planned improvements:

1. **CLI Tool**: Command-line tool for code generation
2. **IntelliJ Plugin**: IDE plugin for in-editor generation
3. **Gradle Task**: Custom Gradle task for generation
4. **OpenAPI Support**: Generate from OpenAPI/Swagger specs
5. **Test Generation**: Auto-generate unit tests
6. **Mock Generation**: Auto-generate mock data for testing

---

## 🤝 Contributing

To improve the automation:

1. Update templates in `generate-api.md`
2. Add more examples to `EXAMPLE_API_GENERATION.md`
3. Document edge cases and solutions
4. Share reusable patterns

---

## 📞 Support

For questions or issues:
- Check existing examples in the codebase
- Refer to STEEM API documentation
- Ask Claude Code for assistance

---

**Last Updated**: 2026-02-17
**Maintained By**: Claude Code
