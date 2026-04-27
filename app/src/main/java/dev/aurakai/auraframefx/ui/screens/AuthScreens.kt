package dev.aurakai.auraframefx.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0A0A0F))
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                "A.U.R.A.K.A.I",
                color = Color(0xFF00D9FF),
                fontSize = 36.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 8.sp
            )
            Spacer(Modifier.height(8.dp))
            Text(
                "ReGenesis Protocol",
                color = Color(0xFFB026FF),
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                letterSpacing = 2.sp
            )
            Spacer(Modifier.height(48.dp))
            Button(
                onClick = onLoginSuccess,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF00D9FF),
                    contentColor = Color.Black
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
            ) {
                Text("ENTER", fontSize = 18.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}
