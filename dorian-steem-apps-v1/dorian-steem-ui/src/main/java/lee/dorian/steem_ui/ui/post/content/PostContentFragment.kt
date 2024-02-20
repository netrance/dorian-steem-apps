package lee.dorian.steem_ui.ui.post.content

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebChromeClient
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexWrap
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.flexbox.JustifyContent
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.launch
import lee.dorian.dorian_android_ktx.android.context.setLeanBackFullscreen
import lee.dorian.dorian_android_ktx.android.context.unsetFullscreen
import lee.dorian.steem_ui.MainViewModel
import lee.dorian.steem_ui.R
import lee.dorian.steem_ui.databinding.FragmentPostBinding
import lee.dorian.steem_ui.ext.*
import lee.dorian.steem_ui.ui.base.BaseFragment

class PostContentFragment : BaseFragment<FragmentPostBinding, PostContentViewModel>(R.layout.fragment_post) {

    val args: PostContentFragmentArgs by navArgs()

    override val viewModel by lazy {
        ViewModelProvider(this).get(PostContentViewModel::class.java)
    }

    val activityViewModel by lazy {
        ViewModelProvider(requireActivity()).get(MainViewModel::class.java)
    }

    private var currentScrollY: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize views.
        binding.webContent.webChromeClient = webChromeClient
        binding.listTag.layoutManager.also {
            if (it is FlexboxLayoutManager) {
                it.flexDirection = FlexDirection.ROW
                it.flexWrap = FlexWrap.WRAP
                it.justifyContent = JustifyContent.CENTER
            }
        }

        // Sets listeners.
        binding.textUpvotes.setOnClickListener(textUpvotesClickListener)
        binding.textDownvotes.setOnClickListener(textDownvotesClickListener)

        lifecycleScope.launch {
            viewModel.flowPostState.collect(postContentStateCollector)
        }

        val author = args.author
        val permlink = args.permlink
        viewModel.initState(author, permlink)
    }

    private val textUpvotesClickListener = View.OnClickListener {
        val postState = viewModel.flowPostState.value
        if (postState is PostContentState.Success) {
            startUpvoteListActivity(postState.post.activeVotes)
        }
    }

    private val textDownvotesClickListener = View.OnClickListener {
        val postState = viewModel.flowPostState.value
        if (postState is PostContentState.Success) {
            startDownvoteListActivity(postState.post.activeVotes)
        }
    }

    private val postContentStateCollector = FlowCollector<PostContentState> { state ->
        when (state) {
            is PostContentState.Loading -> {
                binding.includeLoading.layoutLoading.visibility = View.VISIBLE
                binding.includeLoading.imageLoading.loadGif(lee.dorian.dorian_android_ktx.R.drawable.loading)
            }
            is PostContentState.Success -> {
                binding.includeLoading.layoutLoading.visibility = View.GONE
                binding.includeLoading.imageLoading.unload()

                binding.textTitle.text = state.post.title
                binding.imagePostAuthorProfile.load("https://steemitimages.com/u/${state.post.account}/avatar/small")
                binding.textPostAuthor.text = "${state.post.account} (${state.post.reputation})"
                binding.textPostCommunity.text = "${state.post.communityTitle}"
                binding.textPostTime.text = state.post.time
                binding.webContent.loadMarkdown(state.post.content)
                binding.textRewards.text = String.format("$%.3f", state.post.rewards)
                binding.textUpvotes.text = String.format("\uD83D\uDD3A%d", state.post.upvoteCount)
                binding.textDownvotes.text = String.format("\uD83D\uDD3B%d", state.post.downvoteCount)
                binding.textReplyCount.text = "\uD83D\uDCAC ${state.replies.size}"

                binding.listTag.adapter = TagListAdapter(state.post.tags)
            }
            else -> {
                showToastShortly(getString(R.string.error_cannot_load))
            }
        }

    }

    // To enable fullscreen of a Youtube video
    private val webChromeClient = object: WebChromeClient() {
        private var customView: View? = null
        private var originalScrollY: Int = 0

        override fun onShowCustomView(view: View?, callback: CustomViewCallback?) {
            super.onShowCustomView(view, callback)
            if (customView != null) { // 이미 Full Screen이 표시된 상황이라면 제거 이벤트 위로 전달
                callback?.onCustomViewHidden()
                return
            }

            originalScrollY = binding.scrollPostContent.scrollY
            customView = view // 제거할 때 참조하기 위해 변수에 저장
            binding.layoutPostContent.addView(
                customView,
                ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
            )
            activity?.setLeanBackFullscreen()
            if (activity is AppCompatActivity) {
                (activity as AppCompatActivity)?.supportActionBar?.hide()
            }
        }

        override fun onHideCustomView() {
            super.onHideCustomView()
            binding.layoutPostContent.removeView(customView)
            customView = null
            activity?.unsetFullscreen()
            binding.scrollPostContent.post {    // Used to prevent y position of scroll is reset to 0.
                binding.scrollPostContent.scrollTo(0, originalScrollY)
            }
            if (activity is AppCompatActivity) {
                (activity as AppCompatActivity)?.supportActionBar?.show()
            }
        }
    }
}