package com.example.uistatereplaysdk

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.enableEdgeToEdge
import androidx.activity.compose.setContent
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
import com.example.replaysdk.replay.ReplayNavigator
import com.example.replaysdk.replay.ReplayOverlay
import com.example.uistatereplaysdk.ui.theme.UIStateReplaySDKTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // SDK init
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

    // ✅ שינוי שם כדי שלא יתנגש עם הפרמטר screen ב-Navigator
    var screenState by remember { mutableStateOf(Screen.Login) }
    var selectedProductId by remember { mutableStateOf("p1") }
    var cart by remember { mutableStateOf(listOf<String>()) }

    // ✅ חיבור ניווט פעם אחת - עכשיו הספרייה תנווט בזמן REPLAY
    LaunchedEffect(Unit) {
        Replay.attachNavigator(object : ReplayNavigator {

            override fun goTo(screen: String) {
                // screen מגיע מההקלטה: "Login", "Shop", "Product", "Checkout"
                runCatching {
                    screenState = Screen.valueOf(screen)
                }
            }

            override fun back() {
                screenState = when (screenState) {
                    Screen.Product -> Screen.Shop
                    Screen.Checkout -> Screen.Shop
                    else -> Screen.Login
                }
            }

            override fun performAction(tag: String) {
                when {
                    tag.startsWith("Open_") -> {
                        selectedProductId = tag.removePrefix("Open_")
                        screenState = Screen.Product
                    }

                    tag.startsWith("Add_") -> {
                        val pid = tag.removePrefix("Add_")
                        if (!cart.contains(pid)) cart = cart + pid
                    }

                    tag == "Login_Btn" -> screenState = Screen.Shop
                    tag == "Go_Checkout" -> screenState = Screen.Checkout
                    tag == "Back_Shop" -> screenState = Screen.Shop

                    tag == "Reset_App" -> {
                        screenState = Screen.Login
                        cart = emptyList()
                    }
                }
            }
        })
    }

    // ✅ Track screen בכל שינוי מסך (חלק מההקלטה)
    LaunchedEffect(screenState) {
        Replay.trackScreen(screenState.name)
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("UI Replay Store") }
            )
        },
        floatingActionButton = {
            // ✅ Overlay של הספרייה - היא מנהלת Record/Stop/Replay
            ReplayOverlay()
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (screenState) {
                Screen.Login -> LoginScreen(onLogin = { screenState = Screen.Shop })

                Screen.Shop -> ShopScreen(
                    products = demoProducts,
                    cartCount = cart.size,
                    onOpenProduct = { p ->
                        selectedProductId = p.id
                        screenState = Screen.Product
                    },
                    onGoCheckout = { screenState = Screen.Checkout }
                )

                Screen.Product -> ProductScreen(
                    product = findProduct(selectedProductId),
                    onAddToCart = { cart = cart + selectedProductId },
                    onBackToShop = { screenState = Screen.Shop },
                    onGoCheckout = { screenState = Screen.Checkout }
                )

                Screen.Checkout -> CheckoutScreen(
                    items = cart.map { findProduct(it) },
                    onBackToShop = { screenState = Screen.Shop },
                    onReset = {
                        screenState = Screen.Login
                        cart = emptyList()
                    }
                )
            }
        }
    }
}
