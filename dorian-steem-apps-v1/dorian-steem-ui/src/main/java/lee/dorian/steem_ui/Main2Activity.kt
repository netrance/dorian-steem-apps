package lee.dorian.steem_ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import dagger.hilt.android.AndroidEntryPoint
import lee.dorian.steem_ui.model.navigation.ProfileScreenRoute
import lee.dorian.steem_ui.model.navigation.TagsScreenRoute
import lee.dorian.steem_ui.model.navigation.WalletScreenRoute
import lee.dorian.steem_ui.ui.profile.ProfileScreen
import lee.dorian.steem_ui.ui.tags.TagsScreen
import lee.dorian.steem_ui.ui.wallet.SteemitWalletScreen

// 1. 네비게이션 대상 정의
sealed class Screen<T: Any>(val route: T, val label: String, val iconId: Int) {
    object Tags : Screen<TagsScreenRoute>(
        route = TagsScreenRoute(""),
        label = "Tags",
        iconId = R.drawable.ic_bottom_menu_tags
    )

    object Profile : Screen<ProfileScreenRoute>(
        route = ProfileScreenRoute(""),
        label = "Profile",
        iconId = R.drawable.ic_bottom_menu_profile
    )

    object Wallet : Screen<WalletScreenRoute>(
        route = WalletScreenRoute(""),
        label = "Wallet",
        iconId = R.drawable.ic_bottom_menu_wallet
    )
}

@AndroidEntryPoint
class Main2Activity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(
                scrim = android.graphics.Color.TRANSPARENT
            )
        )
        setContent {
            Main2Screen()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Main2Screen() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    val items = listOf(
        Screen.Tags,
        Screen.Profile,
        Screen.Wallet
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    TopAppBarTitle(currentDestination?.label?.toString() ?: "Steemit")
                },
                actions = {},
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = topAppBarContainerColor,
                    titleContentColor = Color.White
                )
            )
        },
        bottomBar = {
            Main2BottomBar(navController, items)
        }
    ) { innerPadding ->
        AppNavigation(
            navController = navController,
            startDestination = TagsScreenRoute(""),
            modifier = Modifier.padding(innerPadding)
        )
    }
}

@Composable
fun TopAppBarTitle(
    title: String
) {
    Text(
        text = title,
        fontWeight = FontWeight.Bold
    )
}

@Composable
fun Main2BottomBar(
    navController: NavHostController,
    items: List<Screen<Any>>
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    NavigationBar {
        items.forEach { screen ->
            NavigationBarItem(
                icon = {
                    Icon(painter = painterResource(screen.iconId), contentDescription = screen.label)
                },
                label = {
                    Text(screen.label)
                },
                selected = currentDestination?.hierarchy?.any {
                    it.hasRoute(screen.route::class)
                } == true,
                onClick = {
                    navController.navigate(screen.route) {
                        // 탭 전환 시 스택 쌓임 방지 및 상태 유지
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }
    }
}

@Composable
fun AppNavigation(
    navController: NavHostController,
    startDestination: Any,
    modifier: Modifier
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {

        composable<TagsScreenRoute> {
            TagsScreen()
        }

        // 1. 프로필 화면 등록 (데이터 클래스 타입을 경로로 사용)
        composable<ProfileScreenRoute> { backStackEntry ->
            // 경로에서 파라미터 객체 추출
            val params: ProfileScreenRoute = backStackEntry.toRoute()

            // ProfileScreen 호출 시 객체 전달
            ProfileScreen(
                account = params.account
                //onBack = { navController.popBackStack() }
            )
        }

        composable<WalletScreenRoute> {
            SteemitWalletScreen(initialAccount = "")
        }

        // 예시: 메인 화면에서 이동 버튼
//        composable("Home") {
//            Button(onClick = {
//                val params = ProfileScreenRoute(account = "dorian", permlink = "hello-world")
//                navController.navigate(params) // 객체를 직접 전달
//            }) {
//                Text("Go to Profile")
//            }
//        }
    }
}