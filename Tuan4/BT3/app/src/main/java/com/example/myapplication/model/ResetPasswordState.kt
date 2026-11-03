package com.example.myapplication.model

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

/** Kết quả cuối cùng, trả về màn hình 1 sau khi Submit. */
data class ResetResult(val email: String, val otp: String, val password: String)

/**
 * OOP: 1 object giữ dữ liệu dùng chung cho cả 4 màn hình.
 * Màn 1 nhập email -> màn 2 nhập otp -> màn 3 nhập pass -> màn 4 xác nhận.
 */
class ResetPasswordState {
    var email by mutableStateOf("")
    var otp by mutableStateOf("")
    var password by mutableStateOf("")

    /** Khác null khi đã Submit xong -> màn 1 hiển thị khung kết quả. */
    var result by mutableStateOf<ResetResult?>(null)
        private set

    fun submit() {
        result = ResetResult(email, otp, password)
        email = ""; otp = ""; password = ""
    }
}