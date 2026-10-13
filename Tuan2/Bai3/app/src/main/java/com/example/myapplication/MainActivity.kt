package com.example.myapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// ---------- Bước 1: khai báo biến điểm ----------
val diem = 8.5

// ---------- Bước 2: hàm xếp loại dùng if / else ----------
fun xepLoai(diem: Double): String {
    return if (diem < 0 || diem > 10) {
        "Không hợp lệ"
    } else if (diem >= 8.0) {
        "Giỏi"
    } else if (diem >= 6.5) {
        "Khá"
    } else if (diem >= 5.0) {
        "Trung bình"
    } else {
        "Yếu"
    }
}

// ---------- Bước 3: chọn màu chữ / màu nền theo xếp loại bằng if ----------
fun mauChu(loai: String): Color {
    return if (loai == "Giỏi") {
        Color(0xFF2E7D32)
    } else if (loai == "Khá") {
        Color(0xFF1E55B8)
    } else if (loai == "Trung bình") {
        Color(0xFFB7791F)
    } else if (loai == "Yếu") {
        Color(0xFFC0392B)
    } else {
        Color(0xFF6B7280)
    }
}

fun mauNen(loai: String): Color {
    return if (loai == "Giỏi") {
        Color(0xFFE3F2E4)
    } else if (loai == "Khá") {
        Color(0xFFE3ECFB)
    } else if (loai == "Trung bình") {
        Color(0xFFFCEFD6)
    } else if (loai == "Yếu") {
        Color(0xFFFBE3E0)
    } else {
        Color(0xFFEDEEF2)
    }
}

private val NavyBlue = Color(0xFF2B4A85)
private val ScreenBg = Color(0xFFF2F2F7)
private val GrayText = Color(0xFF6B7280)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                XepLoaiScreen()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun XepLoaiScreen() {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("Xếp loại học lực", color = Color.White, fontWeight = FontWeight.Bold)
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = NavyBlue)
            )
        },
        containerColor = ScreenBg
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(16.dp)
        ) {
            MainScoreCard(diem)

            Spacer(modifier = Modifier.height(20.dp))

            // ---------- Bước 4: Kiểm tra thêm ----------
            Text(
                "KIỂM TRA THÊM",
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
                color = GrayText,
                letterSpacing = 0.8.sp
            )
            Spacer(modifier = Modifier.height(8.dp))

            val testScores = listOf(9.0, 7.0, 5.5, 3.0, 11.0)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color.White)
                    .padding(horizontal = 14.dp, vertical = 4.dp)
            ) {
                Column {
                    for ((index, score) in testScores.withIndex()) {
                        TestRow(score)
                        if (index < testScores.size - 1) {
                            HorizontalDivider(color = Color(0xFFEDEEF2))
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MainScoreCard(score: Double) {
    val loai = xepLoai(score)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Color.White)
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Điểm trung bình", fontSize = 12.sp, color = GrayText)
            Spacer(modifier = Modifier.height(4.dp))
            Text(score.toString(), fontSize = 44.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(12.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(mauNen(loai))
                    .padding(vertical = 10.dp)
            ) {
                Text(
                    text = loai,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = mauChu(loai)
                )
            }
        }
    }
}

@Composable
fun TestRow(score: Double) {
    val loai = xepLoai(score)
    // In "9" thay vì "9.0" cho gọn
    val scoreText = if (score % 1.0 == 0.0) score.toInt().toString() else score.toString()

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text("Điểm $scoreText", fontSize = 14.sp)

        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(6.dp))
                .background(mauNen(loai))
                .padding(horizontal = 8.dp, vertical = 3.dp)
        ) {
            Text(loai, fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = mauChu(loai))
        }
    }
}