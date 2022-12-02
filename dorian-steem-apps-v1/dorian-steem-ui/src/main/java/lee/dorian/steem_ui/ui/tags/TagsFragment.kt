package lee.dorian.steem_ui.ui.tags

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioGroup.OnCheckedChangeListener
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SimpleItemAnimator
import lee.dorian.steem_data.model.post.GetRankedPostParamsDTO
import lee.dorian.steem_ui.MainViewModel
import lee.dorian.steem_ui.R
import lee.dorian.steem_ui.databinding.FragmentTagsBinding
import lee.dorian.steem_ui.ui.base.BaseFragment

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

        activityViewModel.currentTag.apply {
            removeObservers(viewLifecycleOwner)
            observe(viewLifecycleOwner, currentTagObserver)
        }

        viewModel.sort.apply {
            removeObservers(viewLifecycleOwner)
            observe(viewLifecycleOwner, currentSortObserver)
        }

        binding.listPostItem.apply {
            adapter = PostItemListAdapter().apply {
                setHasStableIds(true)
            }
            addOnScrollListener(rankedPostsScrollListener)
        }

        binding.radiogroupSort.apply {
            setOnCheckedChangeListener(sortCheckedChangedListener)
            clearCheck()
            check(R.id.radiobtn_trending)
        }
    }

    private val currentSortObserver = Observer<String> { sort ->
        val tag = activityViewModel.currentTag.value ?: ""
        if ((tag.length >= 2) and (sort.isNotEmpty())) {
            viewModel.readRankedPosts(
                tag
            )
        }
    }

    private val currentTagObserver = Observer<String> { tag ->
        val sort = viewModel.sort.value ?: ""
        if ((tag.length >= 2) and (sort.isNotEmpty())) {
            viewModel.readRankedPosts(
                tag
            )
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

    private val rankedPostsScrollListener: RecyclerView.OnScrollListener = object : RecyclerView.OnScrollListener() {
        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)

            binding.viewModel?.let {
                if (!binding.listPostItem.canScrollVertically(1)) {
                    val tag =activityViewModel.currentTag.value ?: ""
                    it.appendRankedPosts(tag)
                }
            }
        }
    }

}