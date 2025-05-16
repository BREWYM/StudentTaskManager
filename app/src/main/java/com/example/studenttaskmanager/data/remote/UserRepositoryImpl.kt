package com.example.studenttaskmanager.data.remote

import android.util.Log
import com.example.studenttaskmanager.domain.models.Group
import com.example.studenttaskmanager.domain.models.User
import com.example.studenttaskmanager.domain.repositories.UserRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class UserRepositoryImpl(
    private val auth: FirebaseAuth,
    firestore: FirebaseFirestore
) : UserRepository {

    // Коллекция пользователей в Firestore
    private val usersCollection = firestore.collection("users")

    // Коллекция групп
    private val groupsCollection = firestore.collection("groups")

    // Получение текущего пользователя из Firestore, если он авторизован.
    override suspend fun getCurrentUser(): User? {
        val uid = auth.currentUser?.uid ?: return null
        val snapshot = usersCollection.document(uid).get().await()
        return snapshot.toObject(User::class.java)?.copy(id = uid)
    }

    // Создание группы.
    // Метод генерирует новый ID для группы, создает документ группы с adminId, а затем обновляет пользователя,
    // присваивая ему созданный groupId.
    override suspend fun createGroup(name: String): String {
        // Получаем текущего пользователя. Если пользователь не авторизован, бросаем исключение.
        val currentUser = getCurrentUser() ?: throw Exception("Пользователь не авторизован")

        // Генерируем новый ID для группы
        val groupId = groupsCollection.document().id
        val allowedChars = ('A'.. 'Z') + ('a'..'z')+('0'..'9')
        val inviteCode = String(CharArray(10) { allowedChars.random() })

        // Создаем объект группы
        val group = Group(
            id = groupId,
            name = name,
            adminId = currentUser.id,
            subjectPool = emptyList(),
            professorPool = emptyList(),
            inviteCode = inviteCode
        )

        // Сохраняем группу в коллекции groups
        groupsCollection.document(groupId).set(group).await()

        // Обновляем документ пользователя, присваивая ему groupId
        usersCollection.document(currentUser.id).update("groupId", groupId).await()

        return groupId
    }

    // Присоединение к группе по invite-коду.
    override suspend fun joinGroup(inviteCode: String): Boolean = withContext(Dispatchers.IO) {
        try {
            val user = getCurrentUser() ?: return@withContext false

            // Находим группу по inviteCode
            val querySnapshot = groupsCollection
                .whereEqualTo("inviteCode", inviteCode)
                .get()
                .await()

            val groupDoc = querySnapshot.documents.firstOrNull() ?: return@withContext false
            val groupId = groupDoc.id

            // Обновляем пользователя, присваивая ему groupId
            usersCollection.document(user.id)
                .update("groupId", groupId)
                .await()

            true
        } catch (e: Exception) {
            Log.e("UserRepository", "Ошибка при входе в группу: ${e.message}")
            false
        }
    }

    // Выход из группы — обновляем поле groupId у пользователя на null.
    override suspend fun leaveGroup() {
        val currentUser = getCurrentUser() ?: return
        usersCollection.document(currentUser.id).update("groupId", null).await()
    }
}
