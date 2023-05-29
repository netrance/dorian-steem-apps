package lee.dorian.steem_ui.ui.post

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewpager.widget.PagerAdapter
import lee.dorian.steem_ui.R
import lee.dorian.steem_ui.databinding.LayoutPostImageBinding
import lee.dorian.steem_ui.databinding.LayoutPostItemBinding
import lee.dorian.steem_ui.ext.load

class PostImagePagerAdapter(
    private val context: Context,
    private val imageURLs: ArrayList<String>
) : PagerAdapter() {

    override fun getCount(): Int = imageURLs.size

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val inflater = LayoutInflater.from(context)
        val binding = LayoutPostImageBinding.inflate(inflater, container, false).also {
            it.imagePost.load(imageURLs[position])
            container.addView(it.root)
        }

        return binding.root
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as View)
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean = (view == `object`)

}