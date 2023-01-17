package lee.dorian.steem_ui.ui.profile

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import lee.dorian.steem_ui.ui.base.BaseViewModel

class ProfileImageViewModel : BaseViewModel() {

    val steemitAccount = MutableLiveData<String>("")

}