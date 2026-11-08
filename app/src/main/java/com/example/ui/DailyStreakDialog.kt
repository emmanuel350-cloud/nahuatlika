package com.example.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.DailyInteractionEntity
import com.example.data.DailyStreakStats
import com.example.data.DayStreakStatus

@Composable
fun DailyStreakDialog(
    viewModel: MainViewModel,
    onDismiss: () -> Unit
) {
    val streakStats by viewModel.streakStats.collectAsState()
    val interactions by viewModel.dailyInteractions.collectAsState()
    var showSimulators by remember { mutableStateOf(false) }
    var showHistory by remember { mutableStateOf(false) }

    // Pulsing flame animation for active streaks
    val infiniteTransition = rememberInfiniteTransition(label = "flame_pulse")
    val flameScale by infiniteTransition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.08f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "flame_scale"
    )

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.94f)
                .fillMaxHeight(0.90f)
                .clip(RoundedCornerShape(24.dp))
                .border(2.dp, Color(0xFFFF9800), RoundedCornerShape(24.dp))
                .testTag("daily_streak_dialog"),
            color = Color(0xFF12141A)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp)
            ) {
                // Top Header Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(38.dp)
                                .clip(CircleShape)
                                .background(Color(0x33FF9800)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Whatshot,
                                contentDescription = "Racha",
                                tint = Color(0xFFFF9800),
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "RACHA ANCESTRAL",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFFFB74D),
                                letterSpacing = 1.5.sp
                            )
                            Text(
                                text = "Control de Días Consecutivos",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.testTag("close_streak_dialog")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Cerrar",
                            tint = Color.LightGray
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // --- 1. HERO STREAK CARD ---
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(20.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = Color(0xFF1E222B)
                            ),
                            border = BorderStroke(1.dp, Color(0x4DFF9800))
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(
                                        Brush.verticalGradient(
                                            listOf(Color(0x40FF9800), Color(0x00FF9800))
                                        )
                                    )
                                    .padding(20.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(80.dp)
                                        .scale(flameScale)
                                        .clip(CircleShape)
                                        .background(
                                            Brush.radialGradient(
                                                listOf(Color(0xFFFF9800), Color(0xFFD84315), Color(0x00FF5722))
                                            )
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Whatshot,
                                        contentDescription = "Fuego de la Racha",
                                        tint = Color.White,
                                        modifier = Modifier.size(46.dp)
                                    )
                                }

                                Spacer(modifier = Modifier.height(10.dp))

                                Text(
                                    text = "${streakStats.currentStreak}",
                                    fontSize = 42.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Color(0xFFFFD54F)
                                )

                                Text(
                                    text = if (streakStats.currentStreak == 1) "Día Consecutivo" else "Días Consecutivos",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )

                                Spacer(modifier = Modifier.height(6.dp))

                                val statusPillBg = if (streakStats.hasLoggedInToday) Color(0x264CAF50) else Color(0x26FF9800)
                                val statusPillColor = if (streakStats.hasLoggedInToday) Color(0xFF81C784) else Color(0xFFFFB74D)
                                val statusText = if (streakStats.hasLoggedInToday) {
                                    "✨ ¡Racha de hoy asegurada en Room DB!"
                                } else {
                                    "⚠️ Aún no has registrado tu interacción de hoy"
                                }

                                Surface(
                                    color = statusPillBg,
                                    shape = RoundedCornerShape(12.dp),
                                    border = BorderStroke(1.dp, statusPillColor.copy(alpha = 0.5f))
                                ) {
                                    Text(
                                        text = statusText,
                                        color = statusPillColor,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Medium,
                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                        textAlign = TextAlign.Center
                                    )
                                }
                            }
                        }
                    }

                    // --- 2. 7-DAYS CALENDAR STRIP ---
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF181C24)),
                            border = BorderStroke(1.dp, Color(0x26FFFFFF))
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Text(
                                    text = "📅 Registro de los Últimos 7 Días",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                Spacer(modifier = Modifier.height(12.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    streakStats.recent7DaysStatus.forEach { day ->
                                        DayCalendarPill(day = day)
                                    }
                                }
                            }
                        }
                    }

                    // --- 3. KEY METRICS GRID ---
                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            // Longest Streak
                            MetricBox(
                                title = "Racha Máxima",
                                value = "${streakStats.longestStreak} días",
                                icon = Icons.Default.EmojiEvents,
                                accentColor = Color(0xFFFFD54F),
                                modifier = Modifier.weight(1f)
                            )
                            // Total Active Days
                            MetricBox(
                                title = "Días Totales",
                                value = "${streakStats.totalActiveDays} días",
                                icon = Icons.Default.CalendarMonth,
                                accentColor = Color(0xFF4FC3F7),
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }

                    // Database Info Bar
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF1A212E)),
                            border = BorderStroke(1.dp, Color(0x3364B5F6))
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Storage,
                                    contentDescription = "Room Database",
                                    tint = Color(0xFF64B5F6),
                                    modifier = Modifier.size(22.dp)
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = "Persistencia: Room Database (SQLite)",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                    Text(
                                        text = "Tabla: daily_interactions (${interactions.size} registros guardados)",
                                        fontSize = 11.sp,
                                        color = Color.LightGray
                                    )
                                }
                            }
                        }
                    }

                    // --- 4. ACTION BUTTON: CHECK-IN / INTERACTION ---
                    item {
                        Button(
                            onClick = {
                                viewModel.performDailyCheckIn()
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (streakStats.hasLoggedInToday) Color(0xFF2E7D32) else Color(0xFFFF9800),
                                contentColor = Color.White
                            ),
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                                .testTag("record_interaction_button")
                        ) {
                            Icon(
                                imageVector = if (streakStats.hasLoggedInToday) Icons.Default.CheckCircle else Icons.Default.LocalFireDepartment,
                                contentDescription = null
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (streakStats.hasLoggedInToday) {
                                    "✓ Interacción Registrada Hoy (+20 XP)"
                                } else {
                                    "🔥 Registrar Interacción de Hoy (+20 XP)"
                                },
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    // --- 5. INTERACTIVE TESTING & SIMULATOR ACCORDION ---
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF151820)),
                            border = BorderStroke(1.dp, Color(0x26AB47BC))
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable { showSimulators = !showSimulators },
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Default.Science,
                                            contentDescription = null,
                                            tint = Color(0xFFBA68C8),
                                            modifier = Modifier.size(20.dp)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = "🧪 Herramientas de Prueba de Rachas",
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFFE1BEE7)
                                        )
                                    }
                                    Icon(
                                        imageVector = if (showSimulators) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                                        contentDescription = null,
                                        tint = Color.LightGray
                                    )
                                }

                                AnimatedVisibility(visible = showSimulators) {
                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(top = 10.dp),
                                        verticalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Text(
                                            text = "Permite probar el cálculo de días consecutivos en Room añadiendo registros con fechas pasadas:",
                                            fontSize = 11.sp,
                                            color = Color.Gray
                                        )

                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                                        ) {
                                            OutlinedButton(
                                                onClick = { viewModel.simulatePastInteraction(1) },
                                                modifier = Modifier.weight(1f).testTag("simulate_yesterday_button"),
                                                shape = RoundedCornerShape(8.dp)
                                            ) {
                                                Text("Ayer (-1d)", fontSize = 11.sp)
                                            }
                                            OutlinedButton(
                                                onClick = { viewModel.simulatePastInteraction(2) },
                                                modifier = Modifier.weight(1f).testTag("simulate_2days_ago_button"),
                                                shape = RoundedCornerShape(8.dp)
                                            ) {
                                                Text("Anteayer (-2d)", fontSize = 11.sp)
                                            }
                                            OutlinedButton(
                                                onClick = { viewModel.simulatePastInteraction(3) },
                                                modifier = Modifier.weight(1f).testTag("simulate_3days_ago_button"),
                                                shape = RoundedCornerShape(8.dp)
                                            ) {
                                                Text("Hace 3d (-3d)", fontSize = 11.sp)
                                            }
                                        }

                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.End
                                        ) {
                                            TextButton(
                                                onClick = { viewModel.refreshDailyStreak() },
                                                modifier = Modifier.testTag("refresh_streak_calculation_button")
                                            ) {
                                                Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(16.dp))
                                                Spacer(modifier = Modifier.width(4.dp))
                                                Text("Recalcular Racha", fontSize = 12.sp)
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // --- 6. ROOM DB LOG ENTRY HISTORY ---
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF151820)),
                            border = BorderStroke(1.dp, Color(0x26FFFFFF))
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable { showHistory = !showHistory },
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Default.History,
                                            contentDescription = null,
                                            tint = Color(0xFF4FC3F7),
                                            modifier = Modifier.size(20.dp)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = "📜 Historial de Inicios Guardados (${interactions.size})",
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White
                                        )
                                    }
                                    Icon(
                                        imageVector = if (showHistory) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                                        contentDescription = null,
                                        tint = Color.LightGray
                                    )
                                }

                                AnimatedVisibility(visible = showHistory) {
                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(top = 10.dp),
                                        verticalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        if (interactions.isEmpty()) {
                                            Text(
                                                text = "No hay registros aún en la base de datos.",
                                                fontSize = 11.sp,
                                                color = Color.Gray,
                                                modifier = Modifier.padding(vertical = 8.dp)
                                            )
                                        } else {
                                            interactions.forEach { record ->
                                                InteractionRecordRow(
                                                    record = record,
                                                    onDelete = { viewModel.deleteDailyInteraction(record.id) }
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Bottom Close Button
                Button(
                    onClick = onDismiss,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF263238),
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Cerrar", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
private fun DayCalendarPill(day: DayStreakStatus) {
    val isCompleted = day.isCompleted
    val isToday = day.isToday

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = day.dayOfWeekLabel,
            fontSize = 10.sp,
            fontWeight = if (isToday) FontWeight.ExtraBold else FontWeight.Medium,
            color = if (isToday) Color(0xFFFFB74D) else Color.Gray
        )
        Spacer(modifier = Modifier.height(4.dp))
        Box(
            modifier = Modifier
                .size(34.dp)
                .clip(CircleShape)
                .background(
                    when {
                        isCompleted -> Color(0xFFFF9800)
                        isToday -> Color(0x33FF9800)
                        else -> Color(0xFF263238)
                    }
                )
                .border(
                    width = if (isToday) 2.dp else 1.dp,
                    color = when {
                        isToday -> Color(0xFFFFD54F)
                        isCompleted -> Color(0xFFFFB74D)
                        else -> Color(0x1AFFFFFF)
                    },
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            if (isCompleted) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(18.dp)
                )
            } else {
                Text(
                    text = "${day.dayOfMonth}",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isToday) Color(0xFFFFB74D) else Color.LightGray
                )
            }
        }
    }
}

@Composable
private fun MetricBox(
    title: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    accentColor: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF181C24)),
        border = BorderStroke(1.dp, accentColor.copy(alpha = 0.3f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(34.dp)
                    .clip(CircleShape)
                    .background(accentColor.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = accentColor,
                    modifier = Modifier.size(18.dp)
                )
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column {
                Text(text = title, fontSize = 10.sp, color = Color.Gray)
                Text(text = value, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color.White)
            }
        }
    }
}

@Composable
private fun InteractionRecordRow(
    record: DailyInteractionEntity,
    onDelete: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = Color(0xFF1E222B),
        shape = RoundedCornerShape(8.dp),
        border = BorderStroke(1.dp, Color(0x1AFFFFFF))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = record.loginDate,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFFFD54F)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Surface(
                        color = Color(0x264FC3F7),
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Text(
                            text = record.interactionType,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF4FC3F7),
                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                        )
                    }
                }
                Text(
                    text = "ID: #${record.id} • +${record.xpEarned} XP",
                    fontSize = 10.sp,
                    color = Color.Gray
                )
            }

            IconButton(
                onClick = onDelete,
                modifier = Modifier.size(28.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.DeleteOutline,
                    contentDescription = "Eliminar",
                    tint = Color(0xFFEF5350),
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}
