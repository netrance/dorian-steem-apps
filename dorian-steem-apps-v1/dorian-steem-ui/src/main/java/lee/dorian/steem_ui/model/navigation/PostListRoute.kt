package lee.dorian.steem_ui.model.navigation

import kotlinx.serialization.Serializable

@Serializable
data class PostListRoute(
    val account: String,
    val sort: String
)