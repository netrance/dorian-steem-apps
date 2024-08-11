package lee.dorian.dorian_ktx

inline fun <reified T> Map<String, Any>.read(key: String, defaultValue: T): T {
    val value = this.getOrDefault(key, defaultValue)

    return when {
        (value is T) -> value
        else -> defaultValue
    }
}