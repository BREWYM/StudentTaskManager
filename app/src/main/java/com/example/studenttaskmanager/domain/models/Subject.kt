package com.example.studenttaskmanager.domain.models

data class Subject(
    val name: String,
    val professor: String,
    val examType: ExamType, // Зачёт, дифф. зачёт, экзамен
    val controlPoints: List<ControlPoint> = emptyList() // Контрольные точки
) {
    enum class ExamType { CREDIT, DIFF_CREDIT, EXAM }
    data class ControlPoint(val name: String, val date: Long)
}