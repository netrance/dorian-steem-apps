package lee.dorian.steem_ui.ui.voter

import androidx.lifecycle.MutableLiveData
import lee.dorian.steem_domain.model.ActiveVote
import lee.dorian.steem_ui.ui.base.BaseViewModel

class VoteListViewModel : BaseViewModel() {

    val votes: MutableLiveData<Array<ActiveVote>> = MutableLiveData(arrayOf())

}