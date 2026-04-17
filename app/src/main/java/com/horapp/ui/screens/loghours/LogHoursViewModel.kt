package com.horapp.ui.screens.loghours

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.horapp.data.repository.ServiceEntryRepository
import com.horapp.domain.model.ServiceEntry
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

val serviceCategories = listOf(
    "Comunitario", "Tutorías", "Restauración", "Social", "Investigación", "Administrativo", "Otro"
)

data class LogHoursUiState(
    val date: LocalDate = LocalDate.now(),
    val organization: String = "",
    val hours: String = "",
    val category: String = serviceCategories.first(),
    val narrative: String = "",
    val isSaving: Boolean = false,
    val saved: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class LogHoursViewModel @Inject constructor(
    private val repository: ServiceEntryRepository
) : ViewModel() {

    private val _state = MutableStateFlow(LogHoursUiState())
    val state = _state.asStateFlow()

    fun onDateChange(v: LocalDate) = _state.update { it.copy(date = v) }
    fun onOrganizationChange(v: String) = _state.update { it.copy(organization = v) }
    fun onHoursChange(v: String) = _state.update { it.copy(hours = v) }
    fun onCategoryChange(v: String) = _state.update { it.copy(category = v) }
    fun onNarrativeChange(v: String) = _state.update { it.copy(narrative = v) }

    fun isFormValid(): Boolean = with(_state.value) {
        organization.isNotBlank() &&
                hours.toFloatOrNull()?.let { it > 0 } == true &&
                narrative.isNotBlank()
    }

    fun submitEntry(onSuccess: () -> Unit) {
        val s = _state.value
        val hoursValue = s.hours.toFloatOrNull() ?: return
        _state.update { it.copy(isSaving = true, error = null) }
        viewModelScope.launch {
            try {
                repository.addEntry(
                    ServiceEntry(
                        date = s.date,
                        organization = s.organization.trim(),
                        hours = hoursValue,
                        category = s.category,
                        narrative = s.narrative.trim()
                    )
                )
                _state.update { it.copy(isSaving = false, saved = true) }
                onSuccess()
            } catch (e: Exception) {
                _state.update { it.copy(isSaving = false, error = "Error al guardar el registro") }
            }
        }
    }
}
