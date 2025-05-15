package com.example.studenttaskmanager.presentation.login

import com.google.firebase.auth.FirebaseUser

data class AuthState(
    val email: String = "",
    val password: String = "",
    val name: String = "", // Только для регистрации
    val isLoading: Boolean = false,
    val error: String? = null,
    val isSuccess: Boolean = false,
    val user: FirebaseUser? = null,
)