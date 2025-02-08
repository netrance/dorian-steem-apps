package lee.dorian.steem_ui.ui.post.content

import android.app.Activity
import android.view.View
import android.webkit.WebChromeClient
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.ScrollState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import lee.dorian.dorian_android_ktx.android.context.setLeanBackFullscreen
import lee.dorian.dorian_android_ktx.android.context.unsetFullscreen

class PostContentWebChromeClient(
    private val activity: Activity,
    private val scrollState: ScrollState,
    private val coroutineScope: CoroutineScope,
) : WebChromeClient() {

    private var isFullscreenMode = false
    private var customView: View? = null
    private val fullScreenContainer = activity.window?.decorView as? FrameLayout
    private var savedScrollY = 0

    override fun onShowCustomView(view: View?, callback: CustomViewCallback?) {
        super.onShowCustomView(view, callback)
        if (customView != null) { // 이미 Full Screen이 표시된 상황이라면 제거 이벤트 위로 전달
            onHideCustomView()
        }
        else {
            customView = view
            fullScreenContainer?.addView(view)
            isFullscreenMode = true
            savedScrollY = scrollState.value
            activity.setLeanBackFullscreen()
            if (activity is AppCompatActivity) {
                activity.supportActionBar?.hide()
            }
        }
    }

    override fun onHideCustomView() {
        super.onHideCustomView()
        customView?.let { fullScreenContainer?.removeView(it) }
        customView = null
        isFullscreenMode = false
        activity.unsetFullscreen()
        if (activity is AppCompatActivity) {
            activity.supportActionBar?.show()
        }
        coroutineScope.launch {
            delay(100)
            scrollState.scrollTo(savedScrollY)
        }
    }
}