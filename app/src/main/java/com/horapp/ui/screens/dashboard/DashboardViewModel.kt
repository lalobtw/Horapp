package com.horapp.ui.screens.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.horapp.data.repository.ServiceEntryRepository
import com.horapp.data.repository.StudentProfileRepository
import com.horapp.domain.model.Milestone
import com.horapp.domain.model.ServiceEntry
import com.horapp.domain.model.StudentProfile
import com.horapp.domain.model.buildMilestones
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import javax.inject.Inject

data class DashboardUiState(
    val profile: StudentProfile? = null,
    val totalHours: Float = 0f,
    val recentEntries: List<ServiceEntry> = emptyList(),
    val milestones: List<Milestone> = emptyList()
)

@HiltViewModel
class DashboardViewModel @Inject constructor(
    profileRepo: StudentProfileRepository,
    entryRepo: ServiceEntryRepository
) : ViewModel() {

    val uiState: StateFlow<DashboardUiState> = combine(
        profileRepo.getProfile(),
        entryRepo.getTotalHours(),
        entryRepo.getRecentEntries(5)
    ) { profile, totalHours, recent ->
        DashboardUiState(
            profile = profile,
            totalHours = totalHours,
            recentEntries = recent,
            milestones = buildMilestones(totalHours)
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), DashboardUiState())
}
