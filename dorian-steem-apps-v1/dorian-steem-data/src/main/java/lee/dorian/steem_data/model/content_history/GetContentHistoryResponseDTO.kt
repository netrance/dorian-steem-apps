package lee.dorian.steem_data.model.content_history

import com.google.gson.Gson
import lee.dorian.steem_domain.model.ContentHistory
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class GetContentHistoryResponseDTO(
    val code: Int?,
    val error: String?,
    val result: ContentHistoryResultDTO?
)

data class ContentHistoryResultDTO(
    val cols: ContentHistoryColsDTO?,
    val rows: List<List<Any>>?
) {
    // The rows are the revisions of one post in ascending time order, the first one being the
    // post as it was published. content_history_api has no ordering parameter, so this order
    // is fixed, and the ascending order is what the carry-forward below depends on.
    fun toContentHistoryList(): List<ContentHistoryDTO> {
        val timeIdx = cols?.time ?: DEFAULT_TIME_INDEX
        val jsonMetadataIdx = cols?.json_metadata ?: DEFAULT_JSON_METADATA_INDEX
        val titleIdx = cols?.title ?: DEFAULT_TITLE_INDEX
        val bodyIdx = cols?.body ?: DEFAULT_BODY_INDEX

        // An edit carries only the fields it changed, and the unchanged ones come back as "".
        // An empty title therefore means "same as before", not "the title was cleared", so it
        // is filled in from the latest earlier revision that did carry one. Without this, a
        // revision would be shown with no title even though the post had one all along.
        var lastTitle = ""
        var lastJsonMetadata = ""

        return rows?.mapNotNull { row ->
            val time = (row.getOrNull(timeIdx) as? Double)?.toLong() ?: return@mapNotNull null
            val title = row.getOrNull(titleIdx) as? String ?: ""
            val jsonMetadata = row.getOrNull(jsonMetadataIdx) as? String ?: ""
            if (title.isNotEmpty()) {
                lastTitle = title
            }
            if (jsonMetadata.isNotEmpty()) {
                lastJsonMetadata = jsonMetadata
            }

            ContentHistoryDTO(
                time = time,
                title = lastTitle,
                // The blockchain stores an edited body as a patch, but SDS applies the patches
                // itself, so this is always the whole body of that revision.
                body = row.getOrNull(bodyIdx) as? String ?: "",
                jsonMetadata = lastJsonMetadata
            )
        } ?: listOf()
    }

    fun toContentHistories(): List<ContentHistory> {
        return toContentHistoryList().map { it.toContentHistory() }
    }

    companion object {
        const val DEFAULT_TIME_INDEX = 0
        const val DEFAULT_JSON_METADATA_INDEX = 1
        const val DEFAULT_TITLE_INDEX = 2
        const val DEFAULT_BODY_INDEX = 3
    }
}

data class ContentHistoryColsDTO(
    val time: Int?,
    val json_metadata: Int?,
    val title: Int?,
    val body: Int?
)

// One row of ContentHistoryResultDTO.rows, mapped to the order declared in "cols".
data class ContentHistoryDTO(
    val time: Long,             // Unix timestamp in seconds
    val title: String,
    val body: String,           // Markdown
    val jsonMetadata: String    // The raw JSON string of the revision, "" if it never had any
) {
    fun toContentHistory(): ContentHistory {
        val metadata = jsonMetadata.toContentHistoryMetadataDTO()

        return ContentHistory(
            time = time.toLocalTimeString(),
            title = title,
            body = body,
            tags = metadata?.getTags() ?: listOf(),
            app = metadata?.app ?: ""
        )
    }

    // json_metadata arrives as a raw JSON string, not as an object, so it is parsed here
    // rather than by the converter. It is written by the app that published the revision and
    // is not validated by anything, so a parse failure has to be survivable: it only costs
    // the tags and the app name of that one revision.
    private fun String.toContentHistoryMetadataDTO(): ContentHistoryMetadataDTO? {
        return try {
            Gson().fromJson(this, ContentHistoryMetadataDTO::class.java)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private fun Long.toLocalTimeString(): String {
        val sdf = SimpleDateFormat(TIME_FORMAT, Locale.US)
        return sdf.format(Date(this * 1000L))
    }

    companion object {
        const val TIME_FORMAT = "yyyy-MM-dd HH:mm"
    }
}

// The json_metadata of one revision. It is deliberately *not* JSONMetadataDTO, which models
// the metadata the official API returns.
//
// SDS rewrites json_metadata before storing it and adds "image":{} — an empty object, not an
// empty array — to a revision that has no images. JSONMetadataDTO declares image as
// List<String>?, so Gson gives up on the whole object and the tags are lost with it. This is
// the same trap the official API set with links / users. (see JSONMetadataDTO)
//
// Only the two fields the screen needs are declared, so there is nothing left to trip over.
// Gson ignores the rest, and they can be declared here if they are ever needed.
data class ContentHistoryMetadataDTO(
    val tags: Any?,      // String or List<String>, depending on the app
    val app: String?
) {

    fun getTags(): List<String> {
        return when (tags) {
            is String -> listOf(tags)
            is List<*> -> tags.filterIsInstance<String>()
            else -> listOf()
        }
    }

}

// Example of a successful response. The post was published in 2018 and edited once in 2025,
// and the second revision replaced both the title and the body with ".".
// {
//     "code": 0,
//     "result": {
//         "cols": {
//             "time": 0,
//             "json_metadata": 1,
//             "title": 2,
//             "body": 3
//         },
//         "rows": [
//             [
//                 1524481023,
//                 "{\"tags\":[\"kr\"],\"app\":\"steemit/0.1\",\"format\":\"markdown\"}",
//                 "장례 잘마무리 되었다고 합니다",
//                 "잘마무리 되었다고 합니다... \n저도 급하게 전달받아 이곳에 급하게 전달한다는게...."
//             ],
//             [
//                 1759138161,
//                 "{\"format\":\"markdown\",\"app\":\"steemit/0.2\"}",
//                 ".",
//                 "."
//             ]
//         ]
//     }
// }
//
// Example of a successful response whose edits left the title and the metadata untouched.
// (chainsmokerslav/python-04-39) The live post still carries the title of the first revision,
// which is why an empty title is read as "unchanged" rather than as an empty title.
// {
//     "code": 0,
//     "result": {
//         "cols": { "time": 0, "json_metadata": 1, "title": 2, "body": 3 },
//         "rows": [
//             [1670284428, "{\"tags\":[...],\"app\":\"steemit/0.2\"}", "[하나하나Python] 04. ...", "..."],
//             [1670399253, "", "", "..."],
//             [1670816691, "{\"tags\":[...],\"app\":\"steemit/0.2\"}", "", "..."]
//         ]
//     }
// }
//
// Example of the json_metadata SDS writes for a revision with no images. (jeehun/6dz5d3)
// The official API returns no "image" key at all for this post; SDS adds it as an empty
// object. It is the reason ContentHistoryMetadataDTO does not declare "image".
// "{\"app\":\"steemitkorea/0.1\",\"category\":\"restaurant\",\"format\":\"markdown\",
//   \"lang\":\"ko\",\"tags\":[\"zzan\",\"dblog\",\"food\",\"life\",\"kr\",\"krsuccess\"],
//   \"image\":{}}"
//
// Example of a failed response. Both a non-existent author and a non-existent permlink are
// reported the same way, so the two cannot be told apart from the response.
// {
//     "code": -1,
//     "error": "Link could not be found: dorian-mobileapp/invalid987655321"
// }
