package com.horapp.ui.screens.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.horapp.data.repository.ServiceEntryRepository
import com.horapp.data.repository.StudentProfileRepository
import com.horapp.domain.model.StudentProfile
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ProfileUiState(
    val profile: StudentProfile? = null,
    val totalHours: Float = 0f,
    val isEditing: Boolean = false,
    // Campos editables
    val editName: String = "",
    val editStudentId: String = "",
    val editEmail: String = "",
    val editMajor: String = "",
    val editLocation: String = "",
    val editInstitution: String = "",
    val editGoal: String = ""
)

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val profileRepo: StudentProfileRepository,
    private val entryRepo: ServiceEntryRepository
) : ViewModel() {

    val uiState: StateFlow<ProfileUiState> = combine(
        profileRepo.getProfile(),
        entryRepo.getTotalHours()
    ) { profile, hours ->
        ProfileUiState(profile = profile, totalHours = hours)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), ProfileUiState())

    private val _editing = MutableStateFlow(false)
    private val _editFields = MutableStateFlow<Map<String, String>>(emptyMap())

    val fullState: StateFlow<ProfileUiState> = combine(uiState, _editing, _editFields) { base, editing, fields ->
        if (editing) {
            base.copy(
                isEditing = true,
                editName = fields["name"] ?: base.profile?.fullName ?: "",
                editStudentId = fields["id"] ?: base.profile?.studentId ?: "",
                editEmail = fields["email"] ?: base.profile?.email ?: "",
                editMajor = fields["major"] ?: base.profile?.major ?: "",
                editLocation = fields["location"] ?: base.profile?.serviceLocation ?: "",
                editInstitution = fields["institution"] ?: base.profile?.institution ?: "",
                editGoal = fields["goal"] ?: base.profile?.totalHoursGoal?.toString() ?: "480"
            )
        } else base.copy(isEditing = false)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), ProfileUiState())

    fun startEditing() {
        _editing.value = true
        _editFields.value = emptyMap()
    }

    fun cancelEditing() { _editing.value = false }

    fun updateField(key: String, value: String) {
        _editFields.update { it + (key to value) }
    }

    fun saveEdits() {
        val s = fullState.value
        val current = s.profile ?: return
        viewModelScope.launch {
            profileRepo.saveProfile(
                current.copy(
                    fullName = s.editName.ifBlank { current.fullName },
                    studentId = s.editStudentId.ifBlank { current.studentId },
                    email = s.editEmail,
                    major = s.editMajor,
                    serviceLocation = s.editLocation.ifBlank { current.serviceLocation },
                    institution = s.editInstitution.ifBlank { current.institution },
                    totalHoursGoal = s.editGoal.toFloatOrNull() ?: current.totalHoursGoal
                )
            )
            _editing.value = false
        }
    }
}
