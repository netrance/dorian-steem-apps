package lee.dorian.steem_ui.ui.voter

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.KeyEvent
import android.view.View.OnClickListener
import android.view.View.OnKeyListener
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import lee.dorian.steem_domain.model.ActiveVote
import lee.dorian.steem_ui.BaseActivity
import lee.dorian.steem_ui.R
import lee.dorian.steem_ui.databinding.ActivityVoteListBinding
import lee.dorian.steem_ui.ui.profile.ProfileImageActivity

class VoteListActivity : BaseActivity<ActivityVoteListBinding, VoteListViewModel>(R.layout.activity_vote_list) {

    companion object {
        const val INTENT_BUNDLE_VOTER_LIST = "voter_list"
    }

    override val viewModel: VoteListViewModel by lazy {
        ViewModelProvider(this).get(VoteListViewModel::class.java)
    }

    val voteListAdapter by lazy {
        VoteListAdapter(profileImageClickListener)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val voterArrayList = when {
            (Build.VERSION.SDK_INT >= 33) -> {
                intent.getSerializableExtra(INTENT_BUNDLE_VOTER_LIST) as ArrayList<ActiveVote>
                //intent.getSerializableExtra(INTENT_BUNDLE_VOTER_LIST, ArrayList<ActiveVote>::class.java)    // Cannot build
            }
            else -> {
                intent.getSerializableExtra(INTENT_BUNDLE_VOTER_LIST) as ArrayList<ActiveVote>
            }
        }
        binding.viewModel = viewModel
        binding.lifecycleOwner = this
        viewModel.votes.value = voterArrayList
        binding.listVoter.adapter = voteListAdapter

        // Set listeners.
        binding.imgbtnSearch.setOnClickListener(imgbtnSearchButtonClickListener)
        binding.editVoter.setOnKeyListener(editVoterKeyListener)

        // Observe live data.
        viewModel.votes.observe(this, votesObserver)
    }

    private val editVoterKeyListener = OnKeyListener { v, keyCode, event ->
        if (keyCode == KeyEvent.KEYCODE_ENTER) {
            binding.imgbtnSearch.performClick()
            true
        }
        false
    }

    private val imgbtnSearchButtonClickListener = OnClickListener {
        with (binding.listVoter.adapter) {
            if (this is VoteListAdapter) {
                this.filter.filter(binding.editVoter.text.toString())
            }
        }
    }

    private val profileImageClickListener = object: VoteListAdapter.OnProfileImageClickListener {
        override fun onClick(steemitAccount: String) {
            startActivity(Intent(this@VoteListActivity, ProfileImageActivity::class.java).apply {
                putExtra(ProfileImageActivity.INTENT_BUNDLE_STEEMIT_ACCOUNT, steemitAccount)
            })
        }
    }

    private val votesObserver = Observer<ArrayList<ActiveVote>> {
        voteListAdapter.setVotes(it)
    }

}