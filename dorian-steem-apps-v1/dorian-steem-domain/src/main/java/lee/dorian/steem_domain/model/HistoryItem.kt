package lee.dorian.steem_domain.model

import com.google.gson.JsonObject

data class HistoryItem(
    val index: Int,
    val timestamp: String,
    val content: Map<String, Any>
)
