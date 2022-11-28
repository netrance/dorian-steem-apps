package lee.dorian.steem_ui.ui.tags

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import lee.dorian.steem_data.model.post.GetRankedPostParamsDTO
import lee.dorian.steem_data.repository.SteemRepositoryImpl
import lee.dorian.steem_domain.model.PostItem
import lee.dorian.steem_domain.model.SteemitWallet
import lee.dorian.steem_domain.usecase.ReadRankedPostsUseCase
import lee.dorian.steem_ui.ui.base.BaseViewModel

class TagsViewModel : BaseViewModel() {

    // Will be deleted soon...
    val text by lazy {
        MutableLiveData("This is tags Fragment")
    }

    val limit = GetRankedPostParamsDTO.InnerParams.DEFAULT_LIMIT

    val sort = MutableLiveData("")
    val rankedPosts = MutableLiveData<MutableList<PostItem>>(mutableListOf())
    val readRankedPostsUseCase = ReadRankedPostsUseCase(SteemRepositoryImpl())

    fun readRankedPosts(
        tag: String,
        limit: Int = this.limit
    ) {
        rankedPosts.value = mutableListOf()

        readRankedPostsUseCase(
            sort.value ?: GetRankedPostParamsDTO.InnerParams.SORT_TRENDING,
            tag
        )
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .onErrorReturn { error ->
            error.printStackTrace()
            liveThrowable.value = error
            listOf()
        }
        .subscribe { rankedPostItemList ->
            rankedPosts.value = rankedPostItemList.toMutableList()
        }
        .also { disposable ->
            compositeDisposable.add(disposable)
        }
    }

    fun appendRankedPosts(
        tag: String,
        limit: Int = this.limit
    ) {
        val rankedPostsValue = rankedPosts.value ?: listOf()
        if (rankedPostsValue.size < GetRankedPostParamsDTO.InnerParams.DEFAULT_LIMIT) {
            return
        }

        val lastPostItem = when {
            (rankedPostsValue.isEmpty()) -> null
            else -> rankedPostsValue.last()
        } ?: return

        readRankedPostsUseCase(
            sort.value ?: GetRankedPostParamsDTO.InnerParams.SORT_TRENDING,
            tag,
            lastPostItem = lastPostItem
        )
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .onErrorReturn { error ->
            error.printStackTrace()
            liveThrowable.value = error
            listOf()
        }
        .subscribe { rankedPostItemList ->
            if (rankedPostsValue.isNotEmpty()) {
                rankedPosts.value?.addAll(rankedPostItemList)
            }
        }
        .also { disposable ->
            compositeDisposable.add(disposable)
        }
    }

}