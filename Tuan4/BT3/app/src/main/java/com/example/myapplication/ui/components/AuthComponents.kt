package com.example.myapplication.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

object SmartColors {
    val Blue = Color(0xFF4A94EA)
    val Ink = Color(0xFF1F2430)
    val Muted = Color(0xFF7A7F8C)
    val Field = Color(0xFFF5F6FA)
    val FieldBorder = Color(0xFFDDE0E8)
    val Teal = Color(0xFF2F8F83)
    val Red = Color(0xFFD9362B)
    val ResultBox = Color(0xFFDCE9FA)
}

/** Logo UTH lấy từ res/drawable/uth_logo.png */
@Composable
fun UthLogo() {
    Image(
        painter = painterResource(com.example.myapplication.R.drawable.uth_logo),
        contentDescription = "UTH logo",
        modifier = Modifier.width(150.dp)
    )
}

@Composable
fun BackButton(onClick: () -> Unit) {
    Box(
        Modifier.size(40.dp).clip(RoundedCornerShape(12.dp)).background(SmartColors.Blue).clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = Color.White, modifier = Modifier.size(22.dp)) }
}

/** Khung chung của cả 4 màn: nút back, logo, SmartTasks, tiêu đề, mô tả. */
@Composable
fun AuthLayout(
    title: String,
    subtitle: String,
    onBack: (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        Modifier
            .fillMaxSize()
            .background(Color.White)
            .systemBarsPadding()
            .imePadding()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp, vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(Modifier.fillMaxWidth().height(40.dp)) { if (onBack != null) BackButton(onBack) }
        Spacer(Modifier.height(16.dp))
        UthLogo()
        Spacer(Modifier.height(10.dp))
        Text("SmartTasks", fontSize = 22.sp, fontWeight = FontWeight.Medium, color = SmartColors.Blue)
        Spacer(Modifier.height(30.dp))
        Text(title, fontSize = 20.sp, fontWeight = FontWeight.SemiBold, color = SmartColors.Ink)
        Spacer(Modifier.height(8.dp))
        Text(subtitle, fontSize = 13.sp, color = SmartColors.Muted, textAlign = TextAlign.Center, lineHeight = 19.sp)
        Spacer(Modifier.height(28.dp))
        content()
    }
}

@Composable
fun AuthTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    icon: ImageVector,
    modifier: Modifier = Modifier,
    isPassword: Boolean = false,
    readOnly: Boolean = false,
    keyboardType: KeyboardType = KeyboardType.Text
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier.fillMaxWidth(),
        placeholder = { Text(placeholder, fontSize = 14.sp, color = SmartColors.Muted) },
        leadingIcon = { Icon(icon, null, tint = SmartColors.Muted, modifier = Modifier.size(20.dp)) },
        singleLine = true,
        readOnly = readOnly,
        visualTransformation = if (isPassword) PasswordVisualTransformation() else VisualTransformation.None,
        keyboardOptions = KeyboardOptions(keyboardType = if (isPassword) KeyboardType.Password else keyboardType),
        shape = RoundedCornerShape(10.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = SmartColors.Field,
            unfocusedContainerColor = SmartColors.Field,
            focusedBorderColor = SmartColors.Blue,
            unfocusedBorderColor = SmartColors.FieldBorder
        )
    )
}

@Composable
fun PrimaryButton(text: String, enabled: Boolean = true, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = Modifier.fillMaxWidth().height(50.dp),
        shape = RoundedCornerShape(25.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = SmartColors.Blue,
            disabledContainerColor = SmartColors.Blue.copy(alpha = 0.45f),
            disabledContentColor = Color.White
        )
    ) { Text(text, fontSize = 15.sp, fontWeight = FontWeight.Medium, color = Color.White) }
}

@Composable
fun ErrorText(message: String) {
    Text(message, color = SmartColors.Red, fontSize = 12.sp, modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp))
}

/** Ô nhập OTP gồm [length] ô vuông, dùng 1 BasicTextField ẩn. */
@Composable
fun OtpInput(value: String, onValueChange: (String) -> Unit, length: Int = 5) {
    BasicTextField(
        value = value,
        onValueChange = { if (it.length <= length && it.all(Char::isDigit)) onValueChange(it) },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
        cursorBrush = SolidColor(Color.Transparent),
        decorationBox = {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                repeat(length) { i ->
                    val active = i == value.length
                    Box(
                        Modifier
                            .size(width = 52.dp, height = 56.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(SmartColors.Field)
                            .border(
                                BorderStroke(1.dp, if (active) SmartColors.Blue else SmartColors.FieldBorder),
                                RoundedCornerShape(10.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(value.getOrNull(i)?.toString() ?: "", fontSize = 20.sp, fontWeight = FontWeight.SemiBold, color = SmartColors.Ink)
                    }
                }
            }
        }
    )
}