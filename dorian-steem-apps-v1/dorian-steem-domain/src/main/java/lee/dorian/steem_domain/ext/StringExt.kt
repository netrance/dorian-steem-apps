package lee.dorian.steem_domain.ext

fun String.removeSubstring(substring: String): String = replace(substring, "")