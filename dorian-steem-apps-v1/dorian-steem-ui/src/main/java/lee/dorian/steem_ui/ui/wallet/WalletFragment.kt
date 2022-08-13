package lee.dorian.steem_ui.ui.wallet

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import lee.dorian.steem_ui.R
import lee.dorian.steem_ui.databinding.FragmentWalletBinding
import lee.dorian.steem_ui.ui.base.BaseFragment

class WalletFragment : BaseFragment<FragmentWalletBinding, WalletViewModel>(R.layout.fragment_wallet) {

    override val viewModel: WalletViewModel by lazy {
        ViewModelProvider(this).get(WalletViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return super.onCreateView(inflater, container, savedInstanceState).apply {
            binding.viewModel = viewModel
        }
    }

}