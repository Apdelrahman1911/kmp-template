package me.onvo.onvo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import me.onvo.onvo.core.datastore.initPreferencesDataStore
import onvo.composeapp.generated.resources.Res
import onvo.composeapp.generated.resources.compose_multiplatform
import org.jetbrains.compose.resources.painterResource

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        // Install splash screen (native Android 12+ splash)
        initPreferencesDataStore(applicationContext)

        val splashScreen = installSplashScreen()

        // Keep splash screen visible while app is loading
        var keepSplashScreen = true
        splashScreen.setKeepOnScreenCondition { keepSplashScreen }

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
                Image(painterResource(Res.drawable.compose_multiplatform), null)


//            App()
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