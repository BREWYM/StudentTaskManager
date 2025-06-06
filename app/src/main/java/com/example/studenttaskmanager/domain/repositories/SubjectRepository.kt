package com.example.studenttaskmanager.domain.repositories

import com.example.studenttaskmanager.domain.models.Subject
import com.example.studenttaskmanager.domain.models.Subject.ControlPoint

interface SubjectRepository {
    suspend fun getSubjects(groupId: String): List<Subject>
    suspend fun addSubject(subject: Subject)
    suspend fun addControlPoint(subjectId: String, groupId: String, point: ControlPoint)
}