package com.example.myapplication.viewmodel

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialCancellationException
import androidx.credentials.exceptions.GetCredentialException
import androidx.credentials.exceptions.NoCredentialException
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.R
import com.example.myapplication.data.AuthRepository
import com.example.myapplication.model.AuthUiState
import com.example.myapplication.model.UserProfile
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.launch

class AuthViewModel : ViewModel() {

    private val repository = AuthRepository()

    var uiState by mutableStateOf<AuthUiState>(AuthUiState.Idle)
        private set

    var profile by mutableStateOf(repository.currentUser?.let { UserProfile.from(it) })
        private set

    val isLoggedIn: Boolean get() = repository.currentUser != null

    fun signInWithGoogle(context: Context) {
        if (uiState is AuthUiState.Loading) return
        uiState = AuthUiState.Loading

        viewModelScope.launch {
            try {
                // default_web_client_id do plugin google-services tự sinh ra

                val webClientId = "125488955004-d1frg9sv2vl8tlrhjo08hq4j31egji12.apps.googleusercontent.com";    val option = GetGoogleIdOption.Builder()
                    .setFilterByAuthorizedAccounts(false)
                    .setServerClientId(webClientId)
                    .build()
                val request = GetCredentialRequest.Builder().addCredentialOption(option).build()

                val result = CredentialManager.create(context).getCredential(context, request)
                val credential = result.credential

                if (credential is CustomCredential &&
                    credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
                ) {
                    val idToken = GoogleIdTokenCredential.createFrom(credential.data).idToken
                    repository.signInWithGoogle(idToken, ::handleResult)
                } else {
                    uiState = AuthUiState.Error("Loại tài khoản này chưa được hỗ trợ")
                }
            } catch (e: GetCredentialCancellationException) {
                uiState = AuthUiState.Error("Bạn đã huỷ đăng nhập")
            } catch (e: NoCredentialException) {
                uiState = AuthUiState.Error("Không tìm thấy tài khoản Google trên thiết bị")
            } catch (e: GetCredentialException) {
                uiState = AuthUiState.Error("Đăng nhập Google thất bại: ${e.message ?: "lỗi không xác định"}")
            } catch (e: Exception) {
                uiState = AuthUiState.Error(e.toUserMessage())
            }
        }
    }

    fun signInAnonymously() {
        if (uiState is AuthUiState.Loading) return
        uiState = AuthUiState.Loading
        repository.signInAnonymously(::handleResult)
    }

    private fun handleResult(result: Result<FirebaseUser>) {
        result
            .onSuccess {
                profile = UserProfile.from(it)
                uiState = AuthUiState.Success
            }
            .onFailure { uiState = AuthUiState.Error(it.toUserMessage()) }
    }

    /** Gọi sau khi UI đã hiển thị xong thông báo. */
    fun consumeState() {
        uiState = AuthUiState.Idle
    }

    fun signOut() {
        repository.signOut()
        profile = null
        uiState = AuthUiState.Idle
    }

    private fun Throwable.toUserMessage(): String = when (this) {
        is FirebaseNetworkException -> "Không có kết nối mạng, vui lòng thử lại"
        else -> "Đăng nhập thất bại: ${localizedMessage ?: "lỗi không xác định"}"
    }
}