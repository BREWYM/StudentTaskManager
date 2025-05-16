package com.example.studenttaskmanager.data.remote

import com.example.studenttaskmanager.domain.models.Subject
import com.example.studenttaskmanager.domain.models.SubjectDto
import com.example.studenttaskmanager.domain.repositories.SubjectRepository
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class SubjectRepositoryImpl(
    private val firestore: FirebaseFirestore
): SubjectRepository {
    override suspend fun getSubjects(groupId: String): List<Subject> {
        return try {
            val snapshot = firestore.collection("groups")
                .document(groupId)
                .collection("subjects")
                .get()
                .await()

            snapshot.documents.mapNotNull { it.toObject(SubjectDto::class.java)?.toSubject() }
        } catch (e: Exception) {
            emptyList()
        }
    }

    override suspend fun addSubject(subject: Subject) {
        val dto = SubjectDto.fromSubject(subject)
        firestore.collection("groups")
            .document(subject.groupId)
            .collection("subjects")
            .add(dto)
            .await()
    }

    override suspend fun addControlPoint(subjectName: String, point: Subject.ControlPoint) {
        // Найдём предмет по имени (можно сделать лучше через id)
        val groupRef = firestore.collection("groups")
        val subjectsQuery = groupRef.get().await()

        for (group in subjectsQuery) {
            val subjectRef = group.reference.collection("subjects")
                .whereEqualTo("name", subjectName)
                .get()
                .await()

            subjectRef.documents.forEach { doc ->
                val dto = doc.toObject(SubjectDto::class.java)
                if (dto != null) {
                    val updatedPoints = dto.controlPoints + mapOf("name" to point.name, "date" to point.date)
                    doc.reference.update("controlPoints", updatedPoints).await()
                }
            }
        }
    }
}