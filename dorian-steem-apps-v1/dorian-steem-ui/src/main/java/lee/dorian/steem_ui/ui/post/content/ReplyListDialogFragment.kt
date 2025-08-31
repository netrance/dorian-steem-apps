package lee.dorian.steem_ui.ui.post.content

import android.webkit.WebView
import androidx.compose.foundation.Image
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import coil.compose.AsyncImage
import lee.dorian.steem_domain.model.ActiveVote
import lee.dorian.steem_domain.model.Post
import lee.dorian.steem_ui.R
import lee.dorian.dorian_android_ktx.android.context.findActivity
import lee.dorian.steem_ui.ext.loadMarkdown
import lee.dorian.steem_ui.ui.post.onDownvoteClick
import lee.dorian.steem_ui.ui.post.onUpvoteClick
import lee.dorian.steem_ui.ui.preview.postForTest
import lee.dorian.steem_ui.ui.preview.replyListForTest

@Composable
fun ReplyBottomSheet(replyList: List<Post>, onDismiss: () -> Unit) {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        ReplyListTopBar(onDismiss)
        ReplyList(replyList, scrollState)
    }
}

@Composable
@Preview(showBackground = true, backgroundColor = 0xFFFFFFFF)
fun ReplyBottomSheetPreview() {
    ReplyBottomSheet(replyListForTest, {})
}

@Composable
fun ReplyListTopBar(onDismiss: () -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            //.background(Color.White)
    ) {
        Text(
            text = "Reply List",
            color = Color.Black,
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.weight(1f)
        )
        Image(
            painter = painterResource(R.drawable.ic_close),
            contentDescription = "Button to close this bottomsheet",
            modifier = Modifier.clickable { onDismiss() }
        )
    }
}

@Composable
@Preview(showBackground = true, backgroundColor = 0xFFFFFFFF)
fun ReplyListTopBarPreview() {
    ReplyListTopBar {}
}

@Composable
fun ReplyList(replyList: List<Post>, scrollState: ScrollState) {
    LazyColumn(
        modifier = Modifier.fillMaxWidth().padding(8.dp)
    ) {
        items(replyList.size) { index ->
            ReplyItem(
                replyList[index],
                Color.White,
                scrollState
            )
        }
    }
}

@Composable
@Preview(showBackground = true, backgroundColor = 0xFFFFFFFF)
fun ReplyListPreview() {
    ReplyList(replyListForTest, rememberScrollState())
}

@Composable
fun ReplyItem(reply: Post, backgroundColor: Color, scrollState: ScrollState) {
    val paddingLeft = Math.min(Math.max(0, reply.depth - 1), 5) * 30

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(backgroundColor)
            .padding(start = paddingLeft.dp)
    ) {
        // Account and time
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            AsyncImage(
                model = "https://steemitimages.com/u/${reply.account}/avatar/small",
                contentDescription = "The profile image of this user",
                //error = painterResource(R.mipmap.ic_launcher),
                modifier = Modifier.width(30.dp).height(30.dp)
            )
            Text(
                text = String.format("%s (%s)", reply.account, reply.reputation),
                color = Color.Black,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(1f).padding(start = 8.dp)
            )
        }

        // Content
        val androidViewModifier = Modifier.fillMaxWidth().padding(vertical = 10.dp)
        if (LocalInspectionMode.current) {
            AndroidView(
                factory = { context ->
                    WebView(context)
                },
                modifier = androidViewModifier
            )
        }
        else {
            val activity = LocalContext.current.findActivity() ?: return
            val coroutineScope = rememberCoroutineScope()
            AndroidView(
                factory = { context ->
                    WebView(context).apply {
                        settings.javaScriptEnabled = true
                        settings.domStorageEnabled = true
                        clipToOutline = true
                        webChromeClient = PostContentWebChromeClient(activity, scrollState, coroutineScope)
                        loadMarkdown(reply.content)
                    }
                },
                modifier = androidViewModifier
            )
        }

        // Rewards and voting
        val context = LocalContext.current
        ReplyRewardsAndVotingCounts(
            reply,
            { activeVotes ->
                onUpvoteClick(context, activeVotes)
            },
            { activeVotes ->
                onDownvoteClick(context, activeVotes)
            }
        )

        HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp))
    }

}

@Composable
@Preview(showBackground = true, backgroundColor = 0xFFFFFFFF)
fun ReplyItemPreview() {
    ReplyItem(postForTest, Color.White, rememberScrollState())
}

@Composable
fun ReplyRewardsAndVotingCounts(
    post: Post,
    onUpvoteClick: (activeVotes: List<ActiveVote>) -> Unit,
    onDownvoteClick: (activeVotes: List<ActiveVote>) -> Unit
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
    }
}

@Composable
@Preview(showBackground = true)
fun ReplyRewardsAndVotingCountsPreview() {
    ReplyRewardsAndVotingCounts(postForTest, {}, {})
}