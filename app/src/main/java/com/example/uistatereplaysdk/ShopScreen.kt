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
fun ShopScreen(
    products: List<Product>,
    cartCount: Int,
    onOpenProduct: (Product) -> Unit,
    onGoCheckout: () -> Unit
) {
    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text("Shop (Cart: $cartCount)", style = MaterialTheme.typography.headlineSmall)

        products.forEach { p ->
            Button(
                // כאן התיקון: ה-onClick עובר לתוך ה-replayElement
                modifier = Modifier
                    .fillMaxWidth()
                    .replayElement("Open_${p.id}") { onOpenProduct(p) },
                onClick = { /* מנוהל ע"י ה-Modifier */ }
            ) {
                Text("${p.emoji} ${p.name}")
            }
        }

        Button(
            modifier = Modifier
                .fillMaxWidth()
                .replayElement("Go_Checkout") { onGoCheckout() },
            onClick = { /* מנוהל ע"י ה-Modifier */ }
        ) {
            Text("Go to Checkout")
        }
    }
}