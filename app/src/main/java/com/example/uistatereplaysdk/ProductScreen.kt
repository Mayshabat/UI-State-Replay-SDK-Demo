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
fun ProductScreen(
    product: Product,
    onAddToCart: () -> Unit,
    onBackToShop: () -> Unit,
    onGoCheckout: () -> Unit
) {
    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {

        Text(
            text = "${product.emoji} ${product.name}",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(Modifier.height(8.dp))

        Text(
            text = "Price: ₪${product.price}",
            style = MaterialTheme.typography.bodyLarge
        )

        Spacer(Modifier.height(20.dp))

        ReplayButton(
            tag = "Add_${product.id}",
            onClick = onAddToCart,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Add to Cart")
        }

        Spacer(Modifier.height(10.dp))

        ReplayButton(
            tag = "Go_Checkout",
            onClick = onGoCheckout,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Checkout")
        }

        Spacer(Modifier.height(10.dp))

        ReplayOutlinedButton(
            tag = "Back_Shop",
            onClick = onBackToShop,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Back")
        }
    }
}
