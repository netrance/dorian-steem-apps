package lee.dorian.steem_data.model.history

import com.google.gson.JsonObject
import com.google.gson.internal.LinkedTreeMap

class HistoryOpDTO(op: String, opContent: Map<String, Any>) : MutableList<Any> by mutableListOf(
    op,
    opContent
) {

    fun getOpCode(): String {
        return try {
            when {
                this[0] is String -> this[0] as String
                else -> ""
            }
        }
        catch (e: java.lang.IndexOutOfBoundsException) {
            e.printStackTrace()
            ""
        }
    }

    fun getOpContent(): Map<String, Any> {
        return try {
            when {
                this[1] is Map<*, *> -> this[1] as Map<String, Any>
                else -> LinkedTreeMap()
            }
        }
        catch (e: java.lang.IndexOutOfBoundsException) {
            e.printStackTrace()
            LinkedTreeMap()
        }
    }

    companion object {
        fun create(op: List<Any>): HistoryOpDTO {
            try {
                val opCode = when (op[0]) {
                    is String -> op[0] as String
                    else -> ""
                }
                val opContent = when (op[1]) {
                    is Map<*, *> -> op[1] as Map<String, Any>
                    else -> LinkedTreeMap()
                }

                return HistoryOpDTO(opCode, opContent)
            }
            catch (e: java.lang.IndexOutOfBoundsException) {
                return HistoryOpDTO("", LinkedTreeMap())
            }
        }
    }
}