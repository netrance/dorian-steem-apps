package lee.dorian.steem_ui.ext

import android.widget.Toast
import androidx.fragment.app.Fragment

fun Fragment.showToastShortly(msg: String) {
    Toast.makeText(this.activity, msg, Toast.LENGTH_SHORT).show()
}