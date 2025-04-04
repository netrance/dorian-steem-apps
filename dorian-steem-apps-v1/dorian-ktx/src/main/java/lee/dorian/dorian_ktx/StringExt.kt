package lee.dorian.steem_domain.ext

import com.google.gson.JsonObject
import com.google.gson.JsonParser
import org.commonmark.ext.gfm.tables.TablesExtension
import org.commonmark.node.Node
import org.commonmark.parser.Parser
import org.commonmark.renderer.html.HtmlRenderer
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import java.io.UnsupportedEncodingException
import java.lang.Math.abs
import java.net.URLEncoder
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.log10
import kotlin.math.round

fun String.removeSubstring(substring: String): String = replace(substring, "")

fun String.startsWithOneOf(vararg prefixes: String): Boolean {
    prefixes.forEach {
        if (startsWith(it)) {
            return true
        }
    }

    return false
}

fun String.endsWithOneOf(vararg prefixes: String): Boolean {
    prefixes.forEach {
        if (endsWith(it)) {
            return true
        }
    }

    return false
}

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

fun String.applyURLEncoding(enc: String = "UTF-8"): String {
    return try {
        URLEncoder.encode(this, enc).replace("+", "%20")
    }
    catch (e: UnsupportedEncodingException) {
        e.printStackTrace()
        ""
    }
}

fun String.convertMarkdownToHtml(): String {
    val extensions = listOf(TablesExtension.create())
    val parser = Parser.builder().extensions(extensions).build()
    val rootNode: Node = parser.parse(this)
    val renderer = HtmlRenderer.builder().extensions(extensions).build()
    return renderer.render(rootNode)
}

fun String.convertMarkdownToHtmlDocument(): Document {
    val document = Jsoup.parse(convertMarkdownToHtml()).apply {
        // to prevent changing newlines to whitespaces
        // Refer to https://stackoverflow.com/questions/40396524/avoid-removal-of-spaces-and-newline-while-parsing-html-using-jsoup
        outputSettings(Document.OutputSettings().prettyPrint(false))

        select("a").forEach {
            val hrefValue = it.attr("href")
            it.attr("href", hrefValue.applyURLEncoding())
        }
    }

    return document
}

fun String.convertToUserReadableReputation(): String {
    val rawReputation = this.toLongOrNull() ?: return ""
    val absoluteReputation = abs(rawReputation)
    val logReputation = log10(absoluteReputation.toDouble())
    var readableReputation = (logReputation - 9) * 9 + 25
    if (rawReputation < 0) {
        readableReputation = 50 - readableReputation
    }

    return String.format("%.2f", round(readableReputation * 100) / 100)
}

fun String.toJsonObject(defaultValue: JsonObject = JsonObject()): JsonObject {
    val jsonParser = JsonParser()
    val jsonObject = jsonParser.parse(this)

    return when {
        (jsonObject.isJsonNull) -> defaultValue
        else -> jsonObject as JsonObject
    }
}