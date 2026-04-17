package com.horapp.data.local

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.horapp.data.local.entities.StudentProfileEntity
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

@RunWith(AndroidJUnit4::class)
class AppDatabaseTest {
    private lateinit var db: AppDatabase
    private lateinit var profileDao: com.horapp.data.local.dao.StudentProfileDao

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java).build()
        profileDao = db.studentProfileDao()
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    @Test
    @Throws(Exception::class)
    fun writeProfileAndReadInList() = runBlocking {
        val profile = StudentProfileEntity(
            id = 1,
            fullName = "Test User",
            studentId = "12345",
            email = "test@example.com",
            major = "CS",
            serviceLocation = "Lab",
            institution = "Uni",
            totalHoursGoal = 480f
        )
        profileDao.saveProfile(profile)
        val result = profileDao.getProfileSync()
        assertEquals("Test User", result?.fullName)
        assertEquals("CS", result?.major)
    }
}
