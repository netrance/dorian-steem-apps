package lee.dorian.steem_ui.ui.history

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.launch
import lee.dorian.steem_domain.model.AccountHistory
import lee.dorian.steem_domain.model.AccountHistoryItem
import lee.dorian.steem_ui.MainViewModel
import lee.dorian.steem_ui.R
import lee.dorian.steem_ui.databinding.FragmentAccountHistoryBinding
import lee.dorian.steem_ui.ext.setActivityActionBarTitle
import lee.dorian.steem_ui.ext.showToastShortly
import lee.dorian.steem_ui.model.State
import lee.dorian.steem_ui.ui.base.BaseFragment
import lee.dorian.steem_ui.ui.tags.PostItemListAdapter

class AccountHistoryFragment : BaseFragment<FragmentAccountHistoryBinding, AccountHistoryViewModel>(R.layout.fragment_account_history) {

    private val args: AccountHistoryFragmentArgs by navArgs()

    override val viewModel by lazy {
        ViewModelProvider(this).get(AccountHistoryViewModel::class.java)
    }

    val activityViewModel by lazy {
        ViewModelProvider(requireActivity()).get(MainViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.swipeRefreshAccountHistory.isRefreshing = false
        binding.listAccountHistory.run {
            adapter = AccountHistoryItemListAdapter(accountHistoryItemClickListener).apply {
                setHasStableIds(true)
            }
            addOnScrollListener(postsScrollListener)
        }

        binding.apply {
            swipeRefreshAccountHistory.setOnRefreshListener(swipeRefreshPostListRefreshListener)
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.flowState.collect(stateCollector)
        }
    }

    private val stateCollector = FlowCollector<State<AccountHistory>> { newState ->
        binding.swipeRefreshAccountHistory.isRefreshing = false
        when (newState) {
            is State.Error, is State.Failure -> {
                showToastShortly(getString(R.string.error_cannot_load))
            }
            is State.Empty -> {
                readAccountHistory()
            }
            is State.Loading -> {
                binding.swipeRefreshAccountHistory.isRefreshing = true
            }
            is State.Success -> {
                updateList(newState.data.historyList)
            }
        }
    }

    private val accountHistoryItemClickListener = object: AccountHistoryItemListAdapter.OnAccountHistoryItemClickListener {
        override fun onClick(itemView: View, accountHistoryItem: AccountHistoryItem) {
            val popupMenu = PopupMenu(activity, itemView)
            val accountHistoryItemLinks = accountHistoryItem.getLinkList()
            accountHistoryItemLinks.forEachIndexed { index, item ->
                popupMenu.menu.add(0, index, index, item.title)
            }
            popupMenu.setOnMenuItemClickListener { menuItem ->
                val link = accountHistoryItemLinks[menuItem.itemId]
                when (link.type) {
                    "profile" -> {
                        val account = link.link.replace("@", "")
                        val action = AccountHistoryFragmentDirections.actionNavigationAccountHistoryToNavigationProfile(account)
                        findNavController().navigate(action)
                        setActivityActionBarTitle("Profile of @${link.link}")
                    }
                    "post" -> {
                        val linkElements = link.link.split("/")
                        val author = linkElements[0].replace("@", "")
                        val permlink = linkElements[1]
                        val action = AccountHistoryFragmentDirections.actionNavigationAccountHistoryToNavigationPost(
                            author, permlink
                        )
                        findNavController().navigate(action)
                        setActivityActionBarTitle("Posts of @${author}")
                    }
                    "wallet" -> {
                        val account = link.link.replace("@", "")
                        val action = AccountHistoryFragmentDirections.actionNavigationAccountHistoryToNavigationWallet(account)
                        findNavController().navigate(action)
                        setActivityActionBarTitle("Wallet of @${account}")
                    }
                }
                true
            }

            if (accountHistoryItemLinks.isNotEmpty()) {
                popupMenu.show()
            }
        }
    }

    private val postsScrollListener: RecyclerView.OnScrollListener = object : RecyclerView.OnScrollListener() {
        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)

            if (!binding.listAccountHistory.canScrollVertically(1)) {
                viewModel.appendAccountHistory()
            }
        }
    }

    private val swipeRefreshPostListRefreshListener = SwipeRefreshLayout.OnRefreshListener {
        readAccountHistory()
    }

    private fun updateList(accountHistoryItemList: List<AccountHistoryItem>) {
        binding.swipeRefreshAccountHistory.isRefreshing = false
        (binding.listAccountHistory.adapter as AccountHistoryItemListAdapter).setList(accountHistoryItemList)
    }

    private fun emptyList() {
        (binding.listAccountHistory.adapter as AccountHistoryItemListAdapter).setList(listOf())
    }

    private fun readAccountHistory() = viewLifecycleOwner.lifecycleScope.launch {
        emptyList()
        viewModel.readAccountHistory(args.author)
    }

    companion object {
    }
}