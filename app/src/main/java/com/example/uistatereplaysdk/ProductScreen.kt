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
fun ProductScreen(
    product: Product,
    onAddToCart: () -> Unit,
    onBackToShop: () -> Unit,
    onGoCheckout: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "${product.emoji} ${product.name}",
            style = MaterialTheme.typography.headlineMedium
        )
        Text(
            text = "Price: ${product.price}₪",
            style = MaterialTheme.typography.bodyLarge
        )

        Spacer(modifier = Modifier.height(16.dp))

        // כפתור הוספה לסל - כבר היה נכון אצלך
        Button(
            modifier = Modifier
                .fillMaxWidth()
                .replayElement("Add_${product.id}") { onAddToCart() },
            onClick = { /* ה-Modifier מטפל בזה */ }
        ) {
            Text("Add to cart")
        }

        // תיקון: הוספת { onGoCheckout() } ל-Modifier
        Button(
            modifier = Modifier
                .fillMaxWidth()
                .replayElement("Go_Checkout") { onGoCheckout() },
            onClick = { /* ה-Modifier מטפל בזה */ }
        ) {
            Text("Checkout")
        }

        // תיקון: הוספת { onBackToShop() } ל-Modifier
        Button(
            modifier = Modifier
                .fillMaxWidth()
                .replayElement("Back_Shop") { onBackToShop() },
            onClick = { /* ה-Modifier מטפל בזה */ }
        ) {
            Text("Back")
        }
    }
}