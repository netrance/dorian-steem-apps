package lee.dorian.steem_data.model.history

class HistoryItemDTO : MutableList<Any> by mutableListOf() {

    fun getIndex(): Int {
        return try {
            when {
                this[0] is Int -> this[0] as Int
                else -> -1
            }
        }
        catch (e: java.lang.IndexOutOfBoundsException) {
            e.printStackTrace()
            -1
        }
    }

    fun getHistoryItemContent(): HistoryItemContentDTO? {
        return try {
            when {
                this[1] is HistoryItemContentDTO -> this[1] as HistoryItemContentDTO
                else -> null
            }
        }
        catch (e: java.lang.IndexOutOfBoundsException) {
            e.printStackTrace()
            null
        }
    }


}