package lee.dorian.steem_ui.ui.post.content

import android.os.Bundle
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.launch
import lee.dorian.steem_ui.databinding.FragmentReplyListDialogBinding

class ReplyListDialogFragment : BottomSheetDialogFragment() {

    private var _binding: FragmentReplyListDialogBinding? = null

    private val viewModel: PostContentViewModel by lazy {
        // Set owner parameter to requireActivity() to access the view model PostContentFragment object created
        ViewModelProvider(requireActivity()).get(PostContentViewModel::class.java)
    }

    // This property is only valid between onCreateView and onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentReplyListDialogBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.imageClose.setOnClickListener { dismiss() }

        lifecycleScope.launch {
            viewModel.flowPostState.collect(postContentStateCollector)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private val postContentStateCollector = FlowCollector<PostContentState> { state ->
        when (state) {
            is PostContentState.Success -> {
                binding.listReply.adapter = ReplyListAdapter(state.replies, state.post)
            }
            else -> {}
        }
    }

}