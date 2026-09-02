package lee.dorian.steem_domain.usecase

import kotlinx.coroutines.test.runTest
import lee.dorian.steem_data.repository.SteemWorldRepositoryImpl
import lee.dorian.steem_domain.model.ApiResult
import lee.dorian.steem_domain.model.Reward
import lee.dorian.steem_domain.model.RewardType
import lee.dorian.steem_test.CommonPartOfViewModelTest
import lee.dorian.steem_test.TestData
import org.junit.Assert
import org.junit.Test
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ReadRewardsUseCaseTest : CommonPartOfViewModelTest() {

    private val readAuthorRewardsUseCase = ReadAuthorRewardsUseCase(
        SteemWorldRepositoryImpl(dispatcher),
        dispatcher
    )

    private val readCurationRewardsUseCase = ReadCurationRewardsUseCase(
        SteemWorldRepositoryImpl(dispatcher),
        dispatcher
    )

    private fun testRewardList(rewardList: List<Reward>, type: RewardType) {
        for (reward in rewardList) {
            Assert.assertEquals(type, reward.type)
            Assert.assertTrue(reward.time.isNotEmpty())
            Assert.assertTrue(reward.author.isNotEmpty())
            Assert.assertTrue(reward.permlink.isNotEmpty())
            // The amount lists every unit that was paid out, formatted as "<number> <unit>".
            // (e.g. "0.323 SBD, 78.416 SP")
            for (paidUnit in reward.amount.split(", ")) {
                Assert.assertEquals(2, paidUnit.split(" ").size)
                Assert.assertTrue(paidUnit.split(" ")[0].toFloat() >= 0f)
            }
        }
    }

    // Test case 1: Valid account — every author reward belongs to a post of the account.
    @Test
    fun readAuthorRewards_case1() = runTest {
        val apiResult = readAuthorRewardsUseCase(TestData.singleAccount)
        Assert.assertTrue(apiResult is ApiResult.Success)

        with(apiResult as ApiResult.Success) {
            Assert.assertTrue(data.size <= ReadAuthorRewardsUseCase.MAX_REWARD_COUNT)
            testRewardList(data, RewardType.AUTHOR)
            for (reward in data) {
                Assert.assertEquals(TestData.singleAccount, reward.author)
            }
        }
    }

    // Test case 2: Invalid account — SDS answers with HTTP 200 and a non-zero code,
    // which has to be reported as a failure.
    @Test
    fun readAuthorRewards_case2() = runTest {
        val apiResult = readAuthorRewardsUseCase(TestData.invalidSingleAccount)
        Assert.assertTrue(apiResult is ApiResult.Failure)
        Assert.assertTrue((apiResult as ApiResult.Failure).content.isNotEmpty())
    }

    // Test case 1: Valid account — every curation reward is paid in Steem Power.
    @Test
    fun readCurationRewards_case1() = runTest {
        val apiResult = readCurationRewardsUseCase(TestData.singleAccount)
        Assert.assertTrue(apiResult is ApiResult.Success)

        with(apiResult as ApiResult.Success) {
            Assert.assertTrue(data.size <= ReadCurationRewardsUseCase.MAX_REWARD_COUNT)
            testRewardList(data, RewardType.CURATION)
            for (reward in data) {
                Assert.assertTrue(reward.amount.endsWith(" SP"))
            }
        }
    }

    // Test case 2: Invalid account — SDS answers with HTTP 200 and a non-zero code,
    // which has to be reported as a failure.
    @Test
    fun readCurationRewards_case2() = runTest {
        val apiResult = readCurationRewardsUseCase(TestData.invalidSingleAccount)
        Assert.assertTrue(apiResult is ApiResult.Failure)
        Assert.assertTrue((apiResult as ApiResult.Failure).content.isNotEmpty())
    }

    // Test case 3: rewards_api orders the rows by time in ascending order, so the use case
    // has to turn them around and report the latest reward of the page first.
    @Test
    fun readCurationRewards_case3() = runTest {
        val apiResult = readCurationRewardsUseCase(TestData.singleAccount)
        Assert.assertTrue(apiResult is ApiResult.Success)

        val data = (apiResult as ApiResult.Success).data
        for (i in 1 until data.size) {
            Assert.assertTrue(data[i - 1].time >= data[i].time)
        }
    }

    // Test case 4: Only the rewards that fall into the given time range are read.
    @Test
    fun readCurationRewards_case4() = runTest {
        // A year the account was already curating in.
        val fromTime = 1577836800L   // 2020-01-01 00:00 UTC
        val toTime = 1609372800L     // 2020-12-31 00:00 UTC
        val apiResult = readCurationRewardsUseCase(TestData.singleAccount2, fromTime, toTime)
        Assert.assertTrue(apiResult is ApiResult.Success)

        val data = (apiResult as ApiResult.Success).data
        Assert.assertTrue(data.isNotEmpty())
        for (reward in data) {
            Assert.assertTrue(reward.time >= fromTime.toLocalTimeString())
            Assert.assertTrue(reward.time <= toTime.toLocalTimeString())
        }
    }

    // Test case 5: A time range that holds more rewards than one call can return is read page
    // by page, and is cut off at its oldest end once it exceeds MAX_REWARD_COUNT.
    @Test
    fun readCurationRewards_case5() = runTest {
        val apiResult = readCurationRewardsUseCase(TestData.singleAccount2)
        Assert.assertTrue(apiResult is ApiResult.Success)

        val data = (apiResult as ApiResult.Success).data
        Assert.assertEquals(ReadCurationRewardsUseCase.MAX_REWARD_COUNT, data.size)
        testRewardList(data, RewardType.CURATION)
        for (i in 1 until data.size) {
            Assert.assertTrue(data[i - 1].time >= data[i].time)
        }

        // The rewards that were cut off are the oldest ones, so the first reward the account
        // ever curated must not have survived. It is read from the earliest days of the account,
        // a range small enough to be returned in full.
        val earliest = readCurationRewardsUseCase(TestData.singleAccount2, toTime = EARLY_HISTORY_TO_TIME)
        Assert.assertTrue(earliest is ApiResult.Success)
        val earliestData = (earliest as ApiResult.Success).data
        Assert.assertTrue(earliestData.isNotEmpty())
        Assert.assertTrue(earliestData.size < ReadCurationRewardsUseCase.MAX_REWARD_COUNT)
        Assert.assertTrue(earliestData.last().time < data.last().time)
    }

    private fun Long.toLocalTimeString(): String {
        val sdf = SimpleDateFormat(TIME_FORMAT, Locale.US)
        return sdf.format(Date(this * 1000L))
    }

    companion object {
        const val TIME_FORMAT = "yyyy-MM-dd HH:mm"

        // The first days the account was curating in, which hold only a handful of rewards.
        const val EARLY_HISTORY_TO_TIME = 1519000000L   // 2018-02-19 00:26 UTC
    }

}
