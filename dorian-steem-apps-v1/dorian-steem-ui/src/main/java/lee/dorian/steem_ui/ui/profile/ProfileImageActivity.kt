package lee.dorian.steem_ui.ui.profile

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter
import lee.dorian.steem_ui.R
import lee.dorian.steem_ui.ui.compose.CustomTopAppBar

class ProfileImageActivity : ComponentActivity() {

    companion object {
        const val INTENT_BUNDLE_STEEMIT_ACCOUNT = "profile_image"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val account = intent.getStringExtra(INTENT_BUNDLE_STEEMIT_ACCOUNT) ?: ""

        setContent {
            ProfileImageScreen(account)
        }
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileImageScreen(account: String) {
    val imageURL = when (account.isEmpty()) {
        true -> ""
        else -> "https://steemitimages.com/u/${account}/avatar/large"
    }

    Scaffold(
        topBar = { CustomTopAppBar(title = "@${account}") }
    ) { innerPadding ->
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(Color.Black)
        ) {
            if (imageURL.isEmpty()) {
                Image(
                    painter = painterResource(R.drawable.no_image_available),
                    contentDescription = "Image for preview"
                )
                return@Box
            }

            AsyncImage(
                model = imageURL,
                contentDescription = "Thumbnail of the first image of this post",
                contentScale = ContentScale.Inside,
                error = painterResource(R.drawable.no_image_available),
                placeholder = rememberAsyncImagePainter(R.drawable.loading),
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
@Preview(showBackground = true)
fun ProfileImageScreenPreview() {
    ProfileImageScreen("")
}
