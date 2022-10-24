package lee.dorian.steem_ui.ui.tags

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import lee.dorian.steem_ui.ui.base.BaseViewModel

class TagsViewModel : BaseViewModel() {

    val text = MutableLiveData<String>().apply {
        value = "This is tags Fragment"
    }

}