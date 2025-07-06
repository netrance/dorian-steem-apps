package lee.dorian.steem_ui.ui.profile

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.ViewModelProvider
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter
import lee.dorian.steem_ui.R

class ProfileImageActivity : AppCompatActivity() {

    companion object {
        const val INTENT_BUNDLE_STEEMIT_ACCOUNT = "profile_image"
    }

    val viewModel: ProfileImageViewModel by lazy {
        ViewModelProvider(this).get(ProfileImageViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(
            ComposeView(this).apply {
                setContent {
                    val account = intent.getStringExtra(INTENT_BUNDLE_STEEMIT_ACCOUNT) ?: ""
                    viewModel.steemitAccount.value = account
                    supportActionBar?.title = "@${account}"
                    ProfileImageScreen(account)
                }
            }
        )
    }

}

@Composable
fun ProfileImageScreen(account: String) {
    val imageURL = when (account.isEmpty()) {
        true -> ""
        else -> "https://steemitimages.com/u/${account}/avatar/large"
    }

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.fillMaxSize().background(Color.Black)
    ) {
        if (imageURL.isEmpty()) {
            Image(
                painter = painterResource(R.drawable.no_image_available),
                contentDescription = "Image for preview"
            )
            return
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

@Composable
@Preview(showBackground = true)
fun ProfileImageScreenPreview() {
    ProfileImageScreen("")
}