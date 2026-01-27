package com.example.uistatereplaysdk

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme // פותר את השגיאה של ה-MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.replaysdk.replay.replayElement

@Composable
fun LoginScreen(onLogin: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxWidth().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(text = "Welcome 👋", style = MaterialTheme.typography.headlineMedium)

        Button(
            modifier = Modifier
                .fillMaxWidth()
                .replayElement("Login_Btn") { onLogin() }, // הפעולה עוברת כאן בצורה נכונה
            onClick = { /* משאירים ריק */ }
        ) {
            Text("Login")
        }
    }
}