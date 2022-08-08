package lee.dorian.steem_ui.ui.tags

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import lee.dorian.steem_ui.R
import lee.dorian.steem_ui.databinding.FragmentTagsBinding

class TagsFragment : Fragment() {

    private var _binding: FragmentTagsBinding? = null

    // This property is only valid between onCreateView and onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val tagsViewModel =
            ViewModelProvider(this).get(TagsViewModel::class.java)

        _binding = DataBindingUtil.inflate(this.layoutInflater, R.layout.fragment_tags, null, false)
        binding.viewModel = tagsViewModel
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}