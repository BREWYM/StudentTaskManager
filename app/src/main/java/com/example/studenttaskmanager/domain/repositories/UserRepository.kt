package com.example.studenttaskmanager.domain.repositories

import com.example.studenttaskmanager.domain.models.User

interface UserRepository {
      // Группы
    suspend fun getCurrentUser(): User?
    suspend fun createGroup(name: String): String // Возвращает ID группы
    suspend fun joinGroup(inviteCode: String): Boolean
    suspend fun leaveGroup()
    suspend fun getGroupMembers(groupId: String): List<User>
    suspend fun removeUserFromGroup(userId: String, groupId: String)
}