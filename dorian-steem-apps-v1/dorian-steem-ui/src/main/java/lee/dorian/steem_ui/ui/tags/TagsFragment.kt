package lee.dorian.steem_ui.ui.tags

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import lee.dorian.steem_ui.MainViewModel
import lee.dorian.steem_ui.R
import lee.dorian.steem_ui.databinding.FragmentTagsBinding
import lee.dorian.steem_ui.ui.base.BaseFragment

class TagsFragment : BaseFragment<FragmentTagsBinding, TagsViewModel>(R.layout.fragment_tags) {

    override val viewModel by lazy {
        ViewModelProvider(this).get(TagsViewModel::class.java)
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
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        activityViewModel.currentTag.observe(requireActivity()) {
            if (it.length > 0) {
                viewModel.text.value = "Current tag is #${it}."
            }
        }
    }

}