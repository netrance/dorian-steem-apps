package lee.dorian.steem_ui.ui.profile

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ProfileViewModel : ViewModel() {

    val text = MutableLiveData<String>().apply {
        value = "This is profile Fragment"
    }

}