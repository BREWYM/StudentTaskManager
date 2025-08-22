package com.example.studenttaskmanager.presentation.profile

import android.util.Log
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
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class ProfileViewModel(
    private val userRepository: UserRepository,
    private val firestore: FirebaseFirestore,
) : ViewModel() {

    var name by mutableStateOf("")
        private set
    var adminId by mutableStateOf<String?>(null)
        private set

    var groupName by mutableStateOf("")
    var inviteCode by mutableStateOf("")

    var currentGroupName by mutableStateOf<String?>(null)
        private set

    var message by mutableStateOf<String?>(null)
        private set
    var members by mutableStateOf<List<User>>(emptyList()); private set
    var currentUser by mutableStateOf<User?>(null); private set

    fun onGroupNameChange(newName: String) {
        groupName = newName
    }

    fun onInviteCodeChange(newCode: String) {
        inviteCode = newCode
    }

    fun loadUser() = viewModelScope.launch {
        val user = userRepository.getCurrentUser()
        currentUser = user
        name = user?.name ?: ""
        user?.groupId?.let { gid ->
            try {
                val snapshot = Firebase.firestore
                    .collection("groups")   // коллекция
                    .document(gid)          // конкретный документ
                    .get()
                    .await()
                val group = snapshot.toObject(Group::class.java)
                currentGroupName = group?.name
                adminId = group?.adminId
                inviteCode = group?.inviteCode.orEmpty()
                loadMembers(gid)
            } catch (e: Exception) {
                println(e.localizedMessage)
                // обработать ошибку чтения
                message = "Не удалось загрузить данные"
            }
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
    }

    fun leaveGroup() = viewModelScope.launch {
        userRepository.leaveGroup()
        currentGroupName = null
        members = emptyList()
        message = "Вы вышли из группы"
        inviteCode = ""
    }
    fun logout() {
        Firebase.auth.signOut() // или через AuthRepository, если он внедрён
    }


    fun removeMember(user: User) = viewModelScope.launch {
        currentUser?.groupId?.let { gid ->
            userRepository.removeUserFromGroup(user.id, gid)
            loadMembers(gid)  // обновляем участников
        }
    }

    private fun loadMembers(groupId: String) = viewModelScope.launch {
        try {
            members = userRepository.getGroupMembers(groupId)
            Log.d("DEBUG", "Loading members for $groupId")
        } catch (e: Exception) {
            message = "Не удалось получить участников: ${e.message}"
            Log.d("DEBUG", "АНЛАК")
        }
    }
}
