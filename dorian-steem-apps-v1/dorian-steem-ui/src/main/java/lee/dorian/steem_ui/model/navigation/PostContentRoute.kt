package lee.dorian.steem_ui.model.navigation

import kotlinx.serialization.Serializable

@Serializable
data class PostContentRoute(
    val author: String,
    val permlink: String
)