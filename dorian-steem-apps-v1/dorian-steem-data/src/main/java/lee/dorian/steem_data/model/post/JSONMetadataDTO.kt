package lee.dorian.steem_data.model.post

data class JSONMetadataDTO(
    val links: List<String>?,
    val image: List<String>?,
    val users: List<String>?,
    val tags: List<String>?,
    val app: String?,
    val format: String?
) {

    fun getThumbnailURL(): String {
        return when {
            (image?.size == 0) -> ""
            else -> image?.get(0) ?: ""
        }
    }

}
