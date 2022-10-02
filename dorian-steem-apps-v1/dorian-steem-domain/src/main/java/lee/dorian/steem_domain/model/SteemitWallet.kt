package lee.dorian.steem_domain.model

import lee.dorian.steem_domain.ext.removeSubstring
import java.lang.NumberFormatException

data class SteemitWallet(
    val account: String = "",
    val steemBalance: String = "0.000 STEEM",
    val sbdBalance: String = "0.000 SBD",
    val savingSteemBalance: String = "0.000 STEEM",
    val savingSbdBalance: String = "0.000 SBD",
    val steemPowerVest: String = "0.000000 VESTS",
    val delegatedSteemPowerVest: String = "0.000000 VESTS",
    val receivedSteemPowerVest: String = "0.000000 VESTS",
    val spWithdrawRate: String = "0.000000 VESTS",
    val spToBeWithdrawn: String = "0.000000 VESTS"
) {

    fun getEffectiveSteemPowerVest(): String {
        return try {
            val steemPowerVestDouble =
                steemPowerVest.removeSubstring(" VESTS").toDouble()
            val delegatedSteemPowerVestDouble =
                delegatedSteemPowerVest.removeSubstring(" VESTS").toDouble()
            val receivedSteemPowerVestDouble =
                receivedSteemPowerVest.removeSubstring(" VESTS").toDouble()
            val effectiveSteemPowerVestDouble =
                steemPowerVestDouble - delegatedSteemPowerVestDouble + receivedSteemPowerVestDouble
            String.format("%.6f VESTS", effectiveSteemPowerVestDouble)
        }
        catch (e: NumberFormatException) {
            e.printStackTrace()
            "0.000000 VESTS"
        }
    }

}