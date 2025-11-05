package me.onvo.onvo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        // Install splash screen (native Android 12+ splash)
        val splashScreen = installSplashScreen()

        // Keep splash screen visible while app is loading
        var keepSplashScreen = true
        splashScreen.setKeepOnScreenCondition { keepSplashScreen }

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            App()
        }

        // Remove splash after a delay
        window.decorView.post {
            keepSplashScreen = false
        }
    }
}

@Preview
@Composable
fun AppAndroidPreview() {
    App()
}