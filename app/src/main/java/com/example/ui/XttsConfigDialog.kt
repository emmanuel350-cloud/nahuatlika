package com.example.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import kotlinx.coroutines.launch
import com.example.data.XttsConfig

/**
 * Integrated Branded Logo for XTTS & Hugging Face Voice Cloning Engine
 * Drawn natively in Compose with vector canvas waveform and glowing glyphs.
 */
@Composable
fun XttsBrandedHeaderLogo(modifier: Modifier = Modifier) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
    ) {
        Box(
            modifier = Modifier
                .size(46.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(
                    Brush.radialGradient(
                        colors = listOf(Color(0xFF00E5FF).copy(alpha = 0.35f), Color(0xFF151820)),
                        radius = 65f
                    )
                )
                .border(
                    1.5.dp,
                    Brush.linearGradient(listOf(Color(0xFF00E5FF), Color(0xFFFFB74D))),
                    RoundedCornerShape(14.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            Canvas(modifier = Modifier.size(36.dp)) {
                val w = size.width
                val h = size.height
                val midY = h / 2f

                // Pulsating audio waveform bars in neon cyan and Aztec gold
                val barHeights = listOf(0.35f, 0.70f, 1.0f, 0.85f, 0.50f, 0.90f, 0.40f)
                val barWidth = 3f
                val spacing = w / (barHeights.size + 1)

                barHeights.forEachIndexed { index, heightFactor ->
                    val x = spacing * (index + 1)
                    val barH = h * 0.65f * heightFactor
                    val color = if (index % 2 == 0) Color(0xFF00E5FF) else Color(0xFFFFB74D)
                    drawLine(
                        color = color,
                        start = Offset(x, midY - barH / 2f),
                        end = Offset(x, midY + barH / 2f),
                        strokeWidth = barWidth
                    )
                }
            }
            Text("🛕", fontSize = 15.sp)
        }

        Spacer(modifier = Modifier.width(10.dp))

        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "XTTS-v2 NEURAL",
                    fontSize = 11.5.sp,
                    fontWeight = FontWeight.Black,
                    color = Color(0xFF00E5FF),
                    letterSpacing = 1.2.sp
                )
                Spacer(modifier = Modifier.width(6.dp))
                Surface(
                    shape = RoundedCornerShape(4.dp),
                    color = Color(0xFFFFB74D).copy(alpha = 0.2f),
                    border = BorderStroke(0.8.dp, Color(0xFFFFB74D))
                ) {
                    Text(
                        text = "🤗 Hugging Face",
                        fontSize = 8.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFFFB74D),
                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                    )
                }
            }
            Text(
                text = "Coqui & Hugging Face Engine",
                fontSize = 13.5.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun XttsConfigDialog(
    viewModel: MainViewModel,
    onDismiss: () -> Unit
) {
    var config by remember { mutableStateOf(viewModel.xttsConfig) }
    var testPhrase by remember { mutableStateOf("Niltze, cualli tonalli. Tlazohcamati ipampa moceltzin.") }
    var showHfGuide by remember { mutableStateOf(false) }
    var isCheckingStatus by remember { mutableStateOf(false) }
    var serverStatusMessage by remember { mutableStateOf<String?>(null) }
    val coroutineScope = rememberCoroutineScope()
    val isSynthesizing = viewModel.isXttsSynthesizing
    val isSpeaking = viewModel.isSpeaking
    val lastError = viewModel.xttsClient.lastError

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .fillMaxHeight(0.92f)
                .clip(RoundedCornerShape(24.dp))
                .border(2.dp, Color(0xFF00E5FF), RoundedCornerShape(24.dp))
                .testTag("xtts_config_dialog"),
            color = Color(0xFF12141A)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(18.dp)
            ) {
                // Top Header with Integrated Branded Logo
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    XttsBrandedHeaderLogo()

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.testTag("close_xtts_dialog")
                    ) {
                        Icon(Icons.Default.Close, contentDescription = "Cerrar", tint = Color.LightGray)
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    // --- 1. TOGGLE ACTIVATION CARD ---
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E222B)),
                        border = BorderStroke(1.dp, if (config.isEnabled) Color(0xFF00E5FF) else Color(0x33FFFFFF))
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = "Activar Motor XTTS",
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    if (config.isEnabled) {
                                        Surface(
                                            shape = RoundedCornerShape(4.dp),
                                            color = Color(0xFF00E5FF).copy(alpha = 0.2f)
                                        ) {
                                            Text(
                                                "ACTIVO",
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.Black,
                                                color = Color(0xFF00E5FF),
                                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                            )
                                        }
                                    }
                                }
                                Text(
                                    text = if (config.isEnabled) "Clonación neuronal activa vía Hugging Face / XTTS" else "Usando Text-To-Speech nativo del dispositivo como respaldo",
                                    fontSize = 11.sp,
                                    color = if (config.isEnabled) Color(0xFF80D8FF) else Color.Gray
                                )
                            }
                            Switch(
                                checked = config.isEnabled,
                                onCheckedChange = { isChecked ->
                                    config = config.copy(isEnabled = isChecked)
                                    viewModel.xttsConfig = config
                                },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = Color(0xFF00E5FF),
                                    checkedTrackColor = Color(0x6600E5FF)
                                ),
                                modifier = Modifier.testTag("xtts_toggle_switch")
                            )
                        }
                    }

                    // --- 2. SACERDOTE MASTER VOICE (LOCKED & IMMUTABLE PERMANENT DEFAULT) ---
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF261D16)),
                        border = BorderStroke(1.5.dp, Color(0xFFFFB74D))
                    ) {
                        Column(
                            modifier = Modifier.padding(14.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text("🛕", fontSize = 20.sp)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Column {
                                        Text(
                                            text = "Voz Oficial del Sacerdote",
                                            fontSize = 13.5.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFFFFB74D)
                                        )
                                        Text(
                                            text = "Víctor Florencio (Voz Sagrada)",
                                            fontSize = 11.sp,
                                            color = Color.White
                                        )
                                    }
                                }

                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = Color(0xFFFFB74D).copy(alpha = 0.25f),
                                    border = BorderStroke(1.dp, Color(0xFFFFB74D))
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            Icons.Default.Lock,
                                            contentDescription = "Bloqueada",
                                            tint = Color(0xFFFFD54F),
                                            modifier = Modifier.size(11.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            "VOZ FIJA",
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Black,
                                            color = Color(0xFFFFD54F)
                                        )
                                    }
                                }
                            }

                            Text(
                                text = "🔒 Esta es la voz predeterminada e inmutable del Sacerdote para el Templo y el Oráculo IA. Está bloqueada en el código fuente para garantizar la identidad espiritual permanente.",
                                fontSize = 10.5.sp,
                                color = Color.White.copy(alpha = 0.9f),
                                lineHeight = 14.sp
                            )

                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = Color(0x33000000),
                                border = BorderStroke(0.8.dp, Color(0x40FFFFFF)),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.padding(8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFF81C784), modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "Audio maestro vinculado: voz_sacerdote_sample.wav (24kHz Mono)",
                                        fontSize = 9.5.sp,
                                        color = Color(0xFFC8E6C9)
                                    )
                                }
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Button(
                                    onClick = { viewModel.playSacerdoteVoiceSample() },
                                    modifier = Modifier.weight(1f),
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFB74D)),
                                    shape = RoundedCornerShape(8.dp),
                                    contentPadding = PaddingValues(horizontal = 6.dp, vertical = 6.dp)
                                ) {
                                    Icon(Icons.Default.PlayArrow, contentDescription = "Muestra", tint = Color.Black, modifier = Modifier.size(15.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Oír Muestra (12s)", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                                }

                                OutlinedButton(
                                    onClick = { viewModel.playSacerdoteVoiceFull() },
                                    modifier = Modifier.weight(1f),
                                    border = BorderStroke(1.dp, Color(0xFFFFB74D)),
                                    shape = RoundedCornerShape(8.dp),
                                    contentPadding = PaddingValues(horizontal = 6.dp, vertical = 6.dp)
                                ) {
                                    Icon(Icons.Default.VolumeUp, contentDescription = "Completo", tint = Color(0xFFFFB74D), modifier = Modifier.size(15.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Oír Completo (3m)", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFFFFB74D))
                                }

                                if (isSpeaking) {
                                    IconButton(
                                        onClick = { viewModel.stopSpeaking() },
                                        modifier = Modifier
                                            .size(36.dp)
                                            .background(Color(0xFFE53935), RoundedCornerShape(8.dp))
                                    ) {
                                        Icon(Icons.Default.Stop, contentDescription = "Detener", tint = Color.White, modifier = Modifier.size(16.dp))
                                    }
                                }
                            }
                        }
                    }

                    // --- 3. HUGGING FACE SPACES & SERVER CONFIGURATION ---
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E222B)),
                        border = BorderStroke(1.dp, Color(0xFF00E5FF).copy(alpha = 0.5f))
                    ) {
                        Column(
                            modifier = Modifier.padding(14.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text("🤗", fontSize = 18.sp)
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "Conexión a Hugging Face / Servidor",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                }

                                TextButton(
                                    onClick = { showHfGuide = !showHfGuide },
                                    contentPadding = PaddingValues(horizontal = 4.dp, vertical = 2.dp)
                                ) {
                                    Icon(Icons.Default.HelpOutline, contentDescription = "Ayuda", tint = Color(0xFF00E5FF), modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(3.dp))
                                    Text(if (showHfGuide) "Ocultar Guía" else "¿Cómo Conectar?", fontSize = 10.sp, color = Color(0xFF00E5FF))
                                }
                            }

                            // Quick Preset Buttons
                            Text(
                                text = "Preajustes Rápidos de Conexión:",
                                fontSize = 10.5.sp,
                                color = Color.LightGray
                            )
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Surface(
                                    onClick = {
                                        config = config.copy(serverUrl = "https://coqui-xtts.hf.space/tts_to_audio")
                                        viewModel.xttsConfig = config
                                    },
                                    color = if (config.serverUrl.contains("coqui-xtts")) Color(0x3300E5FF) else Color(0xFF151820),
                                    shape = RoundedCornerShape(8.dp),
                                    border = BorderStroke(1.dp, if (config.serverUrl.contains("coqui-xtts")) Color(0xFF00E5FF) else Color(0x26FFFFFF)),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text(
                                        "🤗 Space Público",
                                        fontSize = 9.5.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White,
                                        modifier = Modifier.padding(vertical = 6.dp),
                                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                    )
                                }

                                Surface(
                                    onClick = {
                                        config = config.copy(serverUrl = "https://mi-usuario-espacio.hf.space/tts_to_audio")
                                        viewModel.xttsConfig = config
                                    },
                                    color = if (config.serverUrl.contains("mi-usuario")) Color(0x3300E5FF) else Color(0xFF151820),
                                    shape = RoundedCornerShape(8.dp),
                                    border = BorderStroke(1.dp, if (config.serverUrl.contains("mi-usuario")) Color(0xFF00E5FF) else Color(0x26FFFFFF)),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text(
                                        "⚡ Mi Propio Space",
                                        fontSize = 9.5.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White,
                                        modifier = Modifier.padding(vertical = 6.dp),
                                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                    )
                                }

                                Surface(
                                    onClick = {
                                        config = config.copy(serverUrl = "http://10.0.2.2:8020/tts_to_audio")
                                        viewModel.xttsConfig = config
                                    },
                                    color = if (config.serverUrl.contains("10.0.2.2")) Color(0x3300E5FF) else Color(0xFF151820),
                                    shape = RoundedCornerShape(8.dp),
                                    border = BorderStroke(1.dp, if (config.serverUrl.contains("10.0.2.2")) Color(0xFF00E5FF) else Color(0x26FFFFFF)),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text(
                                        "💻 Local (10.0.2.2)",
                                        fontSize = 9.5.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White,
                                        modifier = Modifier.padding(vertical = 6.dp),
                                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                    )
                                }
                            }

                            // Interactive Hugging Face Guide
                            AnimatedVisibility(visible = showHfGuide) {
                                Surface(
                                    shape = RoundedCornerShape(10.dp),
                                    color = Color(0xFF151820),
                                    border = BorderStroke(1.dp, Color(0xFFFFB74D).copy(alpha = 0.5f)),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Column(
                                        modifier = Modifier.padding(10.dp),
                                        verticalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        Text(
                                            "📖 Cómo conectar tu Hugging Face Space:",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFFFFB74D)
                                        )
                                        Text("1. Ve a huggingface.co/spaces y busca un Space de XTTS (o duplica 'coqui/xtts').", fontSize = 10.sp, color = Color.White)
                                        Text("2. En los 3 puntos ⋮ del Space, pulsa 'Embed this Space' o ve a Settings y copia la URL directa '.hf.space'.", fontSize = 10.sp, color = Color.White)
                                        Text("3. Pégala abajo. Si tu Space es privado, genera un Token en huggingface.co/settings/tokens (tipo 'read') y pégalo.", fontSize = 10.sp, color = Color.White)
                                        Text("✨ La app enviará automáticamente el audio del Sacerdote para clonarlo en vivo.", fontSize = 10.sp, color = Color(0xFF80D8FF))
                                    }
                                }
                            }

                            // URL TextField with Auto-Normalization
                            OutlinedTextField(
                                value = config.serverUrl,
                                onValueChange = { inputUrl ->
                                    val normalized = viewModel.xttsClient.normalizeServerUrl(inputUrl)
                                    config = config.copy(serverUrl = normalized)
                                    viewModel.xttsConfig = config
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("xtts_server_url_input"),
                                label = { Text("URL del Hugging Face Space / Servidor", fontSize = 12.sp) },
                                placeholder = { Text("https://tu-space.hf.space/tts_to_audio", fontSize = 11.sp) },
                                singleLine = true,
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = Color(0xFF00E5FF),
                                    unfocusedBorderColor = Color(0x40FFFFFF),
                                    focusedTextColor = Color.White,
                                    unfocusedTextColor = Color.White
                                )
                            )

                            // Token Input
                            OutlinedTextField(
                                value = config.apiKey,
                                onValueChange = {
                                    config = config.copy(apiKey = it)
                                    viewModel.xttsConfig = config
                                },
                                modifier = Modifier.fillMaxWidth(),
                                label = { Text("Hugging Face Token (Opcional si es público)", fontSize = 12.sp) },
                                placeholder = { Text("hf_xxxxxxxxxxxxxxxxxxxxxx", fontSize = 11.sp) },
                                singleLine = true,
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = Color(0xFF00E5FF),
                                    unfocusedBorderColor = Color(0x40FFFFFF),
                                    focusedTextColor = Color.White,
                                    unfocusedTextColor = Color.White
                                )
                            )

                            // Status check button
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                OutlinedButton(
                                    onClick = {
                                        isCheckingStatus = true
                                        serverStatusMessage = null
                                        coroutineScope.launch {
                                            val (_, status) = viewModel.xttsClient.checkServerHealth(config.serverUrl, config.apiKey)
                                            serverStatusMessage = status
                                            isCheckingStatus = false
                                        }
                                    },
                                    shape = RoundedCornerShape(8.dp),
                                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                    border = BorderStroke(1.dp, Color(0xFF00E5FF).copy(alpha = 0.6f))
                                ) {
                                    if (isCheckingStatus) {
                                        CircularProgressIndicator(modifier = Modifier.size(12.dp), strokeWidth = 1.5.dp, color = Color(0xFF00E5FF))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text("Verificando...", fontSize = 10.sp, color = Color(0xFF00E5FF))
                                    } else {
                                        Icon(Icons.Default.Refresh, contentDescription = null, tint = Color(0xFF00E5FF), modifier = Modifier.size(13.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("Comprobar Estado del Space", fontSize = 10.5.sp, color = Color(0xFF00E5FF))
                                    }
                                }
                            }

                            serverStatusMessage?.let { status ->
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = if (status.startsWith("🟢")) Color(0x264CAF50) else Color(0x33FF9800),
                                    border = BorderStroke(1.dp, if (status.startsWith("🟢")) Color(0xFF81C784) else Color(0xFFFFB74D)),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text(
                                        text = status,
                                        fontSize = 10.sp,
                                        color = Color.White,
                                        modifier = Modifier.padding(8.dp)
                                    )
                                }
                            }

                            // Notice on HTTP 505 / 503
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = Color(0x2000E5FF),
                                border = BorderStroke(0.8.dp, Color(0xFF00E5FF).copy(alpha = 0.4f)),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.padding(8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(Icons.Default.Info, contentDescription = null, tint = Color(0xFF00E5FF), modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "ℹ️ Aviso 505/503: Hugging Face duerme los Spaces gratuitos tras inactividad ('Your space is in error / sleeping'). Para despertarlo, entra a tu cuenta en huggingface.co/spaces y pulsa 'Restart Space'.",
                                        fontSize = 9.5.sp,
                                        color = Color(0xFFE0F7FA),
                                        lineHeight = 13.sp
                                    )
                                }
                            }
                        }
                    }

                    // --- 4. TEST VOICE SYNTHESIS ---
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E222B)),
                        border = BorderStroke(1.dp, Color(0x26FFFFFF))
                    ) {
                        Column(
                            modifier = Modifier.padding(14.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Text(
                                text = "🔊 Prueba de Síntesis en Vivo (Voz del Sacerdote)",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )

                            OutlinedTextField(
                                value = testPhrase,
                                onValueChange = { testPhrase = it },
                                modifier = Modifier.fillMaxWidth(),
                                label = { Text("Frase sagrada para sintetizar", fontSize = 11.sp) },
                                singleLine = false,
                                maxLines = 2,
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = Color(0xFF00E5FF),
                                    unfocusedBorderColor = Color(0x40FFFFFF),
                                    focusedTextColor = Color.White,
                                    unfocusedTextColor = Color.White
                                )
                            )

                            // Status Indicator
                            if (isSynthesizing) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.Center,
                                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                                ) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(18.dp),
                                        color = Color(0xFF00E5FF),
                                        strokeWidth = 2.dp
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        "Sintetizando voz del Sacerdote en XTTS...",
                                        fontSize = 11.sp,
                                        color = Color(0xFF80D8FF)
                                    )
                                }
                            } else if (lastError != null) {
                                Surface(
                                    color = Color(0x26D32F2F),
                                    shape = RoundedCornerShape(8.dp),
                                    border = BorderStroke(1.dp, Color(0xFFE57373)),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text(
                                        text = "Aviso: $lastError (usando voz local de respaldo)",
                                        fontSize = 10.5.sp,
                                        color = Color(0xFFFF8A80),
                                        modifier = Modifier.padding(8.dp)
                                    )
                                }
                            }

                            Button(
                                onClick = {
                                    viewModel.synthesizeSacerdotePhrase(testPhrase)
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFF00E5FF),
                                    contentColor = Color(0xFF0C1017)
                                ),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(46.dp)
                                    .testTag("test_xtts_voice_button")
                            ) {
                                Icon(
                                    imageVector = if (isSpeaking) Icons.Default.VolumeUp else Icons.Default.PlayArrow,
                                    contentDescription = null
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = if (isSynthesizing) "Sintetizando Voz de Víctor..." else if (isSpeaking) "Reproduciendo Voz Sagrada..." else "Probar Síntesis del Sacerdote",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Bottom Save & Close Button
                Button(
                    onClick = {
                        viewModel.saveXttsConfig(config)
                        onDismiss()
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF263238),
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Guardar Configuración y Cerrar", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
