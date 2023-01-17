package lee.dorian.steem_ui.ui.voter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.RecyclerView
import lee.dorian.steem_domain.model.ActiveVote
import lee.dorian.steem_domain.model.PostItem
import lee.dorian.steem_ui.databinding.LayoutVoteItemBinding

class VoteListAdapter(
    val profileImageClickListener: OnProfileImageClickListener
) : RecyclerView.Adapter<VoteListAdapter.VoterListViewHolder>(), Filterable {

    var voteArrayList = arrayListOf<ActiveVote>()
    var filteredVoteArrayList = arrayListOf<ActiveVote>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VoterListViewHolder {
        return VoterListViewHolder(LayoutVoteItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        ))
    }

    override fun onBindViewHolder(holder: VoterListViewHolder, position: Int) {
        try {
            holder.bind(filteredVoteArrayList[position])
            holder.binding.imageVoterProfile.setOnClickListener {
                profileImageClickListener.onClick(voteArrayList[position].voter)
            }
        }
        catch (e: IndexOutOfBoundsException) {
            e.printStackTrace()
        }
    }

    override fun getItemCount(): Int = filteredVoteArrayList.size

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    fun setVotes(votes: ArrayList<ActiveVote>) {
        this.voteArrayList = votes
        this.filteredVoteArrayList = votes
        notifyDataSetChanged()
    }

    inner class VoterListViewHolder(
        val binding: LayoutVoteItemBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(activeVote: ActiveVote) {
            binding.vote = activeVote
        }
    }

    override fun getFilter(): Filter = object: Filter() {
        override fun performFiltering(constraint: CharSequence?): FilterResults {
            val partOfSteemitAccount = constraint?.toString() ?: ""

            val filteredVoteArrayList = when {
                (partOfSteemitAccount.isEmpty()) -> voteArrayList
                else -> voteArrayList.filter { it.voter.contains(partOfSteemitAccount) }
            }

            return FilterResults().apply {
                this.values = filteredVoteArrayList
            }
        }

        override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
            filteredVoteArrayList = (results?.values as ArrayList<ActiveVote>)
            notifyDataSetChanged()
        }

    }

    interface OnProfileImageClickListener {
        fun onClick(steemitAccount: String)
    }

}