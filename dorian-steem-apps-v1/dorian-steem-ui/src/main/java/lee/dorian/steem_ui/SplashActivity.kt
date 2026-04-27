package lee.dorian.steem_ui

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import lee.dorian.steem_ui.main.Main2Activity

class SplashActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SplashScreen()
        }

        lifecycleScope.launch {
            delay(1300)
            startActivity(Intent(this@SplashActivity, Main2Activity::class.java))
            finish()
        }
    }

}

@Composable
fun SplashScreen() {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        Image(
            painter = painterResource(R.drawable.img_pircoin),
            contentDescription = "Splash image"
        )
    }
}

@Composable
@Preview(showBackground = true)
fun SplashScreenPreview() {
    SplashScreen()
}
