package com.example.ui

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties

data class TotonaVariantInfo(
    val id: String,
    val name: String,
    val inliCode: String,
    val regionGroup: String, // "Veracruz / Costa", "Puebla / Sierra Norte", "Puebla / Sierra Sur", "Puebla / Necaxa"
    val states: String,
    val estimatedSpeakers: String,
    val status: String,
    val greetingPhrase: String,
    val greetingSpanish: String,
    val linguisticTrait: String,
    val description: String
)

val TOTONA_VARIANTS_CATALOG = listOf(
    TotonaVariantInfo(
        id = "to_costa",
        name = "Totonaco de la costa",
        inliCode = "To-04",
        regionGroup = "Veracruz / Costa",
        states = "Veracruz (Papantla, Cazones, El Tajín, Poza Rica, Espinal)",
        estimatedSpeakers = "120,000+ hablantes",
        status = "Muy vigorosa / Emblemática",
        greetingPhrase = "Tlan kilhtamaku, ¿kuentlahni 'ani'?",
        greetingSpanish = "Buenos días, ¿cómo estás?",
        linguisticTrait = "Variante icónica del Totonacapan con rica tradición poética y ritual.",
        description = "Corazón del imperio Tajín, danza de los Voladores de Papantla y cultivo ancestral de la vainilla."
    ),
    TotonaVariantInfo(
        id = "to_central_sur",
        name = "Totonaco central del sur",
        inliCode = "To-07",
        regionGroup = "Puebla / Sierra Sur",
        states = "Puebla (Filomeno Mata, Mecatlán, Zapotitlán de Méndez, Coatepec)",
        estimatedSpeakers = "130,000+ hablantes",
        status = "Muy vigorosa / Transmisión infantil activa",
        greetingPhrase = "Tlan kilhtamaku, ¿kuentlah 'an'?",
        greetingSpanish = "Buenos días, ¿cómo vas?",
        linguisticTrait = "Conserva una de las tasas de transmisión generacional más altas del país.",
        description = "Sierras elevadas con arquitectura tradicional de piedra y telares de cintura."
    ),
    TotonaVariantInfo(
        id = "to_central_alto",
        name = "Totonaco central alto",
        inliCode = "To-05",
        regionGroup = "Puebla / Sierra Norte",
        states = "Puebla (Zacatlán, Ahuacatlán, Amixtlán, Tepango de Rodríguez)",
        estimatedSpeakers = "85,000+ hablantes",
        status = "Muy vigorosa",
        greetingPhrase = "Tlan kilhtamaku, ¿kuentlahni wilaya'?",
        greetingSpanish = "Buenos días, ¿cómo te encuentras?",
        linguisticTrait = "Pronunciación clara de consonantes glotalizadas en zonas de alta montaña.",
        description = "Ríos cristalinos, niebla serrana y bordados multicolores sobre manta."
    ),
    TotonaVariantInfo(
        id = "to_central_norte",
        name = "Totonaco central del norte",
        inliCode = "To-06",
        regionGroup = "Puebla / Sierra Norte",
        states = "Puebla (Venustiano Carranza, Jalpan, Pantepec)",
        estimatedSpeakers = "45,000+ hablantes",
        status = "Vigorosa",
        greetingPhrase = "Tlan kilhtamaku, ¿kuya 'aniya'?",
        greetingSpanish = "Buenos días, ¿cómo estás?",
        linguisticTrait = "Intercambio fluído entre la vertiente de la Sierra y la llanura norteña.",
        description = "Valle de la baja serranía con ganadería tradicional y gastronomía de pipián."
    ),
    TotonaVariantInfo(
        id = "to_xinolatepetl",
        name = "Totonaco del cerro Xinolatépetl",
        inliCode = "To-02",
        regionGroup = "Puebla / Sierra Norte",
        states = "Puebla (Hermenegildo Galeana, Jopala, Olintla)",
        estimatedSpeakers = "28,000+ hablantes",
        status = "Vigorosa / Comunitaria",
        greetingPhrase = "Tlan tsisa, ¿kuya 'aniya?",
        greetingSpanish = "Buenos días, ¿cómo te va?",
        linguisticTrait = "Gran riqueza de prefijos de localización en el santuario del Xinolatépetl.",
        description = "Comunidades ubicadas alrededor de la sagrada montaña Xinolatépetl."
    ),
    TotonaVariantInfo(
        id = "to_sureste",
        name = "Totonaco del sureste",
        inliCode = "To-01",
        regionGroup = "Veracruz / Costa",
        states = "Veracruz (Misantla, Yecuatla, Colipa) y frontera con Puebla",
        estimatedSpeakers = "15,000+ hablantes",
        status = "En preservación / Histórica",
        greetingPhrase = "Kintlan tlahkat, ¿kuentlahni?",
        greetingSpanish = "Buenos días / Hola, ¿qué tal?",
        linguisticTrait = "Conocido como Totonaco de Misantla, presenta rasgos fonéticos muy arcaicos.",
        description = "Zona cafetalera con manantiales y vestigios arqueológicos de Los Ídolos."
    ),
    TotonaVariantInfo(
        id = "to_rio_necaxa",
        name = "Totonaco del río Necaxa",
        inliCode = "To-03",
        regionGroup = "Puebla / Necaxa",
        states = "Puebla (Patla, Chicontla, Unión Zihuateutla)",
        estimatedSpeakers = "12,000+ hablantes",
        status = "Vigorosa / Documentada",
        greetingPhrase = "Tlan kgakgsis, ¿kuentlah 'ani?",
        greetingSpanish = "Buenas tardes / Hola, ¿cómo estás?",
        linguisticTrait = "Estudiado extensamente por su compleja estructura ergativa y consonantes uvares.",
        description = "Riberas fértiles del río Necaxa con producción de plátano, café y vainilla."
    )
)

/**
 * Main INPI-Style Modal Dialog for Totonaco (To) Variant Selection
 * Directly replicates the exact layout and data from the INPI screenshot!
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun TotonaVariantSelectionDialog(
    currentVariant: String,
    onVariantSelected: (String) -> Unit,
    onDismiss: () -> Unit,
    viewModel: com.example.ui.MainViewModel? = null
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedVariant by remember {
        mutableStateOf(
            TOTONA_VARIANTS_CATALOG.firstOrNull { it.name == currentVariant }
                ?: TOTONA_VARIANTS_CATALOG.first()
        )
    }
    var showInfographicSheet by remember { mutableStateOf(false) }

    val filteredVariants = remember(searchQuery) {
        TOTONA_VARIANTS_CATALOG.filter { v ->
            searchQuery.isBlank() ||
                    v.name.contains(searchQuery, ignoreCase = true) ||
                    v.states.contains(searchQuery, ignoreCase = true) ||
                    v.inliCode.contains(searchQuery, ignoreCase = true)
        }
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.55f))
                .systemBarsPadding(),
            contentAlignment = Alignment.Center
        ) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth(0.96f)
                    .widthIn(max = 840.dp)
                    .fillMaxHeight(0.95f)
                    .padding(vertical = 8.dp),
                shape = RoundedCornerShape(16.dp),
                color = Color.White
            ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .navigationBarsPadding()
            ) {
                // --- Top Terracotta Header Banner (Exact replica of screenshot header) ---
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFF884422)) // Warm Clay Terracotta Brown from INPI popup
                        .padding(horizontal = 20.dp, vertical = 14.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Totonaco (To)",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )

                        IconButton(
                            onClick = onDismiss,
                            modifier = Modifier.size(28.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Cerrar",
                                tint = Color.White
                            )
                        }
                    }
                }

                // --- Scrollable Content Body with INPI Demographic Statistics ---
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState())
                        .padding(20.dp)
                ) {
                    // Item 1: Familia
                    StatDetailRow(
                        label = "Familia:",
                        value = "Totonaco-tepehua",
                        isHighlight = false
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Item 2: Población
                    Column {
                        Text(
                            text = "Población: 438756",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF222222)
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = Color(0xFFF2F0EB)
                        ) {
                            Text(
                                text = "Población indígena de más de 220 000 hasta 2 millones 886 mil personas",
                                fontSize = 12.5.sp,
                                color = Color(0xFF555555),
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Item 3: Migración
                    Column {
                        Text(
                            text = "Migración: 2",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF222222)
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = Color(0xFFF2F0EB)
                        ) {
                            Text(
                                text = "Migración alta",
                                fontSize = 12.5.sp,
                                color = Color(0xFF555555),
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Item 4: Población Rural
                    Column {
                        Text(
                            text = "Población Rural: 56.64",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF222222)
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = Color(0xFFF2F0EB)
                        ) {
                            Text(
                                text = "Rural - urbano",
                                fontSize = 12.5.sp,
                                color = Color(0xFF555555),
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Item 5: IRE
                    Column {
                        Text(
                            text = "IRE (Índice de Remplazo Etnolingüístico): 0.6855",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF222222)
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = Color(0xFFF2F0EB)
                        ) {
                            Text(
                                text = "Extinción lenta",
                                fontSize = 12.5.sp,
                                color = Color(0xFF555555),
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    // --- Item 6: Variantes: 7 ---
                    Text(
                        text = "Variantes: 7",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF222222)
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Flow of 7 Variant Buttons (matching INPI rounded light gray buttons in screenshot)
                    @OptIn(ExperimentalLayoutApi::class)
                    FlowRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        TOTONA_VARIANTS_CATALOG.forEach { variant ->
                            val isSelected = selectedVariant.id == variant.id
                            val isCurrentActive = currentVariant == variant.name

                            Surface(
                                onClick = { selectedVariant = variant },
                                shape = RoundedCornerShape(10.dp),
                                color = when {
                                    isSelected -> Color(0xFF884422)
                                    isCurrentActive -> Color(0xFF2E7D32)
                                    else -> Color(0xFFF0EFEA)
                                },
                                border = BorderStroke(
                                    width = 1.dp,
                                    color = when {
                                        isSelected -> Color(0xFF663311)
                                        isCurrentActive -> Color(0xFF1B5E20)
                                        else -> Color(0xFFE0DDD5)
                                    }
                                )
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    if (isCurrentActive) {
                                        Icon(
                                            imageVector = Icons.Default.CheckCircle,
                                            contentDescription = null,
                                            tint = Color.White,
                                            modifier = Modifier.size(14.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                    }
                                    Text(
                                        text = variant.name,
                                        fontSize = 12.5.sp,
                                        fontWeight = if (isSelected || isCurrentActive) FontWeight.Bold else FontWeight.Medium,
                                        color = if (isSelected || isCurrentActive) Color.White else Color(0xFF444444)
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    // --- Selected Variant Extended Information Card ---
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFFAFAF7)),
                        border = BorderStroke(1.5.dp, Color(0xFF884422).copy(alpha = 0.4f))
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = selectedVariant.name,
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = Color(0xFF884422)
                                    )
                                    Text(
                                        text = "Código INPI: ${selectedVariant.inliCode} • ${selectedVariant.regionGroup}",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.Gray
                                    )
                                }

                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = Color(0xFFEFEBE0)
                                ) {
                                    Text(
                                        text = selectedVariant.estimatedSpeakers,
                                        fontSize = 10.5.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF884422),
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = "📍 Ubicación: ${selectedVariant.states}",
                                fontSize = 11.5.sp,
                                color = Color.DarkGray
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            // Sample Greeting Pill
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(Color(0xFFFFF8E1), RoundedCornerShape(8.dp))
                                    .border(1.dp, Color(0xFFFFE082), RoundedCornerShape(8.dp))
                                    .padding(10.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = "🗣️ Expresión: \"${selectedVariant.greetingPhrase}\"",
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFF333333)
                                        )
                                        Text(
                                            text = "Español: ${selectedVariant.greetingSpanish}",
                                            fontSize = 11.sp,
                                            color = Color.Gray
                                        )
                                    }
                                    IconButton(
                                        onClick = {
                                            viewModel?.speak(selectedVariant.greetingPhrase, selectedVariant.name)
                                        },
                                        modifier = Modifier.size(36.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.PlayArrow,
                                            contentDescription = "Escuchar pronunciación",
                                            tint = Color(0xFF884422)
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(6.dp))
                            val profile = viewModel?.getVariantPronunciationProfile(selectedVariant.name)
                            if (profile != null) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(Color(0xFFE0F2F1), RoundedCornerShape(6.dp))
                                        .padding(horizontal = 8.dp, vertical = 6.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.VolumeUp,
                                        contentDescription = null,
                                        tint = Color(0xFF00796B),
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Column {
                                        Text(
                                            text = "Ajuste Acústico: ${profile.phoneticLabel}",
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFF00796B)
                                        )
                                        Text(
                                            text = "Fono: Tono ${profile.pitch}x • Vel ${profile.speed}x • ${profile.description}",
                                            fontSize = 9.sp,
                                            color = Color(0xFF004D40)
                                         )
                                     }
                                 }
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = "💡 ${selectedVariant.linguisticTrait}",
                                fontSize = 11.sp,
                                color = Color(0xFF555555)
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            Button(
                                onClick = {
                                    onVariantSelected(selectedVariant.name)
                                    onDismiss()
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(42.dp),
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF884422))
                            ) {
                                Icon(Icons.Default.Check, contentDescription = null, tint = Color.White)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Activar la variante: ${selectedVariant.name}",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                        }
                    }
                }

                // --- Bottom Action Bar with "Infografía" button (matches brown button in screenshot) ---
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFFF5F3ED))
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Button(
                        onClick = { showInfographicSheet = true },
                        shape = RoundedCornerShape(6.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF884422)),
                        contentPadding = PaddingValues(horizontal = 22.dp, vertical = 10.dp)
                    ) {
                        Text(
                            text = "Infografía",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color.White
                        )
                    }

                    Text(
                        text = "Catálogo INPI / INALI",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Gray
                    )
                }
            }
        }
    }
    }

    if (showInfographicSheet) {
        TotonaInfographicModal(onDismiss = { showInfographicSheet = false })
    }
}

@Composable
private fun StatDetailRow(label: String, value: String, isHighlight: Boolean) {
    Column {
        Text(
            text = label,
            fontSize = 15.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color(0xFF222222)
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = value,
            fontSize = 14.sp,
            color = Color(0xFF666666)
        )
    }
}

/**
 * Fullscreen Interactive Infographic Sheet for Totonaco (Tutu Nakú)
 */
@Composable
fun TotonaInfographicModal(onDismiss: () -> Unit) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.65f))
                .systemBarsPadding(),
            contentAlignment = Alignment.Center
        ) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth(0.96f)
                    .widthIn(max = 840.dp)
                    .fillMaxHeight(0.95f)
                    .padding(vertical = 8.dp),
                shape = RoundedCornerShape(20.dp),
                color = Color(0xFF141915) // Deep Totonacapan emerald dark background
            ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "📊 INFOGRAFÍA NACIONAL INPI",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Black,
                            color = Color(0xFF81C784),
                            letterSpacing = 1.5.sp
                        )
                        Text(
                            text = "Totonaco • Tutu Nakú (\"Tres Corazones\")",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.White
                        )
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .size(36.dp)
                            .background(Color.White.copy(alpha = 0.15f), CircleShape)
                    ) {
                        Icon(Icons.Default.Close, contentDescription = "Cerrar", tint = Color.White)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Stats Cards
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    TotonaStatCard(
                        modifier = Modifier.weight(1f),
                        title = "Población",
                        value = "438,756",
                        subtitle = "hablantes en México",
                        badgeColor = Color(0xFF4CAF50)
                    )
                    TotonaStatCard(
                        modifier = Modifier.weight(1f),
                        title = "Variantes",
                        value = "7 Dialectos",
                        subtitle = "reconocidos INPI",
                        badgeColor = Color(0xFFFF9800)
                    )
                    TotonaStatCard(
                        modifier = Modifier.weight(1f),
                        title = "Familia",
                        value = "Totonaco-Tepehua",
                        subtitle = "aislado mesoamericano",
                        badgeColor = Color(0xFF2196F3)
                    )
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Tajin & Voladores Cultural Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1B2A1E)),
                    border = BorderStroke(1.dp, Color(0xFF81C784).copy(alpha = 0.4f))
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(
                            text = "🛕 PATRIMONIO CULTURAL DE EL TAJÍN",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Black,
                            color = Color(0xFF81C784)
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "La cultura totonaca floreció en la majestuosa ciudad arqueológica de El Tajín (Ciudad del Dios del Trueno) con la célebre Pirámide de los Nichos.",
                            fontSize = 11.5.sp,
                            color = Color.LightGray,
                            lineHeight = 16.sp
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        TotonaPillRow(
                            icon = "🦅",
                            title = "Danza de los Voladores",
                            description = "Ritual solar declarado Patrimonio Cultural Inmaterial de la Humanidad por la UNESCO."
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        TotonaPillRow(
                            icon = "🌿",
                            title = "Vainilla de Papantla",
                            description = "Originaria de las selvas del Totonacapan, conocida como Xanath (\"Flor Negra\")."
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Geographic distribution card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF221F28)),
                    border = BorderStroke(1.dp, Color(0xFFFFB74D).copy(alpha = 0.4f))
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(
                            text = "🗺️ DISTRIBUCIÓN TERRITORIAL DEL TOTONACAPAN",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Black,
                            color = Color(0xFFFFB74D)
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "El Totonacapan histórico abarca el norte de Veracruz y la Sierra Norte de Puebla:",
                            fontSize = 11.5.sp,
                            color = Color.LightGray
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "• Veracruz (60%): Papantla, Cazones, Coatzintla, Poza Rica, Espinal, Coyutla, Zozocolco, Misantla.\n" +
                                   "• Puebla (40%): Zacatlán, Huauchinango, Zapotitlán de Méndez, Filomeno Mata, Jalpan, Amixtlán.",
                            fontSize = 11.sp,
                            color = Color.White,
                            lineHeight = 16.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = onDismiss,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50))
                ) {
                    Text("Entendido • Cerrar Infografía", fontWeight = FontWeight.Bold, color = Color.White)
                }
            }
        }
    }
    }
}

@Composable
private fun TotonaStatCard(
    modifier: Modifier = Modifier,
    title: String,
    value: String,
    subtitle: String,
    badgeColor: Color
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E2820))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .background(badgeColor, CircleShape)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(title, fontSize = 9.5.sp, color = Color.Gray, fontWeight = FontWeight.Bold)
            Text(value, fontSize = 12.sp, fontWeight = FontWeight.Black, color = Color.White)
            Text(subtitle, fontSize = 8.5.sp, color = Color.LightGray, textAlign = TextAlign.Center)
        }
    }
}

@Composable
private fun TotonaPillRow(icon: String, title: String, description: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White.copy(alpha = 0.08f), RoundedCornerShape(8.dp))
            .padding(8.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(icon, fontSize = 16.sp)
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Text(title, fontSize = 11.5.sp, fontWeight = FontWeight.Bold, color = Color.White)
                Text(description, fontSize = 10.sp, color = Color.LightGray)
            }
        }
    }
}
