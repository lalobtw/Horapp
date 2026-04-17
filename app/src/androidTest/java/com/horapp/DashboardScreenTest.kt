package com.horapp

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.assertIsDisplayed
import androidx.test.platform.app.InstrumentationRegistry
import com.horapp.ui.screens.dashboard.DashboardScreenContent
import com.horapp.ui.screens.dashboard.DashboardUiState
import com.horapp.ui.theme.HorappTheme
import org.junit.Rule
import org.junit.Test
import com.horapp.R
import com.horapp.domain.model.ServiceEntry
import com.horapp.domain.model.Milestone

class DashboardScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun dashboard_displays_user_name_correctly() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val logHoursText = context.getString(R.string.log_hours)
        
        // Creamos un estado de prueba con un nombre específico
        val fakeProfile = com.horapp.domain.model.StudentProfile(
            fullName = "Lalo Prueba",
            studentId = "123",
            email = "lalo@test.com",
            major = "Ingeniería",
            serviceLocation = "Laboratorio",
            institution = "Uni Test",
            totalHoursGoal = 480f
        )
        val fakeState = DashboardUiState(
            profile = fakeProfile,
            totalHours = 120.5f,
            recentEntries = emptyList<ServiceEntry>(),
            milestones = emptyList<Milestone>()
        )

        composeTestRule.setContent {
            HorappTheme {
                DashboardScreenContent(
                    state = fakeState,
                    onLogHours = {}
                )
            }
        }

        // Verificamos que aparezca el saludo con el primer nombre
        val expectedGreeting = context.getString(R.string.hello_user, "Lalo")
        composeTestRule.onNodeWithText(expectedGreeting).assertIsDisplayed()
        
        // Verificamos que el botón de registro esté presente
        composeTestRule.onNodeWithText(logHoursText).assertIsDisplayed()
        
        // Verificamos que se muestren las horas (120.5)
        composeTestRule.onNodeWithText("120.5").assertIsDisplayed()
    }
}
