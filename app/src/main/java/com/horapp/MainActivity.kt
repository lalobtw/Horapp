package com.horapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.horapp.ui.navigation.AppNavigation
import com.horapp.ui.navigation.Screen
import com.horapp.ui.theme.HorappTheme
import com.horapp.ui.viewmodel.MainViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(
            statusBarStyle = androidx.activity.SystemBarStyle.dark(android.graphics.Color.TRANSPARENT)
        )

        setContent {
            HorappTheme {
                val hasProfile by viewModel.hasProfile.collectAsState()

                if (hasProfile != null) {
                    val startDestination = if (hasProfile == true) {
                        Screen.Dashboard.route
                    } else {
                        Screen.Setup.route
                    }
                    AppNavigation(startDestination = startDestination)
                }
            }
        }
    }
}
