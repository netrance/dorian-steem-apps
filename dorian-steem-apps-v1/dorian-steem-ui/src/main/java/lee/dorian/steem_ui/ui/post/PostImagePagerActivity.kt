package lee.dorian.steem_ui.ui.post

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.ImageLoader
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter
import lee.dorian.steem_ui.R
import lee.dorian.steem_ui.model.State

class PostImagePagerActivity : AppCompatActivity() {

    companion object {
        const val INTENT_BUNDLE_IMAGE_URL_LIST = "image_url_list"
    }

    val viewModel: PostImagePagerViewModel by lazy {
        ViewModelProvider(this).get(PostImagePagerViewModel::class.java)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(
            ComposeView(this).apply {
                setContent {
                    val imageURLs = intent.getSerializableExtra(INTENT_BUNDLE_IMAGE_URL_LIST) as ArrayList<String>
                    viewModel.updateImageURLs(imageURLs)

                    PostImagePagerScreen(viewModel)
                }
            }
        )
    }
}

@Composable
fun PostImagePagerScreen(viewModel: PostImagePagerViewModel) {
    val flowImageURLs by viewModel.flowImageURLs.collectAsStateWithLifecycle()

    if (flowImageURLs is State.Success) {
        val context = LocalContext.current
        val imageURLs = (flowImageURLs as State.Success).data
        val pageCount = imageURLs.size
        val pagerState = rememberPagerState { imageURLs.size }
        val imageLoader = remember { ImageLoader(context) }

        HorizontalPager(
            state = pagerState,
            pageSpacing = 16.dp
        ) { page ->
            AsyncImage(
                model = if (imageURLs.isEmpty()) "" else imageURLs[page],
                contentDescription = "Images of this post",
                error = painterResource(R.drawable.no_image_available),
                imageLoader = imageLoader,
                placeholder = rememberAsyncImagePainter(R.drawable.loading),
                modifier = Modifier.fillMaxWidth()
            )
        }


        LaunchedEffect(pagerState.currentPage, pageCount) {
            if (context is AppCompatActivity) {
                context.supportActionBar?.title = "Post Image (${pagerState.currentPage + 1} / ${pageCount})"
            }
        }
    }

}