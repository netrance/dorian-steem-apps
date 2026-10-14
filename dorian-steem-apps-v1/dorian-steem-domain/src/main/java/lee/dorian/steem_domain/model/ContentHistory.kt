package lee.dorian.steem_domain.model

// One revision of a post: the whole post as it stood at that time, not just what the edit
// changed. The fields an edit left untouched are carried over from the previous revision.
data class ContentHistory(
    val time: String = "",             // Local time, formatted as "yyyy-MM-dd HH:mm"
    val title: String = "",
    val body: String = "",             // Markdown, rendered the same way a live post body is
    val tags: List<String> = listOf(),
    val app: String = ""               // The app the revision was written with, e.g. "steemit/0.2"
)
