package com.example.ui

import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AiOracleScreen(viewModel: MainViewModel) {
    val context = LocalContext.current
    val progress by viewModel.userProgress.collectAsState()
    val activeLanguage = progress?.selectedLanguage ?: "Nahuatl"
    
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()
    val keyboardController = LocalSoftwareKeyboardController.current

    // Initialize initial oracle greeting
    LaunchedEffect(activeLanguage) {
        viewModel.initOracleChatIfNeeded()
    }

    // Auto scroll to last message on history update or when oracle is thinking
    LaunchedEffect(viewModel.oracleChatHistory.size, viewModel.isOracleThinking) {
        if (viewModel.oracleChatHistory.isNotEmpty()) {
            delay(100)
            listState.animateScrollToItem(viewModel.oracleChatHistory.size)
        }
    }

    // Dynamic styles and themes based on culture
    val backgroundBrush = when (activeLanguage) {
        "Totona" -> {
            Brush.verticalGradient(
                colors = listOf(
                    Color(0xFF0C2413), // Deep forest green
                    Color(0xFF1E3C25), // Dark jungle emerald
                    Color(0xFF0F1711)  // Near-black jade
                )
            )
        }
        else -> { // Nahuatl
            Brush.verticalGradient(
                colors = listOf(
                    Color(0xFF1E0E08), // Dark obsidian volcanic brown
                    Color(0xFF2C140A), // Warm temple hearth brown
                    Color(0xFF110805)  // Void obsidian black
                )
            )
        }
    }

    val primaryColor = when (activeLanguage) {
        "Totona" -> Color(0xFFFFD54F) // Vanilla Gold
        else -> Color(0xFFD4A337)      // Aztec Gold
    }

    val accentColor = when (activeLanguage) {
        "Totona" -> Color(0xFF81C784) // Emerald Green
        else -> Color(0xFFC85A3F)      // Terracotta Red
    }

    val topTitle = when (activeLanguage) {
        "Totona" -> "ORÁCULO TOTONACO 🕊️"
        else -> "EL ORÁCULO SAGRADO 🛕"
    }

    val nahuatlVariant = progress?.selectedNahuatlVariant ?: "Náhuatl de la Huasteca veracruzana"
    val totonaVariant = progress?.selectedTotonaVariant ?: "Totonaco de la costa"

    val topSubtitle = when (activeLanguage) {
        "Totona" -> "Abuelo de Papantla • $totonaVariant"
        else -> "Sacerdote del Anáhuac • $nahuatlVariant"
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .testTag("oracle_screen_container"),
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = topTitle,
                            fontSize = 19.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 1.sp,
                            color = primaryColor
                        )
                        Text(
                            text = topSubtitle,
                            fontSize = 12.sp,
                            color = Color.LightGray.copy(alpha = 0.7f)
                        )
                    }
                },
                navigationIcon = {
                    IconButton(
                        onClick = { viewModel.showAiOracleChat = false },
                        modifier = Modifier.testTag("oracle_back_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Volver",
                            tint = Color.White
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = { viewModel.showXttsConfigDialog = true },
                        modifier = Modifier.testTag("oracle_xtts_settings_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "Configurar Voz de Víctor (XTTS)",
                            tint = primaryColor
                        )
                    }
                    IconButton(
                        onClick = {
                            viewModel.clearOracleChat()
                            Toast.makeText(context, "El oráculo ha limpiado el humo del copal.", Toast.LENGTH_SHORT).show()
                        },
                        modifier = Modifier.testTag("oracle_clear_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Limpiar Conversación",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Black.copy(alpha = 0.5f),
                    titleContentColor = Color.White
                )
            )
        },
        containerColor = Color.Transparent
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(backgroundBrush)
                .padding(innerPadding)
        ) {
            // Background decorative glow or ancient grid pattern
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .alpha(0.04f)
                    .background(
                        Brush.radialGradient(
                            colors = listOf(primaryColor, Color.Transparent),
                            radius = 600f
                        )
                    )
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 14.dp)
            ) {
                // Majestic Oracle Avatar & Pulsing Aura Row
                OracleAvatarHeader(activeLanguage = activeLanguage, isThinking = viewModel.isOracleThinking, accentColor = accentColor)

                // Conversation body
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                ) {
                    if (viewModel.oracleChatHistory.isEmpty()) {
                        // Empty/Loading fallback
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(color = primaryColor)
                        }
                    } else {
                        LazyColumn(
                            state = listState,
                            modifier = Modifier
                                .fillMaxSize()
                                .testTag("oracle_chat_messages_list"),
                            contentPadding = PaddingValues(top = 8.dp, bottom = 100.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(viewModel.oracleChatHistory, key = { it.id }) { message ->
                                OracleBubbleItem(
                                    message = message,
                                    activeLanguage = activeLanguage,
                                    primaryColor = primaryColor,
                                    accentColor = accentColor,
                                    viewModel = viewModel
                                )
                            }

                            if (viewModel.isOracleThinking) {
                                item {
                                    OracleThinkingBubble(
                                        thinkingText = viewModel.oracleCurrentThinkingText,
                                        primaryColor = primaryColor
                                    )
                                }
                            }
                        }
                    }
                }

                // Quick / Suggested Sacred Questions List (Floating just above the input)
                SuggestedQuestionsSection(
                    activeLanguage = activeLanguage,
                    accentColor = accentColor,
                    onQuestionSelected = { question ->
                        viewModel.oracleUserQuery = question
                        viewModel.sendOracleQuery()
                        keyboardController?.hide()
                    }
                )

                // Input Controls Section at the absolute bottom
                OracleInputSection(
                    query = viewModel.oracleUserQuery,
                    onQueryChange = { viewModel.oracleUserQuery = it },
                    isThinking = viewModel.isOracleThinking,
                    primaryColor = primaryColor,
                    accentColor = accentColor,
                    onSendClicked = {
                        viewModel.sendOracleQuery()
                        keyboardController?.hide()
                    }
                )
                
                Spacer(modifier = Modifier.height(12.dp))
            }
        }
    }
}

@Composable
fun OracleAvatarHeader(
    activeLanguage: String,
    isThinking: Boolean,
    accentColor: Color
) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val auraScale by infiniteTransition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = EaseInOutQuad),
            repeatMode = RepeatMode.Reverse
        ),
        label = "aura_scale"
    )
    val auraAlpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.7f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = EaseInOutQuad),
            repeatMode = RepeatMode.Reverse
        ),
        label = "aura_alpha"
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp)
            .shadow(4.dp, RoundedCornerShape(16.dp)),
        colors = CardDefaults.cardColors(containerColor = Color.Black.copy(alpha = 0.4f)),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, accentColor.copy(alpha = 0.3f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Pulsing Avatar Container
            Box(
                modifier = Modifier
                    .size(54.dp),
                contentAlignment = Alignment.Center
            ) {
                // Pulsing light circle background
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .scale(auraScale)
                        .alpha(auraAlpha)
                        .background(accentColor.copy(alpha = 0.25f), CircleShape)
                )

                // Actual avatar representation
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .background(
                            Brush.linearGradient(
                                colors = listOf(accentColor, Color.Black)
                            ),
                            CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    val avatarEmoji = when (activeLanguage) {
                        "Totona" -> "🕊️"
                        "Otomi" -> "⛰️"
                        "Maya" -> "🌌"
                        else -> "🪶"
                    }
                    Text(
                        text = avatarEmoji,
                        fontSize = 28.sp
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                val avatarTitle = when (activeLanguage) {
                    "Totona" -> "Abuelo Oráculo del Tajín"
                    "Otomi" -> "Sabio Abuelo Hñähñu"
                    "Maya" -> "Sacerdote Chilam Balam"
                    else -> "Supremo Sacerdote del Anáhuac"
                }
                Text(
                    text = avatarTitle,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = if (isThinking) "Consultando con las fuerzas divinas..." else "En sintonía contigo. Haz tus preguntas.",
                    fontSize = 13.sp,
                    color = if (isThinking) accentColor else Color.Gray,
                    fontWeight = if (isThinking) FontWeight.Bold else FontWeight.Normal
                )
            }

            // Decorative fire or stars
            if (isThinking) {
                val rotateTransition = rememberInfiniteTransition(label = "rotate_fire")
                val rotationAngle by rotateTransition.animateFloat(
                    initialValue = 0f,
                    targetValue = 360f,
                    animationSpec = infiniteRepeatable(
                        animation = tween(2000, easing = LinearEasing),
                        repeatMode = RepeatMode.Restart
                    ),
                    label = "rotation"
                )
                Icon(
                    imageVector = Icons.Default.AutoAwesome,
                    contentDescription = "Pensando",
                    tint = accentColor,
                    modifier = Modifier
                        .size(20.dp)
                        .graphicsLayer { rotationZ = rotationAngle }
                )
            } else {
                Icon(
                    imageVector = Icons.Default.LocalFireDepartment,
                    contentDescription = "Fuego Sagrado",
                    tint = accentColor.copy(alpha = 0.7f),
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

@Composable
fun OracleBubbleItem(
    message: MainViewModel.OracleMessage,
    activeLanguage: String,
    primaryColor: Color,
    accentColor: Color,
    viewModel: MainViewModel
) {
    val isUser = message.sender == "user"
    val alignment = if (isUser) Alignment.CenterEnd else Alignment.CenterStart
    val bubbleShape = if (isUser) {
        RoundedCornerShape(topStart = 16.dp, topEnd = 4.dp, bottomStart = 16.dp, bottomEnd = 16.dp)
    } else {
        RoundedCornerShape(topStart = 4.dp, topEnd = 16.dp, bottomStart = 16.dp, bottomEnd = 16.dp)
    }

    var showVoiceWave by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
        contentAlignment = alignment
    ) {
        Column(
            horizontalAlignment = if (isUser) Alignment.End else Alignment.Start,
            modifier = Modifier.fillMaxWidth(0.88f)
        ) {
            // Main Bubble Card
            Card(
                shape = bubbleShape,
                colors = CardDefaults.cardColors(
                    containerColor = if (isUser) {
                        Color(0xFF231E1B) // Warm dark tint
                    } else {
                        Color(0xFFF9F5EC) // Codex scroll ivory paper color
                    }
                ),
                border = BorderStroke(
                    width = 1.dp,
                    color = if (isUser) accentColor.copy(alpha = 0.4f) else primaryColor.copy(alpha = 0.8f)
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier
                    .shadow(1.dp, bubbleShape)
                    .testTag(if (isUser) "user_message_bubble" else "oracle_message_bubble")
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    // Header inside bubble
                    if (!isUser) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(bottom = 6.dp)
                        ) {
                            val bubbleLabel = when (activeLanguage) {
                                "Totona" -> "👴 ABUELO ORÁCULO"
                                "Otomi" -> "⛰️ ABUELO HÑÄHÑU"
                                "Maya" -> "🌌 CHILAM BALAM"
                                else -> "🪶 SACERDOTE MAYOR"
                            }
                            Text(
                                text = bubbleLabel,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Black,
                                color = accentColor,
                                letterSpacing = 0.5.sp
                            )
                            Spacer(modifier = Modifier.weight(1f))
                            
                            // Audio Whisper trigger
                            IconButton(
                                onClick = { 
                                    showVoiceWave = !showVoiceWave
                                    if (showVoiceWave) {
                                        if (isUser) {
                                            viewModel.speak(message.text)
                                        } else {
                                            viewModel.playSacerdoteVoice(message.text)
                                        }
                                    } else {
                                        viewModel.stopSpeaking()
                                    }
                                },
                                modifier = Modifier.size(22.dp)
                            ) {
                                Icon(
                                    imageVector = if (showVoiceWave && (viewModel.isSpeaking || viewModel.isXttsSynthesizing)) Icons.Default.VolumeUp else Icons.Default.VolumeMute,
                                    contentDescription = "Escuchar con voz de Víctor",
                                    tint = accentColor,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }

                    // Message text
                    Text(
                        text = message.text,
                        color = if (isUser) Color.White else Color(0xFF2A2219), // Ancient ink color
                        fontSize = 15.sp,
                        lineHeight = 21.sp,
                        fontFamily = FontFamily.SansSerif
                    )

                    // Audio wave simulator
                    AnimatedVisibility(
                        visible = showVoiceWave,
                        enter = expandVertically() + fadeIn(),
                        exit = shrinkVertically() + fadeOut()
                    ) {
                        Column {
                            Spacer(modifier = Modifier.height(8.dp))
                            Divider(color = primaryColor.copy(alpha = 0.2f), thickness = 1.dp)
                            Spacer(modifier = Modifier.height(6.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Susurro ancestral:",
                                    fontSize = 11.sp,
                                    color = Color.Gray,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                SimulatedSoundWave(active = showVoiceWave, color = accentColor)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SimulatedSoundWave(active: Boolean, color: Color) {
    val infiniteTransition = rememberInfiniteTransition(label = "wave")
    val heights = listOf(0.4f, 0.8f, 1f, 0.6f, 0.9f, 0.3f, 0.7f, 0.5f)

    Row(
        modifier = Modifier.height(14.dp),
        horizontalArrangement = Arrangement.spacedBy(2.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        heights.forEachIndexed { index, baseHeight ->
            val animHeight by if (active) {
                infiniteTransition.animateFloat(
                    initialValue = 2f,
                    targetValue = baseHeight * 14f,
                    animationSpec = infiniteRepeatable(
                        animation = tween(durationMillis = 300 + (index * 80), easing = FastOutSlowInEasing),
                        repeatMode = RepeatMode.Reverse
                    ),
                    label = "bar_$index"
                )
            } else {
                remember { mutableStateOf(2f) }
            }

            Box(
                modifier = Modifier
                    .width(2.dp)
                    .height(animHeight.dp)
                    .background(color, RoundedCornerShape(1.dp))
            )
        }
    }
}

@Composable
fun OracleThinkingBubble(
    thinkingText: String,
    primaryColor: Color
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .shadow(1.dp, RoundedCornerShape(12.dp))
                .background(Color.Black.copy(alpha = 0.35f), RoundedCornerShape(12.dp))
                .border(1.dp, primaryColor.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
                .padding(horizontal = 14.dp, vertical = 10.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                CircularProgressIndicator(
                    modifier = Modifier.size(14.dp),
                    color = primaryColor,
                    strokeWidth = 1.5.dp
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = thinkingText,
                    fontSize = 13.sp,
                    color = Color.LightGray,
                    fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                )
            }
        }
    }
}

@Composable
fun SuggestedQuestionsSection(
    activeLanguage: String,
    accentColor: Color,
    onQuestionSelected: (String) -> Unit
) {
    val questions = when (activeLanguage) {
        "Totona" -> {
            listOf(
                "¿Por qué Papantla huele a vainilla? 🌸",
                "¿Cuál es el origen de los Voladores? 🕊️",
                "¿Qué significa 'Tlan' en totonaco? 🌿",
                "¿Quiénes construyeron El Tajín? 🏛️"
            )
        }
        else -> { // Nahuatl
            listOf(
                "¿Quién era Quetzalcóatl? 🐍",
                "¿Cómo saludaban los aztecas? 🤝",
                "¿Qué significa 'Mesoamérica'? 🛕",
                "¿Cómo contaban con el vigesimal? 🔢"
            )
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Text(
            text = "✨ PREGUNTAS SAGRADAS SUGERIDAS",
            fontSize = 11.sp,
            fontWeight = FontWeight.ExtraBold,
            color = Color.Gray,
            letterSpacing = 1.sp,
            modifier = Modifier.padding(bottom = 6.dp)
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            questions.forEachIndexed { idx, q ->
                Card(
                    modifier = Modifier
                        .clickable { onQuestionSelected(q) }
                        .testTag("suggested_question_chip_$idx"),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = accentColor.copy(alpha = 0.12f)),
                    border = BorderStroke(1.dp, accentColor.copy(alpha = 0.35f))
                ) {
                    Text(
                        text = q,
                        fontSize = 13.sp,
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun OracleInputSection(
    query: String,
    onQueryChange: (String) -> Unit,
    isThinking: Boolean,
    primaryColor: Color,
    accentColor: Color,
    onSendClicked: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.Black.copy(alpha = 0.4f), RoundedCornerShape(24.dp))
            .border(1.dp, primaryColor.copy(alpha = 0.4f), RoundedCornerShape(24.dp))
            .padding(horizontal = 14.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        OutlinedTextField(
            value = query,
            onValueChange = onQueryChange,
            modifier = Modifier
                .weight(1f)
                .testTag("oracle_user_input_field"),
            placeholder = {
                Text(
                    text = "Pregúntale al Oráculo de IA...",
                    fontSize = 14.sp,
                    color = Color.Gray
                )
            },
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White,
                focusedBorderColor = Color.Transparent,
                unfocusedBorderColor = Color.Transparent,
                cursorColor = primaryColor
            ),
            textStyle = androidx.compose.ui.text.TextStyle(fontSize = 15.sp)
        )

        Spacer(modifier = Modifier.width(6.dp))

        if (isThinking) {
            CircularProgressIndicator(
                modifier = Modifier.size(24.dp),
                color = primaryColor,
                strokeWidth = 2.dp
            )
        } else {
            IconButton(
                onClick = onSendClicked,
                enabled = query.isNotBlank(),
                modifier = Modifier
                    .clip(CircleShape)
                    .background(if (query.isNotBlank()) primaryColor else Color.DarkGray)
                    .size(36.dp)
                    .testTag("oracle_send_button")
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Send,
                    contentDescription = "Enviar pregunta",
                    tint = if (query.isNotBlank()) Color.Black else Color.Gray,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}
