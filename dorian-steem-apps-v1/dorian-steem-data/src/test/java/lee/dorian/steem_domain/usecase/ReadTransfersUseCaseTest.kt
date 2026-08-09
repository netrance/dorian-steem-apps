package lee.dorian.steem_domain.usecase

import kotlinx.coroutines.test.runTest
import lee.dorian.steem_data.repository.SteemWorldRepositoryImpl
import lee.dorian.steem_domain.model.ApiResult
import lee.dorian.steem_domain.model.Transfer
import lee.dorian.steem_test.CommonPartOfViewModelTest
import lee.dorian.steem_test.TestData
import org.junit.Assert
import org.junit.Test

class ReadTransfersUseCaseTest : CommonPartOfViewModelTest() {

    private val readOutgoingTransfersUseCase = ReadOutgoingTransfersUseCase(
        SteemWorldRepositoryImpl(dispatcher),
        dispatcher
    )

    private val readIncomingTransfersUseCase = ReadIncomingTransfersUseCase(
        SteemWorldRepositoryImpl(dispatcher),
        dispatcher
    )

    private fun testTransferList(transferList: List<Transfer>) {
        for (transfer in transferList) {
            Assert.assertTrue(transfer.time.isNotEmpty())
            Assert.assertTrue(transfer.from.isNotEmpty())
            Assert.assertTrue(transfer.to.isNotEmpty())
            // The amount is formatted as "<number> <unit>". (e.g. "18.868 STEEM")
            Assert.assertEquals(2, transfer.amount.split(" ").size)
            Assert.assertTrue(transfer.amount.split(" ")[0].toFloat() > 0f)
        }
    }

    // Test case 1: Valid account — every transfer is sent by the account.
    @Test
    fun readOutgoingTransfers_case1() = runTest {
        val apiResult = readOutgoingTransfersUseCase(TestData.singleAccount)
        Assert.assertTrue(apiResult is ApiResult.Success)

        with(apiResult as ApiResult.Success) {
            Assert.assertTrue(data.size <= ReadOutgoingTransfersUseCase.DEFAULT_LIMIT)
            testTransferList(data)
            for (transfer in data) {
                Assert.assertEquals(TestData.singleAccount, transfer.from)
            }
        }
    }

    // Test case 2: Invalid account — SDS answers with HTTP 200 and a non-zero code,
    // which has to be reported as a failure.
    @Test
    fun readOutgoingTransfers_case2() = runTest {
        val apiResult = readOutgoingTransfersUseCase(TestData.invalidSingleAccount)
        Assert.assertTrue(apiResult is ApiResult.Failure)
        Assert.assertTrue((apiResult as ApiResult.Failure).content.isNotEmpty())
    }

    // Test case 1: Valid account — every transfer is received by the account.
    @Test
    fun readIncomingTransfers_case1() = runTest {
        val apiResult = readIncomingTransfersUseCase(TestData.singleAccount)
        Assert.assertTrue(apiResult is ApiResult.Success)

        with(apiResult as ApiResult.Success) {
            Assert.assertTrue(data.size <= ReadIncomingTransfersUseCase.DEFAULT_LIMIT)
            testTransferList(data)
            for (transfer in data) {
                Assert.assertEquals(TestData.singleAccount, transfer.to)
            }
        }
    }

    // Test case 2: Invalid account — SDS answers with HTTP 200 and a non-zero code,
    // which has to be reported as a failure.
    @Test
    fun readIncomingTransfers_case2() = runTest {
        val apiResult = readIncomingTransfersUseCase(TestData.invalidSingleAccount)
        Assert.assertTrue(apiResult is ApiResult.Failure)
        Assert.assertTrue((apiResult as ApiResult.Failure).content.isNotEmpty())
    }

    // Test case 3: The offset skips as many transfers as it is given.
    @Test
    fun readIncomingTransfers_case3() = runTest {
        val limit = 2
        val firstPage = readIncomingTransfersUseCase(TestData.singleAccount, limit = limit)
        val secondPage = readIncomingTransfersUseCase(TestData.singleAccount, offset = limit, limit = limit)
        Assert.assertTrue(firstPage is ApiResult.Success)
        Assert.assertTrue(secondPage is ApiResult.Success)

        val firstPageData = (firstPage as ApiResult.Success).data
        val secondPageData = (secondPage as ApiResult.Success).data
        Assert.assertTrue(firstPageData.size <= limit)
        Assert.assertTrue(secondPageData.size <= limit)
        if (firstPageData.size == limit && secondPageData.isNotEmpty()) {
            Assert.assertNotEquals(firstPageData[0], secondPageData[0])
        }
    }

}
