package lee.dorian.steem_ui.ui.compose

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun AccountSearchTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String = "Input Steemit account.",
    modifier: Modifier = Modifier
) {
    TextField(
        value = value,
        onValueChange = onValueChange,
        placeholder = { Text(placeholder) },
        trailingIcon = {
            Icon(Icons.Default.Search, contentDescription = "Search")
        },
        singleLine = true,
        colors = TextFieldDefaults.colors(
            unfocusedContainerColor = Color.Transparent,
            focusedContainerColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Gray,
            focusedIndicatorColor = Color.Black
        ),
        modifier = modifier.fillMaxWidth()
    )
}

@Composable
@Preview
fun AccountSearchTextFieldPreview() {
    var value by remember { mutableStateOf("") }
    AccountSearchTextField(
        value = value,
        onValueChange = { value = it }
    )
}
