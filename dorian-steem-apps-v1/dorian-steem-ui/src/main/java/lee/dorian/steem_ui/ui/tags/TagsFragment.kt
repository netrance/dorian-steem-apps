package lee.dorian.steem_ui.ui.tags

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import androidx.lifecycle.*
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout.OnRefreshListener
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.launch
import lee.dorian.steem_domain.model.PostItem
import lee.dorian.steem_ui.MainViewModel
import lee.dorian.steem_ui.R
import lee.dorian.steem_ui.databinding.FragmentTagsBinding
import lee.dorian.steem_ui.ext.showToastShortly
import lee.dorian.steem_ui.ui.base.BaseFragment
import lee.dorian.steem_ui.ui.post.PostImagePagerActivity

class TagsFragment : BaseFragment<FragmentTagsBinding, TagsViewModel>(R.layout.fragment_tags) {

    override val viewModel by lazy {
        ViewModelProvider(this).get(TagsViewModel::class.java)
    }

    val activityViewModel by lazy {
        ViewModelProvider(requireActivity()).get(MainViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return super.onCreateView(inflater, container, savedInstanceState).apply {
            //binding.viewModel = viewModel
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.swipeRefreshPostList.isRefreshing = false
        binding.listRankedPostItem.apply {
            adapter = PostItemListAdapter(
                postItemViewClickListener,
                upvoteViewClickListener,
                downvoteViewClickListener,
                postImageViewClickListener
            ).apply {
                setHasStableIds(true)
            }
            addOnScrollListener(rankedPostsScrollListener)
        }

        binding.apply {
            radiobtnTrending.setOnClickListener(radiobtnTrendingClickListener)
            radiobtnCreated.setOnClickListener(radiobtnCreatedClickListener)
            radiobtnPayout.setOnClickListener(radiobtnPayoutClickListener)
            swipeRefreshPostList.setOnRefreshListener(swipeRefreshPostListRefreshListener)
        }

        activityViewModel.currentTag.apply {
            removeObservers(viewLifecycleOwner)
            observe(viewLifecycleOwner, currentTagObserver)
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.flowTagsState.collect(tagsStateCollector)
        }
    }

    private val currentTagObserver = Observer<String> { newTag ->
        val tagState: TagsState = viewModel.flowTagsState.value
        if (tagState is TagsState.Success) {
            if (newTag == viewModel.tag) {
                updateRankedList(tagState.tagList)
                return@Observer
            }
        }

        viewModel.tag = newTag
        readRankedPosts()
    }

    private val tagsStateCollector = FlowCollector<TagsState> { newState ->
        when (newState) {
            is TagsState.Error, is TagsState.Failure -> {
                showToastShortly(getString(R.string.error_cannot_load))
            }
            is TagsState.Success -> {
                binding.swipeRefreshPostList.isRefreshing = false
                updateRankedList(newState.tagList)
            }
            else -> {}
        }
    }

    private val radiobtnTrendingClickListener = OnClickListener {
        updateSort(R.id.radiobtn_trending)
    }

    private val radiobtnCreatedClickListener = OnClickListener {
        updateSort(R.id.radiobtn_created)
    }

    private val radiobtnPayoutClickListener = OnClickListener {
        updateSort(R.id.radiobtn_payout)
    }

    private val rankedPostsScrollListener: RecyclerView.OnScrollListener = object : RecyclerView.OnScrollListener() {
        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)

            if (!binding.listRankedPostItem.canScrollVertically(1)) {
                viewModel.appendRankedPosts()
            }
        }
    }

    private val swipeRefreshPostListRefreshListener = OnRefreshListener {
        readRankedPosts()
    }

    private val upvoteViewClickListener = object: PostItemListAdapter.OnVoteCountViewClickListener {
        override fun onClick(postItem: PostItem) {
            this@TagsFragment.startUpvoteListActivity(postItem.activeVotes)
        }
    }

    private val downvoteViewClickListener = object: PostItemListAdapter.OnVoteCountViewClickListener {
        override fun onClick(postItem: PostItem) {
            this@TagsFragment.startDownvoteListActivity(postItem.activeVotes)
        }
    }

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

    private val postItemViewClickListener = object: PostItemListAdapter.OnPostItemViewClickListener {
        override fun onClick(author: String, permlink: String) {
            val navController = findNavController()
            val action = TagsFragmentDirections.actionNavigationTagsToNavigationPostContent(
                author = author,
                permlink = permlink
            )
            navController.navigate(action)
        }
    }

    private fun updateRankedList(postItemList: List<PostItem>) {
        binding.swipeRefreshPostList.isRefreshing = false
        (binding?.listRankedPostItem?.adapter as PostItemListAdapter).setList(postItemList)
    }

    private fun emptyRankedList() {
        (binding?.listRankedPostItem?.adapter as PostItemListAdapter).setList(listOf())
    }

    private fun readRankedPosts() {
        emptyRankedList()
        viewModel.readRankedPosts()
        binding.swipeRefreshPostList.isRefreshing = true
    }

    private fun updateSort(checkedRadioButtonId: Int) {
        viewModel.updateSort(checkedRadioButtonId)
        readRankedPosts()
    }

}