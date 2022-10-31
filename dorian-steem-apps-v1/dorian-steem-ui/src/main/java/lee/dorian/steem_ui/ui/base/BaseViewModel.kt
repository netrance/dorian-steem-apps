package lee.dorian.steem_ui.ui.base

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.reactivex.disposables.CompositeDisposable

open class BaseViewModel: ViewModel() {

    val compositeDisposable = CompositeDisposable()

    // To handle exceptions from calling APIs
    val liveThrowable = MutableLiveData<Throwable>()

    override fun onCleared() {
        compositeDisposable.dispose()
        super.onCleared()
    }

}