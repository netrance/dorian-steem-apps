package lee.dorian.dorian_android_ktx.android.context

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.util.TypedValue
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity

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

fun Context.getCurrentFragment(id: Int): Fragment? {
    val activity = findActivity() as? FragmentActivity
    val navHostFragment = activity?.supportFragmentManager?.findFragmentById(id)
    return navHostFragment?.childFragmentManager?.fragments?.lastOrNull()
}