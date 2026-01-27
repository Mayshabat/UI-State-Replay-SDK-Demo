package com.example.uistatereplaysdk

import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.replaysdk.replay.ReplayButton
import com.example.replaysdk.replay.ReplayOutlinedButton

@Composable
fun CheckoutScreen(
    items: List<Product>,
    onBackToShop: () -> Unit,
    onReset: () -> Unit
) {
    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {

        Text(
            text = "Checkout",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(Modifier.height(16.dp))

        if (items.isEmpty()) {
            Text("Cart is empty", style = MaterialTheme.typography.bodyMedium)
        } else {
            items.forEach { p ->
                Text(
                    text = "• ${p.emoji} ${p.name}  ₪${p.price}",
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(Modifier.height(6.dp))
            }
        }

        Spacer(Modifier.height(20.dp))

        ReplayOutlinedButton(
            tag = "Back_Shop",
            onClick = onBackToShop,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Back to Shop")
        }

        Spacer(Modifier.height(10.dp))

        ReplayButton(
            tag = "Reset_App",
            onClick = onReset,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Finish / Reset")
        }
    }
}
