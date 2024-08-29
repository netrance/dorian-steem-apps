package lee.dorian.steem_ui.ui.wallet

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.launch
import lee.dorian.steem_domain.model.SteemitWallet
import lee.dorian.steem_ui.MainViewModel
import lee.dorian.steem_ui.R
import lee.dorian.steem_ui.databinding.FragmentWalletBinding
import lee.dorian.steem_ui.ext.showToastShortly
import lee.dorian.steem_ui.model.State
import lee.dorian.steem_ui.ui.base.BaseFragment

class WalletFragment : BaseFragment<FragmentWalletBinding, WalletViewModel>(R.layout.fragment_wallet) {

    private val args: WalletFragmentArgs by navArgs()

    override val viewModel: WalletViewModel by lazy {
        ViewModelProvider(this).get(WalletViewModel::class.java)
    }

    val activityViewModel by lazy {
        ViewModelProvider(requireActivity()).get(MainViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return super.onCreateView(inflater, container, savedInstanceState).apply {
            binding.viewModel = viewModel
            binding.activityViewModel = activityViewModel
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.includeAccountLookup.root.visibility = when {
            (args.account.isEmpty()) -> View.VISIBLE
            else -> View.GONE
        }
        binding.includeAccountLookup.buttonAccountSearch.setOnClickListener(buttonAccountSearchClickListener)

        lifecycleScope.launch {
            viewModel.flowWalletState.collect(walletStateCollector)
        }

        if (args.account.isNotEmpty() and (viewModel.flowWalletState.value is State.Empty)) {
            viewLifecycleOwner.lifecycleScope.launch {
                viewModel.readSteemitWallet(args.account)
            }
        }
    }

    private val walletStateCollector = FlowCollector<State<SteemitWallet>> { state ->
        when (state) {
            is State.Empty -> updateWalletAsEmpty()
            is State.Loading -> updateWalletAsLoading()
            is State.Success -> updateWallet(state.data)
            else -> showToastShortly(getString(R.string.error_cannot_load))
        }
    }

    private val buttonAccountSearchClickListener = View.OnClickListener {
        val account = binding.includeAccountLookup.editSteemitAccount.text.toString()
        if (account.length > 2) {
            viewLifecycleOwner.lifecycleScope.launch {
                viewModel.readSteemitWallet(account)
            }
        }
    }

    private fun updateWallet(wallet: SteemitWallet) {
        binding.includeSteemBalances.apply {
            textSteemBalance.text = "${wallet.steemBalance}"
            textSbdBalance.text = "${wallet.sbdBalance}"
        }
        binding.includeSteemStaking.apply {
            textSpAmount.text = "${wallet.steemPower}"
            textEffetiveSpAmount.text = "${wallet.effectiveSteemPower}"
            textDelegatingAmount.text = "${wallet.delegatedSteemPower}"
            textDelegatedAmount.text = "${wallet.receivedSteemPower}"
        }
        binding.includeSteemSavings.apply {
            textSteemSaving.text = "${wallet.savingSteemBalance}"
            textSbdSaving.text = "${wallet.savingSbdBalance}"
        }
        binding.includePowerDown.apply {
            textSpToPowerDownAmount.text = "${wallet.totalSPToBeWithdrawn}"
            textPowerDownRateAmount.text = "${wallet.spWithdrawRate}"
            textSteemPowerWithdrawnAmount.text = "${wallet.remainingSPToBeWithdrawn}"
            textNextPowerDownTime.text = "${wallet.nextPowerDownTime}"
        }
    }

    private fun updateWalletAsEmpty() {
        val zeroSbd = "0.000 SBD"
        val zeroSteem = "0.000 STEEM"
        val zeroSteemPower = "0.000 SP"

        binding.includeSteemBalances.apply {
            textSteemBalance.text = zeroSteem
            textSbdBalance.text = zeroSbd
        }
        binding.includeSteemStaking.apply {
            textSpAmount.text = zeroSteemPower
            textEffetiveSpAmount.text = zeroSteemPower
            textDelegatingAmount.text = zeroSteemPower
            textDelegatedAmount.text = zeroSteemPower
        }
        binding.includeSteemSavings.apply {
            textSteemSaving.text = zeroSteem
            textSbdSaving.text = zeroSbd
        }
        binding.includePowerDown.apply {
            textSpToPowerDownAmount.text = zeroSteemPower
            textPowerDownRateAmount.text = zeroSteemPower
            textSteemPowerWithdrawnAmount.text = zeroSteemPower
            textNextPowerDownTime.text = "0"
        }
    }

    private fun updateWalletAsLoading() {
        val loading = getString(R.string.loading)
        binding.includeSteemBalances.apply {
            textSteemBalance.text = loading
            textSbdBalance.text = loading
        }
        binding.includeSteemStaking.apply {
            textSpAmount.text = loading
            textEffetiveSpAmount.text = loading
            textDelegatingAmount.text = loading
            textDelegatedAmount.text = loading
        }
        binding.includeSteemSavings.apply {
            textSteemSaving.text = loading
            textSbdSaving.text = loading
        }
        binding.includePowerDown.apply {
            textSpToPowerDownAmount.text = loading
            textPowerDownRateAmount.text = loading
            textSteemPowerWithdrawnAmount.text = loading
            textNextPowerDownTime.text = loading
        }
    }

}