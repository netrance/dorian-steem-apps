package lee.dorian.steem_ui.ext

import android.app.Activity
import android.content.Context
import android.content.Intent
import lee.dorian.steem_domain.model.ActiveVote
import lee.dorian.steem_ui.ui.voter.VoteListActivity

// open new activity to show upvoting list
fun Context.startUpvoteListActivity(activeVotes: List<ActiveVote>) {
    val upvotes = activeVotes.filter { vote ->
        vote.isUpvote()
    }.sortedByDescending { it.value }

    if (upvotes.isEmpty()) {
        return
    }

    val upvoteArrayList = ArrayList(upvotes)
    Intent(this, VoteListActivity::class.java).apply {
        this.putExtra(VoteListActivity.INTENT_BUNDLE_VOTER_LIST, upvoteArrayList)
        startActivity(this)
    }
}

// open new activity to show downvoting list
fun Context.startDownvoteListActivity(activeVotes: List<ActiveVote>) {
    val downvotes = activeVotes.filter { vote ->
        vote.isDownvote()
    }.sortedByDescending { it.value }

    if (downvotes.isEmpty()) {
        return
    }

    val downvoteArrayList = ArrayList(downvotes)
    Intent(this, VoteListActivity::class.java).apply {
        this.putExtra(VoteListActivity.INTENT_BUNDLE_VOTER_LIST, downvoteArrayList)
        startActivity(this)
    }
}