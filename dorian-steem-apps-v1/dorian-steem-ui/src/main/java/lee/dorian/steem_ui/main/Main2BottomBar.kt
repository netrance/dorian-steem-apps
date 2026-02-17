package lee.dorian.steem_ui.main

import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import lee.dorian.steem_ui.R

/**
 * Bottom navigation bar for Main2Activity
 *
 * @param navController Navigation controller
 * @param items List of tab items to display
 */
@Composable
fun Main2BottomBar(
    navController: NavHostController,
    items: List<Screen<out Any>>
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    NavigationBar {
        items.forEach { screen ->
            NavigationBarItem(
                icon = {
                    Icon(
                        painter = painterResource(
                            screen.iconId ?: R.drawable.ic_bottom_menu_profile
                        ),
                        contentDescription = screen.label
                    )
                },
                label = {
                    Text(screen.label)
                },
                selected = currentDestination?.hierarchy?.any {
                    it.hasRoute(screen.route::class)
                } == true,
                onClick = {
                    navController.navigate(screen.route) {
                        // Prevent stack buildup and maintain state when switching tabs
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
