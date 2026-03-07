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
import androidx.lifecycle.SavedStateHandle
import androidx.navigation.fragment.findNavController
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter
import kotlinx.coroutines.Dispatchers
import lee.dorian.dorian_android_ktx.androidx.compose.foundation.lazy.AppendableLazyColumn
import lee.dorian.dorian_android_ktx.androidx.compose.ui.borderBottom
import lee.dorian.steem_domain.model.PostItem
import lee.dorian.steem_ui.R
import lee.dorian.dorian_android_ktx.android.context.getCurrentFragment
import lee.dorian.steem_data.repository.SteemRepositoryImpl
import lee.dorian.steem_domain.model.ActiveVote
import lee.dorian.steem_domain.usecase.ReadPostsUseCase
import lee.dorian.steem_ui.ext.showToastShortly
import lee.dorian.steem_ui.ext.startDownvoteListActivity
import lee.dorian.steem_ui.ext.startUpvoteListActivity
import lee.dorian.steem_ui.ui.post.list.PostListFragment
import lee.dorian.steem_ui.ui.post.list.PostListFragmentDirections
import lee.dorian.steem_ui.ui.post.list.PostListViewModel
import lee.dorian.steem_ui.ui.preview.samplePostItem
import lee.dorian.steem_ui.ui.tags.TagsFragment
import lee.dorian.steem_ui.ui.tags.TagsFragmentDirections

@Composable
fun PostList(
    postList: List<PostItem>,
    viewModel: PostListViewModel,
    onPostItemClick: (PostItem) -> Unit = {},
    onPostImageClick: (Context, PostItem) -> Unit = { _, _ -> },
    onUpvoteClick: (Context, List<ActiveVote>) -> Unit = { _, _ -> },
    onDownvoteClick: (Context, List<ActiveVote>) -> Unit = { _, _ -> }
) {
    AppendableLazyColumn(
        onAppend = {
            viewModel.appendPosts()
        }
    ) {
        items(postList.size) { index ->
            PostListItem(
                postList[index],
                onPostItemClick,
                onPostImageClick,
                onUpvoteClick,
                onDownvoteClick
            )
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

    PostList(
        postList,
        PostListViewModel(
            SavedStateHandle(),
            ReadPostsUseCase(
                SteemRepositoryImpl(Dispatchers.IO),
                Dispatchers.IO
            )
        )
    )
}

@Composable
fun PostListItem(
    postItem: PostItem,
    onPostItemClick: (PostItem) -> Unit,
    onImageClick: (Context, PostItem) -> Unit,
    onUpvoteClick: (Context, List<ActiveVote>) -> Unit,
    onDownvoteClick: (Context, List<ActiveVote>) -> Unit
) {
    val context = LocalContext.current
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .borderBottom(2.dp, Color.Gray)
            .clickable { onPostItemClick(postItem) }
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
                modifier = Modifier.clickable { onUpvoteClick(context, postItem.activeVotes) }
            )
            Text(
                text = "\uD83D\uDD3B ${postItem.downvoteCount} ",
                style = textStyle,
                modifier = Modifier.clickable { onDownvoteClick(context, postItem.activeVotes) }
            )
            Text(
                text = " \uD83D\uDCAC ${postItem.replyCount}",
                textAlign = TextAlign.End,
                style = textStyle,
                modifier = Modifier
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
    PostListItem(
        samplePostItem,
        {},
        { _, _ -> },
        { _, _ -> },
        { _, _ -> }
    )
}