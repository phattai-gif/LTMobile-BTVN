package com.example.myapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// ---------- Bước 1: class Student (score có thể null) ----------
data class Student(
    val name: String,
    val score: Double?
)

// Dữ liệu khai báo sẵn: An 9.0 · Bình 7.2 · Chi (chưa có) · Dũng 4.5 · Em 5.8
val students = listOf(
    Student("Nguyễn Văn An", 9.0),
    Student("Trần Thị Bình", 7.2),
    Student("Lê Minh Chi", null),
    Student("Phạm Quốc Dũng", 4.5),
    Student("Võ Thanh Em", 5.8)
)

// ---------- Hàm xếp loại & màu (dùng lại từ Bài 2) ----------
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

private val NavyBlue = Color(0xFF2B4A85)
private val ScreenBg = Color(0xFFF2F2F7)
private val GrayText = Color(0xFF6B7280)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                BangDiemScreen()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BangDiemScreen() {
    // ---------- Bước 3: dùng for + if để tính thống kê ----------
    var tong = 0.0            // tổng điểm (bỏ qua người chưa có điểm)
    var soCoDiem = 0          // số người đã có điểm
    var soGioi = 0            // số sinh viên Giỏi
    var soChuaCoDiem = 0      // số sinh viên chưa có điểm
    var caoNhat: Double? = null

    for (student in students) {
        val score = student.score
        if (score == null) {
            soChuaCoDiem++
        } else {
            tong += score
            soCoDiem++
            if (xepLoai(score) == "Giỏi") soGioi++
            if (caoNhat == null || score > caoNhat) caoNhat = score
        }
    }

    val diemTrungBinh = if (soCoDiem > 0) tong / soCoDiem else 0.0

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Bảng điểm lớp", color = Color.White, fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = NavyBlue)
            )
        },
        containerColor = ScreenBg
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            SummaryCard(
                average = diemTrungBinh,
                soGioi = soGioi,
                soChuaCoDiem = soChuaCoDiem,
                caoNhat = caoNhat
            )

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                "DANH SÁCH · ${students.size} SINH VIÊN",
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
                color = GrayText,
                letterSpacing = 0.8.sp
            )
            Spacer(modifier = Modifier.height(8.dp))

            // ---------- Bước 2: dùng for hiển thị tên, điểm, xếp loại ----------
            for (student in students) {
                StudentRow(student)
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

@Composable
fun SummaryCard(average: Double, soGioi: Int, soChuaCoDiem: Int, caoNhat: Double?) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(NavyBlue)
            .padding(16.dp)
    ) {
        Column {
            Text("Điểm trung bình lớp", fontSize = 12.sp, color = Color.White.copy(alpha = 0.75f))
            // Locale-independent: "%.2f" luôn cho dấu chấm khi dùng String.format với Locale.US
            Text(
                String.format(java.util.Locale.US, "%.2f", average),
                fontSize = 36.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            Spacer(modifier = Modifier.height(12.dp))
            HorizontalDivider(color = Color.White.copy(alpha = 0.2f))
            Spacer(modifier = Modifier.height(12.dp))

            Row(modifier = Modifier.fillMaxWidth()) {
                StatItem("Giỏi", soGioi.toString(), Modifier.weight(1f))
                StatItem("Chưa có điểm", soChuaCoDiem.toString(), Modifier.weight(1f))
                StatItem(
                    "Cao nhất",
                    if (caoNhat != null) String.format(java.util.Locale.US, "%.1f", caoNhat) else "—",
                    Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
fun StatItem(label: String, value: String, modifier: Modifier = Modifier) {
    Column(modifier = modifier) {
        Text(label, fontSize = 11.sp, color = Color.White.copy(alpha = 0.7f))
        Spacer(modifier = Modifier.height(2.dp))
        Text(value, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.White)
    }
}

@Composable
fun StudentRow(student: Student) {
    val score = student.score

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(Color.White)
            .padding(horizontal = 14.dp, vertical = 12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(student.name, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                Spacer(modifier = Modifier.height(2.dp))

                // Nếu score == null thì hiện "Chưa có điểm"
                if (score == null) {
                    Text("Chưa có điểm", fontSize = 11.sp, color = GrayText)
                } else {
                    val loai = xepLoai(score)
                    Text(loai, fontSize = 11.sp, fontWeight = FontWeight.Medium, color = mauChu(loai))
                }
            }

            if (score == null) {
                Text("—", fontSize = 20.sp, color = Color(0xFFB0B3BA))
            } else {
                Text(
                    String.format(java.util.Locale.US, "%.1f", score),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}