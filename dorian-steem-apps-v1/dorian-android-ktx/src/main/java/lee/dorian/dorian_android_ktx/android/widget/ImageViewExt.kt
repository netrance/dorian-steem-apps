package lee.dorian.steem_ui.ext

import android.graphics.drawable.Drawable
import android.net.Uri
import android.widget.ImageView
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import lee.dorian.dorian_android_ktx.R

fun ImageView.loadGif(resId: Int) {
    Glide.with(this).load(resId)
}

fun ImageView.unload() {
    Glide.with(this).clear(this)
}

fun ImageView.load(url: String, defaultDrawableId: Int? = R.drawable.no_image_available) {
    val imageWidth = this.width
    val imageHeight = this.height
    val thisImageView = this

    if (url.isEmpty() or (!url.startsWith("https://"))) {
        Glide.with(thisImageView)
            .load(defaultDrawableId)
            .override(imageWidth, imageHeight)
            .into(thisImageView)
    }
    else {
        Glide.with(thisImageView)
            .load(Uri.parse(url))
            .override(imageWidth, imageHeight)
            .thumbnail(Glide.with(thisImageView).load(R.drawable.loading))
            .listener(object: RequestListener<Drawable> {
                override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Drawable>?, isFirstResource: Boolean): Boolean {
                    thisImageView.setImageDrawable(when {
                        defaultDrawableId == null -> null
                        else -> ContextCompat.getDrawable(thisImageView.context, defaultDrawableId)
                    })
                    return false
                }

                override fun onResourceReady(resource: Drawable?, model: Any?, target: Target<Drawable>?, dataSource: DataSource?, isFirstResource: Boolean): Boolean {
                    thisImageView.setImageDrawable(resource)
                    return false
                }
            })
            .into(thisImageView)
    }
}