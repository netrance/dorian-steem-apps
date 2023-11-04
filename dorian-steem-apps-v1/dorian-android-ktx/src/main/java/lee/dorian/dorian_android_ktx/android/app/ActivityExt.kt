package lee.dorian.dorian_android_ktx.android.context

import android.app.Activity
import android.content.Context
import android.os.Build
import android.view.View
import android.view.WindowInsets
import android.view.inputmethod.InputMethodManager

fun Activity.hideKeyboard(view: View) {
    val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
}

// Refer to https://developer.android.com/training/system-ui/immersive
fun Activity.setLeanBackFullscreen() {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R) {
        window?.decorView?.let {
            it.systemUiVisibility = it.systemUiVisibility
                .or(View.SYSTEM_UI_FLAG_FULLSCREEN)
                .or(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION)
        }
    }
    else {
        window?.insetsController?.let {
            it.hide(WindowInsets.Type.statusBars() or WindowInsets.Type.navigationBars())
        }
    }
}

fun Activity.unsetFullscreen() {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R) {
        window?.decorView?.let {
            it.systemUiVisibility = it.systemUiVisibility
                .and(View.SYSTEM_UI_FLAG_FULLSCREEN.inv())
                .and(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION.inv())
        }
    }
    else {
        window?.insetsController?.let {
            it.show(WindowInsets.Type.statusBars() or WindowInsets.Type.navigationBars())
        }
    }
}