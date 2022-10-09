package lee.dorian.steem_domain.util

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ConverterTest {

    @Test
    fun toSteemPowerFromVest() {
        val vestingAmount = 239052218.351971f
        val totalVestingShare = 295032233391.941404f
        val totalVestingFundSteem = 164202472.142f
        val steemPower = Converter.toSteemPowerFromVest(vestingAmount, totalVestingShare, totalVestingFundSteem)
        assertTrue(steemPower > 130000.0f)
    }

}