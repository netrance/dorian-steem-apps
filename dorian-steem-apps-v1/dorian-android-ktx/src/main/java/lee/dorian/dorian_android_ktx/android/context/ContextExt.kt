package lee.dorian.steem_ui.ext

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.util.TypedValue

fun Context.findActivity(): Activity? = when (this) {
    is Activity -> this
    is ContextWrapper -> baseContext.findActivity()
    else -> null
}

fun Context.toPixelFromDP(dp: Float): Float {
    return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, resources.displayMetrics)
}

fun Context.toDPFromDimension(dimenID: Int): Float {
    return resources.getDimension(dimenID) / resources.displayMetrics.density
}