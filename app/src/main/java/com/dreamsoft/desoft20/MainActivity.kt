package com.dreamsoft.desoft20

import android.os.Bundle
import android.view.KeyEvent
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.LaunchedEffect // <-- Import LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.lifecycleScope // <-- Import lifecycleScope
import androidx.navigation.compose.rememberNavController
import com.dreamsoft.desoft20.features.printer.SunmiPrinterManager
import com.dreamsoft.desoft20.ui.navigation.AppNavigation
import com.dreamsoft.desoft20.ui.navigation.Routes // <-- Import Routes
import com.dreamsoft.desoft20.ui.themes.AppTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.MutableSharedFlow // <-- Import MutableSharedFlow
import kotlinx.coroutines.flow.collectLatest // <-- Import collectLatest
import kotlinx.coroutines.launch // <-- Import launch

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private var volumeDownCount = 0
    private var volumeUpCount = 0
    private var keySequenceStartTime = 0L

    // SharedFlow to handle navigation events from outside Compose
    private val navigationEvents = MutableSharedFlow<String>(extraBufferCapacity = 1)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        setupFullScreen()
        SunmiPrinterManager.getInstance().initSunmiPrinterService(this)
        setContent {
            AppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Color.Transparent
                ) {
                    val navController = rememberNavController()

                    // Collect navigation events from the SharedFlow
                    LaunchedEffect(key1 = navigationEvents) {
                        navigationEvents.collectLatest { route ->
                            navController.navigate(route)
                        }
                    }

                    AppNavigation(navController = navController)
                }
            }
        }
    }

    private fun setupFullScreen() {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        val windowInsetsController = WindowCompat.getInsetsController(window, window.decorView)
        windowInsetsController.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        when (keyCode) {
            KeyEvent.KEYCODE_VOLUME_DOWN -> {
                if (event?.action == KeyEvent.ACTION_DOWN) {
                    volumeDownCount++
                    checkKeySequence()
                }
                // Return true to consume the event if you've handled it,
                // or super.onKeyDown if you want default system behavior.
                // For volume keys, usually allow system to handle volume changes too.
                return super.onKeyDown(keyCode, event)
            }

            KeyEvent.KEYCODE_VOLUME_UP -> {
                if (event?.action == KeyEvent.ACTION_DOWN) {
                    volumeUpCount++
                    if (volumeUpCount >= 2) { // Reset if just one up is pressed then down.
                        keySequenceStartTime = System.currentTimeMillis()
                        checkKeySequence()
                    }
                }
                return super.onKeyDown(keyCode, event)
            }

            KeyEvent.KEYCODE_MENU -> {
                if (event?.action == KeyEvent.ACTION_DOWN) {
                    navigateToConfiguration()
                    return true // Menu key press handled
                }
            }
        }
        return super.onKeyDown(keyCode, event)
    }

    private fun checkKeySequence() {
        val currentTime = System.currentTimeMillis()

        if (keySequenceStartTime == 0L && (volumeUpCount > 0 || volumeDownCount > 0) ) {
            // If sequence started with volume down, or only one volume up so far.
            // Start timer on any valid key if not already started by volume up == 2
             if (volumeUpCount < 2) keySequenceStartTime = currentTime
        }


        if (currentTime - keySequenceStartTime > 3000 && keySequenceStartTime != 0L) {
            resetKeyCount()
            return
        }

        if (volumeUpCount >= 2 && volumeDownCount >= 3) {
            navigateToConfiguration()
            resetKeyCount()
        }
    }

    private fun navigateToConfiguration() {
        // Emit the configuration route to the SharedFlow
        // Use lifecycleScope for coroutines tied to the Activity's lifecycle
        lifecycleScope.launch {
            navigationEvents.tryEmit(Routes.CONFIGURATION)
        }
    }

    private fun resetKeyCount() {
        volumeDownCount = 0
        volumeUpCount = 0
        keySequenceStartTime = 0L
    }
}
