package com.example.studenttaskmanager.domain.models

data class User(
    val id: String = "", //Firebase UID
    val email: String,
    val name: String,
    val image: String,
    val groupId: String? = null //по умолчанию группы нет
)
