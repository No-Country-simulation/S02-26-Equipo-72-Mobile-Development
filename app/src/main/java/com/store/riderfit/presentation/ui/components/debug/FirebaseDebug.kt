package com.store.riderfit.presentation.ui.components.debug

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.auth.FirebaseAuth

@Composable
fun FirebaseDebugInfo() {
    val firebaseAuth = FirebaseAuth.getInstance()
    val currentUser = firebaseAuth.currentUser
    val isConnected = firebaseAuth != null

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.Black.copy(alpha = 0.8f))
            .padding(8.dp)
    ) {
        Column {
            Text(
                "🔧 Firebase Debug Info",
                color = Color.Yellow,
                fontSize = 10.sp,
                fontFamily = FontFamily.Monospace
            )
            Text(
                "Firebase Connected: ${if (isConnected) "✅ YES" else "❌ NO"}",
                color = if (isConnected) Color.Green else Color.Red,
                fontSize = 9.sp,
                fontFamily = FontFamily.Monospace
            )
            Text(
                "Current User: ${currentUser?.uid ?: "null (No auth)"}",
                color = Color.Cyan,
                fontSize = 9.sp,
                fontFamily = FontFamily.Monospace
            )
            Text(
                "Email: ${currentUser?.email ?: "null"}",
                color = Color.Cyan,
                fontSize = 9.sp,
                fontFamily = FontFamily.Monospace
            )
        }
    }
}
