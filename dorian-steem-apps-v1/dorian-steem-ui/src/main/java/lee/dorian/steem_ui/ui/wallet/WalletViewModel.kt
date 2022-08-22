package lee.dorian.steem_ui.ui.wallet

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class WalletViewModel : ViewModel() {

    val text = MutableLiveData<String>().apply {
        value = "This is wallet Fragment"
    }

}