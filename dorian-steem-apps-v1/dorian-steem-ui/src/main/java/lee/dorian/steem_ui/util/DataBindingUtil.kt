package lee.dorian.steem_ui.util

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import lee.dorian.steem_ui.ext.load

@BindingAdapter("android:src")
fun setSrc(_imageView: ImageView?, _url: String?) {
    // To prevent null pointer exceptions
    if ((null == _imageView) or (null == _url)) {
        return
    }

    _imageView?.load(_url ?: "")
}
