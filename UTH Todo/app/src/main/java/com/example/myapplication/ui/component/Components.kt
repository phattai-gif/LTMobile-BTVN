package com.example.myapplication.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.ui.theme.AppColors

/* ---------- Logo ---------- */

/** Icon app: nền bo góc, 1 dòng đã tick + 1 dòng chưa tick. Mọi kích thước tính theo [size]. */
@Composable
fun AppLogoIcon(
    size: Dp,
    background: Color = AppColors.Primary,
    modifier: Modifier = Modifier
) {
    val dark = background == AppColors.White
    val fg = if (dark) AppColors.Primary else AppColors.White
    val fgSoft = if (dark) AppColors.PrimaryLight else AppColors.PrimaryLight

    Box(
        modifier
            .size(size)
            .clip(RoundedCornerShape(size * 0.26f))
            .background(background),
        contentAlignment = Alignment.Center
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(size * 0.1f)) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(size * 0.06f)) {
                Box(Modifier.size(size * 0.2f).clip(CircleShape).background(fg), contentAlignment = Alignment.Center) {
                    Icon(Icons.Default.Check, null, tint = if (dark) AppColors.White else AppColors.Primary, modifier = Modifier.size(size * 0.13f))
                }
                Box(Modifier.size(size * 0.3f, size * 0.09f).clip(CircleShape).background(fg))
            }
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(size * 0.06f)) {
                Box(Modifier.size(size * 0.2f).clip(CircleShape).border(size * 0.04f, fgSoft, CircleShape))
                Box(Modifier.size(size * 0.22f, size * 0.09f).clip(CircleShape).background(fgSoft))
            }
        }
    }
}

/** Chữ "UTH Todo" – UTH đậm, Todo màu nhấn. */
@Composable
fun AppLogoText(fontSize: TextUnit, uthColor: Color, todoColor: Color) {
    Text(
        buildAnnotatedString {
            withStyle(SpanStyle(fontWeight = FontWeight.ExtraBold, color = uthColor)) { append("UTH") }
            withStyle(SpanStyle(fontWeight = FontWeight.Medium, color = todoColor)) { append(" Todo") }
        },
        fontSize = fontSize
    )
}

/* ---------- Khung minh hoạ chung ---------- */

@Composable
private fun IllustrationFrame(content: @Composable ColumnScope.() -> Unit) {
    Column(
        Modifier
            .size(width = 200.dp, height = 210.dp)
            .clip(RoundedCornerShape(24.dp))
            .background(AppColors.Surface)
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        content = content
    )
}

@Composable
private fun WhiteCard(modifier: Modifier = Modifier, content: @Composable () -> Unit) {
    Box(
        modifier
            .shadow(3.dp, RoundedCornerShape(14.dp), ambientColor = Color(0x1A2F5BEA))
            .clip(RoundedCornerShape(14.dp))
            .background(AppColors.White)
            .padding(horizontal = 12.dp, vertical = 12.dp)
    ) { content() }
}

/* ---------- 3 minh hoạ ---------- */

@Composable
fun ChecklistIllustration() = IllustrationFrame {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        repeat(3) { i ->
            WhiteCard(Modifier.fillMaxWidth()) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    if (i == 0) {
                        Box(Modifier.size(18.dp).clip(CircleShape).background(AppColors.Primary), contentAlignment = Alignment.Center) {
                            Icon(Icons.Default.Check, null, tint = AppColors.White, modifier = Modifier.size(12.dp))
                        }
                    } else {
                        Box(Modifier.size(18.dp).clip(CircleShape).border(1.5.dp, AppColors.Dot, CircleShape))
                    }
                    Box(Modifier.fillMaxWidth().height(5.dp).clip(CircleShape).background(AppColors.Bar))
                }
            }
        }
    }
}

@Composable
fun CategoryIllustration() = IllustrationFrame {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        TaskCardWithTag("Học tập", AppColors.TagStudyBg, AppColors.Primary)
        TaskCardWithTag("Cuộc sống", AppColors.TagLifeBg, AppColors.TagLifeText)
    }
}

@Composable
private fun TaskCardWithTag(label: String, bg: Color, textColor: Color) {
    WhiteCard(Modifier.fillMaxWidth()) {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(
                label,
                color = textColor, fontSize = 11.sp, fontWeight = FontWeight.Medium,
                modifier = Modifier.clip(RoundedCornerShape(6.dp)).background(bg).padding(horizontal = 8.dp, vertical = 3.dp)
            )
            Box(Modifier.fillMaxWidth().height(5.dp).clip(CircleShape).background(AppColors.Bar))
        }
    }
}

@Composable
fun BellIllustration() = IllustrationFrame {
    Box(
        Modifier.size(72.dp).clip(CircleShape).background(AppColors.White),
        contentAlignment = Alignment.Center
    ) {
        Icon(Icons.Default.Notifications, null, tint = AppColors.Primary, modifier = Modifier.size(34.dp))
    }
    Spacer(Modifier.height(14.dp))
    WhiteCard {
        Text("Ôn bài Toán lúc 19:00", fontSize = 11.sp, color = AppColors.Ink, fontWeight = FontWeight.Medium)
    }
}