package lee.dorian.steem_ui.ui.base

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

open class BaseViewModel: ViewModel() {

    // To handle exceptions from calling APIs
    val liveThrowable = MutableLiveData<Throwable>()

}