package lee.dorian.steem_ui.ui.post.list

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter
import lee.dorian.dorian_android_ktx.androidx.compose.foundation.lazy.AppendableLazyColumn
import lee.dorian.dorian_android_ktx.androidx.compose.ui.borderBottom
import lee.dorian.steem_domain.model.ActiveVote
import lee.dorian.steem_domain.model.PostListInfo
import lee.dorian.steem_domain.model.PostItem
import lee.dorian.steem_ui.MainViewModel
import lee.dorian.steem_ui.R
import lee.dorian.steem_ui.databinding.FragmentBlogBinding
import lee.dorian.steem_ui.ext.getCurrentFragment
import lee.dorian.steem_ui.ext.showToastShortly
import lee.dorian.steem_ui.model.State
import lee.dorian.steem_ui.ui.base.BaseFragment
import lee.dorian.steem_ui.ui.compose.ErrorOrFailure
import lee.dorian.steem_ui.ui.compose.Loading
import lee.dorian.steem_ui.ui.post.PostImagePagerActivity

class PostListFragment : BaseFragment<FragmentBlogBinding, PostListViewModel>(R.layout.fragment_blog) {

    private val args: PostListFragmentArgs by navArgs()

    override val viewModel by lazy {
        ViewModelProvider(this).get(PostListViewModel::class.java)
    }

    val activityViewModel by lazy {
        ViewModelProvider(requireActivity()).get(MainViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return ComposeView(requireContext()).apply {
            setContent {
                PostListScreen(
                    viewModel,
                    args.author,
                    args.sort
                )
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PostListScreen(
    viewModel: PostListViewModel,
    account: String,
    sort: String
) {
    val state by viewModel.flowState.collectAsStateWithLifecycle()
    val isRefreshing by viewModel.flowIsRefreshing.collectAsStateWithLifecycle()

    if (state is State.Empty) {
        viewModel.readPosts(account, sort)
        return
    }
    else if (state is State.Loading) {
        Loading()
        return
    }
    else if (state !is State.Success<PostListInfo>) {
        ErrorOrFailure()
        return
    }

    val pullRefreshState = rememberPullToRefreshState()
    PullToRefreshBox(
        state = pullRefreshState,
        isRefreshing = isRefreshing,
        onRefresh = {
            viewModel.readPosts(account, sort)
        }
    ) {
        val postListInfo = (state as State.Success<PostListInfo>).data
        PostList(postListInfo.posts, viewModel)
    }
}

@Composable
fun PostList(postList: List<PostItem>, viewModel: PostListViewModel) {
    AppendableLazyColumn(
        onAppend = {
            viewModel.appendPosts()
        }
    ) {
        items(postList.size) { index ->
            PostListItem(postList[index], ::onItemClick, ::onImageClick, ::onUpvoteClick, ::onDownvoteClick)
        }
    }
}

@Composable
@Preview
fun PostListPreview() {
    val postList = listOf(
        samplePost,
        samplePost,
        samplePost
    )

    PostList(postList, PostListViewModel())
}

@Composable
fun PostListItem(
    postItem: PostItem,
    onItemClick: (Context, PostItem) -> Unit,
    onImageClick: (Context, PostItem) -> Unit,
    onUpvoteClick: (Context, PostItem) -> Unit,
    onDownvoteClick: (Context, PostItem) -> Unit
) {
    val context = LocalContext.current
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .borderBottom(2.dp, Color.Gray)
            .clickable { onItemClick(context, postItem) }
            .padding(start = 8.dp, end = 8.dp, top = 8.dp)
    ) {
        // Title
        Text(
            text = postItem.title,
            maxLines = 2,
            style = TextStyle(
                color = Color.Black,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
        )
        // Image and text preview
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 5.dp, bottom = 8.dp)
        ) {
            val urls = postItem.imageURLs
            AsyncImage(
                model = if (urls.isEmpty()) "" else urls[0],
                contentDescription = "Thumbnail of the first image of this post",
                error = painterResource(R.drawable.no_image_available),
                placeholder = rememberAsyncImagePainter(R.drawable.loading),
                modifier = Modifier
                    .width(80.dp)
                    .height(80.dp)
                    .clickable {
                        onImageClick(context, postItem)
                    }
                    .padding(end = 10.dp)
            )
            Text(
                text = postItem.content,
                maxLines = 3,
                style = TextStyle(
                    color = Color.Black,
                    fontSize = 18.sp
                )
            )
        }
        // Tag/community and time
        Row(
            modifier = Modifier.fillMaxWidth()
        ) {
            val textStyle = TextStyle(
                color = Color.Gray,
                fontSize = 16.sp
            )
            Text(
                text = postItem.tagOrCommunity,
                style = textStyle,
                modifier = Modifier.weight(1f)
            )
            Text(
                text = postItem.time,
                style = textStyle
            )
        }
        // Rewards and account
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 5.dp, bottom = 8.dp)
        ) {
            val textStyle = TextStyle(color = Color.Black, fontSize = 14.sp)
            Text(text = "$${postItem.rewards} ", style = textStyle)
            Text(
                text = "\uD83D\uDD3A ${postItem.upvoteCount} ",
                style = textStyle,
                modifier = Modifier.clickable { onUpvoteClick(context, postItem) }
            )
            Text(
                text = "\uD83D\uDD3B ${postItem.downvoteCount} ",
                style = textStyle,
                modifier = Modifier.clickable { onDownvoteClick(context, postItem) }
            )
            Text(
                text = "${postItem.account} (${postItem.reputation})",
                textAlign = TextAlign.End,
                style = textStyle.copy(fontWeight = FontWeight.Bold),
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
@Preview
fun PostListItemPreview() {
    PostListItem(samplePost, ::onItemClick, ::onItemClick, ::onUpvoteClick, ::onDownvoteClick)
}

fun onItemClick(context: Context, postItem: PostItem) {
    val fragment = context.getCurrentFragment(R.id.nav_host_fragment_activity_main)
    val action = PostListFragmentDirections.actionNavigationPostListToNavigationPost(
        postItem.account,
        postItem.permlink
    )
    fragment?.findNavController()?.navigate(action)
}

fun onImageClick(context: Context, postItem: PostItem) {
    Intent(context, PostImagePagerActivity::class.java).also {
        if (postItem.imageURLs.isEmpty()) {
            val fragment = context.getCurrentFragment(R.id.nav_host_fragment_activity_main)
            fragment?.showToastShortly(context.getString(R.string.error_no_post_image))
            return
        }

        val imageURLArrayList = ArrayList(postItem.imageURLs)
        it.putExtra(PostImagePagerActivity.INTENT_BUNDLE_IMAGE_URL_LIST, imageURLArrayList)
        context.startActivity(it)
    }
}

fun onUpvoteClick(context: Context, postItem: PostItem) {
    val fragment = context.getCurrentFragment(R.id.nav_host_fragment_activity_main)
    if (fragment is PostListFragment) {
        fragment.startUpvoteListActivity(postItem.activeVotes)
    }
}

fun onDownvoteClick(context: Context, postItem: PostItem) {
    val fragment = context.getCurrentFragment(R.id.nav_host_fragment_activity_main)
    if (fragment is PostListFragment) {
        fragment.startDownvoteListActivity(postItem.activeVotes)
    }
}

private val samplePost by lazy {
    PostItem(
        "Lorem ipsum dolor sit amet, consectetur adipiscing elit...",
        "thumbnail url",
        listOf("image url1", "image url2", "image url3"),
        "content",
        "tar or community",
        "2025-01-23 12:34:56",
        987.654f,
        1,
        0,
        listOf(ActiveVote("voter1", 100f, 987.654f)),
        "author",
        88,
        "testpermlink"
    )
}