package lee.dorian.steem_domain.ext

import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

fun String.removeSubstring(substring: String): String = replace(substring, "")

// Precondition: The date format of this string is "yyyy-MM-dd'T'HH:mm:ss" or "yyyy-MM-dd HH:mm:ss"
fun String.fromUtcTimeToLocalTime(): String {
    try {
        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        val date = sdf.parse(this.replace("T", " ").trim())
        val longUtcTime = date.getTime()
        val offset: Int = TimeZone.getDefault().getOffset(longUtcTime)
        val longLocalTime = longUtcTime + offset
        val dateLocalTime = Date().apply {
            setTime(longLocalTime)
        }
        return sdf.format(dateLocalTime)
    }
    catch (e: ParseException) {
        e.printStackTrace()
        return ""
    }
}