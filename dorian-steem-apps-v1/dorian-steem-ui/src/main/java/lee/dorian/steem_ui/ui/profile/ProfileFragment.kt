package lee.dorian.steem_ui.ui.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.launch
import lee.dorian.steem_domain.model.SteemitProfile
import lee.dorian.steem_ui.MainViewModel
import lee.dorian.steem_ui.R
import lee.dorian.steem_ui.databinding.FragmentProfileBinding
import lee.dorian.steem_ui.ext.load
import lee.dorian.steem_ui.ext.setActivityActionBarTitle
import lee.dorian.steem_ui.model.State
import lee.dorian.steem_ui.ui.base.BaseFragment
import lee.dorian.steem_ui.ui.history.AccountHistoryFragmentArgs

class ProfileFragment : BaseFragment<FragmentProfileBinding, ProfileViewModel>(R.layout.fragment_profile) {

    private val args: ProfileFragmentArgs by navArgs()

    override val viewModel by lazy {
        ViewModelProvider(this).get(ProfileViewModel::class.java)
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

        binding.includeAccountLookup.root.visibility = when {
            (args.account.isEmpty()) -> View.VISIBLE
            else -> View.GONE
        }
        binding.includeAccountLookup.buttonAccountSearch.setOnClickListener(buttonAccountSearchClickListener)
        binding.includeProfileMenu.includeMenuItem2.layoutMenuItem.setOnClickListener(menuItem2ClickListener)
        binding.includeProfileMenu.includeMenuItem3.layoutMenuItem.setOnClickListener(menuItem3ClickListener)
        binding.includeProfileMenu.includeMenuItem4.layoutMenuItem.setOnClickListener(menuItem4ClickListener)
        binding.includeProfileMenu.includeMenuItem5.layoutMenuItem.setOnClickListener(menuItem5ClickListener)
        binding.includeProfileMenu.includeMenuItem6.layoutMenuItem.setOnClickListener(menuItem6ClickListener)

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.profileState.collect(profileStateCollector)
        }

        if (args.account.isNotEmpty() and (viewModel.profileState.value is State.Empty)) {
            viewLifecycleOwner.lifecycleScope.launch {
                viewModel.readSteemitProfile(args.account)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        val state = viewModel.profileState.value
        setActivityActionBarTitle(when (state) {
            is State.Success -> "Profile of @${getAuthor()}"
            else -> "Profile"
        })
    }

    private val profileStateCollector = FlowCollector<State<SteemitProfile>> { newState ->
        when (newState) {
            is State.Empty -> showEmpty()
            is State.Loading -> showLoading()
            is State.Success -> showProfile(newState.data)
            is State.Error, is State.Failure -> showLoadingError()
        }
    }

    private val buttonAccountSearchClickListener = View.OnClickListener {
        val account = binding.includeAccountLookup.editSteemitAccount.text.toString()
        if (account.length > 2) {
            viewLifecycleOwner.lifecycleScope.launch {
                viewModel.readSteemitProfile(account)
            }
        }
    }

    private val menuItem2ClickListener = OnClickListener { v ->
        val author = getAuthor()

        val action = ProfileFragmentDirections.actionNavigationProfileToNavigationPostList(author, "blog")
        findNavController().navigate(action)
        setActivityActionBarTitle("Blog of @${author}")
    }

    private val menuItem3ClickListener = OnClickListener { v ->
        val author = getAuthor()

        val action = ProfileFragmentDirections.actionNavigationProfileToNavigationPostList(author, "posts")
        findNavController().navigate(action)
        setActivityActionBarTitle("Posts of @${author}")
    }

    private val menuItem4ClickListener = OnClickListener { v ->
        val author = getAuthor()

        val action = ProfileFragmentDirections.actionNavigationProfileToNavigationPostList(author, "comments")
        findNavController().navigate(action)
        setActivityActionBarTitle("Comments of @${author}")
    }

    private val menuItem5ClickListener = OnClickListener { v ->
        val author = getAuthor()

        val action = ProfileFragmentDirections.actionNavigationProfileToNavigationPostList(author, "replies")
        findNavController().navigate(action)
        setActivityActionBarTitle("Replies of @${author}")
    }

    private val menuItem6ClickListener = OnClickListener { v ->
        val author = getAuthor()

        val action = ProfileFragmentDirections.actionNavigationProfileToNavigationAccountHistory(author)
        findNavController().navigate(action)
        setActivityActionBarTitle("History of @${author}")
    }

    private fun getAuthor(): String {
        val state = viewModel.profileState.value
        return when  {
            (state is State.Success<SteemitProfile>) -> state.data.account
            else -> ""
        }
    }

    private fun showEmpty() {
        binding.includeInputAccount.layoutInputAccount.visibility = View.VISIBLE
        binding.includeLoading.layoutLoading.visibility = View.GONE
        binding.includeLoadingError.layoutLoadingError.visibility = View.GONE
        binding.scrollProfile.visibility = View.GONE
    }

    private fun showLoading() {
        binding.includeInputAccount.layoutInputAccount.visibility = View.GONE
        binding.includeLoading.layoutLoading.visibility = View.VISIBLE
        binding.includeLoadingError.layoutLoadingError.visibility = View.GONE
        binding.scrollProfile.visibility = View.GONE
    }

    private fun showLoadingError() {
        binding.includeInputAccount.layoutInputAccount.visibility = View.GONE
        binding.includeLoading.layoutLoading.visibility = View.GONE
        binding.includeLoadingError.layoutLoadingError.visibility = View.VISIBLE
        binding.scrollProfile.visibility = View.GONE
    }

    private fun showProfile(profile: SteemitProfile) {
        binding.includeInputAccount.layoutInputAccount.visibility = View.GONE
        binding.includeLoading.layoutLoading.visibility = View.GONE
        binding.includeLoadingError.layoutLoadingError.visibility = View.GONE
        binding.scrollProfile.visibility = View.VISIBLE

        binding.includeProfile.run {
            imageProfileBackground.load(profile.coverImageURL, null)
            imageProfile.load("https://steemitimages.com/u/${profile.account}/avatar/small")
            textAccount.text = "@${profile.account}"
            textName.text = profile.name
            textFollowers.text = "${profile.followerCount} Followers"
            textFollowing.text = "${profile.followingCount} Following"
            textAbout.text = profile.about
            textLocation.text = when {
                profile.location.isEmpty() -> "📍 (No location)"
                else -> "📍 ${profile.location}"
            }
            textWebLink.text = when {
                profile.website.isEmpty() -> "📍 (No web site)"
                else -> "🔗 ${profile.website}"
            }
        }

        val state = viewModel.profileState.value
        setActivityActionBarTitle(when (state) {
            is State.Success -> "Profile of @${getAuthor()}"
            else -> "Profile"
        })
    }

}