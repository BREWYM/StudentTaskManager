package com.example.studenttaskmanager.data.remote

import com.example.studenttaskmanager.domain.models.Subject
import com.example.studenttaskmanager.domain.models.SubjectDto
import com.example.studenttaskmanager.domain.repositories.SubjectRepository
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class SubjectRepositoryImpl(
    private val firestore: FirebaseFirestore
) : SubjectRepository {

    override suspend fun getSubjects(groupId: String): List<Subject> {
        return try {
            val snapshot = firestore.collection("groups")
                .document(groupId)
                .collection("subjects")
                .get()
                .await()

            snapshot.documents.mapNotNull { doc ->
                val dto = doc.toObject(SubjectDto::class.java)
                dto?.copy(subjectId = doc.id)?.toSubject()
            }

        } catch (e: Exception) {
            println(e.localizedMessage)
            emptyList()
        }
    }

    override suspend fun addSubject(subject: Subject) {
        val dto = SubjectDto.fromSubject(subject)
        val newDocRef = firestore.collection("groups")
            .document(subject.groupId)
            .collection("subjects")
            .document() // автоматическая генерация ID

        val dtoWithId = dto.copy(subjectId = newDocRef.id)
        newDocRef.set(dtoWithId).await()
    }

    override suspend fun addControlPoint(subjectId: String, groupId: String, point: Subject.ControlPoint) {
        try {
            val docRef = firestore.collection("groups")
                .document(groupId)
                .collection("subjects")
                .document(subjectId)

            val snapshot = docRef.get().await()
            val dto = snapshot.toObject(SubjectDto::class.java)

            if (dto != null) {
                val updatedPoints = dto.controlPoints + mapOf("name" to point.name, "date" to point.date)
                docRef.update("controlPoints", updatedPoints).await()
            }

        } catch (e: Exception) {
            println(e.localizedMessage)
            // обработка ошибки
        }
    }
}

