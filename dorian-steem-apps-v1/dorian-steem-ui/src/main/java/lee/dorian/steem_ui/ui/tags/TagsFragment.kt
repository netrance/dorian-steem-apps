package lee.dorian.steem_ui.ui.tags

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import android.widget.RadioGroup.OnCheckedChangeListener
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
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
import lee.dorian.steem_ui.ui.voter.VoteListActivity

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

        lifecycleScope.apply {
            launch { viewModel.flowTagsState.collect(tagsStateCollector) }
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
        binding.swipeRefreshPostList.isRefreshing = false
        when (newState) {
            is TagsState.Loading -> binding.swipeRefreshPostList.isRefreshing = true
            is TagsState.Error, is TagsState.Failure -> showToastShortly(getString(R.string.error_cannot_load))
            is TagsState.Success -> updateRankedList(newState.tagList)
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
                //val tag = activityViewModel.currentTag.value ?: ""
                viewModel.appendRankedPosts()
            }
        }
    }

    private val swipeRefreshPostListRefreshListener = OnRefreshListener {
        readRankedPosts()
    }

    private val upvoteViewClickListener = object: PostItemListAdapter.OnVoteCountViewClickListener {
        override fun onClick(postItem: PostItem) {
            // open new activity to show upvoting list
            val upvotes = postItem.activeVotes.filter { vote ->
                vote.isUpvote()
            }.sortedByDescending { it.value }

            if (upvotes.isEmpty()) {
                return
            }

            val upvoteArrayList = ArrayList(upvotes)
            Intent(requireActivity(), VoteListActivity::class.java).apply {
                this.putExtra(VoteListActivity.INTENT_BUNDLE_VOTER_LIST, upvoteArrayList)
                startActivity(this)
            }
        }
    }

    private val downvoteViewClickListener = object: PostItemListAdapter.OnVoteCountViewClickListener {
        override fun onClick(postItem: PostItem) {
            // open new activity to show downvoting list
            val downvotes = postItem.activeVotes.filter { vote ->
                vote.isDownvote()
            }.sortedByDescending { it.value }

            if (downvotes.isEmpty()) {
                return
            }

            val downvoteArrayList = ArrayList(downvotes)
            Intent(requireActivity(), VoteListActivity::class.java).apply {
                this.putExtra(VoteListActivity.INTENT_BUNDLE_VOTER_LIST, downvoteArrayList)
                startActivity(this)
            }
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
            val action = TagsFragmentDirections.actionNavigationTagsToNavigationPost(
                author = author,
                permlink = permlink
            )
            navController.navigate(action)
        }
    }

    private fun updateRankedList(postItemList: List<PostItem>) {
        (binding.listRankedPostItem.adapter as PostItemListAdapter).setList(postItemList)
    }

    private fun emptyRankedList() {
        updateRankedList(listOf())
    }

    private fun readRankedPosts() {
        emptyRankedList()
        viewModel.readRankedPosts()
    }

    private fun updateSort(checkedRadioButtonId: Int) {
        viewModel.updateSort(checkedRadioButtonId)
        readRankedPosts()
    }

}