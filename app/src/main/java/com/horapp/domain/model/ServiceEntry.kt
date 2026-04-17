package com.horapp.domain.model

import java.time.LocalDate

data class ServiceEntry(
    val id: Long = 0,
    val date: LocalDate,
    val organization: String,
    val hours: Float,
    val category: String,
    val narrative: String,
    val isVerified: Boolean = false
)
