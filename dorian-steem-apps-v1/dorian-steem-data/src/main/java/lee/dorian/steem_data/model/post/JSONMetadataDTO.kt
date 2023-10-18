package lee.dorian.steem_data.model.post

data class JSONMetadataDTO(
    val links: List<String>?,
    val image: List<String>?,
    val users: List<String>?,
    val tags: Any?,    // String or List<String>
    val app: String?,
    val format: String?,
    val canonical_url: String?
) {

    fun getThumbnailURL(): String {
        return when {
            (image?.size == 0) -> ""
            else -> image?.get(0) ?: ""
        }
    }

    fun getTags(): List<String> {
        return when (tags) {
            is String -> listOf(tags)
            is List<*> -> tags.filter { it is String }
            else -> listOf()
        } as List<String>
    }

}
