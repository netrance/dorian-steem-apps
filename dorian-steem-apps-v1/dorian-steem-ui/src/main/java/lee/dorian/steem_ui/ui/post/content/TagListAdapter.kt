package lee.dorian.steem_ui.ui.post.content

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import lee.dorian.steem_ui.databinding.LayoutTagItemBinding

class TagListAdapter(
    val tagList: List<String>
): RecyclerView.Adapter<TagListAdapter.PostItemListViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostItemListViewHolder {
        return PostItemListViewHolder(LayoutTagItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        ))
    }

    override fun onBindViewHolder(holder: PostItemListViewHolder, position: Int) {
        try {
            holder.bind(tagList[position])
        }
        catch (e: java.lang.IndexOutOfBoundsException) {
            e.printStackTrace()
        }
    }

    override fun getItemCount(): Int = tagList.size

    inner class PostItemListViewHolder(
        val binding: LayoutTagItemBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(tagName: String) {
            binding.textTag.text = "#${tagName}"
        }
    }

}