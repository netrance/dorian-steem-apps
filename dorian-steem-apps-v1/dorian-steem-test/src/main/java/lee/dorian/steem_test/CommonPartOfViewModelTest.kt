package lee.dorian.steem_test

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before

open class CommonPartOfViewModelTest {

    @kotlinx.coroutines.ExperimentalCoroutinesApi
    protected val dispatcher: CoroutineDispatcher = UnconfinedTestDispatcher()

    @Before
    @kotlinx.coroutines.ExperimentalCoroutinesApi
    fun setUp() {
        Dispatchers.setMain(dispatcher)
    }

    @After
    @kotlinx.coroutines.ExperimentalCoroutinesApi
    fun teardown() {
        Dispatchers.resetMain()
    }

}