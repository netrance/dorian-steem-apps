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
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import lee.dorian.steem_ui.R
import androidx.compose.ui.text.font.FontWeight
import lee.dorian.dorian_android_ktx.android.context.showToastShortly
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import lee.dorian.dorian_ktx.fromUtcTimeToLocalTime
import lee.dorian.dorian_ktx.toRelativeTimeString
import lee.dorian.steem_domain.model.ExpiringVestingDelegation
import lee.dorian.steem_domain.model.VestingDelegation
import lee.dorian.steem_ui.model.State
import lee.dorian.steem_ui.ui.compose.AccountSearchTextField
import lee.dorian.steem_ui.ui.compose.ErrorOrFailure
import lee.dorian.steem_ui.ui.compose.Loading

@Composable
fun OutgoingDelegationListScreen(
    onDelegateeClick: (account: String) -> Unit = {},
    viewModel: OutgoingDelegationListViewModel = hiltViewModel()
) {
    val outgoingState by viewModel.flowOutgoingDelegatingList.collectAsStateWithLifecycle()
    val expiringState by viewModel.flowExpiringDelegationList.collectAsStateWithLifecycle()
    var selectedTabIndex by remember { mutableIntStateOf(0) }

    Column(modifier = Modifier.fillMaxSize()) {
        TabRow(
            selectedTabIndex = selectedTabIndex,
            contentColor = Color.Black,
            indicator = { tabPositions ->
                TabRowDefaults.SecondaryIndicator(
                    modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTabIndex]),
                    color = Color.Black
                )
            }
        ) {
            Tab(
                selected = selectedTabIndex == 0,
                onClick = { selectedTabIndex = 0 },
                text = { Text(stringResource(R.string.outgoing_delegation_tab_delegating)) },
                selectedContentColor = Color.Black,
                unselectedContentColor = Color.Black,
            )
            Tab(
                selected = selectedTabIndex == 1,
                onClick = { selectedTabIndex = 1 },
                text = { Text(stringResource(R.string.outgoing_delegation_tab_expiring)) },
                selectedContentColor = Color.Black,
                unselectedContentColor = Color.Black
            )
        }
        when (selectedTabIndex) {
            0 -> OutgoingDelegationListContent(outgoingState, onDelegateeClick)
            else -> ExpiringDelegationListContent(expiringState, onDelegateeClick)
        }
    }
}

@Composable
private fun OutgoingDelegationListContent(
    state: State<List<VestingDelegation>>,
    onDelegateeClick: (account: String) -> Unit
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
                else list.filter { it.delegatee.contains(searchQuery) }
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
                        text = "No steem power is being delegated.",
                        color = Color.Gray,
                        fontSize = 16.sp,
                        modifier = Modifier.padding(16.dp)
                    )
                } else {
                    LazyColumn(modifier = Modifier.fillMaxSize()) {
                        items(filteredList.size) { index ->
                            OutgoingDelegationItem(
                                delegation = filteredList[index],
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(if (index % 2 == 0) Color.LightGray else Color.White)
                                    .padding(10.dp),
                                onDelegateeClick = onDelegateeClick,
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
@Preview
fun OutgoingDelegationListContentPreview() {
    val sampleDelegations = listOf(
        VestingDelegation(delegatee = "alice", steemPower = "500.000 SP", minDelegationTime = "2024-01-15T10:30:00"),
        VestingDelegation(delegatee = "bob", steemPower = "200.000 SP", minDelegationTime = "2023-06-20T08:00:00"),
        VestingDelegation(delegatee = "carol", steemPower = "1000.000 SP", minDelegationTime = "2025-12-01T15:45:00"),
    )
    OutgoingDelegationListContent(
        state = State.Success(sampleDelegations),
        onDelegateeClick = {}
    )
}

@Composable
private fun ExpiringDelegationListContent(
    state: State<List<ExpiringVestingDelegation>>,
    onDelegateeClick: (account: String) -> Unit
) {
    when {
        state is State.Empty -> { }
        state is State.Loading -> Loading()
        state !is State.Success -> ErrorOrFailure()
        else -> {
            var searchQuery by remember { mutableStateOf("") }
            val list = (state as State.Success<List<ExpiringVestingDelegation>>).data
            val filteredList = remember(list, searchQuery) {
                if (searchQuery.isEmpty()) list
                else list.filter { it.delegatee.contains(searchQuery) }
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
                        text = "No steem power is being withdrawn.",
                        color = Color.Gray,
                        fontSize = 16.sp,
                        modifier = Modifier.padding(16.dp)
                    )
                } else {
                    LazyColumn(modifier = Modifier.fillMaxSize()) {
                        items(filteredList.size) { index ->
                            ExpiringDelegationItem(
                                delegation = filteredList[index],
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(if (index % 2 == 0) Color.LightGray else Color.White)
                                    .padding(10.dp),
                                onDelegateeClick = onDelegateeClick
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
@Preview
fun ExpiringDelegationListContentPreview() {
    val sampleExpiringDelegations = listOf(
        ExpiringVestingDelegation(delegatee = "alice", steemPower = "300.000 SP", expiration = "2026-07-20T00:00:00"),
        ExpiringVestingDelegation(delegatee = "bob", steemPower = "150.000 SP", expiration = "2026-07-25T12:00:00"),
    )
    ExpiringDelegationListContent(
        state = State.Success(sampleExpiringDelegations),
        onDelegateeClick = {}
    )
}

@Composable
fun OutgoingDelegationItem(
    delegation: VestingDelegation,
    modifier: Modifier,
    onDelegateeClick: (account: String) -> Unit = {},
    onDelegationTimeClick: (String) -> Unit = {}
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
    ) {
        AsyncImage(
            model = "https://steemitimages.com/u/${delegation.delegatee}/avatar/small",
            contentDescription = "Profile image of ${delegation.delegatee}",
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
                text = delegation.delegatee,
                color = Color.Black,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.clickable { onDelegateeClick(delegation.delegatee) }
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
fun ExpiringDelegationItem(
    delegation: ExpiringVestingDelegation,
    modifier: Modifier,
    onDelegateeClick: (account: String) -> Unit = {}
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
    ) {
        AsyncImage(
            model = "https://steemitimages.com/u/${delegation.delegatee}/avatar/small",
            contentDescription = "Profile image of ${delegation.delegatee}",
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
                text = delegation.delegatee,
                color = Color.Black,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.clickable { onDelegateeClick(delegation.delegatee) }
            )
            Text(
                text = delegation.expiration.fromUtcTimeToLocalTime(),
                color = Color.Gray,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
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
fun OutgoingDelegationItemPreview() {
    val sampleDelegation = VestingDelegation(
        delegatee = "dorian-mobileapp",
        steemPower = "123456 SP",
        minDelegationTime = "2026-06-21 12:34:56"
    )
    OutgoingDelegationItem(
        delegation = sampleDelegation,
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(10.dp)
    )
}

@Composable
@Preview
fun ExpiringDelegationItemPreview() {
    val sampleDelegation = ExpiringVestingDelegation(
        delegatee = "dorian-mobileapp",
        steemPower = "100.000 SP",
        expiration = "2026-07-15T00:00:00"
    )
    ExpiringDelegationItem(
        delegation = sampleDelegation,
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(10.dp)
    )
}
