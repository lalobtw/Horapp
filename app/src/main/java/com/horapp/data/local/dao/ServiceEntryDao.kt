package com.horapp.data.local.dao

import androidx.room.*
import com.horapp.data.local.entities.ServiceEntryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ServiceEntryDao {

    @Query("SELECT * FROM service_entries ORDER BY dateEpochDay DESC")
    fun getAllEntries(): Flow<List<ServiceEntryEntity>>

    @Query("SELECT * FROM service_entries ORDER BY dateEpochDay DESC")
    fun getAllEntriesSync(): List<ServiceEntryEntity>

    @Query("SELECT * FROM service_entries WHERE category = :category ORDER BY dateEpochDay DESC")
    fun getEntriesByCategory(category: String): Flow<List<ServiceEntryEntity>>

    @Query("SELECT * FROM service_entries ORDER BY dateEpochDay DESC LIMIT :limit")
    fun getRecentEntries(limit: Int = 5): Flow<List<ServiceEntryEntity>>

    @Query("SELECT COALESCE(SUM(hours), 0) FROM service_entries")
    fun getTotalHours(): Flow<Float>

    @Query("""
        SELECT * FROM service_entries 
        WHERE organization LIKE '%' || :query || '%' 
           OR narrative LIKE '%' || :query || '%'
           OR category LIKE '%' || :query || '%'
        ORDER BY dateEpochDay DESC
    """)
    fun searchEntries(query: String): Flow<List<ServiceEntryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEntry(entry: ServiceEntryEntity): Long

    @Delete
    suspend fun deleteEntry(entry: ServiceEntryEntity)

    @Query("DELETE FROM service_entries WHERE id = :id")
    suspend fun deleteById(id: Long)
}
