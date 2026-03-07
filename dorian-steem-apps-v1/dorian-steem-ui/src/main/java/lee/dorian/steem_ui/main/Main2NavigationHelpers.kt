package lee.dorian.steem_ui.main

import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.toRoute
import lee.dorian.steem_ui.model.navigation.AccountDetailsRoute
import lee.dorian.steem_ui.model.navigation.PostContentRoute
import lee.dorian.steem_ui.model.navigation.PostListRoute
import lee.dorian.steem_ui.model.navigation.ProfileScreenRoute
import lee.dorian.steem_ui.model.navigation.TagsScreenRoute
import lee.dorian.steem_ui.model.navigation.WalletScreenRoute

/**
 * Returns the TopBar title based on the current screen
 *
 * @param destination Current navigation destination
 * @param navBackStackEntry Current back stack entry (for accessing parameters)
 * @return Title string to display in TopBar
 */
fun getTopBarTitle(
    destination: NavDestination?,
    navBackStackEntry: NavBackStackEntry?
): String {
    return when {
        destination?.hasRoute<TagsScreenRoute>() == true -> {
            "Tags - Steemit"
        }
        destination?.hasRoute<ProfileScreenRoute>() == true -> {
            val route = navBackStackEntry?.toRoute<ProfileScreenRoute>()
            when {
                (route?.account?.isNotEmpty() == true) -> "Profile - @${route?.account}"
                else -> "Profile"
            }
        }
        destination?.hasRoute<WalletScreenRoute>() == true -> {
            "Wallet"
        }
        destination?.hasRoute<PostListRoute>() == true -> {
            val route = navBackStackEntry?.toRoute<PostListRoute>()
            "${route?.sort?.replaceFirstChar { it.uppercase() }} - @${route?.account}"
        }
        destination?.hasRoute<PostContentRoute>() == true -> {
            val route = navBackStackEntry?.toRoute<PostContentRoute>()
            "Post - @${route?.author}"
        }
        destination?.hasRoute<AccountDetailsRoute>() == true -> {
            val route = navBackStackEntry?.toRoute<AccountDetailsRoute>()
            "Account Details - @${route?.account}"
        }
        else -> "Steemit"
    }
}

/**
 * Returns whether to show BottomBar on the current screen
 *
 * @param destination Current navigation destination
 * @return Whether to show BottomBar
 */
fun shouldShowBottomBar(
    destination: NavDestination?,
    navBackStackEntry: NavBackStackEntry? = null
): Boolean {
    return when {
        destination?.hasRoute<TagsScreenRoute>() == true -> true
        destination?.hasRoute<ProfileScreenRoute>() == true -> {
            navBackStackEntry?.toRoute<ProfileScreenRoute>()?.account?.isEmpty() ?: true
        }
        destination?.hasRoute<WalletScreenRoute>() == true -> true
        else -> false
    }
}

/**
 * Returns whether to show the back button on the current screen
 * Hidden on main tab screens (Tags, Profile, Wallet), shown on detail screens.
 *
 * @param destination Current navigation destination
 * @return Whether to show back button
 */
fun shouldShowBackButton(destination: NavDestination?): Boolean {
    return when {
        destination?.hasRoute<TagsScreenRoute>() == true -> false
        destination?.hasRoute<ProfileScreenRoute>() == true -> false
        destination?.hasRoute<WalletScreenRoute>() == true -> false
        else -> true
    }
}
