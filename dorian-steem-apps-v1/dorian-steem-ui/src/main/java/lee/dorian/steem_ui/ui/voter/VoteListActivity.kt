package lee.dorian.steem_ui.ui.voter

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.KeyEvent
import android.view.View.OnClickListener
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_vote_list)

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
        viewModel.votes.value = voterArrayList
        binding.listVoter.adapter = VoteListAdapter(profileImageClickListener)

        // Set listeners.
        binding.imgbtnSearch.setOnClickListener(imgbtnSearchButtonClickListener)
        binding.editVoter.setOnKeyListener { v, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_ENTER) {
                binding.imgbtnSearch.performClick()
                true
            }
            false
        }
    }

    val imgbtnSearchButtonClickListener = OnClickListener {
        with (binding.listVoter.adapter) {
            if (this is VoteListAdapter) {
                this.filter.filter(binding.editVoter.text.toString())
            }
        }
    }

    val profileImageClickListener = object: VoteListAdapter.OnProfileImageClickListener {
        override fun onClick(steemitAccount: String) {
            startActivity(Intent(this@VoteListActivity, ProfileImageActivity::class.java).apply {
                putExtra(ProfileImageActivity.INTENT_BUNDLE_STEEMIT_ACCOUNT, steemitAccount)
            })
        }
    }
}