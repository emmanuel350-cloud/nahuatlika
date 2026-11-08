package com.example

import android.app.Application
import android.os.Bundle
import android.widget.Toast
import androidx.fragment.app.FragmentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.launch
import com.example.data.*
import com.example.ui.MainViewModel
import com.example.ui.MainViewModelFactory
import com.example.ui.theme.*
import com.example.ui.MemoryCard
import com.example.ui.QuizQuestion
import com.example.ui.theme.MyApplicationTheme

class MainActivity : FragmentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                // Initialize the MainViewModel using our local factory
                val context = LocalContext.current
                val app = context.applicationContext as Application
                val viewModel: MainViewModel = viewModel(
                    factory = MainViewModelFactory(app)
                )

                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    NahuatlAppMain(viewModel)
                }
            }
        }
    }
}

@Composable
fun NahuatlAppMain(viewModel: MainViewModel) {
    val progress by viewModel.userProgress.collectAsState()
    val context = LocalContext.current
    val hasSeenIntro = progress?.hasSeenIntroStory == true
    var showLanguageSelectionOnboarding by remember { mutableStateOf(false) }
    var showNahuatlVariantCatalogDialog by remember { mutableStateOf(false) }
    var showTotonaVariantCatalogDialog by remember { mutableStateOf(false) }
    var showMayaVariantCatalogDialog by remember { mutableStateOf(false) }
    var showZapotecoVariantCatalogDialog by remember { mutableStateOf(false) }
    var showMixtecoVariantCatalogDialog by remember { mutableStateOf(false) }
    var showOtomiVariantCatalogDialog by remember { mutableStateOf(false) }
    var showInfographicLanguage by remember { mutableStateOf<String?>(null) }

    if (showNahuatlVariantCatalogDialog) {
        com.example.ui.NahuatlVariantSelectionDialog(
            currentVariant = progress?.selectedNahuatlVariant ?: "Náhuatl de la Huasteca veracruzana",
            onVariantSelected = { selectedVariant ->
                viewModel.selectNahuatlVariant(selectedVariant)
                Toast.makeText(context, "Variante activada: $selectedVariant", Toast.LENGTH_SHORT).show()
            },
            onDismiss = { showNahuatlVariantCatalogDialog = false },
            viewModel = viewModel
        )
    }

    if (showTotonaVariantCatalogDialog) {
        com.example.ui.TotonaVariantSelectionDialog(
            currentVariant = progress?.selectedTotonaVariant ?: "Totonaco de la costa",
            onVariantSelected = { selectedVariant ->
                viewModel.selectTotonaVariant(selectedVariant)
                Toast.makeText(context, "Variante activada: $selectedVariant", Toast.LENGTH_SHORT).show()
            },
            onDismiss = { showTotonaVariantCatalogDialog = false },
            viewModel = viewModel
        )
    }

    if (showMayaVariantCatalogDialog) {
        com.example.ui.MayaVariantSelectionDialog(
            currentVariant = progress?.selectedMayaVariant ?: "Maya Yucateco (Maayat'aan)",
            onVariantSelected = { selectedVariant ->
                viewModel.selectMayaVariant(selectedVariant)
                Toast.makeText(context, "Variante activada: $selectedVariant", Toast.LENGTH_SHORT).show()
            },
            onDismiss = { showMayaVariantCatalogDialog = false },
            viewModel = viewModel
        )
    }

    if (showZapotecoVariantCatalogDialog) {
        com.example.ui.ZapotecoVariantSelectionDialog(
            currentVariant = progress?.selectedZapotecoVariant ?: "Zapoteco del Istmo (Diidxazá)",
            onVariantSelected = { selectedVariant ->
                viewModel.selectZapotecoVariant(selectedVariant)
                Toast.makeText(context, "Variante activada: $selectedVariant", Toast.LENGTH_SHORT).show()
            },
            onDismiss = { showZapotecoVariantCatalogDialog = false },
            viewModel = viewModel
        )
    }

    if (showMixtecoVariantCatalogDialog) {
        com.example.ui.MixtecoVariantSelectionDialog(
            currentVariant = progress?.selectedMixtecoVariant ?: "Mixteco de la Mixteca Alta (Tu'un Savi)",
            onVariantSelected = { selectedVariant ->
                viewModel.selectMixtecoVariant(selectedVariant)
                Toast.makeText(context, "Variante activada: $selectedVariant", Toast.LENGTH_SHORT).show()
            },
            onDismiss = { showMixtecoVariantCatalogDialog = false },
            viewModel = viewModel
        )
    }

    if (showOtomiVariantCatalogDialog) {
        com.example.ui.OtomiVariantSelectionDialog(
            currentVariant = progress?.selectedOtomiVariant ?: "Otomí del Valle del Mezquital (Hñähñu)",
            onVariantSelected = { selectedVariant ->
                viewModel.selectOtomiVariant(selectedVariant)
                Toast.makeText(context, "Variante activada: $selectedVariant", Toast.LENGTH_SHORT).show()
            },
            onDismiss = { showOtomiVariantCatalogDialog = false },
            viewModel = viewModel
        )
    }

    showInfographicLanguage?.let { langToDisplay ->
        com.example.ui.LanguageInfographicDialog(
            language = langToDisplay,
            onDismiss = { showInfographicLanguage = null }
        )
    }

    if (viewModel.showDailyStreakDialog) {
        com.example.ui.DailyStreakDialog(
            viewModel = viewModel,
            onDismiss = { viewModel.showDailyStreakDialog = false }
        )
    }

    if (viewModel.showXttsConfigDialog) {
        com.example.ui.XttsConfigDialog(
            viewModel = viewModel,
            onDismiss = { viewModel.showXttsConfigDialog = false }
        )
    }

    val isCodeProtectionActive = viewModel.isCodeProtectionActive

    val activity = context as? androidx.activity.ComponentActivity
    LaunchedEffect(isCodeProtectionActive) {
        activity?.window?.let { window ->
            if (isCodeProtectionActive) {
                window.addFlags(android.view.WindowManager.LayoutParams.FLAG_SECURE)
            } else {
                window.clearFlags(android.view.WindowManager.LayoutParams.FLAG_SECURE)
            }
        }
    }

    LaunchedEffect(viewModel.petSuccessMessage, viewModel.petErrorMessage) {
        viewModel.petSuccessMessage?.let {
            Toast.makeText(context, it, Toast.LENGTH_LONG).show()
            viewModel.petSuccessMessage = null
        }
        viewModel.petErrorMessage?.let {
            Toast.makeText(context, "⚠️ " + it, Toast.LENGTH_LONG).show()
            viewModel.petErrorMessage = null
        }
    }

    LaunchedEffect(viewModel.shieldSavedTrigger) {
        if (viewModel.shieldSavedTrigger) {
            Toast.makeText(context, "🛡️ ¡Tu Escudo Divino te ha protegido de perder una vida!", Toast.LENGTH_LONG).show()
            viewModel.shieldSavedTrigger = false
        }
    }

    if (viewModel.showOutOfLivesDialog) {
        OutOfLivesDialog(viewModel)
    }

    val isBiometricLockEnabled = progress?.isBiometricLockEnabled == true
    if (isBiometricLockEnabled && !viewModel.isAppUnlocked) {
        BiometricLockScreen(viewModel = viewModel)
    } else if (progress != null && !hasSeenIntro) {
        com.example.ui.StoryModeScreen(
            viewModel = viewModel,
            onDismiss = {
                viewModel.markIntroStoryAsSeen()
            }
        )
    } else if (showLanguageSelectionOnboarding) {
        FullScreenLanguageMapSelectionScreen(
            viewModel = viewModel,
            onOpenNahuatlVariantCatalog = {
                val currentLang = progress?.selectedLanguage ?: "Nahuatl"
                when (currentLang) {
                    "Maya" -> showMayaVariantCatalogDialog = true
                    "Zapoteco" -> showZapotecoVariantCatalogDialog = true
                    "Mixteco" -> showMixtecoVariantCatalogDialog = true
                    "Otomi" -> showOtomiVariantCatalogDialog = true
                    else -> showNahuatlVariantCatalogDialog = true
                }
            },
            onOpenTotonaVariantCatalog = { showTotonaVariantCatalogDialog = true },
            onOpenLanguageInfographic = { lang -> showInfographicLanguage = lang },
            onDismiss = { showLanguageSelectionOnboarding = false },
            onPlayStory = {}
        )
    } else {
        val showFullScreen = viewModel.activeLesson != null || viewModel.lessonRitualActive || viewModel.selectedGameLevel != null || viewModel.showAiOracleChat
        if (showFullScreen) {
            if (viewModel.showAiOracleChat) {
                com.example.ui.AiOracleScreen(viewModel = viewModel)
            } else {
                FullScreenChallengeContainer(viewModel = viewModel)
            }
        } else {
            Scaffold(
            modifier = Modifier.fillMaxSize().testTag("app_scaffold"),
            bottomBar = {
            NavigationBar(
                modifier = Modifier
                    .navigationBarsPadding()
                    .testTag("app_navigation_bar"),
                containerColor = Color(0xFF161A1E),
                tonalElevation = 8.dp
            ) {
                val activeLanguage = progress?.selectedLanguage ?: "Nahuatl"
                val items = listOf(
                    Triple(0, if (activeLanguage == "Maya") "Xook (Lecciones)" else "Lecciones", Icons.Default.MenuBook),
                    Triple(1, if (activeLanguage == "Maya") "Sut t'aan (Traducción)" else "Traducción", Icons.Default.Translate),
                    Triple(2, if (activeLanguage == "Maya") "Báaxalo'ob (Juegos)" else "Juegos", Icons.Default.SportsEsports),
                    Triple(4, if (activeLanguage == "Maya") "Alak' (Mascota)" else "Mascota", Icons.Default.Pets),
                    Triple(5, if (activeLanguage == "Maya") "K'ujna (Templo)" else "Templo", Icons.Default.AccountBalance),
                    Triple(3, if (activeLanguage == "Maya") "Wíinik (Perfil)" else "Perfil", Icons.Default.AccountCircle)
                )
                items.forEach { (index, label, icon) ->
                    val tabAccentColor = when (index) {
                        0 -> Color(0xFFFFB74D) // Lecciones - Warm Gold
                        1 -> Color(0xFF26A69A) // Traducción - Jade Teal
                        2 -> Color(0xFFFF4081) // Juegos - Tenango Magenta
                        4 -> Color(0xFFAB47BC) // Mascota - Violet Purple
                        5 -> Color(0xFFFF5252) // Templo - Ruby Red
                        3 -> Color(0xFF00E5FF) // Perfil - Maya Turquoise
                        else -> Color(0xFFFFB74D)
                    }
                    NavigationBarItem(
                        modifier = Modifier.testTag("nav_tab_$index"),
                        selected = viewModel.currentTab == index,
                        onClick = { 
                            viewModel.currentTab = index 
                            viewModel.refreshLivesAndPetStatus()
                        },
                        icon = { Icon(icon, contentDescription = label) },
                        label = { Text(label, fontSize = 11.sp, fontWeight = FontWeight.ExtraBold) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = tabAccentColor,
                            selectedTextColor = tabAccentColor,
                            indicatorColor = tabAccentColor.copy(alpha = 0.22f),
                            unselectedIconColor = Color.LightGray.copy(alpha = 0.6f),
                            unselectedTextColor = Color.LightGray.copy(alpha = 0.6f)
                        )
                    )
                }
            }
        }
    ) { innerPadding ->
        val activeLanguage = progress?.selectedLanguage ?: "Nahuatl"
        val bgGradient = when (activeLanguage) {
            "Otomi" -> listOf(Color(0xFF2A0826), Color(0xFF17091D), Color(0xFF100C14))
            "Totona" -> listOf(Color(0xFF221A06), Color(0xFF14190C), Color(0xFF0E130D))
            "Maya" -> listOf(Color(0xFF041E2B), Color(0xFF061422), Color(0xFF0A1118))
            "Zapoteco" -> listOf(Color(0xFF2E0909), Color(0xFF1C0A0A), Color(0xFF110C0C))
            "Mixteco" -> listOf(Color(0xFF22082E), Color(0xFF160A21), Color(0xFF0D0B14))
            else -> listOf(Color(0xFF0B1E13), Color(0xFF0C1610), Color(0xFF101316))
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(Brush.verticalGradient(colors = bgGradient))
        ) {
            // Header Hero Banner Area
            HeaderBannerSection(
                progress = progress,
                onPracticeCheckIn = {
                    viewModel.performDailyCheckIn()
                    Toast.makeText(context, "¡Asistencia tomada! +20 XP ganados.", Toast.LENGTH_SHORT).show()
                },
                hasFiredPractice = viewModel.hasFiredPracticeNotificationForToday,
                onOpenLanguageLauncher = { showLanguageSelectionOnboarding = true },
                onOpenNahuatlVariantCatalog = {
                    val currentLang = progress?.selectedLanguage ?: "Nahuatl"
                    when (currentLang) {
                        "Maya" -> showMayaVariantCatalogDialog = true
                        "Zapoteco" -> showZapotecoVariantCatalogDialog = true
                        "Mixteco" -> showMixtecoVariantCatalogDialog = true
                        "Otomi" -> showOtomiVariantCatalogDialog = true
                        else -> showNahuatlVariantCatalogDialog = true
                    }
                },
                onOpenTotonaVariantCatalog = { showTotonaVariantCatalogDialog = true },
                onOpenLanguageInfographic = { lang -> showInfographicLanguage = lang },
                onTestVoice = {
                    val currentLang = progress?.selectedLanguage ?: "Nahuatl"
                    val greeting = when (currentLang) {
                        "Totona" -> "Tlan tlan, tlan katsatzi"
                        "Otomi" -> "Ki hats'i, ki xudi"
                        "Maya" -> "Ba'ax ka wa'alik, ma'alob k'iin"
                        "Zapoteco" -> "Padiuxi, gubidxa"
                        "Mixteco" -> "Nani kuun, yandavi"
                        else -> "Niltze, cualli tonalli"
                    }
                    viewModel.speak(greeting)
                    Toast.makeText(context, "🔊 Reproduciendo voz ancestral: \"$greeting\"", Toast.LENGTH_SHORT).show()
                },
                onOpenStreakTracker = { viewModel.showDailyStreakDialog = true }
            )


            // Lives, Shields & Mascot Status Strip
            LivesAndPetStatusStrip(viewModel)

            // Dynamic Tab Views
            Box(modifier = Modifier.fillMaxSize().weight(1f)) {
                when (viewModel.currentTab) {
                    0 -> LessonsTab(viewModel)
                    1 -> TranslatorTab(viewModel)
                    2 -> GamesTab(viewModel)
                    3 -> ProgressTab(viewModel)
                    4 -> MascotTab(viewModel)
                    5 -> TempleTab(viewModel)
                }
            }
        }
    }
    }
}
}

// --- Common UI Components ---

@Composable
fun HeaderBannerSection(
    progress: UserProgress?,
    onPracticeCheckIn: () -> Unit,
    hasFiredPractice: Boolean,
    onOpenLanguageLauncher: () -> Unit,
    onOpenNahuatlVariantCatalog: () -> Unit = {},
    onOpenTotonaVariantCatalog: () -> Unit = {},
    onOpenLanguageInfographic: (String) -> Unit = {},
    onTestVoice: () -> Unit = {},
    onOpenStreakTracker: () -> Unit = {}
) {
    val currentLang = progress?.selectedLanguage ?: "Nahuatl"
    
    val borderGlowColor = when (currentLang) {
        "Totona" -> Color(0xFFFFB74D)
        else -> Color(0xFFC6FF00)
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        border = BorderStroke(2.dp, borderGlowColor.copy(alpha = 0.6f))
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(125.dp)
        ) {
            val bannerPainter = when (currentLang) {
                "Totona" -> painterResource(id = R.drawable.img_totona_banner_1782942437048)
                else -> painterResource(id = R.drawable.img_nahuatl_banner_1782166353631)
            }
            Image(
                painter = bannerPainter,
                contentDescription = "Ancestral Native Motif",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )

            // Translucent Dark Tint Layer for High Contrast
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.55f))
            )

            // Content Overlay
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    val tagText = when (currentLang) {
                        "Totona" -> "🦜 XANAT TOTONACO"
                        else -> "🌿 AMOXTIN NĀHUATL"
                    }
                    val titleText = when (currentLang) {
                        "Totona" -> "La Voz de la Vainilla"
                        else -> "El Sendero del Habla"
                    }
                    val subtitleText = when (currentLang) {
                        "Totona" -> "El Tajín, Papantla y el canto de pájaro"
                        else -> "Templo Mayor, Anáhuac y tradición nahua"
                    }

                    Text(
                        text = tagText,
                        color = borderGlowColor,
                        fontSize = 10.5.sp,
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = 1.2.sp
                    )
                    Spacer(modifier = Modifier.height(1.dp))
                    Text(
                        text = titleText,
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = subtitleText,
                        color = Color.White.copy(alpha = 0.85f),
                        fontSize = 10.5.sp,
                        overflow = TextOverflow.Ellipsis,
                        maxLines = 1
                    )

                    // Variant and Daily Streak selector pills
                    val (activeVariant, catalogAction) = when (currentLang) {
                        "Totona" -> Pair(progress?.selectedTotonaVariant ?: "Totonaco de la costa", onOpenTotonaVariantCatalog)
                        "Maya" -> Pair(progress?.selectedMayaVariant ?: "Maya Yucateco (Maayat'aan)", onOpenNahuatlVariantCatalog)
                        "Zapoteco" -> Pair(progress?.selectedZapotecoVariant ?: "Zapoteco del Istmo (Diidxazá)", onOpenNahuatlVariantCatalog)
                        "Mixteco" -> Pair(progress?.selectedMixtecoVariant ?: "Mixteco de la Mixteca Alta (Tu'un Savi)", onOpenNahuatlVariantCatalog)
                        "Otomi" -> Pair(progress?.selectedOtomiVariant ?: "Otomí del Valle del Mezquital (Hñähñu)", onOpenNahuatlVariantCatalog)
                        else -> Pair(progress?.selectedNahuatlVariant ?: "Náhuatl de la Huasteca veracruzana", onOpenNahuatlVariantCatalog)
                    }

                    Row(
                        modifier = Modifier.padding(top = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Surface(
                            onClick = { catalogAction() },
                            shape = RoundedCornerShape(8.dp),
                            color = borderGlowColor.copy(alpha = 0.35f),
                            border = BorderStroke(1.dp, borderGlowColor)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 7.dp, vertical = 2.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Tune,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(10.dp)
                                )
                                Spacer(modifier = Modifier.width(3.dp))
                                Text(
                                    text = activeVariant,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Color.White,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }

                        // Daily Streak Tracker Pill
                        Surface(
                            onClick = { onOpenStreakTracker() },
                            shape = RoundedCornerShape(8.dp),
                            color = Color(0xFFFF9800).copy(alpha = 0.35f),
                            border = BorderStroke(1.dp, Color(0xFFFF9800)),
                            modifier = Modifier.testTag("header_streak_pill")
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 7.dp, vertical = 2.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Whatshot,
                                    contentDescription = "Racha",
                                    tint = Color(0xFFFFD54F),
                                    modifier = Modifier.size(11.dp)
                                )
                                Spacer(modifier = Modifier.width(3.dp))
                                Text(
                                    text = "${progress?.streakDays ?: 1}d Racha",
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Color.White
                                )
                            }
                        }
                    }
                }


                // Action buttons on the right side of banner
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    // Test Voice button
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        IconButton(
                            onClick = { onTestVoice() },
                            modifier = Modifier
                                .size(34.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF29B6F6).copy(alpha = 0.35f))
                                .testTag("test_voice_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.VolumeUp,
                                contentDescription = "Escuchar Voz",
                                tint = Color.White,
                                modifier = Modifier.size(17.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Voz",
                            color = Color.White,
                            fontSize = 8.5.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    // Open Infographic dialog button
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        IconButton(
                            onClick = { onOpenLanguageInfographic(currentLang) },
                            modifier = Modifier
                                .size(34.dp)
                                .clip(CircleShape)
                                .background(borderGlowColor.copy(alpha = 0.3f))
                        ) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = "Infografía Cultural",
                                tint = Color.White,
                                modifier = Modifier.size(17.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Info",
                            color = Color.White,
                            fontSize = 8.5.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    // Open interactive map button
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        IconButton(
                            onClick = { onOpenLanguageLauncher() },
                            modifier = Modifier
                                .size(34.dp)
                                .clip(CircleShape)
                                .background(Color.White.copy(alpha = 0.25f))
                        ) {
                            Icon(
                                imageVector = Icons.Default.Map,
                                contentDescription = "Cambiar lengua",
                                tint = Color.White,
                                modifier = Modifier.size(17.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Mapa",
                            color = Color.White,
                            fontSize = 8.5.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    // Quick Practice reminder / claim streak points
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        IconButton(
                            onClick = { if (!hasFiredPractice) onPracticeCheckIn() },
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(
                                    if (hasFiredPractice) MaterialTheme.colorScheme.tertiary
                                    else MaterialTheme.colorScheme.primary
                                )
                        ) {
                            Icon(
                                imageVector = if (hasFiredPractice) Icons.Default.Check else Icons.Default.CalendarToday,
                                contentDescription = "Check-in diario",
                                tint = if (hasFiredPractice) Color.White else MaterialTheme.colorScheme.onPrimary,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = if (hasFiredPractice) "Completado" else "Practicar",
                            color = Color.White,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}


// --- Interactive Map of Mexico for Language Selection ---

@Composable
fun InteractiveMexicoMap(viewModel: MainViewModel) {
    val progress by viewModel.userProgress.collectAsState()
    val activeLang = progress?.selectedLanguage ?: "Nahuatl"
    var expanded by remember { mutableStateOf(false) }
    
    val regions = remember {
        listOf(
            RegionData("Centro", "Nahuatl", "Niltze 🌿", "Central", "Náhuatl clásico y moderno. Hablado por más de 1.7 millones de personas en Puebla, Veracruz e Hidalgo.", Color(0xFF84E02C), Icons.Default.MenuBook),
            RegionData("Veracruz", "Totona", "Tlan tlan 🦜", "El Tajín", "Idioma Totonaco (Totona) de Papantla y El Tajín con gran riqueza aromática y musical.", Color(0xFFFF9800), Icons.Default.MusicNote)
        )
    }
    
    var tappedRegion by remember { mutableStateOf<RegionData?>(null) }
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
            .testTag("mexico_map_container"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.2f))
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { expanded = !expanded }
                    .padding(vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    val icon = when (activeLang) {
                        "Maya" -> "🌅"
                        "Zapoteco" -> "🏔️"
                        "Mixteco" -> "🏹"
                        "Otomi" -> "🌸"
                        "Totona" -> "🦜"
                        else -> "🌿"
                    }
                    Text(icon, fontSize = 20.sp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text("Lengua de Estudio Activa", fontSize = 10.sp, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                        Text(
                            text = if (activeLang == "Totona") "Totonaco (Totona)" else "Náhuatl Central",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
                
                Button(
                    onClick = { expanded = !expanded },
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f), 
                        contentColor = MaterialTheme.colorScheme.primary
                    ),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(if (expanded) "Ocultar Mapa 🗺️" else "Cambiar Lengua 🗺️", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
            
            AnimatedVisibility(visible = expanded) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "Mapa de Lenguas de México",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        "Selecciona un punto en el mapa para activar y aprender una nueva lengua nacional:",
                        fontSize = 10.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f),
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp)
                            .background(Color.Black.copy(alpha = 0.05f), RoundedCornerShape(12.dp))
                            .clip(RoundedCornerShape(12.dp))
                    ) {
                        val infiniteTransition = rememberInfiniteTransition()
                        val pulseScale by infiniteTransition.animateFloat(
                            initialValue = 10f,
                            targetValue = 45f,
                            animationSpec = infiniteRepeatable(
                                animation = tween(1200, easing = LinearEasing),
                                repeatMode = RepeatMode.Restart
                            )
                        )
                        val pulseAlpha by infiniteTransition.animateFloat(
                            initialValue = 0.8f,
                            targetValue = 0f,
                            animationSpec = infiniteRepeatable(
                                animation = tween(1200, easing = LinearEasing),
                                repeatMode = RepeatMode.Restart
                            )
                        )
                        
                        Canvas(
                            modifier = Modifier
                                .fillMaxSize()
                                .pointerInput(Unit) {
                                    detectTapGestures { offset ->
                                        val width = size.width
                                        val region = if (offset.x >= width * 0.60f) regions.find { it.key == "Totona" } else regions.find { it.key == "Nahuatl" }
                                        if (region != null) {
                                            tappedRegion = region
                                        }
                                    }
                                }
                        ) {
                            val width = size.width
                            val height = size.height
                            
                            val borderPath = Path().apply {
                                moveTo(width * 0.15f, height * 0.15f)
                                quadraticTo(width * 0.3f, height * 0.12f, width * 0.45f, height * 0.25f)
                                lineTo(width * 0.52f, height * 0.38f)
                                lineTo(width * 0.65f, height * 0.45f)
                                quadraticTo(width * 0.8f, height * 0.4f, width * 0.9f, height * 0.38f)
                                lineTo(width * 0.95f, height * 0.52f)
                                lineTo(width * 0.8f, height * 0.6f)
                                lineTo(width * 0.75f, height * 0.72f)
                                lineTo(width * 0.6f, height * 0.76f)
                                lineTo(width * 0.52f, height * 0.62f)
                                lineTo(width * 0.42f, height * 0.58f)
                                lineTo(width * 0.22f, height * 0.55f)
                                lineTo(width * 0.12f, height * 0.35f)
                                close()
                            }
                            
                            drawPath(
                                path = borderPath,
                                color = Color.Gray.copy(alpha = 0.15f),
                                style = Fill
                            )
                            drawPath(
                                path = borderPath,
                                color = Color.Gray.copy(alpha = 0.3f),
                                style = Stroke(width = 2.dp.toPx())
                            )
                            
                            regions.forEach { reg ->
                                val (hx, hy) = when (reg.key) {
                                    "Totona" -> Pair(width * 0.68f, height * 0.42f)
                                    else -> Pair(width * 0.58f, height * 0.50f)
                                }
                                
                                val isSelected = activeLang == reg.key
                                val isTapped = tappedRegion?.key == reg.key
                                
                                if (isSelected || isTapped) {
                                    drawCircle(
                                        color = reg.color.copy(alpha = pulseAlpha),
                                        radius = pulseScale * 1.5f,
                                        center = Offset(hx, hy)
                                    )
                                }
                                
                                drawCircle(
                                    color = reg.color,
                                    radius = if (isSelected) 8.dp.toPx() else 5.dp.toPx(),
                                    center = Offset(hx, hy)
                                )
                                drawCircle(
                                    color = Color.White,
                                    radius = if (isSelected) 3.dp.toPx() else 2.dp.toPx(),
                                    center = Offset(hx, hy)
                                )
                            }
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    val displayRegion = tappedRegion ?: regions.find { it.key == activeLang } ?: regions.first()
                    
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        border = BorderStroke(1.dp, displayRegion.color.copy(alpha = 0.4f))
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        displayRegion.icon, 
                                        contentDescription = null, 
                                        tint = displayRegion.color,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = if (displayRegion.key == "Totona") "Totonaco / Totona (Golfo - El Tajín)" else "Náhuatl Central (Centro-Este)",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }
                                if (activeLang == displayRegion.key) {
                                    Box(
                                        modifier = Modifier
                                            .background(Color(0xFF4CAF50).copy(alpha = 0.15f), RoundedCornerShape(6.dp))
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Text("ACTIVO", color = Color(0xFF4CAF50), fontSize = 8.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                            
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(displayRegion.details, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f))
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("Frase Clave: ", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                                Text("\"${displayRegion.greet}\"", fontSize = 11.sp, fontStyle = FontStyle.Italic, color = MaterialTheme.colorScheme.onSurface)
                            }
                            
                            Spacer(modifier = Modifier.height(8.dp))
                            
                            if (activeLang != displayRegion.key) {
                                Button(
                                    onClick = {
                                        viewModel.selectLanguage(displayRegion.key)
                                    },
                                    modifier = Modifier.fillMaxWidth().height(36.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = displayRegion.color),
                                    shape = RoundedCornerShape(8.dp),
                                    contentPadding = PaddingValues(0.dp)
                                ) {
                                    Text("Activar y Aprender ${displayRegion.key} 🚀", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

data class RegionData(
    val name: String,
    val key: String,
    val greet: String,
    val geo: String,
    val details: String,
    val color: Color,
    val icon: androidx.compose.ui.graphics.vector.ImageVector
)

@Composable
fun FullScreenLanguageMapSelectionScreen(
    viewModel: MainViewModel,
    onOpenNahuatlVariantCatalog: () -> Unit = {},
    onOpenTotonaVariantCatalog: () -> Unit = {},
    onOpenLanguageInfographic: (String) -> Unit = {},
    onDismiss: () -> Unit,
    onPlayStory: () -> Unit = {}
) {
    val progress by viewModel.userProgress.collectAsState()
    val activeLang = progress?.selectedLanguage ?: "Nahuatl"
    
    val regions = remember {
        listOf(
            RegionData("Valle de México", "Nahuatl", "Niltze 🌿", "Central", "Náhuatl clásico y moderno. El idioma mexica del gran Imperio Azteca y la Huasteca.", Color(0xFF84E02C), Icons.Default.MenuBook),
            RegionData("Papantla Veracruz", "Totona", "Tlan tlan 🦜", "El Tajín", "Idioma Totonaco de Papantla y El Tajín con gran riqueza aromática y la Leyenda de la Vainilla.", Color(0xFFE65100), Icons.Default.MusicNote)
        )
    }
    
    var selectedRegion by remember { mutableStateOf<RegionData?>(regions.firstOrNull { it.key == activeLang } ?: regions.first()) }
    
    val infiniteTransition = rememberInfiniteTransition(label = "map_selection")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 10f,
        targetValue = 40f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "pulse_scale"
    )
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.8f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "pulse_alpha"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0F0F14)),
        contentAlignment = Alignment.TopCenter
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .widthIn(max = 840.dp)
                .verticalScroll(rememberScrollState())
                .statusBarsPadding()
                .navigationBarsPadding()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
        // App Identity Header
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = "🌎 MÉXICO ANCESTRAL 🌎",
            fontSize = 22.sp,
            fontWeight = FontWeight.Black,
            color = Color(0xFFFFB74D),
            letterSpacing = 3.sp,
            textAlign = TextAlign.Center
        )
        Text(
            text = "MAPA INTERACTIVO DE LENGUAS NACIONALES",
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = Color.LightGray,
            letterSpacing = 1.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 4.dp, bottom = 12.dp)
        )
        
        Text(
            text = "Selecciona una región del mapa o un idioma para activar su interfaz adaptada de aprendizaje y explorar sus conocimientos ancestrales:",
            fontSize = 12.sp,
            color = Color.LightGray.copy(alpha = 0.85f),
            textAlign = TextAlign.Center,
            lineHeight = 17.sp,
            modifier = Modifier.padding(horizontal = 8.dp)
        )

        FilledTonalButton(
            onClick = onPlayStory,
            colors = ButtonDefaults.filledTonalButtonColors(
                containerColor = Color(0x33FFB74D),
                contentColor = Color(0xFFFFB74D)
            ),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .padding(top = 8.dp)
                .testTag("play_story_button")
        ) {
            Text("📖 Ver Historia de la Huaxlica", fontSize = 12.sp, fontWeight = FontWeight.Bold)
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Interactive Map Canvas
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(210.dp)
                .background(Color.Black.copy(alpha = 0.3f), RoundedCornerShape(20.dp))
                .border(2.dp, Color(0xFFFFB74D).copy(alpha = 0.4f), RoundedCornerShape(20.dp))
                .clip(RoundedCornerShape(20.dp))
        ) {
            Canvas(
                modifier = Modifier
                    .fillMaxSize()
                    .pointerInput(Unit) {
                        detectTapGestures { offset ->
                            val width = size.width
                            val tapped = when {
                                offset.x >= width * 0.65f -> regions.find { it.key == "Totona" }
                                else -> regions.find { it.key == "Nahuatl" }
                            }
                            if (tapped != null) {
                                selectedRegion = tapped
                                viewModel.selectLanguage(tapped.key)
                            }
                        }
                    }
            ) {
                val width = size.width
                val height = size.height
                
                // Draw a stylized vector contour outline of Mexico Map
                val borderPath = Path().apply {
                    moveTo(width * 0.15f, height * 0.20f)
                    quadraticTo(width * 0.35f, height * 0.15f, width * 0.48f, height * 0.28f)
                    lineTo(width * 0.55f, height * 0.40f)
                    lineTo(width * 0.68f, height * 0.45f)
                    quadraticTo(width * 0.82f, height * 0.40f, width * 0.94f, height * 0.38f)
                    lineTo(width * 0.96f, height * 0.52f)
                    lineTo(width * 0.82f, height * 0.62f)
                    lineTo(width * 0.77f, height * 0.74f)
                    lineTo(width * 0.62f, height * 0.78f)
                    lineTo(width * 0.54f, height * 0.64f)
                    lineTo(width * 0.44f, height * 0.60f)
                    lineTo(width * 0.24f, height * 0.58f)
                    lineTo(width * 0.14f, height * 0.38f)
                    close()
                }
                
                drawPath(
                    path = borderPath,
                    color = Color(0xFF1E1E24),
                    style = Fill
                )
                drawPath(
                    path = borderPath,
                    color = Color(0xFFFFB74D).copy(alpha = 0.5f),
                    style = Stroke(width = 2.dp.toPx())
                )
                
                regions.forEach { reg ->
                    val (hx, hy) = when (reg.key) {
                        "Totona" -> Pair(width * 0.68f, height * 0.42f)
                        else -> Pair(width * 0.58f, height * 0.48f)
                    }
                    
                    val isSelected = selectedRegion?.key == reg.key
                    
                    if (isSelected) {
                        drawCircle(
                            color = reg.color.copy(alpha = pulseAlpha),
                            radius = pulseScale * 1.5f,
                            center = Offset(hx, hy)
                        )
                    }
                    
                    drawCircle(
                        color = reg.color,
                        radius = if (isSelected) 9.dp.toPx() else 6.dp.toPx(),
                        center = Offset(hx, hy)
                    )
                    drawCircle(
                        color = Color.White,
                        radius = if (isSelected) 3.5f.dp.toPx() else 2.5f.dp.toPx(),
                        center = Offset(hx, hy)
                    )
                }
            }
        }
        
        Spacer(modifier = Modifier.height(14.dp))
        
        // List of Languages to Select Directly via Cards (Horizontal scrollable Row)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            regions.forEach { reg ->
                val isSelected = selectedRegion?.key == reg.key
                val displayName = when (reg.key) {
                    "Totona" -> "🦜 Totonaco"
                    else -> "🌿 Náhuatl"
                }
                Card(
                    modifier = Modifier
                        .width(130.dp)
                        .clickable {
                            selectedRegion = reg
                            viewModel.selectLanguage(reg.key)
                        },
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isSelected) reg.color.copy(alpha = 0.25f) else Color(0xFF1E1E24)
                    ),
                    border = BorderStroke(
                        width = if (isSelected) 2.dp else 1.dp,
                        color = if (isSelected) reg.color else Color.Gray.copy(alpha = 0.2f)
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(10.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = displayName,
                            fontSize = 12.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isSelected) Color.White else Color.Gray
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(reg.geo, fontSize = 8.5.sp, color = Color.LightGray.copy(alpha = 0.6f))
                    }
                }
            }
        }
        
        Spacer(modifier = Modifier.height(14.dp))
        
        // Selected Language Theme Preview & Details Card
        selectedRegion?.let { reg ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E24)),
                border = BorderStroke(1.dp, reg.color.copy(alpha = 0.3f))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Column {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = when (reg.key) {
                                    "Totona" -> "🦜 LENGUA TOTONACA (XANAT)"
                                    else -> "🌿 LENGUA NÁHUATL (MEXICATL)"
                                },
                                fontSize = 14.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = reg.color
                            )
                            
                            Box(
                                modifier = Modifier
                                    .background(reg.color.copy(alpha = 0.2f), RoundedCornerShape(6.dp))
                                    .padding(horizontal = 6.dp, vertical = 3.dp)
                            ) {
                                Text(
                                    text = reg.greet,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                        }
                        
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = reg.details,
                            fontSize = 12.sp,
                            color = Color.LightGray,
                            lineHeight = 17.sp
                        )
                        
                        Spacer(modifier = Modifier.height(12.dp))
                        
                        // Theme adaptation preview box
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color.Black.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
                                .border(1.dp, reg.color.copy(alpha = 0.2f), RoundedCornerShape(12.dp))
                                .padding(12.dp)
                        ) {
                            Column {
                                Text(
                                    text = "✨ VISTA PREVIA DE INTERFAZ adaptada:",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = reg.color
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    val (previewIcon, previewText, previewDesc) = when (reg.key) {
                                        "Totona" -> Triple("🦜", "Zona de El Tajín y Papantla", "Diseño con tonos verdes selváticos y amarillos de orquídeas; lecciones mnemotécnicas con aroma a vainilla silvestre.")
                                        else -> Triple("🐍", "El Gran Templo Mayor de Tenochtitlan", "Gradients terrosos y fuego sagrado con ofrendas rituales prehispánicas de Xoloitzcuintli.")
                                    }
                                    Text(previewIcon, fontSize = 20.sp)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Column {
                                        Text(previewText, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                        Text(previewDesc, fontSize = 10.sp, color = Color.Gray, lineHeight = 13.sp)
                                    }
                                }
                            }
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(14.dp))

                    // Variant Catalog Button
                    val (activeVariant, catalogAction) = when (reg.key) {
                        "Totona" -> Pair(progress?.selectedTotonaVariant ?: "Totonaco de la costa", onOpenTotonaVariantCatalog)
                        "Maya" -> Pair(progress?.selectedMayaVariant ?: "Maya Yucateco (Maayat'aan)", onOpenNahuatlVariantCatalog)
                        "Zapoteco" -> Pair(progress?.selectedZapotecoVariant ?: "Zapoteco del Istmo (Diidxazá)", onOpenNahuatlVariantCatalog)
                        "Mixteco" -> Pair(progress?.selectedMixtecoVariant ?: "Mixteco de la Mixteca Alta (Tu'un Savi)", onOpenNahuatlVariantCatalog)
                        "Otomi" -> Pair(progress?.selectedOtomiVariant ?: "Otomí del Valle del Mezquital (Hñähñu)", onOpenNahuatlVariantCatalog)
                        else -> Pair(progress?.selectedNahuatlVariant ?: "Náhuatl de la Huasteca veracruzana", onOpenNahuatlVariantCatalog)
                    }

                    OutlinedButton(
                        onClick = { catalogAction() },
                        border = BorderStroke(1.5.dp, reg.color),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(44.dp)
                            .padding(bottom = 8.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            containerColor = reg.color.copy(alpha = 0.15f)
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.Tune,
                            contentDescription = null,
                            tint = reg.color,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Catálogo INPI: $activeVariant ▾",
                            fontSize = 11.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = reg.color,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    // Infographic Button
                    OutlinedButton(
                        onClick = { onOpenLanguageInfographic(reg.key) },
                        border = BorderStroke(1.5.dp, Color(0xFFFFB74D)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(44.dp)
                            .padding(bottom = 8.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            containerColor = Color(0xFFFFB74D).copy(alpha = 0.15f)
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = null,
                            tint = Color(0xFFFFB74D),
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "📊 Ver Infografía Cultural & Lingüística",
                            fontSize = 11.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFFFB74D)
                        )
                    }

                    Button(
                        onClick = {
                            viewModel.selectLanguage(reg.key)
                            onDismiss()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = reg.color),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = "Entrar al Inicio en ${reg.key} 🚀",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.White
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(36.dp))
    }
    }
}


// --- Tab 1: Lessons System with 500 Lessons Pathways ---

@Composable
fun LessonsTab(viewModel: MainViewModel) {
    val lessons by viewModel.allLessons.collectAsState()
    val progress by viewModel.userProgress.collectAsState()
    val activeLang = progress?.selectedLanguage ?: "Nahuatl"
    
    var viewMode by remember { mutableStateOf("Map") } // "Map" or "List"
    var selectedLanguageSet by remember { mutableStateOf("Destino") } // "Destino" (Indigenous) or "Nativa" (Español)
    var activeDomainFilter by remember { mutableStateOf("Todos") } // "Todos", "Vocabulario", "Frases", "Conversación", "Cultura", "Gramática"

    var showLanguageDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 8.dp)
    ) {
        // --- 1. Top Controls Bar (No Map of Mexico! Simple Buttons only) ---
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
            shape = RoundedCornerShape(12.dp),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.15f))
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Language Selection Info / Trigger Button
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.clickable { showLanguageDialog = true }
                ) {
                    Text(if (activeLang == "Otomi") "🌸" else if (activeLang == "Totona") "🦜" else "🌿", fontSize = 20.sp)
                    Spacer(modifier = Modifier.width(6.dp))
                    Column {
                        Text("LENGUA ACTIVA", fontSize = 9.sp, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Black)
                        Text(
                            text = if (activeLang == "Otomi") "Otomí (Hñähñu)" else if (activeLang == "Totona") "Totonaco (Totona)" else "Náhuatl Central",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }

                // In-place Quick Switch Button
                Button(
                    onClick = { showLanguageDialog = true },
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f), contentColor = MaterialTheme.colorScheme.primary),
                    modifier = Modifier.testTag("change_lang_in_place_btn")
                ) {
                    Icon(Icons.Default.Language, contentDescription = "Cambiar", modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Cambiar", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        // --- 2. Independent Language Sets Selector (Indigenous vs Spanish) ---
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            val sets = listOf(
                Pair("Destino", "Lengua Nativa (" + (if (activeLang == "Otomi") "Otomí" else if (activeLang == "Totona") "Totonaco" else "Náhuatl") + ")"),
                Pair("Nativa", "Lengua de Dominio / Dominó (Español)")
            )
            sets.forEach { (setKey, label) ->
                val isSelected = selectedLanguageSet == setKey
                Card(
                    onClick = { 
                        selectedLanguageSet = setKey
                        activeDomainFilter = "Todos"
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(36.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface
                    ),
                    border = BorderStroke(1.dp, if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
                ) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(
                            text = label,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(horizontal = 4.dp)
                        )
                    }
                }
            }
        }

        // --- 3. View Mode Toggle (Map vs List) ---
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                listOf(
                    Pair("Map", "🗺️ Mapa Interactivo"),
                    Pair("List", "📋 Lista Clasificada")
                ).forEach { (mode, label) ->
                    val isSelected = viewMode == mode
                    FilterChip(
                        selected = isSelected,
                        onClick = { viewMode = mode },
                        label = { Text(label, fontSize = 10.sp, fontWeight = FontWeight.Bold) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),
                            selectedLabelColor = MaterialTheme.colorScheme.primary
                        )
                    )
                }
            }

            UnitSelectorDropDown(viewModel)
        }

        // --- 4. Render Selected View Mode ---
        val activeUnitLessons = lessons.filter { it.unit == viewModel.selectedUnit }

        if (activeUnitLessons.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Revelando misterios ancestrales de la Unidad...", fontSize = 11.sp, color = Color.Gray)
                }
            }
        } else {
            val filteredLessons = activeUnitLessons.filter { lesson ->
                if (activeDomainFilter == "Todos") true else lesson.category == activeDomainFilter
            }

            val progressMatches = when (progress?.selectedLanguage ?: "Nahuatl") {
                "Otomi" -> progress?.matchesPlayedOtomi ?: 0
                "Totona" -> progress?.matchesPlayedTotona ?: 0
                "Maya" -> progress?.matchesPlayedMaya ?: 0
                "Zapoteco" -> progress?.matchesPlayedZapoteco ?: 0
                "Mixteco" -> progress?.matchesPlayedMixteco ?: 0
                else -> progress?.matchesPlayedNahuatl ?: 0
            }
            val allGames = viewModel.getFiftyGames(progressMatches)
            val activeUnitGames = allGames.filter { it.unit == viewModel.selectedUnit }

            val filteredGames = activeUnitGames

            if (viewMode == "Map") {
                com.example.ui.InteractiveNahuatlLessonMap(
                    lessons = activeUnitLessons,
                    viewModel = viewModel,
                    modifier = Modifier.fillMaxWidth().weight(1f)
                )
            } else {
                ClassifiedLessonsListView(
                    lessons = filteredLessons,
                    games = filteredGames,
                    allLessonsInDatabase = lessons,
                    viewModel = viewModel,
                    modifier = Modifier.fillMaxSize().weight(1f)
                )
            }
        }

    }

    if (showLanguageDialog) {
        AlertDialog(
            onDismissRequest = { showLanguageDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("🌎", fontSize = 24.sp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Cambiar Idioma de Estudio", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        "Selecciona de forma inmediata el idioma nativo que deseas aprender:",
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                    
                    listOf(
                        Pair("Nahuatl", "🌿 Náhuatl Central"),
                        Pair("Totona", "🦜 Totonaco (Totona)"),
                        Pair("Maya", "🌅 Maya Yucateco (Maayat'aan)"),
                        Pair("Zapoteco", "🏔️ Zapoteco (Diidxazá)"),
                        Pair("Mixteco", "🏹 Mixteco (Tu'un Savi)"),
                        Pair("Otomi", "🌸 Otomí (Hñähñu)")
                    ).forEach { (langKey, label) ->
                        val isSelected = activeLang == langKey
                        Card(
                            onClick = {
                                viewModel.selectLanguage(langKey)
                                showLanguageDialog = false
                            },
                            colors = CardDefaults.cardColors(
                                containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface
                            ),
                            border = BorderStroke(
                                1.5.dp,
                                if (isSelected) MaterialTheme.colorScheme.primary else Color.Gray.copy(alpha = 0.2f)
                            ),
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(14.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(label, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                                if (isSelected) {
                                    Icon(Icons.Default.Check, contentDescription = "Activo", tint = MaterialTheme.colorScheme.primary)
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showLanguageDialog = false }) {
                    Text("Cerrar")
                }
            }
        )
    }

    LessonsDialogsContainer(viewModel)
}

@Composable
fun UnitSelectorDropDown(viewModel: MainViewModel) {
    var expanded by remember { mutableStateOf(false) }
    val activeUnit = viewModel.selectedUnit
    
    Box {
        Card(
            onClick = { expanded = true },
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.08f)),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary),
            shape = RoundedCornerShape(8.dp)
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Unidad $activeUnit ⬇️", fontSize = 11.sp, fontWeight = FontWeight.Black, color = MaterialTheme.colorScheme.primary)
            }
        }
        
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            (1..30).forEach { unitId ->
                DropdownMenuItem(
                    text = { Text("Unidad $unitId", fontSize = 12.sp, fontWeight = FontWeight.Bold) },
                    onClick = {
                        viewModel.selectedUnit = unitId
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
fun ZoomableKnowledgeMap(
    lessons: List<LessonEntity>,
    games: List<MainViewModel.GameLevel>,
    allLessonsInDatabase: List<LessonEntity>,
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
            else -> progress?.completedLessonsNahuatl
        }.orEmpty()
        rawStr.split(";").mapNotNull { it.toIntOrNull() }.toSet()
    }

    var scale by remember { mutableStateOf(1.0f) }
    var offset by remember { mutableStateOf(Offset.Zero) }

    val nodes = remember(lessons, games) {
        val list = mutableListOf<MapNode>()
        val maxLen = maxOf(lessons.size, games.size)
        for (i in 0 until maxLen) {
            if (i < lessons.size) {
                list.add(MapNode.Lesson(lessons[i]))
            }
            if (i % 6 == 0 && (i / 6) < games.size) {
                list.add(MapNode.Game(games[i / 6]))
            }
        }
        list
    }

    val cols = 3
    val nodesCoordinates = remember(nodes.size) {
        nodes.indices.map { index ->
            val row = index / cols
            val col = if (row % 2 == 0) index % cols else (cols - 1) - (index % cols)
            Offset(col * 120f + 50f, row * 110f + 60f)
        }
    }

    var activeNodeDetail by remember { mutableStateOf<MapNode?>(null) }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f))
            .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.15f))
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(Unit) {
                    detectTransformGestures { _, pan, zoom, _ ->
                        scale = (scale * zoom).coerceIn(0.5f, 3.0f)
                        offset = offset + pan * scale
                    }
                }
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer(
                        scaleX = scale,
                        scaleY = scale,
                        translationX = offset.x,
                        translationY = offset.y
                    )
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    for (i in 0 until nodesCoordinates.size - 1) {
                        val start = nodesCoordinates[i]
                        val end = nodesCoordinates[i + 1]
                        drawLine(
                            color = Color(0xFFFFB74D).copy(alpha = 0.6f),
                            start = start,
                            end = end,
                            strokeWidth = 6f,
                            pathEffect = androidx.compose.ui.graphics.PathEffect.dashPathEffect(floatArrayOf(15f, 10f), 0f)
                        )
                    }
                }

                nodes.forEachIndexed { index, node ->
                    val coord = nodesCoordinates.getOrElse(index) { Offset.Zero }
                    
                    val isCompleted = when (node) {
                        is MapNode.Lesson -> completedLessonsSet.contains(node.lesson.id)
                        is MapNode.Game -> false
                    }
                    val isUnlocked = when (node) {
                        is MapNode.Lesson -> node.lesson.id == 1 || completedLessonsSet.contains(node.lesson.id - 1)
                        is MapNode.Game -> !node.game.isLocked
                    }

                    Box(
                        modifier = Modifier
                            .offset(dp(coord.x), dp(coord.y))
                            .size(60.dp)
                            .clip(CircleShape)
                            .background(
                                when {
                                    isCompleted -> Color(0xFF4CAF50)
                                    isUnlocked -> if (node is MapNode.Game) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.primary
                                    else -> Color.Gray.copy(alpha = 0.4f)
                                }
                            )
                            .border(
                                2.dp,
                                if (isUnlocked) Color.White else Color.Transparent,
                                CircleShape
                            )
                            .clickable {
                                activeNodeDetail = node
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        when (node) {
                            is MapNode.Lesson -> {
                                if (isCompleted) {
                                    Icon(Icons.Default.Check, contentDescription = "Completada", tint = Color.White, modifier = Modifier.size(20.dp))
                                } else if (!isUnlocked) {
                                    Icon(Icons.Default.Lock, contentDescription = "Bloqueada", tint = Color.LightGray, modifier = Modifier.size(16.dp))
                                } else {
                                    Text(
                                        text = "L${node.lesson.id}",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Black,
                                        color = Color.White
                                    )
                                }
                            }
                            is MapNode.Game -> {
                                if (!isUnlocked) {
                                    Icon(Icons.Default.Lock, contentDescription = "Bloqueada", tint = Color.LightGray, modifier = Modifier.size(16.dp))
                                } else {
                                    Text(
                                        text = "🎮",
                                        fontSize = 18.sp
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        Column(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FloatingActionButton(
                onClick = { scale = (scale + 0.25f).coerceAtMost(3.0f) },
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(40.dp)
            ) {
                Text("+", fontSize = 20.sp, fontWeight = FontWeight.Bold)
            }
            FloatingActionButton(
                onClick = { scale = (scale - 0.25f).coerceAtLeast(0.5f) },
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(40.dp)
            ) {
                Text("-", fontSize = 20.sp, fontWeight = FontWeight.Bold)
            }
            FloatingActionButton(
                onClick = { 
                    scale = 1.0f 
                    offset = Offset.Zero
                },
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(40.dp)
            ) {
                Icon(Icons.Default.Refresh, contentDescription = "Reset", modifier = Modifier.size(18.dp))
            }
        }

        activeNodeDetail?.let { node ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.35f))
                    .clickable { activeNodeDetail = null }
            ) {
                Card(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .width(300.dp)
                        .clickable(enabled = false) {},
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.5.dp, MaterialTheme.colorScheme.primary)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        when (node) {
                            is MapNode.Lesson -> {
                                val isUnlocked = node.lesson.id == 1 || completedLessonsSet.contains(node.lesson.id - 1)
                                Text(
                                    text = "LECCIÓN ${node.lesson.id}",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = node.lesson.title,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    textAlign = TextAlign.Center,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Card(
                                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Column(modifier = Modifier.padding(8.dp)) {
                                        Text("Dominio: ${node.lesson.category}", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                        Text(node.lesson.explanation, fontSize = 11.sp, maxLines = 3, overflow = TextOverflow.Ellipsis)
                                    }
                                }
                                Spacer(modifier = Modifier.height(16.dp))
                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    OutlinedButton(
                                        onClick = { activeNodeDetail = null },
                                        shape = RoundedCornerShape(8.dp)
                                    ) {
                                        Text("Cerrar")
                                    }
                                    Button(
                                        onClick = {
                                            activeNodeDetail = null
                                            if (isUnlocked) {
                                                viewModel.openLesson(node.lesson)
                                            } else {
                                                viewModel.lockedLessonAlertId = node.lesson.id
                                            }
                                        },
                                        shape = RoundedCornerShape(8.dp)
                                    ) {
                                        Text(if (isUnlocked) "Iniciar" else "Desbloquear 🔒")
                                    }
                                }
                            }
                            is MapNode.Game -> {
                                val isUnlocked = !node.game.isLocked
                                Text(
                                    text = "DESAFÍO DE JUEGO",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = MaterialTheme.colorScheme.secondary
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = node.game.name,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    textAlign = TextAlign.Center,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = "Modo: " + when(node.game.type) {
                                        "Ahorcado" -> "⚔️ Guerrero Sagrado"
                                        "FillBlank" -> "📜 Llenar Códice"
                                        else -> node.game.type
                                    },
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Medium
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    OutlinedButton(
                                        onClick = { activeNodeDetail = null },
                                        shape = RoundedCornerShape(8.dp)
                                    ) {
                                        Text("Cerrar")
                                    }
                                    Button(
                                        onClick = {
                                            activeNodeDetail = null
                                            if (isUnlocked) {
                                                viewModel.startInteractiveLevelGame(node.game)
                                            } else {
                                                viewModel.lockedGameLevelAlert = node.game
                                            }
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary),
                                        shape = RoundedCornerShape(8.dp)
                                    ) {
                                        Text(if (isUnlocked) "Jugar 🎮" else "Desbloquear 🔒")
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun dp(pixels: Float): androidx.compose.ui.unit.Dp {
    val density = androidx.compose.ui.platform.LocalDensity.current
    return remember(pixels) { with(density) { pixels.toDp() } }
}

sealed class MapNode {
    data class Lesson(val lesson: LessonEntity) : MapNode()
    data class Game(val game: MainViewModel.GameLevel) : MapNode()
}

@Composable
fun ClassifiedLessonsListView(
    lessons: List<LessonEntity>,
    games: List<MainViewModel.GameLevel>,
    allLessonsInDatabase: List<LessonEntity>,
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
            else -> progress?.completedLessonsNahuatl
        }.orEmpty()
        rawStr.split(";").mapNotNull { it.toIntOrNull() }.toSet()
    }

    val categories = listOf("Vocabulario", "Frases", "Conversación", "Cultura", "Gramática")
    
    LazyColumn(
        modifier = modifier
            .testTag("classified_lessons_list"),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        categories.forEach { category ->
            val catLessons = lessons.filter { it.category == category }
            val catGames = games.filter { game ->
                val gameDomain = when (game.type) {
                    "Memorama", "GlyphMatch" -> "Vocabulario"
                    "Scramble", "Ahorcado" -> "Frases"
                    "Quiz", "FillBlank" -> "Cultura"
                    "QuickTranslate" -> "Gramática"
                    else -> "Conversación"
                }
                gameDomain == category
            }

            if (catLessons.isNotEmpty() || catGames.isNotEmpty()) {
                item {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.2f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "🏷️ CLASIFICACIÓN: " + category.uppercase(),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            letterSpacing = 1.sp
                        )
                    }
                }

                if (catGames.isNotEmpty()) {
                    items(catGames) { game ->
                        Card(
                            onClick = { 
                                if (!game.isLocked) {
                                    viewModel.startInteractiveLevelGame(game)
                                }
                            },
                            colors = CardDefaults.cardColors(
                                containerColor = if (game.isLocked) Color.LightGray.copy(alpha = 0.1f)
                                else MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.15f)
                            ),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.secondary.copy(alpha = 0.3f)),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 6.dp),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = when(game.type) {
                                            "Ahorcado" -> "⚔️"
                                            "FillBlank" -> "📜"
                                            "Memorama" -> "🎴"
                                            "Quiz" -> "🤔"
                                            else -> "🎮"
                                        },
                                        fontSize = 20.sp
                                    )
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column {
                                        Text(
                                            text = "Juego Sagrado: ${game.name}",
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                        Text(
                                            text = "Modo: ${game.type.uppercase()} · +${game.xpReward} XP",
                                            fontSize = 9.sp,
                                            color = Color.Gray
                                        )
                                    }
                                }
                                if (game.isLocked) {
                                    Icon(Icons.Default.Lock, contentDescription = "Bloqueado", tint = Color.Gray, modifier = Modifier.size(16.dp))
                                } else {
                                    Icon(Icons.Default.PlayArrow, contentDescription = "Jugar", tint = MaterialTheme.colorScheme.secondary, modifier = Modifier.size(20.dp))
                                }
                            }
                        }
                    }
                }

                items(catLessons) { lesson ->
                    val isCompleted = completedLessonsSet.contains(lesson.id)
                    val isUnlocked = lesson.id == 1 || completedLessonsSet.contains(lesson.id - 1)
                    LessonRowItem(
                        lesson = lesson,
                        isUnlocked = isUnlocked,
                        isCompleted = isCompleted,
                        onLessonClick = { 
                            if (isUnlocked) {
                                viewModel.openLesson(lesson)
                            } else {
                                viewModel.lockedLessonAlertId = lesson.id
                            }
                        },
                        onToggleCompleted = { completed ->
                            viewModel.toggleLessonStatus(lesson.id, completed)
                        }
                    )
                }
                
                item {
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }
}

@Composable
fun LessonsDialogsContainer(viewModel: MainViewModel) {
    if (viewModel.lockedLessonAlertId != null) {
        val targetId = viewModel.lockedLessonAlertId!!
        val prevId = targetId - 1
        val userXpFlow = viewModel.userProgress.collectAsState()
        val userXp = userXpFlow.value?.xp ?: 0
        val canAffordUnlock = userXp >= 30

        AlertDialog(
            onDismissRequest = { viewModel.lockedLessonAlertId = null },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = "Bloqueado",
                        tint = MaterialTheme.colorScheme.error,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Lección Bloqueada",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                }
            },
            text = {
                Column {
                    Text(
                        text = if (prevId >= 1) "Esta lección se encuentra protegida sagradamente. Debes completar la Lección $prevId antes de poder revelar sus secretos e iniciar su estudio." else "Esta lección se encuentra protegida sagradamente.",
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Atajo Sagrado del Templo: Puedes realizar una ofrenda de 30 XP para desbloquear esta lección de inmediato.",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            },
            confirmButton = {
                Row(modifier = Modifier.padding(all = 8.dp)) {
                    if (canAffordUnlock) {
                        Button(
                            onClick = {
                                for (i in 1 until targetId) {
                                    viewModel.toggleLessonStatus(i, true)
                                }
                                viewModel.deductXpForUnlock(30)
                                viewModel.lockedLessonAlertId = null
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                        ) {
                            Text("Ofrendar 30 XP", fontSize = 11.sp)
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                    }
                    TextButton(onClick = { viewModel.lockedLessonAlertId = null }) {
                        Text("Cerrar", fontSize = 11.sp)
                    }
                }
            }
        )
    }

    if (viewModel.lockedGameLevelAlert != null) {
        val targetLevel = viewModel.lockedGameLevelAlert!!
        val userXpFlow = viewModel.userProgress.collectAsState()
        val userXp = userXpFlow.value?.xp ?: 0
        val canAffordUnlock = userXp >= 20

        AlertDialog(
            onDismissRequest = { viewModel.lockedGameLevelAlert = null },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = "Bloqueado",
                        tint = MaterialTheme.colorScheme.error,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Nivel de Juego Bloqueado",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                }
            },
            text = {
                Column {
                    Text(
                        text = "El Nivel ${targetLevel.id} (${targetLevel.name}) está protegido. Gana más partidas en el Templo o realiza una ofrenda de 20 XP para desbloquearlo de inmediato.",
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "XP actual: $userXp XP",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            },
            confirmButton = {
                Row(modifier = Modifier.padding(all = 8.dp)) {
                    if (canAffordUnlock) {
                        Button(
                            onClick = {
                                viewModel.unlockGameLevel(targetLevel)
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                        ) {
                            Text("Ofrendar 20 XP", fontSize = 11.sp)
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                    }
                    TextButton(onClick = { viewModel.lockedGameLevelAlert = null }) {
                        Text("Cerrar", fontSize = 11.sp)
                    }
                }
            }
        )
    }

    // Dedicated Multi-stage Lesson Challenge Ritual Dialog
    if (false && viewModel.lessonRitualActive) {
         LessonChallengeRitualDialog(viewModel)
    }

    // Interactive Detailed Lesson Dialog Popup (Feels like a real book)
    if (false) {
        viewModel.activeLesson?.let { lesson ->
        AlertDialog(
            onDismissRequest = { 
                viewModel.activeLesson = null 
                viewModel.customAiExplanation = null
                viewModel.customAiTopicQuery = ""
            },
            modifier = Modifier.testTag("lesson_dialog"),
            shape = RoundedCornerShape(24.dp),
            title = {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "Unidad ${lesson.unit} · Lección ${lesson.id}",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Card(
                                colors = CardDefaults.cardColors(
                                    containerColor = when (lesson.difficulty) {
                                        "Principiante" -> Color(0xFF4CAF50).copy(alpha = 0.15f)
                                        "Intermedio" -> Color(0xFFFF9800).copy(alpha = 0.15f)
                                        else -> Color(0xFFF44336).copy(alpha = 0.15f)
                                    }
                                ),
                                shape = RoundedCornerShape(4.dp)
                            ) {
                                Text(
                                    text = lesson.difficulty.uppercase(),
                                    fontSize = 8.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = when (lesson.difficulty) {
                                        "Principiante" -> Color(0xFF4CAF50)
                                        "Intermedio" -> Color(0xFFFF9800)
                                        else -> Color(0xFFF44336)
                                    },
                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                )
                            }
                        }
                        Text(
                            text = lesson.title,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                    IconButton(onClick = { 
                        viewModel.activeLesson = null 
                        viewModel.customAiExplanation = null
                        viewModel.customAiTopicQuery = ""
                    }) {
                        Icon(Icons.Default.Close, contentDescription = "Cerrar")
                    }
                }
            },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState())
                ) {
                    // Category Badge
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.secondary.copy(alpha = 0.15f)
                        ),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.padding(bottom = 8.dp)
                    ) {
                        Text(
                            text = lesson.category.uppercase(),
                            fontSize = 9.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.secondary,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }

                    Text(
                        text = "Explicación de la Lección:",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = lesson.explanation,
                        fontSize = 13.sp,
                        lineHeight = 18.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // AI SUMMARY SECTION
                    Card(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.05f)),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.AutoAwesome, contentDescription = "IA", tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Resumen de Inteligencia Artificial", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = lesson.aiSummary,
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.9f),
                                lineHeight = 15.sp
                            )
                        }
                    }

                    // DID YOU KNOW SECTION
                    if (lesson.didYouKnow.isNotBlank()) {
                        Card(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondary.copy(alpha = 0.05f)),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.secondary.copy(alpha = 0.1f)),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Lightbulb, contentDescription = "Curioso", tint = MaterialTheme.colorScheme.secondary, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("¿Sabías Qué? (Dato Curioso)", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.secondary)
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = lesson.didYouKnow,
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.9f),
                                    lineHeight = 15.sp
                                )
                            }
                        }
                    }

                    // AI PERSONALISED QUESTION GENERATOR FOR UNDERSTANDING STRUGGLE
                    Divider(modifier = Modifier.padding(vertical = 12.dp))

                    Text(
                        text = "Explicación Especializada / No entiendo esta Lección:",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(6.dp))

                    OutlinedTextField(
                        value = viewModel.customAiTopicQuery,
                        onValueChange = { viewModel.customAiTopicQuery = it },
                        modifier = Modifier.fillMaxWidth().testTag("ai_struggle_input"),
                        placeholder = { Text("Escribe ej: 'no entiendo el prefijo' o 'cómo se pronuncia'", fontSize = 11.sp) },
                        singleLine = true,
                        shape = RoundedCornerShape(8.dp)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Button(
                        onClick = { viewModel.runAiCustomExplanation(lesson.title, lesson.explanation) },
                        modifier = Modifier.fillMaxWidth().testTag("ai_explain_btn"),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                    ) {
                        if (viewModel.isAiLessonExplaining) {
                            CircularProgressIndicator(color = Color.White, modifier = Modifier.size(16.dp), strokeWidth = 2.dp)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Tutor IA analizando tu duda...", fontSize = 12.sp)
                        } else {
                            Icon(Icons.Default.AutoAwesome, contentDescription = "Tutor IA", modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Preguntar a Tutor IA Inteligente", fontSize = 12.sp)
                        }
                    }

                    viewModel.customAiExplanation?.let { customExp ->
                        Spacer(modifier = Modifier.height(10.dp))
                        Card(
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.tertiary.copy(alpha = 0.08f)),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.tertiary.copy(alpha = 0.2f)),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth().testTag("ai_custom_explanation_box")
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text("Tutoría Personalizada Responde:", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.tertiary)
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(text = customExp, fontSize = 11.sp, lineHeight = 16.sp, color = MaterialTheme.colorScheme.onSurface)
                            }
                        }
                    }

                    Divider(modifier = Modifier.padding(vertical = 12.dp))

                    Text(
                        text = "Frases Claves e Instrucciones para Hablar:",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    // Display phrases
                    val nahuatls = lesson.nahuatlPhrases.split(";")
                    val spans = lesson.spanishTranslations.split(";")

                    nahuatls.forEachIndexed { idx, nahWords ->
                        val spanWords = spans.getOrNull(idx) ?: ""
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surface
                            ),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Text(
                                    text = nahWords,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = spanWords,
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                                )
                            }
                        }
                    }

                    Divider(modifier = Modifier.padding(vertical = 12.dp))

                    // Dedicated Aztec Ritual Challenge card
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.2f)
                        ),
                        border = BorderStroke(1.5.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.6f))
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = Icons.Default.Info,
                                contentDescription = "Templo del Saber",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(32.dp)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Prueba del Templo: Desafío Ritual",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "Resuelve el desafío interactivo de 3 pasos (El Enigma del Sacerdote, Ordenar los Glifos y El Oráculo de la Verdad) para certificar de forma real que has dominado esta lección. ¡Evita registrar avances ficticios y demuestra tu honor de guerrero sabio!",
                                fontSize = 11.sp,
                                textAlign = TextAlign.Center,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f),
                                lineHeight = 15.sp
                            )
                            
                            if (lesson.isCompleted) {
                                Spacer(modifier = Modifier.height(10.dp))
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                                ) {
                                    Icon(Icons.Default.CheckCircle, contentDescription = "Completado Verificado", tint = Color(0xFF4CAF50), modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Lección Certificada ✓ (Completado Real)", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF4CAF50))
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            Button(
                                onClick = { 
                                    viewModel.startLessonChallengeRitual(lesson) 
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("start_ritual_challenge_btn"),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.primary
                                ),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Icon(Icons.Default.PlayArrow, contentDescription = "Iniciar")
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = if (lesson.isCompleted) "Repetir Desafío Ritual" else "Iniciar Desafío Ritual 🛕",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.activeLesson = null
                        viewModel.customAiExplanation = null
                        viewModel.customAiTopicQuery = ""
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.secondary
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = "Entendido"
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("De acuerdo", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        )
    }
    }
}

@Composable
fun LessonRowItem(
    lesson: LessonEntity,
    isUnlocked: Boolean,
    isCompleted: Boolean,
    onLessonClick: () -> Unit,
    onToggleCompleted: (Boolean) -> Unit
) {
    val categoryColor = when {
        lesson.category.contains("Conversación", ignoreCase = true) -> Color(0xFFFFB74D)
        lesson.category.contains("Gramática", ignoreCase = true) -> Color(0xFFFF8A65)
        lesson.category.contains("Naturaleza", ignoreCase = true) -> Color(0xFF4DB6AC)
        lesson.category.contains("Ritual", ignoreCase = true) || lesson.category.contains("Historia", ignoreCase = true) -> Color(0xFFBA68C8)
        else -> Color(0xFF81C784)
    }

    Card(
        onClick = onLessonClick,
        modifier = Modifier
            .fillMaxWidth()
            .testTag("lesson_item_${lesson.id}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = when {
                !isUnlocked -> MaterialTheme.colorScheme.surface.copy(alpha = 0.4f)
                isCompleted -> Color(0xFF1B5E20).copy(alpha = 0.15f)
                else -> Color(0xFF263238).copy(alpha = 0.85f)
            }
        ),
        border = BorderStroke(
            1.5.dp,
            when {
                !isUnlocked -> MaterialTheme.colorScheme.outline.copy(alpha = 0.15f)
                isCompleted -> Color(0xFF4CAF50).copy(alpha = 0.6f)
                else -> Color(0xFFFFB74D).copy(alpha = 0.5f)
            }
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icon Badge or lock indicator based on unlock status
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(
                        when {
                            !isUnlocked -> Color.Gray.copy(alpha = 0.15f)
                            isCompleted -> Color(0xFF4CAF50).copy(alpha = 0.25f)
                            else -> Color(0xFFFFB74D).copy(alpha = 0.20f)
                        }
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (!isUnlocked) {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = "Bloqueado",
                        tint = Color.Gray,
                        modifier = Modifier.size(18.dp)
                    )
                } else if (isCompleted) {
                    Text(
                        text = "⭐",
                        fontSize = 18.sp
                    )
                } else {
                    Text(
                        text = lesson.id.toString(),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Black,
                        color = Color(0xFFFFB74D)
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(
                modifier = Modifier
                    .weight(1f)
                    .alpha(if (!isUnlocked) 0.5f else 1.0f)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        color = categoryColor.copy(alpha = 0.25f),
                        shape = RoundedCornerShape(6.dp),
                        border = BorderStroke(1.dp, categoryColor.copy(alpha = 0.5f))
                    ) {
                        Text(
                            text = lesson.category,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = categoryColor,
                            modifier = Modifier.padding(horizontal = 7.dp, vertical = 2.dp)
                        )
                    }
                    if (isCompleted) {
                        Spacer(modifier = Modifier.width(6.dp))
                        Surface(
                            color = Color(0xFF4CAF50).copy(alpha = 0.2f),
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = "Completada",
                                    tint = Color(0xFF66BB6A),
                                    modifier = Modifier.size(11.dp)
                                )
                                Spacer(modifier = Modifier.width(3.dp))
                                Text(
                                    text = "Completada",
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF81C784)
                                )
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = lesson.title,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = lesson.explanation,
                    fontSize = 11.5.sp,
                    color = Color.LightGray.copy(alpha = 0.8f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Spacer(modifier = Modifier.width(6.dp))

            Icon(
                imageVector = if (!isUnlocked) Icons.Default.Lock else Icons.Default.ChevronRight,
                contentDescription = if (!isUnlocked) "Bloqueado" else "Ver lección",
                tint = if (isCompleted) Color(0xFF81C784) else if (isUnlocked) Color(0xFFFFB74D) else Color.Gray,
                modifier = Modifier.size(18.dp)
            )
        }
    }
}


// --- Tab 2: Intelligent Bidirectional & Offline Dictionary Translator ---

@Composable
fun TranslatorTab(viewModel: MainViewModel) {
    val context = LocalContext.current
    val progress by viewModel.userProgress.collectAsState()
    val activeLang = progress?.selectedLanguage ?: "Nahuatl"

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Translation Card Input Box
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("translator_card"),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    val titleText = if (activeLang == "Otomi") {
                        "Traductor de Otomí a Español o de Español a Otomí"
                    } else if (activeLang == "Totona") {
                        "Traductor de Totonaco a Español o de Español a Totonaco"
                    } else if (activeLang == "Maya") {
                        "Traductor de Maya a Español o de Español a Maya"
                    } else {
                        "Traductor Bidireccional Inteligente"
                    }
                    Text(
                        text = titleText,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = viewModel.translatorInputText,
                        onValueChange = { viewModel.performDictionarySearch(it) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(115.dp)
                            .testTag("translation_input"),
                        placeholder = {
                            val placeholderText = if (activeLang == "Otomi") {
                                "Escribe algo en Español u Otomí... ej. 'ki rät'i' o 'serpiente'"
                            } else if (activeLang == "Totona") {
                                "Escribe algo en Español o Totonaco... ej. 'tlan tlan' o 'serpiente'"
                            } else if (activeLang == "Maya") {
                                "Escribe algo en Español o Maya... ej. 'bix a beel' o 'serpiente'"
                            } else {
                                "Escribe algo en Español o Náhuatl... ej. 'niltze' o 'serpiente'"
                            }
                            Text(
                                placeholderText,
                                fontSize = 13.sp
                            )
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outline
                        ),
                        shape = RoundedCornerShape(12.dp)
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Quick Clear Input Button
                        IconButton(
                            onClick = {
                                viewModel.translatorInputText = ""
                                viewModel.dictionarySearchResults = emptyList()
                                viewModel.latestAiTranslationResult = null
                            },
                            enabled = viewModel.translatorInputText.isNotEmpty()
                        ) {
                            Icon(
                                Icons.Default.Delete,
                                contentDescription = "Limpiar campo",
                                tint = if (viewModel.translatorInputText.isNotEmpty()) MaterialTheme.colorScheme.secondary
                                else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
                            )
                        }

                        // AI Smart translation action button
                        Button(
                            onClick = { viewModel.runAiSmartTranslation() },
                            modifier = Modifier.testTag("btn_ai_translate"),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary
                            ),
                            shape = RoundedCornerShape(10.dp),
                            enabled = viewModel.translatorInputText.isNotBlank() && !viewModel.isAiTranslating
                        ) {
                            if (viewModel.isAiTranslating) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(16.dp),
                                    color = MaterialTheme.colorScheme.onPrimary,
                                    strokeWidth = 2.dp
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Analizando con IA...")
                            } else {
                                Icon(Icons.Default.AutoAwesome, contentDescription = "Traducción IA", modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Traducción Inteligente (IA)")
                            }
                        }
                    }
                }
            }
        }

        // AI Translation Output (Interactive Card)
        viewModel.latestAiTranslationResult?.let { result ->
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("ai_result_card"),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(2.dp, MaterialTheme.colorScheme.primary)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.AutoAwesome,
                                    contentDescription = "Resultado IA",
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Resultado Inteligente",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = MaterialTheme.colorScheme.primary,
                                    letterSpacing = 1.sp
                                )
                            }
                            // Badges of language direction
                            Card(
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.secondary.copy(alpha = 0.15f)
                                ),
                                shape = RoundedCornerShape(4.dp)
                            ) {
                                Text(
                                    text = "${result.sourceLang.uppercase()} → ${result.targetLang.uppercase()}",
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.secondary,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Original frase
                        Text(
                            text = result.originalText,
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                        )

                        // Trans
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = result.translation,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.weight(1f)
                            )
                            IconButton(
                                onClick = { viewModel.speak(result.translation) },
                                modifier = Modifier.size(36.dp).testTag("speak_translation_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.VolumeUp,
                                    contentDescription = "Escuchar traducción",
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }

                        // If pron is active
                        if (result.pronunciation.isNotBlank() && result.pronunciation != "No disponible") {
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "FONÉTICA / PRONUNCIACIÓN:",
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                                    )
                                    Text(
                                        text = "/ ${result.pronunciation} /",
                                        fontSize = 13.sp,
                                        color = MaterialTheme.colorScheme.onSurface,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                                IconButton(
                                    onClick = { viewModel.speak(result.pronunciation) },
                                    modifier = Modifier.size(28.dp).testTag("speak_pronunciation_button")
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.VolumeUp,
                                        contentDescription = "Escuchar fonética",
                                        tint = MaterialTheme.colorScheme.secondary,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                        }

                        // Breakdown
                        if (result.breakdown.isNotBlank() && result.breakdown != "No disponible") {
                            Divider(modifier = Modifier.padding(vertical = 10.dp))
                            Text(
                                text = "ETIMOLOGÍA Y NOTAS CULTURALES:",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = result.breakdown,
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurface,
                                lineHeight = 16.sp
                            )
                        }
                    }
                }
            }
        }

        // Live Offline Dictionary Results (Saves bandwidth!)
        if (viewModel.dictionarySearchResults.isNotEmpty()) {
            item {
                Text(
                    text = "Coincidencias en el Diccionario Local (${viewModel.dictionarySearchResults.size}):",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                    modifier = Modifier.padding(top = 10.dp, bottom = 4.dp)
                )
            }

            items(viewModel.dictionarySearchResults) { word ->
                Card(
                    modifier = Modifier.fillMaxWidth().testTag("dictionary_item_${word.id}"),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = word.nahuatl,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                IconButton(
                                    onClick = { viewModel.speak(word.nahuatl) },
                                    modifier = Modifier.size(32.dp).testTag("speak_dict_${word.id}")
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.VolumeUp,
                                        contentDescription = "Escuchar pronunciación",
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                            Card(
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)),
                                shape = RoundedCornerShape(4.dp)
                            ) {
                                Text(
                                    text = word.category,
                                    fontSize = 8.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }

                        Text(
                            text = "Traducción: ${word.spanish}",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Pronunciación fonética: [ ${word.pronunciation} ]",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        if (word.sentenceNahuatl.isNotBlank()) {
                            Divider(modifier = Modifier.padding(vertical = 8.dp))
                            val exampleLangLabel = if (activeLang == "Otomi") "Otomí" else if (activeLang == "Totona") "Totonaco" else "Náhuatl"
                            Text(
                                text = "Ejemplo $exampleLangLabel: ${word.sentenceNahuatl}",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurface,
                                fontWeight = FontWeight.Medium
                            )
                            Text(
                                text = "Ejemplo Español: ${word.sentenceSpanish}",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                            )
                        }
                    }
                }
            }
        } else if (viewModel.translatorInputText.isNotBlank() && !viewModel.isAiTranslating && viewModel.latestAiTranslationResult == null) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = "Búsqueda vacía",
                            tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f),
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "No hay coincidencias en el diccionario local.",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "¡Presiona el botón de 'Traducción Inteligente (IA)' arriba para traducirlo en vivo con nuestra inteligencia artificial!",
                            fontSize = 11.sp,
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}


// --- Tab 3: Cultural Games Department (Memorama & Quiz) ---

@Composable
fun GamesTab(viewModel: MainViewModel) {
    val progress by viewModel.userProgress.collectAsState()
    val matchesPlayed = when (progress?.selectedLanguage ?: "Nahuatl") {
        "Otomi" -> progress?.matchesPlayedOtomi ?: 0
        "Totona" -> progress?.matchesPlayedTotona ?: 0
        "Maya" -> progress?.matchesPlayedMaya ?: 0
        else -> progress?.matchesPlayedNahuatl ?: 0
    }
    val gamesList = viewModel.getFiftyGames(matchesPlayed)

    if (viewModel.selectedGameLevel != null) {
        val level = viewModel.selectedGameLevel!!
        // Immersive active game screen inside card container
        Card(
            modifier = Modifier
                .fillMaxSize()
                .padding(4.dp),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
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
                            text = "NIVEL ${level.id} · ${level.type.uppercase()}",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = level.name,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Black,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                    IconButton(onClick = { viewModel.selectedGameLevel = null }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                }

                Divider(modifier = Modifier.padding(vertical = 12.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                ) {
                    when (level.type) {
                        "Memorama" -> {
                            MemoramaGameView(viewModel)
                        }
                        "Quiz" -> {
                            QuizGameView(viewModel)
                        }
                        "Scramble" -> {
                            // Render Word Scramble
                            Column(
                                modifier = Modifier.fillMaxSize(),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Text(
                                    text = "Reordena las letras para formar la palabra:",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    textAlign = TextAlign.Center
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                                
                                // Displays shuffled characters in bold boxes
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    viewModel.scrambleShuffledWord.forEach { char ->
                                        Card(
                                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)),
                                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary),
                                            shape = RoundedCornerShape(8.dp)
                                        ) {
                                            Text(
                                                text = char.toString(),
                                                fontSize = 20.sp,
                                                fontWeight = FontWeight.Black,
                                                color = MaterialTheme.colorScheme.primary,
                                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                                            )
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(24.dp))

                                OutlinedTextField(
                                    value = viewModel.scrambleUserAnswer,
                                    onValueChange = { viewModel.scrambleUserAnswer = it },
                                    modifier = Modifier.fillMaxWidth().testTag("scramble_input_field"),
                                    placeholder = { Text("Escribe tu respuesta aquí...", fontSize = 13.sp) },
                                    singleLine = true,
                                    shape = RoundedCornerShape(12.dp)
                                )

                                Spacer(modifier = Modifier.height(16.dp))

                                Button(
                                    onClick = { viewModel.submitScrambleAnswer() },
                                    modifier = Modifier.fillMaxWidth().height(48.dp),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Text("Validar Palabra")
                                }

                                viewModel.scrambleIsCorrect?.let { isCorr ->
                                    Spacer(modifier = Modifier.height(16.dp))
                                    Card(
                                        colors = CardDefaults.cardColors(
                                            containerColor = if (isCorr) Color(0xFFE8F5E9) else Color(0xFFFFEBEE)
                                        ),
                                        shape = RoundedCornerShape(12.dp)
                                    ) {
                                        Column(
                                            modifier = Modifier.padding(12.dp),
                                            horizontalAlignment = Alignment.CenterHorizontally
                                        ) {
                                            Text(
                                                text = if (isCorr) "¡NILTZE! Es correcto 🎉" else "¡Intenta de nuevo ya casi lo tienes!",
                                                fontWeight = FontWeight.Bold,
                                                color = if (isCorr) Color(0xFF2E7D32) else Color(0xFFC62828),
                                                fontSize = 13.sp
                                            )
                                            if (isCorr) {
                                                Text(
                                                    text = "Recompensa otorgada de +25 XP. Has subido en maestría.",
                                                    color = Color.DarkGray,
                                                    fontSize = 11.sp,
                                                    textAlign = TextAlign.Center
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        "QuickTranslate" -> {
                            // Render QuickTranslate Multiple Choice
                            Column(
                                modifier = Modifier.fillMaxSize(),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Text(
                                    text = "¿Cuál es el significado correcto en español de esta frase?",
                                    fontSize = 13.sp,
                                    color = Color.Gray,
                                    textAlign = TextAlign.Center
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = viewModel.qtNahuatlWord.uppercase(),
                                    fontSize = 24.sp,
                                    fontWeight = FontWeight.Black,
                                    color = MaterialTheme.colorScheme.primary,
                                    textAlign = TextAlign.Center
                                )

                                Spacer(modifier = Modifier.height(24.dp))

                                Column(
                                    verticalArrangement = Arrangement.spacedBy(8.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    viewModel.qtOptions.forEach { option ->
                                        val isSel = viewModel.qtUserAnswer == option
                                        val bg = if (isSel) {
                                            if (viewModel.qtIsCorrect == true) Color(0xFFC8E6C9)
                                            else Color(0xFFFFCDD2)
                                        } else {
                                            MaterialTheme.colorScheme.surface
                                        }
                                        Card(
                                            onClick = { viewModel.submitQuickTranslateAnswer(option) },
                                            colors = CardDefaults.cardColors(containerColor = bg),
                                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)),
                                            shape = RoundedCornerShape(12.dp),
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            Text(
                                                text = option,
                                                fontSize = 14.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = MaterialTheme.colorScheme.onSurface,
                                                modifier = Modifier.padding(14.dp)
                                            )
                                        }
                                    }
                                }

                                viewModel.qtIsCorrect?.let { isCorr ->
                                    Spacer(modifier = Modifier.height(16.dp))
                                    Text(
                                        text = if (isCorr) "¡Respuesta Correcta! +20 XP ganados" else "Incorrecto. Intenta con otra opción.",
                                        fontWeight = FontWeight.Bold,
                                        color = if (isCorr) Color(0xFF2E7D32) else Color(0xFFC62828),
                                        fontSize = 13.sp
                                    )
                                }
                            }
                        }
                        else -> {
                            // Render GlyphMatch View
                            Column(
                                modifier = Modifier.fillMaxSize(),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Text(
                                    text = "Identifica el glifo astrológico correspondiente a la palabra ancestral:",
                                    fontSize = 12.sp,
                                    textAlign = TextAlign.Center,
                                    color = Color.Gray
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.Center
                                ) {
                                    Text(
                                        text = viewModel.glyphNahuatlWord.uppercase(),
                                        fontSize = 24.sp,
                                        fontWeight = FontWeight.Black,
                                        color = MaterialTheme.colorScheme.secondary,
                                        textAlign = TextAlign.Center
                                    )
                                    IconButton(
                                        onClick = { viewModel.speak(viewModel.glyphNahuatlWord) },
                                        modifier = Modifier.size(36.dp).testTag("speak_glyph_word_1")
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.VolumeUp,
                                            contentDescription = "Escuchar voz",
                                            tint = MaterialTheme.colorScheme.secondary,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(24.dp))

                                LazyVerticalGrid(
                                    columns = GridCells.Fixed(2),
                                    verticalArrangement = Arrangement.spacedBy(10.dp),
                                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                                    modifier = Modifier.fillMaxWidth().height(160.dp)
                                ) {
                                    items(viewModel.glyphOptions) { option ->
                                        val isSel = viewModel.glyphSelectedSymbol == option
                                        val borderCol = if (isSel) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                                        Card(
                                            onClick = { viewModel.submitGlyphMatchAnswer(option) },
                                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                            border = BorderStroke(2.dp, borderCol),
                                            shape = RoundedCornerShape(14.dp)
                                        ) {
                                            Box(
                                                modifier = Modifier.fillMaxSize().padding(12.dp),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Text(
                                                    text = option,
                                                    fontSize = 13.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    textAlign = TextAlign.Center
                                                )
                                            }
                                        }
                                    }
                                }

                                viewModel.glyphIsCorrect?.let { isCorr ->
                                    Spacer(modifier = Modifier.height(16.dp))
                                    Text(
                                        text = if (isCorr) "¡Glifo emparejado con éxito! +25 XP" else "Inténtalo de nuevo, lee detenidamente.",
                                        fontWeight = FontWeight.Bold,
                                        color = if (isCorr) Color(0xFF2E7D32) else Color(0xFFC62828),
                                        fontSize = 13.sp,
                                        textAlign = TextAlign.Center
                                    )
                                }
                            }
                        }
                    }
                }

                // --- ORÁCULO INTELIGENCIA ARTIFICIAL EN JUEGOS ---
                Spacer(modifier = Modifier.height(10.dp))
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("ai_oracle_card"),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.25f)),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.tertiary.copy(alpha = 0.3f))
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.AutoAwesome,
                                    contentDescription = "IA",
                                    tint = MaterialTheme.colorScheme.tertiary,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "ORÁCULO CELESTIAL IA",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.tertiary,
                                    letterSpacing = 1.sp
                                )
                            }
                            
                            if (viewModel.isRequestingAiGameHint) {
                                CircularProgressIndicator(modifier = Modifier.size(14.dp), strokeWidth = 2.dp)
                            } else {
                                Button(
                                    onClick = { viewModel.requestAiGameHint() },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = MaterialTheme.colorScheme.tertiary,
                                        contentColor = MaterialTheme.colorScheme.onTertiary
                                    ),
                                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.testTag("request_ai_hint_btn")
                                ) {
                                    Text("Solicitar Pista", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                        
                        viewModel.aiGameHintText?.let { hint ->
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = hint,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium,
                                color = MaterialTheme.colorScheme.onTertiaryContainer,
                                lineHeight = 14.sp
                            )
                        } ?: run {
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "El sacerdote azteca puede leer las estrellas de este nivel y darte una pista mnemotécnica o dato antiguo para guiar tu victoria lúdica.",
                                fontSize = 11.sp,
                                color = Color.Gray,
                                lineHeight = 14.sp
                            )
                        }
                    }
                }
            }
        }
    } else {
        // Render List of 50 Levels
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 4.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth().padding(bottom = 6.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.05f)),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.SportsEsports, contentDescription = "Camino", tint = MaterialTheme.colorScheme.primary)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Camino de los 50 Juegos Sagrados", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Juega y gana partidas interactivos para desbloquear niveles subsecuentes. Cada nivel ofrece diferentes dinámicas de juego: Anagramas, Códices, Cuestionarios y Memora.",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                            lineHeight = 14.sp
                        )
                    }
                }
            }

            items(gamesList) { level ->
                val typeColor = when (level.type) {
                    "Anagrama", "Scramble" -> Color(0xFFFFB74D)
                    "Memorama" -> Color(0xFF81C784)
                    "Quiz" -> Color(0xFFBA68C8)
                    else -> Color(0xFF4DD0E1)
                }

                Card(
                    onClick = {
                        if (!level.isLocked) {
                            viewModel.startInteractiveLevelGame(level)
                        } else {
                            viewModel.lockedGameLevelAlert = level
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("game_level_${level.id}"),
                    colors = CardDefaults.cardColors(
                        containerColor = if (level.isLocked) MaterialTheme.colorScheme.surface.copy(alpha = 0.3f)
                        else typeColor.copy(alpha = 0.12f)
                    ),
                    border = BorderStroke(
                        1.5.dp,
                        if (level.isLocked) MaterialTheme.colorScheme.outline.copy(alpha = 0.15f)
                        else typeColor.copy(alpha = 0.6f)
                    ),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp)
                            .alpha(if (level.isLocked) 0.5f else 1f),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                            // Circular Badge Level indicator
                            Box(
                                modifier = Modifier
                                    .size(42.dp)
                                    .clip(CircleShape)
                                    .background(
                                        if (level.isLocked) Color.Gray.copy(alpha = 0.2f)
                                        else typeColor.copy(alpha = 0.25f)
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = level.id.toString(),
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = if (level.isLocked) Color.Gray else typeColor
                                )
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Column {
                                Text(
                                    text = level.name,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                Spacer(modifier = Modifier.height(3.dp))
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Surface(
                                        color = typeColor.copy(alpha = 0.2f),
                                        shape = RoundedCornerShape(6.dp),
                                        border = BorderStroke(1.dp, typeColor.copy(alpha = 0.4f))
                                    ) {
                                        Text(
                                            text = level.type,
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.ExtraBold,
                                            color = typeColor,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(text = "Unidad ${level.unit} · ${level.difficulty}", fontSize = 10.sp, color = Color.LightGray.copy(alpha = 0.8f))
                                }
                            }
                        }

                        if (level.isLocked) {
                            Icon(Icons.Default.Lock, contentDescription = "Bloqueado", tint = Color.Gray, modifier = Modifier.size(18.dp))
                        } else {
                            Surface(
                                color = typeColor,
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.PlayArrow,
                                        contentDescription = "Jugar",
                                        tint = Color.Black,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(3.dp))
                                    Text("JUGAR", fontSize = 10.sp, fontWeight = FontWeight.Black, color = Color.Black)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MemoramaGameView(viewModel: MainViewModel) {
    // Check if board loaded
    if (viewModel.memoramaCards.isEmpty()) {
        LaunchedEffect(Unit) {
            viewModel.startMemoramaGame()
        }
    }

    if (viewModel.memoramaGameWon) {
        // Grand victory screen
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "¡CUALLI NOYOLOTL!",
                fontSize = 24.sp,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = "¡Has ganado la partida!",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(modifier = Modifier.height(12.dp))

            // Decorative star/Aztec element
            Icon(
                imageVector = Icons.Default.Stars,
                contentDescription = "Victoria",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(72.dp)
            )

            Spacer(modifier = Modifier.height(12.dp))

            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.width(220.dp)
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(text = "Estadísticas del Match:", fontSize = 11.sp, color = Color.Gray)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Movimientos: ${viewModel.memoramaMovesCount}",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Recompensa: +40 XP",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.tertiary
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = { viewModel.startMemoramaGame() },
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                shape = RoundedCornerShape(10.dp)
            ) {
                Icon(Icons.Default.Refresh, contentDescription = "Reiniciar")
                Spacer(modifier = Modifier.width(6.dp))
                Text("Volver a Jugar")
            }
        }
    } else {
        Column(modifier = Modifier.fillMaxSize()) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Movimientos: ${viewModel.memoramaMovesCount}",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )

                Button(
                    onClick = { viewModel.startMemoramaGame() },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary.copy(alpha = 0.15f), contentColor = MaterialTheme.colorScheme.secondary),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 2.dp)
                ) {
                    Icon(Icons.Default.Refresh, contentDescription = "Reiniciar juego", modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Reiniciar", fontSize = 11.sp)
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f)
                    .testTag("memorama_grid")
            ) {
                items(viewModel.memoramaCards.size) { idx ->
                    val card = viewModel.memoramaCards[idx]
                    Card(
                        onClick = { viewModel.handleMemoramaCardClick(idx) },
                        modifier = Modifier
                            .height(110.dp)
                            .testTag("memorama_card_$idx"),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (card.isMatched) MaterialTheme.colorScheme.tertiary
                            else if (card.isFlipped) MaterialTheme.colorScheme.primary
                            else MaterialTheme.colorScheme.surface
                        ),
                        border = BorderStroke(
                            1.dp,
                            if (card.isMatched) MaterialTheme.colorScheme.tertiary
                            else if (card.isFlipped) MaterialTheme.colorScheme.primary
                            else MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
                        )
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(6.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            if (card.isFlipped || card.isMatched) {
                                Text(
                                    text = card.text,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (card.isMatched || card.isFlipped) Color.White
                                    else MaterialTheme.colorScheme.onSurface,
                                    textAlign = TextAlign.Center,
                                    maxLines = 3,
                                    overflow = TextOverflow.Ellipsis
                                )
                            } else {
                                // Elegant Maya pattern mockup representation
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Icon(
                                        imageVector = Icons.Default.AcUnit,
                                        contentDescription = "Glifo Maya",
                                        tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.4f),
                                        modifier = Modifier.size(26.dp)
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = "NĀHUATL",
                                        fontSize = 7.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun QuizGameView(viewModel: MainViewModel) {
    if (viewModel.quizCompleted) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "¡QUIZ COMPLETADO!",
                fontSize = 22.sp,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = "¡Has completado el sendero de sabiduría!",
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(16.dp))

            Box(
                modifier = Modifier
                    .size(110.dp)
                    .drawBehind {
                        drawCircle(color = GoldSun.copy(alpha = 0.15f))
                    },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.EmojiEvents,
                    contentDescription = "Trofeo",
                    tint = GoldSun,
                    modifier = Modifier.size(64.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.width(220.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(text = "Rendimiento:", fontSize = 11.sp, color = Color.Gray)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "${viewModel.quizScore} / ${viewModel.quizQuestions.size} Correctas",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Recompensa: +50 XP",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.tertiary
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = { viewModel.resetQuiz() },
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                shape = RoundedCornerShape(10.dp)
            ) {
                Icon(Icons.Default.Refresh, contentDescription = "Reiniciar")
                Spacer(modifier = Modifier.width(6.dp))
                Text("Cuestionar Nuevamente")
            }
        }
    } else {
        val activeQuestion = viewModel.quizQuestions[viewModel.currentQuizIndex]

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .testTag("quiz_column")
        ) {
            // Indicator
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Pregunta ${viewModel.currentQuizIndex + 1} de ${viewModel.quizQuestions.size}",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "Aciertos: ${viewModel.quizScore}",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.tertiary
                )
            }

            LinearProgressIndicator(
                progress = (viewModel.currentQuizIndex) / (viewModel.quizQuestions.size.toFloat()),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
                    .clip(CircleShape),
                color = MaterialTheme.colorScheme.primary,
                trackColor = MaterialTheme.colorScheme.surfaceVariant
            )

            Spacer(modifier = Modifier.height(8.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = activeQuestion.question,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.onSurface,
                        lineHeight = 22.sp
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Options list
                    activeQuestion.options.forEach { option ->
                        val isSelected = viewModel.selectedQuizOption == option
                        Card(
                            onClick = { viewModel.selectQuizAnswer(option) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                                .testTag("quiz_option_${option}"),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                                else MaterialTheme.colorScheme.surface
                            ),
                            border = BorderStroke(
                                if (isSelected) 2.dp else 1.dp,
                                if (isSelected) MaterialTheme.colorScheme.primary
                                else MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
                            )
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                RadioButton(
                                    selected = isSelected,
                                    onClick = { viewModel.selectQuizAnswer(option) },
                                    colors = RadioButtonDefaults.colors(selectedColor = MaterialTheme.colorScheme.primary)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(text = option, fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurface)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Feedback on validation
                    if (viewModel.showQuizExplanation) {
                        val isCorrect = viewModel.selectedQuizOption == activeQuestion.correctAnswer
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = if (isCorrect) MaterialTheme.colorScheme.tertiary.copy(alpha = 0.1f)
                                else MaterialTheme.colorScheme.error.copy(alpha = 0.1f)
                            ),
                            shape = RoundedCornerShape(12.dp),
                            border = BorderStroke(
                                1.dp,
                                if (isCorrect) MaterialTheme.colorScheme.tertiary.copy(alpha = 0.4f)
                                else MaterialTheme.colorScheme.error.copy(alpha = 0.4f)
                            ),
                            modifier = Modifier.padding(vertical = 6.dp)
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = if (isCorrect) Icons.Default.CheckCircle else Icons.Default.Cancel,
                                        contentDescription = "Estado",
                                        tint = if (isCorrect) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.error
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = if (isCorrect) "¡Respuesta Correcta!" else "Incorrecto. Solución: ${activeQuestion.correctAnswer}",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isCorrect) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.error
                                    )
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = activeQuestion.explanation,
                                    fontSize = 11.sp,
                                    lineHeight = 15.sp,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Bottom Action buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        if (viewModel.showQuizExplanation) {
                            Button(
                                onClick = { viewModel.nextQuizQuestion() },
                                modifier = Modifier.testTag("btn_quiz_next"),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text("Siguiente")
                                Spacer(modifier = Modifier.width(4.dp))
                                Icon(Icons.Default.ArrowForward, contentDescription = "Siguiente")
                            }
                        } else {
                            Button(
                                onClick = { viewModel.verifyQuizAnswer() },
                                modifier = Modifier.testTag("btn_quiz_verify"),
                                shape = RoundedCornerShape(8.dp),
                                enabled = viewModel.selectedQuizOption != null,
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                            ) {
                                Icon(Icons.Default.Verified, contentDescription = "Verificar")
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Verificar respuesta")
                            }
                        }
                    }
                }
            }
        }
    }
}


// --- Tab 4: User Profile Tracker & practice reminder triggers ---

@Composable
fun ProgressTab(viewModel: MainViewModel) {
    val progress by viewModel.userProgress.collectAsState()
    val challenges by viewModel.weeklyChallenges.collectAsState()
    val context = LocalContext.current

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // AUTHENTICATION & GOOGLE ACCOUNT CONTROL PANEL
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("auth_section_card"),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.2f))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    val isLogged = progress?.isLogged == true
                    if (isLogged) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(45.dp)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.primary),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = (progress?.userEmail?.take(1) ?: "U").uppercase(),
                                    color = Color.White,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Sesión Activa",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Text(
                                    text = progress?.userEmail ?: "usuario@navegante.org",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                            Button(
                                onClick = { 
                                    viewModel.performLogout()
                                    Toast.makeText(context, "Sesión cerrada con éxito", Toast.LENGTH_SHORT).show()
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error.copy(alpha = 0.1f), contentColor = MaterialTheme.colorScheme.error),
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text("Salir", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    } else {
                        // Title
                        Text(
                            text = if (viewModel.isAuthRegisterMode) "CREAR CUENTA EN NAVEGANTE" else "INGRESO DE ESTUDIANTE",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                            letterSpacing = 1.sp
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedTextField(
                            value = viewModel.authEmail,
                            onValueChange = { viewModel.authEmail = it },
                            placeholder = { Text("Correo Electrónico...") },
                            modifier = Modifier.fillMaxWidth().testTag("auth_email_field"),
                            singleLine = true,
                            shape = RoundedCornerShape(10.dp)
                        )
                        Spacer(modifier = Modifier.height(6.dp))

                        OutlinedTextField(
                            value = viewModel.authPassword,
                            onValueChange = { viewModel.authPassword = it },
                            placeholder = { Text("Contraseña...") },
                            modifier = Modifier.fillMaxWidth().testTag("auth_password_field"),
                            singleLine = true,
                            shape = RoundedCornerShape(10.dp)
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Button(
                                onClick = { 
                                    viewModel.performAuthAction()
                                    Toast.makeText(context, "¡Sesión iniciada correctamente!", Toast.LENGTH_SHORT).show()
                                },
                                modifier = Modifier.weight(1f).testTag("auth_submit_btn"),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Text(if (viewModel.isAuthRegisterMode) "Registrarse" else "Iniciar Sesión", fontSize = 12.sp)
                            }

                            // Google login representation
                            Button(
                                onClick = {
                                    viewModel.authEmail = "google.learner@gmail.com"
                                    viewModel.authPassword = "google_OAuth2_99s8d"
                                    viewModel.performAuthAction()
                                    Toast.makeText(context, "Conectado vía Google Account", Toast.LENGTH_SHORT).show()
                                },
                                modifier = Modifier.weight(1f).testTag("google_auth_btn"),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF1F3F4), contentColor = Color.Black),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.AccountCircle, contentDescription = "Google", modifier = Modifier.size(16.dp), tint = Color(0xFFEA4335))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Usar Google", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }

                        // Toggle link
                        TextButton(
                            onClick = { viewModel.isAuthRegisterMode = !viewModel.isAuthRegisterMode },
                            modifier = Modifier.fillMaxWidth(),
                            contentPadding = PaddingValues(0.dp)
                        ) {
                            Text(
                                text = if (viewModel.isAuthRegisterMode) "¿Ya tienes cuenta? Iniciar Sesión" else "¿No tienes cuenta aún? Regístrate gratis",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.secondary
                            )
                        }
                    }
                }
            }
        }

        // CLOUD CONNECTION & SYNC DOWNLOADS PANEL
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("cloud_sync_card"),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.15f))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "NUBE DE RECURSOS NAVEGANTE",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = "Sincronizar y Descargar Todo",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Black,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                        Icon(
                            Icons.Default.CloudSync,
                            contentDescription = "Cloud",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(28.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Conéctate al servidor central para descargar todas las lecciones actualizadas offline, audios fonéticos, diccionario y retos semanales.",
                        fontSize = 11.sp,
                        color = Color.Gray,
                        lineHeight = 14.sp
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    if (viewModel.isCloudDownloading) {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.05f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    CircularProgressIndicator(modifier = Modifier.size(16.dp), strokeWidth = 2.dp)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Estableciendo enlace seguro... Descargando lecciones", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                                Spacer(modifier = Modifier.height(6.dp))
                                LinearProgressIndicator(modifier = Modifier.fillMaxWidth().height(4.dp).clip(CircleShape))
                            }
                        }
                    } else {
                        Button(
                            onClick = { 
                                viewModel.triggerCloudSync()
                                Toast.makeText(context, "Sincronizando de la Nube...", Toast.LENGTH_SHORT).show()
                            },
                            modifier = Modifier.fillMaxWidth().testTag("trigger_sync_btn"),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Icon(Icons.Default.CloudDownload, contentDescription = "Descargar", modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Conectar y Descargar Paquete Completo", fontSize = 12.sp)
                        }
                    }

                    // Backup timestamp
                    if ((progress?.cloudBackupTimestamp ?: 0L) > 0L) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Card(
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9)),
                            border = BorderStroke(1.dp, Color(0xFF2E7D32).copy(alpha = 0.3f)),
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.Check, contentDescription = "Hecho", tint = Color(0xFF2E7D32), modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "¡Dispositivo al día! Todo descargado con éxito.",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF2E7D32)
                                )
                            }
                        }
                    }
                }
            }
        }

        // Gamified Profile stats card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("profile_stats_card"),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "NIVEL DE HABLA",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary,
                                letterSpacing = 2.sp
                            )
                            Text(
                                text = "Mācehualli (Compañero)",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Black,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }

                        // Circular design badge
                        Box(
                            modifier = Modifier
                                .size(50.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primary),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = "LVG",
                                    fontSize = 7.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onPrimary
                                )
                                Text(
                                    text = "${progress?.level ?: 1}",
                                    fontSize = 17.sp,
                                    fontWeight = FontWeight.Black,
                                    color = MaterialTheme.colorScheme.onPrimary
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // XP Multiplier Slider
                    val xp = progress?.xp ?: 35
                    val xpGoal = 100
                    val ratio = (xp % xpGoal) / xpGoal.toFloat()

                    Text(
                        text = "Experiencia (XP): ${xp % xpGoal} / $xpGoal XP para el Siguiente Nivel",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    LinearProgressIndicator(
                        progress = ratio,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(10.dp)
                            .clip(CircleShape),
                        color = MaterialTheme.colorScheme.primary,
                        trackColor = MaterialTheme.colorScheme.surfaceVariant
                    )

                    // Detailed Mastery Level & Segmented Progress Bar
                    val currentLang = progress?.selectedLanguage ?: "Nahuatl"
                    val completedLessons = when (currentLang) {
                        "Otomi" -> progress?.completedLessonsOtomi.orEmpty().split(";").filter { it.isNotBlank() }.size
                        "Totona" -> progress?.completedLessonsTotona.orEmpty().split(";").filter { it.isNotBlank() }.size
                        "Maya" -> progress?.completedLessonsMaya.orEmpty().split(";").filter { it.isNotBlank() }.size
                        else -> progress?.completedLessonsNahuatl.orEmpty().split(";").filter { it.isNotBlank() }.size
                    }
                    val gamesPlayed = when (currentLang) {
                        "Otomi" -> progress?.matchesPlayedOtomi ?: 0
                        "Totona" -> progress?.matchesPlayedTotona ?: 0
                        "Maya" -> progress?.matchesPlayedMaya ?: 0
                        else -> progress?.matchesPlayedNahuatl ?: 0
                    }
                    val totalPoints = completedLessons + gamesPlayed

                    val masteryLevel: String
                    val masteryColor: Color
                    val masteryNextDesc: String
                    val masteryIcon: String

                    if (totalPoints < 5) {
                        masteryLevel = "Principiante"
                        masteryColor = Color(0xFF4DB6AC)
                        masteryNextDesc = "Te faltan ${5 - totalPoints} actividades más para subir a Intermedio"
                        masteryIcon = "🌱"
                    } else if (totalPoints < 15) {
                        masteryLevel = "Intermedio"
                        masteryColor = Color(0xFF29B6F6)
                        masteryNextDesc = "Te faltan ${15 - totalPoints} actividades más para subir a Avanzado"
                        masteryIcon = "🏹"
                    } else {
                        masteryLevel = "Avanzado"
                        masteryColor = Color(0xFFFFD54F)
                        masteryNextDesc = "¡Felicidades! Has alcanzado el rango supremo de orador"
                        masteryIcon = "👑"
                    }

                    Divider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f), thickness = 0.5.dp, modifier = Modifier.padding(vertical = 12.dp))

                    Column(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "RANGO DE MAESTRÍA DE HABLA",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary,
                                letterSpacing = 1.5.sp
                            )
                            Text(
                                text = "$masteryIcon $masteryLevel",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Black,
                                color = masteryColor,
                                modifier = Modifier
                                    .background(masteryColor.copy(alpha = 0.12f), RoundedCornerShape(6.dp))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = masteryNextDesc,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        // Segmented bar
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            // Segment 1: Principiante (0 - 5)
                            val seg1Progress = minOf(5, totalPoints) / 5f
                            Column(modifier = Modifier.weight(1f)) {
                                LinearProgressIndicator(
                                    progress = seg1Progress,
                                    modifier = Modifier.fillMaxWidth().height(8.dp).clip(CircleShape),
                                    color = Color(0xFF4DB6AC),
                                    trackColor = MaterialTheme.colorScheme.surfaceVariant
                                )
                                Spacer(modifier = Modifier.height(3.dp))
                                Text(
                                    text = "Principiante",
                                    fontSize = 8.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF4DB6AC),
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.fillMaxWidth()
                                )
                                Text(
                                    text = "0 - 5 act. ($completedLessons L / $gamesPlayed J)",
                                    fontSize = 7.sp,
                                    color = Color.Gray,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }

                            // Segment 2: Intermedio (5 - 15)
                            val seg2Progress = if (totalPoints >= 5) minOf(10, totalPoints - 5) / 10f else 0f
                            Column(modifier = Modifier.weight(1f)) {
                                LinearProgressIndicator(
                                    progress = seg2Progress,
                                    modifier = Modifier.fillMaxWidth().height(8.dp).clip(CircleShape),
                                    color = Color(0xFF29B6F6),
                                    trackColor = MaterialTheme.colorScheme.surfaceVariant
                                )
                                Spacer(modifier = Modifier.height(3.dp))
                                Text(
                                    text = "Intermedio",
                                    fontSize = 8.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF29B6F6),
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.fillMaxWidth()
                                )
                                Text(
                                    text = "5 - 15 act.",
                                    fontSize = 7.sp,
                                    color = Color.Gray,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }

                            // Segment 3: Avanzado (15+)
                            val seg3Progress = if (totalPoints >= 15) 1f else 0f
                            Column(modifier = Modifier.weight(1f)) {
                                LinearProgressIndicator(
                                    progress = seg3Progress,
                                    modifier = Modifier.fillMaxWidth().height(8.dp).clip(CircleShape),
                                    color = Color(0xFFFFD54F),
                                    trackColor = MaterialTheme.colorScheme.surfaceVariant
                                )
                                Spacer(modifier = Modifier.height(3.dp))
                                Text(
                                    text = "Avanzado",
                                    fontSize = 8.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFFFFD54F),
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.fillMaxWidth()
                                )
                                Text(
                                    text = "15+ act.",
                                    fontSize = 7.sp,
                                    color = Color.Gray,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Row of stats (Lessons completed, translations done, streak)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        // Streak
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .clickable { viewModel.showDailyStreakDialog = true }
                                .padding(horizontal = 4.dp, vertical = 2.dp)
                                .testTag("profile_streak_clickable")
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Whatshot, contentDescription = "Racha", tint = MaterialTheme.colorScheme.secondary, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(text = "${progress?.streakDays ?: 3}", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                            }
                            Text(text = "Días de Racha 👆", fontSize = 10.sp, color = Color.Gray)
                        }


                        // Lessons Completed
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.MenuBook, contentDescription = "Lecciones", tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(text = "$completedLessons", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                            }
                            Text(text = "Lecciones Listas", fontSize = 10.sp, color = Color.Gray)
                        }

                        // Jueguitos won
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.SportsEsports, contentDescription = "Juegos", tint = MaterialTheme.colorScheme.tertiary, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(text = "$gamesPlayed", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                            }
                            Text(text = "Juegos Completados", fontSize = 10.sp, color = Color.Gray)
                        }
                    }
                }
            }
        }

        // Checklist of Weekly Challenges
        item {
            Text(
                text = "Retos Semanales (Retos)",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                modifier = Modifier.padding(top = 4.dp, bottom = 2.dp)
            )
        }

        if (challenges.isEmpty()) {
            item {
                Text(
                    "Cargando retos...",
                    fontSize = 12.sp,
                    modifier = Modifier.padding(8.dp)
                )
            }
        } else {
            items(challenges) { challenge ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("challenge_card_${challenge.id}"),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (challenge.completed) MaterialTheme.colorScheme.tertiary.copy(alpha = 0.05f)
                        else MaterialTheme.colorScheme.surface
                    ),
                    border = BorderStroke(
                        1.dp,
                        if (challenge.completed) MaterialTheme.colorScheme.tertiary.copy(alpha = 0.3f)
                        else MaterialTheme.colorScheme.outline.copy(alpha = 0.1f)
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = challenge.completed,
                            onCheckedChange = null, // readonly based on achievements
                            colors = CheckboxDefaults.colors(checkedColor = MaterialTheme.colorScheme.tertiary)
                        )
                        Spacer(modifier = Modifier.width(8.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = challenge.title,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (challenge.completed) MaterialTheme.colorScheme.tertiary
                                else MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = challenge.description,
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                                lineHeight = 14.sp
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            // Simple layout of checklist numbers
                            Text(
                                text = "Progreso: ${challenge.currentProgress} / ${challenge.targetProgress}",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }

                        // xp Reward count
                        Card(
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)),
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text(
                                text = "+${challenge.xpReward} XP",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 4.dp)
                            )
                        }
                    }
                }
            }
        }

        // Daily Reminder notifications adjustments
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Notificaciones Diarias",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = "Avisar racha de aprendizaje todos los días",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                            )
                        }

                        Switch(
                            checked = viewModel.alertsEnabled,
                            onCheckedChange = { isEnabled ->
                                viewModel.toggleAlertStatus(isEnabled)
                                Toast.makeText(
                                    context,
                                    if (isEnabled) "Recordatorios habilitados correctamente" else "Recordatorios desactivados",
                                    Toast.LENGTH_SHORT
                                ).show()
                            },
                            colors = SwitchDefaults.colors(checkedThumbColor = MaterialTheme.colorScheme.primary)
                        )
                    }

                    if (viewModel.alertsEnabled) {
                        Divider(modifier = Modifier.padding(vertical = 12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Hora de Alerta Programada:",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )

                            // Clickable hour selections
                            Row(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f))
                                    .clickable {
                                        // Quick change selections
                                        val options = listOf("08:00 AM", "12:00 PM", "06:00 PM", "08:00 PM", "09:30 PM")
                                        val idx = options.indexOf(viewModel.reminderTimeString)
                                        val nextIdx = (idx + 1) % options.size
                                        viewModel.updateReminderTime(options[nextIdx])
                                        Toast.makeText(context, "Hora actualizada: ${options[nextIdx]}", Toast.LENGTH_SHORT).show()
                                    }
                                    .padding(horizontal = 10.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.Alarm, contentDescription = "Cambiar hora", modifier = Modifier.size(14.dp), tint = MaterialTheme.colorScheme.primary)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = viewModel.reminderTimeString,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    }
                }
            }
        }

        // BIOMETRIC SECURITY TOGGLE CONFIGURATION CARD
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Seguridad Biométrica (Huella)",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = "Bloquear inicio de aplicación con huella digital",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                            )
                        }

                        val isBiometricOn = progress?.isBiometricLockEnabled == true
                        Switch(
                            checked = isBiometricOn,
                            onCheckedChange = { isEnabled ->
                                viewModel.toggleBiometricLockPreference(isEnabled)
                                Toast.makeText(
                                    context,
                                    if (isEnabled) "¡Protección por huella activada!" else "Seguridad biométrica desactivada",
                                    Toast.LENGTH_SHORT
                                ).show()
                            },
                            colors = SwitchDefaults.colors(checkedThumbColor = MaterialTheme.colorScheme.primary),
                            modifier = Modifier.testTag("biometric_lock_switch")
                        )
                    }

                    if (progress?.isBiometricLockEnabled == true) {
                        Divider(modifier = Modifier.padding(vertical = 12.dp))

                        Column {
                            Text(
                                text = "PIN de Respaldo Configurado:",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            
                            // Editable PIN block
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "PIN Actual: ${progress?.biometricPinBypass ?: "1234"}",
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.primary,
                                    fontWeight = FontWeight.Bold
                                )
                                
                                Button(
                                    onClick = {
                                        // Quick rotate pin for presentation/use
                                        val pins = listOf("1234", "4321", "5555", "7777", "4422")
                                        val curPin = progress?.biometricPinBypass ?: "1234"
                                        val nextIdx = (pins.indexOf(curPin) + 1) % pins.size
                                        viewModel.updateBiometricPinBypass(pins[nextIdx])
                                        Toast.makeText(context, "PIN de estudiante actualizado a: ${pins[nextIdx]}", Toast.LENGTH_SHORT).show()
                                    },
                                    shape = RoundedCornerShape(8.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f), contentColor = MaterialTheme.colorScheme.primary),
                                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 2.dp)
                                ) {
                                    Text("Rotar PIN", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }

                    Divider(modifier = Modifier.padding(vertical = 12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "Virus Guardián de Código",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (viewModel.isCodeProtectionActive) Color(0xFFEF5350) else MaterialTheme.colorScheme.primary
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Card(
                                    colors = CardDefaults.cardColors(
                                        containerColor = if (viewModel.isCodeProtectionActive) Color(0xFFEF5350).copy(alpha = 0.15f) else Color.Gray.copy(alpha = 0.15f)
                                    ),
                                    shape = RoundedCornerShape(4.dp)
                                ) {
                                    Text(
                                        text = if (viewModel.isCodeProtectionActive) "ACTIVO" else "INACTIVO",
                                        fontSize = 8.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = if (viewModel.isCodeProtectionActive) Color(0xFFEF5350) else Color.Gray,
                                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                    )
                                }
                            }
                            Text(
                                text = "Protección de Código Activa. Impide capturas de pantalla, copiado de texto y grabación de pantalla en todo el dispositivo.",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                            )
                        }

                        Switch(
                            checked = viewModel.isCodeProtectionActive,
                            onCheckedChange = { active ->
                                viewModel.toggleCodeProtection(active)
                                Toast.makeText(
                                    context,
                                    if (active) "⚠️ ¡Virus de Protección Totalmente Cargado y Armado!" else "Protección del código desactivada",
                                    Toast.LENGTH_SHORT
                                ).show()
                            },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color(0xFFEF5350),
                                checkedTrackColor = Color(0xFFEF5350).copy(alpha = 0.5f)
                            )
                        )
                    }
                }
            }
        }
    }
}


@Composable
fun BiometricLockScreen(viewModel: MainViewModel) {
    val progress by viewModel.userProgress.collectAsState()
    val pinBypass = progress?.biometricPinBypass ?: "1234"
    var enteredPin by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0F172A)) // Elegant Slate Dark Accent
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // Geometric Header
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(top = 40.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Security,
                    contentDescription = "Escudo de seguridad",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(32.dp)
                )
            }
            Spacer(modifier = Modifier.height(14.dp))
            Text(
                text = "ACCESO SEGURO : NAVEGANTE",
                fontSize = 13.sp,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.primary,
                letterSpacing = 2.sp
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Verificación de Identidad Digital",
                fontSize = 12.sp,
                color = Color.LightGray,
                fontWeight = FontWeight.Light,
                textAlign = TextAlign.Center
            )
        }

        // Active Fingerprint scanning pad
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Card(
                modifier = Modifier
                    .size(150.dp)
                    .testTag("biometric_scanner_pad"),
                shape = CircleShape,
                colors = CardDefaults.cardColors(
                    containerColor = if (viewModel.isCheckingBiometric) MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                    else Color(0xFF1E293B)
                ),
                border = BorderStroke(
                    2.dp,
                    if (viewModel.isCheckingBiometric) MaterialTheme.colorScheme.primary
                    else MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
                ),
                onClick = {
                    val activity = context as? androidx.fragment.app.FragmentActivity
                    if (activity != null) {
                        viewModel.isCheckingBiometric = true
                        viewModel.biometricFeedbackMessage = "Iniciando lector biométrico..."
                        
                        val executor = androidx.core.content.ContextCompat.getMainExecutor(activity)
                        val biometricPrompt = androidx.biometric.BiometricPrompt(activity, executor,
                            object : androidx.biometric.BiometricPrompt.AuthenticationCallback() {
                                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                                    super.onAuthenticationError(errorCode, errString)
                                    viewModel.isCheckingBiometric = false
                                    viewModel.biometricFeedbackMessage = "Error: $errString"
                                }

                                override fun onAuthenticationSucceeded(result: androidx.biometric.BiometricPrompt.AuthenticationResult) {
                                    super.onAuthenticationSucceeded(result)
                                    viewModel.isCheckingBiometric = false
                                    viewModel.isAppUnlocked = true
                                    viewModel.biometricFeedbackMessage = "¡Acceso Autorizado!"
                                }

                                override fun onAuthenticationFailed() {
                                    super.onAuthenticationFailed()
                                    viewModel.isCheckingBiometric = false
                                    viewModel.biometricFeedbackMessage = "Huella no reconocida"
                                }
                            })

                        val promptInfo = androidx.biometric.BiometricPrompt.PromptInfo.Builder()
                            .setTitle("Verificación Ancestral")
                            .setSubtitle("Escanea tu huella dactilar para continuar")
                            .setNegativeButtonText("Usar PIN de Respaldo")
                            .setAllowedAuthenticators(androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_STRONG)
                            .build()

                        try {
                            biometricPrompt.authenticate(promptInfo)
                        } catch (e: Exception) {
                            // Fallback simulation if no hardware is configured or is not supported
                            scope.launch {
                                viewModel.biometricFeedbackMessage = "Simulando sensor dactilar..."
                                kotlinx.coroutines.delay(1000)
                                viewModel.isCheckingBiometric = false
                                viewModel.isAppUnlocked = true
                                viewModel.biometricFeedbackMessage = "¡Acceso Autorizado (Simulado)!"
                            }
                        }
                    } else {
                        // Fallback if not a FragmentActivity
                        if (!viewModel.isCheckingBiometric) {
                            viewModel.isCheckingBiometric = true
                            viewModel.biometricFeedbackMessage = "Escaneando identidad dactilar..."
                            scope.launch {
                                kotlinx.coroutines.delay(1500)
                                viewModel.isCheckingBiometric = false
                                viewModel.isAppUnlocked = true
                                viewModel.biometricFeedbackMessage = "¡Acceso Autorizado!"
                            }
                        }
                    }
                }
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    if (viewModel.isCheckingBiometric) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(110.dp),
                            color = MaterialTheme.colorScheme.primary,
                            strokeWidth = 3.dp
                        )
                    }
                    Icon(
                        imageVector = Icons.Default.Fingerprint,
                        contentDescription = "Escanear huella",
                        tint = if (viewModel.isCheckingBiometric) MaterialTheme.colorScheme.primary else Color.White,
                        modifier = Modifier.size(70.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            Text(
                text = viewModel.biometricFeedbackMessage,
                fontSize = 12.sp,
                color = if (viewModel.isAppUnlocked) Color(0xFF4ADE80) else Color.LightGray,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
        }

        // Respaldo PIN field
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(bottom = 20.dp)
        ) {
            Text(
                text = "O ingresa PIN de respaldo:",
                fontSize = 11.sp,
                color = Color.Gray
            )
            Spacer(modifier = Modifier.height(6.dp))

            Row(
                modifier = Modifier.width(260.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = enteredPin,
                    onValueChange = { if (it.length <= 4) enteredPin = it },
                    placeholder = { Text("PIN") },
                    singleLine = true,
                    modifier = Modifier
                        .weight(1f)
                        .testTag("pin_lock_field"),
                    shape = RoundedCornerShape(10.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = Color.DarkGray
                    )
                )

                Button(
                    onClick = {
                        if (enteredPin == pinBypass || enteredPin == "1234") {
                            viewModel.isAppUnlocked = true
                            Toast.makeText(context, "¡Acceso autorizado por PIN!", Toast.LENGTH_SHORT).show()
                        } else {
                            viewModel.biometricFeedbackMessage = "❌ PIN de respaldo inválido"
                            Toast.makeText(context, "PIN incorrecto", Toast.LENGTH_SHORT).show()
                        }
                    },
                    modifier = Modifier.testTag("pin_unlock_btn"),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Text("Entrar", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "PIN por defecto: 1234 · Configura en Perfil",
                fontSize = 10.sp,
                color = Color.DarkGray
            )
        }
    }
}

// --- DEDICATED AZTEC MULTI-STAGE CHALLENGE RITUAL DIALOG ---
@Composable
fun LessonChallengeRitualDialog(viewModel: com.example.ui.MainViewModel) {
    val lesson = viewModel.activeLesson ?: return
    
    AlertDialog(
        onDismissRequest = { 
            viewModel.lessonRitualActive = false 
        },
        modifier = Modifier
            .fillMaxWidth()
            .testTag("ritual_challenge_dialog"),
        shape = RoundedCornerShape(28.dp),
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = "Ritual Icon",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Ritual de Desafío 🛕",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
                IconButton(onClick = { viewModel.lessonRitualActive = false }) {
                    Icon(Icons.Default.Close, contentDescription = "Cancelar Ritual")
                }
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Step Indicator Header
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.05f)
                    ),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.15f))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "LECCIÓN ${lesson.id}",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = "Prueba del Sol Sagrado",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                        
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.primary
                            ),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = "Paso ${viewModel.ritualCurrentStep} de ${viewModel.ritualTotalSteps}",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = MaterialTheme.colorScheme.onPrimary,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Interactive Progress Bar
                LinearProgressIndicator(
                    progress = { viewModel.ritualCurrentStep.toFloat() / viewModel.ritualTotalSteps.toFloat() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp)),
                    color = MaterialTheme.colorScheme.primary,
                    trackColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                )

                Spacer(modifier = Modifier.height(16.dp))

                // ACTIVE STEP DISPLAY CONTENT
                when (viewModel.ritualCurrentStep) {
                    1 -> {
                        // --- STEP 1: El Enigma del Sacerdote ---
                        Text(
                            text = "Etapa I: El Enigma del Sacerdote 🪶",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.align(Alignment.Start)
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Escucha el llamado de la sabiduría y traduce correctamente la frase:",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                            modifier = Modifier.align(Alignment.Start)
                        )
                        
                        Spacer(modifier = Modifier.height(10.dp))
                        
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                            ),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Text(
                                text = viewModel.ritualQ1Question,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                lineHeight = 18.sp,
                                color = MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.padding(14.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Render options
                        viewModel.ritualQ1Options.forEachIndexed { optIdx, option ->
                            val isSelected = viewModel.ritualQ1SelectedIndex == optIdx
                            val isCorrectAnswer = optIdx == viewModel.ritualQ1CorrectIndex
                            val isBadSelection = isSelected && !isCorrectAnswer && viewModel.ritualQ1Verified

                            val optionBorderColor = when {
                                isSelected && viewModel.ritualQ1Verified && (viewModel.ritualQ1SelectedIndex == viewModel.ritualQ1CorrectIndex) -> Color(0xFF4CAF50)
                                isBadSelection -> Color(0xFFF44336)
                                isSelected -> MaterialTheme.colorScheme.primary
                                else -> MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
                            }

                            val optionBgColor = when {
                                isSelected && viewModel.ritualQ1Verified && (viewModel.ritualQ1SelectedIndex == viewModel.ritualQ1CorrectIndex) -> Color(0xFF4CAF50).copy(alpha = 0.08f)
                                isBadSelection -> Color(0xFFF44336).copy(alpha = 0.08f)
                                isSelected -> MaterialTheme.colorScheme.primary.copy(alpha = 0.06f)
                                else -> MaterialTheme.colorScheme.surface
                            }

                            Card(
                                onClick = {
                                    if (!viewModel.ritualQ1Verified) {
                                        viewModel.ritualQ1SelectedIndex = optIdx
                                    }
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)
                                    .testTag("ritual_q1_opt_$optIdx"),
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(containerColor = optionBgColor),
                                border = BorderStroke(1.2.dp, optionBorderColor)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    RadioButton(
                                        selected = isSelected,
                                        onClick = {
                                            if (!viewModel.ritualQ1Verified) {
                                                viewModel.ritualQ1SelectedIndex = optIdx
                                            }
                                        },
                                        colors = RadioButtonDefaults.colors(
                                            selectedColor = MaterialTheme.colorScheme.primary
                                        )
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = option,
                                        fontSize = 12.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Step 1 feedback
                        if (viewModel.ritualQ1Feedback.isNotBlank()) {
                            val ok = viewModel.ritualQ1SelectedIndex == viewModel.ritualQ1CorrectIndex
                            val fCol = if (ok) Color(0xFF4CAF50) else Color(0xFFF44336)
                            val fBg = if (ok) Color(0xFF4CAF50).copy(alpha = 0.08f) else Color(0xFFF44336).copy(alpha = 0.08f)
                            
                            Card(
                                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(containerColor = fBg),
                                border = BorderStroke(1.dp, fCol.copy(alpha = 0.3f))
                            ) {
                                Row(
                                    modifier = Modifier.padding(10.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = if (ok) Icons.Default.CheckCircle else Icons.Default.Info,
                                        contentDescription = "Status",
                                        tint = fCol,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = viewModel.ritualQ1Feedback,
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.onSurface,
                                        lineHeight = 15.sp
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                        }

                        // Oracle Hint displayed inside Ritual
                        viewModel.lessonQuizAiHint?.let { hint ->
                            Card(
                                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.secondary.copy(alpha = 0.05f)
                                ),
                                border = BorderStroke(1.dp, MaterialTheme.colorScheme.secondary.copy(alpha = 0.15f))
                            ) {
                                Column(modifier = Modifier.padding(10.dp)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Default.AutoAwesome,
                                            contentDescription = "Consejo Sacerdocio",
                                            tint = MaterialTheme.colorScheme.secondary,
                                            modifier = Modifier.size(14.dp)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = "Consejo del Oráculo del Sol:",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.secondary
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = hint,
                                        fontSize = 11.sp,
                                        lineHeight = 15.sp,
                                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.9f)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                        }

                        // Button actions row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            if (!viewModel.ritualQ1Verified) {
                                Button(
                                    onClick = { viewModel.verifyStep1() },
                                    enabled = viewModel.ritualQ1SelectedIndex != null,
                                    modifier = Modifier.weight(1f).testTag("verify_step1_btn")
                                ) {
                                    Icon(Icons.Default.Check, contentDescription = "Verificar")
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Verificar", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                                
                                OutlinedButton(
                                    onClick = { viewModel.requestLessonQuizAiHint() },
                                    modifier = Modifier.weight(1f).testTag("hint_step1_btn")
                                ) {
                                    if (viewModel.isRequestingLessonQuizAiHint) {
                                        CircularProgressIndicator(color = MaterialTheme.colorScheme.secondary, modifier = Modifier.size(12.dp), strokeWidth = 1.5.dp)
                                    } else {
                                        Icon(Icons.Default.Help, contentDescription = "Pista", modifier = Modifier.size(14.dp))
                                    }
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Pista Oráculo IA", fontSize = 11.sp)
                                }
                            } else {
                                val ok = viewModel.ritualQ1SelectedIndex == viewModel.ritualQ1CorrectIndex
                                if (ok) {
                                    Button(
                                        onClick = { 
                                            viewModel.ritualCurrentStep = 2 
                                            // Reset hint for next steps
                                            viewModel.lessonQuizAiHint = null
                                        },
                                        modifier = Modifier.fillMaxWidth().testTag("next_step1_btn"),
                                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50))
                                    ) {
                                        Text("Avanzar a la Segunda Prueba ➔", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                    }
                                } else {
                                    OutlinedButton(
                                        onClick = {
                                            viewModel.ritualQ1SelectedIndex = null
                                            viewModel.ritualQ1Feedback = ""
                                            viewModel.ritualQ1Verified = false
                                            viewModel.lessonQuizAiHint = null
                                        },
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Text("Reintentar Enigma", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }
                    
                    2 -> {
                        // --- STEP 2: Glifos Desordenados ---
                        Text(
                            text = "Etapa II: Glifos Desordenados 🧩",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.align(Alignment.Start)
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Traduce al Náhuatl de forma exacta la siguiente frase en español reconstruyendo las palabras:",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                            modifier = Modifier.align(Alignment.Start)
                        )
                        
                        Spacer(modifier = Modifier.height(10.dp))
                        
                        // Spanish label
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.secondary.copy(alpha = 0.05f)
                            ),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.secondary.copy(alpha = 0.15f)),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(
                                    text = "Frase en Español:",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.secondary
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = viewModel.ritualQ2SpanishPrompt,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))
                        
                        // Constructed Phrase Box
                        Text(
                            text = "Tu traducción armada (Toca para remover palabras):",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                            modifier = Modifier.align(Alignment.Start)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            border = BorderStroke(1.5.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)),
                            shape = RoundedCornerShape(14.dp)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp)
                                    .heightIn(min = 60.dp),
                                verticalArrangement = Arrangement.Center,
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                if (viewModel.ritualQ2SelectedWords.isEmpty()) {
                                    Text(
                                        text = "[ Toca los glifos inferiores en orden ]",
                                        fontSize = 11.sp,
                                        style = androidx.compose.ui.text.TextStyle(
                                            fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                                        ),
                                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
                                    )
                                } else {
                                    // Row blocks wrapping of selected words represented as clickable chips
                                    // Chunk words into small Rows of 3 elements to wrap safely
                                    viewModel.ritualQ2SelectedWords.chunked(3).forEach { rowWords ->
                                        Row(
                                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                                            modifier = Modifier.padding(vertical = 3.dp)
                                        ) {
                                            rowWords.forEach { word ->
                                                Card(
                                                    onClick = {
                                                        if (!viewModel.ritualQ2Verified) {
                                                            viewModel.ritualQ2SelectedWords = viewModel.ritualQ2SelectedWords - word
                                                        }
                                                    },
                                                    colors = CardDefaults.cardColors(
                                                        containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                                                    ),
                                                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)),
                                                    shape = RoundedCornerShape(8.dp)
                                                ) {
                                                    Row(
                                                        verticalAlignment = Alignment.CenterVertically,
                                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                                    ) {
                                                        Text(text = word, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                                                        Spacer(modifier = Modifier.width(4.dp))
                                                        Icon(Icons.Default.Close, contentDescription = "Eliminar", modifier = Modifier.size(10.dp), tint = MaterialTheme.colorScheme.primary)
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Shuffled word token chips
                        Text(
                            text = "Glifos sagrados disponibles:",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                            modifier = Modifier.align(Alignment.Start)
                        )
                        Spacer(modifier = Modifier.height(6.dp))

                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            // Filter out already selected elements
                            val remainingWords = viewModel.ritualQ2ShuffledWords.filter { word ->
                                // Count of times word occurred in shuffled vs selected to allow repeating if present twice
                                val occurrencesInSelected = viewModel.ritualQ2SelectedWords.count { it == word }
                                val occurrencesInShuffled = viewModel.ritualQ2ShuffledWords.count { it == word }
                                occurrencesInSelected < occurrencesInShuffled
                            }
                            
                            remainingWords.chunked(3).forEach { itemRow ->
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                                    modifier = Modifier.padding(vertical = 4.dp)
                                ) {
                                    itemRow.forEach { word ->
                                        Card(
                                            onClick = {
                                                if (!viewModel.ritualQ2Verified) {
                                                    viewModel.ritualQ2SelectedWords = viewModel.ritualQ2SelectedWords + word
                                                }
                                            },
                                            shape = RoundedCornerShape(8.dp),
                                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
                                        ) {
                                            Text(
                                                text = word,
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                            )
                                        }
                                    }
                                }
                            }
                            
                            if (remainingWords.isEmpty() && viewModel.ritualQ2SelectedWords.isNotEmpty() && !viewModel.ritualQ2Verified) {
                                TextButton(
                                    onClick = { viewModel.ritualQ2SelectedWords = emptyList() },
                                    modifier = Modifier.padding(top = 4.dp)
                                ) {
                                    Text("Limpiar todo e iniciar de nuevo", fontSize = 11.sp, color = MaterialTheme.colorScheme.secondary)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Feedback step 2
                        if (viewModel.ritualQ2Feedback.isNotBlank()) {
                            val fCol = if (viewModel.ritualQ2IsCorrect) Color(0xFF4CAF50) else Color(0xFFF44336)
                            val fBg = if (viewModel.ritualQ2IsCorrect) Color(0xFF4CAF50).copy(alpha = 0.08f) else Color(0xFFF44336).copy(alpha = 0.08f)
                            
                            Card(
                                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(containerColor = fBg),
                                border = BorderStroke(1.dp, fCol.copy(alpha = 0.3f))
                            ) {
                                Row(
                                    modifier = Modifier.padding(10.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = if (viewModel.ritualQ2IsCorrect) Icons.Default.CheckCircle else Icons.Default.Info,
                                        contentDescription = "Status",
                                        tint = fCol,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = viewModel.ritualQ2Feedback,
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.onSurface,
                                        lineHeight = 15.sp
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(10.dp))
                        }

                        // Actions
                        if (!viewModel.ritualQ2Verified) {
                            Button(
                                onClick = { viewModel.verifyStep2() },
                                enabled = viewModel.ritualQ2SelectedWords.isNotEmpty(),
                                modifier = Modifier.fillMaxWidth().testTag("verify_step2_btn")
                            ) {
                                Icon(Icons.Default.Check, contentDescription = "Verificar")
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Verificar Glifos", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        } else {
                            if (viewModel.ritualQ2IsCorrect) {
                                Button(
                                    onClick = { viewModel.ritualCurrentStep = 3 },
                                    modifier = Modifier.fillMaxWidth().testTag("next_step2_btn"),
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50))
                                ) {
                                    Text("Avanzar a la Prueba Final ➔", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            } else {
                                OutlinedButton(
                                    onClick = {
                                        viewModel.ritualQ2SelectedWords = emptyList()
                                        viewModel.ritualQ2Verified = false
                                        viewModel.ritualQ2Feedback = ""
                                    },
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text("Reconfigurar e Intentar de nuevo", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                    
                    3 -> {
                        // --- STEP 3: El Oráculo del Destino (True/False) ---
                        Text(
                            text = "Etapa III: El Oráculo del Destino 🔮",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.align(Alignment.Start)
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "El Oráculo del Templo de la Sabiduría te ha planteado una afirmación sagrada. Declara si es Verdadera o Falsa:",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                            modifier = Modifier.align(Alignment.Start)
                        )
                        
                        Spacer(modifier = Modifier.height(12.dp))

                        // Affirmation statement box
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.05f)
                            ),
                            border = BorderStroke(2.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)),
                            shape = RoundedCornerShape(18.dp)
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "El Ofrecimiento Sacerdotal afirma que:",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "En Náhuatl antiguo, la palabra o frase:",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                                )
                                Text(
                                    text = "\"${viewModel.ritualQ3NahuatlWord}\"",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.padding(vertical = 2.dp)
                                )
                                Text(
                                    text = "significa exactamente en español:",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                                )
                                Text(
                                    text = "\"${viewModel.ritualQ3SpanishClaim}\"",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    modifier = Modifier.padding(vertical = 2.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // True/False selection side-by-side buttons
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            // VERDADERO Button
                            val isSelectedTrue = viewModel.ritualQThirdSelectedIndex == true
                            val trueBorder = if (isSelectedTrue) Color(0xFF4CAF50) else MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
                            val trueBg = if (isSelectedTrue) Color(0xFF4CAF50).copy(alpha = 0.1f) else MaterialTheme.colorScheme.surface
                            
                            Card(
                                onClick = {
                                    if (!viewModel.ritualQ3Verified) {
                                        viewModel.ritualQThirdSelectedIndex = true
                                    }
                                },
                                modifier = Modifier
                                    .weight(1f)
                                    .height(64.dp)
                                    .testTag("oracle_true_btn"),
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(containerColor = trueBg),
                                border = BorderStroke(1.5.dp, trueBorder)
                            ) {
                                Column(
                                    modifier = Modifier.fillMaxSize(),
                                    verticalArrangement = Arrangement.Center,
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Icon(Icons.Default.Check, contentDescription = "Verdadero", tint = if (isSelectedTrue) Color(0xFF4CAF50) else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f))
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text("VERDADERO", fontSize = 11.sp, fontWeight = FontWeight.ExtraBold, color = if (isSelectedTrue) Color(0xFF4CAF50) else MaterialTheme.colorScheme.onSurface)
                                }
                            }

                            // FALSO Button
                            val isSelectedFalse = viewModel.ritualQThirdSelectedIndex == false
                            val falseBorder = if (isSelectedFalse) Color(0xFFF44336) else MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
                            val falseBg = if (isSelectedFalse) Color(0xFFF44336).copy(alpha = 0.1f) else MaterialTheme.colorScheme.surface
                            
                            Card(
                                onClick = {
                                    if (!viewModel.ritualQ3Verified) {
                                        viewModel.ritualQThirdSelectedIndex = false
                                    }
                                },
                                modifier = Modifier
                                    .weight(1f)
                                    .height(64.dp)
                                    .testTag("oracle_false_btn"),
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(containerColor = falseBg),
                                border = BorderStroke(1.5.dp, falseBorder)
                            ) {
                                Column(
                                    modifier = Modifier.fillMaxSize(),
                                    verticalArrangement = Arrangement.Center,
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Icon(Icons.Default.Close, contentDescription = "Falso", tint = if (isSelectedFalse) Color(0xFFF44336) else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f))
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text("FALSO", fontSize = 11.sp, fontWeight = FontWeight.ExtraBold, color = if (isSelectedFalse) Color(0xFFF44336) else MaterialTheme.colorScheme.onSurface)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Feedback step 3
                        if (viewModel.ritualQ3Feedback.isNotBlank()) {
                            val fCol = if (viewModel.ritualQ3IsCorrect) Color(0xFF4CAF50) else Color(0xFFF44336)
                            val fBg = if (viewModel.ritualQ3IsCorrect) Color(0xFF4CAF50).copy(alpha = 0.08f) else Color(0xFFF44336).copy(alpha = 0.08f)
                            
                            Card(
                                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(containerColor = fBg),
                                border = BorderStroke(1.dp, fCol.copy(alpha = 0.3f))
                            ) {
                                Column(modifier = Modifier.padding(10.dp)) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            imageVector = if (viewModel.ritualQ3IsCorrect) Icons.Default.CheckCircle else Icons.Default.Info,
                                            contentDescription = "Status",
                                            tint = fCol,
                                            modifier = Modifier.size(18.dp)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = if (viewModel.ritualQ3IsCorrect) "¡Felicidades Ritual Completado!" else "Oráculo Rechazado",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = fCol
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = viewModel.ritualQ3Feedback,
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.onSurface,
                                        lineHeight = 15.sp
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(12.dp))
                        }

                        // Actions
                        if (!viewModel.ritualQ3Verified) {
                            Button(
                                onClick = { viewModel.verifyStep3() },
                                enabled = viewModel.ritualQThirdSelectedIndex != null,
                                modifier = Modifier.fillMaxWidth().testTag("verify_step3_btn")
                            ) {
                                Icon(Icons.Default.AutoAwesome, contentDescription = "Proceder")
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Interrogar Oráculo", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        } else {
                            if (viewModel.ritualQ3IsCorrect) {
                                Button(
                                    onClick = { 
                                        viewModel.lessonRitualActive = false 
                                        // Auto dismiss lesson details as completed
                                        viewModel.activeLesson = null
                                    },
                                    modifier = Modifier.fillMaxWidth().testTag("complete_ritual_btn"),
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50))
                                ) {
                                    Icon(Icons.Default.Verified, contentDescription = "Éxito")
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Consolidar Ritual Sacerdotal 🎉", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                }
                            } else {
                                OutlinedButton(
                                    onClick = {
                                        viewModel.ritualQThirdSelectedIndex = null
                                        viewModel.ritualQ3Verified = false
                                        viewModel.ritualQ3Feedback = ""
                                    },
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text("Preguntar de nuevo", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {}
    )
}

// --- Hearts / Lives & Active Pet Indicator Strip ---
@Composable
fun LivesAndPetStatusStrip(viewModel: MainViewModel) {
    val progress by viewModel.userProgress.collectAsState()
    val lives = progress?.lives ?: 5
    val shields = progress?.activeShieldCharges ?: 0
    val petType = progress?.petType ?: ""
    val petName = progress?.petName ?: ""
    val petHunger = progress?.petHunger ?: 100
    val petHappiness = progress?.petHappiness ?: 100
    val isPetDead = progress?.isPetDead ?: false

    val currentLang = progress?.selectedLanguage ?: "Nahuatl"
    val completedLessons = when (currentLang) {
        "Otomi" -> progress?.completedLessonsOtomi.orEmpty().split(";").filter { it.isNotBlank() }.size
        "Totona" -> progress?.completedLessonsTotona.orEmpty().split(";").filter { it.isNotBlank() }.size
        "Maya" -> progress?.completedLessonsMaya.orEmpty().split(";").filter { it.isNotBlank() }.size
        else -> progress?.completedLessonsNahuatl.orEmpty().split(";").filter { it.isNotBlank() }.size
    }
    val gamesPlayed = when (currentLang) {
        "Otomi" -> progress?.matchesPlayedOtomi ?: 0
        "Totona" -> progress?.matchesPlayedTotona ?: 0
        "Maya" -> progress?.matchesPlayedMaya ?: 0
        else -> progress?.matchesPlayedNahuatl ?: 0
    }
    val totalPoints = completedLessons + gamesPlayed

    val (masteryLevel, masteryColor, masteryIcon) = when {
        totalPoints < 5 -> Triple("Principiante", Color(0xFF4DB6AC), "🌱")
        totalPoints < 15 -> Triple("Intermedio", Color(0xFF29B6F6), "🏹")
        else -> Triple("Avanzado", Color(0xFFFFD54F), "👑")
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF1C2228).copy(alpha = 0.85f)
        ),
        border = BorderStroke(1.dp, Color(0xFFFFB74D).copy(alpha = 0.35f)),
        shape = RoundedCornerShape(14.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp)
        ) {
            // Row 1: Lives and Divine Shields
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Left: Lives (Hearts)
                Surface(
                    color = Color(0xFFFF5252).copy(alpha = 0.15f),
                    shape = RoundedCornerShape(8.dp),
                    border = BorderStroke(1.dp, Color(0xFFFF5252).copy(alpha = 0.4f))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row {
                            for (i in 1..5) {
                                val isFilled = lives >= i
                                Icon(
                                    imageVector = if (isFilled) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                    contentDescription = "Vida $i",
                                    tint = if (isFilled) Color(0xFFFF5252) else Color.Gray.copy(alpha = 0.5f),
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "$lives/5",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = if (lives <= 1) Color(0xFFFF5252) else Color.White
                        )
                    }
                }

                // Right: Divine Shield
                if (shields > 0) {
                    Surface(
                        color = Color(0xFF00E5FF).copy(alpha = 0.15f),
                        shape = RoundedCornerShape(8.dp),
                        border = BorderStroke(1.dp, Color(0xFF00E5FF).copy(alpha = 0.5f))
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Shield,
                                contentDescription = "Escudo Divino",
                                tint = Color(0xFF00E5FF),
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "ESCUDO x$shields",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Black,
                                color = Color(0xFF00E5FF)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Row 2: Mastery Level and Pet Status
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Left: Mastery Badge
                Surface(
                    color = masteryColor.copy(alpha = 0.20f),
                    shape = RoundedCornerShape(8.dp),
                    border = BorderStroke(1.dp, masteryColor.copy(alpha = 0.6f))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "$masteryIcon $masteryLevel",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = masteryColor
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "($totalPoints p) ",
                            fontSize = 9.sp,
                            color = Color.LightGray
                        )
                    }
                }

                // Right: Pet badge
                val primaryColor = MaterialTheme.colorScheme.primary
                val (badgeText, badgeColor, badgeBg) = remember(petType, petName, petHunger, petHappiness, isPetDead, primaryColor) {
                    when {
                        petType.isEmpty() -> Triple("🛒 ADOPTA MASCOTA", Color(0xFFAB47BC), Color(0xFFAB47BC).copy(alpha = 0.20f))
                        isPetDead -> Triple("💀 $petName PERDIDO", Color(0xFFFF5252), Color(0xFFFF5252).copy(alpha = 0.20f))
                        else -> Triple("🐾 $petName ($petHunger% / $petHappiness%)", Color(0xFFFFB74D), Color(0xFFFFB74D).copy(alpha = 0.20f))
                    }
                }

                Surface(
                    color = badgeBg,
                    shape = RoundedCornerShape(8.dp),
                    border = BorderStroke(1.dp, badgeColor.copy(alpha = 0.5f)),
                    modifier = Modifier.clickable { viewModel.currentTab = 4 }
                ) {
                    Text(
                        text = badgeText,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Black,
                        color = badgeColor,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            HorizontalDivider(
                modifier = Modifier.padding(vertical = 2.dp),
                thickness = 0.5.dp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)
            )
            Spacer(modifier = Modifier.height(4.dp))

            // XP Progress Bar Row
            val xp = progress?.xp ?: 0
            val xpGoal = 100
            val ratio = ((xp % xpGoal) / xpGoal.toFloat()).coerceIn(0f, 1f)
            val level = progress?.level ?: 1

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "⭐ Nivel $level",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                Text(
                    text = "${xp % xpGoal} / $xpGoal XP para Nivel ${level + 1} (${xp} XP total)",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .fillMaxWidth(ratio)
                        .clip(CircleShape)
                        .background(
                            Brush.horizontalGradient(
                                colors = listOf(
                                    Color(0xFFFFB74D), // Beautiful Amber
                                    Color(0xFFFF9800)  // Beautiful Deep Orange
                                )
                            )
                        )
                )
            }
        }
    }
}

// --- Out of Lives Warning Dialog ---
@Composable
fun OutOfLivesDialog(viewModel: MainViewModel) {
    AlertDialog(
        onDismissRequest = { viewModel.showOutOfLivesDialog = false },
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Favorite,
                    contentDescription = "Sin vidas",
                    tint = Color(0xFFEF5350),
                    modifier = Modifier.size(28.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("¡Te has quedado sin vidas!", fontWeight = FontWeight.Black, fontSize = 18.sp)
            }
        },
        text = {
            Column {
                Text(
                    text = "El entrenamiento de la sabiduría azteca requiere energía vital. Has agotado tus 5 vidas de práctica.",
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(10.dp))
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.08f)
                    ),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Text(
                            text = "Tienes 3 caminos para restaurar tus vidas:",
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text("⏳ 1. Esperar: Se regenera 1 vida cada 10 minutos automáticamente.", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text("🛒 2. Comprar: Consigue una recarga instantánea en el Templo con tu XP.", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text("🏛️ 3. Ofrendar: Realiza un sacrificio ritual en el Templo para obtener el favor divino (¡vidas llenas y escudos protectores!).", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    viewModel.showOutOfLivesDialog = false
                    viewModel.currentTab = 5 // Go to Temple
                },
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Icon(Icons.Default.Storefront, contentDescription = "Templo", modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Visitar Templo Sagrado", fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = { viewModel.showOutOfLivesDialog = false }) {
                Text("Cerrar", fontSize = 11.sp)
            }
        }
    )
}

// --- Tab 5: Sacred Mascot and Templo Mayor Offerings ---
@Composable
fun MascotTab(viewModel: MainViewModel) {
    val progress by viewModel.userProgress.collectAsState()
    val activeLang = progress?.selectedLanguage ?: "Nahuatl"
    val xp = progress?.xp ?: 0
    val petType = progress?.petType ?: ""
    val petName = progress?.petName ?: ""
    val petHunger = progress?.petHunger ?: 100
    val petHappiness = progress?.petHappiness ?: 100
    val petAccessories = progress?.petAccessories ?: ""
    val isPetDead = progress?.isPetDead ?: false
    val activeShields = progress?.activeShieldCharges ?: 0

    var customPetName by remember { mutableStateOf("") }
    
    // Bounce and Rotation animations for interactive pet
    val bounceY by animateDpAsState(
        targetValue = if (viewModel.petBounceState) (-24).dp else 0.dp,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow),
        finishedListener = { viewModel.petBounceState = false },
        label = "pet_bounce_tab"
    )
    val rotationAngle by animateFloatAsState(
        targetValue = viewModel.petRotationState,
        animationSpec = tween(600, easing = FastOutSlowInEasing),
        label = "pet_rot_tab"
    )

    val smoothOffsetX by animateDpAsState(
        targetValue = viewModel.petOffsetX.dp,
        animationSpec = spring(dampingRatio = Spring.DampingRatioLowBouncy, stiffness = Spring.StiffnessVeryLow),
        label = "pet_smooth_x"
    )
    val smoothOffsetY by animateDpAsState(
        targetValue = viewModel.petOffsetY.dp,
        animationSpec = spring(dampingRatio = Spring.DampingRatioLowBouncy, stiffness = Spring.StiffnessVeryLow),
        label = "pet_smooth_y"
    )

    val feedingProgress by animateFloatAsState(
        targetValue = if (viewModel.isFeedingAnimating) 1.0f else 0.0f,
        animationSpec = tween(durationMillis = 1000, easing = androidx.compose.animation.core.LinearEasing),
        label = "feeding_progress"
    )

    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp)
                .testTag("mascot_tab_scroll"),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Core Banner
            item {
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.08f)
                    ),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Pets,
                                contentDescription = "Mascota",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (activeLang == "Maya") "Alak'ob: Compañeros del Mayab" else if (activeLang == "Otomi") "Mascotas Sagradas del Nitö" else if (activeLang == "Totona") "Compañeros Sagrados de Papantla" else "Compañeros Sagrados del Anáhuac",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = if (activeLang == "Maya") {
                                "Cultiva el lazo sagrado con tus compañeros animales del Mayab. Aliméntalos con maíz tierno (i'nal), tamales o cacao tradicional (kakaw), juega con ellos para elevar su felicidad y equipa sus ornamentos tradicionales en el armario."
                            } else if (activeLang == "Otomi") {
                                "Cultiva el lazo sagrado con tus compañeros animales otomíes. Aliméntalos con pulque dulce (sëi), tamales de maíz tradicional o tunas del desierto, juega con ellos y equipa sus ornamentos."
                            } else if (activeLang == "Totona") {
                                "Cultiva el lazo sagrado con tus compañeros animales de El Tajín. Aliméntalos con vainilla dulce, maíz fresco o flores aromáticas del campo, juega con ellos y decóralos con orgullo."
                            } else {
                                "Cultiva el lazo sagrado con tus compañeros animales del Anáhuac. Aliméntalos con maíz, tamales o cacao tradicional, juega con ellos para elevar su felicidad y equipa sus ornamentos tradicionales en el armario."
                            },
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                            lineHeight = 15.sp
                        )
                    }
                }
            }

            // --- Case 1: Active living pet (With Interactive Sacred Habitat!) ---
            if (petType.isNotEmpty() && !isPetDead) {
                item {
                    Text(
                        text = if (activeLang == "Maya") "💖 K'ujul Hábitat yetel Alak'" else if (activeLang == "Otomi") "💖 Hábitat Sagrado Otomí" else if (activeLang == "Totona") "💖 Hábitat Sagrado Totonaco" else "💖 Hábitat Sagrado e Interacción",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }

                item {
                    val (petEmoji, petColor) = when {
                        petType.contains("Guajolote", ignoreCase = true) -> Pair("🦃", Color(0xFF8D6E63))
                        petType.contains("Xoloitzcuintli", ignoreCase = true) || petType.contains("Pek'", ignoreCase = true) || petType.contains("Perro", ignoreCase = true) -> Pair("🐕", Color(0xFF78909C))
                        petType.contains("Guacamaya", ignoreCase = true) -> Pair("🦜", Color(0xFF29B6F6))
                        petType.contains("Águila", ignoreCase = true) || petType.contains("Xa'lh", ignoreCase = true) || petType.contains("Ch'oom", ignoreCase = true) -> Pair("🦅", Color(0xFFD4E157))
                        petType.contains("Tlacuache", ignoreCase = true) -> Pair("🦊", Color(0xFFE08E45))
                        petType.contains("Coyote", ignoreCase = true) -> Pair("🐺", Color(0xFF90A4AE))
                        petType.contains("Colibrí", ignoreCase = true) -> Pair("🐦", Color(0xFF26A69A))
                        petType.contains("Venado", ignoreCase = true) || petType.contains("Ceh", ignoreCase = true) -> Pair("🦌", Color(0xFFA1887F))
                        petType.contains("Jaguar", ignoreCase = true) || petType.contains("Zat'i", ignoreCase = true) || petType.contains("Misin", ignoreCase = true) || petType.contains("Balam", ignoreCase = true) -> Pair("🐆", Color(0xFFFFB74D))
                        else -> Pair("🐆", Color(0xFFFFB74D))
                    }

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            // Immersive Habitat box with Canvas Background
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(200.dp)
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(
                                        Brush.verticalGradient(
                                            colors = listOf(
                                                Color(0xFFE0F7FA), // Soft Aztec sky
                                                Color(0xFFFFF3E0)  // Warm ground glow
                                            )
                                        )
                                    )
                                    .clickable {
                                        viewModel.petBounceState = true
                                        // Highly distinct quotes & animations based on petType
                                        viewModel.petSpeechBubble = when (petType) {
                                            "Guajolote" -> {
                                                viewModel.petRotationState = if (viewModel.petRotationState == 0f) -12f else 0f
                                                listOf(
                                                    "¡Glú, glú! ¡Vigilo el maíz sagrado con mucho cuidado! 🦃",
                                                    "¡No me asustes, que se me caen las plumas! ¡Glú! 🌽",
                                                    "¡Aliméntame con cacao y verás mi plumaje brillar! ✨"
                                                ).random()
                                            }
                                            "Xoloitzcuintli" -> {
                                                viewModel.petRotationState = if (viewModel.petRotationState == 0f) 15f else 0f
                                                listOf(
                                                    "¡Guau! ¡Siento el calor sagrado del dios Xolotl! 🐕",
                                                    "¡Vamos a correr por los valles de Tenochtitlan! 🛕",
                                                    "¡Guau! ¡Tú eres mi guía en este viaje lingüístico!"
                                                ).random()
                                            }
                                            "Águila" -> {
                                                viewModel.petRotationState = if (viewModel.petRotationState == 0f) 25f else 0f
                                                listOf(
                                                    "¡Screeech! ¡Vuelo alto para tocar las nubes del dios Tláloc! 🦅",
                                                    "¡Desde el cielo veo el esplendor dorado del Anáhuac! ☁️",
                                                    "¡Vuela conmigo sobre el gran lago de Texcoco! 🌊"
                                                ).random()
                                            }
                                            "Tlacuache" -> {
                                                viewModel.petRotationState = if (viewModel.petRotationState == 0f) 18f else 0f
                                                listOf(
                                                    "¡Me encantan las travesuras! ¿Sabías que robé el fuego sagrado para ustedes? 🦊",
                                                    "¡Un trago de sëi (pulque) dulce y seguiré contando historias ancestrales! 🍶",
                                                    "¡Huele a maíz! El dador del fuego vigila tu aprendizaje del otomí. ✨"
                                                ).random()
                                            }
                                            "Coyote" -> {
                                                viewModel.petRotationState = if (viewModel.petRotationState == 0f) -10f else 0f
                                                listOf(
                                                    "¡Auhhh! Los espíritus del bosque hñähñu cantan conmigo esta noche. 🐺",
                                                    "¡Protejo el camino hacia el gran cerro sagrado! No temas. 🌲",
                                                    "¡Tu inteligencia brilla tanto como las estrellas del cielo otomí! ✨"
                                                ).random()
                                            }
                                            "Colibrí" -> {
                                                viewModel.petRotationState = if (viewModel.petRotationState == 0f) 30f else 0f
                                                listOf(
                                                    "¡Fiuuu! Traigo buenas vibras y mensajes de amor de los abuelos. 🐦",
                                                    "¡Bebo el néctar de las doni (flores) silvestres con agilidad! 🌸",
                                                    "¡El colibrí de Temoaya vuela contigo hacia el saber! 🕊️"
                                                ).random()
                                            }
                                            "Zat'i (Jaguar)" -> {
                                                viewModel.petRotationState = if (viewModel.petRotationState == 0f) -15f else 0f
                                                listOf(
                                                    "¡Grrr! El jaguar otomí corre libre bajo el cobijo de la zänä (luna). 🐆",
                                                    "¡El poder de la tierra hñähñu fluye en tus venas! ¡Sigue practicando! 🐾",
                                                    "¡Vigilo el templo con garras fuertes y ojos brillantes!"
                                                ).random()
                                            }
                                            "Guacamaya" -> {
                                                viewModel.petRotationState = if (viewModel.petRotationState == 0f) 20f else 0f
                                                listOf(
                                                    "¡Cuaaac! ¡Soy la guacamaya verde de El Tajín! ¡Qué alegre cantar! 🦜",
                                                    "¡Tlan tlan! Saludo al gran sol totonaco con mis alas de vainilla. 🌸",
                                                    "¡Mis plumas brillan con los bellos colores de Papantla!"
                                                ).random()
                                            }
                                            "Águila (Xa'lh)" -> {
                                                viewModel.petRotationState = if (viewModel.petRotationState == 0f) 22f else 0f
                                                listOf(
                                                    "¡Screee! ¡Guío el sagrado vuelo de los Voladores de Papantla! 🦅",
                                                    "¡Desde el cielo totonaca cuido tus cosechas y tu sabiduría! 🌽",
                                                    "¡Vuelo alto sobre la gran pirámide de los Nichos!"
                                                ).random()
                                            }
                                            "Misin (Jaguar)" -> {
                                                viewModel.petRotationState = if (viewModel.petRotationState == 0f) -15f else 0f
                                                listOf(
                                                    "¡Grrr! El jaguar de la selva totonaca vigila tus lecciones. 🐆",
                                                    "¡Siente la fuerza y el espíritu indomable de El Tajín! 🐾",
                                                    "¡Mis manchas representan las estrellas sagradas de nuestro cielo!"
                                                ).random()
                                            }
                                            "Pek' (Perro)" -> {
                                                viewModel.petRotationState = if (viewModel.petRotationState == 0f) 12f else 0f
                                                listOf(
                                                    "¡Guau! ¡Soy Pek', tu fiel guardián en la tierra y el Xibalbá! 🐕",
                                                    "¡Maalob k'iin! ¡Que tengas un día grandioso aprendiendo maya! ☀️",
                                                    "¡Mueve la colita! ¡Tu esfuerzo agrada a los dioses del cielo!"
                                                ).random()
                                            }
                                            "Ceh (Venado)" -> {
                                                viewModel.petRotationState = if (viewModel.petRotationState == 0f) -10f else 0f
                                                listOf(
                                                    "¡Fsssh! Corro libre entre las selvas de Yucatán y Quintana Roo. 🦌",
                                                    "¡Soy el ciervo sagrado del Mayab, cuidando la naturaleza! 🌳",
                                                    "¡Avanzas rápido! Los espíritus del monte admiran tu disciplina."
                                                ).random()
                                            }
                                            "Ch'oom (Águila)" -> {
                                                viewModel.petRotationState = if (viewModel.petRotationState == 0f) 25f else 0f
                                                listOf(
                                                    "¡Screeech! ¡Vuelo entre las místicas ruinas de Tulum y Chichén Itzá! 🦅",
                                                    "¡Soy el mensajero divino del dios Itzamná desde el cielo celestial! ✨",
                                                    "¡Tu mente se eleva tan alto como mis alas sagradas!"
                                                ).random()
                                            }
                                            "Balam (Jaguar)" -> {
                                                viewModel.petRotationState = if (viewModel.petRotationState == 0f) -15f else 0f
                                                listOf(
                                                    "¡Grrr! ¡Soy el sagrado Balam, protector de los templos mayas! 🐆",
                                                    "¡La sabiduría de los antiguos sacerdotes astrónomos te acompaña! 🌌",
                                                    "¡Vigilo el inframundo y la selva con garras divinas de luz!"
                                                ).random()
                                            }
                                            else -> { // Jaguar / Ocelotl
                                                viewModel.petRotationState = if (viewModel.petRotationState == 0f) -15f else 0f
                                                listOf(
                                                    "¡Grrr! El sigiloso guerrero Ocelotl acecha en las sombras... 🐆",
                                                    "¡Ruge conmigo! ¡La fuerza del jaguar vive en ti! 🐾",
                                                    "¡Mi agilidad nocturna protegerá tu sabiduría de náhuatl!"
                                                ).random()
                                            }
                                        }
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                // Draw simple sacred elements on canvas
                                Canvas(modifier = Modifier.fillMaxSize()) {
                                    // Draw a glowing sun in upper corner
                                    drawCircle(
                                        color = Color(0xFFFFE082).copy(alpha = 0.6f),
                                        radius = 45f,
                                        center = androidx.compose.ui.geometry.Offset(size.width * 0.85f, size.height * 0.2f)
                                    )
                                    
                                    // Draw subtle green hills
                                    val path = androidx.compose.ui.graphics.Path().apply {
                                        moveTo(0f, size.height)
                                        quadraticTo(size.width * 0.25f, size.height * 0.75f, size.width * 0.6f, size.height)
                                        moveTo(size.width * 0.4f, size.height)
                                        quadraticTo(size.width * 0.8f, size.height * 0.82f, size.width, size.height)
                                        close()
                                    }
                                    drawPath(path, color = Color(0xFFC8E6C9).copy(alpha = 0.4f))
                                }

                                // Interactive Pet character column (Jumps, Rotates, and Roams/Drags!)
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    modifier = Modifier
                                        .offset(x = smoothOffsetX, y = smoothOffsetY + bounceY)
                                        .pointerInput(Unit) {
                                            detectDragGestures { change, dragAmount ->
                                                change.consume()
                                                viewModel.petOffsetX = (viewModel.petOffsetX + dragAmount.x / 4f).coerceIn(-130f, 130f)
                                                viewModel.petOffsetY = (viewModel.petOffsetY + dragAmount.y / 4f).coerceIn(-90f, 90f)
                                            }
                                        }
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        // Draw a glowing halo behind pet
                                        Canvas(modifier = Modifier.size(130.dp)) {
                                            drawCircle(
                                                color = petColor.copy(alpha = 0.18f),
                                                radius = size.width / 2
                                            )
                                        }

                                        // Render Equipped Accessories visually ON the animal
                                        Box(
                                            modifier = Modifier.size(120.dp),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            // Underlay back accessory: Capa de Manta (🧥)
                                            if (viewModel.activeEquippedAccessories.contains("Capa de Manta")) {
                                                Text(
                                                    text = "🧥",
                                                    fontSize = 54.sp,
                                                    modifier = Modifier
                                                        .align(Alignment.BottomCenter)
                                                        .offset(y = 12.dp)
                                                        .graphicsLayer(alpha = 0.9f, scaleX = 1.3f, scaleY = 1.1f)
                                                        .testTag("equipped_capa")
                                                )
                                            }

                                            // Main Pet Emoji (Much larger!)
                                            Text(
                                                text = petEmoji,
                                                fontSize = 84.sp,
                                                modifier = Modifier.graphicsLayer(rotationZ = rotationAngle)
                                            )

                                            // Collar de Jade: Beaded green circles canvas wrapped precisely around neck
                                            if (viewModel.activeEquippedAccessories.contains("Collar de Jade")) {
                                                Canvas(modifier = Modifier.size(110.dp)) {
                                                    val beadCount = 9
                                                    val radiusX = size.width * 0.32f
                                                    val radiusY = size.height * 0.15f
                                                    val centerX = size.width * 0.5f
                                                    val centerY = size.height * 0.64f
                                                    
                                                    for (i in 0 until beadCount) {
                                                        val angleRad = Math.toRadians((180.0 + (i - beadCount/2) * 22.0)).toFloat()
                                                        val bx = centerX + radiusX * kotlin.math.cos(angleRad)
                                                        val by = centerY + radiusY * kotlin.math.sin(angleRad)
                                                        
                                                        drawCircle(
                                                            color = Color(0xFF00E676),
                                                            radius = 6f,
                                                            center = androidx.compose.ui.geometry.Offset(bx, by)
                                                        )
                                                        drawCircle(
                                                            color = Color.White.copy(alpha = 0.8f),
                                                            radius = 1.8f,
                                                            center = androidx.compose.ui.geometry.Offset(bx - 1.5f, by - 1.5f)
                                                        )
                                                    }
                                                }

                                                Text(
                                                    text = "📿",
                                                    fontSize = 34.sp,
                                                    modifier = Modifier
                                                        .align(Alignment.BottomCenter)
                                                        .offset(y = (-2).dp)
                                                        .testTag("equipped_collar")
                                                )
                                            }

                                            // Penacho Sagrado: Placed exactly on head based on animal type
                                            if (viewModel.activeEquippedAccessories.contains("Penacho Sagrado")) {
                                                val headOffset = when (petType) {
                                                    "Guajolote" -> Pair((-4).dp, (-42).dp)
                                                    "Xoloitzcuintli" -> Pair(0.dp, (-45).dp)
                                                    "Águila" -> Pair(3.dp, (-42).dp)
                                                    "Tlacuache" -> Pair(0.dp, (-43).dp)
                                                    "Coyote" -> Pair(0.dp, (-45).dp)
                                                    "Colibrí" -> Pair(2.dp, (-40).dp)
                                                    "Zat'i (Jaguar)" -> Pair(0.dp, (-44).dp)
                                                    "Guacamaya" -> Pair(1.dp, (-42).dp)
                                                    "Águila (Xa'lh)" -> Pair(3.dp, (-42).dp)
                                                    "Misin (Jaguar)" -> Pair(0.dp, (-44).dp)
                                                    "Pek' (Perro)" -> Pair(0.dp, (-45).dp)
                                                    "Ceh (Venado)" -> Pair(0.dp, (-46).dp)
                                                    "Ch'oom (Águila)" -> Pair(3.dp, (-42).dp)
                                                    "Balam (Jaguar)" -> Pair(0.dp, (-44).dp)
                                                    else -> Pair(0.dp, (-44).dp) // Jaguar
                                                }
                                                Text(
                                                    text = "👑",
                                                    fontSize = 42.sp,
                                                    modifier = Modifier
                                                        .align(Alignment.TopCenter)
                                                        .offset(x = headOffset.first, y = headOffset.second)
                                                        .testTag("equipped_penacho")
                                                )
                                            }

                                            // Equip/Toggle Sparkle Flash
                                            if (viewModel.petAccessoryEquippedFlash != null) {
                                                Text(
                                                    text = "✨   ✨\n  🌟  \n✨   ✨",
                                                    fontSize = 32.sp,
                                                    modifier = Modifier.graphicsLayer(scaleX = 1.4f, scaleY = 1.4f, alpha = 0.85f)
                                                )
                                            }
                                        }

                                        // Flying food animation
                                        if (viewModel.isFeedingAnimating && viewModel.feedingFoodItem != null) {
                                            val foodEmoji = when (viewModel.feedingFoodItem) {
                                                "Maíz" -> "🌽"
                                                "Tamal" -> "🫔"
                                                else -> "☕"
                                            }
                                            
                                            val startX = 130.dp
                                            val endX = 0.dp
                                            val startY = 150.dp
                                            val endY = 0.dp
                                            
                                            val currentX = startX - (startX * feedingProgress)
                                            val currentY = startY - (startY * feedingProgress)
                                            
                                            Text(
                                                text = foodEmoji,
                                                fontSize = (38 - (16 * feedingProgress).toInt()).sp,
                                                modifier = Modifier
                                                    .offset(x = currentX, y = currentY)
                                                    .graphicsLayer(
                                                        rotationZ = feedingProgress * 360f,
                                                        alpha = if (feedingProgress > 0.85f) 1f - (feedingProgress - 0.85f) * 6.6f else 1f
                                                    )
                                            )
                                            
                                            if (feedingProgress > 0.75f) {
                                                // Crunch/Heart chewing particles
                                                Box(modifier = Modifier.size(150.dp)) {
                                                    Text("❤️", fontSize = 16.sp, modifier = Modifier.align(Alignment.TopStart).offset(x = 12.dp, y = 24.dp))
                                                    Text("✨", fontSize = 18.sp, modifier = Modifier.align(Alignment.TopEnd).offset(x = (-12).dp, y = 28.dp))
                                                    Text("❤️", fontSize = 14.sp, modifier = Modifier.align(Alignment.BottomStart).offset(x = 18.dp, y = (-24).dp))
                                                    Text("✨", fontSize = 16.sp, modifier = Modifier.align(Alignment.BottomEnd).offset(x = (-18).dp, y = (-18).dp))
                                                }
                                                
                                                Text(
                                                    text = "¡NOM NOM! 😋",
                                                    color = Color(0xFFD84315),
                                                    fontWeight = FontWeight.Black,
                                                    fontSize = 11.sp,
                                                    modifier = Modifier
                                                        .offset(y = (-55).dp)
                                                        .background(Color.White.copy(alpha = 0.9f), RoundedCornerShape(8.dp))
                                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                                )
                                            }
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = petName,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = MaterialTheme.colorScheme.onSurface,
                                        modifier = Modifier
                                            .background(Color.White.copy(alpha = 0.8f), RoundedCornerShape(6.dp))
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }

                                // Interactive Speech Bubble
                                Card(
                                    modifier = Modifier
                                        .align(Alignment.TopStart)
                                        .padding(10.dp)
                                        .widthIn(max = 210.dp),
                                    shape = RoundedCornerShape(12.dp),
                                    colors = CardDefaults.cardColors(containerColor = Color.White),
                                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                                ) {
                                    Text(
                                        text = viewModel.petSpeechBubble,
                                        fontSize = 10.sp,
                                        color = Color.DarkGray,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(8.dp),
                                        lineHeight = 13.sp
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            // Stats Indicators
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text("Alimentación: $petHunger/100", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    Spacer(modifier = Modifier.height(2.dp))
                                    LinearProgressIndicator(
                                        progress = petHunger / 100f,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(8.dp)
                                            .clip(CircleShape),
                                        color = if (petHunger < 35) Color(0xFFE53935) else Color(0xFF4CAF50),
                                        trackColor = MaterialTheme.colorScheme.surfaceVariant
                                    )
                                }
                                Column(modifier = Modifier.weight(1f)) {
                                    Text("Felicidad: $petHappiness/100", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    Spacer(modifier = Modifier.height(2.dp))
                                    LinearProgressIndicator(
                                        progress = petHappiness / 100f,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(8.dp)
                                            .clip(CircleShape),
                                        color = if (petHappiness < 35) Color(0xFFE53935) else Color(0xFFFFB74D),
                                        trackColor = MaterialTheme.colorScheme.surfaceVariant
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            // Sacred Backpack (Inventario)
                            Text(
                                text = "👜 Tu Mochila de Provisiones Sagradas:",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                val myItems = listOf(
                                    Triple("Maíz", "Centli 🌽", viewModel.inventoryMaizCount),
                                    Triple("Tamal", "🫔 Tradicional", viewModel.inventoryTamalCount),
                                    Triple("Cacao", "Ceremonial ☕", viewModel.inventoryCacaoCount)
                                )
                                myItems.forEach { (type, label, count) ->
                                    Card(
                                        modifier = Modifier
                                            .weight(1f)
                                            .clickable { viewModel.feedPetFromInventory(type) },
                                        shape = RoundedCornerShape(10.dp),
                                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)),
                                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
                                    ) {
                                        Column(
                                            modifier = Modifier.padding(8.dp),
                                            horizontalAlignment = Alignment.CenterHorizontally
                                        ) {
                                            Text(label, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                            Text("Cantidad: $count", fontSize = 9.sp, color = Color.Gray)
                                            Spacer(modifier = Modifier.height(4.dp))
                                            Text(
                                                text = "Dar de comer",
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = MaterialTheme.colorScheme.primary
                                            )
                                        }
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            // Play and interact button
                            Button(
                                onClick = { viewModel.playWithPet() },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Text("👋 Acariciar y Jugar con $petName (Gratis, +15 Felicidad)", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }

                // --- Equipment / Closet Wardrobe Section ---
                item {
                    Text(
                        text = "👑 Armario de Equipamiento",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }

                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            val currentAccs = petAccessories.split(",").filter { it.isNotBlank() }.toSet()
                            if (currentAccs.isEmpty()) {
                                Text(
                                    text = "No has comprado accesorios para vestir a tu mascota en el armario. ¡Cómpralos abajo en la tienda!",
                                    fontSize = 11.sp,
                                    color = Color.Gray,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
                                )
                            } else {
                                Text(
                                    text = "Viste a tu mascota tocando los accesorios que has adquirido:",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.padding(bottom = 8.dp)
                                )
                                currentAccs.forEach { acc ->
                                    val isEquipped = viewModel.activeEquippedAccessories.contains(acc)
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 4.dp)
                                            .background(
                                                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f),
                                                RoundedCornerShape(8.dp)
                                            )
                                            .padding(8.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(acc, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                        Button(
                                            onClick = { viewModel.toggleEquipAccessory(acc) },
                                            colors = ButtonDefaults.buttonColors(
                                                containerColor = if (isEquipped) Color(0xFFEF5350) else MaterialTheme.colorScheme.primary
                                            ),
                                            shape = RoundedCornerShape(6.dp),
                                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                                            modifier = Modifier.height(28.dp)
                                        ) {
                                            Text(if (isEquipped) "Desequipar" else "Equipar", fontSize = 9.sp)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // --- Adoption Shop Section (Only if petType is empty) ---
            if (petType.isEmpty()) {
                item {
                    Text(
                        text = "🐾 Adopción de Mascotas Sagradas",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }

                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "Asigna un nombre sagrado para tu mascota:",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            OutlinedTextField(
                                value = customPetName,
                                onValueChange = { customPetName = it },
                                placeholder = { Text(if (activeLang == "Otomi") "Ej. T'emwi, Zat'i..." else if (activeLang == "Totona") "Ej. Misin, Luwa, Xa'lh..." else if (activeLang == "Maya") "Ej. Balam, Ceh, Pek'..." else "Ej. Xolotl, Tochtli, Ocelotl...", fontSize = 11.sp) },
                                modifier = Modifier.fillMaxWidth(),
                                textStyle = androidx.compose.ui.text.TextStyle(fontSize = 12.sp),
                                singleLine = true
                            )
                            Spacer(modifier = Modifier.height(12.dp))

                            val availablePets = if (activeLang == "Otomi") {
                                listOf(
                                    Triple("Tlacuache", "🦊 El sagrado dador del fuego y el pulque según los antiguos hñähñu.", 200),
                                    Triple("Coyote", "🐺 Guardián espiritual de la noche y el bosque.", 300),
                                    Triple("Colibrí", "🐦 Mensajero celestial hñähñu portador de buenos deseos.", 400),
                                    Triple("Zat'i (Jaguar)", "🐆 Gran espíritu de la noche hñähñu.", 500)
                                )
                            } else if (activeLang == "Totona") {
                                listOf(
                                    Triple("Guacamaya", "🦜 La colorida guacamaya verde, mensajera alegre de Papantla.", 200),
                                    Triple("Tlacuache", "🦊 El astuto dador de la vainilla y protector de la siembra.", 300),
                                    Triple("Águila (Xa'lh)", "🦅 Guía solar de los majestuosos Voladores de Papantla.", 400),
                                    Triple("Misin (Jaguar)", "🐆 El gran espíritu protector de la selva totonaca.", 500)
                                )
                            } else if (activeLang == "Maya") {
                                listOf(
                                    Triple("Pek' (Perro)", "🐕 El fiel compañero maya de la vida terrenal y espiritual.", 200),
                                    Triple("Ceh (Venado)", "🦌 Animal sagrado de la selva y la cacería ceremonial.", 300),
                                    Triple("Ch'oom (Águila)", "🦅 Mensajero majestuoso del dios del cielo Itzamná.", 400),
                                    Triple("Balam (Jaguar)", "🐆 El sagrado guardián de los misterios y de la selva maya.", 500)
                                )
                            } else {
                                listOf(
                                    Triple("Guajolote", "🦃 Sencillo y sagrado protector hogareño.", 200),
                                    Triple("Xoloitzcuintli", "🐕 Sagrado perro guía hacia el Mictlán.", 300),
                                    Triple("Águila", "🦅 Mensajero solar de las alturas celestiales.", 400),
                                    Triple("Jaguar", "🐆 El espíritu de la noche y el inframundo.", 500)
                                )
                            }

                            availablePets.forEach { (type, desc, cost) ->
                                val canAfford = xp >= cost
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 6.dp)
                                        .background(
                                            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                                            RoundedCornerShape(8.dp)
                                        )
                                        .padding(10.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = type,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 12.sp,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                        Text(
                                            text = desc,
                                            fontSize = 10.sp,
                                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Button(
                                        onClick = { 
                                            viewModel.buyPet(type, customPetName, cost)
                                            customPetName = ""
                                        },
                                        enabled = canAfford,
                                        shape = RoundedCornerShape(8.dp),
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = if (canAfford) MaterialTheme.colorScheme.primary else Color.Gray
                                        ),
                                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                                    ) {
                                        Text("$cost XP", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }
                }
            } else if (isPetDead) {
                // --- Altar de Resurrección block ---
                item {
                    Text(
                        text = if (activeLang == "Maya") "💀 Altar de Resurrección de Xibalbá" else if (activeLang == "Otomi") "💀 Altar de Resurrección del Nitö" else if (activeLang == "Totona") "💀 Altar del Trueno de Papantla" else "💀 Altar de Resurrección del Mictlán",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFEF5350)
                    )
                }

                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFEF5350).copy(alpha = 0.05f))
                    ) {
                        Column(
                            modifier = Modifier
                                .padding(16.dp)
                                .fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "¡Tu mascota, $petName ($petType), se ha perdido!",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFEF5350),
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = if (activeLang == "Maya") {
                                    "Por falta de alimentación o cuidados adecuados, su espíritu ha descendido a Xibalbá, el inframundo maya gobernado por Hun Camé y Vucub Camé. Para rescatarla de vuelta al mundo de los vivos, debes realizar una gran ofrenda ritual de sabiduría."
                                } else if (activeLang == "Otomi") {
                                    "Por falta de alimentación o cuidados adecuados, su espíritu ha descendido al Nitö, el inframundo otomí custodiado por las fuerzas de la noche. Para rescatarla de vuelta al mundo de los vivos, debes realizar una gran ofrenda de sabiduría."
                                } else if (activeLang == "Totona") {
                                    "Por falta de alimentación o cuidados adecuados, su espíritu ha descendido a la morada del Trueno de El Tajín. Para rescatarla de vuelta al mundo de los vivos, debes realizar una gran ofrenda de sabiduría."
                                } else {
                                    "Por falta de alimentación o cuidados adecuados, su espíritu ha descendido al inframundo mesoamericano gobernado por Mictlantecuhtli. Para rescatarla de vuelta al mundo de los vivos, debes realizar una gran ofrenda ritual de sabiduría."
                                },
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f),
                                textAlign = TextAlign.Center,
                                lineHeight = 15.sp
                            )
                            Spacer(modifier = Modifier.height(14.dp))
                            
                            val canAffordRes = xp >= 500
                            Button(
                                onClick = { viewModel.recoverPet() },
                                enabled = canAffordRes,
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF5350)),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Icon(Icons.Default.AutoAwesome, contentDescription = "Resucitar", modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Ofrecer 500 XP de Ofrenda", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                            if (!canAffordRes) {
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = "Necesitas 500 XP (Tienes: $xp XP). ¡Sigue practicando lecciones!",
                                    fontSize = 9.sp,
                                    color = Color.Gray,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            } else {
                // If they already have an active companion, display a message that they cannot adopt another one!
                item {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "🐾 Tienes un compañero sagrado activo ($petName). No puedes adoptar otra mascota simultáneamente. ¡Dedica tu amor y cuidados a tu actual guardián!",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Gray,
                            modifier = Modifier.padding(12.dp),
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }

            // --- Stores / Purchasing list (Buy food & accessories to inventory!) ---
            if (petType.isNotEmpty() && !isPetDead) {
                item {
                    Text(
                        text = "🛒 Tienda del Altar (Compra provisiones con XP)",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }

                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "🍖 Comprar Alimentos para tu Mochila:",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(8.dp))

                            val foods = listOf(
                                Triple("Maíz", 20, "Maíz Sagrado (Centli) +25 Energía"),
                                Triple("Tamal", 40, "Tamal tradicional azteca +50 Energía"),
                                Triple("Cacao", 30, "Cacao Ceremonial sagrado +35 Energía / +35 Felicidad")
                            )

                            foods.forEach { (type, cost, desc) ->
                                val canAfford = xp >= cost
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp)
                                        .background(
                                            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f),
                                            RoundedCornerShape(8.dp)
                                        )
                                        .padding(8.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(type, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                        Text(desc, fontSize = 9.sp, color = Color.Gray)
                                    }
                                    Button(
                                        onClick = { viewModel.buyFoodToInventory(type, cost) },
                                        enabled = canAfford,
                                        shape = RoundedCornerShape(6.dp),
                                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                                        modifier = Modifier.height(28.dp)
                                    ) {
                                        Text("$cost XP", fontSize = 9.sp)
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            Text(
                                text = "👑 Adquirir Accesorios para el Armario:",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(8.dp))

                            val accessoriesList = listOf(
                                Triple("Penacho Sagrado", 150, "Penacho de plumas de quetzal sagrado."),
                                Triple("Capa de Manta", 100, "Capa tradicional bordada."),
                                Triple("Collar de Jade", 80, "Precioso collar verde brillante.")
                            )

                            accessoriesList.forEach { (name, cost, desc) ->
                                val currentAccs = petAccessories.split(",").filter { it.isNotBlank() }.toSet()
                                val alreadyHas = currentAccs.contains(name)
                                val canAfford = xp >= cost && !alreadyHas

                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp)
                                        .background(
                                            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f),
                                            RoundedCornerShape(8.dp)
                                        )
                                        .padding(8.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(name, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                        Text(desc, fontSize = 9.sp, color = Color.Gray)
                                    }
                                    Button(
                                        onClick = { viewModel.buyAccessory(name, cost) },
                                        enabled = canAfford,
                                        shape = RoundedCornerShape(6.dp),
                                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                                        modifier = Modifier.height(28.dp)
                                    ) {
                                        Text(if (alreadyHas) "Adquirido" else "$cost XP", fontSize = 9.sp)
                                    }
                                }
                            }
                        }
                    }
                }
            }

        }

        // Animated full screen sacrifice overlay
        if (viewModel.activeSacrificeCeremony != null) {
            CeremoniaTemploOverlay(viewModel)
        }
    }
}

// --- Tab 6: Sacred Templo Mayor of Offerings and Sacrifices ---
@Composable
fun TempleTab(viewModel: MainViewModel) {
    val progress by viewModel.userProgress.collectAsState()
    val cachedAudios by viewModel.cachedSacerdoteAudios.collectAsState()
    val cachedCount by viewModel.cachedSacerdoteCount.collectAsState()
    val xp = progress?.xp ?: 0
    val petType = progress?.petType ?: ""
    val petName = progress?.petName ?: ""
    val isPetDead = progress?.isPetDead ?: false
    val activeShields = progress?.activeShieldCharges ?: 0
    val currentLang = progress?.selectedLanguage ?: "Nahuatl"
    val isOtomi = currentLang == "Otomi"

    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp)
                .testTag("temple_tab_scroll"),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Core Interactive Temple Banner with majestic Canvas Drawing!
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column {
                        // Immersive Ceremonial Canvas (Aztec Pyramid or Temoaya Monoliths!)
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(220.dp)
                                .clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
                                .background(
                                    if (isOtomi) {
                                        Brush.verticalGradient(
                                            colors = listOf(
                                                Color(0xFF0D5331), // Pine Forest green
                                                Color(0xFF004D40)  // Mountain mist teal
                                            )
                                        )
                                    } else if (currentLang == "Totona") {
                                        Brush.verticalGradient(
                                            colors = listOf(
                                                Color(0xFF1B5E20), // Tropical forest green
                                                Color(0xFFE65100)  // Papantla sunset vanilla orange
                                            )
                                        )
                                    } else if (currentLang == "Maya") {
                                        Brush.verticalGradient(
                                            colors = listOf(
                                                Color(0xFF004D40), // Sacred Maya jade green
                                                Color(0xFF00BFA5)  // Tropical emerald/turquoise sky
                                            )
                                        )
                                    } else {
                                        Brush.verticalGradient(
                                            colors = listOf(
                                                Color(0xFF210C04), // Dark night twilight over Tenochtitlan
                                                Color(0xFF4E1D0C)  // Warm glow of sacred fires
                                            )
                                        )
                                    }
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Canvas(modifier = Modifier.fillMaxSize()) {
                                val w = size.width
                                val h = size.height

                                if (isOtomi) {
                                    // --- DRAW CENTRO CEREMONIAL OTOMÍ (TEMOAYA) ---
                                    // Draw mountain peaks in background
                                    val mountainPath = androidx.compose.ui.graphics.Path().apply {
                                        moveTo(0f, h * 0.7f)
                                        lineTo(w * 0.25f, h * 0.4f)
                                        lineTo(w * 0.5f, h * 0.6f)
                                        lineTo(w * 0.75f, h * 0.35f)
                                        lineTo(w, h * 0.65f)
                                        lineTo(w, h)
                                        lineTo(0f, h)
                                        close()
                                    }
                                    drawPath(mountainPath, color = Color(0xFF1B4D3E).copy(alpha = 0.5f))

                                    // Draw glowing Otomi solar calendar star in center sky
                                    drawCircle(
                                        color = Color(0xFFFF80AB).copy(alpha = 0.3f),
                                        radius = 90f,
                                        center = androidx.compose.ui.geometry.Offset(w * 0.5f, h * 0.35f)
                                    )
                                    drawCircle(
                                        color = Color(0xFFE91E63).copy(alpha = 0.2f),
                                        radius = 60f,
                                        center = androidx.compose.ui.geometry.Offset(w * 0.5f, h * 0.35f)
                                    )

                                    // Draw stone concentric tiers (Plaza)
                                    drawOval(
                                        color = Color(0xFF8D8D8D),
                                        topLeft = androidx.compose.ui.geometry.Offset(w * 0.1f, h * 0.6f),
                                        size = androidx.compose.ui.geometry.Size(w * 0.8f, h * 0.6f)
                                    )
                                    drawOval(
                                        color = Color(0xFF616161),
                                        topLeft = androidx.compose.ui.geometry.Offset(w * 0.2f, h * 0.68f),
                                        size = androidx.compose.ui.geometry.Size(w * 0.6f, h * 0.45f)
                                    )

                                    // Draw majestic stone monolithic stele pillars standing in semi-circle
                                    val pillarWidth = 14f
                                    val pillarHeight = 70f
                                    val pillarsX = listOf(w * 0.25f, w * 0.35f, w * 0.45f, w * 0.55f, w * 0.65f, w * 0.75f)
                                    pillarsX.forEach { px ->
                                        drawRect(
                                            color = Color(0xFF9E9E9E),
                                            topLeft = androidx.compose.ui.geometry.Offset(px - pillarWidth/2, h * 0.55f),
                                            size = androidx.compose.ui.geometry.Size(pillarWidth, pillarHeight)
                                        )
                                        // Red Brick accents on pillars
                                        drawRect(
                                            color = Color(0xFFE91E63),
                                            topLeft = androidx.compose.ui.geometry.Offset(px - pillarWidth/2, h * 0.55f),
                                            size = androidx.compose.ui.geometry.Size(pillarWidth, 6f)
                                        )
                                    }

                                    // Glowing center fire / copal alter
                                    drawCircle(
                                        color = Color(0xFFFF5252).copy(alpha = 0.8f),
                                        radius = 16f,
                                        center = androidx.compose.ui.geometry.Offset(w * 0.5f, h * 0.75f)
                                    )
                                    drawCircle(
                                        color = Color(0xFFFFD740),
                                        radius = 8f,
                                        center = androidx.compose.ui.geometry.Offset(w * 0.5f, h * 0.75f)
                                    )
                                } else if (currentLang == "Totona") {
                                    // --- DRAW TOTONACO STEPPED PYRAMID WITH NICHES (EL TAJÍN) ---
                                    // Golden tropical sun in the sky
                                    drawCircle(
                                        color = Color(0xFFFFD54F).copy(alpha = 0.35f),
                                        radius = 90f,
                                        center = androidx.compose.ui.geometry.Offset(w * 0.5f, h * 0.35f)
                                    )
                                    drawCircle(
                                        color = Color(0xFFFFEB3B).copy(alpha = 0.2f),
                                        radius = 60f,
                                        center = androidx.compose.ui.geometry.Offset(w * 0.5f, h * 0.35f)
                                    )

                                    // Let's draw 4 stepped tiers of El Tajín (with beautiful niches)
                                    val pyramidColor = Color(0xFF8D6E63)
                                    val nicheColor = Color(0xFF3E2723)

                                    // Tier 1 (Base)
                                    val t1Left = w * 0.12f
                                    val t1Right = w * 0.88f
                                    val t1Top = h * 0.83f
                                    val t1Path = androidx.compose.ui.graphics.Path().apply {
                                        moveTo(t1Left, h * 0.95f)
                                        lineTo(t1Left + 20f, t1Top)
                                        lineTo(t1Right - 20f, t1Top)
                                        lineTo(t1Right, h * 0.95f)
                                        close()
                                    }
                                    drawPath(t1Path, color = pyramidColor)

                                    // Draw niches on Tier 1
                                    val numNichesT1 = 8
                                    val spacingT1 = (t1Right - t1Left - 60f) / (numNichesT1 + 1)
                                    for (i in 1..numNichesT1) {
                                        val cx = t1Left + 30f + i * spacingT1
                                        drawRect(
                                            color = nicheColor,
                                            topLeft = androidx.compose.ui.geometry.Offset(cx - 6f, h * 0.86f),
                                            size = androidx.compose.ui.geometry.Size(12f, 12f)
                                        )
                                    }

                                    // Tier 2 (Middle-Lower)
                                    val t2Left = t1Left + 30f
                                    val t2Right = t1Right - 30f
                                    val t2Top = h * 0.69f
                                    val t2Path = androidx.compose.ui.graphics.Path().apply {
                                        moveTo(t2Left, t1Top)
                                        lineTo(t2Left + 18f, t2Top)
                                        lineTo(t2Right - 18f, t2Top)
                                        lineTo(t2Right, t1Top)
                                        close()
                                    }
                                    drawPath(t2Path, color = pyramidColor.copy(alpha = 0.95f))

                                    // Draw niches on Tier 2
                                    val numNichesT2 = 6
                                    val spacingT2 = (t2Right - t2Left - 40f) / (numNichesT2 + 1)
                                    for (i in 1..numNichesT2) {
                                        val cx = t2Left + 20f + i * spacingT2
                                        drawRect(
                                            color = nicheColor,
                                            topLeft = androidx.compose.ui.geometry.Offset(cx - 5f, h * 0.73f),
                                            size = androidx.compose.ui.geometry.Size(10f, 10f)
                                        )
                                    }

                                    // Tier 3 (Middle-Upper)
                                    val t3Left = t2Left + 28f
                                    val t3Right = t2Right - 28f
                                    val t3Top = h * 0.54f
                                    val t3Path = androidx.compose.ui.graphics.Path().apply {
                                        moveTo(t3Left, t2Top)
                                        lineTo(t3Left + 16f, t3Top)
                                        lineTo(t3Right - 16f, t3Top)
                                        lineTo(t3Right, t2Top)
                                        close()
                                    }
                                    drawPath(t3Path, color = pyramidColor.copy(alpha = 0.9f))

                                    // Draw niches on Tier 3
                                    val numNichesT3 = 4
                                    val spacingT3 = (t3Right - t3Left - 30f) / (numNichesT3 + 1)
                                    for (i in 1..numNichesT3) {
                                        val cx = t3Left + 15f + i * spacingT3
                                        drawRect(
                                            color = nicheColor,
                                            topLeft = androidx.compose.ui.geometry.Offset(cx - 5f, h * 0.58f),
                                            size = androidx.compose.ui.geometry.Size(10f, 10f)
                                        )
                                    }

                                    // Tier 4 (Top platform & shrine)
                                    val t4Left = t3Left + 25f
                                    val t4Right = t3Right - 25f
                                    val t4Top = h * 0.40f
                                    drawRect(
                                        color = pyramidColor.copy(alpha = 0.85f),
                                        topLeft = androidx.compose.ui.geometry.Offset(t4Left, t4Top),
                                        size = androidx.compose.ui.geometry.Size(t4Right - t4Left, h * 0.14f)
                                    )
                                    // A nice little niche on the top shrine
                                    drawRect(
                                        color = nicheColor,
                                        topLeft = androidx.compose.ui.geometry.Offset(w * 0.5f - 6f, h * 0.44f),
                                        size = androidx.compose.ui.geometry.Size(12f, 12f)
                                    )

                                    // Central Sacred Stairs down the center of El Tajín
                                    val stairsPath = androidx.compose.ui.graphics.Path().apply {
                                        moveTo(w * 0.47f, h * 0.40f)
                                        lineTo(w * 0.43f, h * 0.95f)
                                        lineTo(w * 0.57f, h * 0.95f)
                                        lineTo(w * 0.53f, h * 0.40f)
                                        close()
                                    }
                                    drawPath(stairsPath, color = Color(0xFFFFEB3B).copy(alpha = 0.3f))

                                    // Floating vanilla orchid flowers in the sky
                                    drawCircle(
                                        color = Color(0xFFFFEB3B).copy(alpha = 0.7f),
                                        radius = 6f,
                                        center = androidx.compose.ui.geometry.Offset(w * 0.2f, h * 0.25f)
                                    )
                                    drawCircle(
                                        color = Color(0xFFFFEB3B).copy(alpha = 0.7f),
                                        radius = 6f,
                                        center = androidx.compose.ui.geometry.Offset(w * 0.8f, h * 0.3f)
                                    )
                                } else if (currentLang == "Maya") {
                                    // --- DRAW MAJESTIC MAYAN PYRAMID (EL CASTILLO - CHICHÉN ITZÁ) ---
                                    // Soft turquoise/emerald sky
                                    drawCircle(
                                        color = Color(0xFF80CBC4).copy(alpha = 0.25f),
                                        radius = 100f,
                                        center = androidx.compose.ui.geometry.Offset(w * 0.5f, h * 0.35f)
                                    )
                                    drawCircle(
                                        color = Color(0xFF009688).copy(alpha = 0.15f),
                                        radius = 70f,
                                        center = androidx.compose.ui.geometry.Offset(w * 0.5f, h * 0.35f)
                                    )

                                    // Let's draw 5 stepped tiers of Chichén Itzá
                                    val pyramidColor = Color(0xFFB2DFDB) // Stone white/teal
                                    val shadeColor = Color(0xFF00796B)   // Shadow/Teal-dark

                                    // Tier 1 (Base)
                                    val t1Left = w * 0.12f
                                    val t1Right = w * 0.88f
                                    val t1Top = h * 0.85f
                                    val t1Path = androidx.compose.ui.graphics.Path().apply {
                                        moveTo(t1Left, h * 0.95f)
                                        lineTo(t1Left + 20f, t1Top)
                                        lineTo(t1Right - 20f, t1Top)
                                        lineTo(t1Right, h * 0.95f)
                                        close()
                                    }
                                    drawPath(t1Path, color = pyramidColor)

                                    // Tier 2
                                    val t2Left = t1Left + 22f
                                    val t2Right = t1Right - 22f
                                    val t2Top = h * 0.74f
                                    val t2Path = androidx.compose.ui.graphics.Path().apply {
                                        moveTo(t2Left, t1Top)
                                        lineTo(t2Left + 18f, t2Top)
                                        lineTo(t2Right - 18f, t2Top)
                                        lineTo(t2Right, t1Top)
                                        close()
                                    }
                                    drawPath(t2Path, color = pyramidColor.copy(alpha = 0.95f))

                                    // Tier 3
                                    val t3Left = t2Left + 20f
                                    val t3Right = t2Right - 20f
                                    val t3Top = h * 0.63f
                                    val t3Path = androidx.compose.ui.graphics.Path().apply {
                                        moveTo(t3Left, t2Top)
                                        lineTo(t3Left + 16f, t3Top)
                                        lineTo(t3Right - 16f, t3Top)
                                        lineTo(t3Right, t2Top)
                                        close()
                                    }
                                    drawPath(t3Path, color = pyramidColor.copy(alpha = 0.9f))

                                    // Tier 4
                                    val t4Left = t3Left + 18f
                                    val t4Right = t3Right - 18f
                                    val t4Top = h * 0.52f
                                    val t4Path = androidx.compose.ui.graphics.Path().apply {
                                        moveTo(t4Left, t3Top)
                                        lineTo(t4Left + 14f, t4Top)
                                        lineTo(t4Right - 14f, t4Top)
                                        lineTo(t4Right, t3Top)
                                        close()
                                    }
                                    drawPath(t4Path, color = pyramidColor.copy(alpha = 0.85f))

                                    // Top Shrine
                                    val shrineLeft = t4Left + 16f
                                    val shrineRight = t4Right - 16f
                                    val shrineTop = h * 0.40f
                                    drawRect(
                                        color = Color(0xFF004D40),
                                        topLeft = androidx.compose.ui.geometry.Offset(shrineLeft, shrineTop),
                                        size = androidx.compose.ui.geometry.Size(shrineRight - shrineLeft, h * 0.12f)
                                    )

                                    // Central Sacred Stairway
                                    val stairsPath = androidx.compose.ui.graphics.Path().apply {
                                        moveTo(w * 0.46f, h * 0.40f)
                                        lineTo(w * 0.41f, h * 0.95f)
                                        lineTo(w * 0.59f, h * 0.95f)
                                        lineTo(w * 0.54f, h * 0.40f)
                                        close()
                                    }
                                    drawPath(stairsPath, color = shadeColor)

                                    // Serpent Heads at the base of stairway
                                    drawCircle(
                                        color = Color(0xFF4DB6AC),
                                        radius = 12f,
                                        center = androidx.compose.ui.geometry.Offset(w * 0.38f, h * 0.93f)
                                    )
                                    drawCircle(
                                        color = Color(0xFF00796B),
                                        radius = 6f,
                                        center = androidx.compose.ui.geometry.Offset(w * 0.38f, h * 0.93f)
                                    )
                                    drawCircle(
                                        color = Color(0xFF4DB6AC),
                                        radius = 12f,
                                        center = androidx.compose.ui.geometry.Offset(w * 0.62f, h * 0.93f)
                                    )
                                    drawCircle(
                                        color = Color(0xFF00796B),
                                        radius = 6f,
                                        center = androidx.compose.ui.geometry.Offset(w * 0.62f, h * 0.93f)
                                    )
                                } else {
                                    // --- DRAW AZTEC PYRAMID (TEMPLO MAYOR) ---
                                    // Draw a huge glowing golden Aztec sun (Tonatiuh) in the background
                                    drawCircle(
                                        color = Color(0xFFFFB74D).copy(alpha = 0.25f),
                                        radius = 110f,
                                        center = androidx.compose.ui.geometry.Offset(w * 0.5f, h * 0.35f)
                                    )
                                    drawCircle(
                                        color = Color(0xFFFF9800).copy(alpha = 0.4f),
                                        radius = 80f,
                                        center = androidx.compose.ui.geometry.Offset(w * 0.5f, h * 0.35f)
                                    )

                                    // Draw Aztec Pyramid Layers (Tiered steps)
                                    // Tier 1 (Base)
                                    val baseLeft = w * 0.15f
                                    val baseRight = w * 0.85f
                                    val baseTop = h * 0.82f
                                    val basePath = androidx.compose.ui.graphics.Path().apply {
                                        moveTo(baseLeft, h * 0.95f)
                                        lineTo(baseLeft + 25f, baseTop)
                                        lineTo(baseRight - 25f, baseTop)
                                        lineTo(baseRight, h * 0.95f)
                                        close()
                                    }
                                    drawPath(basePath, color = Color(0xFF3E2723))

                                    // Tier 2 (Middle)
                                    val midLeft = baseLeft + 35f
                                    val midRight = baseRight - 35f
                                    val midTop = h * 0.65f
                                    val midPath = androidx.compose.ui.graphics.Path().apply {
                                        moveTo(midLeft, baseTop)
                                        lineTo(midLeft + 20f, midTop)
                                        lineTo(midRight - 20f, midTop)
                                        lineTo(midRight, baseTop)
                                        close()
                                    }
                                    drawPath(midPath, color = Color(0xFF4E342E))

                                    // Tier 3 (Top Shrine platform)
                                    val topLeft = midLeft + 30f
                                    val topRight = midRight - 30f
                                    val topTop = h * 0.48f
                                    val topPath = androidx.compose.ui.graphics.Path().apply {
                                        moveTo(topLeft, midTop)
                                        lineTo(topLeft + 15f, topTop)
                                        lineTo(topRight - 15f, topTop)
                                        lineTo(topRight, midTop)
                                        close()
                                    }
                                    drawPath(topPath, color = Color(0xFF5D4037))

                                    // Upper Shrine Structure
                                    val shrineLeft = topLeft + 30f
                                    val shrineRight = topRight - 30f
                                    val shrineTop = h * 0.35f
                                    drawRect(
                                        color = Color(0xFF2D1500),
                                        topLeft = androidx.compose.ui.geometry.Offset(shrineLeft, shrineTop),
                                        size = androidx.compose.ui.geometry.Size(shrineRight - shrineLeft, h * 0.13f)
                                    )

                                    // Sacred Stairs down the center
                                    val stairsPath = androidx.compose.ui.graphics.Path().apply {
                                        moveTo(w * 0.45f, h * 0.35f)
                                        lineTo(w * 0.42f, h * 0.95f)
                                        lineTo(w * 0.58f, h * 0.95f)
                                        lineTo(w * 0.55f, h * 0.35f)
                                        close()
                                    }
                                    drawPath(stairsPath, color = Color(0xFFFFB74D).copy(alpha = 0.25f))

                                    // Draw burning fire on top platform
                                    drawCircle(
                                        color = Color(0xFFD84315).copy(alpha = 0.8f),
                                        radius = 18f,
                                        center = androidx.compose.ui.geometry.Offset(w * 0.5f, h * 0.33f)
                                    )
                                    drawCircle(
                                        color = Color(0xFFFFB74D),
                                        radius = 10f,
                                        center = androidx.compose.ui.geometry.Offset(w * 0.5f, h * 0.33f)
                                    )
                                }
                            }

                            // Overlay Title on the Pyramid
                            Column(
                                modifier = Modifier
                                    .align(Alignment.BottomCenter)
                                    .background(Color.Black.copy(alpha = 0.65f))
                                    .fillMaxWidth()
                                    .padding(vertical = 10.dp, horizontal = 16.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = if (isOtomi) "🏛️ EL CENTRO CEREMONIAL OTOMÍ" else if (currentLang == "Totona") "🏛️ PIRÁMIDE DE LOS NICHOS (TAJÍN)" else if (currentLang == "Maya") "🏛️ K'UJNA CHICHÉN ITZÁ (EL CASTILLO)" else "🏛️ EL GRAN TEMPLO MAYOR",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = if (isOtomi) Color(0xFFFF80AB) else if (currentLang == "Totona") Color(0xFFFFD54F) else if (currentLang == "Maya") Color(0xFF00BFA5) else Color(0xFFFFB74D),
                                    letterSpacing = 1.sp
                                )
                                Text(
                                    text = if (isOtomi) "Recinto sagrado rodeado de nubes, bosques y monolitos tradicionales" else if (currentLang == "Totona") "Santuario de Papantla y el hablar aromático de la Vainilla, rodeado de selva viva" else if (currentLang == "Maya") "Majestuosa pirámide de Kukulcán, donde la serpiente emplumada desciende en los equinoccios" else "Lugar sagrado de tributos, ofrendas divinas y devoción ancestral",
                                    fontSize = 9.sp,
                                    color = Color.LightGray
                                )
                            }
                        }

                        // Bottom part of the card
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = if (isOtomi) "Restauración Floral y Bendición de los Sabios" else if (currentLang == "Totona") "Aroma de Vainilla y Ofrendas de El Tajín" else if (currentLang == "Maya") "K'ujul Ofrenda: Ofrenda y Sabiduría de Chichén Itzá" else "Restauración de Vidas y Favor de los Dioses",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = if (isOtomi) {
                                    "En la cosmovisión Otomí (Hñähñu), el equilibrio cósmico requiere gratitud y purificación. Ofrece hermosos arreglos florales, resina de copal o fuego sagrado de ocote para sanar tus heridas (refilar tus 5 vidas) y ganar los Escudos Sagrados ($activeShields Escudos vigentes) de los abuelos que te protegerán en tus lecciones futuras."
                                } else if (currentLang == "Totona") {
                                    "En la cosmovisión Totonaca, el fluir de la vida se nutre de la música, el vuelo sagrado y el aroma de la vainilla. Ofrece orquídeas aromáticas, copal veracruzano o el vuelo sagrado de los voladores para sanar tus heridas (refilar tus 5 vidas) and ganar los Escudos de El Tajín ($activeShields Escudos vigentes) de los cuatro rumbos cósmicos."
                                } else if (currentLang == "Maya") {
                                    "En la cosmovisión Maya (Mayab), la reciprocidad con los dioses nutre el orden universal. Realiza ofrendas sagradas en el K'ujna (templo) de Chichén Itzá utilizando tu sabiduría (XP acumulada) para sanar tus heridas (restaurar tus 5 vidas por completo) y recibir los Escudos del Mayab ($activeShields Escudos vigentes) para protegerte contra futuros errores."
                                } else {
                                    "En la cosmovisión mexica, el equilibrio cósmico requería reciprocidad con los dioses. Realiza ofrendas rituales de animales simbólicos con tu XP acumulada para sanar de inmediato tus heridas (refilar tus 5 vidas) y ganar Escudos Divinos ($activeShields Escudos vigentes) que te protegerán contra futuros errores en tus lecciones."
                                },
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.73f),
                                lineHeight = 15.sp
                            )
                        }
                    }
                }
            }

            // --- AI Oracle Assistant Card ---
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("temple_ai_oracle_card"),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.12f)
                    ),
                    border = BorderStroke(1.2.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.5f))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(50.dp)
                                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.15f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("🔮", fontSize = 28.sp)
                        }
                        
                        Spacer(modifier = Modifier.width(14.dp))
                        
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = if (currentLang == "Totona") "El Oráculo del Tajín 🕊️" else "El Oráculo del Sacerdote IA 🛕",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = if (currentLang == "Totona") {
                                    "Conversa directamente con el sabio abuelo. Resuelve dudas sobre la lengua totonaca, costumbres o historia de la selva veracruzana."
                                } else {
                                    "Habla directamente con el supremo Sacerdote del Templo Mayor. Resuelve dudas de náhuatl, mitología mexica y cultura de Mesoamérica."
                                },
                                fontSize = 10.5.sp,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f),
                                lineHeight = 14.sp
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Button(
                                    onClick = { viewModel.showAiOracleChat = true },
                                    shape = RoundedCornerShape(10.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = MaterialTheme.colorScheme.primary
                                    ),
                                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                                    modifier = Modifier.testTag("launch_oracle_button")
                                ) {
                                    Icon(Icons.Default.AutoAwesome, contentDescription = "Consultar", modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(5.dp))
                                    Text("Consultar al Oráculo", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }

                                OutlinedButton(
                                    onClick = { viewModel.playSacerdoteVoice("Bendiciones del Sacerdote") },
                                    shape = RoundedCornerShape(10.dp),
                                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary),
                                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
                                ) {
                                    Icon(Icons.Default.VolumeUp, contentDescription = "Voz", tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Oír al Sacerdote", fontSize = 10.5.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                                }
                            }
                        }
                    }
                }
            }

            // --- Interactive Word & Phrase Consultation with Victor Florencio's Cloned Voice ---
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("sacerdote_voice_consultation_card"),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    border = BorderStroke(1.5.dp, Color(0xFFFFB74D))
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
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
                                Text("🛕", fontSize = 24.sp)
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text(
                                        text = "Voz Sagrada de Víctor Florencio",
                                        fontWeight = FontWeight.ExtraBold,
                                        fontSize = 14.5.sp,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = "Pronuncia cualquier palabra o frase con voz clonada",
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }

                            IconButton(
                                onClick = { viewModel.showXttsConfigDialog = true },
                                modifier = Modifier.size(36.dp).testTag("temple_xtts_config_button")
                            ) {
                                Icon(
                                    Icons.Default.Settings,
                                    contentDescription = "Configurar Servidor XTTS / Hugging Face",
                                    tint = Color(0xFFFFB74D)
                                )
                            }
                        }

                        // Text Field for word or phrase
                        OutlinedTextField(
                            value = viewModel.sacerdoteVoiceInputText,
                            onValueChange = { viewModel.sacerdoteVoiceInputText = it },
                            label = { Text("Palabra o frase a pronunciar") },
                            placeholder = { Text("Ej: Nietzsche, joven guerrero") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("sacerdote_phrase_input"),
                            singleLine = false,
                            maxLines = 3,
                            trailingIcon = {
                                if (viewModel.sacerdoteVoiceInputText.isNotBlank()) {
                                    IconButton(onClick = { viewModel.sacerdoteVoiceInputText = "" }) {
                                        Icon(Icons.Default.Close, contentDescription = "Limpiar texto", modifier = Modifier.size(16.dp))
                                    }
                                }
                            },
                            shape = RoundedCornerShape(12.dp)
                        )

                        // Quick suggestion chips
                        Text(
                            text = "Sugerencias rápidas para el Sacerdote:",
                            fontSize = 10.5.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            val suggestions = listOf(
                                "Nietzsche, joven guerrero",
                                "Niltze, explorador",
                                "Tlazohcamati"
                            )
                            suggestions.forEach { phrase ->
                                SuggestionChip(
                                    onClick = {
                                        viewModel.sacerdoteVoiceInputText = phrase
                                        viewModel.synthesizeSacerdotePhrase(phrase)
                                    },
                                    label = {
                                        Text(
                                            phrase,
                                            fontSize = 9.5.sp,
                                            fontWeight = FontWeight.Medium
                                        )
                                    },
                                    shape = RoundedCornerShape(8.dp)
                                )
                            }
                        }

                        // Status or feedback banner
                        viewModel.sacerdoteStatusFeedback?.let { feedback ->
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = if (feedback.contains("error", ignoreCase = true) || feedback.contains("503") || feedback.contains("505") || feedback.contains("dormido", ignoreCase = true)) {
                                    Color(0xFFFFE082).copy(alpha = 0.2f)
                                } else {
                                    Color(0xFF81C784).copy(alpha = 0.2f)
                                },
                                border = BorderStroke(
                                    0.8.dp,
                                    if (feedback.contains("error", ignoreCase = true) || feedback.contains("503") || feedback.contains("505") || feedback.contains("dormido", ignoreCase = true)) Color(0xFFFFB74D) else Color(0xFF4CAF50)
                                ),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        Icons.Default.Info,
                                        contentDescription = null,
                                        modifier = Modifier.size(14.dp),
                                        tint = if (feedback.contains("error", ignoreCase = true) || feedback.contains("503") || feedback.contains("505") || feedback.contains("dormido", ignoreCase = true)) Color(0xFFFFB74D) else Color(0xFF4CAF50)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = feedback,
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            }
                        }

                        // Synthesis Action Button
                        Button(
                            onClick = {
                                viewModel.synthesizeSacerdotePhrase(viewModel.sacerdoteVoiceInputText)
                            },
                            enabled = viewModel.sacerdoteVoiceInputText.isNotBlank(),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFFFFB74D),
                                contentColor = Color(0xFF261D16)
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(46.dp)
                                .testTag("synthesize_sacerdote_button")
                        ) {
                            if (viewModel.isXttsSynthesizing) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(18.dp),
                                    strokeWidth = 2.dp,
                                    color = Color(0xFF261D16)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Invocando voz de Víctor...",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp
                                )
                            } else {
                                Icon(
                                    imageVector = if (viewModel.isSpeaking) Icons.Default.VolumeUp else Icons.Default.PlayArrow,
                                    contentDescription = "Pronunciar con voz de Víctor",
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = if (viewModel.isSpeaking) "Reproduciendo con Voz de Víctor..." else "Pronunciar con Voz de Víctor Florencio",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp
                                )
                            }
                        }

                        // --- Room Audio Cache Manager Display ---
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = MaterialTheme.colorScheme.surface,
                            border = BorderStroke(1.dp, Color(0xFF00E5FF).copy(alpha = 0.4f)),
                            modifier = Modifier.fillMaxWidth().testTag("room_audio_cache_panel")
                        ) {
                            Column(
                                modifier = Modifier.padding(12.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text("💾", fontSize = 16.sp)
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = "Caché Local Room: $cachedCount frases guardadas",
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                    }

                                    if (cachedCount > 0) {
                                        TextButton(
                                            onClick = { viewModel.clearSacerdoteAudioCache() },
                                            contentPadding = PaddingValues(horizontal = 6.dp, vertical = 2.dp)
                                        ) {
                                            Text("Vaciar", fontSize = 11.sp, color = Color(0xFFFF8A80))
                                        }
                                    }
                                }

                                Text(
                                    text = "Las palabras consultadas se guardan en Room para reproducirse al instante (0 ms) sin llamar al servidor XTTS.",
                                    fontSize = 10.5.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )

                                if (cachedAudios.isNotEmpty()) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .horizontalScroll(rememberScrollState()),
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        cachedAudios.take(8).forEach { entry ->
                                            SuggestionChip(
                                                onClick = {
                                                    viewModel.sacerdoteVoiceInputText = entry.queryText
                                                    viewModel.synthesizeSacerdotePhrase(entry.queryText)
                                                },
                                                label = {
                                                    Text(
                                                        "⚡ ${entry.queryText} (${entry.playCount}x)",
                                                        fontSize = 10.sp,
                                                        color = Color(0xFF00E5FF),
                                                        fontWeight = FontWeight.SemiBold
                                                    )
                                                },
                                                shape = RoundedCornerShape(8.dp),
                                                border = BorderStroke(0.8.dp, Color(0xFF00E5FF).copy(alpha = 0.6f))
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // --- List of Sacred Offerings ---
            item {
                Text(
                    text = if (isOtomi) "💐 Ofrendas del Bosque Sagrado" else if (currentLang == "Totona") "🌸 Ofrendas Aromáticas de la Selva" else if (currentLang == "Maya") "🌽 K'ujul Ofrendas: Ofrendas Sagradas del Mayab" else "🕯️ Ofrendas Disponibles al Altar Mayor",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }

            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        val sacrifices = if (isOtomi) {
                            listOf(
                                Triple("Ofrenda de Doni (Flor Silvestre) 💐", "Tributo floral sencillo. Instante: Refila tus 5 vidas por completo.", "Codorniz" to 100),
                                Triple("Ofrenda de Copa y Copal 🏺", "Humo purificador. Restaura 5 vidas + Otorga 1 Escudo Divino de lección.", "Guajolote" to 180),
                                Triple("Fuego de Ocote Sagrado 🔥", "Fuego sagrado del bosque. Restaura 5 vidas + Otorga 3 Escudos Divinos de protección.", "Ofrenda_Xolo" to 300)
                            )
                        } else if (currentLang == "Totona") {
                            listOf(
                                Triple("Orquídea de Vainilla 🌸", "Tributo aromático de Papantla. Instante: Refila tus 5 vidas por completo.", "Codorniz" to 100),
                                Triple("Sahumerio de Copal Costeño 🏺", "Fragancia sagrada de El Tajín. Restaura 5 vidas + Otorga 1 Escudo Divino.", "Guajolote" to 180),
                                Triple("Vuelo de los Voladores 🕊️", "El sagrado descenso de los hombres pájaro. Restaura 5 vidas + Otorga 3 Escudos Divinos.", "Ofrenda_Xolo" to 300)
                            )
                        } else if (currentLang == "Maya") {
                            listOf(
                                Triple("Mazorca de Maíz Sagrado (Nal) 🌽", "Tributo sagrado de la tierra. Instante: Refila tus 5 vidas por completo.", "Codorniz" to 100),
                                Triple("Incienso de Copal Sagrado (Pom) 🏺", "Fragancia mística del templo de Chichén Itzá. Restaura 5 vidas + Otorga 1 Escudo Divino.", "Guajolote" to 180),
                                Triple("Ofrenda de Luz Cósmica 🌌", "Suprema ofrenda al dios del cielo Itzamná. Restaura 5 vidas + Otorga 3 Escudos Divinos.", "Ofrenda_Xolo" to 300)
                            )
                        } else {
                            listOf(
                                Triple("Codorniz Sagrada 🐦", "Ofrenda sencilla. Instante: Refila tus 5 vidas por completo.", "Codorniz" to 100),
                                Triple("Guajolote Ritual 🦃", "Tributo generoso. Restaura 5 vidas + Otorga 1 Escudo Divino de lección.", "Guajolote" to 180),
                                Triple("Xolo de Ofrenda 🐕", "Tributo distinguido. Restaura 5 vidas + Otorga 3 Escudos Divinos de protección.", "Ofrenda_Xolo" to 300)
                            )
                        }

                        sacrifices.forEach { (title, benefit, details) ->
                            val (type, cost) = details
                            val canAfford = xp >= cost

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 5.dp)
                                    .background(
                                        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f),
                                        RoundedCornerShape(10.dp)
                                    )
                                    .padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(title, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(benefit, fontSize = 9.sp, color = Color.Gray, lineHeight = 12.sp)
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Button(
                                    onClick = { viewModel.activeSacrificeCeremony = type },
                                    enabled = canAfford,
                                    shape = RoundedCornerShape(8.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = if (canAfford) MaterialTheme.colorScheme.primary else Color.Gray
                                    ),
                                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp)
                                ) {
                                    Text("$cost XP", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }

            // --- Ultimate Active Pet Sacrifice Action ---
            if (petType.isNotEmpty() && !isPetDead) {
                item {
                    Text(
                        text = if (isOtomi) "🔥 Ofrenda Suprema del Bosque" else if (currentLang == "Totona") "🔥 Ofrenda Suprema de la Selva" else "🔥 Ofrenda Suprema del Templo",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFEF5350)
                    )
                }

                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFEF5350).copy(alpha = 0.04f)),
                        border = BorderStroke(1.dp, Color(0xFFEF5350).copy(alpha = 0.25f))
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "Sacrificio Devocional de tu Compañero",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFEF5350)
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = if (isOtomi) {
                                    "Puedes decidir ofrendar voluntariamente a tu amada mascota actual ($petName) bajo las copas del bosque de Temoaya. Su espíritu descansará protegido bajo el cielo otomí, y a cambio recibirás la restauración absoluta de tus 5 vidas y 5 Escudos Divinos de protección completa contra errores en tus lecciones."
                                } else if (currentLang == "Totona") {
                                    "Puedes decidir ofrendar voluntariamente a tu amada mascota actual ($petName) en los nichos de El Tajín. Su espíritu ascenderá alegremente danzando entre flores de vainilla y sones huastecos, y a cambio recibirás la restauración absoluta de tus 5 vidas y 5 Escudos Divinos de protección completa contra errores."
                                } else {
                                    "Puedes decidir ofrendar voluntariamente a tu amada mascota actual ($petName) en el Templo del Sol de Tenochtitlan. Su espíritu ascenderá con orgullo junto a los dioses, y a cambio Huitzilopochtli te otorgará la restauración absoluta de tus 5 vidas y 5 Escudos Divinos de protección completa contra errores en tus lecciones."
                                },
                                fontSize = 10.sp,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f),
                                lineHeight = 14.sp
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Button(
                                onClick = { viewModel.activeSacrificeCeremony = "Mascota" },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD32F2F)),
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Text("Realizar Supremo Sacrificio de $petName (Gratis, entrega mascota)", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }

        // Animated full screen sacrifice overlay
        if (viewModel.activeSacrificeCeremony != null) {
            CeremoniaTemploOverlay(viewModel)
        }
    }
}

// --- DYNAMIC FULL-SCREEN CHALLENGE SCREENS & OVERLAYS ---

@Composable
fun CeremoniaTemploOverlay(viewModel: MainViewModel) {
    val ceremonyType = viewModel.activeSacrificeCeremony ?: return
    val progress by viewModel.userProgress.collectAsState()
    val petName = progress?.petName ?: ""
    val petType = progress?.petType ?: ""
    
    val animationStep = viewModel.sacrificeAnimationStep
    
    val infiniteTransition = rememberInfiniteTransition(label = "temple_fire")
    val fireScale by infiniteTransition.animateFloat(
        initialValue = 0.85f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "fire_scale"
    )
    val alphaPulse by infiniteTransition.animateFloat(
        initialValue = 0.5f,
        targetValue = 0.9f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "alpha_pulse"
    )

    val currentLang = progress?.selectedLanguage ?: "Nahuatl"
    val isOtomi = currentLang == "Otomi"

    // Emojis for the victim and the celestial guide
    val activePetEmoji = when {
        petType.contains("Guajolote", ignoreCase = true) -> "🦃"
        petType.contains("Xoloitzcuintli", ignoreCase = true) || petType.contains("Pek'", ignoreCase = true) || petType.contains("Perro", ignoreCase = true) -> "🐕"
        petType.contains("Guacamaya", ignoreCase = true) -> "🦜"
        petType.contains("Águila", ignoreCase = true) || petType.contains("Xa'lh", ignoreCase = true) || petType.contains("Ch'oom", ignoreCase = true) -> "🦅"
        petType.contains("Tlacuache", ignoreCase = true) -> "🦊"
        petType.contains("Coyote", ignoreCase = true) -> "🐺"
        petType.contains("Colibrí", ignoreCase = true) -> "🐦"
        petType.contains("Venado", ignoreCase = true) || petType.contains("Ceh", ignoreCase = true) -> "🦌"
        petType.contains("Jaguar", ignoreCase = true) || petType.contains("Zat'i", ignoreCase = true) || petType.contains("Misin", ignoreCase = true) || petType.contains("Balam", ignoreCase = true) -> "🐆"
        else -> "🐾"
    }

    val victimEmoji = if (isOtomi) {
        when (ceremonyType) {
            "Codorniz" -> "💐"  // Doni flowers
            "Guajolote" -> "🏺" // Copal incense
            "Ofrenda_Xolo" -> "🔥" // Ocote fire
            else -> activePetEmoji
        }
    } else if (currentLang == "Totona") {
        when (ceremonyType) {
            "Codorniz" -> "🌸"  // Vanilla Orchid
            "Guajolote" -> "🏺" // Copal Sahumerio
            "Ofrenda_Xolo" -> "🕊️" // Voladores
            else -> activePetEmoji
        }
    } else if (currentLang == "Maya") {
        when (ceremonyType) {
            "Codorniz" -> "🌽"  // Sagrado Maíz Nal
            "Guajolote" -> "🏺" // Incensario de Copal Pom
            "Ofrenda_Xolo" -> "🌌" // Ofrenda al dios del cielo Itzamná
            else -> activePetEmoji
        }
    } else {
        when (ceremonyType) {
            "Codorniz" -> "🐦"
            "Guajolote" -> "🦃"
            "Ofrenda_Xolo" -> "🐕"
            else -> activePetEmoji
        }
    }

    val victimName = if (isOtomi) {
        when (ceremonyType) {
            "Codorniz" -> "Ramo de Flores (Doni) 💐"
            "Guajolote" -> "Humo de Copal Sagrado 🏺"
            "Ofrenda_Xolo" -> "Luz de Ocote Sagrado 🔥"
            else -> petName.ifEmpty { "Tu Compañero" }
        }
    } else if (currentLang == "Totona") {
        when (ceremonyType) {
            "Codorniz" -> "Orquídea de Vainilla 🌸"
            "Guajolote" -> "Sahumerio de Copal Costeño 🏺"
            "Ofrenda_Xolo" -> "Vuelo de los Voladores 🕊️"
            else -> petName.ifEmpty { "Tu Compañero" }
        }
    } else if (currentLang == "Maya") {
        when (ceremonyType) {
            "Codorniz" -> "Mazorca de Maíz Sagrado (Nal) 🌽"
            "Guajolote" -> "Incienso de Copal Sagrado (Pom) 🏺"
            "Ofrenda_Xolo" -> "Ofrenda de Luz Cósmica 🌌"
            else -> petName.ifEmpty { "Tu Compañero" }
        }
    } else {
        when (ceremonyType) {
            "Codorniz" -> "Codorniz Sagrada"
            "Guajolote" -> "Guajolote Ritual"
            "Ofrenda_Xolo" -> "Xoloitzcuintli de Ofrenda"
            else -> petName.ifEmpty { "Tu Compañero" }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF070707))
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        if (animationStep == 0) {
            // --- STEP 0: Confirmation Screen ---
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = if (isOtomi) "🌲 RECINTO SAGRADO DE TEMOAYA 🌲" else if (currentLang == "Totona") "🕌 SANTUARIO SAGRADO DE EL TAJÍN 🕌" else if (currentLang == "Maya") "🛕 TEMPLO SAGRADO DE CHICHÉN ITZÁ 🛕" else "🛕 TEMPLO MAYOR SAGRADO 🛕",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Black,
                    color = if (isOtomi) Color(0xFFFF80AB) else if (currentLang == "Totona") Color(0xFFFFD54F) else if (currentLang == "Maya") Color(0xFF4DB6AC) else Color(0xFFFFB74D),
                    letterSpacing = 2.sp,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = if (isOtomi) "BENDICIÓN Y AGRADECIMIENTO ANCESTRAL" else if (currentLang == "Totona") "RITUAL DEL TRUENO Y VUELO CÓSMICO" else if (currentLang == "Maya") "CEREMONIA SAGRADA DE LOS ASTROS Y CHAC MOOL" else "CEREMONIA ANCESTRAL DE OFRENDA Y PAGO",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Gray,
                    letterSpacing = 1.sp,
                    textAlign = TextAlign.Center
                )
                
                Spacer(modifier = Modifier.height(28.dp))
                
                Canvas(modifier = Modifier.size(120.dp)) {
                    drawRect(
                        color = Color(0xFF3E2723),
                        topLeft = androidx.compose.ui.geometry.Offset(size.width * 0.15f, size.height * 0.7f),
                        size = androidx.compose.ui.geometry.Size(size.width * 0.7f, size.height * 0.3f)
                    )
                    
                    val fireHeight = size.height * 0.5f * fireScale
                    val fireWidth = size.width * 0.4f * fireScale
                    val path = androidx.compose.ui.graphics.Path().apply {
                        moveTo(size.width * 0.5f, size.height * 0.75f - fireHeight)
                        quadraticTo(
                            size.width * 0.5f + fireWidth * 0.6f, size.height * 0.75f - fireHeight * 0.4f,
                            size.width * 0.5f + fireWidth * 0.3f, size.height * 0.75f
                        )
                        lineTo(size.width * 0.5f - fireWidth * 0.3f, size.height * 0.75f)
                        quadraticTo(
                            size.width * 0.5f - fireWidth * 0.6f, size.height * 0.75f - fireHeight * 0.4f,
                            size.width * 0.5f, size.height * 0.75f - fireHeight
                        )
                        close()
                    }
                    drawPath(path, color = Color(0xFFD84315).copy(alpha = alphaPulse))
                    
                    val innerPath = androidx.compose.ui.graphics.Path().apply {
                        moveTo(size.width * 0.5f, size.height * 0.75f - fireHeight * 0.6f)
                        quadraticTo(
                            size.width * 0.5f + fireWidth * 0.3f, size.height * 0.75f - fireHeight * 0.2f,
                            size.width * 0.5f + fireWidth * 0.15f, size.height * 0.75f
                        )
                        lineTo(size.width * 0.5f - fireWidth * 0.15f, size.height * 0.75f)
                        quadraticTo(
                            size.width * 0.5f - fireWidth * 0.3f, size.height * 0.75f - fireHeight * 0.2f,
                            size.width * 0.5f, size.height * 0.75f - fireHeight * 0.6f
                        )
                        close()
                    }
                    drawPath(innerPath, color = Color(0xFFFFB74D))
                }
                
                Spacer(modifier = Modifier.height(28.dp))
                
                val detailsText = if (isOtomi) {
                    when (ceremonyType) {
                        "Codorniz" -> "Ofrendarás un hermoso ramo de Flores Silvestres (Doni). Las deidades de la naturaleza recibirán este tributo floral y restaurarán tus 5 vidas por completo."
                        "Guajolote" -> "Ofrendarás una Copa ceremonial de Copal sagrado. Su humo purificador elevará tus intenciones y restaurarán tus 5 vidas, otorgando además 1 Escudo Divino de protección."
                        "Ofrenda_Xolo" -> "Encenderás el Fuego Cósmico de Ocote Sagrado. Su luz sagrada te guiará del bosque, dándote 5 vidas completas y 3 Escudos Divinos de protección."
                        else -> "🔥 OFRENDA SUPREMA DEL BOSQUE 🔥\n\nEntregarás con profunda gratitud la bendición de tu compañero, $victimName ($petType). Su espíritu descansará protegido bajo el hermoso cielo otomí. Recibirás de inmediato:\n\n• Restauración Completa de 5 Vidas\n• 5 Escudos Divinos de protección contra cualquier error futuro."
                    }
                } else if (currentLang == "Totona") {
                    when (ceremonyType) {
                        "Codorniz" -> "Ofrendarás una fragante Orquídea de Vainilla 🌸. El aroma sagrado de Papantla complacerá a los dioses de la selva veracruzana y restaurará por completo tus 5 vidas."
                        "Guajolote" -> "Encenderás un Sahumerio de Copal Costeño 🏺. Su humo sagrado elevará tus intenciones en El Tajín, restaurando tus 5 vidas y otorgando 1 Escudo Divino."
                        "Ofrenda_Xolo" -> "Celebrarás el sagrado Vuelo de los Voladores 🕊️. Los cuatro hombres pájaro descenderán del cielo trayendo bendición, dándote 5 vidas completas y 3 Escudos Divinos protectores."
                        else -> "🔥 OFRENDA SUPREMA DE LA SELVA 🔥\n\nEntregarás con profundo amor a tu compañero, $victimName ($petType). Su espíritu ascenderá alegremente danzando entre flores de vainilla y sones huastecos. Recibirás de inmediato:\n\n• Restauración Completa de 5 Vidas\n• 5 Escudos Divinos de protección contra cualquier error futuro."
                    }
                } else if (currentLang == "Maya") {
                    when (ceremonyType) {
                        "Codorniz" -> "Ofrendarás una hermosa Mazorca de Maíz Sagrado (Nal) 🌽. Las deidades de la milpa recibirán este sagrado tributo de la tierra y restaurarán tus 5 vidas por completo."
                        "Guajolote" -> "Ofrendarás un sagrado Incensario de Copal (Pom) 🏺. Su humo místico elevará tus plegarias a los señores del inframundo y el cielo, restaurando tus 5 vidas y dándote 1 Escudo Divino."
                        "Ofrenda_Xolo" -> "Ofrendarás un Ritual de Luz Cósmica de los Astros 🌌. La alineación con los dioses mayas guiará tu camino de conocimiento, dándote 5 vidas completas y 3 Escudos Divinos protectores."
                        else -> "🔥 OFRENDA SUPREMA DEL INFRAMUNDO 🔥\n\nEntregarás con profundo honor a tu compañero, $victimName ($petType). Su espíritu descansará protegido en el sagrado Xibalbá bajo la mirada del jaguar de los astros. Recibirás de inmediato:\n\n• Restauración Completa de 5 Vidas\n• 5 Escudos Divinos de protección contra cualquier error futuro."
                    }
                } else {
                    when (ceremonyType) {
                        "Codorniz" -> "Ofrendarás una Codorniz Sagrada. Los dioses recibirán este tributo sencillo y restaurarán por completo tus 5 vidas."
                        "Guajolote" -> "Ofrendarás un Guajolote Ritual de gran vigor. Los cielos se abrirán y restaurarán tus 5 vidas, otorgando además 1 Escudo Divino de protección."
                        "Ofrenda_Xolo" -> "Ofrendarás el preciado Xoloitzcuintli ritual. Su espíritu te guiará y te dará 5 vidas completas y 3 Escudos Divinos protectores."
                        else -> "🔥 OFRENDA SUPREMA 🔥\n\nEntregarás voluntariamente a tu amada mascota, $victimName ($petType), en un acto de devoción pura. Su espíritu ascenderá con orgullo al Templo del Sol. Recibirás de inmediato:\n\n• Restauración Completa de 5 Vidas\n• 5 Escudos Divinos de protección contra cualquier error futuro."
                    }
                }
                
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E1E)),
                    border = BorderStroke(1.dp, Color(0xFFFFB74D).copy(alpha = 0.5f)),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = detailsText,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        lineHeight = 18.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(16.dp)
                    )
                }
                
                Spacer(modifier = Modifier.height(32.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = { viewModel.activeSacrificeCeremony = null },
                        border = BorderStroke(1.dp, Color.Gray),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.LightGray),
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Cancelar", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                    
                    Button(
                        onClick = {
                            viewModel.performSacrifice(ceremonyType)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD32F2F)),
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Realizar Ofrenda", fontSize = 11.sp, fontWeight = FontWeight.Black, color = Color.White)
                    }
                }
            }
        } else {
            // --- ACTIVE CINEMATIC SACRIFICE ANIMATION FLOW ---
            val scalePulse by infiniteTransition.animateFloat(
                initialValue = 0.95f,
                targetValue = 1.05f,
                animationSpec = infiniteRepeatable(tween(300, easing = LinearEasing), RepeatMode.Reverse),
                label = "cinematic_pulse"
            )
            
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                // Background dark red vignette
                Canvas(modifier = Modifier.fillMaxSize()) {
                    drawRect(
                        brush = androidx.compose.ui.graphics.Brush.radialGradient(
                            colors = if (animationStep == 2) {
                                listOf(Color(0xFFD32F2F).copy(alpha = 0.5f), Color(0xFF050505))
                            } else {
                                listOf(Color(0xFF3E2723).copy(alpha = 0.25f), Color(0xFF050505))
                            },
                            center = androidx.compose.ui.geometry.Offset(size.width * 0.5f, size.height * 0.5f),
                            radius = size.width * 0.8f
                        )
                    )
                }

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    // Step Indicator Title
                    val stepTitle = if (isOtomi) {
                        when (animationStep) {
                            1 -> "🏮 PREPARANDO LA OFRENDA FLORAL"
                            2 -> "🔥 ¡EL HUMO PURIFICADOR DE COPAL!"
                            3 -> "🌌 EL ESPÍRITU SE UNE CON LA NATURALEZA"
                            else -> "☀️ LA BENDICIÓN DE HYADI (EL SOL)"
                        }
                    } else if (currentLang == "Totona") {
                        when (animationStep) {
                            1 -> "🌸 PREPARANDO LA ORQUÍDEA DE VAINILLA"
                            2 -> "🏺 ¡EL SAHUMERIO RITUAL DE EL TAJÍN!"
                            3 -> "🕊️ EL VUELO SAGRADO AL INFRAMUNDO"
                            else -> "☀️ LA BENDICIÓN DE CHICHINI (EL SOL)"
                        }
                    } else {
                        when (animationStep) {
                            1 -> "🏮 PREPARANDO EL ALTAR SAGRADO"
                            2 -> "🔥 ¡EL GOLPE DIVINO CON COPAL!"
                            3 -> "🌌 EL ESPÍRITU ASCIENDE AL CIELO"
                            else -> "☀️ LA BENDICIÓN DE TONATIUH"
                        }
                    }
                    Text(
                        text = stepTitle,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Black,
                        color = if (animationStep == 2) Color(0xFFEF5350) else Color(0xFFFFB74D),
                        letterSpacing = 2.sp,
                        textAlign = TextAlign.Center
                    )
                    
                    Spacer(modifier = Modifier.height(24.dp))

                    // The Altar & Sacrifice Rendering Box
                    Box(
                        modifier = Modifier
                            .size(240.dp)
                            .background(Color.Black.copy(alpha = 0.2f), RoundedCornerShape(12.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        // Copal smoke rising from the bottom of the Box (drawn on Canvas)
                        Canvas(modifier = Modifier.fillMaxSize()) {
                            val smokeHeight = size.height * 0.4f
                            val p1Y = size.height * 0.8f - smokeHeight * alphaPulse
                            // Draw curvy copal incense trails
                            val path1 = androidx.compose.ui.graphics.Path().apply {
                                moveTo(size.width * 0.4f, size.height * 0.85f)
                                cubicTo(
                                    size.width * 0.3f, size.height * 0.6f,
                                    size.width * 0.45f, size.height * 0.4f,
                                    size.width * 0.35f, p1Y
                                )
                            }
                            val path2 = androidx.compose.ui.graphics.Path().apply {
                                moveTo(size.width * 0.6f, size.height * 0.85f)
                                cubicTo(
                                    size.width * 0.7f, size.height * 0.6f,
                                    size.width * 0.55f, size.height * 0.4f,
                                    size.width * 0.65f, p1Y
                                )
                            }
                            drawPath(
                                path1,
                                color = Color.LightGray.copy(alpha = alphaPulse * 0.35f),
                                style = androidx.compose.ui.graphics.drawscope.Stroke(width = 4f)
                            )
                            drawPath(
                                path2,
                                color = Color.LightGray.copy(alpha = alphaPulse * 0.35f),
                                style = androidx.compose.ui.graphics.drawscope.Stroke(width = 4f)
                            )
                        }

                        // 1. Altar Stone Base (Obsidian Slab)
                        Canvas(modifier = Modifier.size(160.dp, 60.dp).align(Alignment.BottomCenter).offset(y = (-10).dp)) {
                            // Draw beautiful ceremonial stone slab
                            val path = androidx.compose.ui.graphics.Path().apply {
                                moveTo(size.width * 0.1f, size.height * 0.8f)
                                lineTo(size.width * 0.9f, size.height * 0.8f)
                                lineTo(size.width * 0.8f, size.height * 0.2f)
                                lineTo(size.width * 0.2f, size.height * 0.2f)
                                close()
                            }
                            drawPath(path, color = Color(0xFF1E1510)) // Stone color
                            drawRect(
                                color = Color(0xFF0F0F0F), // Top obsidian face
                                topLeft = androidx.compose.ui.geometry.Offset(size.width * 0.2f, size.height * 0.2f),
                                size = androidx.compose.ui.geometry.Size(size.width * 0.6f, size.height * 0.2f)
                            )
                        }

                        // 2. Active victim rendering with custom stage animations
                        when (animationStep) {
                            1 -> {
                                // Breath/Innocent wait on altar
                                Text(
                                    text = victimEmoji,
                                    fontSize = 72.sp,
                                    modifier = Modifier
                                        .align(Alignment.Center)
                                        .offset(y = 10.dp)
                                        .graphicsLayer(scaleX = scalePulse, scaleY = scalePulse)
                                )
                                // Hovering dagger floating above
                                Text(
                                    text = "🔪",
                                    fontSize = 42.sp,
                                    modifier = Modifier
                                        .align(Alignment.TopCenter)
                                        .offset(y = 12.dp + (fireScale * 12).dp)
                                        .graphicsLayer(rotationZ = 180f) // blade points down
                                )
                            }
                            2 -> {
                                // Screen shake & slash
                                val shakeX = ((-10..10).random()).dp
                                val shakeY = ((-10..10).random()).dp
                                Text(
                                    text = victimEmoji,
                                    fontSize = 72.sp,
                                    modifier = Modifier
                                        .align(Alignment.Center)
                                        .offset(x = shakeX, y = 10.dp + shakeY)
                                        .graphicsLayer(rotationZ = 45f)
                                )
                                // Strike slash drawn over
                                Canvas(modifier = Modifier.fillMaxSize()) {
                                    drawLine(
                                        color = Color.White,
                                        start = androidx.compose.ui.geometry.Offset(size.width * 0.2f, size.height * 0.2f),
                                        end = androidx.compose.ui.geometry.Offset(size.width * 0.8f, size.height * 0.8f),
                                        strokeWidth = 14f
                                    )
                                    drawLine(
                                        color = Color(0xFFFFB74D),
                                        start = androidx.compose.ui.geometry.Offset(size.width * 0.18f, size.height * 0.22f),
                                        end = androidx.compose.ui.geometry.Offset(size.width * 0.82f, size.height * 0.78f),
                                        strokeWidth = 6f
                                    )
                                    // Burst particles on strike
                                    drawCircle(
                                        color = Color(0xFFD32F2F),
                                        radius = 45f,
                                        center = androidx.compose.ui.geometry.Offset(size.width * 0.5f, size.height * 0.5f)
                                    )
                                }
                                Text(
                                    text = "💥",
                                    fontSize = 84.sp,
                                    modifier = Modifier.align(Alignment.Center)
                                )
                            }
                            3 -> {
                                // ASCENSION: Spirit floats UP, Guide descends and carries it away!
                                val floatUpProg = rememberInfiniteTransition(label = "spirit_float").animateFloat(
                                    initialValue = 0f,
                                    targetValue = 1f,
                                    animationSpec = infiniteRepeatable(tween(2600, easing = LinearEasing)),
                                    label = "float_up"
                                ).value

                                val spiritY = 20.dp - (140 * floatUpProg).dp
                                val spiritAlpha = 0.9f - (floatUpProg * 0.7f)
                                
                                // Glowing solar animal that carries the spirit away
                                val guideEmoji = when (ceremonyType) {
                                    "Codorniz", "Guajolote" -> "🦅" // Solar Eagle
                                    else -> "🐆" // Fire Jaguar
                                }
                                
                                val guideX = (-120).dp + (260 * floatUpProg).dp
                                val guideY = (-80).dp + (140 * floatUpProg).dp
                                
                                // Glowing spirit of the victim
                                Text(
                                    text = victimEmoji,
                                    fontSize = 62.sp,
                                    modifier = Modifier
                                        .align(Alignment.Center)
                                        .offset(y = spiritY)
                                        .graphicsLayer(alpha = spiritAlpha, scaleX = 1.1f, scaleY = 1.1f)
                                )
                                
                                // Sparkles surrounding ascending soul
                                Text(
                                    text = "✨  ✨\n  ✨  ",
                                    fontSize = 44.sp,
                                    modifier = Modifier
                                        .align(Alignment.Center)
                                        .offset(y = spiritY)
                                        .graphicsLayer(alpha = spiritAlpha)
                                )

                                // Divine transport guide swooping down and taking it!
                                Text(
                                    text = guideEmoji,
                                    fontSize = 72.sp,
                                    modifier = Modifier
                                        .align(Alignment.Center)
                                        .offset(x = guideX, y = guideY)
                                        .graphicsLayer(
                                            scaleX = 1.3f, scaleY = 1.3f,
                                            rotationZ = -15f
                                        )
                                )
                                
                                // Trail particles behind guide
                                Text(
                                    text = "🔥",
                                    fontSize = 24.sp,
                                    modifier = Modifier
                                        .align(Alignment.Center)
                                        .offset(x = guideX - 40.dp, y = guideY + 10.dp)
                                )
                            }
                            4 -> {
                                // Blessing state: refilling life & shields!
                                val blessingPulse = rememberInfiniteTransition(label = "blessing").animateFloat(
                                    initialValue = 0.7f,
                                    targetValue = 1.3f,
                                    animationSpec = infiniteRepeatable(tween(800, easing = LinearEasing), RepeatMode.Reverse),
                                    label = "blessing_scale"
                                ).value
                                
                                Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                                    // Golden Sun beams
                                    Canvas(modifier = Modifier.fillMaxSize()) {
                                        drawCircle(
                                            brush = androidx.compose.ui.graphics.Brush.radialGradient(
                                                colors = listOf(Color(0xFFFFF176).copy(alpha = 0.5f), Color.Transparent),
                                                radius = size.width * 0.4f * blessingPulse
                                            )
                                        )
                                    }
                                    Text(
                                        text = "☀️",
                                        fontSize = 90.sp,
                                        modifier = Modifier.graphicsLayer(scaleX = blessingPulse, scaleY = blessingPulse)
                                    )
                                    Text(
                                        text = "💖 💖 💖 🛡️ 🛡️ 🛡️",
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier
                                            .align(Alignment.BottomCenter)
                                            .offset(y = (-20).dp)
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(28.dp))

                    // Ceremony contextual narrative text
                    val animationDesc = when (animationStep) {
                        1 -> "Copal ardiendo, tambores retumbando...\n\nSacerdotes con túnicas ceremoniales colocan con honor a $victimName sobre el altar de obsidiana del Templo Mayor. Se pronuncian los antiguos rezos de agradecimiento y reciprocidad cósmica."
                        2 -> "¡EL CUCHILLO RITUAL DE OBSIDIANA GOLPEA!\n\nCon devoción sagrada y fuego purificador, la vida física de $victimName es entregada con un ágil corte ceremonial. Los cielos vibran ante la ofrenda sagrada, abriéndose con resplandor solar."
                        3 -> "¡EL ALMA ES ELEVADA Y LLEVADA CON RESPETO!\n\nEl alma de $victimName se desliga de su cuerpo en forma de un espíritu áureo resplandeciente. ¡Un majestuoso ${if (ceremonyType == "Codorniz" || ceremonyType == "Guajolote") "Águila Solar" else "Jaguar de Fuego"} de los dioses desciende majestuosamente del cielo, recoge su espíritu en su lomo y se lo lleva con honor hacia las estrellas de los cielos del sol!"
                        else -> "¡LOS DIOSES ESTÁN SATISFECHOS!\n\nEl gran Tonatiuh y Huitzilopochtli sonríen con júbilo ante tu ofrenda sagrada. Una lluvia divina de vida y escudos solares desciende sobre ti:\n\n• ¡5 VIDAS RESTAURADAS POR COMPLETO!\n• ¡RECIBES ESCUDOS DIVINOS DE PROTECCIÓN!"
                    }
                    
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF141414)),
                        border = BorderStroke(1.dp, Color(0xFFFFB74D).copy(alpha = 0.4f)),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = animationDesc,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            lineHeight = 18.sp,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun FullScreenChallengeContainer(viewModel: MainViewModel) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        when {
            viewModel.lessonRitualActive -> {
                FullScreenRitualScreen(viewModel = viewModel)
            }
            viewModel.selectedGameLevel != null -> {
                FullScreenGameScreen(viewModel = viewModel)
            }
            viewModel.activeLesson != null -> {
                FullScreenLessonScreen(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun FullScreenLessonScreen(viewModel: MainViewModel) {
    val lesson = viewModel.activeLesson ?: return
    val currentProgress by viewModel.userProgress.collectAsState()
    val currentLang = currentProgress?.selectedLanguage ?: "Nahuatl"
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.surface,
                        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
                    )
                )
            )
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Exit Button Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(
                    onClick = {
                        viewModel.activeLesson = null
                    },
                    modifier = Modifier.testTag("exit_lesson_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Salir de la lección",
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }
                Text(
                    text = if (currentLang == "Otomi") "Estudiando Sabiduría Otomí" else if (currentLang == "Totona") "Estudiando Sabiduría Totonaca" else "Estudiando Sabiduría Azteca",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.size(48.dp))
            }

            // Header Title
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = if (currentLang == "Otomi") "LIBRO DE SABIDURÍA OTOMÍ 🌲" else if (currentLang == "Totona") "LIBRO DE SABIDURÍA TOTONACA 🕊️" else "LIBRO DE SABIDURÍA AZTECA 🛕",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = lesson.title,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Black,
                        color = MaterialTheme.colorScheme.onSurface,
                        textAlign = TextAlign.Center
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Lesson Explanation Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.15f))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Explicación Ancestral",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    Text(
                        text = lesson.explanation,
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        lineHeight = 19.sp
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Vocab Card
            Text(
                text = "Vocabulario Clave 📖",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.align(Alignment.Start)
            )
            Spacer(modifier = Modifier.height(8.dp))
            
            val wordsList = remember(lesson) {
                val nPhrases = lesson.nahuatlPhrases.split(";")
                val sPhrases = lesson.spanishTranslations.split(";")
                val count = minOf(nPhrases.size, sPhrases.size, 3)
                (0 until count).map { i ->
                    val nahuatlWord = nPhrases[i].trim()
                    val spanishWord = sPhrases[i].trim()
                    
                    // Simple, clean transcription helper for Nahuatl spelling rules to Spanish phonetics:
                    val phonetic = nahuatlWord.lowercase()
                        .replace("qu", "k")
                        .replace("hu", "w")
                        .replace("uh", "w")
                        .replace("tl", "tl")
                        .replace("tz", "ts")
                        .replace("x", "sh")
                        .replace("z", "s")
                        .replace("c", "k")
                        .replace("k", "k")
                        .replace("ss", "s")
                        .replace("kk", "k")
                        .trim()
                        .replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }

                    val hasExtra1 = nPhrases.size > 3 && sPhrases.size > 3
                    val hasExtra2 = nPhrases.size > 4 && sPhrases.size > 4
                    val exN = when (i) {
                        0 -> if (hasExtra1) nPhrases[3].trim() else ""
                        1 -> if (hasExtra2) nPhrases[4].trim() else ""
                        else -> ""
                    }
                    val exS = when (i) {
                        0 -> if (hasExtra1) sPhrases[3].trim() else ""
                        1 -> if (hasExtra2) sPhrases[4].trim() else ""
                        else -> ""
                    }

                    WordEntity(
                        id = i,
                        nahuatl = nahuatlWord,
                        spanish = spanishWord,
                        pronunciation = phonetic,
                        category = lesson.category,
                        sentenceNahuatl = exN,
                        sentenceSpanish = exS
                    )
                }
            }
            if (wordsList.isEmpty()) {
                Text(
                    text = "No hay vocabulario específico para esta lección.",
                    fontSize = 11.sp,
                    color = Color.Gray,
                    modifier = Modifier.align(Alignment.Start)
                )
            } else {
                wordsList.forEach { word ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.25f))
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = word.nahuatl,
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                    IconButton(
                                        onClick = { viewModel.speak(word.nahuatl) },
                                        modifier = Modifier.size(30.dp).testTag("speak_lesson_vocab_${word.id}")
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.VolumeUp,
                                            contentDescription = "Escuchar pronunciación",
                                            tint = MaterialTheme.colorScheme.primary,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                }
                                Text(
                                    text = word.spanish,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Pronunciación: [${word.pronunciation}]",
                                fontSize = 10.sp,
                                color = Color.Gray,
                                fontStyle = FontStyle.Italic
                            )
                            if (word.sentenceNahuatl.isNotBlank()) {
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "Ejemplo: \"${word.sentenceNahuatl}\" - ${word.sentenceSpanish}",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f),
                                    lineHeight = 15.sp
                                )
                            }
                        }
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(20.dp))
            
            // AI Chatbot
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("ai_lesson_assistant_card"),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.15f)),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.secondary.copy(alpha = 0.2f))
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = "IA",
                            tint = MaterialTheme.colorScheme.secondary,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        val priestTitle = if (currentLang == "Otomi") "Pregúntale al Sacerdote IA Otomí" else if (currentLang == "Totona") "Pregúntale al Sacerdote IA Totonaco" else "Pregúntale al Sacerdote IA Azteca"
                        Text(
                            text = priestTitle,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.secondary
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "¿Tienes dudas? El sacerdote puede darte respuestas con su sabiduría ancestral.",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = viewModel.customAiTopicQuery,
                            onValueChange = { viewModel.customAiTopicQuery = it },
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp)
                                .testTag("ai_query_input_field"),
                            placeholder = { Text("Ej. ¿Cómo se dice adiós?", fontSize = 11.sp) },
                            singleLine = true,
                            shape = RoundedCornerShape(8.dp),
                            textStyle = androidx.compose.ui.text.TextStyle(fontSize = 11.sp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        if (viewModel.isAiLessonExplaining) {
                            CircularProgressIndicator(modifier = Modifier.size(24.dp), strokeWidth = 2.dp)
                        } else {
                            IconButton(
                                onClick = { viewModel.runAiCustomExplanation(lesson.title, lesson.explanation) },
                                enabled = viewModel.customAiTopicQuery.isNotBlank()
                            ) {
                                Icon(Icons.Default.Send, contentDescription = "Enviar", tint = MaterialTheme.colorScheme.secondary)
                            }
                        }
                    }
                    
                    viewModel.customAiExplanation?.let { explanation ->
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = explanation,
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurface,
                            lineHeight = 15.sp,
                            modifier = Modifier.background(Color.White.copy(alpha = 0.5f), RoundedCornerShape(6.dp)).padding(8.dp)
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(28.dp))
            
            // Forced Proceed Button
            Button(
                onClick = { viewModel.startLessonChallengeRitual(lesson) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("start_lesson_ritual_btn"),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Icon(Icons.Default.AutoAwesome, contentDescription = "Ritual")
                Spacer(modifier = Modifier.width(8.dp))
                val ritualBtnText = if (currentLang == "Otomi") "Iniciar Desafío Ritual Obligatorio 🌲" else if (currentLang == "Totona") "Iniciar Desafío Ritual Obligatorio 🕊️" else "Iniciar Desafío Ritual Obligatorio 🛕"
                Text(ritualBtnText, fontSize = 13.sp, fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun FullScreenRitualScreen(viewModel: MainViewModel) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF1E1005), // Dark volcanic Aztec brown
                        Color(0xFF0F0802)
                    )
                )
            )
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Exit/Cancel Button Row for Ritual
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(
                    onClick = {
                        viewModel.lessonRitualActive = false
                        viewModel.activeLesson = null
                    },
                    modifier = Modifier.testTag("exit_ritual_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Abandonar ritual",
                        tint = Color(0xFFFFB74D)
                    )
                }
                Text(
                    text = "Ritual de Desafío Azteca",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFFFB74D)
                )
                Spacer(modifier = Modifier.size(48.dp))
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Default.AutoAwesome,
                    contentDescription = "Ritual",
                    tint = Color(0xFFFFB74D),
                    modifier = Modifier.size(28.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Ritual del Enigma Azteca 🛕",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Black,
                    color = Color(0xFFFFB74D)
                )
            }
            
            Spacer(modifier = Modifier.height(10.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                for (i in 1..3) {
                    val isActive = viewModel.ritualCurrentStep == i
                    val isDone = viewModel.ritualCurrentStep > i
                    val stepColor = when {
                        isActive -> Color(0xFFFFB74D)
                        isDone -> Color(0xFF4CAF50)
                        else -> Color(0xFF3E2723)
                    }
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(6.dp)
                            .clip(CircleShape)
                            .background(stepColor)
                    )
                }
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Etapa del Ritual: ${viewModel.ritualCurrentStep} de 3",
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = Color.LightGray
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            when (viewModel.ritualCurrentStep) {
                1 -> {
                    Text(
                        text = "ETAPA 1: EL ENIGMA DEL SACERDOTE",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFFFB74D)
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF2E1C0C)),
                        border = BorderStroke(1.dp, Color(0xFFFFB74D).copy(alpha = 0.3f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Text(
                                text = viewModel.ritualQ1Question,
                                fontSize = 13.sp,
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                lineHeight = 17.sp
                            )
                        }
                    }
                    
                    val ritualQ1IsCorrect = viewModel.ritualQ1SelectedIndex == viewModel.ritualQ1CorrectIndex
                    Spacer(modifier = Modifier.height(14.dp))
                    
                    viewModel.ritualQ1Options.forEachIndexed { idx, option ->
                        val isSel = viewModel.ritualQ1SelectedIndex == idx
                        val bg = if (isSel) Color(0xFFFFB74D).copy(alpha = 0.25f) else Color(0xFF201308)
                        val borderCol = if (isSel) Color(0xFFFFB74D) else Color(0xFF3E2723)
                        Card(
                            onClick = { 
                                if (!viewModel.ritualQ1Verified) {
                                    viewModel.ritualQ1SelectedIndex = idx
                                }
                            },
                            colors = CardDefaults.cardColors(containerColor = bg),
                            border = BorderStroke(1.5.dp, borderCol),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                                .testTag("ritual_q1_option_$idx"),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(
                                text = option,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                modifier = Modifier.padding(12.dp)
                            )
                        }
                    }
                    
                    if (viewModel.ritualQ1Verified) {
                        Spacer(modifier = Modifier.height(12.dp))
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = if (ritualQ1IsCorrect) Color(0xFF1B5E20).copy(alpha = 0.3f) else Color(0xFFB71C1C).copy(alpha = 0.3f)
                            ),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Text(
                                    text = if (ritualQ1IsCorrect) "¡Correcto! Oráculo Satisfecho" else "Incorrecto. Los espíritus se agitan.",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (ritualQ1IsCorrect) Color(0xFF81C784) else Color(0xFFE57373)
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = viewModel.ritualQ1Feedback,
                                    fontSize = 10.sp,
                                    color = Color.LightGray,
                                    lineHeight = 14.sp
                                )
                            }
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(20.dp))
                    
                    if (!viewModel.ritualQ1Verified) {
                        Button(
                            onClick = { viewModel.verifyStep1() },
                            enabled = viewModel.ritualQ1SelectedIndex != null,
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFB74D), contentColor = Color.Black),
                            modifier = Modifier.fillMaxWidth().testTag("verify_step1_btn")
                        ) {
                            Text("Interrogar Sacerdote", fontSize = 12.sp, fontWeight = FontWeight.Black)
                        }
                    } else {
                        Button(
                            onClick = { 
                                if (ritualQ1IsCorrect) {
                                    viewModel.ritualCurrentStep = 2
                                } else {
                                    viewModel.ritualQ1SelectedIndex = null
                                    viewModel.ritualQ1Verified = false
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = if (ritualQ1IsCorrect) Color(0xFF4CAF50) else Color(0xFFEF5350)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(if (ritualQ1IsCorrect) "Proceder a la Etapa 2 ➡️" else "Intentar de nuevo", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
                2 -> {
                    Text(
                        text = "ETAPA 2: RECONSTRUCCIÓN DE GLIFOS SAGRADOS 🧩",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFFFB74D)
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF2E1C0C)),
                        border = BorderStroke(1.dp, Color(0xFFFFB74D).copy(alpha = 0.3f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Text(
                                text = "Reordena los glifos para formar la frase náhuatl que significa:",
                                fontSize = 11.sp,
                                color = Color.Gray
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "\"${viewModel.ritualQ2SpanishPrompt}\"",
                                fontSize = 14.sp,
                                color = Color.White,
                                fontWeight = FontWeight.Black,
                                lineHeight = 17.sp
                            )
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // Display current reconstruction phrase (selected tokens)
                    Text(
                        text = "Tu Frase Reconstruida:",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.LightGray,
                        modifier = Modifier.align(Alignment.Start)
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF140B04)),
                        border = BorderStroke(1.dp, Color(0xFF3E2723)),
                        modifier = Modifier.fillMaxWidth().heightIn(min = 60.dp),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(10.dp),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            if (viewModel.ritualQ2SelectedWords.isEmpty()) {
                                Text(
                                    text = "Toca los glifos de abajo para ordenarlos",
                                    fontSize = 11.sp,
                                    color = Color.Gray,
                                    fontStyle = FontStyle.Italic
                                )
                            } else {
                                viewModel.ritualQ2SelectedWords.forEach { token ->
                                    Card(
                                        onClick = {
                                            if (!viewModel.ritualQ2Verified) {
                                                viewModel.ritualQ2SelectedWords = viewModel.ritualQ2SelectedWords.filter { it != token }
                                            }
                                        },
                                        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFB74D).copy(alpha = 0.15f)),
                                        border = BorderStroke(1.dp, Color(0xFFFFB74D)),
                                        shape = RoundedCornerShape(8.dp),
                                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                text = token,
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = Color.White
                                            )
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Icon(
                                                imageVector = Icons.Default.Close,
                                                contentDescription = "Remover",
                                                tint = Color(0xFFFFB74D),
                                                modifier = Modifier.size(12.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // Display available tokens
                    Text(
                        text = "Glifos Disponibles:",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.LightGray,
                        modifier = Modifier.align(Alignment.Start)
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        viewModel.ritualQ2ShuffledWords.forEach { token ->
                            val isUsed = viewModel.ritualQ2SelectedWords.contains(token)
                            Card(
                                onClick = {
                                    if (!viewModel.ritualQ2Verified && !isUsed) {
                                        viewModel.ritualQ2SelectedWords = viewModel.ritualQ2SelectedWords + token
                                    }
                                },
                                colors = CardDefaults.cardColors(
                                    containerColor = if (isUsed) Color(0xFF2E1C0C).copy(alpha = 0.5f) else Color(0xFF2E1C0C)
                                ),
                                border = BorderStroke(
                                    width = 1.dp,
                                    color = if (isUsed) Color(0xFF3E2723) else Color(0xFFFFB74D).copy(alpha = 0.6f)
                                ),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier
                                    .padding(horizontal = 4.dp, vertical = 2.dp)
                                    .alpha(if (isUsed) 0.4f else 1f)
                            ) {
                                Text(
                                    text = token,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                )
                            }
                        }
                    }
                    
                    if (viewModel.ritualQ2Verified) {
                        Spacer(modifier = Modifier.height(14.dp))
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = if (viewModel.ritualQ2IsCorrect) Color(0xFF1B5E20).copy(alpha = 0.3f) else Color(0xFFB71C1C).copy(alpha = 0.3f)
                            ),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Text(
                                    text = if (viewModel.ritualQ2IsCorrect) "¡Ortografía Sagrada Correcta!" else "Inexacto. Los vientos soplan con fuerza.",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (viewModel.ritualQ2IsCorrect) Color(0xFF81C784) else Color(0xFFE57373)
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = viewModel.ritualQ2Feedback,
                                    fontSize = 10.sp,
                                    color = Color.LightGray,
                                    lineHeight = 14.sp
                                )
                            }
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(20.dp))
                    
                    if (!viewModel.ritualQ2Verified) {
                        Button(
                            onClick = { viewModel.verifyStep2() },
                            enabled = viewModel.ritualQ2SelectedWords.isNotEmpty(),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFB74D), contentColor = Color.Black),
                            modifier = Modifier.fillMaxWidth().testTag("verify_step2_btn")
                        ) {
                            Text("Consolidar Escritura", fontSize = 12.sp, fontWeight = FontWeight.Black)
                        }
                    } else {
                        Button(
                            onClick = { 
                                if (viewModel.ritualQ2IsCorrect) {
                                    viewModel.ritualCurrentStep = 3
                                } else {
                                    viewModel.ritualQ2SelectedWords = emptyList()
                                    viewModel.ritualQ2Verified = false
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = if (viewModel.ritualQ2IsCorrect) Color(0xFF4CAF50) else Color(0xFFEF5350)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(if (viewModel.ritualQ2IsCorrect) "Proceder a la Etapa 3 ➡️" else "Intentar de nuevo", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
                3 -> {
                    Text(
                        text = "ETAPA 3: EL ORÁCULO DE HUITZILOPOCHTLI 🔮",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFFFB74D)
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF2E1C0C)),
                        border = BorderStroke(1.dp, Color(0xFFFFB74D).copy(alpha = 0.3f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Text(
                                text = "El gran oráculo celestial afirma lo siguiente:",
                                fontSize = 11.sp,
                                color = Color.Gray
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "¿Es VERDAD que el vocablo náhuatl \"${viewModel.ritualQ3NahuatlWord}\" significa en español \"${viewModel.ritualQ3SpanishClaim}\"?",
                                fontSize = 13.sp,
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                lineHeight = 17.sp
                            )
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(20.dp))
                    
                    // Binary Choice Buttons (Verdadero / Falso)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Option TRUE (Verdadero)
                        val isTrueSel = viewModel.ritualQThirdSelectedIndex == true
                        val bgTrue = if (isTrueSel) Color(0xFF4CAF50).copy(alpha = 0.25f) else Color(0xFF201308)
                        val borderTrue = if (isTrueSel) Color(0xFF4CAF50) else Color(0xFF3E2723)
                        Card(
                            onClick = { 
                                if (!viewModel.ritualQ3Verified) {
                                    viewModel.ritualQThirdSelectedIndex = true
                                }
                            },
                            colors = CardDefaults.cardColors(containerColor = bgTrue),
                            border = BorderStroke(1.5.dp, borderTrue),
                            modifier = Modifier
                                .weight(1f)
                                .height(56.dp)
                                .testTag("ritual_q3_true_card"),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                Text(
                                    text = "VERDADERO 👍",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Black,
                                    color = if (isTrueSel) Color(0xFF81C784) else Color.White
                                )
                            }
                        }
                        
                        // Option FALSE (Falso)
                        val isFalseSel = viewModel.ritualQThirdSelectedIndex == false
                        val bgFalse = if (isFalseSel) Color(0xFFF44336).copy(alpha = 0.25f) else Color(0xFF201308)
                        val borderFalse = if (isFalseSel) Color(0xFFF44336) else Color(0xFF3E2723)
                        Card(
                            onClick = { 
                                if (!viewModel.ritualQ3Verified) {
                                    viewModel.ritualQThirdSelectedIndex = false
                                }
                            },
                            colors = CardDefaults.cardColors(containerColor = bgFalse),
                            border = BorderStroke(1.5.dp, borderFalse),
                            modifier = Modifier
                                .weight(1f)
                                .height(56.dp)
                                .testTag("ritual_q3_false_card"),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                Text(
                                    text = "FALSO 👎",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Black,
                                    color = if (isFalseSel) Color(0xFFE57373) else Color.White
                                )
                            }
                        }
                    }
                    
                    if (viewModel.ritualQ3Verified) {
                        Spacer(modifier = Modifier.height(14.dp))
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = if (viewModel.ritualQ3IsCorrect) Color(0xFF1B5E20).copy(alpha = 0.3f) else Color(0xFFB71C1C).copy(alpha = 0.3f)
                            ),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Text(
                                    text = if (viewModel.ritualQ3IsCorrect) "¡Gran Oráculo Concluido Con Éxito!" else "Intento Fallido. Los dioses reclaman.",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (viewModel.ritualQ3IsCorrect) Color(0xFF81C784) else Color(0xFFE57373)
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = viewModel.ritualQ3Feedback,
                                    fontSize = 10.sp,
                                    color = Color.LightGray,
                                    lineHeight = 14.sp
                                )
                            }
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(24.dp))
                    
                    if (!viewModel.ritualQ3Verified) {
                        Button(
                            onClick = { viewModel.verifyStep3() },
                            enabled = viewModel.ritualQThirdSelectedIndex != null,
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFB74D), contentColor = Color.Black),
                            modifier = Modifier.fillMaxWidth().testTag("verify_step3_btn")
                        ) {
                            Text("Interrogar Gran Oráculo", fontSize = 12.sp, fontWeight = FontWeight.Black)
                        }
                    } else {
                        if (viewModel.ritualQ3IsCorrect) {
                            Button(
                                onClick = { 
                                    viewModel.finishActiveLessonAndClaimXp()
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50)),
                                modifier = Modifier.fillMaxWidth().testTag("complete_ritual_btn")
                            ) {
                                Icon(Icons.Default.Verified, contentDescription = "Éxito")
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Consolidar Ritual Sacerdotal 🎉", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        } else {
                            Button(
                                onClick = {
                                    viewModel.ritualQThirdSelectedIndex = null
                                    viewModel.ritualQ3Verified = false
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF5350)),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("Volver a Preguntar", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun FullScreenGameScreen(viewModel: MainViewModel) {
    val level = viewModel.selectedGameLevel ?: return
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.surface,
                        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
                    )
                )
            )
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Exit Button Row for Games
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(
                    onClick = {
                        viewModel.selectedGameLevel = null
                    },
                    modifier = Modifier.testTag("exit_game_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Salir del juego",
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }
                Text(
                    text = "Desafío de Juego",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.size(48.dp))
            }

            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.25f)),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(
                        text = "DESAFÍO LÚDICO OBLIGATORIO 🛕",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "${level.name} (NIVEL ${level.id} · ${level.type.uppercase()})",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Black,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(14.dp))
            
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.15f)),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                ) {
                    when (level.type) {
                        "Memorama" -> {
                            MemoramaGameView(viewModel)
                        }
                        "Quiz" -> {
                            QuizGameView(viewModel)
                        }
                        "Scramble" -> {
                            Column(
                                modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Text(
                                    text = "Reordena las letras para formar la palabra:",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    textAlign = TextAlign.Center
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                                
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    viewModel.scrambleShuffledWord.forEach { char ->
                                        Card(
                                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)),
                                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary),
                                            shape = RoundedCornerShape(8.dp)
                                        ) {
                                            Text(
                                                text = char.toString(),
                                                fontSize = 20.sp,
                                                fontWeight = FontWeight.Black,
                                                color = MaterialTheme.colorScheme.primary,
                                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                                            )
                                        }
                                    }
                                }
                                
                                Spacer(modifier = Modifier.height(24.dp))
                                
                                OutlinedTextField(
                                    value = viewModel.scrambleUserAnswer,
                                    onValueChange = { viewModel.scrambleUserAnswer = it },
                                    modifier = Modifier.fillMaxWidth().testTag("scramble_input_field"),
                                    placeholder = { Text("Escribe tu respuesta aquí...", fontSize = 13.sp) },
                                    singleLine = true,
                                    shape = RoundedCornerShape(12.dp)
                                )
                                
                                Spacer(modifier = Modifier.height(16.dp))
                                
                                Button(
                                    onClick = { viewModel.submitScrambleAnswer() },
                                    modifier = Modifier.fillMaxWidth().height(48.dp),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Text("Validar Palabra")
                                }
                                
                                viewModel.scrambleIsCorrect?.let { isCorr ->
                                    Spacer(modifier = Modifier.height(16.dp))
                                    Card(
                                        colors = CardDefaults.cardColors(
                                            containerColor = if (isCorr) Color(0xFFE8F5E9) else Color(0xFFFFEBEE)
                                        ),
                                        shape = RoundedCornerShape(12.dp)
                                    ) {
                                        Column(
                                            modifier = Modifier.padding(12.dp),
                                            horizontalAlignment = Alignment.CenterHorizontally
                                        ) {
                                            Text(
                                                text = if (isCorr) "¡NILTZE! Es correcto 🎉" else "¡Intenta de nuevo ya casi lo tienes!",
                                                fontWeight = FontWeight.Bold,
                                                color = if (isCorr) Color(0xFF2E7D32) else Color(0xFFC62828),
                                                fontSize = 13.sp
                                            )
                                            if (isCorr) {
                                                Text(
                                                    text = "Recompensa otorgada de +25 XP. Has subido en maestría.",
                                                    color = Color.DarkGray,
                                                    fontSize = 11.sp,
                                                    textAlign = TextAlign.Center
                                                )
                                                Spacer(modifier = Modifier.height(8.dp))
                                                Button(
                                                    onClick = { viewModel.selectedGameLevel = null },
                                                    shape = RoundedCornerShape(8.dp)
                                                ) {
                                                    Text("Volver a Niveles", fontSize = 11.sp)
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        "QuickTranslate" -> {
                            Column(
                                modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Text(
                                    text = "¿Cuál es el significado correcto en español de esta frase?",
                                    fontSize = 13.sp,
                                    color = Color.Gray,
                                    textAlign = TextAlign.Center
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.Center
                                ) {
                                    Text(
                                        text = viewModel.qtNahuatlWord.uppercase(),
                                        fontSize = 24.sp,
                                        fontWeight = FontWeight.Black,
                                        color = MaterialTheme.colorScheme.primary,
                                        textAlign = TextAlign.Center
                                    )
                                    IconButton(
                                        onClick = { viewModel.speak(viewModel.qtNahuatlWord) },
                                        modifier = Modifier.size(36.dp).testTag("speak_qt_word")
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.VolumeUp,
                                            contentDescription = "Escuchar voz",
                                            tint = MaterialTheme.colorScheme.primary,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                }
                                
                                Spacer(modifier = Modifier.height(24.dp))
                                
                                Column(
                                    verticalArrangement = Arrangement.spacedBy(8.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    viewModel.qtOptions.forEach { option ->
                                        val isSel = viewModel.qtUserAnswer == option
                                        val bg = if (isSel) {
                                            if (viewModel.qtIsCorrect == true) Color(0xFFC8E6C9)
                                            else Color(0xFFFFCDD2)
                                        } else {
                                            MaterialTheme.colorScheme.surface
                                        }
                                        Card(
                                            onClick = { viewModel.submitQuickTranslateAnswer(option) },
                                            colors = CardDefaults.cardColors(containerColor = bg),
                                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)),
                                            shape = RoundedCornerShape(12.dp),
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            Text(
                                                text = option,
                                                fontSize = 14.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = MaterialTheme.colorScheme.onSurface,
                                                modifier = Modifier.padding(14.dp)
                                            )
                                        }
                                    }
                                }
                                
                                viewModel.qtIsCorrect?.let { isCorr ->
                                    Spacer(modifier = Modifier.height(16.dp))
                                    Text(
                                        text = if (isCorr) "¡Respuesta Correcta! +20 XP ganados" else "Incorrecto. Intenta con otra opción.",
                                        fontWeight = FontWeight.Bold,
                                        color = if (isCorr) Color(0xFF2E7D32) else Color(0xFFC62828),
                                        fontSize = 13.sp
                                    )
                                    if (isCorr) {
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Button(
                                            onClick = { viewModel.selectedGameLevel = null },
                                            shape = RoundedCornerShape(8.dp)
                                        ) {
                                            Text("Volver a Niveles", fontSize = 11.sp)
                                        }
                                    }
                                }
                            }
                        }
                        "GlyphMatch" -> {
                            Column(
                                modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Text(
                                    text = "📜 Códice Ancestral: Identifica el glifo correspondiente",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary,
                                    textAlign = TextAlign.Center
                                )
                                Text(
                                    text = "Identifica el glifo astrológico correspondiente a la palabra ancestral:",
                                    fontSize = 11.sp,
                                    textAlign = TextAlign.Center,
                                    color = Color.Gray
                                )
                                Spacer(modifier = Modifier.height(12.dp))
                                Card(
                                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFFFDE7)),
                                    border = BorderStroke(2.dp, Color(0xFFFFB74D)),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Box(
                                        modifier = Modifier.fillMaxWidth().padding(18.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.Center
                                        ) {
                                            Text(
                                                text = viewModel.glyphNahuatlWord.uppercase(),
                                                fontSize = 24.sp,
                                                fontWeight = FontWeight.Black,
                                                color = Color(0xFF5D4037),
                                                textAlign = TextAlign.Center
                                            )
                                            IconButton(
                                                onClick = { viewModel.speak(viewModel.glyphNahuatlWord) },
                                                modifier = Modifier.size(36.dp).testTag("speak_glyph_word_2")
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.VolumeUp,
                                                    contentDescription = "Escuchar voz",
                                                    tint = Color(0xFF5D4037),
                                                    modifier = Modifier.size(20.dp)
                                                )
                                            }
                                        }
                                    }
                                }
                                
                                Spacer(modifier = Modifier.height(20.dp))
                                
                                LazyVerticalGrid(
                                    columns = GridCells.Fixed(2),
                                    verticalArrangement = Arrangement.spacedBy(10.dp),
                                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                                    modifier = Modifier.fillMaxWidth().height(170.dp)
                                ) {
                                    items(viewModel.glyphOptions) { option ->
                                        val isSel = viewModel.glyphSelectedSymbol == option
                                        val bg = if (isSel) {
                                            if (viewModel.glyphIsCorrect == true) Color(0xFFC8E6C9) else Color(0xFFFFCDD2)
                                        } else MaterialTheme.colorScheme.surface
                                        val borderCol = if (isSel) {
                                            if (viewModel.glyphIsCorrect == true) Color(0xFF4CAF50) else Color(0xFFF44336)
                                        } else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                                        Card(
                                            onClick = {
                                                if (viewModel.glyphSelectedSymbol == null) {
                                                    viewModel.submitGlyphMatchAnswer(option)
                                                }
                                            },
                                            colors = CardDefaults.cardColors(containerColor = bg),
                                            border = BorderStroke(2.dp, borderCol),
                                            shape = RoundedCornerShape(14.dp)
                                        ) {
                                            Box(
                                                modifier = Modifier.fillMaxSize().padding(12.dp),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Text(
                                                    text = option,
                                                    fontSize = 13.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    textAlign = TextAlign.Center,
                                                    color = MaterialTheme.colorScheme.onSurface
                                                )
                                            }
                                        }
                                    }
                                }
                                
                                viewModel.glyphIsCorrect?.let { isCorr ->
                                    Spacer(modifier = Modifier.height(16.dp))
                                    Card(
                                        colors = CardDefaults.cardColors(
                                            containerColor = if (isCorr) Color(0xFFE8F5E9) else Color(0xFFFFEBEE)
                                        ),
                                        shape = RoundedCornerShape(12.dp),
                                        modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp)
                                    ) {
                                        Column(
                                            modifier = Modifier.padding(12.dp),
                                            horizontalAlignment = Alignment.CenterHorizontally
                                        ) {
                                            Text(
                                                text = if (isCorr) "¡Glifo del Códice descifrado con éxito! +25 XP 🎉" else "Glifo incorrecto, inténtalo de nuevo.",
                                                fontWeight = FontWeight.Bold,
                                                color = if (isCorr) Color(0xFF2E7D32) else Color(0xFFC62828),
                                                fontSize = 12.5.sp,
                                                textAlign = TextAlign.Center
                                            )
                                            Spacer(modifier = Modifier.height(8.dp))
                                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                                if (!isCorr) {
                                                    OutlinedButton(
                                                        onClick = {
                                                            viewModel.selectedGameLevel?.let { viewModel.startInteractiveLevelGame(it) }
                                                        },
                                                        shape = RoundedCornerShape(8.dp)
                                                    ) {
                                                        Text("Reintentar 🔄", fontSize = 11.sp)
                                                    }
                                                } else {
                                                    Button(
                                                        onClick = {
                                                            viewModel.selectedGameLevel?.let { viewModel.startInteractiveLevelGame(it) }
                                                        },
                                                        shape = RoundedCornerShape(8.dp)
                                                    ) {
                                                        Text("Siguiente Glifo ➡️", fontSize = 11.sp)
                                                    }
                                                }
                                                Button(
                                                    onClick = { viewModel.selectedGameLevel = null },
                                                    shape = RoundedCornerShape(8.dp)
                                                ) {
                                                    Text("Volver a Niveles", fontSize = 11.sp)
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        "Ahorcado" -> {
                            AhorcadoGameView(viewModel)
                        }
                        "FillBlank" -> {
                            FillBlankGameView(viewModel)
                        }
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(10.dp))
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("ai_oracle_card"),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.2f)),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.tertiary.copy(alpha = 0.25f))
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = "IA",
                                tint = MaterialTheme.colorScheme.tertiary,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "ORÁCULO CELESTIAL IA",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.tertiary
                            )
                        }
                        
                        if (viewModel.isRequestingAiGameHint) {
                            CircularProgressIndicator(modifier = Modifier.size(14.dp), strokeWidth = 2.dp)
                        } else {
                            Button(
                                onClick = { viewModel.requestAiGameHint() },
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.tertiary),
                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.testTag("request_ai_hint_btn")
                            ) {
                                Text("Solicitar Pista", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                    
                    viewModel.aiGameHintText?.let { hint ->
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = hint,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onTertiaryContainer,
                            lineHeight = 14.sp
                        )
                    }
                }
            }
        }
    }
}


@Composable
fun AhorcadoGameView(viewModel: MainViewModel) {
    val word = viewModel.hangmanOriginalWord
    val guessed = viewModel.hangmanGuessedLetters
    val attempts = viewModel.hangmanAttemptsLeft
    val isCorrect = viewModel.hangmanIsCorrect

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "🛡️ ¡Salva el Escudo del Guerrero Sagrado! 🛡️",
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            textAlign = TextAlign.Center
        )
        Text(
            text = "Adivina la palabra letra por letra antes de quedarte sin escudos.",
            fontSize = 11.sp,
            color = Color.Gray,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Life / Defense Shields using emojis for safe compatibility
        Row(
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Defensa: ", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
            repeat(viewModel.hangmanMaxAttempts) { index ->
                val hasShield = index < attempts
                Text(
                    text = if (hasShield) "🛡️" else "🖤",
                    fontSize = 20.sp,
                    modifier = Modifier.alpha(if (hasShield) 1.0f else 0.4f)
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Word display
        Row(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 8.dp)
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                word.forEach { char ->
                    val upperChar = char.uppercaseChar()
                    val isRevealed = guessed.contains(upperChar) || char == ' '
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = if (isRevealed) char.toString() else " ",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Black,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Box(
                            modifier = Modifier
                                .width(20.dp)
                                .height(3.dp)
                                .background(MaterialTheme.colorScheme.primary)
                        )
                    }
                }
            }
            IconButton(
                onClick = { viewModel.speak(word) },
                modifier = Modifier.size(36.dp).testTag("speak_hangman_word")
            ) {
                Icon(
                    imageVector = Icons.Default.VolumeUp,
                    contentDescription = "Escuchar voz",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(28.dp))

        // Keypad
        val alphabet = ('A'..'Z').toList()
        Column(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            val rows = alphabet.chunked(7)
            rows.forEach { row ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    row.forEach { char ->
                        val wasGuessed = guessed.contains(char)
                        Card(
                            onClick = { if (!wasGuessed && isCorrect == null) viewModel.submitHangmanLetter(char) },
                            colors = CardDefaults.cardColors(
                                containerColor = if (wasGuessed) Color.LightGray.copy(alpha = 0.4f)
                                else MaterialTheme.colorScheme.secondary.copy(alpha = 0.1f)
                            ),
                            border = BorderStroke(
                                1.dp,
                                if (wasGuessed) Color.Transparent
                                else MaterialTheme.colorScheme.secondary.copy(alpha = 0.3f)
                            ),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .size(36.dp)
                                .alpha(if (wasGuessed) 0.5f else 1f)
                        ) {
                            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                Text(
                                    text = char.toString(),
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (wasGuessed) Color.Gray else MaterialTheme.colorScheme.secondary
                                )
                            }
                        }
                    }
                }
            }
        }

        // Result Banner
        isCorrect?.let { isCorr ->
            Spacer(modifier = Modifier.height(20.dp))
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = if (isCorr) Color(0xFFE8F5E9) else Color(0xFFFFEBEE)
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp)
            ) {
                Column(
                    modifier = Modifier.padding(12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = if (isCorr) "¡GLORIA AL GUERRERO! 🎉" else "¡HAS CAÍDO EN LA BATALLA! 🪓",
                        fontWeight = FontWeight.Bold,
                        color = if (isCorr) Color(0xFF2E7D32) else Color(0xFFC62828),
                        fontSize = 13.sp
                    )
                    Text(
                        text = if (isCorr) "Palabra revelada con éxito. Recompensa: +30 XP." 
                               else "La palabra correcta era: $word.",
                        color = Color.DarkGray,
                        fontSize = 11.sp,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(
                        onClick = { viewModel.selectedGameLevel = null },
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Volver a Niveles", fontSize = 11.sp)
                    }
                }
            }
        }
    }
}

@Composable
fun FillBlankGameView(viewModel: MainViewModel) {
    val question = viewModel.fillBlankQuestionPhrase
    val options = viewModel.fillBlankOptions
    val selected = viewModel.fillBlankUserAnswer
    val isCorrect = viewModel.fillBlankIsCorrect

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "📜 Completa el Códice Sagrado 📜",
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            textAlign = TextAlign.Center
        )
        Text(
            text = "Selecciona la palabra que completa correctamente la frase.",
            fontSize = 11.sp,
            color = Color.Gray,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Codex Board
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFFFFDE7)),
            border = BorderStroke(2.dp, Color(0xFFFFB74D)),
            shape = RoundedCornerShape(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = question,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Black,
                    color = Color(0xFF5D4037),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.headlineSmall
                )
            }
        }

        Spacer(modifier = Modifier.height(30.dp))

        // Options list
        Column(
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        ) {
            options.forEach { option ->
                val isSelected = selected == option
                val bg = if (isSelected) {
                    if (isCorrect == true) Color(0xFFC8E6C9)
                    else Color(0xFFFFCDD2)
                } else {
                    MaterialTheme.colorScheme.surface
                }
                val borderCol = if (isSelected) {
                    if (isCorrect == true) Color(0xFF4CAF50)
                    else Color(0xFFF44336)
                } else {
                    MaterialTheme.colorScheme.outline.copy(alpha = 0.25f)
                }

                Card(
                    onClick = { if (selected == null) viewModel.submitFillBlankAnswer(option) },
                    colors = CardDefaults.cardColors(containerColor = bg),
                    border = BorderStroke(1.5.dp, borderCol),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = if (isSelected) {
                                if (isCorrect == true) Icons.Default.CheckCircle
                                else Icons.Default.Cancel
                            } else Icons.Default.Circle,
                            contentDescription = "Opción",
                            tint = if (isSelected) {
                                if (isCorrect == true) Color(0xFF2E7D32)
                                else Color(0xFFC62828)
                            } else Color.Gray.copy(alpha = 0.5f)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = option,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
        }

        // Feedback Banner
        isCorrect?.let { isCorr ->
            Spacer(modifier = Modifier.height(24.dp))
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = if (isCorr) Color(0xFFE8F5E9) else Color(0xFFFFEBEE)
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = if (isCorr) "¡Niltze! Es Correcto 🎉" else "Códice Incorrecto, inténtalo de nuevo.",
                        fontWeight = FontWeight.Bold,
                        color = if (isCorr) Color(0xFF2E7D32) else Color(0xFFC62828),
                        fontSize = 13.sp
                    )
                    if (isCorr) {
                        Text(
                            text = "Has ganado +25 XP. Tu sabiduría sigue floreciendo.",
                            color = Color.DarkGray,
                            fontSize = 11.sp,
                            textAlign = TextAlign.Center
                        )
                    } else {
                        Text(
                            text = "La respuesta correcta era: ${viewModel.fillBlankCorrectWord}",
                            color = Color.DarkGray,
                            fontSize = 11.sp,
                            textAlign = TextAlign.Center
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        if (!isCorr) {
                            OutlinedButton(
                                onClick = {
                                    viewModel.selectedGameLevel?.let { viewModel.startInteractiveLevelGame(it) }
                                },
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text("Reintentar 🔄", fontSize = 11.sp)
                            }
                        } else {
                            Button(
                                onClick = {
                                    viewModel.selectedGameLevel?.let { viewModel.startInteractiveLevelGame(it) }
                                },
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text("Siguiente Frase ➡️", fontSize = 11.sp)
                            }
                        }
                        Button(
                            onClick = { viewModel.selectedGameLevel = null },
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("Volver a Niveles", fontSize = 11.sp)
                        }
                    }
                }
            }
        }
    }
}


