package com.example.studenttaskmanager.domain.models

data class Subject(
    val name: String,
    val professor: String,
    val groupId: String,
    val examType: ExamType = ExamType.CREDIT, // Зачёт, дифф. зачёт, экзамен
    val controlPoints: List<ControlPoint> = emptyList() // Контрольные точки
) {
    enum class ExamType { CREDIT, DIFF_CREDIT, EXAM }
    data class ControlPoint(val name: String, val date: Long)

    override fun toString(): String = name
}


data class SubjectDto(
    val name: String = "",
    val professor: String = "",
    val examType: String = "CREDIT", // enum как строка
    val groupId: String,
    val controlPoints: List<Map<String, Any>> = emptyList()
) {
    fun toSubject(): Subject {
        return Subject(
            name = name,
            professor = professor,
            examType = Subject.ExamType.valueOf(examType),
            groupId = groupId,
            controlPoints = controlPoints.map {
                Subject.ControlPoint(
                    name = it["name"] as String,
                    date = (it["date"] as Number).toLong()
                )
            }
        )
    }

    companion object {
        fun fromSubject(subject: Subject): SubjectDto {
            return SubjectDto(
                name = subject.name,
                professor = subject.professor,
                examType = subject.examType.name,
                groupId = subject.groupId,
                controlPoints = subject.controlPoints.map {
                    mapOf(
                        "name" to it.name,
                        "date" to it.date
                    )
                }
            )
        }
    }
}