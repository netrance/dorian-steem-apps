package lee.dorian.steem_ui.main

import android.content.Context
import android.content.Intent
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import lee.dorian.dorian_android_ktx.android.context.getCurrentFragment
import lee.dorian.dorian_android_ktx.android.context.showToastShortly
import lee.dorian.steem_domain.model.AccountHistoryItemLink
import lee.dorian.steem_domain.model.PostItem
import lee.dorian.steem_ui.R
import lee.dorian.steem_ui.ext.showToastShortly
import lee.dorian.steem_ui.ext.startDownvoteListActivity
import lee.dorian.steem_ui.ext.startUpvoteListActivity
import lee.dorian.steem_ui.model.navigation.AccountDetailsRoute
import lee.dorian.steem_ui.model.navigation.AccountHistoryRoute
import lee.dorian.steem_ui.model.navigation.PostContentRoute
import lee.dorian.steem_ui.model.navigation.PostListRoute
import lee.dorian.steem_ui.model.navigation.ProfileScreenRoute
import lee.dorian.steem_ui.model.navigation.TagsScreenRoute
import lee.dorian.steem_ui.model.navigation.WalletScreenRoute
import lee.dorian.steem_ui.ui.account_details.AccountDetailsContent
import lee.dorian.steem_ui.ui.history.AccountHistoryScreen
import lee.dorian.steem_ui.ui.post.PostImagePagerActivity
import lee.dorian.steem_ui.ui.post.content.PostScreen
import lee.dorian.steem_ui.ui.post.list.PostListScreen
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
                        PostContentRoute(author = postItem.account, permlink = postItem.permlink)
                    )
                },
                onPostImageClick = { context, postItem ->
                    onPostImageClick(context, postItem)
                },
                onUpvoteClick = { context, activeVotes ->
                    context.startUpvoteListActivity(activeVotes)
                },
                onDownvoteClick = { context, activeVotes ->
                    context.startDownvoteListActivity(activeVotes)
                }
            )
        }

        // Profile screen
        composable<ProfileScreenRoute> { backStackEntry ->
            val params: ProfileScreenRoute = backStackEntry.toRoute()
            ProfileScreen(
                account = params.account,
                onAccountDetailsMenuClicked = { account ->
                    navController.navigate(AccountDetailsRoute(account))
                },
                onPostListMenuClicked = { account, sort ->
                    navController.navigate(PostListRoute(account, sort))
                },
                onAccountHistoryMenuClicked = { account ->
                    navController.navigate(AccountHistoryRoute(account))
                }
            )
        }

        // Wallet screen
        composable<WalletScreenRoute> {
            SteemitWalletScreen(initialAccount = "")
        }

        // Post list screen
        composable<PostListRoute> {
            PostListScreen(
                onPostItemClick = { postItem ->
                    navController.navigate(PostContentRoute(postItem.account, postItem.permlink))
                },
                onPostImageClick = { context, postItem ->
                    onPostImageClick(context, postItem)
                },
                onUpvoteClick = { context, activeVotes ->
                    context.startUpvoteListActivity(activeVotes)
                },
                onDownvoteClick = { context, activeVotes ->
                    context.startDownvoteListActivity(activeVotes)
                }
            )
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

        // Account Details screen
        composable<AccountDetailsRoute> { backStackEntry ->
            val params: AccountDetailsRoute = backStackEntry.toRoute()
            AccountDetailsContent()
        }

        // Account History screen
        composable<AccountHistoryRoute> { backStackEntry ->
            val params: AccountHistoryRoute = backStackEntry.toRoute()
            AccountHistoryScreen {
                onAccountHistoryItemClick(
                    navController = navController,
                    accountHistoryItemLink = it
                )
            }
        }

    }
}

fun onPostImageClick(context: Context, postItem: PostItem) {
    Intent(context, PostImagePagerActivity::class.java).also {
        if (postItem.imageURLs.isEmpty()) {
            context.showToastShortly(context.getString(R.string.error_no_post_image))
            return@also
        }
        val imageURLArrayList = ArrayList(postItem.imageURLs)
        it.putExtra(PostImagePagerActivity.INTENT_BUNDLE_IMAGE_URL_LIST, imageURLArrayList)
        context.startActivity(it)
    }
}

fun onAccountHistoryItemClick(
    navController: NavHostController,
    accountHistoryItemLink: AccountHistoryItemLink
) {
    when (accountHistoryItemLink.type) {
        "profile" -> {
            val account = accountHistoryItemLink.link.replace("@", "")
            navController.navigate(ProfileScreenRoute(account))
        }
        "post" -> {
            val linkElements = accountHistoryItemLink.link.split("/")
            val author = linkElements[0].replace("@", "")
            val permlink = linkElements[1]
            navController.navigate(PostContentRoute(author, permlink))
        }
        "wallet" -> {
            val account = accountHistoryItemLink.link.replace("@", "")
            navController.navigate(WalletScreenRoute(account))
        }
    }
}