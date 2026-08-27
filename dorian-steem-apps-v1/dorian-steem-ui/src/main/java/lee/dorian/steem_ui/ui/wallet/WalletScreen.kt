package lee.dorian.steem_ui.ui.wallet

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.Icon
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import lee.dorian.steem_domain.model.SteemitWallet
import lee.dorian.steem_domain.model.Transfer
import lee.dorian.steem_ui.model.State
import lee.dorian.steem_ui.ui.compose.AccountInputForm
import lee.dorian.steem_ui.ui.compose.ErrorOrFailure
import lee.dorian.steem_ui.ui.compose.Loading

private val walletTabList = WalletTabInfo.entries

@Composable
fun SteemitWalletScreen(
    initialAccount: String,
    onDelegatingClick: (account: String) -> Unit = {},
    onDelegatedClick: (account: String) -> Unit = {},
    viewModel: WalletViewModel = hiltViewModel()
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    val state by viewModel.flowWalletState.collectAsStateWithLifecycle()

    LaunchedEffect(initialAccount) {
        if (initialAccount.isNotEmpty()) {
            viewModel.readSteemitWallet(initialAccount)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        if (initialAccount.isEmpty()) {
            AccountInputForm("Input a Steemit account.") { account ->
                if (account.length > 2) {
                    viewModel.readSteemitWallet(account)
                    keyboardController?.hide()
                }
            }
        }

        val commonModifier = Modifier.fillMaxWidth().weight(1f).background(Color.White)
        when (state) {
            is State.Empty -> WalletEmpty(modifier = commonModifier)
            is State.Loading -> Loading(modifier = commonModifier)
            !is State.Success -> ErrorOrFailure()
            else -> {
                val wallet = (state as State.Success<SteemitWallet>).data
                SteemitWalletContent(
                    wallet,
                    Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .background(Color.White),
                    onDelegatingClick = { onDelegatingClick(wallet.account) },
                    onDelegatedClick = { onDelegatedClick(wallet.account) }
                )
            }
        }
    }
}

@Composable
fun WalletEmpty(modifier: Modifier) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
    ) {
        Text(
            text = "Input a Steemit account.",
            style = TextStyle(
                color = Color.Black,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )
        )
    }
}

@Composable
@Preview
fun WalletEmptyPreview() {
    WalletEmpty(modifier = Modifier.fillMaxSize().background(Color.White))
}

@Composable
fun SteemitWalletContent(
    wallet: SteemitWallet,
    modifier: Modifier,
    onDelegatingClick: () -> Unit,
    onDelegatedClick: () -> Unit
) {
    var selectedTabIndex by rememberSaveable { mutableStateOf(0) }

    Column(
        modifier = modifier
    ) {
        WalletTabRow(selectedTabIndex) { index ->
            selectedTabIndex = index
        }

        val contentModifier = Modifier
            .fillMaxWidth()
            .weight(1f)
        when (walletTabList[selectedTabIndex]) {
            WalletTabInfo.BALANCE -> BalanceTabContent(
                wallet,
                contentModifier.verticalScroll(rememberScrollState()),
                onDelegatingClick,
                onDelegatedClick
            )
            WalletTabInfo.SENT -> SentTransferTabContent(wallet.account, contentModifier)
            WalletTabInfo.RECEIVED -> ReceivedTransferTabContent(wallet.account, contentModifier)
        }
    }
}

@Composable
@Preview
fun SteemitWalletContentPreview() {
    SteemitWalletContent(
        walletForTest,
        Modifier.fillMaxSize(),
        onDelegatingClick = {},
        onDelegatedClick = {}
    )
}

@Composable
fun WalletTabRow(
    selectedTabIndex: Int,
    onTabSelected: (index: Int) -> Unit
) {
    TabRow(
        selectedTabIndex = selectedTabIndex,
        containerColor = Color.White,
        contentColor = Color.Black,
        indicator = { tabPositions ->
            TabRowDefaults.SecondaryIndicator(
                Modifier.tabIndicatorOffset(tabPositions[selectedTabIndex]),
                color = Color.Black
            )
        },
        modifier = Modifier.fillMaxWidth()
    ) {
        walletTabList.forEachIndexed { index, walletTabInfo ->
            Tab(
                selected = (selectedTabIndex == index),
                onClick = {
                    onTabSelected(index)
                },
                text = { Text(text = walletTabInfo.title) }
            )
        }
    }
}

@Composable
@Preview
fun WalletTabRowPreview() {
    WalletTabRow(0) {}
}

@Composable
fun BalanceTabContent(
    wallet: SteemitWallet,
    modifier: Modifier,
    onDelegatingClick: () -> Unit,
    onDelegatedClick: () -> Unit
) {
    Column(
        modifier = modifier
    ) {
        val cardModifier = Modifier
            .fillMaxWidth()
            .padding(start = 8.dp, end = 8.dp, top = 8.dp)
        WalletBalances(wallet, cardModifier)
        WalletStakingCard(wallet, cardModifier, onDelegatingClick, onDelegatedClick)
        WalletSavings(wallet, cardModifier)
        WalletPowerDown(wallet, cardModifier)
    }
}

@Composable
@Preview
fun BalanceTabContentPreview() {
    BalanceTabContent(
        walletForTest,
        Modifier.fillMaxWidth(),
        onDelegatingClick = {},
        onDelegatedClick = {}
    )
}

@Composable
fun SentTransferTabContent(
    account: String,
    modifier: Modifier,
    viewModel: WalletViewModel = hiltViewModel()
) {
    val state by viewModel.flowSentTransfersState.collectAsStateWithLifecycle()

    LaunchedEffect(account) {
        if (account.isNotEmpty()) {
            viewModel.readSentTransfers(account)
        }
    }

    when (state) {
        is State.Empty, is State.Loading -> Loading(modifier = modifier)
        !is State.Success -> ErrorOrFailure()
        else -> TransferList((state as State.Success<List<Transfer>>).data, modifier, isSent = true)
    }
}

@Composable
fun ReceivedTransferTabContent(
    account: String,
    modifier: Modifier,
    viewModel: WalletViewModel = hiltViewModel()
) {
    val state by viewModel.flowReceivedTransfersState.collectAsStateWithLifecycle()

    LaunchedEffect(account) {
        if (account.isNotEmpty()) {
            viewModel.readReceivedTransfers(account)
        }
    }

    when (state) {
        is State.Empty, is State.Loading -> Loading(modifier = modifier)
        !is State.Success -> ErrorOrFailure()
        else -> TransferList((state as State.Success<List<Transfer>>).data, modifier, isSent = false)
    }
}

@Composable
fun TransferList(transferList: List<Transfer>, modifier: Modifier, isSent: Boolean) {
    LazyColumn(
        modifier = modifier
    ) {
        items(transferList.size) { index ->
            TransferItem(
                transfer = transferList[index],
                isSent = isSent,
                modifier = Modifier
                    .fillMaxWidth()
                    .background(if (index % 2 == 0) Color.LightGray else Color.White)
                    .padding(10.dp)
            )
        }
    }
}

@Composable
@Preview
fun TransferListPreview() {
    TransferList(transferListForTest, Modifier.fillMaxWidth(), isSent = true)
}

// A sent transfer is shown with its receiver, a received one with its sender.
@Composable
fun TransferItem(transfer: Transfer, isSent: Boolean, modifier: Modifier) {
    Column(
        modifier = modifier
    ) {
        val contentTextStyle = TextStyle(color = Color.Black, fontSize = 16.sp)
        val account = when {
            isSent -> "to: ${transfer.to}"
            else -> "from: ${transfer.from}"
        }
        Text(text = account, style = contentTextStyle)
        Text(text = "amount: ${transfer.amount}", style = contentTextStyle)
        Text(text = "time: ${transfer.time}", style = contentTextStyle)
        Text(text = "memo: ${transfer.memo}", style = contentTextStyle)
    }
}

@Composable
@Preview
fun TransferItemPreview() {
    TransferItem(transferListForTest[0], isSent = true, modifier = Modifier.fillMaxWidth())
}

@Composable
@Preview
fun ReceivedTransferItemPreview() {
    TransferItem(transferListForTest[0], isSent = false, modifier = Modifier.fillMaxWidth())
}

@Composable
fun WalletBalances(wallet: SteemitWallet, modifier: Modifier) {
    TitleContentCard(
        title = "Balances",
        contents = listOf(
            Pair("STEEM:", wallet.steemBalance),
            Pair("STEEM DOLLAR:", wallet.sbdBalance)
        ),
        modifier = modifier
    )
}

@Composable
@Preview
fun WalletBalancesPreview() {
    WalletBalances(walletForTest, Modifier.fillMaxWidth())
}

@Composable
fun WalletStakingCard(
    wallet: SteemitWallet,
    modifier: Modifier,
    onDelegatingClick: () -> Unit,
    onDelegatedClick: () -> Unit
) {
    Column(
        modifier = modifier
            .background(color = Color.LightGray, shape = RoundedCornerShape(12.dp))
            .padding(12.dp)
    ) {
        Text(
            text = "Staking",
            style = TextStyle(fontSize = 24.sp, fontWeight = FontWeight.Bold),
            modifier = Modifier.padding(bottom = 3.dp)
        )

        val contentTextStyle = TextStyle(fontSize = 16.sp)

        Row(modifier = Modifier.fillMaxWidth().padding(top = 6.dp)) {
            Text(text = "STEEM POWER:", style = contentTextStyle, modifier = Modifier.weight(1f))
            Text(text = wallet.steemPower, style = contentTextStyle)
        }
        Row(modifier = Modifier.fillMaxWidth().padding(top = 6.dp)) {
            Text(text = " - Effective SP:", style = contentTextStyle, modifier = Modifier.weight(1f))
            Text(text = wallet.effectiveSteemPower, style = contentTextStyle)
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 6.dp)
                .clickable { onDelegatingClick() },
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = " - Delegating:", style = contentTextStyle, modifier = Modifier.weight(1f))
            Text(text = wallet.delegatedSteemPower, style = contentTextStyle)
            Icon(
                imageVector = Icons.Default.Visibility,
                contentDescription = "View delegating list",
                modifier = Modifier.padding(start = 4.dp).size(20.dp)
            )
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 6.dp)
                .clickable { onDelegatedClick() },
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = " - Delegated:", style = contentTextStyle, modifier = Modifier.weight(1f))
            Text(text = wallet.receivedSteemPower, style = contentTextStyle)
            Icon(
                imageVector = Icons.Default.Visibility,
                contentDescription = "View delegated list",
                modifier = Modifier.padding(start = 4.dp).size(20.dp)
            )
        }
    }
}

@Composable
@Preview
fun WalletStakingCardPreview() {
    WalletStakingCard(
        walletForTest,
        Modifier.fillMaxWidth(),
        onDelegatingClick = {},
        onDelegatedClick = {}
    )
}

@Composable
fun WalletSavings(wallet: SteemitWallet, modifier: Modifier) {
    TitleContentCard(
        title = "Savings",
        contents = listOf(
            Pair("STEEM:", wallet.savingSteemBalance),
            Pair("STEEM DOLLAR:", wallet.savingSbdBalance)
        ),
        modifier = modifier
    )
}

@Composable
@Preview
fun WalletSavingsPreview() {
    WalletSavings(walletForTest, Modifier.fillMaxWidth())
}

@Composable
fun WalletPowerDown(wallet: SteemitWallet, modifier: Modifier) {
    TitleContentCard(
        title = "Power Down",
        contents = listOf(
            Pair("SP to power down", wallet.totalSPToBeWithdrawn),
            Pair("Power down rate:", wallet.spWithdrawRate),
            Pair("Remaing SP:", wallet.remainingSPToBeWithdrawn),
            Pair("Next power down:", wallet.nextPowerDownTime)
        ),
        modifier = modifier
    )
}

@Composable
@Preview
fun WalletPowerDownPreview() {
    WalletPowerDown(walletForTest, Modifier.fillMaxWidth())
}

@Composable
fun TitleContentCard(title: String, contents: List<Pair<String, String>>, modifier: Modifier) {
    Column(
        modifier = modifier
            .background(color = Color.LightGray, shape = RoundedCornerShape(12.dp))
            .padding(12.dp)
    ) {
        Text(
            text = title,
            style = TextStyle(fontSize = 24.sp, fontWeight = FontWeight.Bold),
            modifier = Modifier.padding(bottom = 3.dp)
        )

        val contentTextStyle = TextStyle(fontSize = 16.sp)
        contents.forEach {
            Row(modifier = Modifier.fillMaxWidth().padding(top = 6.dp)) {
                Text(text = it.first, style = contentTextStyle, modifier = Modifier.weight(1f))
                Text(text = it.second, style = contentTextStyle)
            }
        }
    }
}

@Composable
@Preview
fun TitleContentCardPreview() {
    TitleContentCard(
        "Title",
        listOf(
            Pair("item1", "value1"),
            Pair("item2", "value2")
        ),
        Modifier.fillMaxWidth()
    )
}

private val walletForTest by lazy {
    SteemitWallet(
        account = "test-account",
        steemBalance = "123 STEEM",
        sbdBalance = "123 SBD",
        savingSteemBalance = "0.123 STEEM",
        savingSbdBalance = "0.456 SBD",
        steemPower = "123456 SP",
        effectiveSteemPower = "456 SP",
        delegatedSteemPower = "123000 SP",
        receivedSteemPower = "0 SP",
        spWithdrawRate = "0 SP",
        totalSPToBeWithdrawn = "0 SP",
        remainingSPToBeWithdrawn = "0 SP",
        nextPowerDownTime = ""
    )
}

private val transferListForTest by lazy {
    listOf(
        Transfer(
            time = "2025-12-01 15:45",
            from = "test-account",
            to = "alice",
            amount = "18.868 STEEM",
            memo = "Thanks for your support!"
        ),
        Transfer(
            time = "2025-11-28 09:12",
            from = "test-account",
            to = "bob",
            amount = "3.500 SBD",
            memo = ""
        )
    )
}
