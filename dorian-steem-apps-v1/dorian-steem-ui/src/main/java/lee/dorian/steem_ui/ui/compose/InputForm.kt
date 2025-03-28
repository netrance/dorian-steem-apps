package lee.dorian.steem_ui.ui.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun AccountInputForm(placeholder: String, onSearchClick: (account: String) -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(5.dp)
    ) {
        val accountText = rememberSaveable { mutableStateOf("") }

        Text(
            text = "@",
            style = TextStyle(color = Color.Black, fontSize = 16.sp)
        )
        Spacer(modifier = Modifier.width(5.dp))
        CustomTextField(
            accountText,
            TextStyle(color = Color.Black, fontSize = 15.sp),
            placeholder,
            Modifier
                .weight(1f)
                .background(
                    color = Color.LightGray,
                    shape = RoundedCornerShape(10.dp)
                )
                .padding(10.dp)
        )
        Icon(
            imageVector = Icons.Filled.Search,
            contentDescription = "Search button",
            modifier = Modifier
                .clickable { onSearchClick(accountText.value) }
                .padding(start = 8.dp)
        )
    }
}

@Composable
@Preview
fun AccountInputFormPreview() {
    AccountInputForm("Input an account.") {}
}
