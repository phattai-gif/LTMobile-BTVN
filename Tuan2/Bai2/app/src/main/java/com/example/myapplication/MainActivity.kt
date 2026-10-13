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
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// ---------- Bước 1: class Student ----------
data class Student(
    val name: String,
    val email: String?,
    val phone: String?
)

// ---------- Bước 2: danh sách 5 sinh viên (có null) ----------
val students = listOf(
    Student("Nguyễn Văn An", "an@sv.edu.vn", "0901 234 567"),
    Student("Trần Thị Bình", null, "0912 888 999"),
    Student("Lê Minh Chi", "chi@sv.edu.vn", null),
    Student("Phạm Quốc Dũng", null, null),
    Student("Võ Thanh Em", "em@sv.edu.vn", "0933 111 222")
)

private val NavyBlue = Color(0xFF2B4A85)
private val ScreenBg = Color(0xFFF2F2F7)
private val CardBg = Color.White
private val SummaryBg = Color(0xFFE4EAF4)
private val RedText = Color(0xFFC0392B)
private val GrayText = Color(0xFF6B7280)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                StudentListScreen()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudentListScreen() {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("Danh sách sinh viên", color = Color.White, fontWeight = FontWeight.Bold)
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
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            // ---------- Bước 3: dùng for để tạo các thẻ sinh viên ----------
            for (student in students) {
                StudentCard(student)
                Spacer(modifier = Modifier.height(12.dp))
            }

            // ---------- Bước 5: dùng for để đếm số SV thiếu email ----------
            var missingEmailCount = 0
            for (student in students) {
                if (student.email == null) missingEmailCount++
            }

            SummaryBar(total = students.size, missingEmail = missingEmailCount)
        }
    }
}

@Composable
fun StudentCard(student: Student) {
    // Container -> Box có nền, bo góc, padding
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(CardBg)
            .padding(horizontal = 14.dp, vertical = 12.dp)
    ) {
        Column {
            Text(student.name, fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
            Spacer(modifier = Modifier.height(4.dp))
            InfoRow(label = "Email", value = student.email)
            Spacer(modifier = Modifier.height(2.dp))
            InfoRow(label = "SĐT", value = student.phone)
        }
    }
}

/** Hiển thị "Nhãn: giá trị". Nếu value == null thì hiện "Chưa cập nhật" (chữ đỏ, nghiêng). */
@Composable
fun InfoRow(label: String, value: String?) {
    Row {
        Text("$label: ", fontSize = 12.sp, color = GrayText)

        // ---------- Bước 4: toán tử Elvis (?:) – tương đương ?? trong Dart ----------
        val displayText = value ?: "Chưa cập nhật"

        if (value == null) {
            Text(displayText, fontSize = 12.sp, color = RedText, fontStyle = FontStyle.Italic)
        } else {
            Text(displayText, fontSize = 12.sp, color = GrayText)
        }
    }
}

@Composable
fun SummaryBar(total: Int, missingEmail: Int) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(SummaryBg)
            .padding(horizontal = 14.dp, vertical = 12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row {
                Text("Tổng: ", fontSize = 13.sp, color = GrayText)
                Text("$total sinh viên", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = NavyBlue)
            }
            Row {
                Text("Thiếu email: ", fontSize = 13.sp, color = GrayText)
                Text("$missingEmail", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = RedText)
            }
        }
    }
}