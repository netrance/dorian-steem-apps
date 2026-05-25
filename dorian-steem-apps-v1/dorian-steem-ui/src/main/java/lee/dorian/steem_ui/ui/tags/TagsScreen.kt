package lee.dorian.steem_ui.ui.tags

import android.content.Context
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import lee.dorian.dorian_android_ktx.androidx.compose.foundation.lazy.AppendableLazyColumn
import lee.dorian.steem_domain.model.ActiveVote
import lee.dorian.steem_domain.model.PostItem
import lee.dorian.steem_ui.model.State
import lee.dorian.steem_ui.ui.compose.ErrorOrFailure
import lee.dorian.steem_ui.ui.compose.Loading
import lee.dorian.steem_ui.ui.compose.OnLaunch
import lee.dorian.steem_ui.ui.compose.TagInputForm
import lee.dorian.steem_ui.ui.post.PostListItem

private val tagInfoList = listOf(
    TagScreenSortTabInfo("Trending", "trending"),
    TagScreenSortTabInfo("Created", "created"),
    TagScreenSortTabInfo("Payout", "payout")
)

@Composable
fun TagsScreen(
    onPostItemClick: (PostItem) -> Unit = {},
    onPostImageClick: (Context, PostItem) -> Unit = { _, _ -> },
    onUpvoteClick: (Context, List<ActiveVote>) -> Unit = { _, _ -> },
    onDownvoteClick: (Context, List<ActiveVote>) -> Unit = { _, _ -> },
    viewModel: TagsViewModel = hiltViewModel()
) {
    val keyboardController = LocalSoftwareKeyboardController.current

    var tag by rememberSaveable { mutableStateOf("") }
    var sort by rememberSaveable { mutableStateOf(tagInfoList[0].sort) }

    OnLaunch {
        if (viewModel.isContentEmpty()) {
            viewModel.readRankedPosts(tag, sort)
        }
    }

    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        TagInputForm(placeholder = "Input a tag.") { newTag ->
            tag = newTag
            viewModel.readRankedPosts(tag, sort)
            keyboardController?.hide()
        }
        TagsSortTabRow { tagTabInfo ->
            sort = tagTabInfo.sort
            viewModel.readRankedPosts(tag, sort)
        }
        TagsContent(
            viewModel,
            onAppend = {
                viewModel.appendRankedPosts(tag, sort)
            },
            onPostItemClick = onPostItemClick,
            onPostImageClick = onPostImageClick,
            onUpvoteClick = onUpvoteClick,
            onDownvoteClick = onDownvoteClick,
            modifier = Modifier.fillMaxWidth().weight(1f)
        )
    }
}

@Composable
fun TagsContent(
    viewModel: TagsViewModel,
    onAppend: () -> Unit,
    onPostItemClick: (PostItem) -> Unit = {},
    onPostImageClick: (Context, PostItem) -> Unit = { _, _ -> },
    onUpvoteClick: (Context, List<ActiveVote>) -> Unit = { _, _ -> },
    onDownvoteClick: (Context, List<ActiveVote>) -> Unit = { _, _ -> },
    modifier: Modifier
) {
    val state by viewModel.flowTagsState.collectAsStateWithLifecycle()

    if (state is State.Loading) {
        Loading()
        return
    }
    else if (state !is State.Success) {
        ErrorOrFailure()
        return
    }

    val tagPostList = (state as State.Success).data
    TagsPostList(
        tagPostList,
        viewModel,
        onAppend = onAppend,
        onPostItemClick = onPostItemClick,
        onPostImageClick = onPostImageClick,
        onUpvoteClick = onUpvoteClick,
        onDownvoteClick = onDownvoteClick
    )
}

@Composable
fun TagsSortTabRow(
    onTabSelected: (TagScreenSortTabInfo) -> Unit,
) {
    var selectedTabIndex by rememberSaveable { mutableStateOf(0) }

    TabRow(
        selectedTabIndex = selectedTabIndex,
        containerColor = Color.White,
        contentColor = Color.Black,
        indicator = { tabPositions ->
            TabRowDefaults.SecondaryIndicator(
                Modifier.tabIndicatorOffset(tabPositions[selectedTabIndex]),
                color = Color.Black
            )
        },
        modifier = Modifier.fillMaxWidth()
    ) {
        tagInfoList.forEachIndexed { index, tagInfo ->
            Tab(
                selected = (selectedTabIndex == index),
                onClick = {
                    selectedTabIndex = index
                    onTabSelected(tagInfoList[index])
                },
                text = { Text(text = tagInfo.title) }
            )
        }
    }
}

@Composable
@Preview
fun TagsSortTabRowPreview() {
    TagsSortTabRow {}
}

@Composable
fun TagsPostList(
    postList: List<PostItem>,
    viewModel: TagsViewModel,
    onAppend: () -> Unit,
    onPostItemClick: (PostItem) -> Unit,
    onPostImageClick: (Context, PostItem) -> Unit = { _, _ -> },
    onUpvoteClick: (Context, List<ActiveVote>) -> Unit = { _, _ -> },
    onDownvoteClick: (Context, List<ActiveVote>) -> Unit = { _, _ -> }
) {
    AppendableLazyColumn(
        onAppend = onAppend
    ) {
        items(postList.size) { index ->
            PostListItem(
                postItem = postList[index],
                onPostItemClick = { postItem ->
                    onPostItemClick(postItem)
                },
                onImageClick = onPostImageClick,
                onUpvoteClick = onUpvoteClick,
                onDownvoteClick = onDownvoteClick
            )
        }
    }
}

