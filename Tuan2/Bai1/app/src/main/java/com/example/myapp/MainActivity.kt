package com.example.myapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface(modifier = Modifier.fillMaxSize(), color = Color.White) {
                    CalculatorScreen()
                }
            }
        }
    }
}

/** Mô tả một nút phép toán: ký hiệu + màu nền */
private data class Operation(val symbol: String, val color: Color)

private val operations = listOf(
    Operation("+", Color(0xFFD9534F)),   // đỏ
    Operation("-", Color(0xFFDDA94B)),   // vàng
    Operation("*", Color(0xFF5B3CE2)),   // tím
    Operation("/", Color(0xFF1C1C1C))    // đen
)

/** Hàm tính toán, trả về chuỗi kết quả hiển thị */
private fun calculate(aText: String, bText: String, op: String?): String {
    if (op == null) return ""
    val a = aText.toDoubleOrNull()
    val b = bText.toDoubleOrNull()
    if (a == null || b == null) return "Vui lòng nhập số hợp lệ"

    val result = when (op) {
        "+" -> a + b
        "-" -> a - b
        "*" -> a * b
        "/" -> {
            if (b == 0.0) return "Không thể chia cho 0"
            a / b
        }
        else -> return ""
    }
    // Bỏ phần ".0" nếu kết quả là số nguyên
    return if (result % 1.0 == 0.0) result.toLong().toString() else result.toString()
}

@Composable
fun CalculatorScreen() {
    // ----- State -----
    var number1 by remember { mutableStateOf("") }
    var number2 by remember { mutableStateOf("") }
    var selectedOp by remember { mutableStateOf<String?>(null) }

    // Kết quả được tính lại mỗi khi state thay đổi => hiển thị ngay lập tức
    val result = calculate(number1, number2, selectedOp)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp, vertical = 48.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Thực hành 03", fontSize = 16.sp)

        Spacer(modifier = Modifier.height(64.dp))

        // Ô nhập số thứ nhất
        OutlinedTextField(
            value = number1,
            onValueChange = { number1 = it },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Hàng 4 nút phép toán
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            operations.forEach { op ->
                val isSelected = selectedOp == op.symbol
                Button(
                    onClick = { selectedOp = op.symbol },
                    modifier = Modifier.size(width = 70.dp, height = 44.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = op.color),
                    border = if (isSelected) BorderStroke(3.dp, Color.Black) else null,
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Text(text = op.symbol, color = Color.White)
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Ô nhập số thứ hai
        OutlinedTextField(
            value = number2,
            onValueChange = { number2 = it },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Kết quả
        Text(
            text = "Kết quả: $result",
            modifier = Modifier.fillMaxWidth(),
            fontSize = 14.sp
        )
    }
}