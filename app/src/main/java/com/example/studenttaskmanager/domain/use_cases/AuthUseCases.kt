package com.example.studenttaskmanager.domain.use_cases

import com.example.studenttaskmanager.domain.repositories.AuthRepository

class SignInUseCase(private val authRepo: AuthRepository) {
    suspend operator fun invoke(email: String, password: String) = authRepo.login(email, password)
}

class SignUpUseCase(private val authRepo: AuthRepository) {
    suspend operator fun invoke(email: String, password: String, name: String) =
        authRepo.register(email, password, name)

}

class GoogleSignInUseCase(private val authRepo: AuthRepository) {
    suspend operator fun invoke(idToken: String) = authRepo.signInWithGoogle(idToken)
}