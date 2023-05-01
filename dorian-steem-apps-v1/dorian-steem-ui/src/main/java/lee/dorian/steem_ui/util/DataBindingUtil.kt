package lee.dorian.steem_ui.util

import android.graphics.drawable.Drawable
import android.net.Uri
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import lee.dorian.steem_domain.model.ActiveVote
import lee.dorian.steem_domain.model.PostItem
import lee.dorian.steem_ui.R
import lee.dorian.steem_ui.ext.toDPFromDimension
import lee.dorian.steem_ui.ext.toPixelFromDP
import lee.dorian.steem_ui.ui.tags.PostItemListAdapter
import lee.dorian.steem_ui.ui.voter.VoteListAdapter

@BindingAdapter("postItemList")
fun bind(recyclerView: RecyclerView?, postItemList: List<PostItem>?) {
    (recyclerView?.adapter as PostItemListAdapter).let {
        if (null != postItemList) {
            it.setList(postItemList)
        }
    }
}

@BindingAdapter("android:src")
fun setSrc(_imageView: ImageView?, _url: String?) {
    // To prevent null pointer exceptions
    if ((null == _imageView) or (null == _url)) {
        return
    }

    val url = _url!!
    val imageView = _imageView!!
    val imageWidth = imageView.width
    val imageHeight = imageView.height

    if (url.isEmpty() or (!url.startsWith("https://"))) {
        Glide.with(imageView)
            .load(R.drawable.no_image_available)
            .override(imageWidth, imageHeight)
            .into(imageView)
    }
    else {
        Glide.with(imageView)
            .load(Uri.parse(url))
            .override(imageWidth, imageHeight)
            .thumbnail(Glide.with(imageView).load(R.drawable.loading))
            .listener(object: RequestListener<Drawable> {
                override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Drawable>?, isFirstResource: Boolean): Boolean {
                    imageView.setImageDrawable(ContextCompat.getDrawable(imageView.context, R.drawable.no_image_available))
                    return false
                }

                override fun onResourceReady(resource: Drawable?, model: Any?, target: Target<Drawable>?, dataSource: DataSource?, isFirstResource: Boolean): Boolean {
                    imageView.setImageDrawable(resource)
                    return false
                }
            })
            .into(imageView)
    }
}

@BindingAdapter("votes")
fun bind(recyclerView: RecyclerView?, votes: ArrayList<ActiveVote>?) {
    (recyclerView?.adapter as VoteListAdapter)?.let {
        if (null != votes) {
            it.setVotes(votes)
        }
    }
}