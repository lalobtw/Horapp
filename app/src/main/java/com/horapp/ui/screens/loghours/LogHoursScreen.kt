package com.horapp.ui.screens.loghours

import android.app.DatePickerDialog
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import com.horapp.R
import com.horapp.ui.theme.*
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LogHoursScreen(
    onBack: () -> Unit,
    viewModel: LogHoursViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val context = LocalContext.current
    val dateFormatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.LONG)
    var showCategoryMenu by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.log_hours), fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Outlined.ArrowBack, stringResource(R.string.back_btn))
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = SurfaceContainerLowest,
                    titleContentColor = OnSurface
                )
            )
        },
        containerColor = Surface
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // ── Encabezado informativo ─────────────────────────────────────────
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceContainerLowest),
                elevation = CardDefaults.cardElevation(0.dp)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Outlined.EditNote, null, tint = Primary, modifier = Modifier.size(28.dp))
                    Column {
                        Text(stringResource(R.string.log_details), style = MaterialTheme.typography.titleSmall, color = Primary, fontWeight = FontWeight.SemiBold)
                        Text(stringResource(R.string.log_details_desc), style = MaterialTheme.typography.bodySmall, color = OnSurfaceVariant)
                    }
                }
            }

            // ── Fecha ─────────────────────────────────────────────────────────
            Text(stringResource(R.string.service_date), style = MaterialTheme.typography.labelLarge, color = Primary)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(SurfaceContainerLow)
                    .clickable {
                        DatePickerDialog(
                            context,
                            { _, y, m, d ->
                                viewModel.onDateChange(java.time.LocalDate.of(y, m + 1, d))
                            },
                            state.date.year, state.date.monthValue - 1, state.date.dayOfMonth
                        ).show()
                    }
                    .padding(horizontal = 16.dp, vertical = 14.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    Icon(Icons.Outlined.CalendarMonth, null, tint = Primary, modifier = Modifier.size(20.dp))
                    Text(state.date.format(dateFormatter), style = MaterialTheme.typography.bodyLarge, color = OnSurface)
                }
            }

            // ── Organización ──────────────────────────────────────────────────
            Text(stringResource(R.string.organization_label), style = MaterialTheme.typography.labelLarge, color = Primary)
            OutlinedTextField(
                value = state.organization,
                onValueChange = viewModel::onOrganizationChange,
                placeholder = { Text(stringResource(R.string.organization_placeholder), color = OutlineVariant) },
                leadingIcon = { Icon(Icons.Outlined.CorporateFare, null, tint = OnSurfaceVariant) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = formFieldColors(),
                keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Words),
                singleLine = true
            )

            // ── Horas y Categoría ─────────────────────────────────────────────
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(stringResource(R.string.hours_label), style = MaterialTheme.typography.labelLarge, color = Primary)
                    OutlinedTextField(
                        value = state.hours,
                        onValueChange = viewModel::onHoursChange,
                        placeholder = { Text("0.0", color = OutlineVariant) },
                        leadingIcon = { Icon(Icons.Outlined.Timer, null, tint = OnSurfaceVariant) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        shape = RoundedCornerShape(12.dp),
                        colors = formFieldColors(),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(stringResource(R.string.category_label), style = MaterialTheme.typography.labelLarge, color = Primary)
                    ExposedDropdownMenuBox(
                        expanded = showCategoryMenu,
                        onExpandedChange = { showCategoryMenu = it }
                    ) {
                        OutlinedTextField(
                            value = state.category,
                            onValueChange = {},
                            readOnly = true,
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(showCategoryMenu) },
                            shape = RoundedCornerShape(12.dp),
                            colors = formFieldColors(),
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth().menuAnchor()
                        )
                        ExposedDropdownMenu(
                            expanded = showCategoryMenu,
                            onDismissRequest = { showCategoryMenu = false }
                        ) {
                            serviceCategories.forEach { cat ->
                                DropdownMenuItem(
                                    text = { Text(cat) },
                                    onClick = {
                                        viewModel.onCategoryChange(cat)
                                        showCategoryMenu = false
                                    }
                                )
                            }
                        }
                    }
                }
            }

            // ── Narrativa ─────────────────────────────────────────────────────
            Text(stringResource(R.string.activities_desc_label), style = MaterialTheme.typography.labelLarge, color = Primary)
            OutlinedTextField(
                value = state.narrative,
                onValueChange = viewModel::onNarrativeChange,
                placeholder = { Text(stringResource(R.string.activities_desc_placeholder), color = OutlineVariant) },
                modifier = Modifier.fillMaxWidth().height(140.dp),
                shape = RoundedCornerShape(12.dp),
                colors = formFieldColors(),
                keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences),
                maxLines = 6
            )

            state.error?.let { err ->
                Text(err, color = Error, style = MaterialTheme.typography.bodySmall)
            }

            Spacer(Modifier.height(8.dp))

            // ── Botón enviar ──────────────────────────────────────────────────
            Button(
                onClick = { viewModel.submitEntry(onBack) },
                enabled = viewModel.isFormValid() && !state.isSaving,
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                contentPadding = PaddingValues(0.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            if (viewModel.isFormValid()) Brush.linearGradient(listOf(Primary, PrimaryContainer))
                            else Brush.linearGradient(listOf(SurfaceContainerHigh, SurfaceContainerHigh)),
                            RoundedCornerShape(16.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    if (state.isSaving) {
                        CircularProgressIndicator(color = OnPrimary, modifier = Modifier.size(24.dp))
                    } else {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Icon(Icons.Outlined.Send, null, tint = if (viewModel.isFormValid()) OnPrimary else OnSurfaceVariant)
                            Text(
                                stringResource(R.string.save_log_btn),
                                color = if (viewModel.isFormValid()) OnPrimary else OnSurfaceVariant,
                                fontWeight = FontWeight.SemiBold,
                                style = MaterialTheme.typography.titleMedium
                            )
                        }
                    }
                }
            }
            Spacer(Modifier.height(16.dp))
        }
    }
}

@Composable
private fun formFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor = Primary,
    focusedLabelColor = Primary,
    unfocusedBorderColor = OutlineVariant,
    focusedContainerColor = SurfaceContainerLowest,
    unfocusedContainerColor = SurfaceContainerLow
)
