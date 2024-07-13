package lee.dorian.steem_data.model.history

data class HistoryItemContentDTO(
    val trx_id: String,
    val block: Int,
    val trx_in_block: Int,
    val op_in_trx: Int,
    val virtual_op: Int,
    val timestamp: String,
    val op: Any
)
