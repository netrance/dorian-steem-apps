package lee.dorian.steem_ui.ext

import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment

fun Fragment.showToastShortly(msg: String) {
    Toast.makeText(this.activity, msg, Toast.LENGTH_SHORT).show()
}

fun Fragment.setActivityActionBarTitle(title: String) {
    if (activity is AppCompatActivity) {
        (activity as AppCompatActivity).supportActionBar?.title = title
    }
    else {
        activity?.actionBar?.title = title
    }
}