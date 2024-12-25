package lee.dorian.dorian_android_ktx.androidx.compose.ui

import androidx.compose.runtime.Stable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp

@Stable
fun Modifier.borderBottom(
    width: Dp,
    color: Color
): Modifier = this.then(
    drawBehind {
        drawLine(
            color = color,
            start = Offset(0f, size.height),
            end = Offset(size.width, size.height),
            strokeWidth = width.toPx()
        )
    }
)