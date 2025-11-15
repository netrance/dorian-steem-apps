package lee.dorian.steem_domain.util

import lee.dorian.steem_domain.model.DynamicGlobalProperties
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

    fun toSteemPowerFromVest(
        vestingAmount: Float,
        dynamicGlobalProperties: DynamicGlobalProperties
    ): Float {
        val totalVestingFundSteem = dynamicGlobalProperties.total_vesting_fund_steem.replace(" STEEM", "").toFloat()
        val totalVestingShares = dynamicGlobalProperties.total_vesting_shares.replace(" VESTS", "").toFloat()
        return (totalVestingFundSteem * vestingAmount) / totalVestingShares
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