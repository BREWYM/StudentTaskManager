package com.example.studenttaskmanager.domain.models

data class Group(
    val id: String = "",
    val name: String = "",
    val adminId: String = "",
    val inviteCode: String = "",
    val subjectPool: List<Subject> = emptyList(),
    val professorPool: List<String> = emptyList()
)
