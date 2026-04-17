package com.horapp.domain.model

data class StudentProfile(
    val id: Int = 1,
    val fullName: String,
    val studentId: String,
    val email: String,
    val major: String,
    val serviceLocation: String,
    val institution: String,
    val totalHoursGoal: Float = 480f
)
