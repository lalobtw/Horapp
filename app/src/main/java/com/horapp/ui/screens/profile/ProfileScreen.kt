package com.horapp.ui.screens.profile

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import com.horapp.R
import com.horapp.ui.theme.*
import com.horapp.utils.PdfReportGenerator
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import androidx.compose.ui.text.input.KeyboardCapitalization

@Composable
fun ProfileScreen(viewModel: ProfileViewModel = hiltViewModel()) {
    val state by viewModel.fullState.collectAsState()
    val profile = state.profile
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Surface)
            .verticalScroll(rememberScrollState())
    ) {
        // ── Header ────────────────────────────────────────────────────────────
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Brush.linearGradient(listOf(Primary, PrimaryContainer)))
                .padding(horizontal = 24.dp)
                .statusBarsPadding()
                .padding(top = 16.dp, bottom = 64.dp)
        ) {
            Column(horizontalAlignment = Alignment.Start) {
                Icon(Icons.Outlined.School, null, tint = OnPrimary.copy(alpha = 0.7f), modifier = Modifier.size(24.dp).padding(bottom = 4.dp))
                Text(stringResource(R.string.student_profile), style = MaterialTheme.typography.headlineMedium, color = OnPrimary, fontWeight = FontWeight.ExtraBold)
                Spacer(Modifier.height(4.dp))
                Text(profile?.institution ?: "", style = MaterialTheme.typography.bodyMedium, color = OnPrimary.copy(alpha = 0.75f))
            }
        }

        // ── Avatar + nombre ───────────────────────────────────────────────────
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .offset(y = (-20).dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(PrimaryFixed),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = profile?.fullName?.firstOrNull()?.toString() ?: "?",
                    style = MaterialTheme.typography.headlineMedium,
                    color = Primary,
                    fontWeight = FontWeight.ExtraBold
                )
            }
            Spacer(Modifier.height(8.dp))
            Text(profile?.fullName ?: "", style = MaterialTheme.typography.titleLarge, color = OnSurface, fontWeight = FontWeight.ExtraBold)
            Text(profile?.serviceLocation ?: "", style = MaterialTheme.typography.bodyMedium, color = OnSurfaceVariant)
        }



        // ── Información personal ──────────────────────────────────────────────
        Card(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = SurfaceContainerLowest),
            elevation = CardDefaults.cardElevation(0.dp)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Text(stringResource(R.string.personal_info), style = MaterialTheme.typography.titleSmall, color = OnSurface, fontWeight = FontWeight.Bold)
                    if (!state.isEditing) {
                        TextButton(onClick = viewModel::startEditing) {
                            Icon(Icons.Outlined.Edit, null, modifier = Modifier.size(16.dp))
                            Spacer(Modifier.width(4.dp))
                            Text(stringResource(R.string.edit))
                        }
                    }
                }
                Spacer(Modifier.height(12.dp))

                if (state.isEditing) {
                    EditField(stringResource(R.string.name_label), state.editName) { viewModel.updateField("name", it) }
                    EditField(stringResource(R.string.id_label), state.editStudentId) { viewModel.updateField("id", it) }
                    EditField(stringResource(R.string.email_label), state.editEmail) { viewModel.updateField("email", it) }
                    EditField(stringResource(R.string.major_label), state.editMajor) { viewModel.updateField("major", it) }
                    EditField(stringResource(R.string.service_location_label), state.editLocation) { viewModel.updateField("location", it) }
                    EditField(stringResource(R.string.institution_label), state.editInstitution) { viewModel.updateField("institution", it) }
                    EditField(stringResource(R.string.hours_goal_label), state.editGoal) { viewModel.updateField("goal", it) }
                    Spacer(Modifier.height(8.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedButton(onClick = viewModel::cancelEditing, modifier = Modifier.weight(1f)) { Text(stringResource(R.string.cancel)) }
                        Button(onClick = viewModel::saveEdits, modifier = Modifier.weight(1f), colors = ButtonDefaults.buttonColors(containerColor = Primary)) { Text(stringResource(R.string.save)) }
                    }
                } else {
                    ProfileRow(label = stringResource(R.string.full_name_label), value = profile?.fullName ?: "—")
                    ProfileRow(label = stringResource(R.string.id_label), value = profile?.studentId ?: "—")
                    ProfileRow(label = stringResource(R.string.email_label), value = profile?.email ?: "—")
                    ProfileRow(label = stringResource(R.string.major_label), value = profile?.major ?: "—")
                    ProfileRow(label = stringResource(R.string.service_location_label), value = profile?.serviceLocation ?: "—")
                    ProfileRow(label = stringResource(R.string.institution_label), value = profile?.institution ?: "—")
                    ProfileRow(label = stringResource(R.string.hours_goal_label), value = "%.0f h".format(profile?.totalHoursGoal ?: 480f))
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        // ── Exportar PDF ──────────────────────────────────────────────────────
        Card(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = SurfaceContainerLowest),
            elevation = CardDefaults.cardElevation(0.dp)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(stringResource(R.string.export_report), style = MaterialTheme.typography.titleSmall, color = OnSurface, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(4.dp))
                Text(stringResource(R.string.export_report_desc), style = MaterialTheme.typography.bodySmall, color = OnSurfaceVariant)
                Spacer(Modifier.height(12.dp))
                Button(
                    onClick = { exportPdf(context, profile?.fullName ?: "Estudiante") },
                    modifier = Modifier.fillMaxWidth().height(48.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Primary)
                ) {
                    Icon(Icons.Outlined.PictureAsPdf, null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(8.dp))
                    Text(stringResource(R.string.export_to_pdf_btn))
                }
            }
        }

        Spacer(Modifier.height(32.dp))
    }
}

private fun exportPdf(context: Context, studentName: String) {
    val file = PdfReportGenerator.generate(context, studentName)
    val uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "application/pdf"
        putExtra(Intent.EXTRA_STREAM, uri)
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
    }
    context.startActivity(Intent.createChooser(intent, context.getString(R.string.share_pdf_title)))
}

@Composable
private fun StatCard(modifier: Modifier, label: String, value: String) {
    Card(modifier = modifier, shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = SurfaceContainerLow), elevation = CardDefaults.cardElevation(0.dp)) {
        Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(value, style = MaterialTheme.typography.headlineSmall, color = Primary, fontWeight = FontWeight.ExtraBold)
            Text(label, style = MaterialTheme.typography.bodySmall, color = OnSurfaceVariant)
        }
    }
}

@Composable
private fun ProfileRow(label: String, value: String) {
    Column(modifier = Modifier.fillMaxWidth().padding(vertical = 10.dp)) {
        Text(label, style = MaterialTheme.typography.labelMedium, color = OnSurfaceVariant)
        Text(value, style = MaterialTheme.typography.bodyLarge, color = OnSurface, fontWeight = FontWeight.Medium)
    }
    HorizontalDivider(color = OutlineVariant.copy(alpha = 0.3f), thickness = 0.5.dp)
}

@Composable
private fun EditField(label: String, value: String, onValueChange: (String) -> Unit) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        shape = RoundedCornerShape(10.dp),
        keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Words),
        singleLine = true,
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = Primary, focusedLabelColor = Primary,
            unfocusedBorderColor = OutlineVariant,
            focusedContainerColor = SurfaceContainerLowest, unfocusedContainerColor = SurfaceContainerLow
        )
    )
}
