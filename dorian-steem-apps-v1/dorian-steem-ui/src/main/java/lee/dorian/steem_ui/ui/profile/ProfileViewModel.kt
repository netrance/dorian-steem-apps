package lee.dorian.steem_ui.ui.profile

import androidx.lifecycle.MutableLiveData
import lee.dorian.steem_ui.ui.base.BaseViewModel

class ProfileViewModel : BaseViewModel() {

    val text = MutableLiveData<String>().apply {
        value = "This is profile Fragment"
    }

}