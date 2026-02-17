package lee.dorian.steem_ui.main

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import dagger.hilt.android.AndroidEntryPoint

/**
 * Main Activity based on Jetpack Compose
 *
 * This activity displays Main2Screen and supports screen navigation using Compose Navigation.
 * This is a new implementation replacing the existing MainActivity (Fragment-based).
 *
 * Related files:
 * - [Main2Screen]: Main screen UI (Scaffold, TopBar, BottomBar)
 * - [Main2Navigation]: Navigation graph and Screen sealed class
 * - [Main2BottomBar]: Bottom navigation bar
 * - [Main2NavigationHelpers]: Navigation helper functions
 */
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
