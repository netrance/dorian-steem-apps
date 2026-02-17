package lee.dorian.steem_ui.main

import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.toRoute
import lee.dorian.steem_ui.model.navigation.AccountDetailsRoute
import lee.dorian.steem_ui.model.navigation.PostContentRoute
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
        destination?.hasRoute<TagsScreenRoute>() == true -> "Tags - Steemit"
        destination?.hasRoute<ProfileScreenRoute>() == true -> {
            val route = navBackStackEntry?.toRoute<ProfileScreenRoute>()
            if (route?.account?.isNotEmpty() == true) {
                "Profile - @${route.account}"
            } else {
                "Profile"
            }
        }
        destination?.hasRoute<WalletScreenRoute>() == true -> "Wallet"
        destination?.hasRoute<PostContentRoute>() == true -> "Post"
        destination?.hasRoute<AccountDetailsRoute>() == true -> "Account Details"
        else -> "Steemit"
    }
}

/**
 * Returns whether to show BottomBar on the current screen
 *
 * @param destination Current navigation destination
 * @return Whether to show BottomBar
 */
fun shouldShowBottomBar(destination: NavDestination?): Boolean {
    return when {
        destination?.hasRoute<PostContentRoute>() == true -> false
        destination?.hasRoute<AccountDetailsRoute>() == true -> false
        else -> true
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
