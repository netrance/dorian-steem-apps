package lee.dorian.steem_domain.util

import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

object Converter {

    fun toSteemPowerFromVest(
        vestingAmount: Float,
        totalVestingShare: Float,
        totalVestingFundSteem: Float
    ): Float {
        return (totalVestingFundSteem * vestingAmount) / totalVestingShare
    }

    fun toLocalTimeFromUTCTime(utcTime: String, timeFormat: String): String {
        if (utcTime.isEmpty()) {
            return ""
        }

        try {
            val sdf = SimpleDateFormat(timeFormat)
            val date = sdf.parse(utcTime.replace("T", " ").trim())
            val longUtcTime = date.getTime()
            val offset: Int = TimeZone.getDefault().getOffset(longUtcTime)
            val longLocalTime = longUtcTime + offset
            val dateLocalTime = Date().apply {
                setTime(longLocalTime)
            }
            return sdf.format(dateLocalTime)
        }
        catch (e: ParseException) {
            e.printStackTrace()
            return ""
        }
    }

}