package lee.dorian.dorian_android_ktx.android.view

import android.view.View
import android.view.ViewGroup
import lee.dorian.dorian_android_ktx.android.context.toPixelFromDP

fun View.setMargins(leftDP: Int, topDP: Int, rightDP: Int, bottomDP: Int) {
    if (layoutParams !is ViewGroup.MarginLayoutParams) {
        return
    }

    (layoutParams as ViewGroup.MarginLayoutParams).setMargins(
        context.toPixelFromDP(leftDP.toFloat()).toInt(),
        context.toPixelFromDP(topDP.toFloat()).toInt(),
        context.toPixelFromDP(rightDP.toFloat()).toInt(),
        context.toPixelFromDP(bottomDP.toFloat()).toInt()
    )
}