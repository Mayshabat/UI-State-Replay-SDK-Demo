package com.example.uistatereplaysdk

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.replaysdk.replay.ReplayButton

@Composable
fun CheckoutScreen(
    items: List<Product>,
    highlightKey: String?,
    onBackToShop: () -> Unit,
    onReset: () -> Unit
) {
    Column(
        modifier = Modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("Checkout")

        if (items.isEmpty()) {
            Text("Cart is empty.")
        } else {
            items.forEach { p ->
                Text("- ${p.emoji} ${p.name} (${p.price}₪)")
            }
        }

        Spacer(Modifier.height(8.dp))

        ReplayButton(tag = "BackShop", onClick = onBackToShop, modifier = Modifier.fillMaxWidth()) {
            Text("Back to shop")
        }

        ReplayButton(tag = "Reset", onClick = onReset, modifier = Modifier.fillMaxWidth()) {
            Text("Reset")
        }
    }
}
