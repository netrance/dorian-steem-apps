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
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import lee.dorian.steem_domain.model.Reward
import lee.dorian.steem_domain.model.RewardType
import lee.dorian.steem_domain.model.SteemitWallet
import lee.dorian.steem_domain.model.Transfer
import lee.dorian.steem_ui.model.State
import lee.dorian.steem_ui.ui.compose.AccountInputForm
import lee.dorian.steem_ui.ui.compose.ErrorOrFailure
import lee.dorian.steem_ui.ui.compose.Loading
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone

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
            WalletTabInfo.REWARDS -> RewardsTabContent(wallet.account, contentModifier)
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
fun RewardsTabContent(
    account: String,
    modifier: Modifier,
    viewModel: WalletViewModel = hiltViewModel()
) {
    val state by viewModel.flowRewardsState.collectAsStateWithLifecycle()
    var selectedRewardType by rememberSaveable { mutableStateOf(RewardType.AUTHOR) }
    // The list is reported latest first, so it starts at today and ends three months back.
    var startDate by rememberSaveable { mutableStateOf(datePickerMillisOfToday()) }
    var endDate by rememberSaveable { mutableStateOf(datePickerMillisOfToday(monthOffset = -3)) }

    LaunchedEffect(account, selectedRewardType, startDate, endDate) {
        if (account.isNotEmpty()) {
            val (fromTime, toTime) = rewardTimeRange(startDate, endDate)
            viewModel.readRewards(account, selectedRewardType, fromTime, toTime)
        }
    }

    Column(
        modifier = modifier
    ) {
        RewardTypeSelector(
            selectedRewardType = selectedRewardType,
            modifier = Modifier.fillMaxWidth().padding(start = 8.dp, end = 8.dp, top = 8.dp)
        ) { rewardType ->
            selectedRewardType = rewardType
        }

        RewardDateRangeSelector(
            startDate = startDate,
            endDate = endDate,
            modifier = Modifier.fillMaxWidth().padding(all = 8.dp),
            onStartDateSelected = { startDate = it },
            onEndDateSelected = { endDate = it }
        )

        val listModifier = Modifier
            .fillMaxWidth()
            .weight(1f)
        when (state) {
            is State.Empty, is State.Loading -> Loading(modifier = listModifier)
            !is State.Success -> ErrorOrFailure()
            else -> RewardList((state as State.Success<List<Reward>>).data, listModifier)
        }
    }
}

@Composable
@Preview
fun RewardsTabContentPreview() {
    Column(modifier = Modifier.fillMaxWidth()) {
        RewardTypeSelector(
            selectedRewardType = RewardType.AUTHOR,
            modifier = Modifier.fillMaxWidth().padding(start = 8.dp, end = 8.dp, top = 8.dp)
        ) {}
        RewardDateRangeSelector(
            startDate = datePickerMillisOfToday(),
            endDate = datePickerMillisOfToday(monthOffset = -3),
            modifier = Modifier.fillMaxWidth().padding(all = 8.dp),
            onStartDateSelected = {},
            onEndDateSelected = {}
        )
        RewardList(rewardListForTest, Modifier.fillMaxWidth())
    }
}

// The two ends of the range the reward list covers, each opening a date picker when tapped.
@Composable
fun RewardDateRangeSelector(
    startDate: Long,
    endDate: Long,
    modifier: Modifier,
    onStartDateSelected: (Long) -> Unit,
    onEndDateSelected: (Long) -> Unit
) {
    var editedField by remember { mutableStateOf<RewardDateField?>(null) }

    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        RewardDateButton(
            label = "Start",
            date = startDate,
            modifier = Modifier.weight(1f)
        ) {
            editedField = RewardDateField.START
        }
        Text(
            text = "~",
            style = TextStyle(color = Color.Black, fontSize = 16.sp),
            modifier = Modifier.padding(horizontal = 8.dp)
        )
        RewardDateButton(
            label = "End",
            date = endDate,
            modifier = Modifier.weight(1f)
        ) {
            editedField = RewardDateField.END
        }
    }

    editedField?.let { field ->
        val editedDate = when (field) {
            RewardDateField.START -> startDate
            RewardDateField.END -> endDate
        }
        RewardDatePickerDialog(
            initialDate = editedDate,
            onDismissRequest = { editedField = null }
        ) { selectedDate ->
            editedField = null
            when (field) {
                RewardDateField.START -> onStartDateSelected(selectedDate)
                RewardDateField.END -> onEndDateSelected(selectedDate)
            }
        }
    }
}

@Composable
@Preview
fun RewardDateRangeSelectorPreview() {
    RewardDateRangeSelector(
        startDate = datePickerMillisOfToday(),
        endDate = datePickerMillisOfToday(monthOffset = -3),
        modifier = Modifier.fillMaxWidth(),
        onStartDateSelected = {},
        onEndDateSelected = {}
    )
}

private enum class RewardDateField {
    START,
    END
}

@Composable
fun RewardDateButton(label: String, date: Long, modifier: Modifier, onClick: () -> Unit) {
    Row(
        modifier = modifier
            .background(color = Color.LightGray, shape = RoundedCornerShape(12.dp))
            .clickable { onClick() }
            .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "$label: ${date.toDateString()}",
            style = TextStyle(color = Color.Black, fontSize = 16.sp),
            modifier = Modifier.weight(1f)
        )
        Icon(
            imageVector = Icons.Default.DateRange,
            contentDescription = "Select $label date",
            modifier = Modifier.size(20.dp)
        )
    }
}

@Composable
@Preview
fun RewardDateButtonPreview() {
    RewardDateButton("Start", datePickerMillisOfToday(), Modifier.fillMaxWidth()) {}
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RewardDatePickerDialog(
    initialDate: Long,
    onDismissRequest: () -> Unit,
    onDateSelected: (Long) -> Unit
) {
    val datePickerState = rememberDatePickerState(initialSelectedDateMillis = initialDate)

    DatePickerDialog(
        onDismissRequest = onDismissRequest,
        confirmButton = {
            TextButton(
                onClick = {
                    datePickerState.selectedDateMillis?.let { onDateSelected(it) }
                        ?: onDismissRequest()
                }
            ) {
                Text(text = "OK")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismissRequest) {
                Text(text = "Cancel")
            }
        }
    ) {
        DatePicker(state = datePickerState)
    }
}

// DatePickerState reports a picked day as UTC midnight, so every date this screen holds is
// kept in that form and only converted where it is shown or sent to the API.
private const val DATE_FORMAT = "yyyy-MM-dd"

private val utcCalendar: Calendar
    get() = Calendar.getInstance(TimeZone.getTimeZone("UTC"))

fun datePickerMillisOfToday(monthOffset: Int = 0): Long {
    val today = Calendar.getInstance()
    return utcCalendar.apply {
        clear()
        set(today.get(Calendar.YEAR), today.get(Calendar.MONTH), today.get(Calendar.DAY_OF_MONTH))
        add(Calendar.MONTH, monthOffset)
    }.timeInMillis
}

private fun Long.toDateString(): String {
    val sdf = SimpleDateFormat(DATE_FORMAT, Locale.US)
    sdf.timeZone = TimeZone.getTimeZone("UTC")
    return sdf.format(Date(this))
}

// The two dates are shown latest first, so which one bounds which end of the range depends on
// what was picked, not on which field holds it. They are ordered here first and only then
// widened to cover both days in full, so that neither the first nor the last day is cut short.
private fun rewardTimeRange(startDate: Long, endDate: Long): Pair<Long, Long> {
    val earlierDate = minOf(startDate, endDate)
    val laterDate = maxOf(startDate, endDate)

    return earlierDate.toEpochSecondOfDayStart() to laterDate.toEpochSecondOfDayEnd()
}

// The day a UTC-midnight date names is rebuilt in the local time zone, so the range the API is
// asked for is the range the user sees in the reward times, which are local as well.
private fun Long.toEpochSecondOfDayStart(): Long = toLocalEpochSecond(endOfDay = false)

private fun Long.toEpochSecondOfDayEnd(): Long = toLocalEpochSecond(endOfDay = true)

private fun Long.toLocalEpochSecond(endOfDay: Boolean): Long {
    val pickedDay = utcCalendar.apply { timeInMillis = this@toLocalEpochSecond }
    val localDay = Calendar.getInstance().apply {
        clear()
        set(
            pickedDay.get(Calendar.YEAR),
            pickedDay.get(Calendar.MONTH),
            pickedDay.get(Calendar.DAY_OF_MONTH)
        )
        if (endOfDay) {
            set(Calendar.HOUR_OF_DAY, 23)
            set(Calendar.MINUTE, 59)
            set(Calendar.SECOND, 59)
        }
    }

    return localDay.timeInMillis / 1000L
}

// A combo box that picks which kind of reward the list below shows.
@Composable
fun RewardTypeSelector(
    selectedRewardType: RewardType,
    modifier: Modifier,
    onRewardTypeSelected: (RewardType) -> Unit
) {
    var isDropdownMenuOpen by remember { mutableStateOf(false) }

    Box(
        modifier = modifier
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(color = Color.LightGray, shape = RoundedCornerShape(12.dp))
                .clickable { isDropdownMenuOpen = true }
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = selectedRewardType.title,
                style = TextStyle(color = Color.Black, fontSize = 16.sp),
                modifier = Modifier.weight(1f)
            )
            Icon(
                imageVector = Icons.Default.ArrowDropDown,
                contentDescription = "Select a reward type"
            )
        }

        DropdownMenu(
            expanded = isDropdownMenuOpen,
            onDismissRequest = { isDropdownMenuOpen = false },
            modifier = Modifier.background(Color.DarkGray)
        ) {
            RewardType.entries.forEach { rewardType ->
                DropdownMenuItem(
                    text = {
                        Text(text = rewardType.title, color = Color.White, fontSize = 16.sp)
                    },
                    onClick = {
                        isDropdownMenuOpen = false
                        onRewardTypeSelected(rewardType)
                    }
                )
            }
        }
    }
}

@Composable
@Preview
fun RewardTypeSelectorPreview() {
    RewardTypeSelector(RewardType.AUTHOR, Modifier.fillMaxWidth()) {}
}

private val RewardType.title: String
    get() = when (this) {
        RewardType.AUTHOR -> "author"
        RewardType.CURATION -> "curation"
    }

@Composable
fun RewardList(rewardList: List<Reward>, modifier: Modifier) {
    LazyColumn(
        modifier = modifier
    ) {
        items(rewardList.size) { index ->
            RewardItem(
                reward = rewardList[index],
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
fun RewardListPreview() {
    RewardList(rewardListForTest, Modifier.fillMaxWidth())
}

@Composable
fun RewardItem(reward: Reward, modifier: Modifier) {
    Column(
        modifier = modifier
    ) {
        val contentTextStyle = TextStyle(color = Color.Black, fontSize = 16.sp)
        Text(text = "amount: ${reward.amount}", style = contentTextStyle)
        Text(text = "post: @${reward.author}/${reward.permlink}", style = contentTextStyle)
        Text(text = "time: ${reward.time}", style = contentTextStyle)
    }
}

@Composable
@Preview
fun RewardItemPreview() {
    RewardItem(rewardListForTest[0], Modifier.fillMaxWidth())
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

private val rewardListForTest by lazy {
    listOf(
        Reward(
            time = "2025-12-01 15:45",
            type = RewardType.AUTHOR,
            amount = "0.323 SBD, 78.416 SP",
            author = "test-account",
            permlink = "my-first-post"
        ),
        Reward(
            time = "2025-11-28 09:12",
            type = RewardType.CURATION,
            amount = "1.234 SP",
            author = "alice",
            permlink = "a-post-i-voted-on"
        )
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
