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
fun ShopScreen(
    products: List<Product>,
    cartCount: Int,
    highlightKey: String?, // אפשר להשאיר (לא חובה)
    onOpenProduct: (Product) -> Unit,
    onGoCheckout: () -> Unit
) {
    Column(
        modifier = Modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("Shop (Cart: $cartCount)")

        products.forEach { p ->
            ReplayButton(
                tag = "Open_${p.id}",
                onClick = { onOpenProduct(p) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("${p.name} - ${p.price}₪")
            }
        }

        Spacer(Modifier.height(8.dp))

        ReplayButton(
            tag = "GoCheckout",
            onClick = onGoCheckout,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Go to Checkout")
        }
    }
}
