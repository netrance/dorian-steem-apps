package lee.dorian.steem_ui.ui.post

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.fragment.findNavController
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter
import lee.dorian.dorian_android_ktx.androidx.compose.foundation.lazy.AppendableLazyColumn
import lee.dorian.dorian_android_ktx.androidx.compose.ui.borderBottom
import lee.dorian.steem_domain.model.ActiveVote
import lee.dorian.steem_domain.model.PostItem
import lee.dorian.steem_ui.R
import lee.dorian.dorian_android_ktx.android.context.getCurrentFragment
import lee.dorian.steem_ui.ext.showToastShortly
import lee.dorian.steem_ui.ext.startDownvoteListActivity
import lee.dorian.steem_ui.ext.startUpvoteListActivity
import lee.dorian.steem_ui.ui.post.list.PostListFragment
import lee.dorian.steem_ui.ui.post.list.PostListFragmentDirections
import lee.dorian.steem_ui.ui.post.list.PostListViewModel
import lee.dorian.steem_ui.ui.preview.samplePostItem
import lee.dorian.steem_ui.ui.tags.TagsFragment
import lee.dorian.steem_ui.ui.tags.TagsFragmentDirections
import lee.dorian.steem_ui.ui.voter.VoteListActivity

@Composable
fun PostList(postList: List<PostItem>, viewModel: PostListViewModel) {
    AppendableLazyColumn(
        onAppend = {
            viewModel.appendPosts()
        }
    ) {
        items(postList.size) { index ->
            PostListItem(postList[index], ::onPostListItemClick, ::onPostListItemImageClick, ::onUpvoteClick, ::onDownvoteClick)
        }
    }
}

@Composable
@Preview
fun PostListPreview() {
    val postList = listOf(
        samplePostItem,
        samplePostItem,
        samplePostItem
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
    PostListItem(samplePostItem, ::onPostListItemClick, ::onPostListItemImageClick, ::onUpvoteClick, ::onDownvoteClick)
}

fun onPostListItemClick(context: Context, postItem: PostItem) {
    val fragment = context.getCurrentFragment(R.id.nav_host_fragment_activity_main)
    val action = when (fragment) {
        is PostListFragment -> {
            PostListFragmentDirections.actionNavigationPostListToNavigationPost(
                postItem.account,
                postItem.permlink
            )
        }
        is TagsFragment -> {
            TagsFragmentDirections.actionNavigationTagsToNavigationPostContent(
                postItem.account,
                postItem.permlink
            )
        }
        else -> {
            return
        }
    }
    fragment.findNavController().navigate(action)
}

fun onPostListItemImageClick(context: Context, postItem: PostItem) {
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
    if ((fragment is PostListFragment) or (fragment is TagsFragment)) {
        context.startUpvoteListActivity(postItem.activeVotes)
    }
}

fun onDownvoteClick(context: Context, postItem: PostItem) {
    val fragment = context.getCurrentFragment(R.id.nav_host_fragment_activity_main)
    if ((fragment is PostListFragment) or (fragment is TagsFragment)) {
        context.startDownvoteListActivity(postItem.activeVotes)
    }
}
