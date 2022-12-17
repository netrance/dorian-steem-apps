package lee.dorian.steem_ui.ui.voter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import lee.dorian.steem_domain.model.ActiveVote
import lee.dorian.steem_ui.databinding.LayoutVoteItemBinding

class VoteListAdapter() : RecyclerView.Adapter<VoteListAdapter.VoterListViewHolder>() {

    var voteArray = arrayOf<ActiveVote>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VoterListViewHolder {
        return VoterListViewHolder(LayoutVoteItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        ))
    }

    override fun onBindViewHolder(holder: VoterListViewHolder, position: Int) {
        try {
            holder.bind(voteArray[position])
        }
        catch (e: IndexOutOfBoundsException) {
            e.printStackTrace()
        }
    }

    override fun getItemCount(): Int = voteArray.size

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    fun setVotes(votes: Array<ActiveVote>) {
        this.voteArray = votes
        notifyDataSetChanged()
    }

    inner class VoterListViewHolder(
        val binding: LayoutVoteItemBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(activeVote: ActiveVote) {
            binding.vote = activeVote
        }
    }

}