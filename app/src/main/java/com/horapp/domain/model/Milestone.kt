package com.horapp.domain.model

data class Milestone(
    val title: String,
    val description: String,
    val targetHours: Float,
    val isCompleted: Boolean
)

fun buildMilestones(totalHours: Float): List<Milestone> = listOf(
    Milestone(
        title = "Primer Paso",
        description = "Registra tus primeras 10 horas",
        targetHours = 10f,
        isCompleted = totalHours >= 10f
    ),
    Milestone(
        title = "En Marcha",
        description = "Alcanza las 50 horas de servicio",
        targetHours = 50f,
        isCompleted = totalHours >= 50f
    ),
    Milestone(
        title = "Mitad del Camino",
        description = "Has completado 120 horas",
        targetHours = 120f,
        isCompleted = totalHours >= 120f
    ),
    Milestone(
        title = "Community Leader",
        description = "240 horas de impacto comunitario",
        targetHours = 240f,
        isCompleted = totalHours >= 240f
    ),
    Milestone(
        title = "Meta Completada 🎉",
        description = "Has alcanzado tu meta de servicio social",
        targetHours = 480f,
        isCompleted = totalHours >= 480f
    )
)
