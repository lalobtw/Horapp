package com.horapp.ui.screens.setup

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import com.horapp.R
import com.horapp.ui.theme.*

@Composable
fun SetupScreen(
    onSetupComplete: () -> Unit,
    viewModel: SetupViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Surface)
    ) {
        // Header decorativo con gradiente
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(260.dp)
                .background(
                    Brush.linearGradient(
                        colors = listOf(Primary, PrimaryContainer),
                        start = androidx.compose.ui.geometry.Offset(0f, 0f),
                        end = androidx.compose.ui.geometry.Offset(1000f, 500f)
                    )
                )
        ) {
            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .statusBarsPadding()
                    .padding(start = 24.dp, bottom = 72.dp)
            ) {
                Icon(
                    imageVector = Icons.Outlined.School,
                    contentDescription = null,
                    tint = OnPrimary.copy(alpha = 0.7f),
                    modifier = Modifier.size(36.dp)
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    text = stringResource(R.string.welcome),
                    style = MaterialTheme.typography.displaySmall,
                    color = OnPrimary
                )
                Text(
                    text = stringResource(R.string.setup_profile_desc),
                    style = MaterialTheme.typography.bodyLarge,
                    color = OnPrimary.copy(alpha = 0.8f)
                )
            }
        }

        // Formulario
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 220.dp)
                .clip(RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp))
                .background(Surface)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 32.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = stringResource(R.string.student_info),
                style = MaterialTheme.typography.titleLarge,
                color = OnSurface
            )
            Spacer(Modifier.height(4.dp))

            SetupField(
                value = state.fullName,
                onChange = viewModel::onFullNameChange,
                label = stringResource(R.string.full_name_label),
                icon = Icons.Outlined.Person,
                imeAction = ImeAction.Next
            )
            SetupField(
                value = state.studentId,
                onChange = viewModel::onStudentIdChange,
                label = stringResource(R.string.id_control_label),
                icon = Icons.Outlined.Badge,
                imeAction = ImeAction.Next
            )
            SetupField(
                value = state.email,
                onChange = viewModel::onEmailChange,
                label = stringResource(R.string.institutional_email_label),
                icon = Icons.Outlined.Email,
                keyboardType = KeyboardType.Email,
                imeAction = ImeAction.Next
            )
            SetupField(
                value = state.major,
                onChange = viewModel::onMajorChange,
                label = stringResource(R.string.major_label),
                icon = Icons.Outlined.MenuBook,
                imeAction = ImeAction.Next
            )
            SetupField(
                value = state.serviceLocation,
                onChange = viewModel::onServiceLocationChange,
                label = stringResource(R.string.service_location_setup_label),
                icon = Icons.Outlined.Business,
                imeAction = ImeAction.Next
            )
            SetupField(
                value = state.institution,
                onChange = viewModel::onInstitutionChange,
                label = stringResource(R.string.institution_setup_label),
                icon = Icons.Outlined.AccountBalance,
                imeAction = ImeAction.Next
            )
            SetupField(
                value = state.hoursGoal,
                onChange = viewModel::onHoursGoalChange,
                label = stringResource(R.string.meta_hours_setup_label),
                icon = Icons.Outlined.Timer,
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Done
            )

            Spacer(Modifier.height(8.dp))

            // Botón primario con gradiente
            Button(
                onClick = { viewModel.saveProfile(onSetupComplete) },
                enabled = viewModel.isFormValid() && !state.isSaving,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                contentPadding = PaddingValues(0.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.linearGradient(listOf(Primary, PrimaryContainer)),
                            RoundedCornerShape(16.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    if (state.isSaving) {
                        CircularProgressIndicator(color = OnPrimary, modifier = Modifier.size(24.dp))
                    } else {
                        Text(
                            text = stringResource(R.string.start_log_btn),
                            style = MaterialTheme.typography.titleMedium,
                            color = OnPrimary
                        )
                    }
                }
            }

            Spacer(Modifier.height(32.dp))
        }
    }
}

@Composable
private fun SetupField(
    value: String,
    onChange: (String) -> Unit,
    label: String,
    icon: ImageVector,
    keyboardType: KeyboardType = KeyboardType.Text,
    imeAction: ImeAction = ImeAction.Next
) {
    OutlinedTextField(
        value = value,
        onValueChange = onChange,
        label = { Text(label) },
        leadingIcon = { Icon(imageVector = icon, contentDescription = null, tint = Primary) },
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        keyboardOptions = KeyboardOptions(
            keyboardType = keyboardType,
            capitalization = if (keyboardType == KeyboardType.Text) KeyboardCapitalization.Words else KeyboardCapitalization.None,
            imeAction = imeAction
        ),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = Primary,
            focusedLabelColor = Primary,
            unfocusedBorderColor = OutlineVariant,
            unfocusedLabelColor = OnSurfaceVariant,
            focusedContainerColor = SurfaceContainerLowest,
            unfocusedContainerColor = SurfaceContainerLow
        ),
        singleLine = true
    )
}
