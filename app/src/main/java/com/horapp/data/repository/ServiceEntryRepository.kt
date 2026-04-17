package com.horapp.data.repository

import com.horapp.data.local.dao.ServiceEntryDao
import com.horapp.data.local.entities.ServiceEntryEntity
import com.horapp.domain.model.ServiceEntry
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDate
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ServiceEntryRepository @Inject constructor(
    private val dao: ServiceEntryDao
) {
    fun getAllEntries(): Flow<List<ServiceEntry>> =
        dao.getAllEntries().map { list -> list.map { it.toDomain() } }

    fun getRecentEntries(limit: Int = 5): Flow<List<ServiceEntry>> =
        dao.getRecentEntries(limit).map { list -> list.map { it.toDomain() } }

    fun getTotalHours(): Flow<Float> = dao.getTotalHours()

    fun searchEntries(query: String): Flow<List<ServiceEntry>> =
        dao.searchEntries(query).map { list -> list.map { it.toDomain() } }

    fun getEntriesByCategory(category: String): Flow<List<ServiceEntry>> =
        dao.getEntriesByCategory(category).map { list -> list.map { it.toDomain() } }

    suspend fun addEntry(entry: ServiceEntry): Long =
        dao.insertEntry(entry.toEntity())

    suspend fun deleteEntry(entry: ServiceEntry) =
        dao.deleteById(entry.id)
}

// ── Mappers ──────────────────────────────────────────────────────────────────

private fun ServiceEntryEntity.toDomain() = ServiceEntry(
    id = id,
    date = LocalDate.ofEpochDay(dateEpochDay),
    organization = organization,
    hours = hours,
    category = category,
    narrative = narrative,
    isVerified = isVerified
)

private fun ServiceEntry.toEntity() = ServiceEntryEntity(
    id = id,
    dateEpochDay = date.toEpochDay(),
    organization = organization,
    hours = hours,
    category = category,
    narrative = narrative,
    isVerified = isVerified
)
