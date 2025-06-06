package com.example.studenttaskmanager.domain.models

data class Subject(
    val subjectId: String = "", // Новый ID
    val name: String = "",
    val professor: String = "",
    val groupId: String = "",
    val examType: ExamType = ExamType.CREDIT,
    val controlPoints: List<ControlPoint> = emptyList()
) {
    enum class ExamType { CREDIT, DIFF_CREDIT, EXAM }
    data class ControlPoint(val name: String, val date: Long)

    override fun toString(): String = name
}

data class SubjectDto(
    val subjectId: String = "",
    val name: String = "",
    val professor: String = "",
    val examType: String = "CREDIT",
    val groupId: String = "",
    val controlPoints: List<Map<String, Any>> = emptyList()
) {
    fun toSubject(): Subject {
        return Subject(
            subjectId = subjectId,
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
                subjectId = subject.subjectId,
                name = subject.name,
                professor = subject.professor,
                examType = subject.examType.name,
                groupId = subject.groupId,
                controlPoints = subject.controlPoints.map {
                    mapOf("name" to it.name, "date" to it.date)
                }
            )
        }
    }
}
