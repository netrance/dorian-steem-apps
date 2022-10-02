package lee.dorian.steem_ui.ui.wallet

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import lee.dorian.steem_ui.MainViewModel
import lee.dorian.steem_ui.R
import lee.dorian.steem_ui.databinding.FragmentWalletBinding
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
    }

    private val currentAccountObserver = Observer<String> {
        if (it.length > 2) {
            viewModel.readSteemitWallet(it).subscribe()
        }
    }

}