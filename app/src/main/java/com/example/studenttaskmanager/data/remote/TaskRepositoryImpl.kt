package com.example.studenttaskmanager.data.remote

import android.util.Log
import com.example.studenttaskmanager.common.Resource
import com.example.studenttaskmanager.domain.models.Comment
import com.example.studenttaskmanager.domain.models.Task
import com.example.studenttaskmanager.domain.repositories.TaskRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import java.util.UUID

class TaskRepositoryImpl(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore,
//    private val storage: FirebaseStorage
): TaskRepository
{
    override suspend fun getTasks(userId: String): Resource<List<Task>> {
        return try {
            val snapshot = firestore.collection("tasks")
//                .whereEqualTo("creatorId", userId)
//                .whereEqualTo("isGroupTask", false)
                .get()
                .await()
                .toObjects(Task::class.java)
            Log.d("RepoDebug", "Документов: ${snapshot.lastIndex}")
            Resource.Success(snapshot)
        } catch (e: Exception) {
            Resource.Error(e.localizedMessage ?: "Не удалось загрузить задачи")
        }
    }
    override suspend fun getGroupTasks(groupId: String): Resource<List<Task>> {
        return try {
            val snapshot = firestore.collection("tasks")
//                .whereEqualTo("groupId", groupId)
//                .whereEqualTo("isGroupTask", true)
                .get()
                .await()
                .toObjects(Task::class.java)
            Resource.Success(snapshot)
        } catch (e: Exception) {
            Resource.Error(e.localizedMessage ?: "Не удалось загрузить групповые задачи")
        }
    }


    override suspend fun addTask(task: Task) {
//        val taskId = UUID.randomUUID().toString() // Генерируем уникальный ID
//        firestore.collection("tasks")
//            .document(taskId)
//            .set(task.copy(id = taskId))
//            .await()
//        return taskId
        firestore.collection("tasks").add(task)
            .addOnSuccessListener { docRef ->
                Log.d("TaskVM", "Задача добавлена, ID = ${docRef.id}")
            }
            .addOnFailureListener { e ->
                Log.e("TaskVM", "Ошибка добавления задачи", e)
            }

    }
    override suspend fun deleteTask(taskId: String) {
        firestore.collection("tasks")
            .document(taskId)
            .delete()
            .await()
    }

    override suspend fun updateTask(task: Task) {
        firestore.collection("tasks")
            .document(task.id)
            .set(task)
            .await()
    }

    override suspend fun getComments(taskId: String): List<Comment> {
        return firestore.collection("tasks")
            .document(taskId)
            .collection("comments")
            .get()
            .await()
            .toObjects(Comment::class.java)
    }

    override suspend fun addComment(comment: Comment) {
        val commentId = UUID.randomUUID().toString()
        firestore.collection("tasks")
            .document(comment.taskId)
            .collection("comments")
            .document(commentId)
            .set(comment.copy(id = commentId))
            .await()
    }


//    override suspend fun uploadFile(file: Uri): String {
//        val fileName = UUID.randomUUID().toString() // Уникальное имя файла
//        val storageRef = storage.reference.child("files/$fileName")
//        storageRef.putFile(file).await()
//        return storageRef.downloadUrl.await().toString()
//    }
}