package lee.dorian.steem_ui.ui.base

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import lee.dorian.steem_domain.model.ActiveVote
import lee.dorian.steem_ui.ext.showToastShortly
import lee.dorian.steem_ui.ui.voter.VoteListActivity
import java.net.UnknownHostException

abstract class BaseFragment<VDB: ViewDataBinding, VM: BaseViewModel>(
    @LayoutRes private val layoutResID: Int
) : Fragment() {

    private var _binding: VDB? = null
    protected val binding get() = _binding!!

    abstract protected val viewModel: VM

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = DataBindingUtil.inflate(this.layoutInflater, layoutResID, null, false) as VDB
        _binding?.lifecycleOwner = this
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.liveThrowable.observe(viewLifecycleOwner, liveThrowableObserver)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        viewModel.compositeDisposable.clear()
        _binding = null
    }

    protected val liveThrowableObserver = Observer<Throwable> { error ->
        when (error) {
            is UnknownHostException -> showToastShortly("Error. Check internet connection.")
        }
    }

    // open new activity to show upvoting list
    fun startUpvoteListActivity(activeVotes: List<ActiveVote>) {
        val upvotes = activeVotes.filter { vote ->
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

    // open new activity to show downvoting list
    fun startDownvoteListActivity(activeVotes: List<ActiveVote>) {
        val downvotes = activeVotes.filter { vote ->
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