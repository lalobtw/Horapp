package com.horapp.ui.screens.history

import app.cash.turbine.test
import com.horapp.data.repository.ServiceEntryRepository
import com.horapp.domain.model.ServiceEntry
import com.horapp.utils.MainDispatcherRule
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.time.LocalDate

@OptIn(ExperimentalCoroutinesApi::class)
class HistoryViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var repository: ServiceEntryRepository
    private lateinit var viewModel: HistoryViewModel

    @Before
    fun setUp() {
        repository = mockk()
        
        // Mock data
        val entries = listOf(
            ServiceEntry(id = 1, date = LocalDate.of(2023, 10, 15), organization = "Org A", hours = 5.0f, category = "Comunitario", narrative = "Detail A"),
            ServiceEntry(id = 2, date = LocalDate.of(2023, 10, 20), organization = "Org B", hours = 3.0f, category = "Tutoría", narrative = "Detail B"),
            ServiceEntry(id = 3, date = LocalDate.of(2023, 11, 5), organization = "Org A", hours = 2.0f, category = "Comunitario", narrative = "Detail C")
        )
        
        coEvery { repository.getAllEntries() } returns flowOf(entries)
        coEvery { repository.getEntriesByCategory("Comunitario") } returns flowOf(entries.filter { it.category == "Comunitario" })
        coEvery { repository.searchEntries("Org B") } returns flowOf(entries.filter { it.organization.contains("Org B") })
        
        viewModel = HistoryViewModel(repository)
    }

    @Test
    fun `initial state emits all entries and calculates total hours correctly`() = runTest {
        viewModel.entries.test {
            // Wait for initial empty state, then the data load
            val initial = awaitItem()
            if (initial.isEmpty()) {
                val loaded = awaitItem()
                assertEquals(3, loaded.size)
            } else {
                assertEquals(3, initial.size)
            }
        }
        
        viewModel.totalFilteredHours.test {
            val total = awaitItem()
            // 5 + 3 + 2 = 10
            if (total == 0f) {
                val updatedTotal = awaitItem()
                assertEquals(10f, updatedTotal)
            } else {
                assertEquals(10f, total)
            }
        }
    }

    @Test
    fun `changing category filters entries`() = runTest {
        viewModel.onCategoryChange("Comunitario")
        
        viewModel.entries.test {
            // First might be empty or all items depending on timing
            val items = expectMostRecentItem()
            assertEquals(2, items.size)
        }
    }

    @Test
    fun `searching updates entries correctly`() = runTest {
        viewModel.onSearchChange("Org B")
        
        viewModel.entries.test {
            val items = expectMostRecentItem()
            assertEquals(1, items.size)
            assertEquals("Org B", items.first().organization)
        }
    }
}
