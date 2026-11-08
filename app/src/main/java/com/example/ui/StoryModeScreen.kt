package com.example.ui

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.MainActivity
import com.example.R
import com.example.ui.MainViewModel

data class DialogueLine(
    val speaker: String,
    val avatar: String,
    val text: String,
    val voiceVariant: String? = null
)

data class StoryScene(
    val title: String,
    val subtitle: String,
    val bgScale: Float,
    val bgOffsetX: Float,
    val bgOffsetY: Float,
    val description: String,
    val dialogues: List<DialogueLine>
)

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun StoryModeScreen(
    viewModel: MainViewModel,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var currentSceneIndex by remember { mutableStateOf(0) }
    var currentDialogueIndex by remember { mutableStateOf(0) }
    
    val storyScenes = remember {
        listOf(
            StoryScene(
                title = "Capítulo 1: La Llegada a México 🇲🇽",
                subtitle = "Tres jóvenes exploradores entran al místico pueblo de la Huaxlica.",
                bgScale = 1.25f,
                bgOffsetX = -50f,
                bgOffsetY = 15f,
                description = "Xavi, Toti y su fiel compañero robótico Kiri han cruzado desiertos y selvas en busca de las voces perdidas del México ancestral...",
                dialogues = listOf(
                    DialogueLine("Toti", "🎒 Toti", "¡Miren! ¡Por fin hemos llegado a México! Este pueblo de la Huaxlica se siente increíblemente vivo.", null),
                    DialogueLine("Xavi", "🔭 Xavi", "¡Es asombroso! Dicen que aquí los vientos aún transportan las antiguas leyendas del Náhuatl y el Totonaco.", null),
                    DialogueLine("Kiri", "🤖 Kiri", "¡Bip... bup! Detectando altos niveles de energía cultural y sabiduría ancestral en la atmósfera.", null)
                )
            ),
            StoryScene(
                title = "Capítulo 2: El Encuentro Sagrado 🤝",
                subtitle = "Los sabios guardianes dan la bienvenida a los viajeros.",
                bgScale = 1.45f,
                bgOffsetX = 60f,
                bgOffsetY = -10f,
                description = "Al entrar a la plaza central, un aroma de copal y vainilla los guía hacia dos venerables figuras que los observan con agrado...",
                dialogues = listOf(
                    DialogueLine("Don Tecpatl", "🪶 Don Tecpatl", "Niltze, jóvenes viajeros. Bienvenidos a la Huaxlica, la tierra de las voces eternas del Náhuatl.", "Náhuatl Central / Imperial"),
                    DialogueLine("Abuela Tajín", "🌸 Abuela Tajín", "Tlan tlan, pequeños guardianes. Soy Abuela Tajín, cuidadora de los cantos aromáticos de la vainilla.", "Ceremonial de Papantla"),
                    DialogueLine("Toti", "🎒 Toti", "¡Es un gran honor conocerlos! Queremos aprender sus lenguas y tradiciones.", null)
                )
            ),
            StoryScene(
                title = "Capítulo 3: El Viento del Olvido 🌪️",
                subtitle = "Un peligro acecha a las lenguas originarias.",
                bgScale = 1.15f,
                bgOffsetX = 0f,
                bgOffsetY = -40f,
                description = "El semblante de los sabios se torna serio mientras señalan hacia las nubes misteriosas del horizonte.",
                dialogues = listOf(
                    DialogueLine("Don Tecpatl", "🪶 Don Tecpatl", "El olvido avanza como un viento frío. Las variantes de nuestras lenguas se están dispersando por valles y montañas.", "Náhuatl Central / Imperial"),
                    DialogueLine("Abuela Tajín", "🌸 Abuela Tajín", "Si nadie las recolecta en nuestro Códice Sagrado, el saber de nuestros ancestros desaparecerá para siempre.", "Ceremonial de Papantla"),
                    DialogueLine("Xavi", "🔭 Xavi", "¡No permitiremos que eso pase! Nosotros los ayudaremos a salvar cada variante.", null)
                )
            ),
            StoryScene(
                title = "Capítulo 4: El Códice Sagrado 📜",
                subtitle = "La gran misión de recolección de voces ancestrales.",
                bgScale = 1.5f,
                bgOffsetX = -30f,
                bgOffsetY = 40f,
                description = "Abuela Tajín extiende un pergamino antiguo que resplandece con símbolos dorados y glifos prehispánicos.",
                dialogues = listOf(
                    DialogueLine("Abuela Tajín", "🌸 Abuela Tajín", "Lleven este pergamino místico. Cada palabra que aprendan y cada desafío que superen quedará grabado en oro.", "Ceremonial de Papantla"),
                    DialogueLine("Don Tecpatl", "🪶 Don Tecpatl", "Deben recorrer todo el pueblo de la Huaxlica, desde la costa de Papantla hasta las tierras de la Huasteca.", "Náhuatl Central / Imperial"),
                    DialogueLine("Kiri", "🤖 Kiri", "¡Bip! Códice sincronizado. Objetivo de misión: ¡Salvar las voces originarias de México!", null)
                )
            ),
            StoryScene(
                title = "Capítulo 5: ¡La Aventura Comienza! 🗺️",
                subtitle = "Listos para explorar la geografía de la Huaxlica.",
                bgScale = 1.0f,
                bgOffsetX = 0f,
                bgOffsetY = 0f,
                description = "Con el Códice de la Huaxlica en la mano, los tres aventureros miran hacia el horizonte con determinación.",
                dialogues = listOf(
                    DialogueLine("Xavi", "🔭 Xavi", "¡Es hora de explorar! Recorreremos todo el mapa para registrar cada dialecto.", null),
                    DialogueLine("Toti", "🎒 Toti", "¡Sí! ¡Vamos a aprender y rescatar el Náhuatl y el Totonaco!", null),
                    DialogueLine("Don Tecpatl", "🪶 Don Tecpatl", "¡Que el calor del Sol y la fuerza del viento guíen sus pasos, pequeños guardianes!", "Náhuatl Central / Imperial")
                )
            )
        )
    }

    val activeScene = storyScenes[currentSceneIndex]
    val activeDialogue = activeScene.dialogues[currentDialogueIndex]

    // Automatically speak the dialogue when it changes
    LaunchedEffect(currentSceneIndex, currentDialogueIndex) {
        viewModel.speak(activeDialogue.text, activeDialogue.voiceVariant)
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF0C0E12))
    ) {
        // --- 1. CINEMATIC CAMERA BACKDROP ---
        Box(
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer(
                    scaleX = activeScene.bgScale,
                    scaleY = activeScene.bgScale,
                    translationX = activeScene.bgOffsetX,
                    translationY = activeScene.bgOffsetY
                )
        ) {
            Image(
                painter = painterResource(id = R.drawable.img_story_huaxlica_1783736664154),
                contentDescription = "Fondo de la Historia",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop,
                alpha = 0.45f
            )
        }

        // Gradient Overlay for readability
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0xBB000000),
                            Color(0x33000000),
                            Color(0xEE0C0E12)
                        )
                    )
                )
        )

        // Floating particle canvas for atmosphere
        Canvas(modifier = Modifier.fillMaxSize()) {
            // Draw ancient magical fireflies or wind particles
        }

        // --- 2. MAIN STORY CONTAINER ---
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header Section
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "LA GRAN HUAXLICA",
                        color = Color(0xFFFFD700),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 2.sp
                    )
                    Text(
                        text = activeScene.title,
                        color = Color.White,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                }
                
                TextButton(
                    onClick = {
                        viewModel.stopSpeaking()
                        onDismiss()
                    },
                    colors = ButtonDefaults.textButtonColors(contentColor = Color(0xFFB0BEC5)),
                    modifier = Modifier.testTag("skip_story_button")
                ) {
                    Text("Saltar Historia ⏩", fontSize = 13.sp)
                }
            }

            // Scene Narrative Box
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0x7F151B26)),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, Color(0x3F90A4AE)),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            ) {
                Text(
                    text = activeScene.description,
                    color = Color(0xFFECEFF1),
                    fontSize = 13.sp,
                    fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(12.dp)
                )
            }

            // --- 3. CHARACTER VISUAL FOCUS AREA ---
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                // We show custom styled characters
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    activeScene.dialogues.forEachIndexed { idx, diag ->
                        val isSpeaker = diag.speaker == activeDialogue.speaker
                        val bounceOffset by animateDpAsState(
                            targetValue = if (isSpeaker) (-12).dp else 0.dp,
                            animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
                            label = "character_bounce"
                        )
                        val scaleFactor by animateFloatAsState(
                            targetValue = if (isSpeaker) 1.15f else 0.85f,
                            label = "character_scale"
                        )
                        val avatarAlpha by animateFloatAsState(
                            targetValue = if (isSpeaker) 1f else 0.5f,
                            label = "character_alpha"
                        )

                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .offset(y = bounceOffset)
                                .scale(scaleFactor)
                                .alpha(avatarAlpha)
                                .clickable {
                                    currentDialogueIndex = idx
                                }
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(72.dp)
                                    .clip(CircleShape)
                                    .background(
                                        if (isSpeaker) Color(0xFFFFD700) else Color(0xFF263238)
                                    )
                                    .border(
                                        2.dp,
                                        if (isSpeaker) Color.White else Color.Transparent,
                                        CircleShape
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                val emoji = when (diag.speaker) {
                                    "Toti" -> "🎒"
                                    "Xavi" -> "🔭"
                                    "Kiri" -> "🤖"
                                    "Don Tecpatl" -> "🪶"
                                    "Abuela Tajín" -> "🌸"
                                    else -> "🧑"
                                }
                                Text(emoji, fontSize = 36.sp)
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = diag.speaker,
                                color = if (isSpeaker) Color(0xFFFFD700) else Color(0xFFCFD8DC),
                                fontSize = 12.sp,
                                fontWeight = if (isSpeaker) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    }
                }
            }

            // --- 4. INTERACTIVE DIALOGUE BALLOON ---
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFF1E2736)
                ),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(2.dp, Color(0xFFFFD700)),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp)
                    .testTag("dialogue_bubble_card")
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = activeDialogue.avatar,
                            color = Color(0xFFFFD700),
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )

                        IconButton(
                            onClick = {
                                viewModel.speak(activeDialogue.text, activeDialogue.voiceVariant)
                            },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.VolumeUp,
                                contentDescription = "Escuchar voz",
                                tint = Color(0xFFFFD700)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = activeDialogue.text,
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        lineHeight = 22.sp
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Indicator for variant voice profile
                    activeDialogue.voiceVariant?.let { variant ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .background(Color(0x3300E676), RoundedCornerShape(4.dp))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "🗣️ Perfil de Voz Regional: $variant",
                                color = Color(0xFF00E676),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            // --- 5. PROGRESS & NAVIGATION CONTROLS ---
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Scene indicator dots
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    repeat(storyScenes.size) { index ->
                        Box(
                            modifier = Modifier
                                .size(if (index == currentSceneIndex) 16.dp else 8.dp, 8.dp)
                                .clip(RoundedCornerShape(4.dp))
                                .background(
                                    if (index == currentSceneIndex) Color(0xFFFFD700) else Color(0x3FFFFFFF)
                                )
                        )
                    }
                }

                // Prev / Next controls
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (currentDialogueIndex > 0 || currentSceneIndex > 0) {
                        FilledTonalButton(
                            onClick = {
                                viewModel.stopSpeaking()
                                if (currentDialogueIndex > 0) {
                                    currentDialogueIndex--
                                } else {
                                    currentSceneIndex--
                                    currentDialogueIndex = storyScenes[currentSceneIndex].dialogues.size - 1
                                }
                            },
                            colors = ButtonDefaults.filledTonalButtonColors(
                                containerColor = Color(0x33FFFFFF),
                                contentColor = Color.White
                            ),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Atrás")
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Atrás", fontSize = 12.sp)
                        }
                    }

                    val isLastDialogueInScene = currentDialogueIndex == activeScene.dialogues.size - 1
                    val isLastScene = currentSceneIndex == storyScenes.size - 1

                    Button(
                        onClick = {
                            viewModel.stopSpeaking()
                            if (isLastDialogueInScene) {
                                if (isLastScene) {
                                    onDismiss()
                                } else {
                                    currentSceneIndex++
                                    currentDialogueIndex = 0
                                }
                            } else {
                                currentDialogueIndex++
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFFFD700),
                            contentColor = Color(0xFF0C0E12)
                        ),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.testTag("next_story_button")
                    ) {
                        val label = if (isLastScene && isLastDialogueInScene) {
                            "¡Comenzar!"
                        } else if (isLastDialogueInScene) {
                            "Siguiente Capítulo"
                        } else {
                            "Siguiente"
                        }
                        
                        Text(label, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(
                            imageVector = if (isLastScene && isLastDialogueInScene) Icons.Default.PlayArrow else Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = "Siguiente"
                        )
                    }
                }
            }
        }
    }
}
