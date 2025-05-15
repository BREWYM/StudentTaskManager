package com.example.studenttaskmanager.domain.repositories

import com.example.studenttaskmanager.common.Resource
import com.google.firebase.auth.FirebaseUser

interface AuthRepository {
    suspend fun login(email: String, password: String): Resource<FirebaseUser>
    suspend fun register(email: String, password: String, name: String): Resource<FirebaseUser>
    suspend fun signInWithGoogle(idToken: String): Resource<FirebaseUser>
    fun getCurrentUser(): FirebaseUser?
}