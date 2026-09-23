package lee.dorian.steem_data.model.post

// links / users 는 선언하지 않는다.
//
// 작성 앱이 선택적으로 채우는 필드라 글의 26% / 19% 에만 존재하고, 본문에 실제로 링크나
// 멘션이 있어도 78% / 56% 는 비어 있다. 게다가 steemitkorea/0.1 은 빈 배열 대신 빈
// 객체({})를 넣어서, List<String> 으로 선언하면 Gson 이 응답 전체의 파싱을 포기한다.
//
// 링크나 멘션이 필요하면 body 에서 직접 파싱하는 쪽이 정확하다. Gson 은 선언하지 않은
// 필드를 무시할 뿐이므로, 나중에 필요해지면 다시 선언하기만 하면 된다.
data class JSONMetadataDTO(
    val image: List<String>?,
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
