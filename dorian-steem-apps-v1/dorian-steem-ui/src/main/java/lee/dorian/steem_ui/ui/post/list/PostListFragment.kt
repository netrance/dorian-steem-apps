package lee.dorian.steem_ui.ui.post.list

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.launch
import lee.dorian.steem_domain.model.PostListInfo
import lee.dorian.steem_domain.model.PostItem
import lee.dorian.steem_ui.MainViewModel
import lee.dorian.steem_ui.R
import lee.dorian.steem_ui.databinding.FragmentBlogBinding
import lee.dorian.steem_ui.ext.showToastShortly
import lee.dorian.steem_ui.model.State
import lee.dorian.steem_ui.ui.base.BaseFragment
import lee.dorian.steem_ui.ui.post.PostImagePagerActivity
import lee.dorian.steem_ui.ui.tags.PostItemListAdapter

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
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.swipeRefreshBlog.isRefreshing = false
        binding.listBlogPostItem.run {
            adapter = PostItemListAdapter(
                postItemViewClickListener,
                upvoteViewClickListener,
                downvoteViewClickListener,
                postImageViewClickListener
            ).apply {
                setHasStableIds(true)
            }
            addOnScrollListener(postsScrollListener)
        }

        binding.apply {
            swipeRefreshBlog.setOnRefreshListener(swipeRefreshPostListRefreshListener)
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.flowState.collect(stateCollector)
        }
    }

    private val stateCollector = FlowCollector<State<PostListInfo>> { newState ->
        binding.swipeRefreshBlog.isRefreshing = false
        when (newState) {
            is State.Error, is State.Failure -> {
                showToastShortly(getString(R.string.error_cannot_load))
            }
            is State.Empty -> {
                readPosts()
            }
            is State.Loading -> {
                binding.swipeRefreshBlog.isRefreshing = true
            }
            is State.Success -> {
                updateList(newState.data.posts)
            }
            else -> {
                showToastShortly("Testing...")
            }
        }
    }

    private val upvoteViewClickListener = object: PostItemListAdapter.OnVoteCountViewClickListener {
        override fun onClick(postItem: PostItem) {
            this@PostListFragment.startUpvoteListActivity(postItem.activeVotes)
        }
    }

    private val downvoteViewClickListener = object: PostItemListAdapter.OnVoteCountViewClickListener {
        override fun onClick(postItem: PostItem) {
            this@PostListFragment.startDownvoteListActivity(postItem.activeVotes)
        }
    }

    // Redundant code
    private val postImageViewClickListener = object: PostItemListAdapter.OnPostImageViewClickListener {
        override fun onClick(imageURLs: List<String>) {
            Intent(requireActivity(), PostImagePagerActivity::class.java).also {
                if (imageURLs.isEmpty()) {
                    showToastShortly(getString(R.string.error_no_post_image))
                    return
                }

                val imageURLArrayList = ArrayList(imageURLs)
                it.putExtra(PostImagePagerActivity.INTENT_BUNDLE_IMAGE_URL_LIST, imageURLArrayList)
                startActivity(it)
            }
        }
    }

    // Redundant code
    private val postItemViewClickListener = object: PostItemListAdapter.OnPostItemViewClickListener {
        override fun onClick(author: String, permlink: String) {
            val navController = findNavController()
            val action = PostListFragmentDirections.actionNavigationBlogToNavigationPost(
                author = author,
                permlink = permlink
            )
            navController.navigate(action)
        }
    }

    private val postsScrollListener: RecyclerView.OnScrollListener = object : RecyclerView.OnScrollListener() {
        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)

            if (!binding.listBlogPostItem.canScrollVertically(1)) {
                viewModel.appendPosts()
            }
        }
    }

    private val swipeRefreshPostListRefreshListener = SwipeRefreshLayout.OnRefreshListener {
        readPosts()
    }

    private fun updateList(postItemList: List<PostItem>) {
        binding.swipeRefreshBlog.isRefreshing = false
        (binding.listBlogPostItem.adapter as PostItemListAdapter).setList(postItemList)
    }

    private fun emptyList() {
        (binding.listBlogPostItem.adapter as PostItemListAdapter).setList(listOf())
    }

    private fun readPosts() = viewLifecycleOwner.lifecycleScope.launch {
        emptyList()
        viewModel.readPosts(args.author, args.sort)
    }

    companion object {
    }
}