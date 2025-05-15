package com.example.studenttaskmanager.presentation.login

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.studenttaskmanager.common.Resource
import com.example.studenttaskmanager.domain.use_cases.SignInUseCase
import com.example.studenttaskmanager.domain.use_cases.SignUpUseCase
import kotlinx.coroutines.launch

class AuthViewModel(
    private val signInUseCase: SignInUseCase,
    private val signUpUseCase: SignUpUseCase,
//    private val googleSignInUseCase: GoogleSignInUseCase
) : ViewModel() {

    private val _state = mutableStateOf(AuthState())
    val state: State<AuthState> = _state

    fun resetSuccessFlag(){
        _state.value = _state.value.copy(isSuccess = false)
    }
    fun updateEmail(email: String) {
        _state.value = _state.value.copy(email = email)
    }

    fun updatePassword(password: String) {
        _state.value = _state.value.copy(password = password)
    }

    fun updateName(name: String) {
        _state.value = _state.value.copy(name = name)
    }

    fun login() = viewModelScope.launch {
        val email = state.value.email.trim()
        val pass  = state.value.password

        // Валидация полей
        if (email.isBlank() || pass.isBlank()) {
            _state.value = AuthState(error = "Email и пароль не должны быть пустыми")
            return@launch
        }

        _state.value = AuthState(isLoading = true)
        when(val res = signInUseCase(email, pass)) {
            is Resource.Success -> _state.value = AuthState(user = res.data, isSuccess = true)
            is Resource.Error   -> _state.value = AuthState(error = res.message)
            else -> Unit
        }
    }

    fun register() = viewModelScope.launch {
        val email = state.value.email.trim()
        val pass  = state.value.password
        val name  = state.value.name.trim()

        // Валидация полей
        when {
            email.isBlank() || pass.isBlank() -> {
                _state.value = AuthState(error = "Email и пароль не должны быть пустыми")
                return@launch
            }
            name.isBlank() -> {
                _state.value = AuthState(error = "Имя не должно быть пустым")
                return@launch
            }
        }
        _state.value = AuthState(isLoading = true)
        when(val res = signUpUseCase(
            email,
            pass,
            name
            )) {
            is Resource.Success -> _state.value = AuthState(user = res.data, isSuccess = true)
            is Resource.Error   -> _state.value = AuthState(error = res.message)
            else -> Unit
        }
    }

//    fun handleGoogleSignIn(idToken: String) = viewModelScope.launch {
//        _state.value = AuthState(isLoading = true)
//        when(val res = GoogleSignInUseCase(idToken)) {
//            is Resource.Success -> _state.value = AuthState(user = res.data)
//            is Resource.Error   -> _state.value = AuthState(error = res.message)
//            else -> Unit
//        }
//    }
}