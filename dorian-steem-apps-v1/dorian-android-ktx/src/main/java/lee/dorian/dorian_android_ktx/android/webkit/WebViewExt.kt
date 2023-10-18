package lee.dorian.steem_ui.ext

import android.net.Uri
import android.text.Html
import android.webkit.WebSettings
import android.webkit.WebView
import lee.dorian.dorian_android_ktx.android.net.getStartTimeOfYoutubeVideo
import lee.dorian.dorian_android_ktx.android.net.getYoutubeVideoId
import lee.dorian.dorian_android_ktx.android.net.isContentImage
import lee.dorian.dorian_android_ktx.android.net.isYoutubeSite
import lee.dorian.steem_domain.ext.applyURLEncoding
import lee.dorian.steem_domain.ext.convertMarkdownToHtml
import lee.dorian.steem_domain.ext.convertMarkdownToHtmlDocument
import org.commonmark.ext.gfm.tables.TablesExtension
import org.commonmark.node.Node
import org.commonmark.parser.Parser
import org.commonmark.renderer.html.HtmlRenderer
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element

fun WebView.loadMarkdown(markdown: String) {
    val document = markdown.convertMarkdownToHtmlDocument()

    for (element in document.allElements) {
        if (element.childrenSize() > 0) {
            continue
        }

        val innerHTML = element.html()
        val innerHTMLLines = innerHTML.lines()
        val newHtmlBuilder = java.lang.StringBuilder()

        innerHTMLLines.forEachIndexed { lineIndex, line ->
            val words = line.split(" ")

            words.forEachIndexed { wordIndex, word ->
                val uriFromWord = Uri.parse(word)
                when {
                    (uriFromWord.isYoutubeSite()) -> {
                        val unescapedAddrdss = Html.fromHtml(word, Html.FROM_HTML_MODE_LEGACY).toString()
                        val unescapedUri = Uri.parse(unescapedAddrdss)
                        val videoId = unescapedUri.getYoutubeVideoId()
                        val start = unescapedUri.getStartTimeOfYoutubeVideo()
                        val newElement = Element("iframe").apply {
                            attr("frameborder", "0")
                            attr("allowfullscreen", "")
                            attr("src", "https://www.youtube.com/embed/${videoId}?start=${start}")
                        }
                        newHtmlBuilder.append(newElement.toString())
                    }
                    (uriFromWord.isContentImage()) -> {
                        val newImgElement = Element("img").attr("src", word.applyURLEncoding())
                        newHtmlBuilder.append(newImgElement.toString())
                    }
                    else -> {
                        newHtmlBuilder.append("${word.applyURLEncoding()}")
                        if (wordIndex < words.size - 1) {
                            newHtmlBuilder.append(" ")
                        }
                    }
                }
            }

            if (lineIndex < innerHTMLLines.size - 1) {
                newHtmlBuilder.append("<br/>")
            }
        }
        element.html(newHtmlBuilder.toString())
    }

    settings.javaScriptEnabled = true
    settings.layoutAlgorithm = WebSettings.LayoutAlgorithm.TEXT_AUTOSIZING

    val style = """
        <style>
            iframe { display: block; max-width: 100%; height: auto; }
            img { display: block; max-width: 100%; height: auto; }
            th { background-color: rgb(188, 188, 188) }
            td { background-color: rgb(233, 233, 233) }
            blockquote { border-left: 4px solid rgb(194, 194, 194); padding-left: 10px; margin-left: 0px; }
        </style>
    """.trimIndent()
    loadData("${style}${document.toString()}", "text/html; charset=utf-8", "UTF-8")
}