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
fun ProductScreen(
    product: Product,
    highlightKey: String?, // אפשר להשאיר (לא חובה)
    onAddToCart: () -> Unit,
    onBackToShop: () -> Unit,
    onGoCheckout: () -> Unit
) {
    Column(
        modifier = Modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("Product")
        Text("${product.emoji} ${product.name}")
        Text("${product.price}₪")

        Spacer(Modifier.height(8.dp))

        ReplayButton(tag = "Add_${product.id}", onClick = onAddToCart, modifier = Modifier.fillMaxWidth()) {
            Text("Add to cart")
        }

        ReplayButton(tag = "GoCheckout", onClick = onGoCheckout, modifier = Modifier.fillMaxWidth()) {
            Text("Checkout")
        }

        ReplayButton(tag = "BackShop", onClick = onBackToShop, modifier = Modifier.fillMaxWidth()) {
            Text("Back to Shop")
        }
    }
}
