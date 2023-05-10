package lee.dorian.steem_ui.ui.wallet

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.launch
import lee.dorian.steem_domain.model.SteemitWallet
import lee.dorian.steem_ui.MainViewModel
import lee.dorian.steem_ui.R
import lee.dorian.steem_ui.databinding.FragmentWalletBinding
import lee.dorian.steem_ui.ext.showToastShortly
import lee.dorian.steem_ui.ui.base.BaseFragment

class WalletFragment : BaseFragment<FragmentWalletBinding, WalletViewModel>(R.layout.fragment_wallet) {

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

        activityViewModel.currentAccount.removeObservers(viewLifecycleOwner)
        activityViewModel.currentAccount.observe(viewLifecycleOwner, currentAccountObserver)

        lifecycleScope.launch {
            viewModel.flowWalletState.collect(walletStateCollector)
        }
    }

    private val currentAccountObserver = Observer<String> {
        if (it.length > 2) {
            viewModel.readSteemitWallet(it)
        }
    }

    private val walletStateCollector = FlowCollector<WalletState> { state ->
        when (state) {
            is WalletState.Success -> updateWallet(state.wallet)
            is WalletState.Failure, is WalletState.Error -> {
                showToastShortly(getString(R.string.error_cannot_load))
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

}