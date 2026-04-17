package com.horapp.di

import android.content.Context
import androidx.room.Room
import com.horapp.data.local.AppDatabase
import com.horapp.data.local.dao.ServiceEntryDao
import com.horapp.data.local.dao.StudentProfileDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase =
        Room.databaseBuilder(context, AppDatabase::class.java, "horapp.db")
            .fallbackToDestructiveMigration()
            .build()

    @Provides
    fun provideServiceEntryDao(db: AppDatabase): ServiceEntryDao = db.serviceEntryDao()

    @Provides
    fun provideStudentProfileDao(db: AppDatabase): StudentProfileDao = db.studentProfileDao()
}
