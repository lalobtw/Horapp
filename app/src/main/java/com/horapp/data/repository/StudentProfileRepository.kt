package com.horapp.data.repository

import com.horapp.data.local.dao.StudentProfileDao
import com.horapp.data.local.entities.StudentProfileEntity
import com.horapp.domain.model.StudentProfile
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StudentProfileRepository @Inject constructor(
    private val dao: StudentProfileDao
) {
    fun getProfile(): Flow<StudentProfile?> =
        dao.getProfile().map { it?.toDomain() }

    suspend fun saveProfile(profile: StudentProfile) =
        dao.saveProfile(profile.toEntity())
}

// ── Mappers ──────────────────────────────────────────────────────────────────

private fun StudentProfileEntity.toDomain() = StudentProfile(
    id = id,
    fullName = fullName,
    studentId = studentId,
    email = email,
    major = major,
    serviceLocation = serviceLocation,
    institution = institution,
    totalHoursGoal = totalHoursGoal
)

private fun StudentProfile.toEntity() = StudentProfileEntity(
    id = id,
    fullName = fullName,
    studentId = studentId,
    email = email,
    major = major,
    serviceLocation = serviceLocation,
    institution = institution,
    totalHoursGoal = totalHoursGoal
)
