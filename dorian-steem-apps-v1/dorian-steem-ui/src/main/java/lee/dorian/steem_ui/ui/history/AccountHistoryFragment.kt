@file:OptIn(ExperimentalMaterial3Api::class)

package lee.dorian.steem_ui.ui.history

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import lee.dorian.dorian_android_ktx.androidx.compose.foundation.lazy.AppendableLazyColumn
import lee.dorian.steem_domain.model.AccountHistory
import lee.dorian.steem_domain.model.AccountHistoryItem
import lee.dorian.steem_domain.model.AccountHistoryItemLink
import lee.dorian.steem_ui.MainViewModel
import lee.dorian.steem_ui.ext.setActivityActionBarTitle
import lee.dorian.steem_ui.model.State
import lee.dorian.steem_ui.ui.compose.ErrorOrFailure
import lee.dorian.steem_ui.ui.compose.Loading

class AccountHistoryFragment : Fragment() {

    private val args: AccountHistoryFragmentArgs by navArgs()

    val viewModel by lazy {
        ViewModelProvider(this).get(AccountHistoryViewModel::class.java)
    }

    val activityViewModel by lazy {
        ViewModelProvider(requireActivity()).get(MainViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return ComposeView(requireContext()).apply {
            setContent {
                AccountHistoryScreen(viewModel, args.author) { accountHistoryItemLink ->
                    onMenuItemClick(accountHistoryItemLink)
                }
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    fun onMenuItemClick(accountHistoryItemLink: AccountHistoryItemLink) {
        when (accountHistoryItemLink.type) {
            "profile" -> {
                val account = accountHistoryItemLink.link.replace("@", "")
                val action = AccountHistoryFragmentDirections.actionNavigationAccountHistoryToNavigationProfile(account)
                findNavController().navigate(action)
                setActivityActionBarTitle("Profile of @${accountHistoryItemLink.link}")
            }
            "post" -> {
                val linkElements = accountHistoryItemLink.link.split("/")
                val author = linkElements[0].replace("@", "")
                val permlink = linkElements[1]
                val action = AccountHistoryFragmentDirections.actionNavigationAccountHistoryToNavigationPost(
                    author, permlink
                )
                findNavController().navigate(action)
                setActivityActionBarTitle("Posts of @${author}")
            }
            "wallet" -> {
                val account = accountHistoryItemLink.link.replace("@", "")
                val action = AccountHistoryFragmentDirections.actionNavigationAccountHistoryToNavigationWallet(account)
                findNavController().navigate(action)
                setActivityActionBarTitle("Wallet of @${account}")
            }
        }
    }

}

@Composable
fun AccountHistoryScreen(
    viewModel: AccountHistoryViewModel,
    account: String,
    onMenuItemClick: (AccountHistoryItemLink) -> Unit
) {
    val state by viewModel.flowState.collectAsStateWithLifecycle()
    val isRefreshing by viewModel.flowIsRefreshing.collectAsStateWithLifecycle()

    if (state is State.Empty) {
        viewModel.readAccountHistory(account)
        return
    }
    else if (state is State.Loading) {
        Loading()
        return
    }
    else if (state !is State.Success<AccountHistory>) {
        ErrorOrFailure()
        return
    }

    val pullRefreshState = rememberPullToRefreshState()

    PullToRefreshBox(
        state = pullRefreshState,
        isRefreshing = isRefreshing,
        onRefresh = {
            viewModel.refreshAccountHistory()
        }
    ) {
        val accountHistory = (state as State.Success<AccountHistory>).data
        AccountHistoryItemList(accountHistory, viewModel, onMenuItemClick)
    }
}

@Composable
fun AccountHistoryItemList(
    accountHistory: AccountHistory,
    viewModel: AccountHistoryViewModel,
    onMenuItemClick: (AccountHistoryItemLink) -> Unit
) {
    AppendableLazyColumn(
        onAppend = { viewModel.appendAccountHistory() },
        modifier = Modifier.fillMaxSize()
    ) {
        items(accountHistory.historyList.size) { index ->
            AccountHistoryItem(
                accountHistory.historyList[index],
                if ((index % 2) == 0) Color.White else Color.LightGray,
                onMenuItemClick
            )
        }
    }
}

@Composable
@Preview
fun AccountHistoryItemListPreview() {
    AccountHistoryItemList(
        AccountHistory(
            "test-account",
            listOf(
                sampleAccountHistoryItem,
                sampleAccountHistoryItem,
                sampleAccountHistoryItem
            )
        ),
        AccountHistoryViewModel(),
        {}
    )
}

@Composable
fun AccountHistoryItem(
    item: AccountHistoryItem,
    backgroundColor: Color = Color.White,
    onMenuItemClick: (AccountHistoryItemLink) -> Unit
) {
    var isDropdownMenuOpen by remember { mutableStateOf(false) }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(backgroundColor)
            .clickable {
                isDropdownMenuOpen = true
            }
            .padding(all = 12.dp)
    ) {
        Text(
            text = item.type,
            style = TextStyle(
                color = Color.Black,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            ),
            modifier = Modifier.fillMaxWidth()
        )
        Text(
            text = item.getUserReadableContent(),
            style = TextStyle(
                color = Color.Black,
                fontSize = 14.sp
            ),
            modifier = Modifier.fillMaxWidth().padding(top = 5.dp)
        )
        Text(
            text = item.localTime,
            textAlign = TextAlign.End,
            style = TextStyle(
                color = Color.DarkGray,
                fontSize = 12.sp
            ),
            modifier = Modifier.fillMaxWidth().padding(top = 5.dp)
        )
    }

    if (isDropdownMenuOpen) {
        // DropdownMenu 컴포저블 함수를 활용하여 팝업 메뉴를 구현하면 된다. (2025. 02. 22 예정)
        val accountHistoryItemLinks = item.getLinkList()

        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            DropdownMenu(
                expanded = isDropdownMenuOpen,
                onDismissRequest = { isDropdownMenuOpen = false },
                modifier = Modifier.background(Color.DarkGray)
            ) {
                accountHistoryItemLinks.forEach {
                    DropdownMenuItem(
                        text = {
                            Text(text = it.title, color = Color.White, fontSize = 16.sp)
                        },
                        onClick = { onMenuItemClick(it) }
                    )
                }
            }
        }
    }
}

@Composable
@Preview
fun AccountHistoryItemPreview() {
    AccountHistoryItem(sampleAccountHistoryItem) {}
}

private val sampleAccountHistoryItem = AccountHistoryItem(
    0,
    "2025-02-13 12:34:56",
    "vote",
    mapOf(
        "voter" to "dorian-mobileapp",
        "author" to "dorian-mobileapp",
        "permlink" to "abcd",
        "weight" to 10000
    )
)
