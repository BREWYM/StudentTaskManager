package com.example.studenttaskmanager.data.remote

import android.util.Log
import com.example.studenttaskmanager.common.Resource
import com.example.studenttaskmanager.domain.models.Comment
import com.example.studenttaskmanager.domain.models.Task
import com.example.studenttaskmanager.domain.models.TaskDto
import com.example.studenttaskmanager.domain.repositories.SubjectRepository
import com.example.studenttaskmanager.domain.repositories.TaskRepository
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.util.UUID

class TaskRepositoryImpl(
    private val firestore: FirebaseFirestore,
    private val subjectRepository: SubjectRepository,
//    private val storage: FirebaseStorage,
): TaskRepository
{
    private val tasksCollection = firestore.collection("tasks")

    override suspend fun getTasks(userId: String): Resource<List<Task>> = withContext(Dispatchers.IO) {
        return@withContext try {
            val snapshot = tasksCollection
                .whereEqualTo("creatorId", userId)
                .get()
                .await()

            val tasks = snapshot.documents.mapNotNull { doc ->
                val dto = doc.toObject(TaskDto::class.java)
                dto?.toPersonalTask(dto.subjectName)
            }
            Log.d("LOAD", "${tasks.size} Загружен")
            checkSubjectsForGroup("")

            Resource.Success(tasks)
        } catch (e: Exception) {
            Resource.Error("Не удалось загрузить задачи: ${e.message}")
        }
    }

    override suspend fun getGroupTasks(groupId: String): Resource<List<Task>> = withContext(Dispatchers.IO) {
        return@withContext try {
            val snapshot = tasksCollection
                .whereEqualTo("isGroupTask", true)
                .get()
                .await()

            val subjects = subjectRepository.getSubjects(groupId)

            val tasks = snapshot.documents.mapNotNull { doc ->
                val dto = doc.toObject(TaskDto::class.java)
                val subject = subjects.find { it.name == dto?.subjectName }
                dto?.toTask(subject)
            }

            Resource.Success(tasks)
        } catch (e: Exception) {
            Resource.Error("Не удалось загрузить групповые задачи: ${e.message}")
        }
    }
    suspend fun checkSubjectsForGroup(groupId: String) {
        val subjects = subjectRepository.getSubjects(groupId)
        if (subjects.isEmpty()) {
            Log.d("CheckSubjects", "Для groupId = $groupId предметы не найдены")
        } else {
            Log.d("CheckSubjects", "Для groupId = $groupId найдены предметы:")
            subjects.forEach { subject ->
                Log.d("CheckSubjects", "- ${subject.name}")
            }
        }
    }

    override suspend fun addTask(task: Task): Unit = withContext(Dispatchers.IO) {
        val docRef = if (task.id.isEmpty()) tasksCollection.document() else tasksCollection.document(task.id)
        val taskWithId = task.copy(id = docRef.id)
        val dto = TaskDto.fromTask(taskWithId)
        docRef.set(dto).await()
//        scheduleDeadlineNotification(context = context, task)
    }

    override suspend fun getTaskById(taskId: String): Task {
        val snapshot = tasksCollection.document(taskId).get().await()
        return snapshot.toObject(Task::class.java)!!.copy(id = snapshot.id)
    }

    override suspend fun deleteTask(taskId: String): Unit = withContext(Dispatchers.IO) {
        tasksCollection.document(taskId).delete().await()
    }

    override suspend fun updateTask(task: Task): Unit = withContext(Dispatchers.IO) {
        require(task.id.isNotEmpty()) { "Task ID must not be empty when updating." }
        val dto = TaskDto.fromTask(task)
        tasksCollection.document(dto.id).set(dto).await()
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
//    @RequiresPermission(Manifest.permission.SCHEDULE_EXACT_ALARM)
//    fun scheduleDeadlineNotification(context: Context, task: Task) {
//        val intent = Intent(context, DeadlineReceiver::class.java).apply {
//            putExtra("title", task.title)
//            putExtra("deadline", SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()).format(Date(task.deadline)))
//        }
//
//        val pendingIntent = PendingIntent.getBroadcast(
//            context,
//            task.id.hashCode(),
//            intent,
//            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
//        )
//
//        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
//
//        val notifyTime = task.deadline - TimeUnit.HOURS.toMillis(3) // за 3 часа до дедлайна
//
//        if (notifyTime > System.currentTimeMillis()) {
//            alarmManager.setExactAndAllowWhileIdle(
//                AlarmManager.RTC_WAKEUP,
//                notifyTime,
//                pendingIntent
//            )
//        }
//    }


//    override suspend fun uploadFile(file: Uri): String {
//        val fileName = UUID.randomUUID().toString() // Уникальное имя файла
//        val storageRef = storage.reference.child("files/$fileName")
//        storageRef.putFile(file).await()
//        return storageRef.downloadUrl.await().toString()
//    }
}