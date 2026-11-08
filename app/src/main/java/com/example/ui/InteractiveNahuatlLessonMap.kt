package com.example.ui

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.DayStreakStatus
import com.example.data.LessonEntity
import kotlinx.coroutines.launch

/**
 * Interactive Lesson Map (Sendero Sagrado de Aprendizaje Náhuatl)
 * Provides a winding, progressive learning journey through Nahuatl lessons.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InteractiveNahuatlLessonMap(
    lessons: List<LessonEntity>,
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val progressState by viewModel.userProgress.collectAsState()
    val progress = progressState
    val currentLang = progress?.selectedLanguage ?: "Nahuatl"
    val completedLessonsSet = remember(progress, currentLang) {
        val rawStr = when (currentLang) {
            "Otomi" -> progress?.completedLessonsOtomi
            "Totona" -> progress?.completedLessonsTotona
            "Maya" -> progress?.completedLessonsMaya
            "Zapoteco" -> progress?.completedLessonsZapoteco
            "Mixteco" -> progress?.completedLessonsMixteco
            else -> progress?.completedLessonsNahuatl
        }.orEmpty()
        rawStr.split(";").mapNotNull { it.toIntOrNull() }.toSet()
    }

    val coroutineScope = rememberCoroutineScope()
    val listState = rememberLazyListState()

    var selectedLessonForDetail by remember { mutableStateOf<LessonEntity?>(null) }
    var categoryFilter by remember { mutableStateOf("Todos") }

    val filteredLessons = remember(lessons, categoryFilter) {
        if (categoryFilter == "Todos") lessons else lessons.filter { it.category == categoryFilter }
    }

    // Determine currently active lesson (first uncompleted unlocked lesson)
    val activeLessonIndex = remember(filteredLessons, completedLessonsSet) {
        val idx = filteredLessons.indexOfFirst { lesson ->
            val isCompleted = completedLessonsSet.contains(lesson.id)
            val isUnlocked = lesson.id == 1 || completedLessonsSet.contains(lesson.id - 1)
            isUnlocked && !isCompleted
        }
        if (idx != -1) idx else 0
    }

    // Unit descriptive themes
    val unitThemeTitle = remember(viewModel.selectedUnit) {
        when (viewModel.selectedUnit) {
            1 -> "Raíces y Saludos Sagrados (Tonalpohualli)"
            2 -> "Los Guardianes y el Reino Animal (Nahualli)"
            3 -> "El Fuego del Hogar y la Familia (Chantli)"
            4 -> "El Sistema Vigesimal y Comercio (Tianquiztli)"
            5 -> "Flor y Canto de los Dioses (In Xochitl In Cuicatl)"
            6 -> "La Medicina Tradicional y Herbolaria Sagrada"
            7 -> "Mitos y el Sol Cósmico de los Cuatro Vientos"
            else -> "Códice Sagrado • Sendero de Sabiduría"
        }
    }

    val totalLessonsInUnit = filteredLessons.size
    val completedCountInUnit = filteredLessons.count { completedLessonsSet.contains(it.id) }
    val unitProgressFraction = if (totalLessonsInUnit > 0) completedCountInUnit.toFloat() / totalLessonsInUnit else 0f

    val streakStats by viewModel.streakStats.collectAsState()

    // Pulsing aura for active current node
    val infiniteTransition = rememberInfiniteTransition(label = "active_node_pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1.0f,
        targetValue = 1.14f,
        animationSpec = infiniteRepeatable(
            animation = tween(900, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_scale"
    )

    // Pulsing flame animation for streak counter
    val flameTransition = rememberInfiniteTransition(label = "map_flame_pulse")
    val flameScale by flameTransition.animateFloat(
        initialValue = 0.94f,
        targetValue = 1.10f,
        animationSpec = infiniteRepeatable(
            animation = tween(850, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "flame_scale"
    )

    Box(modifier = modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {

            // --- 1. UNIT PROGRESS HEADER ---
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 4.dp)
                    .testTag("lesson_map_header_card"),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1A1E26)),
                border = BorderStroke(1.dp, Color(0x33FFB74D))
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "SENDERO SAGRADO NÁHUATL",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Black,
                                    color = Color(0xFFFFB74D),
                                    letterSpacing = 1.2.sp
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("🗺️", fontSize = 12.sp)
                            }
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "Unidad ${viewModel.selectedUnit}: $unitThemeTitle",
                                fontSize = 13.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }

                        // Badges Row: Streak + Progress Pill
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            // Streak Pill
                            Surface(
                                onClick = { viewModel.showDailyStreakDialog = true },
                                shape = RoundedCornerShape(12.dp),
                                color = Color(0x33FF9800),
                                border = BorderStroke(1.dp, Color(0xFFFF9800)),
                                modifier = Modifier.testTag("map_header_streak_pill")
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Whatshot,
                                        contentDescription = "Racha Diaria",
                                        tint = Color(0xFFFFD54F),
                                        modifier = Modifier.size(13.dp)
                                    )
                                    Spacer(modifier = Modifier.width(3.dp))
                                    Text(
                                        text = "${streakStats.currentStreak}d",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                }
                            }

                            // Progress Pill
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = Color(0x332E7D32),
                                border = BorderStroke(1.dp, Color(0xFF4CAF50))
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.CheckCircle,
                                        contentDescription = null,
                                        tint = Color(0xFF4CAF50),
                                        modifier = Modifier.size(13.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "$completedCountInUnit/$totalLessonsInUnit",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF81C784)
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Progress bar
                    LinearProgressIndicator(
                        progress = { unitProgressFraction },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(5.dp)
                            .clip(RoundedCornerShape(3.dp)),
                        color = Color(0xFFFFB74D),
                        trackColor = Color(0x33FFFFFF)
                    )
                }
            }

            // --- 2. CONTADOR DE RACHA DIARIA (ROOM DATABASE PERSISTENCE) ---
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 4.dp)
                    .clickable { viewModel.showDailyStreakDialog = true }
                    .testTag("lesson_map_daily_streak_card"),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1E222B)),
                border = BorderStroke(1.5.dp, Color(0xFFFF9800).copy(alpha = 0.85f))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.horizontalGradient(
                                listOf(Color(0x33FF9800), Color(0x15FF9800), Color(0x05000000))
                            )
                        )
                        .padding(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(42.dp)
                                    .scale(flameScale)
                                    .clip(CircleShape)
                                    .background(
                                        Brush.radialGradient(
                                            listOf(Color(0xFFFFB74D), Color(0xFFFF9800), Color(0xFFE65100))
                                        )
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Whatshot,
                                    contentDescription = "Racha Diaria",
                                    tint = Color.White,
                                    modifier = Modifier.size(24.dp)
                                )
                            }

                            Spacer(modifier = Modifier.width(10.dp))

                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = "${streakStats.currentStreak}",
                                        fontSize = 20.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = Color(0xFFFFD54F)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = if (streakStats.currentStreak == 1) "Día de Racha" else "Días de Racha",
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                }
                                Text(
                                    text = if (streakStats.hasLoggedInToday) "✓ Guardado en Room SQLite • Hoy activo" else "⚠️ Registro de hoy pendiente en Room",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = if (streakStats.hasLoggedInToday) Color(0xFF81C784) else Color(0xFFFFB74D)
                                )
                            }
                        }

                        // Action / status button
                        if (!streakStats.hasLoggedInToday) {
                            Button(
                                onClick = {
                                    viewModel.performDailyCheckIn()
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFFFF9800),
                                    contentColor = Color(0xFF1A120B)
                                ),
                                shape = RoundedCornerShape(10.dp),
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                                modifier = Modifier.testTag("map_quick_check_in_btn")
                            ) {
                                Text("🔥 Registrar (+20 XP)", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        } else {
                            Surface(
                                color = Color(0x334CAF50),
                                shape = RoundedCornerShape(10.dp),
                                border = BorderStroke(1.dp, Color(0xFF4CAF50))
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text("Activa", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFF81C784))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Icon(Icons.Default.Check, contentDescription = null, tint = Color(0xFF81C784), modifier = Modifier.size(12.dp))
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // 7-Days Calendar Streak Strip directly on the lesson map!
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        streakStats.recent7DaysStatus.forEach { day ->
                            MapMiniDayPill(day = day)
                        }
                    }
                }
            }


            // --- 2. CATEGORY FILTERS CHIPS ---
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                val categories = listOf("Todos", "Vocabulario", "Frases", "Conversación", "Cultura", "Gramática")
                categories.forEach { cat ->
                    val isSelected = categoryFilter == cat
                    FilterChip(
                        selected = isSelected,
                        onClick = { categoryFilter = cat },
                        label = {
                            val icon = when (cat) {
                                "Vocabulario" -> "📖"
                                "Frases" -> "💬"
                                "Conversación" -> "🗣️"
                                "Cultura" -> "🪶"
                                "Gramática" -> "📐"
                                else -> "✨"
                            }
                            Text("$icon $cat", fontSize = 10.sp, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal)
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = Color(0x40FFB74D),
                            selectedLabelColor = Color(0xFFFFB74D),
                            containerColor = Color(0x1AFFFFFF),
                            labelColor = Color.LightGray
                        ),
                        border = FilterChipDefaults.filterChipBorder(
                            enabled = true,
                            selected = isSelected,
                            borderColor = if (isSelected) Color(0xFFFFB74D) else Color(0x26FFFFFF)
                        )
                    )
                }
            }

            // --- 3. WINDING ROADMAP / LESSON MAP CANVAS & LIST ---
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                if (filteredLessons.isEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No hay lecciones para la categoría seleccionada.",
                            color = Color.Gray,
                            fontSize = 12.sp
                        )
                    }
                } else {
                    LazyColumn(
                        state = listState,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 12.dp),
                        contentPadding = PaddingValues(top = 16.dp, bottom = 80.dp),
                        verticalArrangement = Arrangement.spacedBy(20.dp)
                    ) {
                        itemsIndexed(filteredLessons) { index, lesson ->
                            val isCompleted = completedLessonsSet.contains(lesson.id)
                            val isUnlocked = lesson.id == 1 || completedLessonsSet.contains(lesson.id - 1)
                            val isCurrentActive = index == activeLessonIndex && isUnlocked && !isCompleted
                            val isCheckpoint = (index + 1) % 5 == 0

                            // Horizontal curve bias: alternates in an S-curve (0.5 -> 0.78 -> 0.5 -> 0.22)
                            val horizontalBias = when (index % 4) {
                                0 -> 0.50f
                                1 -> 0.75f
                                2 -> 0.50f
                                3 -> 0.25f
                                else -> 0.50f
                            }

                            LessonMapNodeRow(
                                lesson = lesson,
                                index = index,
                                isCompleted = isCompleted,
                                isUnlocked = isUnlocked,
                                isCurrentActive = isCurrentActive,
                                isCheckpoint = isCheckpoint,
                                horizontalBias = horizontalBias,
                                pulseScale = if (isCurrentActive) pulseScale else 1.0f,
                                onClick = {
                                    selectedLessonForDetail = lesson
                                }
                            )
                        }
                    }
                }

                // FAB: Scroll to Current Active Lesson
                FloatingActionButton(
                    onClick = {
                        coroutineScope.launch {
                            listState.animateScrollToItem(activeLessonIndex.coerceAtLeast(0))
                        }
                    },
                    containerColor = Color(0xFFFFB74D),
                    contentColor = Color(0xFF12141A),
                    shape = CircleShape,
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(16.dp)
                        .size(48.dp)
                        .testTag("scroll_to_current_lesson_fab")
                ) {
                    Icon(
                        imageVector = Icons.Default.MyLocation,
                        contentDescription = "Ir a mi lección actual",
                        modifier = Modifier.size(22.dp)
                    )
                }
            }
        }

        // --- 4. INTERACTIVE LESSON PREVIEW & DETAIL BOTTOM SHEET ---
        selectedLessonForDetail?.let { lesson ->
            val isCompleted = completedLessonsSet.contains(lesson.id)
            val isUnlocked = lesson.id == 1 || completedLessonsSet.contains(lesson.id - 1)

            LessonDetailModalSheet(
                lesson = lesson,
                isCompleted = isCompleted,
                isUnlocked = isUnlocked,
                viewModel = viewModel,
                onDismiss = { selectedLessonForDetail = null },
                onStartLesson = {
                    selectedLessonForDetail = null
                    viewModel.openLesson(lesson)
                }
            )
        }
    }
}

/**
 * Individual Node Row along the winding progressive path
 */
@Composable
private fun LessonMapNodeRow(
    lesson: LessonEntity,
    index: Int,
    isCompleted: Boolean,
    isUnlocked: Boolean,
    isCurrentActive: Boolean,
    isCheckpoint: Boolean,
    horizontalBias: Float,
    pulseScale: Float,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        contentAlignment = Alignment.Center
    ) {
        // Decorative Path Line Indicator
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(40.dp)
        ) {
            val stepX = size.width * horizontalBias
            // Subtle glowing dot trail
            drawCircle(
                color = if (isCompleted) Color(0x664CAF50) else Color(0x33FFB74D),
                radius = 3f,
                center = Offset(stepX, size.height / 2)
            )
        }

        // Horizontal positioning container using BiasAlignment
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentSize(
                    align = androidx.compose.ui.BiasAlignment(
                        horizontalBias = (horizontalBias * 2) - 1.0f,
                        verticalBias = 0f
                    )
                )
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .clickable(enabled = true, onClick = onClick)
                    .testTag("lesson_node_${lesson.id}")
            ) {
                // Floating Active Indicator Flag
                if (isCurrentActive) {
                    Surface(
                        color = Color(0xFFFFB74D),
                        shape = RoundedCornerShape(8.dp),
                        shadowElevation = 6.dp,
                        modifier = Modifier
                            .padding(bottom = 6.dp)
                            .scale(pulseScale)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("⚡", fontSize = 10.sp)
                            Spacer(modifier = Modifier.width(3.dp))
                            Text(
                                text = "¡AQUÍ VAS!",
                                color = Color(0xFF1A120B),
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Black
                            )
                        }
                    }
                }

                // Checkpoint Banner if Milestone
                if (isCheckpoint) {
                    Text(
                        text = "🏛️ HITO DE SABIDURÍA",
                        color = Color(0xFFFFD54F),
                        fontSize = 8.sp,
                        fontWeight = FontWeight.ExtraBold,
                        modifier = Modifier.padding(bottom = 2.dp)
                    )
                }

                // Sacred Node Circle
                Box(
                    modifier = Modifier
                        .size(if (isCheckpoint) 70.dp else 62.dp)
                        .scale(if (isCurrentActive) pulseScale else 1.0f)
                        .shadow(
                            elevation = if (isCurrentActive) 12.dp else if (isCompleted) 6.dp else 2.dp,
                            shape = CircleShape,
                            ambientColor = if (isCompleted) Color(0xFF4CAF50) else Color(0xFFFFB74D)
                        )
                        .clip(CircleShape)
                        .background(
                            when {
                                isCompleted -> Brush.radialGradient(
                                    listOf(Color(0xFF66BB6A), Color(0xFF2E7D32), Color(0xFF1B5E20))
                                )
                                isCurrentActive -> Brush.radialGradient(
                                    listOf(Color(0xFFFFD54F), Color(0xFFFFB74D), Color(0xFFE65100))
                                )
                                isUnlocked -> Brush.radialGradient(
                                    listOf(Color(0xFF42A5F5), Color(0xFF1976D2), Color(0xFF0D47A1))
                                )
                                else -> Brush.radialGradient(
                                    listOf(Color(0xFF37474F), Color(0xFF263238), Color(0xFF102027))
                                )
                            }
                        )
                        .border(
                            width = if (isCurrentActive) 3.dp else 2.dp,
                            color = when {
                                isCurrentActive -> Color.White
                                isCompleted -> Color(0xFF81C784)
                                isUnlocked -> Color(0xFF90CAF9)
                                else -> Color(0x33FFFFFF)
                            },
                            shape = CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    when {
                        isCompleted -> {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = "Completada",
                                    tint = Color.White,
                                    modifier = Modifier.size(24.dp)
                                )
                                Text(
                                    text = "⭐⭐⭐",
                                    fontSize = 7.sp
                                )
                            }
                        }
                        isCurrentActive -> {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(
                                    imageVector = Icons.Default.PlayArrow,
                                    contentDescription = "En curso",
                                    tint = Color(0xFF1A120B),
                                    modifier = Modifier.size(26.dp)
                                )
                                Text(
                                    text = "L${lesson.id}",
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Black,
                                    color = Color(0xFF1A120B)
                                )
                            }
                        }
                        isUnlocked -> {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                val catEmoji = when (lesson.category) {
                                    "Vocabulario" -> "📖"
                                    "Frases" -> "💬"
                                    "Conversación" -> "🗣️"
                                    "Cultura" -> "🪶"
                                    else -> "✨"
                                }
                                Text(catEmoji, fontSize = 16.sp)
                                Text(
                                    text = "L${lesson.id}",
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                        }
                        else -> {
                            Icon(
                                imageVector = Icons.Default.Lock,
                                contentDescription = "Bloqueada",
                                tint = Color.LightGray.copy(alpha = 0.5f),
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                // Short Title Label Under Node
                Surface(
                    color = Color(0xCC1A1E26),
                    shape = RoundedCornerShape(6.dp),
                    border = BorderStroke(1.dp, Color(0x26FFFFFF))
                ) {
                    Text(
                        text = "Lección ${lesson.id}",
                        color = if (isUnlocked) Color.White else Color.Gray,
                        fontSize = 10.sp,
                        fontWeight = if (isCurrentActive) FontWeight.Bold else FontWeight.Medium,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }
        }
    }
}

/**
 * Modal Bottom Sheet for inspecting and launching an interactive lesson
 */
@Composable
private fun LessonDetailModalSheet(
    lesson: LessonEntity,
    isCompleted: Boolean,
    isUnlocked: Boolean,
    viewModel: MainViewModel,
    onDismiss: () -> Unit,
    onStartLesson: () -> Unit
) {
    val phrases = remember(lesson.nahuatlPhrases) {
        lesson.nahuatlPhrases.split(";").map { it.trim() }.filter { it.isNotEmpty() }
    }
    val translations = remember(lesson.spanishTranslations) {
        lesson.spanishTranslations.split(";").map { it.trim() }.filter { it.isNotEmpty() }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        properties = androidx.compose.ui.window.DialogProperties(usePlatformDefaultWidth = false),
        modifier = Modifier
            .fillMaxWidth(0.92f)
            .clip(RoundedCornerShape(24.dp))
            .border(2.dp, Color(0xFFFFB74D), RoundedCornerShape(24.dp))
            .testTag("lesson_detail_modal"),
        containerColor = Color(0xFF161920),
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(
                                if (isCompleted) Color(0x334CAF50)
                                else if (isUnlocked) Color(0x33FFB74D)
                                else Color(0x339E9E9E)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (isCompleted) "⭐" else if (isUnlocked) "📜" else "🔒",
                            fontSize = 18.sp
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "LECCIÓN ${lesson.id} • ${lesson.category.uppercase()}",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Black,
                            color = Color(0xFFFFB74D),
                            letterSpacing = 1.sp
                        )
                        Text(
                            text = lesson.title,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }

                IconButton(onClick = onDismiss) {
                    Icon(Icons.Default.Close, contentDescription = "Cerrar", tint = Color.LightGray)
                }
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Explanation paragraph
                Text(
                    text = lesson.explanation,
                    fontSize = 12.5.sp,
                    color = Color(0xFFECEFF1),
                    lineHeight = 18.sp
                )

                // Cultural curiosity badge if available
                if (lesson.didYouKnow.isNotBlank()) {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF222834)),
                        shape = RoundedCornerShape(10.dp),
                        border = BorderStroke(1.dp, Color(0x334FC3F7))
                    ) {
                        Row(
                            modifier = Modifier.padding(10.dp),
                            verticalAlignment = Alignment.Top
                        ) {
                            Text("💡", fontSize = 14.sp)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = lesson.didYouKnow,
                                fontSize = 11.sp,
                                color = Color(0xFFB0BEC5),
                                lineHeight = 15.sp
                            )
                        }
                    }
                }

                // Sample vocabulary / phrases preview with audio speaker
                if (phrases.isNotEmpty()) {
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text(
                            text = "Voces de esta lección:",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFFFD54F)
                        )

                        phrases.take(3).forEachIndexed { i, phrase ->
                            val trans = translations.getOrNull(i) ?: ""
                            Surface(
                                color = Color(0xFF1E232E),
                                shape = RoundedCornerShape(8.dp),
                                border = BorderStroke(1.dp, Color(0x1AFFFFFF)),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 10.dp, vertical = 6.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = phrase,
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White
                                        )
                                        if (trans.isNotBlank()) {
                                            Text(
                                                text = trans,
                                                fontSize = 11.sp,
                                                color = Color.LightGray
                                            )
                                        }
                                    }

                                    IconButton(
                                        onClick = { viewModel.speak(phrase) },
                                        modifier = Modifier.size(30.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.VolumeUp,
                                            contentDescription = "Escuchar pronunciación",
                                            tint = Color(0xFFFFB74D),
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // Difficulty and Reward row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        color = Color(0x33FFB74D),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text(
                            text = "Dificultad: ${lesson.difficulty}",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFFFB74D),
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }

                    Surface(
                        color = Color(0x332E7D32),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text(
                            text = "Recompensa: +25 XP",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF81C784),
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }
                }
            }
        },
        confirmButton = {
            if (isUnlocked) {
                Button(
                    onClick = onStartLesson,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isCompleted) Color(0xFF2E7D32) else Color(0xFFFFB74D),
                        contentColor = if (isCompleted) Color.White else Color(0xFF1A120B)
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("start_lesson_cta_button")
                ) {
                    Icon(
                        imageVector = if (isCompleted) Icons.Default.Refresh else Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = null
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (isCompleted) "Repasar Lección" else "Comenzar Lección",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            } else {
                Surface(
                    color = Color(0xFF263238),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(Icons.Default.Lock, contentDescription = null, tint = Color.LightGray, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Completa la lección anterior para desbloquear",
                            fontSize = 11.sp,
                            color = Color.LightGray,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }
    )
}

@Composable
private fun MapMiniDayPill(day: DayStreakStatus) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = day.dayOfWeekLabel,
            fontSize = 9.sp,
            fontWeight = if (day.isToday) FontWeight.ExtraBold else FontWeight.Medium,
            color = if (day.isToday) Color(0xFFFFB74D) else Color.Gray
        )
        Spacer(modifier = Modifier.height(3.dp))
        Box(
            modifier = Modifier
                .size(26.dp)
                .clip(CircleShape)
                .background(
                    when {
                        day.isCompleted -> Color(0xFFFF9800)
                        day.isToday -> Color(0x33FF9800)
                        else -> Color(0xFF263238)
                    }
                )
                .border(
                    width = if (day.isToday) 1.5.dp else 1.dp,
                    color = when {
                        day.isToday -> Color(0xFFFFD54F)
                        day.isCompleted -> Color(0xFFFFB74D)
                        else -> Color(0x1AFFFFFF)
                    },
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            if (day.isCompleted) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(14.dp)
                )
            } else {
                Text(
                    text = "${day.dayOfMonth}",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (day.isToday) Color(0xFFFFB74D) else Color.LightGray
                )
            }
        }
    }
}

