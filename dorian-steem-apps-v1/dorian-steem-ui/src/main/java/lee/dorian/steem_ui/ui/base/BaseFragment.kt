package lee.dorian.steem_ui.ui.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel

abstract class BaseFragment<VDB: ViewDataBinding, VM: ViewModel>(
    @LayoutRes private val layoutResID: Int
) : Fragment() {

    private var _binding: VDB? = null
    protected val binding get() = _binding!!

    abstract protected val viewModel: VM

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = DataBindingUtil.inflate(this.layoutInflater, layoutResID, null, false) as VDB
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}