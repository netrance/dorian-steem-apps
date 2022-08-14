package lee.dorian.steem_ui

import android.os.Bundle
import android.os.PersistableBundle
import androidx.activity.viewModels
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

abstract class BaseActivity<VDB: ViewDataBinding, VM: ViewModel>(
    @LayoutRes private val layoutResID: Int
) : AppCompatActivity() {

    protected val binding by lazy {
        DataBindingUtil.setContentView(this, layoutResID) as VDB
    }

    abstract protected val viewModel: VM

    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onCreate(savedInstanceState, persistentState)
        binding.lifecycleOwner = this
    }

}