package com.learn2hire.app.data.model

data class UserProfile(
    val uid: String = "",
    val name: String = "",
    val email: String = "",
    val photoUrl: String = "",
    val course: String = "",
    val semester: String = "",
    val authProvider: String = "",
    val createdAt: Long = 0L
)