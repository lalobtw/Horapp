package com.horapp.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.horapp.data.repository.StudentProfileRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    profileRepository: StudentProfileRepository
) : ViewModel() {

    /** null = cargando, true = tiene perfil, false = sin perfil (ir a Setup) */
    val hasProfile = profileRepository.getProfile()
        .map { it != null }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)
}
