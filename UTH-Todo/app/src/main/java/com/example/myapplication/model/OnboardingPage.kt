package com.example.myapplication.model

import androidx.compose.runtime.Composable
import com.example.myapplication.ui.component.BellIllustration
import com.example.myapplication.ui.component.CategoryIllustration
import com.example.myapplication.ui.component.ChecklistIllustration

/**
 * OOP: lớp trừu tượng (sealed) đại diện 1 trang onboarding.
 * Mỗi trang con kế thừa và tự override phần minh hoạ (đa hình).
 */
sealed class OnboardingPage(
    val title: String,
    val description: String,
    val buttonText: String
) {
    @Composable
    abstract fun Illustration()

    object Checklist : OnboardingPage(
        title = "Mọi việc, gọn trong một chỗ",
        description = "Ghi lại bài tập, việc nhà, lịch hẹn. Không còn quên việc nào.",
        buttonText = "Tiếp tục"
    ) {
        @Composable
        override fun Illustration() = ChecklistIllustration()
    }

    object Category : OnboardingPage(
        title = "Chia theo Học tập và Cuộc sống",
        description = "Mỗi việc có nhóm riêng, nhìn là biết hôm nay cần lo gì.",
        buttonText = "Tiếp tục"
    ) {
        @Composable
        override fun Illustration() = CategoryIllustration()
    }

    object Reminder : OnboardingPage(
        title = "Nhắc đúng lúc",
        description = "Bật thông báo để UTH Todo nhắc bạn trước hạn chót.",
        buttonText = "Bắt đầu ngay"
    ) {
        @Composable
        override fun Illustration() = BellIllustration()
    }

    companion object {
        val all: List<OnboardingPage> = listOf(Checklist, Category, Reminder)
    }
}