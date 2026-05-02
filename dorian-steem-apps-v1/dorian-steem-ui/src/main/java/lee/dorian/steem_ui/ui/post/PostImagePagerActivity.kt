package lee.dorian.steem_ui.ui.post

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.ImageLoader
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter
import lee.dorian.steem_ui.R
import lee.dorian.steem_ui.model.State
import lee.dorian.steem_ui.ui.compose.CustomTopAppBar

class PostImagePagerActivity : ComponentActivity() {

    companion object {
        const val INTENT_BUNDLE_IMAGE_URL_LIST = "image_url_list"
    }

    private val viewModel: PostImagePagerViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val imageURLs = intent.getSerializableExtra(INTENT_BUNDLE_IMAGE_URL_LIST) as ArrayList<String>
        viewModel.updateImageURLs(imageURLs)

        setContent {
            PostImagePagerScreen(viewModel)
        }
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PostImagePagerScreen(viewModel: PostImagePagerViewModel) {
    val flowImageURLs by viewModel.flowImageURLs.collectAsStateWithLifecycle()
    var title by remember { mutableStateOf("Post Image") }

    Scaffold(
        topBar = { CustomTopAppBar(title = title) }
    ) { innerPadding ->
        if (flowImageURLs is State.Success) {
            val context = LocalContext.current
            val imageURLs = (flowImageURLs as State.Success).data
            val pageCount = imageURLs.size
            val pagerState = rememberPagerState { pageCount }
            val imageLoader = remember { ImageLoader(context) }

            LaunchedEffect(pagerState.currentPage, pageCount) {
                title = "Post Image (${pagerState.currentPage + 1} / $pageCount)"
            }

            HorizontalPager(
                state = pagerState,
                pageSpacing = 16.dp,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .background(Color.Black)
            ) { page ->
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.fillMaxSize()
                ) {
                    AsyncImage(
                        model = if (imageURLs.isEmpty()) "" else imageURLs[page],
                        contentDescription = "Images of this post",
                        error = painterResource(R.drawable.no_image_available),
                        imageLoader = imageLoader,
                        placeholder = rememberAsyncImagePainter(R.drawable.loading),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}
