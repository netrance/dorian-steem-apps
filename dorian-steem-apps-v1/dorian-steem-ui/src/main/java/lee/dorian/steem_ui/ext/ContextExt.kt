package lee.dorian.steem_ui.ext

import android.content.Context
import android.util.TypedValue

fun Context.toPixelFromDP(dp: Float): Float {
    return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, resources.displayMetrics)
}

fun Context.toDPFromDimension(dimenID: Int): Float {
    return resources.getDimension(dimenID) / resources.displayMetrics.density
}