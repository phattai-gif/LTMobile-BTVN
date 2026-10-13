package com.example.myapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { MyFirstApp() }
    }
}

@Composable
fun MyFirstApp() {
    // 0: màn ban đầu, 1: sau khi nhấn lần 1, 2: sau khi nhấn lần 2
    var step by remember { mutableIntStateOf(0) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(vertical = 64.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // TextView 1
        Text(
            text = if (step == 0) "TextView" else "My First App",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )

        // TextView 2
        Text(
            text = when (step) {
                0 -> buildAnnotatedString {
                    withStyle(SpanStyle(fontWeight = FontWeight.Bold)) { append("TextView") }
                }
                1 -> buildAnnotatedString { append("Hello") }
                else -> buildAnnotatedString {
                    append("I'm\n")
                    withStyle(SpanStyle(fontWeight = FontWeight.Bold)) { append("Nguyen Van A") }
                }
            },
            fontSize = if (step == 0) 24.sp else 18.sp,
            textAlign = TextAlign.Center
        )

        // Button
        Button(
            onClick = { if (step < 2) step++ },   // xử lý sự kiện nhấn nút
            modifier = Modifier.width(200.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0D5CB6))
        ) {
            Text(
                text = if (step == 0) "Button" else "Say Hi!",
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
        }
    }
}