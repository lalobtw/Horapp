package com.horapp.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "student_profile")
data class StudentProfileEntity(
    @PrimaryKey val id: Int = 1, // singleton — siempre 1
    val fullName: String,
    val studentId: String,
    val email: String,
    val major: String,
    val serviceLocation: String,
    val institution: String,
    val totalHoursGoal: Float = 480f
)
