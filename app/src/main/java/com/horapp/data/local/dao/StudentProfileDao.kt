package com.horapp.data.local.dao

import androidx.room.*
import com.horapp.data.local.entities.StudentProfileEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface StudentProfileDao {

    @Query("SELECT * FROM student_profile WHERE id = 1 LIMIT 1")
    fun getProfile(): Flow<StudentProfileEntity?>

    @Query("SELECT * FROM student_profile WHERE id = 1 LIMIT 1")
    fun getProfileSync(): StudentProfileEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveProfile(profile: StudentProfileEntity)
}
