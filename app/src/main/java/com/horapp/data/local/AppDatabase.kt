package com.horapp.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.horapp.data.local.dao.ServiceEntryDao
import com.horapp.data.local.dao.StudentProfileDao
import com.horapp.data.local.entities.ServiceEntryEntity
import com.horapp.data.local.entities.StudentProfileEntity

@Database(
    entities = [StudentProfileEntity::class, ServiceEntryEntity::class],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun studentProfileDao(): StudentProfileDao
    abstract fun serviceEntryDao(): ServiceEntryDao
}
