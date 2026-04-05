package lee.dorian.steem_ui.ui.profile

import androidx.compose.ui.graphics.Color
import lee.dorian.steem_ui.ui.post.PostSortType

data class ProfileMenuItem(
    val id: ProfileMenuItemID,
    val name: String,
    val textColor: Color,
    val fontSize: Int,
    val backgroundColor: Color,
    //val onClick: () -> Unit
)