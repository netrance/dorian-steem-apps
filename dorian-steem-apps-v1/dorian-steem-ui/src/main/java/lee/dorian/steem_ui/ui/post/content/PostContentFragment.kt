package lee.dorian.steem_ui.ui.post.content

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import coil.compose.AsyncImage
import kotlinx.coroutines.launch
import lee.dorian.steem_domain.model.ActiveVote
import lee.dorian.steem_domain.model.Post
import lee.dorian.steem_ui.MainViewModel
import lee.dorian.steem_ui.ext.*
import lee.dorian.steem_ui.ui.compose.ErrorOrFailure
import lee.dorian.steem_ui.ui.compose.Loading

class PostContentFragment : Fragment() {

    private val args: PostContentFragmentArgs by navArgs()

    val viewModel: PostContentViewModel by lazy {
        // Set owner parameter to requireActivity() to share this view model with bottom sheet
        ViewModelProvider(requireActivity()).get(PostContentViewModel::class.java)
    }

    val activityViewModel by lazy {
        ViewModelProvider(requireActivity()).get(MainViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                PostScreen(
                    viewModel,
                    onUpvoteClick = { activeVotes -> onUpvoteClicked(activeVotes) },
                    onDownvoteClick = { activeVotes -> onDownvoteClicked(activeVotes) },
                    onReplyCountClick = { replyCount -> onReplyCountClicked(replyCount) }
                )
            }

            viewLifecycleOwner.lifecycleScope.launch {
                val author = args.author
                val permlink = args.permlink
                viewModel.initState(author, permlink)
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    private fun onUpvoteClicked(activeVotes: List<ActiveVote>) {
        requireActivity().startUpvoteListActivity(activeVotes)
    }

    private fun onDownvoteClicked(activeVotes: List<ActiveVote>) {
        requireActivity().startDownvoteListActivity(activeVotes)
    }

    private fun onReplyCountClicked(replyCount: Int) {
        if (replyCount == 0) {
            showToastShortly("No reply of this post.")
            return
        }

        val replyBottomSheet = ReplyListDialogFragment()
        replyBottomSheet.show(requireActivity().supportFragmentManager, replyBottomSheet.tag)
    }

}

@Composable
fun PostScreen(
    viewModel: PostContentViewModel,
    onUpvoteClick: (activeVotes: List<ActiveVote>) -> Unit,
    onDownvoteClick: (activeVotes: List<ActiveVote>) -> Unit,
    onReplyCountClick: (replyCount: Int) -> Unit
) {
    val state by viewModel.flowPostState.collectAsStateWithLifecycle()

    if (state is PostContentState.Loading) {
        Loading()
        return
    }
    else if (state !is PostContentState.Success) {
        ErrorOrFailure()
        return
    }

    val post = (state as PostContentState.Success).post
    val replyCount = (state as PostContentState.Success).replies.size
    Box(modifier = Modifier.fillMaxSize()) {
        val scrollState = rememberScrollState()

        Column(
            modifier = Modifier.fillMaxSize().padding(10.dp).verticalScroll(scrollState)
        ) {
            with(post) {
                PostInfo(title, communityTitle, time, account, reputation.toString())
                PostContent(post, scrollState)
                PostTagsRow(tags)
            }
            HorizontalDivider(
                thickness = 2.dp,
                modifier = Modifier.padding(PaddingValues(vertical = 15.dp))
            )
            PostRewardsReplyCount(
                post,
                replyCount,
                onUpvoteClick,
                onDownvoteClick,
                onReplyCountClick
            )
        }
    }
}

@Composable
fun PostInfo(title: String, communityTitle: String, time: String, account: String, reputation: String) {
    Column(
        modifier = Modifier
            .background(Color.White)
            .fillMaxWidth()
            .padding(PaddingValues(vertical = 10.dp))
    ) {
        Text(
            text = title,
            color = Color.Black,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(PaddingValues(bottom = 10.dp))
        )
        Row(
            modifier = Modifier.fillMaxWidth().padding(PaddingValues(bottom = 10.dp))
        ) {
            Text(
                text = communityTitle,
                color = Color.Black,
                fontSize = 18.sp,
                modifier = Modifier.weight(1f)
            )
            Text(
                text = time,
                color = Color.Black,
                fontSize = 18.sp
            )
        }
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(PaddingValues(bottom = 10.dp))
        ) {
            Text(
                text = String.format("%s (%s)", account, reputation),
                color = Color.Black,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.End,
                modifier = Modifier.weight(1f).padding(PaddingValues(end = 8.dp))
            )
            AsyncImage(
                model = "https://steemitimages.com/u/${account}/avatar/small",
                contentDescription = "The profile image of this user",
                modifier = Modifier.width(30.dp).height(30.dp)
            )
        }
        HorizontalDivider(thickness = 2.dp)
    }
}

@Composable
@Preview
fun PostInfoPreview() {
    PostInfo("Post example", "community ex", "2024-12-31 12:34:56", "dorian-mobileapp", "60")
}

@Composable
fun PostContent(
    post: Post,
    scrollState: ScrollState
) {
    val activity = LocalContext.current.findActivity() ?: return
    val coroutineScope = rememberCoroutineScope()

    AndroidView(
        factory = { context ->
            WebView(context).apply {
                settings.javaScriptEnabled = true
                settings.domStorageEnabled = true
                clipToOutline = true
                webChromeClient = PostContentWebChromeClient(activity, scrollState, coroutineScope)
                loadMarkdown(post.content)
            }
        }
    )
}

@Composable
fun PostTagsRow(tags: List<String>) {
    LazyRow(modifier = Modifier.fillMaxWidth()) {
        items(tags) { tag ->
            PostTag(tag)
            Spacer(Modifier.width(5.dp))
        }
    }
}

@Composable
@Preview(showBackground = true)
fun PostTagsRowPreview() {
    PostTagsRow(listOf("tag1", "tag2", "tag3", "tag4", "tag5"))
}

@Composable
fun PostTag(item: String) {
    Text(
        text = "#${item}",
        color = Color.Black,
        fontSize = 13.sp,
        modifier = Modifier.background(Color(0xFFEEEEEE)).padding(5.dp)
    )
}

@Composable
@Preview(showBackground = true)
fun PostTagPreview() {
    PostTag("steem")
}

@Composable
fun PostRewardsReplyCount(
    post: Post,
    replyCount: Int,
    onUpvoteClick: (activeVotes: List<ActiveVote>) -> Unit,
    onDownvoteClick: (activeVotes: List<ActiveVote>) -> Unit,
    onReplyCountClick: (replyCount: Int) -> Unit
) {
    val textStyle = TextStyle(
        color = Color.Black,
        fontSize = 16.sp
    )

    Row(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = String.format("$%.3f", post.rewards),
            style = textStyle
        )
        Text(
            text = " \uD83D\uDD3A ${post.upvoteCount}",
            style = textStyle,
            modifier = Modifier
                .padding(PaddingValues(end = 6.dp))
                .clickable { onUpvoteClick(post.activeVotes) }
        )
        Text(
            text = "\uD83D\uDD3B ${post.downvoteCount}",
            style = textStyle,
            modifier = Modifier
                .padding(PaddingValues(end = 6.dp))
                .clickable { onDownvoteClick(post.activeVotes) }
        )
        Text(
            text = "\uD83D\uDCAC ${replyCount}",
            style = textStyle,
            textAlign = TextAlign.End,
            modifier = Modifier
                .weight(1f)
                .clickable { onReplyCountClick(replyCount) }
        )
    }
}

@Composable
@Preview(showBackground = true)
fun PostRewardsReplyCountPreview() {
    PostRewardsReplyCount(postForTest, 0, {}, {}, {})
}

private val postForTest = Post(
    "Title For Test",
    "https://cdn.steemitimages.com/DQmdGQpHV23GagfH4teLgwfGCRahgbioTZBBj23axEZgpdA/image.png",
    listOf("tag1", "tag2", "tag3", "tag4", "tag5"),
    listOf(),
    "DorianSteemApp",
    "hive-XXXXXX",
    "Community Example",
    "Content sample......",
    "2345-01-23 12:34:56",
    0,
    100.100f,
    100.100f,
    0f,
    false,
    "2345-01-30 12:34:56",
    3,
    0,
    listOf(
        ActiveVote("voter-a", 100f, 50.05f),
        ActiveVote("voter-b", 50f, 25.025f),
        ActiveVote("voter-c", 50f, 25.025f),
    ),
    0f,
    "account",
    25,
    "permlink-sample"
)