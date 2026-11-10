package com.example.myapplication.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.ui.theme.AppColors

/* ---------- Thanh Navigation Bar chuẩn với nút '+' ở chính giữa ---------- */

private val PrimaryBlue = Color(0xFF2B5CE6)
private val TabUnselected = Color(0xFF94A3B8)

@Composable
fun UthBottomBar(
    currentRoute: String,
    onNavigateHome: () -> Unit,
    onNavigateList: () -> Unit,
    onNavigateNotifications: () -> Unit,
    onNavigateProfile: () -> Unit,
    onFabClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight(),
        contentAlignment = Alignment.BottomCenter
    ) {
        // Thanh nền trắng bên dưới
        Surface(
            color = Color.White,
            shadowElevation = 10.dp,
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .height(64.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                BottomTabItem(
                    modifier = Modifier.weight(1f),
                    label = "Trang chủ",
                    icon = Icons.Default.Home,
                    selected = currentRoute == "home",
                    onClick = onNavigateHome
                )

                BottomTabItem(
                    modifier = Modifier.weight(1f),
                    label = "Danh sách",
                    icon = Icons.AutoMirrored.Filled.List,
                    selected = currentRoute == "list",
                    onClick = onNavigateList
                )

                // Khoảng trống ở giữa dành cho nút FAB
                Spacer(modifier = Modifier.weight(1f))

                BottomTabItem(
                    modifier = Modifier.weight(1f),
                    label = "Thông báo",
                    icon = Icons.Default.Notifications,
                    selected = false,
                    onClick = onNavigateNotifications
                )

                BottomTabItem(
                    modifier = Modifier.weight(1f),
                    label = "Cài đặt",
                    icon = Icons.Default.Tune,
                    selected = false,
                    onClick = onNavigateProfile
                )
            }
        }

        // Nút tròn '+' màu xanh nằm lồng ngay chính giữa mép trên của thanh bar
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .navigationBarsPadding()
                .padding(bottom = 26.dp)
                .size(52.dp)
                .shadow(elevation = 6.dp, shape = CircleShape)
                .clip(CircleShape)
                .background(PrimaryBlue)
                .clickable(onClick = onFabClick),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Thêm công việc",
                tint = Color.White,
                modifier = Modifier.size(28.dp)
            )
        }
    }
}

@Composable
private fun BottomTabItem(
    modifier: Modifier = Modifier,
    label: String,
    icon: ImageVector,
    selected: Boolean,
    onClick: () -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxHeight()
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick
            ),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = if (selected) PrimaryBlue else TabUnselected,
            modifier = Modifier.size(24.dp)
        )
        Spacer(Modifier.height(4.dp))
        Text(
            text = label,
            fontSize = 11.sp,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
            color = if (selected) PrimaryBlue else TabUnselected
        )
    }
}

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
