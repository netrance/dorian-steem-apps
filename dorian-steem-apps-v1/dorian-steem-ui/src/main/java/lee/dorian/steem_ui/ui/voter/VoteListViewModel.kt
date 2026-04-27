package lee.dorian.steem_ui.ui.voter

import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import lee.dorian.steem_domain.model.ActiveVote
import lee.dorian.steem_ui.ui.base.BaseViewModel
import javax.inject.Inject

@HiltViewModel
class VoteListViewModel @Inject constructor() : BaseViewModel() {

    private val _votes = MutableStateFlow<List<ActiveVote>>(emptyList())
    val votes: StateFlow<List<ActiveVote>> = _votes.asStateFlow()

    fun setVotes(votes: List<ActiveVote>) {
        _votes.value = votes
    }

}
