package lee.dorian.steem_ui.ui.post

import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.viewpager.widget.ViewPager.OnPageChangeListener
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.launch
import lee.dorian.steem_ui.BaseActivity
import lee.dorian.steem_ui.R
import lee.dorian.steem_ui.databinding.ActivityPostImagePagerBinding

class PostImagePagerActivity : BaseActivity<
    ActivityPostImagePagerBinding,
    PostImagePagerViewModel
>(
    R.layout.activity_post_image_pager
) {

    companion object {
        const val INTENT_BUNDLE_IMAGE_URL_LIST = "image_url_list"
    }

    override val viewModel: PostImagePagerViewModel by lazy {
        ViewModelProvider(this).get(PostImagePagerViewModel::class.java)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize binding object.
        binding.apply {
            lifecycleOwner = this@PostImagePagerActivity
            viewpagerPostImages.addOnPageChangeListener(pageChangeListener)
        }

        // Prepares to collect data.
        lifecycleScope.launch {
            viewModel.flowImageURLs.collect(postImagePagerStateCollector)
        }

        // Loads data.
        val imageURLs = intent.getSerializableExtra(INTENT_BUNDLE_IMAGE_URL_LIST) as ArrayList<String>
        viewModel.updateImageURLs(imageURLs)
    }

    private val postImagePagerStateCollector = FlowCollector<PostImagePagerState> {
        when (it) {
            is PostImagePagerState.Loading -> {}
            is PostImagePagerState.Success -> {
                updateViewPager(it.imageURLList)
            }
            else -> {}
        }
    }

    private fun updateViewPager(imageURLs: ArrayList<String>) {
        val newAdapter = PostImagePagerAdapter(this, imageURLs)
        binding.viewpagerPostImages.adapter = newAdapter
        updateTitle(0)
    }

    private fun updateTitle(pagePosition: Int) {
        supportActionBar?.title = "Post Images (${pagePosition + 1} / ${binding.viewpagerPostImages.adapter?.count})"
    }

    private val pageChangeListener = object: OnPageChangeListener {
        override fun onPageScrolled(
            position: Int,
            positionOffset: Float,
            positionOffsetPixels: Int
        ) {
        }

        override fun onPageSelected(position: Int) {
            updateTitle(position)
        }

        override fun onPageScrollStateChanged(state: Int) {
        }

    }
}