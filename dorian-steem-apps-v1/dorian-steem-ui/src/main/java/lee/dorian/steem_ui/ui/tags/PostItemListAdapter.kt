package lee.dorian.steem_ui.ui.tags

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import lee.dorian.steem_domain.model.PostItem
import lee.dorian.steem_ui.databinding.LayoutPostItemBinding

class PostItemListAdapter(
    val upvoteViewClickListener: OnVoteCountViewClickListener,
    val downvoteViewClickListener: OnVoteCountViewClickListener
) : RecyclerView.Adapter<PostItemListAdapter.PostItemListViewHolder>() {

    val postItemList = mutableListOf<PostItem>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostItemListViewHolder {
        return PostItemListViewHolder(LayoutPostItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        ))
    }

    override fun onBindViewHolder(holder: PostItemListViewHolder, position: Int) {
        try {
            holder.bind(postItemList[position])
            holder.binding.textUpvotes.setOnClickListener { upvoteViewClickListener.onClick(postItemList[position]) }
            holder.binding.textDownvotes.setOnClickListener { downvoteViewClickListener.onClick(postItemList[position]) }
        }
        catch (e: IndexOutOfBoundsException) {
            e.printStackTrace()
        }
    }

    override fun getItemCount(): Int = postItemList.size

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    fun setList(list: List<PostItem>) {
        this.postItemList.apply {
            clear()
            addAll(list)
        }
        notifyDataSetChanged()
    }

    inner class PostItemListViewHolder(
        val binding: LayoutPostItemBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(postItem: PostItem) {
            binding.postItem = postItem
        }
    }

    interface OnVoteCountViewClickListener {
        fun onClick(postItem: PostItem)
    }

}