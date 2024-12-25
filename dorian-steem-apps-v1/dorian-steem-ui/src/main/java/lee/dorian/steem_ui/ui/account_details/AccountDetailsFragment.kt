package lee.dorian.steem_ui.ui.account_details

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import kotlinx.coroutines.launch
import lee.dorian.dorian_android_ktx.androidx.compose.ui.borderBottom
import lee.dorian.steem_domain.model.AccountDetails
import lee.dorian.steem_ui.model.State
import lee.dorian.steem_ui.ui.compose.ErrorOrFailure
import lee.dorian.steem_ui.ui.compose.Loading

class AccountDetailsFragment : Fragment() {

    private val args: AccountDetailsFragmentArgs by navArgs()

    private val viewModel by viewModels<AccountDetailsViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                AccountDetailsContent(viewModel)
            }

            viewLifecycleOwner.lifecycleScope.launch {
                viewModel.readAccountDetails(args.account)
            }
        }
    }

}

@Composable
fun AccountDetailsRow(name: String, value: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .borderBottom(2.dp, Color.Gray)
            .padding(8.dp)

    ) {
        Text(
            text = name
        )
        Text(
            textAlign = TextAlign.End,
            text = value,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
@Preview
fun PreviewAccountDetailsRow() {
    AccountDetailsRow("account", "dorian-mobileapp")
}

@Composable
fun AccountDetailsContent(viewModel: AccountDetailsViewModel) {
    val accountDetailsState = viewModel.accontDetailsState.collectAsState()
    AccountDetailsContent(accountDetailsState.value)
}

@Composable
fun AccountDetailsContent(state: State<AccountDetails>) {
    when (state) {
        is State.Empty -> Loading()
        is State.Loading -> Loading()
        is State.Success -> AccountDetailsLoaded(state.data)
        is State.Error, is State.Failure -> ErrorOrFailure()
    }
}

@Composable
@Preview(widthDp = 320, heightDp = 480)
fun PreviewAccountDetailsContent_Loading() {
    AccountDetailsContent(State.Loading)
}

@Composable
@Preview(widthDp = 320, heightDp = 480)
fun PreviewAccountDetailsContent_Success() {
    AccountDetailsContent(State.Loading)
}

@Composable
fun AccountDetailsLoaded(accountDetails: AccountDetails) {
    val items = listOf<Pair<String, String>>(
        Pair("ID", accountDetails.id),
        Pair("name", accountDetails.name),
        Pair("Recovery Account", accountDetails.recoveryAccount),
        Pair("Last Owner Update", accountDetails.lastOwnerUpdate),
        Pair("Last Account Update", accountDetails.lastAccountUpdate),
        Pair("Pending STEEM rewards", accountDetails.pendingSteemRewards),
        Pair("Pending SBD rewards", accountDetails.pendingSBDRewards),
        Pair("Pending SP rewards", accountDetails.pendingSPBalance),
        Pair("Post Count", accountDetails.postCount.toString()),
        Pair("Last Post Time", accountDetails.lastRootPost),
        Pair("Last Reply Time", accountDetails.lastPost),
        Pair("Last Vote Time", accountDetails.lastVoteTime),
        Pair("Voting Power", "${accountDetails.votingPower}%"),
        Pair("Upvoting Mana Rate", "${accountDetails.votingManaRate}%"),
        Pair("Downvoting Mana Rate", "${accountDetails.downvotignManaRate}%"),
        Pair("Witness Votes", accountDetails.witnessVotes.toString()),
        Pair("Proxy", accountDetails.proxy),
        Pair("Reputation", accountDetails.reputation),
    )

    LazyColumn {
        items(items.size) { i ->
            AccountDetailsRow(items[i].first, items[i].second)
        }
    }
}

@Composable
@Preview
fun PreviewAccountDetailsContent() {
    val accountDetails = AccountDetails(
        id = "1234",
        name = "sample",
        recoveryAccount = "steem"
    )
    AccountDetailsLoaded(accountDetails)
}