package lee.dorian.steem_data.model.history

import com.google.gson.JsonObject
import com.google.gson.internal.LinkedTreeMap

data class HistoryItemContentDTO(
    val trx_id: String,
    val block: Double,
    val trx_in_block: Double,
    val op_in_trx: Double,
    val virtual_op: Double,
    val timestamp: String,
    val op: HistoryOpDTO
) {

    companion object {
        fun create(map: Map<String, Any>): HistoryItemContentDTO? {
            val trx_id = map.getOrDefault("trx_id", "") as String
            val block = map.getOrDefault("block", 0.0) as Double
            val trx_in_block = map.getOrDefault("trx_in_block", 0.0) as Double
            val op_in_trx = map.getOrDefault("op_in_trx", 0.0) as Double
            val virtual_op = map.getOrDefault("virtual_op", 0.0) as Double
            val timestamp = map.getOrDefault("timestamp", "") as String
            val op = map.getOrDefault("op", listOf("", LinkedTreeMap<String, Any>())) as List<Any>
            val historyOpDTO = HistoryOpDTO.create(op)

            return HistoryItemContentDTO(
                trx_id,
                block,
                trx_in_block,
                op_in_trx,
                virtual_op,
                timestamp,
                historyOpDTO
            )
        }
    }

}