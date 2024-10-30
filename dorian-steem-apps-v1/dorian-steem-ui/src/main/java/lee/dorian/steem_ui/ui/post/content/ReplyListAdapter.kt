package lee.dorian.steem_ui.ui.post.content

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import lee.dorian.dorian_android_ktx.android.view.setMargins
import lee.dorian.steem_domain.model.Post
import lee.dorian.steem_ui.databinding.LayoutReplyItemBinding
import lee.dorian.steem_ui.ext.loadMarkdown
import lee.dorian.steem_ui.ext.loadSmallSteemitProfileImage

class ReplyListAdapter(
    val replyList: List<Post>,
    val post: Post
): RecyclerView.Adapter<ReplyListAdapter.ReplyListViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReplyListViewHolder {
        return ReplyListViewHolder(LayoutReplyItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        ))
    }

    override fun onBindViewHolder(holder: ReplyListViewHolder, position: Int) {
        try {
            holder.bind(replyList[position])
        }
        catch (e: java.lang.IndexOutOfBoundsException) {
            e.printStackTrace()
        }
    }

    override fun getItemCount(): Int = replyList.size

    inner class ReplyListViewHolder(
        val binding: LayoutReplyItemBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(post: Post) {
            binding.imagePostAuthorProfile.loadSmallSteemitProfileImage(post.account)
            binding.textPostAuthor.text = "${post.account} (${post.reputation})"
            binding.textCreatedTime.text = post.time
            binding.webReplyContent.loadMarkdown(post.content)
            binding.textRewards.text = String.format("$%.3f", post.rewards)
            binding.textUpvotes.text = String.format("\uD83D\uDD3A%d", post.upvoteCount)
            binding.textDownvotes.text = String.format("\uD83D\uDD3B%d", post.downvoteCount)
            setMarginLeft(post.depth)
        }

        private fun setMarginLeft(depth: Int) {
            val marginLeft = Math.min(Math.max(0, depth - post.depth - 1), 5) * 30
            binding.root.setMargins(marginLeft, 0, 0, 0)
        }
    }

}