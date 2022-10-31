package lee.dorian.steem_ui.ui.wallet

import androidx.lifecycle.MutableLiveData
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import lee.dorian.steem_data.repository.SteemRepositoryImpl
import lee.dorian.steem_domain.model.SteemitWallet
import lee.dorian.steem_domain.usecase.ReadSteemitWalletUseCase
import lee.dorian.steem_ui.ui.base.BaseViewModel

class WalletViewModel : BaseViewModel() {

    val steemitWallet = MutableLiveData(SteemitWallet())
    val readSteemitWalletUseCase = ReadSteemitWalletUseCase(SteemRepositoryImpl())

    fun readSteemitWallet(account: String) {
        readSteemitWalletUseCase(account)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .onErrorReturn { error ->
                error.printStackTrace()
                liveThrowable.value = error
                arrayOf(SteemitWallet())
            }
            .subscribe { steemitWallets ->
                steemitWallet.value = when {
                    (steemitWallets.isNotEmpty()) -> steemitWallets[0]
                    else -> SteemitWallet()
                }
            }
            .also { disposable ->
                compositeDisposable.add(disposable)
            }
    }
}