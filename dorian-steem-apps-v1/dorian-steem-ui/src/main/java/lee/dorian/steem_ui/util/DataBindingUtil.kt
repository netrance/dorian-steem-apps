package lee.dorian.steem_ui.util

import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import lee.dorian.steem_domain.model.PostItem
import lee.dorian.steem_ui.ui.tags.PostItemListAdapter

object DataBindingUtil {

    @BindingAdapter("postItemList")
    fun bind(recyclerView: RecyclerView?, postItemList: List<PostItem>?) {
        (recyclerView?.adapter as PostItemListAdapter)?.let {
            if (null != postItemList) {
                it.setList(postItemList)
            }
        }
    }

}