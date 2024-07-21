package lee.dorian.steem_data.model.history

import lee.dorian.steem_domain.model.HistoryItem

data class GetAccountHistoryResponseDTO(
    val jsonrpc: String?,
    val result: List<HistoryItemDTO>?,    // Array<Any>: Index and history content
    val id: Int?
) {
    fun toHistoryItemList(): List<HistoryItem> {
        return result?.map {
            it.toHistoryItem()
        } ?: listOf()
    }

}

// Example of one history item.
// 831265 is index, and JSON object is history content.
// [
//     831265,
//     {
//         "trx_id": "221bd2095a27526fc736979fe1d1b74f4302fbf0",
//         "block": 86014254,
//         "trx_in_block": 2,
//         "op_in_trx": 0,
//         "virtual_op": 0,
//         "timestamp": "2024-06-15T23:35:24",
//         "op": [
//             "vote",
//             {
//                 "voter": "dorian-lee",
//                 "author": "sean2masaaki",
//                 "permlink": "2024-06-16",
//                 "weight": 2500
//             }
//         ]
//     }
// ]