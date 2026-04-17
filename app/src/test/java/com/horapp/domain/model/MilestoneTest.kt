package com.horapp.domain.model

import org.junit.Assert.assertEquals
import org.junit.Test

class MilestoneTest {

    @Test
    fun `buildMilestones returns all milestones as uncompleted when 0 hours`() {
        val milestones = buildMilestones(0f)
        assertEquals(5, milestones.size)
        milestones.forEach { 
            assertEquals(false, it.isCompleted)
        }
    }

    @Test
    fun `buildMilestones completes first step at 10 hours`() {
        val milestones = buildMilestones(10f)
        assertEquals(true, milestones.find { it.title == "Primer Paso" }?.isCompleted)
        assertEquals(false, milestones.find { it.title == "En Marcha" }?.isCompleted)
    }

    @Test
    fun `buildMilestones completes middle step at 120 hours`() {
        val milestones = buildMilestones(120f)
        assertEquals(true, milestones.find { it.title == "Mitad del Camino" }?.isCompleted)
        assertEquals(false, milestones.find { it.title == "Community Leader" }?.isCompleted)
    }

    @Test
    fun `buildMilestones completes all steps at 480 hours`() {
        val milestones = buildMilestones(480f)
        milestones.forEach { 
            assertEquals("Milestone ${it.title} should be completed", true, it.isCompleted)
        }
    }
}
