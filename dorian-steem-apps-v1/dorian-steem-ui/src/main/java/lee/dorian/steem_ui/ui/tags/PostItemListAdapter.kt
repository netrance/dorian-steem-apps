package lee.dorian.steem_ui.ui.tags

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import lee.dorian.steem_domain.model.PostItem
import lee.dorian.steem_ui.databinding.LayoutPostItemBinding

class PostItemListAdapter : RecyclerView.Adapter<PostItemListAdapter.PostItemListViewHolder>() {

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
        }
        catch (e: IndexOutOfBoundsException) {
            e.printStackTrace()
        }
    }

    override fun getItemCount(): Int = postItemList.size

    fun setList(list: List<PostItem>) {
        this.postItemList.apply {
            clear()
            addAll(list)
        }
        notifyDataSetChanged()
    }

    inner class PostItemListViewHolder(
        private val binding: LayoutPostItemBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(postItem: PostItem) {
            binding.postItem = postItem
        }
    }

}