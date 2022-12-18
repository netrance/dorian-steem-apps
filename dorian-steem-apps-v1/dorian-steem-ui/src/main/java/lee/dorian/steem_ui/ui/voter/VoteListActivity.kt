package lee.dorian.steem_ui.ui.voter

import android.os.Build
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import lee.dorian.steem_domain.model.ActiveVote
import lee.dorian.steem_ui.BaseActivity
import lee.dorian.steem_ui.R
import lee.dorian.steem_ui.databinding.ActivityVoteListBinding

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

        //val voterList = intent.getSerializableExtra("voter_list", Array<ActiveVote>::class.java)
        val voterList = when {
            (Build.VERSION.SDK_INT >= 33) -> {
                intent.getSerializableExtra(INTENT_BUNDLE_VOTER_LIST, Array<ActiveVote>::class.java)
            }
            else -> {
                intent.getSerializableExtra(INTENT_BUNDLE_VOTER_LIST) as Array<ActiveVote>
            }
        }
        binding.viewModel = viewModel
        viewModel.votes.value = voterList
        binding.listVoter.adapter = VoteListAdapter()
    }

}