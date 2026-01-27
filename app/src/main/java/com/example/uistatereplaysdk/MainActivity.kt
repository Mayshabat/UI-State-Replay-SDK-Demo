package com.example.uistatereplaysdk

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.enableEdgeToEdge
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.replaysdk.replay.Replay
import com.example.replaysdk.replay.ReplayNavigator
import com.example.replaysdk.replay.ReplayOverlay
import com.example.uistatereplaysdk.ui.theme.UIStateReplaySDKTheme
import kotlinx.coroutines.delay

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

    var screenState by remember { mutableStateOf(Screen.Login) }
    var selectedProductId by remember { mutableStateOf("p1") }
    var cart by remember { mutableStateOf(listOf<String>()) }

    // Attach navigator once (SDK drives replay)
    LaunchedEffect(Unit) {
        Replay.attachNavigator(object : ReplayNavigator {

            override fun goTo(screen: String) {
                screenState = Screen.from(screen, fallback = Screen.Login)

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

    // Track screen changes for recording
    LaunchedEffect(screenState) {
        Replay.trackScreen(screenState.name)
    }

    // ✅ Keep a lightweight ticker to refresh the UI while replaying
    // This is safer than LaunchedEffect(Replay.isReplaying()) because isReplaying() is not Compose state.
    var tick by remember { mutableStateOf(0) }
    LaunchedEffect(Unit) {
        while (true) {
            if (Replay.isReplaying()) tick++
            delay(120)
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("UI Replay Store") }
            )
        },
        floatingActionButton = { ReplayOverlay() }
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {

            // App screens area
            Box(modifier = Modifier.weight(1f).fillMaxWidth()) {
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
                        onAddToCart = {
                            if (!cart.contains(selectedProductId)) cart = cart + selectedProductId
                        },
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

            // ✅ Show clicks ONLY during REPLAY
            if (Replay.isReplaying()) {
                // ✅ read tick so Compose will recompose and refresh the text live
                val tickValue = tick // רק כדי "לקרוא" את ה-state ולגרום לרה-קומפוזיציה


                Text(
                    text =
                        "REPLAY NOW: ${Replay.currentHighlightTag() ?: "-"}\n\n" +
                                "Replay clicks:\n" +
                                Replay.getReplayClickFeedText(12),
                    modifier = Modifier.padding(12.dp)
                )
            }
        }
    }
}
