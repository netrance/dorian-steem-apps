package lee.dorian.steem_ui.ui.tags

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class TagsViewModel : ViewModel() {

    val text = MutableLiveData<String>().apply {
        value = "This is tags Fragment"
    }

}