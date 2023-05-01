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

        binding.listPostItem.apply {
            adapter = PostItemListAdapter(upvoteViewClickListener, downvoteViewClickListener).apply {
                setHasStableIds(true)
            }
            addOnScrollListener(rankedPostsScrollListener)
        }

        binding.radiogroupSort.apply {
            clearCheck()
            setOnCheckedChangeListener(sortCheckedChangedListener)
            check(R.id.radiobtn_trending)
        }

        binding.apply {
            radiobtnTrending.setOnClickListener(radiobtnSortClickListener)
            radiobtnCreated.setOnClickListener(radiobtnSortClickListener)
            radiobtnPayout.setOnClickListener(radiobtnSortClickListener)
            swipeRefreshPostList.setOnRefreshListener(swipeRefreshPostListRefreshListener)
        }

        activityViewModel.currentTag.apply {
            removeObservers(viewLifecycleOwner)
            observe(viewLifecycleOwner, currentTagObserver)
        }

        lifecycleScope.apply {
            launch { viewModel.flowTagsState.collect(tagsStateCollector) }
            launch { viewModel.flowTag.collect(tagCollector) }
            launch { viewModel.flowSort.collect(sortCollector) }
        }
    }

    private val currentTagObserver = Observer<String> { newTag ->
        viewModel.updateTag(newTag)
    }

    private val tagCollector = FlowCollector<String> { newTag ->
        readRankedPosts()
    }

    private val sortCollector = FlowCollector<String> { newSort ->
        readRankedPosts()
    }

    private val tagsStateCollector = FlowCollector<TagsState> { newState ->
        binding.swipeRefreshPostList.isRefreshing = false
        when (newState) {
            is TagsState.Loading -> {
                binding.swipeRefreshPostList.isRefreshing = true
                updateRankedList(listOf())
            }
            is TagsState.Error, is TagsState.Failure -> {
                showToastShortly(getString(R.string.error_cannot_load))
            }
            is TagsState.Success -> {
                updateRankedList(newState.tagList)
            }
        }
    }

    private val sortCheckedChangedListener = OnCheckedChangeListener { radioGroup, radioButtonId ->
        viewModel.updateSort(radioButtonId)
    }

    private val radiobtnSortClickListener = OnClickListener {
        readRankedPosts()
    }

    private val rankedPostsScrollListener: RecyclerView.OnScrollListener = object : RecyclerView.OnScrollListener() {
        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)

            if (!binding.listPostItem.canScrollVertically(1)) {
                val tag = activityViewModel.currentTag.value ?: ""
                viewModel.appendRankedPosts(tag)
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

    private fun updateRankedList(postItemList: List<PostItem>) {
        (binding.listPostItem.adapter as PostItemListAdapter).setList(postItemList)
    }

    private fun readRankedPosts() {
        val sort = viewModel._flowSort.value

        if (sort.isEmpty()) {
            showToastShortly(getString(R.string.error_sort_is_not_set))
            return
        }

        viewModel.readRankedPosts()
    }

}