package lee.dorian.steem_ui.ui.post.list

import android.content.Context
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import lee.dorian.steem_domain.model.ActiveVote
import lee.dorian.steem_domain.model.PostItem
import lee.dorian.steem_domain.model.PostListInfo
import lee.dorian.steem_ui.model.State
import lee.dorian.steem_ui.ui.compose.ErrorOrFailure
import lee.dorian.steem_ui.ui.compose.Loading
import lee.dorian.steem_ui.ui.post.PostList

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PostListScreen(
    viewModel: PostListViewModel = hiltViewModel(),
    onPostItemClick: (PostItem) -> Unit = {},
    onPostImageClick: (Context, PostItem) -> Unit = { _, _ -> },
    onUpvoteClick: (Context, List<ActiveVote>) -> Unit = { _, _ -> },
    onDownvoteClick: (Context, List<ActiveVote>) -> Unit = { _, _ -> }
) {
    val state by viewModel.flowState.collectAsStateWithLifecycle()
    val isRefreshing by viewModel.flowIsRefreshing.collectAsStateWithLifecycle()

    if (state is State.Empty) {
        viewModel.readPosts()
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
            viewModel.readPosts()
        }
    ) {
        val postListInfo = (state as State.Success<PostListInfo>).data
        PostList(
            postListInfo.posts,
            viewModel,
            onPostItemClick,
            onPostImageClick,
            onUpvoteClick,
            onDownvoteClick
        )
    }
}
