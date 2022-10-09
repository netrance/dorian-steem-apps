package lee.dorian.steem_domain.util

object Converter {

    fun toSteemPowerFromVest(
        vestingAmount: Float,
        totalVestingShare: Float,
        totalVestingFundSteem: Float
    ): Float {
        return (totalVestingFundSteem * vestingAmount) / totalVestingShare
    }

}