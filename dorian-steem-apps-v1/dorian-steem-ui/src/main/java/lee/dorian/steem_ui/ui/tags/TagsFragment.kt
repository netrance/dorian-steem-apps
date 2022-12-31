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
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout.OnRefreshListener
import lee.dorian.steem_data.model.post.GetRankedPostParamsDTO
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
            binding.viewModel = viewModel
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
    }

    private val currentTagObserver = Observer<String> { tag ->
        readRankedPosts()
    }

    private fun readRankedPosts() {
        val tag = activityViewModel.currentTag.value ?: ""
        val sort = viewModel.sort.value ?: ""

        if (sort.isEmpty()) {
            showToastShortly(getString(R.string.error_sort_is_not_set))
            return
        }

        binding.swipeRefreshPostList.isRefreshing = true
        viewModel.readRankedPosts(
            tag
        ) {
            binding.swipeRefreshPostList.isRefreshing = false
        }
    }

    private val sortCheckedChangedListener = OnCheckedChangeListener { radioGroup, radioButtonId ->
        viewModel.sort.value = when (radioButtonId) {
            R.id.radiobtn_trending -> GetRankedPostParamsDTO.InnerParams.SORT_TRENDING
            R.id.radiobtn_created -> GetRankedPostParamsDTO.InnerParams.SORT_CREATED
            R.id.radiobtn_payout -> GetRankedPostParamsDTO.InnerParams.SORT_PAYOUT
            else -> GetRankedPostParamsDTO.InnerParams.SORT_TRENDING
        }
    }

    private val radiobtnSortClickListener = OnClickListener {
        readRankedPosts()
    }

    private val rankedPostsScrollListener: RecyclerView.OnScrollListener = object : RecyclerView.OnScrollListener() {
        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)

            binding.viewModel?.let {
                if (!binding.listPostItem.canScrollVertically(1)) {
                    val tag = activityViewModel.currentTag.value ?: ""
                    it.appendRankedPosts(tag)
                }
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
            // open new activity to show upvoting list
            val downvotes = postItem.activeVotes.filter { vote ->
                vote.isDownvote()
            }.toTypedArray().apply { sortBy { it.value } }

            if (downvotes.isEmpty()) {
                return
            }

            Intent(requireActivity(), VoteListActivity::class.java).apply {
                this.putExtra(VoteListActivity.INTENT_BUNDLE_VOTER_LIST, downvotes)
                startActivity(this)
            }
        }
    }

}