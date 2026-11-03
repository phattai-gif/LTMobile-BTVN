package com.example.myapplication.model

/** Các trạng thái của quá trình đăng nhập (sealed class). */
sealed class AuthUiState {
    object Idle : AuthUiState()
    object Loading : AuthUiState()
    object Success : AuthUiState()
    data class Error(val message: String) : AuthUiState()
}