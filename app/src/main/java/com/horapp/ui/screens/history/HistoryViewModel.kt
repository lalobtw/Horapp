package com.horapp.ui.screens.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.horapp.data.repository.ServiceEntryRepository
import com.horapp.domain.model.ServiceEntry
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.format.DateTimeFormatter
import java.util.Locale
import javax.inject.Inject

const val FILTER_ALL = "Todos"

@HiltViewModel
class HistoryViewModel @Inject constructor(
    private val repository: ServiceEntryRepository
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    private val _selectedCategory = MutableStateFlow(FILTER_ALL)
    val selectedCategory = _selectedCategory.asStateFlow()

    private val _selectedMonth = MutableStateFlow(FILTER_ALL)
    val selectedMonth = _selectedMonth.asStateFlow()

    val availableMonths: StateFlow<List<String>> = repository.getAllEntries()
        .map { list ->
            val formatter = DateTimeFormatter.ofPattern("MMMM yyyy", Locale("es", "ES"))
            val months = list.map { it.date.format(formatter).replaceFirstChar { c -> c.uppercase() } }.distinct()
            listOf(FILTER_ALL) + months
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), listOf(FILTER_ALL))

    @OptIn(ExperimentalCoroutinesApi::class)
    val entries: StateFlow<List<ServiceEntry>> = combine(_searchQuery, _selectedCategory, _selectedMonth) { q, cat, month -> Triple(q, cat, month) }
        .flatMapLatest { (query, category, month) ->
            val source = when {
                query.isNotBlank() -> repository.searchEntries(query)
                category == FILTER_ALL -> repository.getAllEntries()
                else -> repository.getEntriesByCategory(category)
            }
            if (month == FILTER_ALL) source else {
                val formatter = DateTimeFormatter.ofPattern("MMMM yyyy", Locale("es", "ES"))
                source.map { list ->
                    list.filter { it.date.format(formatter).replaceFirstChar { c -> c.uppercase() } == month }
                }
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val totalFilteredHours: StateFlow<Float> = entries
        .map { list -> list.sumOf { it.hours.toDouble() }.toFloat() }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), 0f)

    fun onSearchChange(q: String) = _searchQuery.update { q }
    fun onCategoryChange(cat: String) = _selectedCategory.update { cat }
    fun onMonthChange(month: String) = _selectedMonth.update { month }

    fun deleteEntry(entry: ServiceEntry) {
        viewModelScope.launch { repository.deleteEntry(entry) }
    }
}

