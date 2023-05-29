package lee.dorian.steem_ui.ui.tags

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import lee.dorian.steem_domain.model.PostItem
import lee.dorian.steem_ui.databinding.LayoutPostItemBinding
import lee.dorian.steem_ui.ext.load

class PostItemListAdapter(
    private val upvoteViewClickListener: OnVoteCountViewClickListener,
    private val downvoteViewClickListener: OnVoteCountViewClickListener,
    private val postImageViewClickListener: OnPostImageViewClickListener
) : RecyclerView.Adapter<PostItemListAdapter.PostItemListViewHolder>() {

    private val postItemList = mutableListOf<PostItem>()

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
            holder.binding.imageThumbnail.setOnClickListener { postImageViewClickListener.onClick(postItemList[position].imageURLs) }
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
            binding.apply {
                imageThumbnail.load(postItem.thumbnailURL)
                textTitle.text = postItem.title
                textContent.text = postItem.content
                textMainTag.text = postItem.tagOrCommunity
                textCreatedTime.text = postItem.time
                textRewards.text = String.format("$%.3f", postItem.rewards)
                textUpvotes.text = String.format("\uD83D\uDD3A%d", postItem.upvoteCount)
                textDownvotes.text = String.format("\uD83D\uDD3B%d", postItem.downvoteCount)
                textAccountReputation.text = String.format("%s (%d)", postItem.account, postItem.reputation)
            }
        }
    }

    interface OnVoteCountViewClickListener {
        fun onClick(postItem: PostItem)
    }

    interface OnPostImageViewClickListener {
        fun onClick(imageURLs: List<String>)
    }

}