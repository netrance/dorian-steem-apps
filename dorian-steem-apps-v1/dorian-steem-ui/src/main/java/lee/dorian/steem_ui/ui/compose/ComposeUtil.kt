package lee.dorian.steem_ui.ui.compose

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import coil.compose.rememberAsyncImagePainter
import lee.dorian.steem_ui.R

@Composable
fun Loading(@DrawableRes id: Int = R.drawable.loading) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
        .fillMaxSize()
        .background(Color.White)
    ) {
        Image(
            painter = when (LocalInspectionMode.current) {
                true -> painterResource(id)
                else -> rememberAsyncImagePainter(id)
            },
            contentDescription = "Loading icon"
        )
    }
}

@Composable
@Preview
fun PreviewLoading() {
    Loading()
}

@Composable
fun ErrorOrFailure(@DrawableRes id: Int = R.drawable.img_loading_error) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
        .fillMaxSize()
        .background(Color.White)
    ) {
        Image(
            painter = painterResource(id),
            contentDescription = "Loading icon"
        )
    }
}

@Composable
@Preview
fun PreviewErrorOrFailure() {
    ErrorOrFailure()
}