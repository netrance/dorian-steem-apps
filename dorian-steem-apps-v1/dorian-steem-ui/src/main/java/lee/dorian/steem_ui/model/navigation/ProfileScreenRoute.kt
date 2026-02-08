package lee.dorian.steem_ui.model.navigation

import kotlinx.serialization.Serializable

@Serializable
data class ProfileScreenRoute(
    val account: String
)