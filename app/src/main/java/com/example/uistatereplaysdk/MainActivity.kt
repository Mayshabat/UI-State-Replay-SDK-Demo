package com.example.uistatereplaysdk

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.replaysdk.replay.Event
import com.example.replaysdk.replay.Replay
import com.example.replaysdk.replay.ReplayOverlay
import com.example.replaysdk.replay.LocalReplayHighlightTag
import com.example.uistatereplaysdk.ui.theme.UIStateReplaySDKTheme
import androidx.compose.material3.MaterialTheme
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // אתחול ה-SDK
        Replay.init("https://ui-state-replay-sdk.onrender.com/")
        enableEdgeToEdge()
        setContent {
            UIStateReplaySDKTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    DemoApp()
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DemoApp() {
    var screen by remember { mutableStateOf(Screen.Login) }
    var selectedProductId by remember { mutableStateOf("p1") }
    var cart by remember { mutableStateOf(listOf<String>()) }
    var highlightKey by remember { mutableStateOf<String?>(null) }
    var replayBanner by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(screen) {
        Replay.trackScreen(screen.name)
    }

    fun handleReplayEvent(e: Event) {
        replayBanner = when (e.type) {
            "REPLAY_START" -> "🔄 Replay started"
            "SCREEN" -> "📱 Moving to: ${e.screen}"
            "CLICK" -> "👆 Clicked: ${e.target}"
            "REPLAY_END" -> "✅ Replay finished"
            else -> null
        }

        when (e.type) {
            "REPLAY_START" -> {
                screen = Screen.Login
                cart = emptyList()
                highlightKey = null
            }
            "SCREEN" -> {
                runCatching { screen = Screen.valueOf(e.screen) }
            }
            "CLICK" -> {
                val tag = e.target ?: return
                when {
                    tag == "Login_Btn" -> screen = Screen.Shop
                    tag == "Go_Checkout" -> screen = Screen.Checkout
                    tag == "Back_Shop" -> screen = Screen.Shop
                    tag == "Reset_App" -> { screen = Screen.Login; cart = emptyList() }
                    tag.startsWith("Open_") -> {
                        selectedProductId = tag.removePrefix("Open_")
                        screen = Screen.Product
                    }
                    tag.startsWith("Add_") -> {
                        val pid = tag.removePrefix("Add_")
                        if (!cart.contains(pid)) cart = cart + pid
                    }
                }
            }
        }
    }

    CompositionLocalProvider(LocalReplayHighlightTag provides highlightKey) {
        Scaffold(
            topBar = { CenterAlignedTopAppBar(title = { Text("UI Replay Store") }) },
            floatingActionButton = {
                ReplayOverlay(
                    onReplayEvent = ::handleReplayEvent,
                    onHighlightChanged = { highlightKey = it }
                )
            }
        ) { innerPadding ->
            Box(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
                when (screen) {
                    Screen.Login -> LoginScreen(onLogin = { screen = Screen.Shop })
                    Screen.Shop -> ShopScreen(
                        products = demoProducts,
                        cartCount = cart.size,
                        onOpenProduct = { p -> selectedProductId = p.id; screen = Screen.Product },
                        onGoCheckout = { screen = Screen.Checkout }
                    )
                    Screen.Product -> ProductScreen(
                        product = findProduct(selectedProductId),
                        onAddToCart = { cart = cart + selectedProductId },
                        onBackToShop = { screen = Screen.Shop },
                        onGoCheckout = { screen = Screen.Checkout }
                    )
                    Screen.Checkout -> CheckoutScreen(
                        items = cart.map { findProduct(it) },
                        onBackToShop = { screen = Screen.Shop },
                        onReset = { screen = Screen.Login; cart = emptyList() }
                    )
                }

                if (replayBanner != null) {
                    Snackbar(
                        modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 90.dp)
                    ) { Text(replayBanner!!) }
                }
            }
        }
    }
}