package lee.dorian.steem_ui.ui.compose

import androidx.compose.foundation.layout.RowScope
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import lee.dorian.steem_ui.topAppBarContainerColor

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomTopAppBar(
    title: String,
    actions: @Composable (RowScope.() -> Unit) = {}
) {
    TopAppBar(
        title = {
            TopAppBarTitle(title)
        },
        actions = actions,
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = topAppBarContainerColor,
            titleContentColor = Color.White
        )
    )
}

@Composable
private fun TopAppBarTitle(
    title: String
) {
    Text(
        text = title,
        fontWeight = FontWeight.Bold
    )
}