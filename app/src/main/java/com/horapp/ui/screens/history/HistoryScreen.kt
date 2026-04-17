package com.horapp.ui.screens.history

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.horapp.R
import com.horapp.domain.model.ServiceEntry
import com.horapp.ui.screens.loghours.serviceCategories
import com.horapp.ui.theme.*
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

@Composable
fun HistoryScreen(viewModel: HistoryViewModel = hiltViewModel()) {
    val entries by viewModel.entries.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val selectedCategory by viewModel.selectedCategory.collectAsState()
    val selectedMonth by viewModel.selectedMonth.collectAsState()
    val availableMonths by viewModel.availableMonths.collectAsState()
    val totalHours by viewModel.totalFilteredHours.collectAsState()

    val categories = listOf(FILTER_ALL) + serviceCategories

    LazyColumn(
        modifier = Modifier.fillMaxSize().background(Surface),
        contentPadding = PaddingValues(bottom = 32.dp)
    ) {
        // ── Encabezado ────────────────────────────────────────────────────────
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Brush.linearGradient(listOf(Primary, PrimaryContainer)))
                    .padding(horizontal = 24.dp)
                    .statusBarsPadding()
                    .padding(top = 16.dp, bottom = 64.dp)
            ) {
                Column {
                    Icon(Icons.Outlined.History, null, tint = OnPrimary.copy(alpha = 0.7f), modifier = Modifier.size(24.dp).padding(bottom = 4.dp))
                    Text(stringResource(R.string.history_title), style = MaterialTheme.typography.headlineMedium, color = OnPrimary, fontWeight = FontWeight.ExtraBold)
                    Spacer(Modifier.height(4.dp))
                    Text(stringResource(R.string.history_desc), style = MaterialTheme.typography.bodyMedium, color = OnPrimary.copy(alpha = 0.75f))
                }
            }
        }
        item {
            // Resumen rápido
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
                    .offset(y = (-24).dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceContainerLowest),
                elevation = CardDefaults.cardElevation(0.dp)
            ) {
                Row(modifier = Modifier.padding(16.dp), horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Outlined.Schedule, null, tint = Primary)
                    Column {
                        Text(stringResource(R.string.hours_format, totalHours), style = MaterialTheme.typography.titleMedium, color = OnSurface, fontWeight = FontWeight.Bold)
                        Text(stringResource(R.string.records_shown_format, entries.size), style = MaterialTheme.typography.bodySmall, color = OnSurfaceVariant)
                    }
                }
            }
        }

        // ── Buscador ──────────────────────────────────────────────────────────
        item {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = viewModel::onSearchChange,
                placeholder = { Text(stringResource(R.string.search_placeholder), color = OutlineVariant) },
                leadingIcon = { Icon(Icons.Outlined.Search, null, tint = OnSurfaceVariant) },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { viewModel.onSearchChange("") }) {
                            Icon(Icons.Outlined.Close, null, tint = OnSurfaceVariant)
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Primary,
                    unfocusedBorderColor = OutlineVariant,
                    focusedContainerColor = SurfaceContainerLowest,
                    unfocusedContainerColor = SurfaceContainerLow
                ),
                singleLine = true
            )
            Spacer(Modifier.height(12.dp))
        }

        // ── Chips de filtro ───────────────────────────────────────────────────
        item {
            LazyRow(
                contentPadding = PaddingValues(horizontal = 24.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(categories) { cat ->
                    FilterChip(
                        selected = cat == selectedCategory,
                        onClick = { viewModel.onCategoryChange(cat) },
                        label = { Text(cat) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = Primary,
                            selectedLabelColor = OnPrimary,
                            containerColor = SurfaceContainerHigh,
                            labelColor = OnSurfaceVariant
                        ),
                        border = FilterChipDefaults.filterChipBorder(
                            enabled = true, selected = cat == selectedCategory,
                            borderColor = OutlineVariant, selectedBorderColor = Primary
                        )
                    )
                }
            }
            Spacer(Modifier.height(8.dp))
            LazyRow(
                contentPadding = PaddingValues(horizontal = 24.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(availableMonths) { month ->
                    FilterChip(
                        selected = month == selectedMonth,
                        onClick = { viewModel.onMonthChange(month) },
                        label = { Text(month) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = Primary,
                            selectedLabelColor = OnPrimary,
                            containerColor = SurfaceContainerHigh,
                            labelColor = OnSurfaceVariant
                        ),
                        border = FilterChipDefaults.filterChipBorder(
                            enabled = true, selected = month == selectedMonth,
                            borderColor = OutlineVariant, selectedBorderColor = Primary
                        )
                    )
                }
            }
            Spacer(Modifier.height(16.dp))
        }

        // ── Lista de entradas ─────────────────────────────────────────────────
        if (entries.isEmpty()) {
            item {
                Box(
                    modifier = Modifier.fillMaxWidth().padding(48.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Outlined.Inbox, null, tint = OutlineVariant, modifier = Modifier.size(48.dp))
                        Spacer(Modifier.height(12.dp))
                        Text(stringResource(R.string.no_results), color = OnSurfaceVariant, style = MaterialTheme.typography.bodyLarge)
                    }
                }
            }
        } else {
            items(entries, key = { it.id }) { entry ->
                HistoryEntryCard(
                    entry = entry,
                    onDelete = { viewModel.deleteEntry(entry) },
                    modifier = Modifier.padding(horizontal = 24.dp, vertical = 4.dp)
                )
            }
        }
    }
}

@Composable
private fun HistoryEntryCard(entry: ServiceEntry, onDelete: () -> Unit, modifier: Modifier = Modifier) {
    val formatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM)
    var showDeleteDialog by remember { mutableStateOf(false) }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text(stringResource(R.string.delete_record_title)) },
            text = { Text(stringResource(R.string.delete_record_confirm)) },
            confirmButton = {
                TextButton(onClick = { onDelete(); showDeleteDialog = false }) {
                    Text(stringResource(R.string.delete_btn), color = Error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) { Text(stringResource(R.string.cancel)) }
            }
        )
    }

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceContainerLowest),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier.size(40.dp).clip(RoundedCornerShape(12.dp)).background(TertiaryFixed),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Outlined.WorkHistory, null, tint = OnTertiaryFixedVariant, modifier = Modifier.size(20.dp))
                }
                Spacer(Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(entry.organization, style = MaterialTheme.typography.titleSmall, color = OnSurface, fontWeight = FontWeight.Bold)
                    Text(entry.category, style = MaterialTheme.typography.bodySmall, color = OnSurfaceVariant)
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text("+%.1f h".format(entry.hours), style = MaterialTheme.typography.titleSmall, color = Primary, fontWeight = FontWeight.Bold)
                    Text(entry.date.format(formatter), style = MaterialTheme.typography.labelSmall, color = OnSurfaceVariant)
                }
            }
            if (entry.narrative.isNotBlank()) {
                Spacer(Modifier.height(10.dp))
                Text(entry.narrative, style = MaterialTheme.typography.bodySmall, color = OnSurfaceVariant, maxLines = 2)
            }
            Spacer(Modifier.height(10.dp))
            Row(horizontalArrangement = Arrangement.End, modifier = Modifier.fillMaxWidth()) {
                TextButton(onClick = { showDeleteDialog = true }, contentPadding = PaddingValues(horizontal = 8.dp)) {
                    Icon(Icons.Outlined.DeleteOutline, null, tint = Error, modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(4.dp))
                    Text(stringResource(R.string.delete_btn), color = Error, style = MaterialTheme.typography.labelMedium)
                }
            }
        }
    }
}
