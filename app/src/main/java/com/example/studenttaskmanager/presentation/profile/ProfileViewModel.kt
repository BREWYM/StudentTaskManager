package com.example.studenttaskmanager.presentation.profile

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.studenttaskmanager.domain.models.Group
import com.example.studenttaskmanager.domain.models.User
import com.example.studenttaskmanager.domain.repositories.UserRepository
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class ProfileViewModel(
    private val userRepository: UserRepository,
    private val firestore: FirebaseFirestore,
) : ViewModel() {

    var name by mutableStateOf("")
        private set

    var groupName by mutableStateOf("")
    var inviteCode by mutableStateOf("")

    var currentGroupName by mutableStateOf<String?>(null)
        private set

    var message by mutableStateOf<String?>(null)
        private set

    fun onGroupNameChange(newName: String) {
        groupName = newName
    }

    fun onInviteCodeChange(newCode: String) {
        inviteCode = newCode
    }

    suspend fun loadUser() {
        val uid = Firebase.auth.currentUser?.uid ?: return
        val userDoc = firestore.collection("users").document(uid).get().await()
        val user = userDoc.toObject(User::class.java)

        name = user?.name ?: ""

        user?.groupId?.let { groupId ->
            val groupDoc = firestore.collection("groups").document(groupId).get().await()
            val group = groupDoc.toObject(Group::class.java)

            currentGroupName = group?.name
            inviteCode = group?.inviteCode ?: "" // 👈 вот это нужно!
        }
    }


    fun createGroup() = viewModelScope.launch {
        try {
            val id = userRepository.createGroup(groupName)
            message = "Группа создана: $id"
            groupName = ""
            loadUser()
        } catch (e: Exception) {
            message = "Ошибка: ${e.message}"
        }
    }

    fun joinGroup() = viewModelScope.launch {
        val success = userRepository.joinGroup(inviteCode)
        if (success) {
            loadUser()
            message = "Успешно присоединились!"
        } else {
            message = "Группа не найдена"
        }
        inviteCode = ""
    }

    fun leaveGroup() = viewModelScope.launch {
        userRepository.leaveGroup()
        currentGroupName = null
        message = "Вы вышли из группы"
    }
    fun logout() {
        Firebase.auth.signOut() // или через AuthRepository, если он внедрён
    }
}
