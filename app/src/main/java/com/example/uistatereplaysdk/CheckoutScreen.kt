package com.example.uistatereplaysdk

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.replaysdk.replay.replayElement

@Composable
fun CheckoutScreen(
    items: List<Product>,
    onBackToShop: () -> Unit,
    onReset: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "Your Cart",
            style = MaterialTheme.typography.headlineSmall
        )

        if (items.isEmpty()) {
            Text("Your cart is empty.", style = MaterialTheme.typography.bodyMedium)
        } else {
            items.forEach { product ->
                Text("• ${product.name} (${product.price}₪)", style = MaterialTheme.typography.bodyLarge)
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        // תיקון: הוספת הבלוק { onBackToShop() } ל-Modifier
        Button(
            modifier = Modifier
                .fillMaxWidth()
                .replayElement("Back_Shop") { onBackToShop() },
            onClick = { /* מנוהל ע"י ה-Modifier בספרייה */ }
        ) {
            Text("Back to Shop")
        }

        Button(
            modifier = Modifier
                .fillMaxWidth()
                .replayElement("Reset_App") { onReset() },
            onClick = { /* מנוהל ע"י ה-Modifier בספרייה */ }
        ) {
            Text("Reset / Clear All")
        }
    }
}