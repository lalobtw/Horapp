package com.horapp.ui.screens.setup

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.horapp.data.repository.StudentProfileRepository
import com.horapp.domain.model.StudentProfile
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SetupUiState(
    val fullName: String = "",
    val studentId: String = "",
    val email: String = "",
    val major: String = "",
    val serviceLocation: String = "",
    val institution: String = "",
    val hoursGoal: String = "480",
    val isSaving: Boolean = false
)

@HiltViewModel
class SetupViewModel @Inject constructor(
    private val repository: StudentProfileRepository
) : ViewModel() {

    private val _state = MutableStateFlow(SetupUiState())
    val state = _state.asStateFlow()

    fun onFullNameChange(v: String) = _state.update { it.copy(fullName = v) }
    fun onStudentIdChange(v: String) = _state.update { it.copy(studentId = v) }
    fun onEmailChange(v: String) = _state.update { it.copy(email = v) }
    fun onMajorChange(v: String) = _state.update { it.copy(major = v) }
    fun onServiceLocationChange(v: String) = _state.update { it.copy(serviceLocation = v) }
    fun onInstitutionChange(v: String) = _state.update { it.copy(institution = v) }
    fun onHoursGoalChange(v: String) = _state.update { it.copy(hoursGoal = v) }

    fun isFormValid(): Boolean = with(_state.value) {
        fullName.isNotBlank() && studentId.isNotBlank() && institution.isNotBlank() && serviceLocation.isNotBlank()
    }

    fun saveProfile(onDone: () -> Unit) {
        val s = _state.value
        _state.update { it.copy(isSaving = true) }
        viewModelScope.launch {
            repository.saveProfile(
                StudentProfile(
                    fullName = s.fullName.trim(),
                    studentId = s.studentId.trim(),
                    email = s.email.trim(),
                    major = s.major.trim(),
                    serviceLocation = s.serviceLocation.trim(),
                    institution = s.institution.trim(),
                    totalHoursGoal = s.hoursGoal.toFloatOrNull() ?: 480f
                )
            )
            onDone()
        }
    }
}
