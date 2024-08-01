package lee.dorian.steem_ui.ui.history

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import lee.dorian.dorian_ktx.isOdd
import lee.dorian.steem_domain.model.AccountHistoryItem
import lee.dorian.steem_ui.databinding.LayoutAccountHistoryItemBinding

class AccountHistoryItemListAdapter() : RecyclerView.Adapter<AccountHistoryItemListAdapter.AccountHistoryItemListViewHolder>() {

    private val accountHistoryItemList = mutableListOf<AccountHistoryItem>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AccountHistoryItemListViewHolder {
        return AccountHistoryItemListViewHolder(LayoutAccountHistoryItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        ))
    }

    override fun onBindViewHolder(holder: AccountHistoryItemListViewHolder, position: Int) {
        try {
            val post = accountHistoryItemList[position]
            holder.bind(post, position)
        }
        catch (e: IndexOutOfBoundsException) {
            e.printStackTrace()
        }
    }

    override fun getItemCount(): Int = accountHistoryItemList.size

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    fun setList(list: List<AccountHistoryItem>) {
        this.accountHistoryItemList.apply {
            clear()
            addAll(list)
        }
        notifyDataSetChanged()
    }

    inner class AccountHistoryItemListViewHolder(
        val binding: LayoutAccountHistoryItemBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(accountHistoryItem: AccountHistoryItem, position: Int) {
            binding.apply {
                textHistoryItemType.text = accountHistoryItem.type
                textHistoryItemContent.text = accountHistoryItem.content.toString()
                textHistoryItemTime.text = accountHistoryItem.localTime
                root.setBackgroundColor(when (position.isOdd()) {
                    true -> Color.rgb(0xEE, 0xEE, 0xEE)
                    else -> Color.WHITE
                })
            }
        }
    }

}