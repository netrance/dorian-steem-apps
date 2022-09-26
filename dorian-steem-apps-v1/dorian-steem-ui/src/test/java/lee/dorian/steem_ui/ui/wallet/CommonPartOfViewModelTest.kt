package lee.dorian.steem_ui.ui.wallet

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import io.reactivex.android.plugins.RxAndroidPlugins
import io.reactivex.plugins.RxJavaPlugins
import io.reactivex.schedulers.Schedulers
import org.junit.AfterClass
import org.junit.BeforeClass
import org.junit.Rule

open class CommonPartOfViewModelTest {

    @Rule
    @JvmField
    val instantExecutorRule = InstantTaskExecutorRule()

    companion object {
        @JvmStatic
        @BeforeClass
        fun setUp() {
            // Refer to https://medium.com/@jungil.han/junit-rxjava-%EA%B7%B8%EB%A6%AC%EA%B3%A0-%EC%BB%B4%ED%8C%A8%EB%8B%88%EC%96%B8-%EC%98%A4%EB%B8%8C%EC%A0%9D%ED%8A%B8-e8d17b674bdd
            RxJavaPlugins.setIoSchedulerHandler {
                Schedulers.trampoline()
            }
            RxAndroidPlugins.setInitMainThreadSchedulerHandler {
                Schedulers.trampoline()
            }
        }

        @JvmStatic
        @AfterClass
        fun teardown() {
            RxJavaPlugins.reset()
            RxAndroidPlugins.reset()
        }
    }

}