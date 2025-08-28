package lee.dorian.steem_ui.ui.wallet

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.fragment.navArgs
import lee.dorian.steem_domain.model.SteemitWallet
import lee.dorian.steem_ui.MainViewModel
import lee.dorian.steem_ui.model.State
import lee.dorian.steem_ui.ui.compose.AccountInputForm
import lee.dorian.steem_ui.ui.compose.ErrorOrFailure
import lee.dorian.steem_ui.ui.compose.Loading

class WalletFragment : Fragment() {

    private val args: WalletFragmentArgs by navArgs()

    val viewModel: WalletViewModel by lazy {
        ViewModelProvider(this).get(WalletViewModel::class.java)
    }

    val activityViewModel by lazy {
        ViewModelProvider(requireActivity()).get(MainViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                SteemitWalletScreen(viewModel, args.account)
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

}

@Composable
fun SteemitWalletScreen(
    viewModel: WalletViewModel,
    initialAccount: String
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    val state by viewModel.flowWalletState.collectAsStateWithLifecycle()

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
            is State.Empty -> {
                if (initialAccount.isEmpty()) {
                    WalletEmpty(modifier = commonModifier)
                }
                else {
                    viewModel.readSteemitWallet(initialAccount)
                }
            }
            is State.Loading -> Loading(modifier = commonModifier)
            !is State.Success -> ErrorOrFailure()
            else -> {
                val wallet = (state as State.Success<SteemitWallet>).data
                SteemitWalletContent(
                    wallet,
                    Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .background(Color.White)
                        .verticalScroll(rememberScrollState())
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
fun SteemitWalletContent(wallet: SteemitWallet, modifier: Modifier) {
    Column(
        modifier = modifier
    ) {
        val cardModifier = Modifier
            .fillMaxWidth()
            .padding(start = 8.dp, end = 8.dp, top = 8.dp)
        WalletBalances(wallet, cardModifier)
        WalletStaking(wallet, cardModifier)
        WalletSavings(wallet, cardModifier)
        WalletPowerDown(wallet, cardModifier)
    }
}

@Composable
@Preview
fun SteemitWalletContentPreview() {
    SteemitWalletContent(walletForTest, Modifier.fillMaxWidth())
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
fun WalletStaking(wallet: SteemitWallet, modifier: Modifier) {
    TitleContentCard(
        title = "Staking",
        contents = listOf(
            Pair("STEEM POWER:", wallet.steemPower),
            Pair(" - Effective SP:", wallet.effectiveSteemPower),
            Pair(" - Delegating:", wallet.delegatedSteemPower),
            Pair(" - Delegated:", wallet.receivedSteemPower)
        ),
        modifier = modifier
    )
}

@Composable
@Preview
fun WalletStakingPreview() {
    WalletStaking(walletForTest, Modifier.fillMaxWidth())
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
        title = "Balances",
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
        modifier= modifier
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