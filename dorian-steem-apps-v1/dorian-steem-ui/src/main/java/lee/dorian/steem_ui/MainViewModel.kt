package lee.dorian.steem_ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData

class MainViewModel(val app: Application) : AndroidViewModel(app) {

    fun readTitle(destID: Int): String {
        val appName = app.getString(R.string.app_name)
        return when (destID) {
            R.id.navigation_tags -> "${appName} - ${app.getString(R.string.title_tags)}"
            R.id.navigation_profile -> "${appName} - ${app.getString(R.string.title_profile)}"
            R.id.navigation_wallet -> "${appName} - ${app.getString(R.string.title_wallet)}"
            else -> "${appName}"
        }
    }

}