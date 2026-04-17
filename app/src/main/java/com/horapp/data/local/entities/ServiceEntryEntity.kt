package com.horapp.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "service_entries")
data class ServiceEntryEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val dateEpochDay: Long,         // LocalDate.toEpochDay()
    val organization: String,
    val hours: Float,
    val category: String,
    val narrative: String,
    val isVerified: Boolean = false
)
