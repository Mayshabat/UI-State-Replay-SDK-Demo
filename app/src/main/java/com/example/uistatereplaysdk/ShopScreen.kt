package com.example.uistatereplaysdk

import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.replaysdk.replay.ReplayButton

@Composable
fun ShopScreen(
    products: List<Product>,
    cartCount: Int,
    onOpenProduct: (Product) -> Unit,
    onGoCheckout: () -> Unit
) {
    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {

        Text("Shop", style = MaterialTheme.typography.headlineMedium)
        Spacer(Modifier.height(6.dp))
        Text("Cart: $cartCount items", style = MaterialTheme.typography.bodyMedium)

        Spacer(Modifier.height(16.dp))

        products.forEach { p ->
            ReplayButton(
                tag = "Open_${p.id}",
                onClick = { onOpenProduct(p) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("${p.emoji} ${p.name}  ₪${p.price}")
            }

            Spacer(Modifier.height(10.dp))
        }

        Spacer(Modifier.height(12.dp))

        ReplayButton(
            tag = "Go_Checkout",
            onClick = onGoCheckout,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Go to Checkout")
        }
    }
}
