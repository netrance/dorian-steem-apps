package lee.dorian.steem_ui.ui.wallet

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import lee.dorian.dorian_android_ktx.android.context.showToastShortly
import lee.dorian.steem_domain.ext.fromUtcTimeToLocalTime
import lee.dorian.steem_domain.ext.toRelativeTimeString
import lee.dorian.steem_domain.model.VestingDelegation
import lee.dorian.steem_ui.model.State
import lee.dorian.steem_ui.ui.compose.AccountSearchTextField
import lee.dorian.steem_ui.ui.compose.ErrorOrFailure
import lee.dorian.steem_ui.ui.compose.Loading

@Composable
fun IncomingDelegationListScreen(
    onDelegatorClick: (account: String) -> Unit = {},
    viewModel: IncomingDelegationListViewModel = hiltViewModel()
) {
    val state by viewModel.flowIncomingDelegationList.collectAsStateWithLifecycle()
    IncomingDelegationListContent(state, onDelegatorClick)
}

@Composable
private fun IncomingDelegationListContent(
    state: State<List<VestingDelegation>>,
    onDelegatorClick: (account: String) -> Unit
) {
    val context = LocalContext.current
    when {
        state is State.Empty -> { }
        state is State.Loading -> Loading()
        state !is State.Success -> ErrorOrFailure()
        else -> {
            var searchQuery by remember { mutableStateOf("") }
            val list = (state as State.Success<List<VestingDelegation>>).data
            val filteredList = remember(list, searchQuery) {
                if (searchQuery.isEmpty()) list
                else list.filter { it.delegator.contains(searchQuery) }
            }
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.White)
            ) {
                AccountSearchTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it }
                )
                if (filteredList.isEmpty()) {
                    Text(
                        text = "No steem power is being delegated to you.",
                        color = Color.Gray,
                        fontSize = 16.sp,
                        modifier = Modifier.padding(16.dp)
                    )
                } else {
                    LazyColumn(modifier = Modifier.fillMaxSize()) {
                        items(filteredList.size) { index ->
                            IncomingDelegationItem(
                                delegation = filteredList[index],
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(if (index % 2 == 0) Color.LightGray else Color.White)
                                    .padding(10.dp),
                                onDelegatorClick = onDelegatorClick,
                                onDelegationTimeClick = { time ->
                                    context.showToastShortly(time.fromUtcTimeToLocalTime())
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun IncomingDelegationItem(
    delegation: VestingDelegation,
    modifier: Modifier,
    onDelegatorClick: (account: String) -> Unit = {},
    onDelegationTimeClick: (String) -> Unit = {}
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
    ) {
        AsyncImage(
            model = "https://steemitimages.com/u/${delegation.delegator}/avatar/small",
            contentDescription = "Profile image of ${delegation.delegator}",
            modifier = Modifier
                .width(50.dp)
                .height(50.dp)
                .clickable {}
        )
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = delegation.delegator,
                color = Color.Black,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.clickable { onDelegatorClick(delegation.delegator) }
            )
            Text(
                text = delegation.minDelegationTime.toRelativeTimeString(),
                color = Color.Gray,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.clickable { onDelegationTimeClick(delegation.minDelegationTime) }
            )
        }
        Text(
            text = delegation.steemPower,
            color = Color.Black,
            fontSize = 15.sp
        )
    }
}

@Composable
@Preview
fun IncomingDelegationListScreenPreview() {
    val sampleDelegations = listOf(
        VestingDelegation(delegator = "alice", steemPower = "500.000 SP", minDelegationTime = "2024-01-15T10:30:00"),
        VestingDelegation(delegator = "bob", steemPower = "200.000 SP", minDelegationTime = "2023-06-20T08:00:00"),
        VestingDelegation(delegator = "carol", steemPower = "1000.000 SP", minDelegationTime = "2025-12-01T15:45:00"),
    )
    IncomingDelegationListContent(
        state = State.Success(sampleDelegations),
        onDelegatorClick = {}
    )
}
