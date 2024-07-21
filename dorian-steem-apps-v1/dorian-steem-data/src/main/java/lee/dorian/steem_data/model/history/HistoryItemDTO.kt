package lee.dorian.steem_data.model.history

import com.google.gson.JsonObject
import com.google.gson.internal.LinkedTreeMap
import lee.dorian.steem_domain.model.HistoryItem
import lee.dorian.steem_domain.util.Converter

class HistoryItemDTO : MutableList<Any> by mutableListOf() {

    fun getIndex(): Int {
        return try {
            when {
                this[0] is Double -> (this[0] as Double).toInt()
                else -> -1
            }
        }
        catch (e: java.lang.IndexOutOfBoundsException) {
            e.printStackTrace()
            -1
        }
    }

    fun getHistoryItemContent(): HistoryItemContentDTO? {
        return try {
            when {
                this[1] !is Map<*, *> -> null
                else -> {
                    HistoryItemContentDTO.create(this[1] as Map<String, Any>)
                }
            }
        }
        catch (e: java.lang.IndexOutOfBoundsException) {
            e.printStackTrace()
            null
        }
    }

    fun toHistoryItem(): HistoryItem {
        val historyItemContent = getHistoryItemContent()
        val historyItemContent2 = this[1]
        val index = getIndex()
        val utcTime = Converter.toLocalTimeFromUTCTime(historyItemContent?.timestamp ?: "", "yyyy-MM-dd HH:mm")
        val histoyItemContent = historyItemContent?.op?.getOpContent() ?: LinkedTreeMap()

        return HistoryItem(
            index,
            utcTime,
            histoyItemContent
        )
    }

}