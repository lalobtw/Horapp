package com.horapp.ui.screens.dashboard

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.horapp.R
import com.horapp.domain.model.Milestone
import com.horapp.domain.model.ServiceEntry
import com.horapp.ui.theme.*
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import androidx.compose.foundation.Canvas
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size

@Composable
fun DashboardScreen(
    onLogHours: () -> Unit,
    viewModel: DashboardViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()
    DashboardScreenContent(
        state = state,
        onLogHours = onLogHours
    )
}

@Composable
fun DashboardScreenContent(
    state: DashboardUiState,
    onLogHours: () -> Unit
) {
    val profile = state.profile
    val progress = if ((profile?.totalHoursGoal ?: 0f) > 0f)
        (state.totalHours / (profile?.totalHoursGoal ?: 480f)).coerceIn(0f, 1f)
    else 0f

    Box(modifier = Modifier.fillMaxSize().background(Surface)) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 100.dp)
        ) {
            // ── Header ────────────────────────────────────────────────────────
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.linearGradient(
                                colors = listOf(Primary, PrimaryContainer),
                                start = Offset(0f, 0f),
                                end = Offset(1200f, 600f)
                            )
                        )
                        .padding(horizontal = 24.dp)
                        .statusBarsPadding()
                        .padding(top = 16.dp, bottom = 64.dp)
                ) {
                    Column {
                        Icon(
                            imageVector = Icons.Outlined.School,
                            contentDescription = null,
                            tint = OnPrimary.copy(alpha = 0.6f),
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(Modifier.height(4.dp))
                        Text(
                            text = if (profile != null) stringResource(R.string.hello_user, profile.fullName.split(" ").first()) else stringResource(R.string.service_hub),
                            style = MaterialTheme.typography.headlineMedium,
                            color = OnPrimary,
                            fontWeight = FontWeight.ExtraBold
                        )
                        Text(
                            text = stringResource(R.string.service_social_log),
                            style = MaterialTheme.typography.bodyMedium,
                            color = OnPrimary.copy(alpha = 0.75f)
                        )
                    }
                }
            }

            // ── Progress Ring Card ─────────────────────────────────────────────
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp)
                        .offset(y = (-20).dp),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = SurfaceContainerLowest),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = stringResource(R.string.total_progress),
                            style = MaterialTheme.typography.titleMedium,
                            color = OnSurfaceVariant,
                            modifier = Modifier.align(Alignment.Start)
                        )
                        Spacer(Modifier.height(24.dp))
                        ProgressRing(progress = progress, totalHours = state.totalHours, goalHours = profile?.totalHoursGoal ?: 480f)
                        Spacer(Modifier.height(24.dp))
                        Button(
                            onClick = onLogHours,
                            modifier = Modifier.fillMaxWidth().height(52.dp),
                            shape = RoundedCornerShape(16.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                            contentPadding = PaddingValues(0.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(Brush.linearGradient(listOf(Primary, PrimaryContainer)), RoundedCornerShape(16.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Icon(Icons.Filled.Add, null, tint = OnPrimary)
                                    Text(stringResource(R.string.log_hours), color = OnPrimary, fontWeight = FontWeight.SemiBold)
                                }
                            }
                        }
                    }
                }
            }

            // ── Métricas bento ────────────────────────────────────────────────
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp)
                        .offset(y = (-12).dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    MetricCard(
                        modifier = Modifier.weight(1f),
                        label = stringResource(R.string.total_hours_label),
                        value = "%.1f".format(state.totalHours),
                        unit = stringResource(R.string.hrs_unit),
                        icon = Icons.Outlined.Schedule,
                        iconColor = Primary
                    )
                    MetricCard(
                        modifier = Modifier.weight(1f),
                        label = stringResource(R.string.activities_label),
                        value = state.recentEntries.size.toString(),
                        unit = stringResource(R.string.logs_unit),
                        icon = Icons.Outlined.Assignment,
                        iconColor = Tertiary
                    )
                }
            }


            // ── Milestones ────────────────────────────────────────────────────
            item { Spacer(Modifier.height(16.dp)) }
            item {
                Text(
                    stringResource(R.string.milestones),
                    style = MaterialTheme.typography.titleMedium,
                    color = OnSurface,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 24.dp)
                )
                Spacer(Modifier.height(8.dp))
            }
            items(state.milestones) { milestone ->
                MilestoneItem(milestone = milestone, modifier = Modifier.padding(horizontal = 24.dp, vertical = 4.dp))
            }
        }

    }
}

@Composable
private fun ProgressRing(progress: Float, totalHours: Float, goalHours: Float) {
    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(1000, easing = EaseOutCubic),
        label = "progress"
    )
    Box(contentAlignment = Alignment.Center, modifier = Modifier.size(180.dp)) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val strokeWidth = 16.dp.toPx()
            val radius = (size.minDimension - strokeWidth) / 2
            val center = Offset(size.width / 2, size.height / 2)
            // Track
            drawCircle(color = SurfaceContainer, radius = radius, center = center, style = Stroke(strokeWidth))
            // Progress arc
            rotate(-90f, center) {
                drawArc(
                    brush = Brush.linearGradient(listOf(Primary, PrimaryContainer)),
                    startAngle = 0f,
                    sweepAngle = 360f * animatedProgress,
                    useCenter = false,
                    topLeft = Offset(center.x - radius, center.y - radius),
                    size = Size(radius * 2, radius * 2),
                    style = Stroke(strokeWidth, cap = StrokeCap.Round)
                )
            }
        }
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "${(progress * 100).toInt()}%",
                style = MaterialTheme.typography.headlineMedium,
                color = OnSurface,
                fontWeight = FontWeight.ExtraBold
            )
            Text(
                text = stringResource(R.string.meta_label),
                style = MaterialTheme.typography.labelSmall,
                color = OnSurfaceVariant,
                letterSpacing = 2.sp
            )
            Spacer(Modifier.height(2.dp))
            Text(
                text = stringResource(R.string.progress_format, totalHours, goalHours),
                style = MaterialTheme.typography.bodySmall,
                color = OnSurfaceVariant
            )
        }
    }
}

@Composable
private fun MetricCard(
    modifier: Modifier = Modifier,
    label: String,
    value: String,
    unit: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    iconColor: Color
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceContainerLowest),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Icon(imageVector = icon, contentDescription = null, tint = iconColor, modifier = Modifier.size(20.dp))
            Spacer(Modifier.height(8.dp))
            Text(value, style = MaterialTheme.typography.headlineSmall, color = OnSurface, fontWeight = FontWeight.ExtraBold)
            Text(label, style = MaterialTheme.typography.bodySmall, color = OnSurfaceVariant)
        }
    }
}

@Composable
private fun MilestoneItem(milestone: Milestone, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (milestone.isCompleted) PrimaryFixed.copy(alpha = 0.4f) else SurfaceContainerLow
        ),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Icon(
                imageVector = if (milestone.isCompleted) Icons.Outlined.CheckCircle else Icons.Outlined.RadioButtonUnchecked,
                contentDescription = null,
                tint = if (milestone.isCompleted) Primary else OutlineVariant,
                modifier = Modifier.size(22.dp)
            )
            Column(modifier = Modifier.weight(1f)) {
                Text(milestone.title, style = MaterialTheme.typography.titleSmall, color = OnSurface, fontWeight = FontWeight.Bold)
                Text(milestone.description, style = MaterialTheme.typography.bodySmall, color = OnSurfaceVariant)
            }
            Text(
                "%.0f h".format(milestone.targetHours),
                style = MaterialTheme.typography.labelMedium,
                color = if (milestone.isCompleted) Primary else OnSurfaceVariant,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
