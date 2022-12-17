package lee.dorian.steem_ui.util

import android.net.Uri
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import lee.dorian.steem_domain.model.ActiveVote
import lee.dorian.steem_domain.model.PostItem
import lee.dorian.steem_ui.R
import lee.dorian.steem_ui.ui.tags.PostItemListAdapter
import lee.dorian.steem_ui.ui.voter.VoteListAdapter

@BindingAdapter("postItemList")
fun bind(recyclerView: RecyclerView?, postItemList: List<PostItem>?) {
    (recyclerView?.adapter as PostItemListAdapter)?.let {
        if (null != postItemList) {
            it.setList(postItemList)
        }
    }
}

@BindingAdapter("android:src")
fun setSrc(imageView: ImageView?, url: String?) {
    // To prevent null pointer exceptions
    if ((null == imageView) or (null == url)) {
        return
    }

    Glide.with(imageView!!)
        .load(Uri.parse(url))
        .placeholder(R.drawable.default_post_thumbnail)
        .error(R.drawable.default_post_thumbnail)
        .fallback(R.drawable.default_post_thumbnail)
        .into(imageView!!)
}

@BindingAdapter("votes")
fun bind(recyclerView: RecyclerView?, votes: Array<ActiveVote>?) {
    (recyclerView?.adapter as VoteListAdapter)?.let {
        if (null != votes) {
            it.setVotes(votes)
        }
    }
}