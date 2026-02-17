package lee.dorian.steem_ui.main

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import lee.dorian.steem_ui.model.navigation.TagsScreenRoute
import lee.dorian.steem_ui.ui.compose.CustomTopAppBar

/**
 * Main screen for Main2Activity
 *
 * Uses Scaffold to manage TopBar, BottomBar, and Content.
 * - TopBar: Title changes dynamically based on current screen
 * - BottomBar: Shown only on main tab screens (hidden on PostContent, AccountDetails, etc.)
 * - Content: Screen navigation managed by AppNavigation
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Main2Screen() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    // Define main tab items
    val mainTabItems = listOf(
        Screen.Tags,
        Screen.Profile,
        Screen.Wallet
    )

    // Determine UI elements based on current screen
    val topBarTitle = getTopBarTitle(currentDestination, navBackStackEntry)
    val showBottomBar = shouldShowBottomBar(currentDestination)

    Scaffold(
        topBar = {
            CustomTopAppBar(
                title = topBarTitle
            )
        },
        bottomBar = {
            if (showBottomBar) {
                Main2BottomBar(
                    navController = navController,
                    items = mainTabItems
                )
            }
        }
    ) { innerPadding ->
        AppNavigation(
            navController = navController,
            startDestination = TagsScreenRoute(""),
            modifier = Modifier.padding(innerPadding)
        )
    }
}
