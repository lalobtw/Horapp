package com.horapp.data.local

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.assertIsDisplayed
import org.junit.Rule
import org.junit.Test
import androidx.compose.material3.Text

class SimpleUITest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun basicTextViewTest() {
        composeTestRule.setContent {
            Text("Horapp Testing UI")
        }

        composeTestRule.onNodeWithText("Horapp Testing UI").assertIsDisplayed()
    }
}
