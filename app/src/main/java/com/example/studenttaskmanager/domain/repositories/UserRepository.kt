package com.example.studenttaskmanager.domain.repositories

import com.example.studenttaskmanager.domain.models.User

interface UserRepository {
    // Авторизация
    suspend fun signIn(email: String, password: String): User?
    suspend fun signUp(email: String, password: String, name: String): User?
    suspend fun signOut()

    // Группы
    suspend fun getCurrentUser(): User?
    suspend fun createGroup(name: String): String // Возвращает ID группы
    suspend fun joinGroup(inviteCode: String): Boolean
    suspend fun leaveGroup()
}