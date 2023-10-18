package lee.dorian.dorian_android_ktx.android.net

import android.net.Uri
import lee.dorian.steem_domain.ext.endsWithOneOf
import lee.dorian.steem_domain.ext.startsWithOneOf

private const val youtubeSiteTypeA = "https://youtube.com"
private const val youtubeSiteTypeB = "https://m.youtube.com"
private const val youtubeSiteTypeC = "https://www.youtube.com"

private const val youtubeSiteEmbedTypeA = "https://youtube.com/embed/"
private const val youtubeSiteEmbedTypeB = "https://m.youtube.com/embed/"
private const val youtubeSiteEmbedTypeC = "https://www.youtube.com/embed/"

private const val youtubeSiteWatchTypeA = "https://youtube.com/watch"
private const val youtubeSiteWatchTypeB = "https://m.youtube.com/watch"
private const val youtubeSiteWatchTypeC = "https://www.youtube.com/watch"

fun Uri.isYoutubeSite(): Boolean {
    val address = this.toString()
    return when {
        address.startsWithOneOf(youtubeSiteTypeA, youtubeSiteTypeB, youtubeSiteTypeC) -> true
        else -> false
    }
}

fun Uri.getYoutubeVideoId(): String {
    if (!isYoutubeSite()) {
        return ""
    }

    val address = this.toString()
    return when {
        address.startsWithOneOf(youtubeSiteEmbedTypeA, youtubeSiteEmbedTypeB, youtubeSiteEmbedTypeC) -> this.lastPathSegment ?: ""
        address.startsWithOneOf(youtubeSiteWatchTypeA, youtubeSiteWatchTypeB, youtubeSiteWatchTypeC) -> this.getQueryParameter("v") ?: ""
        else -> ""
    }
}

fun Uri.getStartTimeOfYoutubeVideo(): String {
    if (!isYoutubeSite()) {
        return ""
    }

    val addresss = this.toString()
    return when {
        addresss.startsWithOneOf(youtubeSiteEmbedTypeA, youtubeSiteEmbedTypeB, youtubeSiteEmbedTypeC) -> this.getQueryParameter("start") ?: "0"
        addresss.startsWithOneOf(youtubeSiteWatchTypeA, youtubeSiteWatchTypeB, youtubeSiteWatchTypeC) -> this.getQueryParameter("t")?.replace("s", "") ?: "0"
        else -> ""
    }
}

// Common image formats: https://www.w3schools.com/html/html_images.asp
// More consideration(s): Allow HTTP URLs or not?
fun Uri.isContentImage(): Boolean {
    val address = this.toString()
    return when {
        (scheme != "https") -> false
        address.endsWithOneOf(
            ".apng", ".gif", ".ico", ".cur", ".jpg", ".jpeg",
            ".jfif", ".pjpeg", ".pjp", ".png", ".svg"
        ) -> true
        else -> false
    }
}