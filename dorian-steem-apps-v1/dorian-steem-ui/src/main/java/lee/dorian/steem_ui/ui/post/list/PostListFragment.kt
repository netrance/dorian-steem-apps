package lee.dorian.steem_ui.ui.post.list

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.ComposeView
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.fragment.navArgs
import lee.dorian.steem_domain.model.PostListInfo
import lee.dorian.steem_ui.MainViewModel
import lee.dorian.steem_ui.R
import lee.dorian.steem_ui.databinding.FragmentBlogBinding
import lee.dorian.steem_ui.model.State
import lee.dorian.steem_ui.ui.base.BaseFragment
import lee.dorian.steem_ui.ui.compose.ErrorOrFailure
import lee.dorian.steem_ui.ui.compose.Loading
import lee.dorian.steem_ui.ui.post.PostList

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
