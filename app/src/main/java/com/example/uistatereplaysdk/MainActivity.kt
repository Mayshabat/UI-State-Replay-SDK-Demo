package com.example.uistatereplaysdk

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.example.replaysdk.replay.Replay
import com.example.replaysdk.replay.ReplayOverlay
import com.example.uistatereplaysdk.ui.theme.UIStateReplaySDKTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Cloud API base URL (Render)
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
    // Demo app state (normal app state)
    var screen by remember { mutableStateOf(Screen.Login) }
    var selectedProductId by remember { mutableStateOf("p1") }
    var cart by remember { mutableStateOf(listOf<String>()) } // productIds

    fun resetToLogin() {
        screen = Screen.Login
        selectedProductId = "p1"
        cart = emptyList()
    }

    fun handleReplayTag(tag: String) {
        // Library replays Event(type="CLICK", screen="<tag>")
        when {
            tag == "Login" -> screen = Screen.Shop

            tag == "GoCheckout" -> screen = Screen.Checkout
            tag == "BackShop" -> screen = Screen.Shop
            tag == "Reset" -> resetToLogin()

            tag.startsWith("Open_") -> {
                val id = tag.removePrefix("Open_")
                selectedProductId = id
                screen = Screen.Product
            }

            tag.startsWith("Add_") -> {
                val id = tag.removePrefix("Add_")
                cart = cart + id
            }
        }
    }

    Scaffold(
        topBar = { CenterAlignedTopAppBar(title = { Text("UI State Replay – Demo") }) },

        // ✅ SDK controls are inside the LIBRARY
        floatingActionButton = {
            ReplayOverlay(
                delayMs = 650,
                onReplayEvent = { e ->
                    if (e.type == "CLICK") handleReplayTag(e.screen)
                }
            )
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            when (screen) {
                Screen.Login -> LoginScreen(
                    onLogin = { screen = Screen.Shop }
                )

                Screen.Shop -> ShopScreen(
                    products = demoProducts,
                    cartCount = cart.size,
                    highlightKey = null,
                    onOpenProduct = { p ->
                        selectedProductId = p.id
                        screen = Screen.Product
                    },
                    onGoCheckout = { screen = Screen.Checkout }
                )

                Screen.Product -> ProductScreen(
                    product = findProduct(selectedProductId),
                    highlightKey = null,
                    onAddToCart = { cart = cart + selectedProductId },
                    onBackToShop = { screen = Screen.Shop },
                    onGoCheckout = { screen = Screen.Checkout }
                )

                Screen.Checkout -> CheckoutScreen(
                    items = cart.map { findProduct(it) },
                    highlightKey = null,
                    onBackToShop = { screen = Screen.Shop },
                    onReset = { resetToLogin() }
                )
            }
        }
    }
}
