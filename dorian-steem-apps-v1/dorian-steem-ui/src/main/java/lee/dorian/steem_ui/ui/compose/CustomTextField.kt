package lee.dorian.steem_ui.ui.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun CustomTextField(
    textState: MutableState<String>,
    textStyle: TextStyle,
    placeholder: String,
    modifier: Modifier
) {
    BasicTextField(
        value = textState.value,
        onValueChange = { textState.value = it },
        modifier = modifier,
        singleLine = true,
        textStyle = textStyle
    ) { innerTextField ->
        innerTextField()
        if (textState.value.isEmpty()) {
            Text(text = placeholder, style = textStyle.copy(color = Color.DarkGray))
        }
    }
}

@Composable
@Preview
fun CustomTextFieldPreview1() {
    var textState = remember { mutableStateOf("") }
    CustomTextField(
        textState,
        TextStyle(),
        "Input ID",
        Modifier
            .fillMaxWidth()
            .background(
                color = Color.LightGray,
                shape = RoundedCornerShape(5.dp)
            )
            .padding(8.dp)
    )
}

@Composable
@Preview
fun CustomTextFieldPreview2() {
    var textState = remember { mutableStateOf("test") }
    CustomTextField(
        textState,
        TextStyle(),
        "Input ID",
        Modifier
            .fillMaxWidth()
            .background(
                color = Color.LightGray,
                shape = RoundedCornerShape(5.dp)
            )
            .padding(8.dp)
    )
}