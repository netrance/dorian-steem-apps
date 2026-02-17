package lee.dorian.steem_ui.main

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import lee.dorian.steem_ui.R
import lee.dorian.steem_ui.ext.startDownvoteListActivity
import lee.dorian.steem_ui.ext.startUpvoteListActivity
import lee.dorian.steem_ui.model.navigation.PostContentRoute
import lee.dorian.steem_ui.model.navigation.ProfileScreenRoute
import lee.dorian.steem_ui.model.navigation.TagsScreenRoute
import lee.dorian.steem_ui.model.navigation.WalletScreenRoute
import lee.dorian.steem_ui.ui.post.content.PostScreen
import lee.dorian.steem_ui.ui.profile.ProfileScreen
import lee.dorian.steem_ui.ui.tags.TagsScreen
import lee.dorian.steem_ui.ui.wallet.SteemitWalletScreen

/**
 * Sealed class defining navigation destinations
 *
 * @param T Route type
 * @param route Navigation route
 * @param label Screen label
 * @param iconId Icon resource ID to display in BottomBar (null if not shown in BottomBar)
 */
sealed class Screen<T : Any>(val route: T, val label: String, val iconId: Int?) {
    /**
     * Tags screen (main tab)
     */
    data object Tags : Screen<TagsScreenRoute>(
        route = TagsScreenRoute(""),
        label = "Tags",
        iconId = R.drawable.ic_bottom_menu_tags
    )

    /**
     * Profile screen (main tab)
     */
    data object Profile : Screen<ProfileScreenRoute>(
        route = ProfileScreenRoute(""),
        label = "Profile",
        iconId = R.drawable.ic_bottom_menu_profile
    )

    /**
     * Wallet screen (main tab)
     */
    data object Wallet : Screen<WalletScreenRoute>(
        route = WalletScreenRoute(""),
        label = "Wallet",
        iconId = R.drawable.ic_bottom_menu_wallet
    )
}

/**
 * Defines the app's navigation graph
 *
 * @param navController Navigation controller
 * @param startDestination Starting screen
 * @param modifier Modifier
 */
@Composable
fun AppNavigation(
    navController: NavHostController,
    startDestination: Any,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        // Tags screen
        composable<TagsScreenRoute> {
            TagsScreen(
                onPostItemClick = { postItem ->
                    navController.navigate(
                        PostContentRoute(
                            author = postItem.account,
                            permlink = postItem.permlink
                        )
                    )
                }
            )
        }

        // Profile screen
        composable<ProfileScreenRoute> { backStackEntry ->
            val params: ProfileScreenRoute = backStackEntry.toRoute()
            ProfileScreen(
                account = params.account
            )
        }

        // Wallet screen
        composable<WalletScreenRoute> {
            SteemitWalletScreen(initialAccount = "")
        }

        // Post Content screen (detail screen)
        composable<PostContentRoute> {
            val context = LocalContext.current
            PostScreen(
                onUpvoteClick = { activeVotes ->
                    context.startUpvoteListActivity(activeVotes)
                },
                onDownvoteClick = { activeVotes ->
                    context.startDownvoteListActivity(activeVotes)
                }
            )
        }
    }
}
