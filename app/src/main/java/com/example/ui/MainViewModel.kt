package com.example.ui

import android.app.Application
import android.os.Build
import android.speech.tts.TextToSpeech
import java.util.Locale
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

// --- Memory Match Card Definition ---
data class MemoryCard(
    val id: Int,
    val text: String,
    val pairId: Int,
    val isFlipped: Boolean = false,
    val isMatched: Boolean = false
)

// --- Word Quiz Definition ---
data class QuizQuestion(
    val question: String,
    val options: List<String>,
    val correctAnswer: String,
    val explanation: String
)

class MainViewModel(application: Application) : AndroidViewModel(application) {
    private val db = AppDatabase.getDatabase(application)
    private val repository = NahuatlRepository(
        userProgressDao = db.userProgressDao(),
        lessonDao = db.lessonDao(),
        wordDao = db.wordDao(),
        challengeDao = db.challengeDao(),
        dailyStreakDao = db.dailyStreakDao(),
        sacerdoteAudioCacheDao = db.sacerdoteAudioCacheDao()
    )

    // --- Room Sacerdote Voice Audio Cache Tracking ---
    val cachedSacerdoteAudios: StateFlow<List<SacerdoteAudioCacheEntity>> = repository.cachedSacerdoteAudiosFlow
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val cachedSacerdoteCount: StateFlow<Int> = repository.cachedSacerdoteAudiosCountFlow
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = 0
        )

    // --- Room Daily Streak Tracking ---
    private val _streakStats = MutableStateFlow(DailyStreakStats())
    val streakStats: StateFlow<DailyStreakStats> = _streakStats.asStateFlow()

    val dailyInteractions: StateFlow<List<DailyInteractionEntity>> = repository.dailyInteractionsFlow
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    var showDailyStreakDialog by mutableStateOf(false)

    var isCodeProtectionActive by mutableStateOf(false)
    var petOffsetX by mutableStateOf(0f)
    var petOffsetY by mutableStateOf(0f)
    var showLanguageSelectionOnboarding by mutableStateOf(false)

    // --- Text-To-Speech (Pronunciation Engine) & XTTS Integration ---
    val xttsClient = XttsAudioClient(application)
    var xttsConfig by mutableStateOf(XttsConfig())
    var showXttsConfigDialog by mutableStateOf(false)
    var isXttsSynthesizing by mutableStateOf(false)
    var sacerdoteVoiceInputText by mutableStateOf("Nietzsche, joven guerrero")
    var sacerdoteStatusFeedback by mutableStateOf<String?>(null)

    private var tts: TextToSpeech? = null
    var isTtsReady by mutableStateOf(false)
    var isSpeaking by mutableStateOf(false)
    var currentlySpeakingText by mutableStateOf<String?>(null)


    // Ensure database prepopulates immediately on launch
    init {
        initTts()
        loadXttsConfig()
        viewModelScope.launch {
            repository.initializeDatabaseIfNeeded()
            // Automatically record today's login in Room database and calculate consecutive days
            val stats = repository.recordDailyInteraction(interactionType = "LOGIN", xpEarned = 10)
            _streakStats.value = stats
            refreshLivesAndPetStatus()
            
            // Load code security mode state from SharedPreferences
            val prefs = application.getSharedPreferences("security_prefs", android.content.Context.MODE_PRIVATE)
            isCodeProtectionActive = prefs.getBoolean("code_protection_active", false)
            
            startPetRoaming()
        }
    }


    fun initTts() {
        try {
            tts = TextToSpeech(getApplication()) { status ->
                if (status == TextToSpeech.SUCCESS) {
                    val result = tts?.setLanguage(Locale("es", "MX"))
                    if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                        tts?.setLanguage(Locale("es", "ES"))
                    }

                    // Select Male voice if available in system voices
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        try {
                            val availableVoices = tts?.voices
                            if (!availableVoices.isNullOrEmpty()) {
                                val maleVoice = availableVoices.firstOrNull { voice ->
                                    val name = voice.name.lowercase()
                                    val lang = voice.locale.language
                                    lang == "es" && (name.contains("male") || name.contains("masculino") || name.contains("hombre") || name.contains("-m-") || name.contains("_m_") || name.contains("esm") || name.contains("sfd"))
                                } ?: availableVoices.firstOrNull { voice ->
                                    voice.locale.language == "es" && voice.name.lowercase().contains("male")
                                } ?: availableVoices.firstOrNull { voice ->
                                    voice.name.lowercase().contains("male")
                                }
                                if (maleVoice != null) {
                                    tts?.voice = maleVoice
                                }
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }

                    tts?.setSpeechRate(0.85f)
                    tts?.setPitch(0.78f) // Deeper masculine pitch
                    tts?.setOnUtteranceProgressListener(object : android.speech.tts.UtteranceProgressListener() {
                        override fun onStart(utteranceId: String?) {
                            isSpeaking = true
                        }

                        override fun onDone(utteranceId: String?) {
                            isSpeaking = false
                            currentlySpeakingText = null
                        }

                        @Deprecated("Deprecated in Java")
                        override fun onError(utteranceId: String?) {
                            isSpeaking = false
                            currentlySpeakingText = null
                        }
                    })
                    isTtsReady = true
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    // --- Variant Pronunciation Profiles & Customizations ---
    data class VariantPronunciationProfile(
        val pitch: Float,
        val speed: Float,
        val phoneticLabel: String,
        val description: String,
        val textTransformer: (String) -> String
    )

    fun getVariantPronunciationProfile(variantName: String): VariantPronunciationProfile {
        val name = variantName.lowercase()
        return when {
            // --- NAHUATL VARIANTS ---
            name.contains("pajapan") || name.contains("istmo") || name.contains("nawat") -> {
                VariantPronunciationProfile(
                    pitch = 1.15f,
                    speed = 1.05f,
                    phoneticLabel = "Dialecto Nawat (TL -> T)",
                    description = "Variante cálida del Istmo veracruzano, habla rápida y rítmica con elisión de TL.",
                    textTransformer = { it.replace("tl", "t").replace("TL", "T").replace("Tl", "T") }
                )
            }
            name.contains("huasteca veracruzana") || name.contains("veracruzana") -> {
                VariantPronunciationProfile(
                    pitch = 1.10f,
                    speed = 0.92f,
                    phoneticLabel = "Vocalización alargada de la Huasteca",
                    description = "Giro dinámico con vocales prolongadas y tono cantarín tradicional.",
                    textTransformer = { it.replace("a", "aa").replace("e", "ee").replace("o", "oo") }
                )
            }
            name.contains("huasteca potosina") || name.contains("potosina") -> {
                VariantPronunciationProfile(
                    pitch = 1.05f,
                    speed = 1.00f,
                    phoneticLabel = "Acento Xantolo Potosino",
                    description = "Giro rítmico ceremonial con vocalización de rituales sagrados.",
                    textTransformer = { it.replace("u", "uu").replace("i", "ii") }
                )
            }
            name.contains("huasteca hidalguense") || name.contains("hidalguense") -> {
                VariantPronunciationProfile(
                    pitch = 1.00f,
                    speed = 0.90f,
                    phoneticLabel = "Giro hidalguense directo",
                    description = "Variante con pronunciación estándar y firme de la Huasteca oriental.",
                    textTransformer = { it }
                )
            }
            name.contains("nororiental") || name.contains("cuetzalan") -> {
                VariantPronunciationProfile(
                    pitch = 1.05f,
                    speed = 1.10f,
                    phoneticLabel = "Variante sibilante de Cuetzalan",
                    description = "Entonación poética, dulce y suave en las consonantes sibilantes.",
                    textTransformer = { it.replace("s", "sh").replace("S", "Sh") }
                )
            }
            name.contains("sierra negra") -> {
                VariantPronunciationProfile(
                    pitch = 1.25f,
                    speed = 0.85f,
                    phoneticLabel = "Acento montañoso elevado",
                    description = "Tono elevado de la Sierra de Tehuacán con respiraciones marcadas.",
                    textTransformer = { it.replace("e", "eh") }
                )
            }
            name.contains("milpa alta") || name.contains("central") || name.contains("anáhuac") || name.contains("clasico") || name.contains("clásico") || name.contains("imperial") -> {
                VariantPronunciationProfile(
                    pitch = 0.85f,
                    speed = 0.88f,
                    phoneticLabel = "Náhuatl Central / Imperial",
                    description = "Tono noble, calmado y de reverencia solemne del Anáhuac clásico.",
                    textTransformer = { ", , " + it.replace("!", ", !").replace("?", ", ?") }
                )
            }
            name.contains("zongolica") -> {
                VariantPronunciationProfile(
                    pitch = 0.90f,
                    speed = 0.80f,
                    phoneticLabel = "Entonación de neblina oaxaqueña",
                    description = "Variante muy suave con sufijo honorífico -tzin para dar reverencia.",
                    textTransformer = { it.replace("a", "ah").replace("o", "oh") }
                )
            }
            name.contains("guerrero") -> {
                VariantPronunciationProfile(
                    pitch = 1.18f,
                    speed = 1.12f,
                    phoneticLabel = "Asertivo de Guerrero",
                    description = "Vigoroso e imperativo de la Montaña, ideal para la Tigrada ritual.",
                    textTransformer = { it.replace("a", "a!") }
                )
            }
            name.contains("tabasco") -> {
                VariantPronunciationProfile(
                    pitch = 1.08f,
                    speed = 1.15f,
                    phoneticLabel = "Fusión Maya-Chontal",
                    description = "Acústica suave del trópico con ritmo acortado de sílabas.",
                    textTransformer = { it.replace("tl", "t").replace("a", "á") }
                )
            }
            name.contains("tlaxcala") -> {
                VariantPronunciationProfile(
                    pitch = 1.02f,
                    speed = 1.08f,
                    phoneticLabel = "Consonante limpia de la Malinche",
                    description = "Articulación nítida de fonemas dobles en faldas del volcán.",
                    textTransformer = { it.replace("c", "cc").replace("C", "Cc") }
                )
            }

            // --- TOTONACO VARIANTS ---
            name.contains("costa") -> {
                VariantPronunciationProfile(
                    pitch = 0.92f,
                    speed = 0.88f,
                    phoneticLabel = "Caudal costero relajado",
                    description = "Giro costero con vocales abiertas y menor tensión oclusiva.",
                    textTransformer = { it.replace("h", "'").replace("k", "kg") }
                )
            }
            name.contains("sierra") -> {
                VariantPronunciationProfile(
                    pitch = 1.22f,
                    speed = 1.05f,
                    phoneticLabel = "Enérgico de montaña",
                    description = "Tono agudo de la Sierra de Puebla de ritmo rápido.",
                    textTransformer = { it.replace("g", "kg").replace("a", "á") }
                )
            }
            name.contains("papantla") -> {
                VariantPronunciationProfile(
                    pitch = 1.00f,
                    speed = 0.90f,
                    phoneticLabel = "Ceremonial de Papantla",
                    description = "Llanuras de Papantla con cadencia ritual y solemne.",
                    textTransformer = { it.replace("lh", "l'h") }
                )
            }
            name.contains("misantla") -> {
                VariantPronunciationProfile(
                    pitch = 0.75f,
                    speed = 0.85f,
                    phoneticLabel = "Arcaico de Misantla",
                    description = "Tono profundo y sobrio del Totonaco antiguo de Misantla.",
                    textTransformer = { ", , " + it }
                )
            }
            name.contains("necaxa") -> {
                VariantPronunciationProfile(
                    pitch = 1.10f,
                    speed = 0.98f,
                    phoneticLabel = "Fluido del río Necaxa",
                    description = "Cadencia fluida, semejante al correr de las aguas serranas.",
                    textTransformer = { it.replace("n", "nh") }
                )
            }
            name.contains("filomeno") -> {
                VariantPronunciationProfile(
                    pitch = 1.30f,
                    speed = 0.95f,
                    phoneticLabel = "Ejectivo de Filomeno Mata",
                    description = "Consonantes marcadas y cortantes del pueblo guerrero.",
                    textTransformer = { it.replace("p", "p'") }
                )
            }

            // --- MAYA VARIANTS ---
            name.contains("yucateco") || name.contains("maayat") -> {
                VariantPronunciationProfile(
                    pitch = 0.95f,
                    speed = 0.90f,
                    phoneticLabel = "Maya Yucateco Glotalizado",
                    description = "Aireado con las pausas glotales inconfundibles de la península.",
                    textTransformer = { it.replace("'", "''").replace("t'", "t''") }
                )
            }
            name.contains("tojolabal") -> {
                VariantPronunciationProfile(
                    pitch = 1.15f,
                    speed = 1.05f,
                    phoneticLabel = "Nosótrico Tojolabal",
                    description = "Tono de la selva y frontera con entonación rítmica y firme.",
                    textTransformer = { it.replace("'", "' ") }
                )
            }
            name.contains("lacandón") || name.contains("lacand") || name.contains("hach") -> {
                VariantPronunciationProfile(
                    pitch = 0.85f,
                    speed = 0.75f,
                    phoneticLabel = "Susurro de la Selva",
                    description = "Místico y misterioso, pronunciado con pausas sagradas.",
                    textTransformer = { ", " + it.replace(" ", ", ") }
                )
            }
            name.contains("huasteco") || name.contains("téenek") || name.contains("teenek") -> {
                VariantPronunciationProfile(
                    pitch = 1.10f,
                    speed = 0.98f,
                    phoneticLabel = "Téenek Florido",
                    description = "Rama maya separada con hermosa entonación del bordado floral.",
                    textTransformer = { it.replace("k'", "k''") }
                )
            }

            // --- ZAPOTECO VARIANTS ---
            name.contains("istmo") || name.contains("diidxa") -> {
                VariantPronunciationProfile(
                    pitch = 1.25f,
                    speed = 1.05f,
                    phoneticLabel = "Canto de Juchitán",
                    description = "Tonalidad cantarína, melódica y poética de las velas del Istmo.",
                    textTransformer = { it.replace("z", "z'") }
                )
            }
            name.contains("sierra sur") -> {
                VariantPronunciationProfile(
                    pitch = 0.78f,
                    speed = 0.85f,
                    phoneticLabel = "Zapoteco de Sierra Sur",
                    description = "Variante tonal con ricas pausas aspiradas y glotales.",
                    textTransformer = { it.replace("'", "h'") }
                )
            }
            name.contains("costa") -> {
                VariantPronunciationProfile(
                    pitch = 0.95f,
                    speed = 1.12f,
                    phoneticLabel = "Brisa Costera Dizhsa",
                    description = "Variante de los valles de Pochutla, ágil y de sonidos relajados.",
                    textTransformer = { it.replace("r", "l") }
                )
            }

            // --- MIXTECO VARIANTS ---
            name.contains("mixteca alta") || name.contains("alta") -> {
                VariantPronunciationProfile(
                    pitch = 1.05f,
                    speed = 0.88f,
                    phoneticLabel = "Cadencia Pluvial Mixteca",
                    description = "Acento tonal nasalizado propio de la Mixteca Alta de Oaxaca.",
                    textTransformer = { it.replace("n", "n'") }
                )
            }
            name.contains("guerrero") -> {
                VariantPronunciationProfile(
                    pitch = 1.15f,
                    speed = 1.10f,
                    phoneticLabel = "Vigoroso de la Montaña",
                    description = "Arcaísmos vigorosos muy vivos de las montañas de Guerrero.",
                    textTransformer = { it.replace("v", "w") }
                )
            }
            name.contains("puebla") -> {
                VariantPronunciationProfile(
                    pitch = 0.90f,
                    speed = 1.00f,
                    phoneticLabel = "Suavidad Poblana",
                    description = "Variante norteña sumamente dulce y fonología fluida.",
                    textTransformer = { it.replace("z", "s") }
                )
            }

            // --- OTOMÍ VARIANTS ---
            name.contains("mezquital") || name.contains("hñähñu") || name.contains("hñahñu") -> {
                VariantPronunciationProfile(
                    pitch = 1.00f,
                    speed = 0.95f,
                    phoneticLabel = "Viento de Mezquital",
                    description = "Giro del Valle semiárido, caracterizado por nasales ricas.",
                    textTransformer = { it.replace("ñ", "ñ'") }
                )
            }
            name.contains("querétaro") || name.contains("ñäñho") || name.contains("ñanho") -> {
                VariantPronunciationProfile(
                    pitch = 1.15f,
                    speed = 1.10f,
                    phoneticLabel = "Acento Lelé Queretano",
                    description = "Cadencia alegre y expresiva de la muñeca sagrada de Amealco.",
                    textTransformer = { it.replace("ñ", "ny") }
                )
            }
            name.contains("ixtenco") || name.contains("yümu") || name.contains("yumu") -> {
                VariantPronunciationProfile(
                    pitch = 0.88f,
                    speed = 0.92f,
                    phoneticLabel = "Yümu de Tlaxcala",
                    description = "Otomí de Ixtenco, sumamente tradicional y preciso.",
                    textTransformer = { it.replace("u", "ü") }
                )
            }

            else -> {
                VariantPronunciationProfile(
                    pitch = 1.00f,
                    speed = 1.00f,
                    phoneticLabel = "Pronunciación Estándar",
                    description = "Variante regional equilibrada con entonación natural.",
                    textTransformer = { it }
                )
            }
        }
    }

    fun speak(text: String, variantOverride: String? = null) {
        if (text.isBlank()) return
        val cleanText = text
            .replace("*", "")
            .replace("_", "")
            .replace("`", "")
            .replace("~", "")
            .replace("(", " ")
            .replace(")", " ")
            .replace("[", " ")
            .replace("]", " ")
            .replace("{", " ")
            .replace("}", " ")
            .replace("<", " ")
            .replace(">", " ")
            .replace("/", " ")
            .replace("\\", " ")
            .replace("|", " ")
            .replace("#", "")
            .replace("\\s+".toRegex(), " ")
            .trim()
        
        val activeVariant = variantOverride ?: run {
            val progress = userProgress.value
            val lang = progress?.selectedLanguage ?: "Nahuatl"
            when (lang) {
                "Totona" -> progress?.selectedTotonaVariant
                "Maya" -> progress?.selectedMayaVariant
                "Zapoteco" -> progress?.selectedZapotecoVariant
                "Mixteco" -> progress?.selectedMixtecoVariant
                "Otomi" -> progress?.selectedOtomiVariant
                else -> progress?.selectedNahuatlVariant
            }
        } ?: "Náhuatl de la Huasteca veracruzana"

        val profile = getVariantPronunciationProfile(activeVariant)
        val transformedText = profile.textTransformer(cleanText)
        currentlySpeakingText = cleanText

        // --- XTTS Remote Voice Synthesis Path ---
        if (xttsConfig.isEnabled && xttsConfig.serverUrl.isNotBlank()) {
            isXttsSynthesizing = true
            viewModelScope.launch {
                val success = xttsClient.synthesizeAndPlay(
                    text = transformedText,
                    config = xttsConfig,
                    onStart = {
                        isSpeaking = true
                        isXttsSynthesizing = false
                    },
                    onCompletion = {
                        isSpeaking = false
                        currentlySpeakingText = null
                    },
                    onError = {
                        isXttsSynthesizing = false
                        // Fallback to local native TTS
                        fallbackNativeSpeak(transformedText, profile)
                    }
                )
                if (!success) {
                    isXttsSynthesizing = false
                    fallbackNativeSpeak(transformedText, profile)
                }
            }
            return
        }

        // --- Fallback / Default Native Android TTS Path ---
        fallbackNativeSpeak(transformedText, profile)
    }

    private fun fallbackNativeSpeak(text: String, profile: VariantPronunciationProfile) {
        if (tts == null || !isTtsReady) {
            initTts()
        }
        try {
            tts?.setPitch(profile.pitch)
            tts?.setSpeechRate(profile.speed)

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, "tts_pronunciation_${System.currentTimeMillis()}")
            } else {
                @Suppress("DEPRECATION")
                tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null)
            }
            isSpeaking = true
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun stopSpeaking() {
        try {
            xttsClient.stopAudio()
            tts?.stop()
            isSpeaking = false
            isXttsSynthesizing = false
            currentlySpeakingText = null
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun saveXttsConfig(newConfig: XttsConfig) {
        xttsConfig = newConfig
        try {
            val prefs = getApplication<Application>().getSharedPreferences("xtts_prefs", android.content.Context.MODE_PRIVATE)
            prefs.edit()
                .putBoolean("is_enabled", newConfig.isEnabled)
                .putString("server_url", newConfig.serverUrl)
                .putString("api_key", newConfig.apiKey)
                .putString("language", newConfig.language)
                .putFloat("speed", newConfig.speed)
                .apply()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun loadXttsConfig() {
        try {
            val prefs = getApplication<Application>().getSharedPreferences("xtts_prefs", android.content.Context.MODE_PRIVATE)
            xttsConfig = XttsConfig(
                isEnabled = prefs.getBoolean("is_enabled", true),
                serverUrl = prefs.getString("server_url", "https://coqui-xtts.hf.space/tts_to_audio") ?: "https://coqui-xtts.hf.space/tts_to_audio",
                apiKey = prefs.getString("api_key", "") ?: "",
                language = prefs.getString("language", "es") ?: "es",
                speed = prefs.getFloat("speed", 0.95f)
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * Synthesizes and plays any given word or phrase with Víctor Florencio's cloned voice.
     * Uses Room database as the local audio cache manager to avoid repeated calls to the XTTS server
     * for previously queried words.
     * If cached in Room and present on disk, plays instantly (0ms).
     * If not cached, requests synthesis from XTTS, saves the audio to disk, and indexes it in Room.
     * If server is offline or sleeping, smoothly falls back to native priest voice.
     */
    fun synthesizeSacerdotePhrase(text: String, onDone: () -> Unit = {}) {
        val cleanText = text.trim()
        if (cleanText.isBlank()) return
        stopSpeaking()
        currentlySpeakingText = cleanText

        viewModelScope.launch {
            // 1. Calculate unique text hash
            val hash = xttsClient.hashString("$cleanText|${xttsConfig.speakerProfile}|${xttsConfig.language}")

            // 2. Check Room Database Cache first to avoid repeated calls!
            val cachedEntry = repository.getCachedSacerdoteAudioByHash(hash)
            if (cachedEntry != null) {
                val audioFile = java.io.File(cachedEntry.filePath)
                if (audioFile.exists() && audioFile.length() > 0) {
                    // Update play count and timestamp in Room
                    repository.recordSacerdoteAudioPlayed(hash)
                    sacerdoteStatusFeedback = "⚡ Reproducido desde caché Room (${cachedEntry.playCount + 1} consultas)"
                    isSpeaking = true
                    xttsClient.playAudioFile(
                        file = audioFile,
                        onStart = {
                            isSpeaking = true
                        },
                        onCompletion = {
                            isSpeaking = false
                            currentlySpeakingText = null
                            onDone()
                        }
                    )
                    return@launch
                }
            }

            // 3. Not in Room cache: synthesize via XTTS server if enabled
            if (xttsConfig.isEnabled && xttsConfig.serverUrl.isNotBlank()) {
                isXttsSynthesizing = true
                sacerdoteStatusFeedback = "Invocando voz de Víctor Florencio..."

                val success = xttsClient.synthesizeAndPlay(
                    text = cleanText,
                    config = xttsConfig,
                    onStart = {
                        isSpeaking = true
                        isXttsSynthesizing = false
                        sacerdoteStatusFeedback = "💾 Guardado en caché Room (consulta completada)"
                    },
                    onCompletion = {
                        isSpeaking = false
                        currentlySpeakingText = null
                        onDone()
                    },
                    onError = { err ->
                        isXttsSynthesizing = false
                        sacerdoteStatusFeedback = err
                        fallbackNativeSacerdoteSpeak(cleanText)
                        onDone()
                    },
                    onAudioCached = { savedFile ->
                        // Automatically record in Room database
                        viewModelScope.launch {
                            repository.saveCachedSacerdoteAudio(
                                textHash = hash,
                                queryText = cleanText,
                                filePath = savedFile.absolutePath,
                                fileSizeBytes = savedFile.length(),
                                audioFormat = "wav"
                            )
                        }
                    }
                )
                if (!success) {
                    isXttsSynthesizing = false
                    fallbackNativeSacerdoteSpeak(cleanText)
                    onDone()
                }
            } else {
                fallbackNativeSacerdoteSpeak(cleanText)
                onDone()
            }
        }
    }

    fun clearSacerdoteAudioCache() {
        viewModelScope.launch {
            try {
                val cacheDir = java.io.File(getApplication<Application>().cacheDir, "xtts_audio")
                if (cacheDir.exists() && cacheDir.isDirectory) {
                    cacheDir.listFiles()?.forEach { it.delete() }
                }
                repository.clearSacerdoteAudioCache()
                sacerdoteStatusFeedback = "Caché de audio en Room vaciado correctamente"
            } catch (e: Exception) {
                sacerdoteStatusFeedback = "Error al limpiar caché: ${e.localizedMessage}"
            }
        }
    }

    private fun fallbackNativeSacerdoteSpeak(text: String) {
        if (tts == null || !isTtsReady) {
            initTts()
        }
        try {
            tts?.setPitch(0.72f) // Deep solemn priest pitch
            tts?.setSpeechRate(0.85f)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, "sacerdote_${System.currentTimeMillis()}")
            } else {
                @Suppress("DEPRECATION")
                tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null)
            }
            isSpeaking = true
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun playSacerdoteVoice(text: String? = null) {
        if (!text.isNullOrBlank()) {
            synthesizeSacerdotePhrase(text)
        } else {
            playSacerdoteVoiceSample()
        }
    }

    fun markIntroStoryAsSeen() {
        viewModelScope.launch {
            val current = userProgress.value ?: repository.getProgress() ?: UserProgress()
            repository.updateProgress(current.copy(hasSeenIntroStory = true))
            currentTab = 0 // Navigate directly to lessons map
        }
    }

    fun playSacerdoteVoiceSample() {
        stopSpeaking()
        xttsClient.playLocalResource(
            rawResId = com.example.R.raw.voz_sacerdote_sample,
            onStart = { isSpeaking = true },
            onCompletion = { isSpeaking = false }
        )
    }

    fun playSacerdoteVoiceFull() {
        stopSpeaking()
        xttsClient.playLocalResource(
            rawResId = com.example.R.raw.voz_sacerdote_victor,
            onStart = { isSpeaking = true },
            onCompletion = { isSpeaking = false }
        )
    }

    override fun onCleared() {
        super.onCleared()
        try {
            xttsClient.stopAudio()
            tts?.stop()
            tts?.shutdown()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    fun startPetRoaming() {
        viewModelScope.launch {
            while (true) {
                kotlinx.coroutines.delay(2500)
                val progress = repository.getProgress()
                if (progress != null) {
                    val pet = progress.petType
                    if (pet.isNotEmpty() && !progress.isPetDead) {
                        when (pet) {
                            "Guajolote" -> {
                                // Walks slowly on ground
                                petOffsetX = (-100..100).random().toFloat()
                                petOffsetY = 40f + (-5..5).random().toFloat()
                                petRotationState = if (petOffsetX < 0) -8f else 8f
                            }
                            "Xoloitzcuintli" -> {
                                // Energetic run/jumping bursts
                                petOffsetX = (-120..120).random().toFloat()
                                petOffsetY = 30f + (-12..12).random().toFloat()
                                petRotationState = if (petRotationState == 0f) 12f else 0f
                            }
                            "Águila" -> {
                                // Flies high in wide smooth waves
                                petOffsetX = (-120..120).random().toFloat()
                                petOffsetY = (-80..-20).random().toFloat()
                                petRotationState = (-18..18).random().toFloat()
                            }
                            "Jaguar" -> {
                                // Stealthy prowling and sudden dashes
                                petOffsetX = (-110..110).random().toFloat()
                                petOffsetY = 50f + (-6..6).random().toFloat()
                                petRotationState = if (petOffsetX > 50) 10f else -10f
                            }
                        }
                    }
                }
            }
        }
    }

    // --- State Flows from Room ---
    val userProgress: StateFlow<UserProgress?> = repository.progressFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val allLessons: StateFlow<List<LessonEntity>> = repository.allLessonsFlow
        .combine(repository.progressFlow) { lessons, progress ->
            val lang = progress?.selectedLanguage ?: "Nahuatl"
            val completedSet = when (lang) {
                "Totona" -> progress?.completedLessonsTotona.orEmpty().split(";").mapNotNull { it.toIntOrNull() }.toSet()
                else -> progress?.completedLessonsNahuatl.orEmpty().split(";").mapNotNull { it.toIntOrNull() }.toSet()
            }
            lessons.map { originalLesson ->
                val adapted = adaptLessonForLanguage(originalLesson, lang)
                adapted.copy(isCompleted = completedSet.contains(originalLesson.id))
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allWords: StateFlow<List<WordEntity>> = repository.allWordsFlow
        .combine(repository.progressFlow) { words, progress ->
            val lang = progress?.selectedLanguage ?: "Nahuatl"
            if (lang == "Nahuatl") {
                words
            } else {
                words.map { adaptWordForLanguage(it, lang) }
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun selectLanguage(language: String) {
        viewModelScope.launch {
            val progress = repository.getProgress() ?: UserProgress()
            repository.updateProgress(progress.copy(selectedLanguage = language))
        }
    }

    fun selectNahuatlVariant(variant: String) {
        viewModelScope.launch {
            val progress = repository.getProgress() ?: UserProgress()
            repository.updateProgress(progress.copy(selectedNahuatlVariant = variant))
        }
    }

    fun selectTotonaVariant(variant: String) {
        viewModelScope.launch {
            val progress = repository.getProgress() ?: UserProgress()
            repository.updateProgress(progress.copy(selectedTotonaVariant = variant))
        }
    }

    fun selectMayaVariant(variant: String) {
        viewModelScope.launch {
            val progress = repository.getProgress() ?: UserProgress()
            repository.updateProgress(progress.copy(selectedMayaVariant = variant))
        }
    }

    fun selectZapotecoVariant(variant: String) {
        viewModelScope.launch {
            val progress = repository.getProgress() ?: UserProgress()
            repository.updateProgress(progress.copy(selectedZapotecoVariant = variant))
        }
    }

    fun selectMixtecoVariant(variant: String) {
        viewModelScope.launch {
            val progress = repository.getProgress() ?: UserProgress()
            repository.updateProgress(progress.copy(selectedMixtecoVariant = variant))
        }
    }

    fun selectOtomiVariant(variant: String) {
        viewModelScope.launch {
            val progress = repository.getProgress() ?: UserProgress()
            repository.updateProgress(progress.copy(selectedOtomiVariant = variant))
        }
    }

    fun adaptLessonForLanguage(lesson: LessonEntity, language: String): LessonEntity {
        if (language == "Nahuatl") return lesson
        
        val (langName, demoPlace) = when (language) {
            "Totona" -> Pair("Totonaco", "El Tajín")
            else -> Pair("Náhuatl", "Tenochtitlan")
        }

        val newTitle = lesson.title
            .replace("Nahuatl", langName, ignoreCase = true)
            .replace("Náhuatl", langName, ignoreCase = true)
            .replace("azteca", langName.lowercase(), ignoreCase = true)
            .replace("Aztecas", langName + "s", ignoreCase = true)
            .replace("Tenochtitlan", demoPlace, ignoreCase = true)
            .replace("Mesoamericana", langName, ignoreCase = true)
            
        val newExplanation = lesson.explanation
            .replace("Nahuatl", langName, ignoreCase = true)
            .replace("Náhuatl", langName, ignoreCase = true)
            .replace("azteca", langName.lowercase(), ignoreCase = true)
            .replace("Aztecas", langName + "s", ignoreCase = true)
            .replace("Tenochtitlan", demoPlace, ignoreCase = true)
            .replace("Mesoamericana", langName, ignoreCase = true)

        val newDidYouKnow = lesson.didYouKnow
            .replace("Nahuatl", langName, ignoreCase = true)
            .replace("Náhuatl", langName, ignoreCase = true)
            .replace("azteca", langName.lowercase(), ignoreCase = true)
            .replace("Aztecas", langName + "s", ignoreCase = true)
            .replace("Tenochtitlan", demoPlace, ignoreCase = true)
            .replace("Mesoamericana", langName, ignoreCase = true)

        val newAiSummary = lesson.aiSummary
            .replace("Nahuatl", langName, ignoreCase = true)
            .replace("Náhuatl", langName, ignoreCase = true)
            .replace("azteca", langName.lowercase(), ignoreCase = true)
            .replace("Aztecas", langName + "s", ignoreCase = true)
            .replace("Tenochtitlan", demoPlace, ignoreCase = true)
            .replace("Mesoamericana", langName, ignoreCase = true)

        val phrases = lesson.nahuatlPhrases.split(";")
        val translations = lesson.spanishTranslations.split(";")

        val adaptedPhrases = phrases.mapIndexed { index, phrase ->
            val translation = translations.getOrNull(index) ?: ""
            getTranslatedPhraseForLanguage(phrase, translation, language, lesson.id, index)
        }

        return lesson.copy(
            title = newTitle,
            explanation = newExplanation,
            didYouKnow = newDidYouKnow,
            aiSummary = newAiSummary,
            nahuatlPhrases = adaptedPhrases.joinToString(";")
        )
    }

    data class Quadruple<out A, out B, out C, out D>(
        val first: A,
        val second: B,
        val third: C,
        val fourth: D
    )

    fun adaptWordForLanguage(word: WordEntity, language: String): WordEntity {
        if (language == "Nahuatl") return word
        
        if (language == "Totona") {
            val cleanNahuatl = word.nahuatl.trim().lowercase()
            val (totonaWord, totonaPronun, totonaSentence, totonaSentenceSpanish) = when (cleanNahuatl) {
                "niltze" -> Quadruple("Tlan tlan", "Tlahn tlahn", "Tlan tlan, ¿tlan tila?", "Hola, ¿cómo estás?")
                "tlazohcamati" -> Quadruple("Paxcatgatzilh", "Pash-kaht-gah-tseelh", "Paxcatgatzilh kili'akxka.", "Gracias de mi parte.")
                "cualli tonalli" -> Quadruple("Tlan katsatzi", "Tlahn kah-tsah-tsee", "Tlan katsatzi kisk'ata.", "Buenos días, mi niño.")
                "queniuhqui ticate?" -> Quadruple("¿Tlan tila?", "Tlahn tee-lah", "¿Tlan tila lamá?", "¿Cómo estás allí?")
                "noyolotl" -> Quadruple("Ki naku", "Kee nah-koo", "Ki naku, kutzun di ne'i.", "Mi corazón, quédate tranquilo.")
                "ce" -> Quadruple("Tum", "Toom", "Tum sk'ata kutzun.", "Un niño corre.")
                "ome" -> Quadruple("Tuy", "Tooy", "Tuy misto thuhu.", "Dos gatos chillan.")
                "eyi" -> Quadruple("Tutu", "Too-too", "Tutu ts'umu'ut pat'i.", "Tres mariposas vuelan.")
                "nahui" -> Quadruple("Tati", "Tah-tee", "Tati staku mocahua.", "Cuatro estrellas quedan.")
                "macuilli" -> Quadruple("Tutsun", "Toot-soohn", "Tutsun chichini rayo.", "Cinco rayos de sol.")
                "ocelotl" -> Quadruple("Misin", "Mee-seen", "Misin tlacua cuauhtla.", "El jaguar come en la selva.")
                "coatl" -> Quadruple("Luwa", "Loo-wah", "Luwa xanat nemilitztli.", "La serpiente de vainilla es vida.")
                "cuauhtli" -> Quadruple("Xa'lh", "Shahlh", "Xa'lh pat'i ilhuicac.", "El águila vuela en el cielo.")
                "tochtli" -> Quadruple("Chiya", "Chee-yah", "Chiya tlacua papak.", "El conejo come bajo la luz de la luna.")
                "papalotl" -> Quadruple("Ts'umu'ut", "Tsoo-moo-oot", "Ts'umu'ut quitemoa xtahuat.", "La mariposa busca una flor.")
                "tonatiuh" -> Quadruple("Chichini", "Chee-chee-nee", "Chichini tlanextia iyoliz.", "El sol alumbra la vida.")
                "metztli" -> Quadruple("Papak", "Pah-pahk", "Papak tlahvia yohualnepantla.", "La luna ilumina a medianoche.")
                "atl" -> Quadruple("Chuchut", "Choo-choot", "Chuchut chicahuac ihcac.", "El agua corre abundante.")
                "tletl" -> Quadruple("Lhkuyat", "L'koo-yaht", "Lhkuyat tlatia tlanextiliztli.", "El fuego quema brillante.")
                "citlalin" -> Quadruple("Staku", "Stah-koo", "Staku tlanextia ilhuicac.", "La estrella brilla en el cielo.")
                else -> Quadruple(
                    getTranslatedPhraseForLanguage(word.nahuatl, word.spanish, "Totona", word.id, 99),
                    "Fonetica Totonaco",
                    getTranslatedPhraseForLanguage(word.sentenceNahuatl, word.sentenceSpanish, "Totona", word.id, 101),
                    word.sentenceSpanish
                )
            }
            return word.copy(
                nahuatl = totonaWord,
                pronunciation = totonaPronun,
                sentenceNahuatl = totonaSentence,
                sentenceSpanish = totonaSentenceSpanish
            )
        }
        
        val newNahuatl = getTranslatedPhraseForLanguage(word.nahuatl, word.spanish, language, word.id, 99)
        val newPronunciation = when (language) {
            "Totona" -> "Fonetica Totonaco"
            else -> word.pronunciation
        }
        val newSentenceNahuatl = getTranslatedPhraseForLanguage(word.sentenceNahuatl, word.sentenceSpanish, language, word.id, 101)
        
        return word.copy(
            nahuatl = newNahuatl,
            pronunciation = newPronunciation,
            sentenceNahuatl = newSentenceNahuatl
        )
    }

    fun getTranslatedPhraseForLanguage(originalPhrase: String, translation: String, language: String, lessonId: Int, index: Int): String {
        var res = originalPhrase.lowercase()
        
        val mappings = when (language) {
            "Totona" -> mapOf(
                "niltze" to "tlan tlan",
                "cualli tonalli" to "tlan katsatzi",
                "cualli yohualli" to "tlan tsisni",
                "tlazohcamati" to "paxcatgatzilh",
                "queniuhqui ticate?" to "¿tlan tila?",
                "queniuhqui ticate" to "tlan tila",
                "queniuhqui" to "tlan",
                "noyolotl" to "ki naku",
                "noitlah noyolotl" to "tlan ki naku",
                "noitlah" to "tlan",
                "queniuhqui motoca?" to "¿tuchi wan mawan?",
                "queniuhqui motoca" to "tuchi wan mawan",
                "notoca" to "nkiwan",
                "canin tichanti?" to "¿nila lama?",
                "canin tichanti" to "nila lama",
                "notzo, tlazohcamati" to "tlan kakuanza, paxcatgatzilh",
                "notzo" to "tlan kakuanza",
                "ce" to "tum",
                "ome" to "tuy",
                "eyi" to "tutu",
                "nahui" to "tati",
                "macuilli" to "tutsun",
                "ocelotl" to "misin",
                "coatl" to "luwa",
                "cuauhtli" to "xa'lh",
                "tochtli" to "chiya",
                "papalotl" to "ts'umu'ut",
                "tonatiuh" to "chichini",
                "metztli" to "papak",
                "atl" to "chuchut",
                "tletl" to "lhkuyat",
                "citlalin" to "staku"
            )
            else -> emptyMap()
        }

        val cleanFull = originalPhrase.replace(",", "").replace(".", "").replace("?", "").replace("¿", "").lowercase().trim()
        val directReplacement = mappings[cleanFull]
        if (directReplacement != null) {
            var finalPhrase = directReplacement
            if (originalPhrase.firstOrNull()?.isUpperCase() == true) {
                finalPhrase = directReplacement.replaceFirstChar { it.uppercase() }
            }
            val hasQuestion = originalPhrase.contains("?")
            if (hasQuestion && !finalPhrase.endsWith("?")) {
                finalPhrase = if (finalPhrase.startsWith("¿")) finalPhrase + "?" else "¿" + finalPhrase + "?"
            }
            return finalPhrase
        }

        val words = originalPhrase.split(" ")
        val mappedWords = words.map { word ->
            val cleanWord = word.replace(",", "").replace(".", "").replace("?", "").replace("¿", "").lowercase().trim()
            val replacement = mappings[cleanWord]
            if (replacement != null) {
                var finalRep = replacement
                if (word.firstOrNull()?.isUpperCase() == true) {
                    finalRep = replacement.replaceFirstChar { it.uppercase() }
                }
                word.replace(cleanWord, finalRep, ignoreCase = true)
            } else {
                word
            }
        }
        
        var resultPhrase = mappedWords.joinToString(" ")
        if (resultPhrase == originalPhrase.lowercase()) {
            val syllables = when (language) {
                "Totona" -> listOf("tla", "ti", "la", "sta", "pa", "na", "lu", "mi", "pax", "cat", "ga", "chu", "chut", "ku", "chi", "ni")
                else -> listOf("nah", "tla", "co", "me", "ce", "yo", "cu", "pa", "mi", "zo", "ca", "an", "te", "to", "ix")
            }
            val r = kotlin.random.Random(lessonId * 31L + index)
            val phraseLen = r.nextInt(3, 6)
            val generatedWords = (1..phraseLen).map { wordIdx ->
                val r2 = kotlin.random.Random(lessonId * 17L + index + wordIdx)
                val wordLen = r2.nextInt(2, 4)
                val syls = (1..wordLen).map { sylIdx ->
                    syllables[(lessonId * 7 + index * 3 + wordIdx * 5 + sylIdx) % syllables.size]
                }
                var gWord = syls.joinToString("")
                if (wordIdx == 1) {
                    gWord = gWord.replaceFirstChar { it.uppercase() }
                }
                gWord
            }
            resultPhrase = generatedWords.joinToString(" ")
        }
        
        return resultPhrase
    }

    val weeklyChallenges: StateFlow<List<WeeklyChallenge>> = repository.allChallengesFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())


    // --- UI State Variables ---
    var currentTab by mutableStateOf(0) // 0: Lecciones, 1: Traductor, 2: Juegos, 3: Progreso

    // --- Interactive Mascot States ---
    var inventoryMaizCount by mutableStateOf(2)
    var inventoryTamalCount by mutableStateOf(1)
    var inventoryCacaoCount by mutableStateOf(0)
    
    var petBounceState by mutableStateOf(false)
    var petRotationState by mutableStateOf(0f)
    var petSpeechBubble by mutableStateOf("¡Ma-cualli tonalli! Bienvenido al Templo Sagrado.")
    var activeSacrificeCeremony by mutableStateOf<String?>(null)
    var activeEquippedAccessories by mutableStateOf<Set<String>>(emptySet())

    // Feeding & Accessory & Sacrifice animations
    var feedingFoodItem by mutableStateOf<String?>(null)
    var isFeedingAnimating by mutableStateOf(false)
    var sacrificeAnimationStep by mutableStateOf(0) // 0=idle, 1=prep, 2=strike, 3=ascension/carried, 4=complete/blessed
    var petAccessoryEquippedFlash by mutableStateOf<String?>(null)

    // Biometric security states
    var isAppUnlocked by mutableStateOf(false)
    var isCheckingBiometric by mutableStateOf(false)
    var biometricFeedbackMessage by mutableStateOf("Toca el sensor azteca para ingresar")

    // AI Game influence states
    var aiGameHintText by mutableStateOf<String?>(null)
    var isRequestingAiGameHint by mutableStateOf(false)

    // 1. Lessons Tab State
    var selectedUnit by mutableStateOf(1) // Unit idx 1 to 10
    var activeLesson by mutableStateOf<LessonEntity?>(null)
    var lockedLessonAlertId by mutableStateOf<Int?>(null)

    // Lesson verification quiz states
    var lessonQuizActive by mutableStateOf(false)
    var lessonQuizQuestion by mutableStateOf("")
    var lessonQuizOptions by mutableStateOf<List<String>>(emptyList())
    var lessonQuizCorrectIndex by mutableStateOf(0)
    var selectedLessonQuizOptionIndex by mutableStateOf<Int?>(null)
    var isLessonQuizVerified by mutableStateOf(false)
    var isLessonQuizCorrect by mutableStateOf(false)
    var lessonQuizFeedback by mutableStateOf("")
    var lessonQuizAiHint by mutableStateOf<String?>(null)
    var isRequestingLessonQuizAiHint by mutableStateOf(false)

    // Dedicated Aztec Multi-Stage Ritual Challenge states
    var lessonRitualActive by mutableStateOf(false)
    var ritualCurrentStep by mutableStateOf(1) // 1, 2, 3
    var ritualTotalSteps = 3
    
    // Step 1: Multiple choice (El Enigma del Sacerdote)
    var ritualQ1Question by mutableStateOf("")
    var ritualQ1Options by mutableStateOf<List<String>>(emptyList())
    var ritualQ1CorrectIndex by mutableStateOf(0)
    var ritualQ1SelectedIndex by mutableStateOf<Int?>(null)
    var ritualQ1Feedback by mutableStateOf("")
    var ritualQ1Verified by mutableStateOf(false)
    
    // Step 2: Sentence reconstruction (Glifos Desordenados)
    var ritualQ2TargetPhrase by mutableStateOf("") // The correct Nahuatl phrase
    var ritualQ2SpanishPrompt by mutableStateOf("") // The Spanish meaning
    var ritualQ2ShuffledWords by mutableStateOf<List<String>>(emptyList()) // Shuffled word tokens
    var ritualQ2SelectedWords by mutableStateOf<List<String>>(emptyList()) // The user's selection
    var ritualQ2Verified by mutableStateOf(false)
    var ritualQ2Feedback by mutableStateOf("")
    var ritualQ2IsCorrect by mutableStateOf(false)
    
    // Step 3: True / False (El Oráculo del Destino)
    var ritualQ3NahuatlWord by mutableStateOf("")
    var ritualQ3SpanishClaim by mutableStateOf("")
    var ritualQ3IsClaimTrue by mutableStateOf(false) // Whether the claim is correct
    var ritualQThirdSelectedIndex by mutableStateOf<Boolean?>(null) // null, true, false
    var ritualQ3Verified by mutableStateOf(false)
    var ritualQ3Feedback by mutableStateOf("")
    var ritualQ3IsCorrect by mutableStateOf(false)

    // Complete Ritual Status
    var ritualCompleteSuccess by mutableStateOf(false)

    // Mascot and Hearts (Lives) System
    var petSuccessMessage by mutableStateOf<String?>(null)
    var petErrorMessage by mutableStateOf<String?>(null)
    var shieldSavedTrigger by mutableStateOf(false)
    var showOutOfLivesDialog by mutableStateOf(false)

    // 2. Translator Tab State
    var translatorInputText by mutableStateOf("")
    var dictionarySearchResults by mutableStateOf<List<WordEntity>>(emptyList())
    var isAiTranslating by mutableStateOf(false)
    var latestAiTranslationResult by mutableStateOf<NahuatlTranslationResult?>(null)

    // 3. Mini Games - Memorama State
    var memoramaCards by mutableStateOf<List<MemoryCard>>(emptyList())
    var firstSelectedCardIndex by mutableStateOf<Int?>(null)
    var secondSelectedCardIndex by mutableStateOf<Int?>(null)
    var memoramaScore by mutableStateOf(0)
    var memoramaMovesCount by mutableStateOf(0)
    var memoramaGameWon by mutableStateOf(false)

    // 4. Mini Games - Word Quiz State
    var activeQuizQuestions by mutableStateOf<List<QuizQuestion>>(emptyList())
    val quizQuestions: List<QuizQuestion>
        get() = if (activeQuizQuestions.isNotEmpty()) activeQuizQuestions else getDefaultQuizQuestions()

    fun getDefaultQuizQuestions(): List<QuizQuestion> {
        return getQuizQuestionsBankForLanguage(userProgress.value?.selectedLanguage ?: "Nahuatl", 1).take(5)
    }

    fun getQuizQuestionsBankForLanguage(lang: String, levelId: Int): List<QuizQuestion> {
        return when (lang) {
            "Totona" -> listOf(
                QuizQuestion("¿Qué significa el saludo totonaco 'Tlan tlan' en español?", listOf("Gracias", "Hola", "Adiós", "Te amo"), "Hola", "Tlan tlan es el saludo de bienvenida y cordialidad totonaca."),
                QuizQuestion("¿Cómo se traduce al totonaco la palabra 'Serpiente'?", listOf("Misin", "Xa'lh", "Luwa", "Chiya"), "Luwa", "Luwa significa serpiente en totonaco."),
                QuizQuestion("¿Cómo agradeces en totonaco?", listOf("Paxcatgatzilh", "Tlan tlan", "Tlan katsatzi", "Ki naku"), "Paxcatgatzilh", "Paxcatgatzilh es el término reverencial para dar las gracias."),
                QuizQuestion("¿Qué astro representa 'Chichini' en la cosmovisión totonaca?", listOf("La Luna", "El Sol", "La Tierra", "El Fuego"), "El Sol", "Chichini es el Sol, central en El Tajín."),
                QuizQuestion("¿Cómo se expresa 'Buenos días' en totonaco?", listOf("Tlan yohualli", "Tlan tlan", "¿Tlan tila?", "Tlan katsatzi"), "Tlan katsatzi", "Tlan katsatzi es el saludo matutino totonaca."),
                QuizQuestion("¿Qué animal sagrado es 'Misin' en totonaco?", listOf("Jaguar", "Águila", "Conejo", "Venado"), "Jaguar", "Misin representa al felino sagrado de la selva."),
                QuizQuestion("¿Qué elemento natural es 'Chuchut'?", listOf("Agua", "Fuego", "Tierra", "Viento"), "Agua", "Chuchut es la corriente de agua vital."),
                QuizQuestion("¿Qué ciudad ancestral totonaca albergó la Pirámide de los Nichos?", listOf("El Tajín", "Teotihuacan", "Chichén Itzá", "Monte Albán"), "El Tajín", "El Tajín es la gran capital ceremonial totonaca."),
                QuizQuestion("¿Qué significa 'Xanat' en totonaco?", listOf("Flor de Vainilla", "Maíz", "Miel", "Sol"), "Flor de Vainilla", "Xanat es la orquídea nativa de la vainilla de Papantla."),
                QuizQuestion("¿Qué ave majestuosa es 'Xa'lh'?", listOf("Águila", "Guacamaya", "Colibrí", "Búho"), "Águila", "Xa'lh es el águila real guardiana de las montañas.")
            )
            "Otomi" -> listOf(
                QuizQuestion("¿Cómo se dice 'Hola' en Otomí?", listOf("Ki hats'i", "Di jamädi", "Dehe", "Zat'i"), "Ki hats'i", "Ki hats'i es el saludo ceremonial otomí."),
                QuizQuestion("¿Qué significa 'Di jamädi'?", listOf("Gracias", "Buenos días", "Adiós", "Agua"), "Gracias", "Di jamädi expresa profunda gratitud."),
                QuizQuestion("¿Cómo se dice 'Agua' en Otomí?", listOf("Dehe", "Sipi", "Hyadi", "Ngü"), "Dehe", "Dehe representa el agua de los manantiales."),
                QuizQuestion("¿Qué astro es 'Hyadi' en la tradición otomí?", listOf("Sol", "Luna", "Estrella", "Nube"), "Sol", "Hyadi es el astro solar."),
                QuizQuestion("¿Qué felino sagrado es 'Zat'i'?", listOf("Jaguar", "Puma", "Gato", "Lobo"), "Zat'i", "Zat'i representa al jaguar en la tradición otomí."),
                QuizQuestion("¿Qué astro nocturno es 'Zänä'?", listOf("Luna", "Sol", "Estrella", "Viento"), "Luna", "Zänä ilumina la noche en la cosmogonía otomí.")
            )
            "Maya" -> listOf(
                QuizQuestion("¿Qué significa 'Ba'ax ka wa'alik'?", listOf("Hola / ¿Qué dices?", "Gracias", "Buenos días", "Adiós"), "Hola / ¿Qué dices?", "Es el saludo habitual maya peninsular."),
                QuizQuestion("¿Cómo se dice 'Gracias' en Maya?", listOf("Dios bo'otik", "Ma'alob k'iin", "Balam", "Kan"), "Dios bo'otik", "Dios bo'otik expresa gratitud y bendición."),
                QuizQuestion("¿Qué animal guardián del monte es 'Balam'?", listOf("Jaguar", "Serpiente", "Águila", "Venado"), "Balam", "Balam es el felino guardián de la selva maya."),
                QuizQuestion("¿Qué astro es 'K'iin'?", listOf("Sol / Día", "Luna", "Estrella", "Cielo"), "Sol / Día", "K'iin es el Sol y la medición del tiempo."),
                QuizQuestion("¿Qué reptil sagrado es 'Kan'?", listOf("Serpiente", "Iguana", "Cocodrilo", "Tortuga"), "Kan", "Kan es la serpiente universal."),
                QuizQuestion("¿Qué árbol sagrado une el inframundo con los cielos?", listOf("Yaxché (Ceiba)", "Caoba", "Cedro", "Pino"), "Yaxché (Ceiba)", "El Yaxché es el árbol cósmico de la vida maya.")
            )
            else -> listOf(
                QuizQuestion("¿Qué significa el saludo náhuatl 'Niltze' en español?", listOf("Gracias", "Hola", "Adiós", "Te amo"), "Hola", "Niltze es el saludo amistoso y general en náhuatl."),
                QuizQuestion("¿Cómo se traduce la palabra 'Serpiente' en náhuatl?", listOf("Ocelotl", "Cuauhtli", "Coatl", "Tochtli"), "Coatl", "Coatl significa serpiente."),
                QuizQuestion("¿Cómo agradeces diciendo 'Muchas Gracias' en náhuatl?", listOf("Tlazohcamati", "Niltze", "Cualli tonalli", "Noyolotl"), "Tlazohcamati", "Tlazohcamati proviene de tlazotli (apreciado/amado)."),
                QuizQuestion("¿Qué astro representa 'Tonatiuh' en la cosmovisión náhuatl?", listOf("La Luna", "El Sol", "La Tierra", "El Fuego"), "El Sol", "Tonatiuh es el Sol dador de vida."),
                QuizQuestion("¿Cómo se expresa 'Buenos días' en náhuatl clásico?", listOf("Cualli yohualli", "Niltze", "Queniuhqui ticate?", "Cualli tonalli"), "Cualli tonalli", "Cualli tonalli significa buen día o buen sol."),
                QuizQuestion("¿Qué ave majestuosa representa 'Cuauhtli'?", listOf("Águila", "Quetzal", "Colibrí", "Guacamaya"), "Águila", "Cuauhtli representa al ave guerrera solar."),
                QuizQuestion("¿Qué felino sagrado es 'Ocelotl'?", listOf("Jaguar", "Puma", "Ocelote", "Gato silvestre"), "Jaguar", "Ocelotl es el jaguar nocturno guardián de los templos."),
                QuizQuestion("¿Qué flor ceremonial se usaba en el Mictlán?", listOf("Cempoalxóchitl", "Yoloxóchitl", "Izquixóchitl", "Acapatli"), "Cempoalxóchitl", "Cempoalxóchitl (20 flores) guía a las almas con su aroma."),
                QuizQuestion("¿Qué es un 'Amoxtli' en la tradición prehispánica?", listOf("Códice / Libro sagrado", "Tambor", "Flauta", "Escudo"), "Códice / Libro sagrado", "Amoxtli es el libro sagrado de pictogramas."),
                QuizQuestion("¿Qué gran metrópoli fue fundada en el lago de Texcoco?", listOf("Tenochtitlan", "Teotihuacan", "Tula", "Cholula"), "Tenochtitlan", "Tenochtitlan fue la gran capital mexica fundada en 1325.")
            )
        }
    }
    var currentQuizIndex by mutableStateOf(0)
    var selectedQuizOption by mutableStateOf<String?>(null)
    var quizScore by mutableStateOf(0)
    var showQuizExplanation by mutableStateOf(false)
    var quizCompleted by mutableStateOf(false)

    // 5. Weekly notification settings (triggered and saved locally)
    var alertsEnabled by mutableStateOf(true)
    var reminderTimeString by mutableStateOf("08:00 PM")
    var hasFiredPracticeNotificationForToday by mutableStateOf(false)


    // --- Core Operations & Handlers ---

    // Search offline Dictionary
    fun performDictionarySearch(query: String) {
        translatorInputText = query
        viewModelScope.launch {
            if (query.isBlank()) {
                dictionarySearchResults = emptyList()
            } else {
                dictionarySearchResults = repository.searchWords(query)
                // Log search in Progress / challenges
                // Challenge ID 2 (vocab search status)
                repository.incrementChallengeProgress(id = 2, amount = 1)
            }
        }
    }

    // Bidirectional translator via Gemini API
    fun runAiSmartTranslation() {
        val query = translatorInputText.trim()
        if (query.isBlank()) return

        viewModelScope.launch {
            isAiTranslating = true
            latestAiTranslationResult = null
            
            val lang = userProgress.value?.selectedLanguage ?: "Nahuatl"
            val result = GeminiTranslationHelper.translateText(query, lang)
            latestAiTranslationResult = result
            isAiTranslating = false

            if (result.success) {
                repository.logTranslation()
            }
        }
    }

    // Toggle lesson completion state
    fun toggleLessonStatus(lessonId: Int, isCompleted: Boolean) {
        viewModelScope.launch {
            repository.updateLessonCompletion(lessonId, isCompleted)
            // Update active lesson state if open
            if (activeLesson?.id == lessonId) {
                activeLesson = activeLesson?.copy(isCompleted = isCompleted)
            }
            if (isCompleted) {
                val stats = repository.recordDailyInteraction(interactionType = "LESSON_COMPLETED", xpEarned = 25)
                _streakStats.value = stats
            }
        }
    }


    fun deductXpForUnlock(amount: Int) {
        viewModelScope.launch {
            repository.addXp(-amount)
        }
    }

    fun getRandomDistractors(correctAnswer: String, count: Int): List<String> {
        val allPhrases = allLessons.value.flatMap { lesson ->
            lesson.spanishTranslations.split(";").map { it.trim() }
        }.filter { it.isNotEmpty() && it != correctAnswer }.distinct()
        
        return if (allPhrases.size >= count) {
            allPhrases.shuffled().take(count)
        } else {
            val fallback = listOf(
                "La luz del sol ilumina la montaña.",
                "Los niños juegan cerca del río sagrado.",
                "El viento sopla fuerte desde el norte.",
                "Ofrecemos cacao y maíz en el altar.",
                "La luna llena brilla sobre el lago.",
                "El tejón corre por el campo.",
                "La flor del girasol busca el calor.",
                "El guerrero viste plumas de quetzal.",
                "Los abuelos relatan historias antiguas.",
                "El río lleva agua fresca al valle."
            ).filter { it != correctAnswer }
            fallback.shuffled().take(count.coerceAtMost(fallback.size))
        }
    }

    fun openLesson(lesson: LessonEntity) {
        val currentLives = userProgress.value?.lives ?: 5
        if (currentLives <= 0) {
            showOutOfLivesDialog = true
            return
        }
        activeLesson = lesson
        updateLastActiveLesson(lesson.id)
        
        // Reset quiz state
        selectedLessonQuizOptionIndex = null
        isLessonQuizVerified = false
        isLessonQuizCorrect = false
        lessonQuizFeedback = ""
        lessonQuizAiHint = null
        isRequestingLessonQuizAiHint = false
        lessonQuizActive = false
        
        val nahuatls = lesson.nahuatlPhrases.split(";").map { it.trim() }.filter { it.isNotEmpty() }
        val spans = lesson.spanishTranslations.split(";").map { it.trim() }.filter { it.isNotEmpty() }
        
        if (nahuatls.isNotEmpty() && spans.isNotEmpty()) {
            val qIdx = (0 until nahuatls.size.coerceAtMost(spans.size)).random()
            val questionPhrase = nahuatls[qIdx]
            val correctAnswer = spans[qIdx]
            
            lessonQuizQuestion = "¿Cuál es la traducción correcta al español de la frase: '**$questionPhrase**'?"
            
            val optionsSet = mutableSetOf<String>()
            optionsSet.add(correctAnswer)
            
            // Add other options from the same lesson
            for (span in spans) {
                if (span != correctAnswer && optionsSet.size < 3) {
                    optionsSet.add(span)
                }
            }
            
            // Generate completely dynamic and unique distractors from other lessons
            if (optionsSet.size < 3) {
                val needed = 3 - optionsSet.size
                val distractors = getRandomDistractors(correctAnswer, needed)
                optionsSet.addAll(distractors)
            }
            
            val shuffledOptions = optionsSet.toList().shuffled()
            lessonQuizOptions = shuffledOptions
            lessonQuizCorrectIndex = shuffledOptions.indexOf(correctAnswer)
            lessonQuizActive = true
        } else {
            // Fallback default
            lessonQuizQuestion = "¿Qué significa el término sagrado 'Tletl'?"
            lessonQuizOptions = listOf("Agua", "Fuego", "Tierra")
            lessonQuizCorrectIndex = 1
            lessonQuizActive = true
        }
    }

    fun verifyLessonQuizAnswer() {
        val selectedIdx = selectedLessonQuizOptionIndex
        if (selectedIdx == null) {
            lessonQuizFeedback = "Selecciona una opción para verificar."
            return
        }
        
        isLessonQuizVerified = true
        if (selectedIdx == lessonQuizCorrectIndex) {
            isLessonQuizCorrect = true
            lessonQuizFeedback = "¡Correcto! Has descifrado la sabiduría sagrada de este vocablo de manera impecable. ¡Reto verificado con éxito!"
            
            // Auto complete lesson
            val currentActive = activeLesson
            if (currentActive != null && !currentActive.isCompleted) {
                toggleLessonStatus(currentActive.id, true)
            }
        } else {
            isLessonQuizCorrect = false
            lessonQuizFeedback = "Respuesta incorrecta. El Sacerdote te recomienda estudiar las frases y la explicación antes de volver a intentar."
            deductLifeAndSave()
        }
    }

    fun requestLessonQuizAiHint() {
        val lesson = activeLesson ?: return
        if (isRequestingLessonQuizAiHint) return
        isRequestingLessonQuizAiHint = true
        lessonQuizAiHint = "Consultando con las estrellas..."
        
        viewModelScope.launch {
            try {
                val correctAnswer = lessonQuizOptions.getOrNull(lessonQuizCorrectIndex) ?: ""
                val lang = userProgress.value?.selectedLanguage ?: "Nahuatl"
                val hint = GeminiTranslationHelper.getLessonQuizHint(lessonQuizQuestion, lessonQuizOptions, correctAnswer, lang)
                lessonQuizAiHint = hint
            } catch (e: Exception) {
                val lang = userProgress.value?.selectedLanguage ?: "Nahuatl"
                lessonQuizAiHint = if (lang == "Otomi") {
                    "El sabio otomí susurra: Concéntrate en la armonía del otomí y en los tonos de esta lección para encontrar la respuesta."
                } else if (lang == "Totona") {
                    "El abuelo totonaca susurra: Respira el aroma de la vainilla y busca la respuesta en la sabiduría de las estrellas de El Tajín."
                } else {
                    "El Sacerdote susurra: Concéntrate en la raíz de la palabra y el contexto de la lección para encontrar el camino."
                }
            } finally {
                isRequestingLessonQuizAiHint = false
            }
        }
    }

    // --- NEW DEDICATED AZTEC MULTI-STAGE CHALLENGE RITUAL ---
    fun startLessonChallengeRitual(lesson: LessonEntity) {
        ritualCurrentStep = 1
        ritualCompleteSuccess = false
        
        // --- STEP 1: El Enigma del Sacerdote (Multiple-choice) ---
        val nahuatls = lesson.nahuatlPhrases.split(";").map { it.trim() }.filter { it.isNotEmpty() }
        val spans = lesson.spanishTranslations.split(";").map { it.trim() }.filter { it.isNotEmpty() }
        
        if (nahuatls.isNotEmpty() && spans.isNotEmpty()) {
            val qIdx = (0 until nahuatls.size.coerceAtMost(spans.size)).random()
            val questionPhrase = nahuatls[qIdx]
            val correctAnswer = spans[qIdx]
            ritualQ1Question = "¿Cuál es la traducción correcta al español de esta expresión azteca?:\n\n\"$questionPhrase\""
            
            val optionsSet = mutableSetOf<String>()
            optionsSet.add(correctAnswer)
            for (span in spans) {
                if (span != correctAnswer && optionsSet.size < 3) {
                    optionsSet.add(span)
                }
            }
            if (optionsSet.size < 3) {
                val needed = 3 - optionsSet.size
                val distractors = getRandomDistractors(correctAnswer, needed)
                optionsSet.addAll(distractors)
            }
            val shuffled = optionsSet.toList().shuffled()
            ritualQ1Options = shuffled
            ritualQ1CorrectIndex = shuffled.indexOf(correctAnswer)
        } else {
            ritualQ1Question = "¿Qué representa el agua para nuestra cultura (Atl)?"
            ritualQ1Options = listOf("La fuerza del fuego", "La vida celeste y fertilidad", "El metal del templo")
            ritualQ1CorrectIndex = 1
        }
        
        ritualQ1SelectedIndex = null
        ritualQ1Feedback = ""
        ritualQ1Verified = false
        
        // --- STEP 2: Glifos Desordenados (Sentence Reconstruction) ---
        if (nahuatls.isNotEmpty()) {
            val q2Idx = if (nahuatls.size > 1) 1 else 0
            val targetPhrase = nahuatls[q2Idx]
            ritualQ2TargetPhrase = targetPhrase
            ritualQ2SpanishPrompt = spans.getOrNull(q2Idx) ?: "Traduce esta frase"
            
            // Clean punctuation and get words
            val words = targetPhrase.split(" ")
                .map { it.trim().replace(Regex("[.,:;!¿?\"']"), "") }
                .filter { it.isNotEmpty() }
            
            ritualQ2ShuffledWords = words.shuffled()
        } else {
            ritualQ2TargetPhrase = "Nimitztlazohtla"
            ritualQ2SpanishPrompt = "Te amo"
            ritualQ2ShuffledWords = listOf("Nimitztlazohtla")
        }
        ritualQ2SelectedWords = emptyList()
        ritualQ2Verified = false
        ritualQ2Feedback = ""
        ritualQ2IsCorrect = false
        
        // --- STEP 3: El Oráculo del Destino (True/False) ---
        val isTrueClaim = (0..1).random() == 1
        if (nahuatls.isNotEmpty()) {
            val q3Idx = (nahuatls.indices).random()
            ritualQ3NahuatlWord = nahuatls[q3Idx]
            if (isTrueClaim) {
                ritualQ3SpanishClaim = spans[q3Idx]
                ritualQ3IsClaimTrue = true
            } else {
                val distractor = getRandomDistractors(spans[q3Idx], 1).firstOrNull() ?: "Una idea aleatoria del viento sagrado"
                ritualQ3SpanishClaim = distractor
                ritualQ3IsClaimTrue = false
            }
        } else {
            ritualQ3NahuatlWord = "Cozamalotl"
            if (isTrueClaim) {
                ritualQ3SpanishClaim = "Arcoíris"
                ritualQ3IsClaimTrue = true
            } else {
                ritualQ3SpanishClaim = "Serpiente de fuego"
                ritualQ3IsClaimTrue = false
            }
        }
        ritualQThirdSelectedIndex = null
        ritualQ3Verified = false
        ritualQ3Feedback = ""
        ritualQ3IsCorrect = false
        
        lessonRitualActive = true
    }

    fun verifyStep1() {
        val sel = ritualQ1SelectedIndex
        if (sel == null) {
            ritualQ1Feedback = "El Sacerdote espera tu elección en silencio..."
            return
        }
        ritualQ1Verified = true
        if (sel == ritualQ1CorrectIndex) {
            ritualQ1Feedback = "¡Correcto! Los espíritus de los sabios aprueban tu traducción. Avanza al siguiente misterio."
        } else {
            ritualQ1Feedback = "Incorrecto. Los glifos revelan otra verdad. Vuelve a examinar el significado."
            deductLifeAndSave()
        }
    }
    
    fun verifyStep2() {
        val userReconstructed = ritualQ2SelectedWords.joinToString(" ").lowercase().trim().replace(Regex("[.,:;!¿?\"']"), "")
        val targetCleaned = ritualQ2TargetPhrase.lowercase().trim().replace(Regex("[.,:;!¿?\"']"), "")
        
        ritualQ2Verified = true
        if (userReconstructed == targetCleaned) {
            ritualQ2IsCorrect = true
            ritualQ2Feedback = "¡Impecable! Has ordenado los glifos sagrados perfectamente. El camino se aclara."
        } else {
            ritualQ2IsCorrect = false
            ritualQ2Feedback = "Los glifos no están en el orden sagrado. Intenta de nuevo."
            deductLifeAndSave()
        }
    }
    
    fun verifyStep3() {
        val selected = ritualQThirdSelectedIndex
        if (selected == null) {
            ritualQ3Feedback = "Debes declarar si la afirmación es verdadera o falsa."
            return
        }
        
        ritualQ3Verified = true
        if (selected == ritualQ3IsClaimTrue) {
            ritualQ3IsCorrect = true
            ritualQ3Feedback = "¡Sabiduría divina! Guardabas la verdad en tu corazón. ¡El ritual está completo! Finaliza la lección para reclamar tu ofrenda de XP."
            ritualCompleteSuccess = true
        } else {
            ritualQ3IsCorrect = false
            ritualQ3Feedback = "El Oráculo del Templo no concuerda. La balanza se inclina, medita tu elección."
            deductLifeAndSave()
        }
    }

    // Finish lesson, mark it completed and award XP only after user finishes
    fun finishActiveLessonAndClaimXp() {
        val currentActive = activeLesson
        if (currentActive != null && ritualCompleteSuccess && !currentActive.isCompleted) {
            toggleLessonStatus(currentActive.id, true)
            viewModelScope.launch {
                repository.addXp(50)
            }
        }
        lessonRitualActive = false
        activeLesson = null
    }


    // --- Games Management ---

    // History sets to prevent repetition
    private val recentScrambleWords = mutableSetOf<String>()
    private val recentQTKeys = mutableSetOf<String>()
    private val recentGlyphKeys = mutableSetOf<String>()
    private val recentHangmanWords = mutableSetOf<String>()
    private val recentFillBlankKeys = mutableSetOf<String>()
    private val recentMemoramaPairs = mutableSetOf<String>()
    private val recentQuizQuestionKeys = mutableSetOf<String>()

    // Initialize Memorama game
    fun startMemoramaGame(level: GameLevel? = selectedGameLevel) {
        viewModelScope.launch {
            val levelId = level?.id ?: selectedGameLevel?.id ?: 1
            val pairCount = when {
                levelId <= 10 -> 3 // 6 cards
                levelId <= 25 -> 4 // 8 cards
                levelId <= 40 -> 6 // 12 cards
                else -> 8          // 16 cards
            }
            val lang = userProgress.value?.selectedLanguage ?: "Nahuatl"
            val fullPool = when (lang) {
                "Otomi" -> listOf(
                    Pair("Ki hats'i", "Hola"), Pair("Di jamädi", "Gracias"), Pair("K'ëñä", "Serpiente"),
                    Pair("Dehe", "Agua"), Pair("Ki xudi", "Buenos días"), Pair("Zat'i", "Jaguar"),
                    Pair("Hyadi", "Sol"), Pair("Zänä", "Luna"), Pair("Sipi", "Fuego"),
                    Pair("Tso'e", "Estrella"), Pair("Ngü", "Casa"), Pair("Thuju", "Conejo"),
                    Pair("T'oomi", "Águila"), Pair("B'aye", "Viento"), Pair("Mäs'i", "Maíz")
                )
                "Totona" -> listOf(
                    Pair("Tlan tlan", "Hola"), Pair("Paxcatgatzilh", "Gracias"), Pair("Luwa", "Serpiente"),
                    Pair("Chuchut", "Agua"), Pair("Tlan katsatzi", "Buenos días"), Pair("Misin", "Jaguar"),
                    Pair("Chichini", "Sol"), Pair("Papak", "Luna"), Pair("Lhkuyat", "Fuego"),
                    Pair("Staku", "Estrella"), Pair("Xa'lh", "Águila"), Pair("Chiya", "Conejo"),
                    Pair("Xanat", "Flor"), Pair("Tiyán", "Tierra"), Pair("Naku", "Corazón")
                )
                "Maya" -> listOf(
                    Pair("Ba'ax ka wa'alik", "Hola"), Pair("Dios bo'otik", "Gracias"), Pair("Kan", "Serpiente"),
                    Pair("Ja'", "Agua"), Pair("Ma'alob k'iin", "Buenos días"), Pair("Balam", "Jaguar"),
                    Pair("K'iin", "Sol"), Pair("Uj", "Luna"), Pair("K'a'ak'", "Fuego"),
                    Pair("Ek'", "Estrella"), Pair("Ch'oom", "Águila"), Pair("T'u'ul", "Conejo"),
                    Pair("Yaxche'", "Ceiba"), Pair("Otot", "Casa"), Pair("Ixchel", "Arcoíris")
                )
                "Zapoteco" -> listOf(
                    Pair("Gubidxa", "Sol"), Pair("Beedu", "Luna"), Pair("Nisa", "Agua"),
                    Pair("Gi", "Fuego"), Pair("Bizu'u", "Estrella"), Pair("Beza'a", "Jaguar"),
                    Pair("Mani'", "Águila"), Pair("Benda", "Serpiente"), Pair("Bizuu", "Conejo"), Pair("Liiza", "Casa")
                )
                "Mixteco" -> listOf(
                    Pair("Yandavi", "Sol"), Pair("Yoo", "Luna"), Pair("Ntkuii", "Agua"),
                    Pair("Ñuhu", "Fuego"), Pair("Kimij", "Estrella"), Pair("Kiti", "Jaguar"),
                    Pair("Yute", "Río"), Pair("Koo", "Serpiente"), Pair("Isi", "Conejo"), Pair("Ve'e", "Casa")
                )
                else -> listOf(
                    Pair("Niltze", "Hola"), Pair("Tlazohcamati", "Gracias"), Pair("Coatl", "Serpiente"),
                    Pair("Atl", "Agua"), Pair("Cualli tonalli", "Buenos días"), Pair("Ocelotl", "Jaguar"),
                    Pair("Tonatiuh", "Sol"), Pair("Metztli", "Luna"), Pair("Tletl", "Fuego"),
                    Pair("Citlalin", "Estrella"), Pair("Cuauhtli", "Águila"), Pair("Tochtli", "Conejo"),
                    Pair("Xochitl", "Flor"), Pair("Calli", "Casa"), Pair("Yoloti", "Corazón"), Pair("Papalotl", "Mariposa")
                )
            }

            var availableTerms = fullPool.filter { pair -> !recentMemoramaPairs.contains("${pair.first}-${pair.second}") }
            if (availableTerms.size < pairCount) {
                recentMemoramaPairs.clear()
                availableTerms = fullPool
            }

            val chosenTerms = availableTerms.shuffled().take(pairCount)
            chosenTerms.forEach { pair ->
                recentMemoramaPairs.add("${pair.first}-${pair.second}")
            }

            val cardsList = mutableListOf<MemoryCard>()
            var cardId = 1
            chosenTerms.forEachIndexed { pairIndex, pair ->
                cardsList.add(MemoryCard(id = cardId++, text = pair.first, pairId = pairIndex))
                cardsList.add(MemoryCard(id = cardId++, text = pair.second, pairId = pairIndex))
            }
            cardsList.shuffle()

            memoramaCards = cardsList
            firstSelectedCardIndex = null
            secondSelectedCardIndex = null
            memoramaMovesCount = 0
            memoramaScore = 0
            memoramaGameWon = false
        }
    }

    // Handle card click inside Memorama
    fun handleMemoramaCardClick(index: Int) {
        // Prevent clicking already flipped or matched cards
        if (memoramaCards[index].isFlipped || memoramaCards[index].isMatched) return
        if (firstSelectedCardIndex != null && secondSelectedCardIndex != null) return

        viewModelScope.launch {
            // Flip card visually
            memoramaCards = memoramaCards.mapIndexed { idx, card ->
                if (idx == index) card.copy(isFlipped = true) else card
            }

            if (firstSelectedCardIndex == null) {
                firstSelectedCardIndex = index
            } else {
                secondSelectedCardIndex = index
                memoramaMovesCount++

                val firstCardIdx = firstSelectedCardIndex!!
                val secondCardIdx = secondSelectedCardIndex!!

                if (memoramaCards[firstCardIdx].pairId == memoramaCards[secondCardIdx].pairId) {
                    // Match found!
                    kotlinx.coroutines.delay(600)
                    memoramaCards = memoramaCards.mapIndexed { idx, card ->
                        if (idx == firstCardIdx || idx == secondCardIdx) {
                            card.copy(isMatched = true)
                        } else {
                            card
                        }
                    }
                    memoramaScore += 10
                    firstSelectedCardIndex = null
                    secondSelectedCardIndex = null

                    // Win Check
                    if (memoramaCards.all { card -> card.isMatched }) {
                        memoramaGameWon = true
                        repository.playMatch() // Register win and log progress
                        repository.addXp(40) // Rewards
                    }
                } else {
                    // No match
                    kotlinx.coroutines.delay(1200)
                    memoramaCards = memoramaCards.mapIndexed { idx, card ->
                        if (idx == firstCardIdx || idx == secondCardIdx) {
                            card.copy(isFlipped = false)
                        } else {
                            card
                        }
                    }
                    firstSelectedCardIndex = null
                    secondSelectedCardIndex = null
                }
            }
        }
    }

    // Quiz action handlers
    fun selectQuizAnswer(option: String) {
        if (showQuizExplanation) return // locked once checked
        selectedQuizOption = option
    }

    fun verifyQuizAnswer() {
        if (selectedQuizOption == null || showQuizExplanation) return
        
        showQuizExplanation = true
        if (selectedQuizOption == quizQuestions[currentQuizIndex].correctAnswer) {
            quizScore++
            viewModelScope.launch {
                repository.addXp(15) // Gain points for correct guess
            }
        } else {
            deductLifeAndSave()
        }
    }

    fun nextQuizQuestion() {
        showQuizExplanation = false
        selectedQuizOption = null
        if (currentQuizIndex < quizQuestions.size - 1) {
            currentQuizIndex++
        } else {
            quizCompleted = true
            viewModelScope.launch {
                repository.addXp(50) // Master completion points
                repository.playMatch() // Register match
            }
        }
    }

    fun resetQuiz(level: GameLevel? = selectedGameLevel) {
        val targetLevel = level ?: selectedGameLevel
        val levelId = targetLevel?.id ?: 1
        val qCount = when {
            levelId <= 15 -> 3
            levelId <= 35 -> 5
            else -> 7
        }
        val lang = userProgress.value?.selectedLanguage ?: "Nahuatl"
        val fullBank = getQuizQuestionsBankForLanguage(lang, levelId)
        
        var filtered = fullBank.filter { !recentQuizQuestionKeys.contains(it.question) }
        if (filtered.size < qCount) {
            recentQuizQuestionKeys.clear()
            filtered = fullBank
        }
        
        val chosen = filtered.shuffled().take(qCount)
        chosen.forEach { recentQuizQuestionKeys.add(it.question) }
        activeQuizQuestions = chosen

        currentQuizIndex = 0
        selectedQuizOption = null
        quizScore = 0
        showQuizExplanation = false
        quizCompleted = false
    }


    // --- Daily Practices & Reminder Scheduling ---

    fun toggleBiometricLockPreference(isEnabled: Boolean) {
        viewModelScope.launch {
            val currentProgress = repository.getProgress() ?: UserProgress()
            repository.updateProgress(currentProgress.copy(isBiometricLockEnabled = isEnabled))
        }
    }

    fun toggleCodeProtection(active: Boolean) {
        isCodeProtectionActive = active
        val prefs = getApplication<Application>().getSharedPreferences("security_prefs", android.content.Context.MODE_PRIVATE)
        prefs.edit().putBoolean("code_protection_active", active).apply()
    }

    fun updateBiometricPinBypass(newPin: String) {
        viewModelScope.launch {
            val currentProgress = repository.getProgress() ?: UserProgress()
            repository.updateProgress(currentProgress.copy(biometricPinBypass = newPin))
        }
    }

    fun requestAiGameHint() {
        val level = selectedGameLevel ?: return
        val contextDetail = when (level.type) {
            "Scramble" -> "Palabra anagrama desordenada: $scrambleShuffledWord. Juego de anagramas."
            "QuickTranslate" -> "Palabra indígena: $qtNahuatlWord, opciones: ${qtOptions.joinToString(",")}. Juego de traducción rápida."
            "GlyphMatch" -> "Palabra de códice a identificar: $glyphNahuatlWord. Opciones de glifos: ${glyphOptions.joinToString(",")}. Juego de glifos."
            "Quiz" -> {
                val q = quizQuestions.getOrNull(currentQuizIndex)
                if (q != null) {
                    "Pregunta: ${q.question}, opciones: ${q.options.joinToString(",")}."
                } else {
                    "Pregunta sobre geografía o cultura indígena de unidad ${level.unit}."
                }
            }
            "Memorama" -> "La temática de retención rápida de glifos en memorama de la unidad ${level.unit}."
            else -> "Consejo general de vocabulario indígena."
        }

        viewModelScope.launch {
            isRequestingAiGameHint = true
            aiGameHintText = "Invocando conocimiento del Oráculo..."
            try {
                val lang = userProgress.value?.selectedLanguage ?: "Nahuatl"
                val hint = com.example.data.GeminiTranslationHelper.getGameOracleHint(level.type, contextDetail, lang)
                aiGameHintText = hint
            } catch (e: Exception) {
                val lang = userProgress.value?.selectedLanguage ?: "Nahuatl"
                aiGameHintText = if (lang == "Otomi") {
                    "Consejo del viento: Sintoniza la sabiduría otomí de las flores y los tenangos para resolver el enigma."
                } else if (lang == "Totona") {
                    "Consejo del viento: Sintoniza la sabiduría totonaca del Tajín y la vainilla aromática para resolver el enigma."
                } else {
                    "Sugerencia del viento: Confía en tus ancestros para resolver el enigma lúdico."
                }
            } finally {
                isRequestingAiGameHint = false
            }
        }
    }

    // Toggle notifications toggle
    fun toggleAlertStatus(isEnabled: Boolean) {
        alertsEnabled = isEnabled
        viewModelScope.launch {
            val currentProgress = repository.getProgress() ?: UserProgress()
            repository.updateProgress(currentProgress.copy(notificationEnabled = isEnabled))
        }
    }

    // Update reminder alert time
    fun updateReminderTime(time: String) {
        reminderTimeString = time
        viewModelScope.launch {
            val currentProgress = repository.getProgress() ?: UserProgress()
            repository.updateProgress(currentProgress.copy(notificationTime = time))
        }
    }

    // Practice click & Daily Streak Check-in
    fun performDailyCheckIn() {
        hasFiredPracticeNotificationForToday = true
        viewModelScope.launch {
            repository.addXp(20) // daily reward streak
            // Record interaction in Room DB and compute consecutive days
            val stats = repository.recordDailyInteraction(interactionType = "CHECK_IN", xpEarned = 20)
            _streakStats.value = stats
            val progress = repository.getProgress() ?: UserProgress()
            repository.updateProgress(
                progress.copy(
                    streakDays = maxOf(1, stats.currentStreak),
                    lastPracticeTime = System.currentTimeMillis()
                )
            )
        }
    }

    fun recordDailyInteraction(type: String = "LOGIN", xpEarned: Int = 0, customDate: String? = null) {
        viewModelScope.launch {
            val dateStr = customDate ?: DailyStreakCalculator.getCurrentDateString()
            val stats = repository.recordDailyInteraction(
                interactionType = type,
                xpEarned = xpEarned,
                dateString = dateStr
            )
            _streakStats.value = stats
            if (xpEarned > 0) {
                repository.addXp(xpEarned)
            }
        }
    }

    fun refreshDailyStreak() {
        viewModelScope.launch {
            _streakStats.value = repository.calculateStreakStats()
        }
    }

    fun simulatePastInteraction(daysAgo: Int) {
        viewModelScope.launch {
            val cal = java.util.Calendar.getInstance().apply {
                add(java.util.Calendar.DAY_OF_YEAR, -daysAgo)
            }
            val dateStr = DailyStreakCalculator.getDateFormat().format(cal.time)
            val stats = repository.recordDailyInteraction(
                interactionType = "TEST_INTERACTION",
                xpEarned = 15,
                dateString = dateStr
            )
            _streakStats.value = stats
        }
    }

    fun deleteDailyInteraction(id: Long) {
        viewModelScope.launch {
            repository.deleteInteractionById(id)
            _streakStats.value = repository.calculateStreakStats()
        }
    }

    // --- Navegante Expanded Features ---

    // Auth fields
    var authEmail by mutableStateOf("")
    var authPassword by mutableStateOf("")
    var isAuthRegisterMode by mutableStateOf(false)
    var isCloudDownloading by mutableStateOf(false)
    var cloudDownloadComplete by mutableStateOf(false)

    // Google Cloud & Sync Stability
    var isGoogleChooserOpen by mutableStateOf(false)
    var isCloudConnected by mutableStateOf(true)
    var cloudServerStatus by mutableStateOf("En línea (Excelente)")
    var cloudServerFailureRate by mutableStateOf("0.00% (Sin fallos de red)")
    var fontSizeMultiplier by mutableStateOf(1.35f) // Default to 1.35f (Large font size)

    fun performGoogleLogin(email: String) {
        viewModelScope.launch {
            authEmail = email
            authPassword = "google_OAuth2_secure_token"
            // Record login in Room DB for daily streak
            val stats = repository.recordDailyInteraction(interactionType = "GOOGLE_LOGIN", xpEarned = 100)
            _streakStats.value = stats
            val progress = repository.getProgress() ?: UserProgress()
            repository.updateProgress(
                progress.copy(
                    userEmail = email,
                    isLogged = true,
                    xp = progress.xp + 100,
                    streakDays = maxOf(1, stats.currentStreak)
                )
            )
        }
    }


    // Personalized AI Lessons help
    var isAiLessonExplaining by mutableStateOf(false)
    var customAiExplanation by mutableStateOf<String?>(null)
    var customAiTopicQuery by mutableStateOf("")

    // --- Oracle Chat (Asistente Oráculo de IA) ---
    data class OracleMessage(
        val id: String,
        val sender: String, // "user" or "oracle"
        val text: String,
        val timestamp: Long = System.currentTimeMillis()
    )

    var showAiOracleChat by mutableStateOf(false)
    var isOracleThinking by mutableStateOf(false)
    var oracleCurrentThinkingText by mutableStateOf("")
    var oracleChatHistory by mutableStateOf<List<OracleMessage>>(emptyList())
    var oracleUserQuery by mutableStateOf("")

    fun initOracleChatIfNeeded() {
        if (oracleChatHistory.isEmpty()) {
            val lang = userProgress.value?.selectedLanguage ?: "Nahuatl"
            val initialGreeting = when (lang) {
                "Totona" -> {
                    val variant = userProgress.value?.selectedTotonaVariant ?: "Totonaco de la costa"
                    "🌿 **¡Tlan kilhtamaku, joven alma!** Soy el sabio abuelo y Sacerdote Oráculo de Papantla. Veo que deseas aprender la variante **$variant**. Bienvenido a este recinto perfumado con vainilla y bendecido por El Tajín. ¿Qué misterios de la lengua totonaca o de nuestras tradiciones deseas revelar hoy? Mi sabiduría está a tu entero servicio."
                }
                else -> { // Nahuatl
                    val variant = userProgress.value?.selectedNahuatlVariant ?: "Náhuatl de la Huasteca veracruzana"
                    "🛕 **¡Niltze, joven guerrero de la sabiduría!** Soy el Sacerdote Oráculo guardián del gran Anáhuac. Veo que has elegido aprender la variante **$variant**. El copal perfuma el viento y los astros guían nuestro encuentro. Pregúntame sobre la sagrada lengua náhuatl, sus giros regionales, nuestras deidades o de nuestro origen mesoamericano. Te guiaré con honor."
                }
            }
            oracleChatHistory = listOf(
                OracleMessage(
                    id = "init_msg",
                    sender = "oracle",
                    text = initialGreeting
                )
            )
        }
    }

    fun sendOracleQuery() {
        val query = oracleUserQuery.trim()
        if (query.isBlank()) return
        
        val userMsg = OracleMessage(id = "msg_${System.currentTimeMillis()}_user", sender = "user", text = query)
        oracleChatHistory = oracleChatHistory + userMsg
        oracleUserQuery = ""
        
        viewModelScope.launch {
            isOracleThinking = true
            val lang = userProgress.value?.selectedLanguage ?: "Nahuatl"
            
            val thinkingTexts = when (lang) {
                "Totona" -> {
                    listOf(
                        "El abuelo de Papantla muele vainilla silvestre en el metate ceremonial...",
                        "Elevando plegarias con el vuelo místico de los Voladores de El Tajín...",
                        "Los cuatro rumbos del viento traen aromas de sabiduría ancestral...",
                        "El Trueno Sagrado retumba en los nichos de El Tajín revelando la respuesta..."
                    )
                }
                else -> { // Nahuatl
                    listOf(
                        "El Sacerdote quema copal en el sahumador sagrado para purificar tu duda...",
                        "Consultando el amoxtli (libro de pinturas) para desenterrar la sabiduría del Anáhuac...",
                        "Buscando consejo en el canto de los caracoles sagrados del Templo Mayor...",
                        "Huitzilopochtli susurra antiguas voces sobre la gran Tenochtitlan..."
                    )
                }
            }
            
            oracleCurrentThinkingText = thinkingTexts.random()
            
            val result = GeminiTranslationHelper.queryOracleWithAi(query, lang)
            
            val oracleMsg = OracleMessage(id = "msg_${System.currentTimeMillis()}_oracle", sender = "oracle", text = result)
            oracleChatHistory = oracleChatHistory + oracleMsg
            isOracleThinking = false
            
            repository.addXp(15) // XP bonus for consulting the Oracle!
        }
    }

    fun clearOracleChat() {
        oracleChatHistory = emptyList()
        initOracleChatIfNeeded()
    }

    // 50 Interactive Games Explorer
    data class GameLevel(
        val id: Int,
        val name: String,
        val type: String, // Memorama, Quiz, Scramble, QuickTranslate, GlyphMatch
        val difficulty: String,
        val unit: Int,
        val isLocked: Boolean = false,
        val xpReward: Int = 50
    )

    // Generate 50 unique game levels
    fun getFiftyGames(matchesPlayed: Int): List<GameLevel> {
        return (1..50).map { id ->
            val gType = when (id % 7) {
                0 -> "Memorama"
                1 -> "Quiz"
                2 -> "Scramble"
                3 -> "QuickTranslate"
                4 -> "GlyphMatch"
                5 -> "Ahorcado"
                else -> "FillBlank"
            }
            val categoryTitle = when (gType) {
                "Memorama" -> "Memoria Sagrada"
                "Quiz" -> "Desafío de Sabios"
                "Scramble" -> "Letras al Viento"
                "QuickTranslate" -> "Flecha Veloz"
                "GlyphMatch" -> "Códice Ancestral"
                "Ahorcado" -> "Guerrero Sagrado"
                else -> "Llenar Códice"
            }
            val difficulty = when {
                id <= 15 -> "Principiante"
                id <= 35 -> "Intermedio"
                else -> "Avanzado"
            }
            // Lock levels that are ahead of the user's completed matches
            val locked = id > 1 && id > (matchesPlayed + 1)
            GameLevel(
                id = id,
                name = "$categoryTitle $id",
                type = gType,
                difficulty = difficulty,
                unit = ((id - 1) / 5) + 1,
                isLocked = locked
            )
        }
    }

    var selectedGameLevel by mutableStateOf<GameLevel?>(null)
    var lockedGameLevelAlert by mutableStateOf<GameLevel?>(null)

    fun unlockGameLevel(level: GameLevel) {
        viewModelScope.launch {
            repository.playMatch()
            deductXpForUnlock(20)
            lockedGameLevelAlert = null
        }
    }

    // Scramble Game State
    var scrambleOriginalWord by mutableStateOf("")
    var scrambleShuffledWord by mutableStateOf("")
    var scrambleUserAnswer by mutableStateOf("")
    var scrambleIsCorrect by mutableStateOf<Boolean?>(null)

    // Quick Translate State
    var qtNahuatlWord by mutableStateOf("")
    var qtOptions by mutableStateOf<List<String>>(emptyList())
    var qtCorrectAnswer by mutableStateOf("")
    var qtUserAnswer by mutableStateOf<String?>(null)
    var qtIsCorrect by mutableStateOf<Boolean?>(null)

    // Glyph Match State
    var glyphNahuatlWord by mutableStateOf("")
    val glyphSymbols = listOf("☀️ Sol", "🌙 Luna", "💧 Agua", "🔥 Fuego", "⭐ Estrella", "🐆 Jaguar", "🦅 Águila", "🐍 Serpiente", "🐇 Conejo")
    var glyphCorrectSymbol by mutableStateOf("")
    var glyphOptions by mutableStateOf<List<String>>(emptyList())
    var glyphSelectedSymbol by mutableStateOf<String?>(null)
    var glyphIsCorrect by mutableStateOf<Boolean?>(null)

    // Ahorcado (Sacred Hangman) State
    var hangmanOriginalWord by mutableStateOf("")
    var hangmanGuessedLetters by mutableStateOf<Set<Char>>(emptySet())
    var hangmanMaxAttempts by mutableStateOf(6)
    var hangmanAttemptsLeft by mutableStateOf(6)
    var hangmanIsCorrect by mutableStateOf<Boolean?>(null)

    // Fill-in-the-Blanks (Llenar Códice) State
    var fillBlankQuestionPhrase by mutableStateOf("")
    var fillBlankCorrectWord by mutableStateOf("")
    var fillBlankOptions by mutableStateOf<List<String>>(emptyList())
    var fillBlankUserAnswer by mutableStateOf<String?>(null)
    var fillBlankIsCorrect by mutableStateOf<Boolean?>(null)

    // Set last active lesson ID
    fun updateLastActiveLesson(lessonId: Int) {
        viewModelScope.launch {
            val progress = repository.getProgress() ?: UserProgress()
            repository.updateProgress(progress.copy(lastActiveLessonId = lessonId))
        }
    }

    // Trigger Cloud Sync Integration
    fun triggerCloudSync() {
        viewModelScope.launch {
            isCloudDownloading = true
            kotlinx.coroutines.delay(2000)
            isCloudDownloading = false
            cloudDownloadComplete = true
            repository.addXp(120) // Cloud sync reward
            val progress = repository.getProgress() ?: UserProgress()
            repository.updateProgress(progress.copy(cloudBackupTimestamp = System.currentTimeMillis()))
        }
    }

    // Auth logic
    fun performAuthAction() {
        if (authEmail.isBlank() || authPassword.isBlank()) return
        viewModelScope.launch {
            val stats = repository.recordDailyInteraction(interactionType = "EMAIL_LOGIN", xpEarned = 50)
            _streakStats.value = stats
            val progress = repository.getProgress() ?: UserProgress()
            repository.updateProgress(
                progress.copy(
                    userEmail = authEmail,
                    isLogged = true,
                    xp = progress.xp + 50,
                    streakDays = maxOf(1, stats.currentStreak)
                )
            )
        }
    }


    fun performLogout() {
        viewModelScope.launch {
            val progress = repository.getProgress() ?: UserProgress()
            repository.updateProgress(progress.copy(isLogged = false))
            authEmail = ""
            authPassword = ""
        }
    }

    // Core AI explanations generator struggle-specific
    fun runAiCustomExplanation(lessonTitle: String, explanationText: String) {
        viewModelScope.launch {
            isAiLessonExplaining = true
            customAiExplanation = null
            val lang = userProgress.value?.selectedLanguage ?: "Nahuatl"
            val result = GeminiTranslationHelper.explainLessonWithAi(lessonTitle, explanationText, customAiTopicQuery, lang)
            customAiExplanation = result
            isAiLessonExplaining = false
            repository.addXp(10) // Small exploration XP
        }
    }

    // Launch Interactive Level Game Play
    fun startInteractiveLevelGame(level: GameLevel) {
        val currentLives = userProgress.value?.lives ?: 5
        if (currentLives <= 0) {
            showOutOfLivesDialog = true
            return
        }
        selectedGameLevel = level
        aiGameHintText = null
        scrambleIsCorrect = null
        qtIsCorrect = null
        glyphIsCorrect = null
        fillBlankIsCorrect = null
        fillBlankUserAnswer = null
        scrambleUserAnswer = ""
        qtUserAnswer = null
        glyphSelectedSymbol = null

        val lang = userProgress.value?.selectedLanguage ?: "Nahuatl"
        val levelId = level.id

        when (level.type) {
            "Memorama" -> {
                startMemoramaGame(level)
            }
            "Quiz" -> {
                resetQuiz(level)
            }
            "Scramble" -> {
                val pool = when {
                    levelId <= 15 -> when (lang) { // Easy (4-5 chars)
                        "Totona" -> listOf("TLAN", "LUWA", "MISIN", "PAPAK", "CHIYA", "STAKU", "XANAT")
                        "Maya" -> listOf("KAN", "UJ", "EK'", "K'IIN", "CH'O", "B'AL", "OTOT")
                        "Otomi" -> listOf("DEHE", "HYADI", "SIPI", "ZÄNÄ", "NGÜ", "ZAT'I", "THUJU")
                        else -> listOf("TLAN", "COATL", "AMATL", "TEOTL", "ATLE", "MIZTO", "NILTZE", "YOLOTL")
                    }
                    levelId <= 35 -> when (lang) { // Medium (6-8 chars)
                        "Totona" -> listOf("CHICHINI", "CHUCHUT", "LHKUYAT", "TSUMUUT", "PAXCAT", "KATZATZI")
                        "Maya" -> listOf("BALAM", "MA'ALOB", "BO'OTIK", "CH'OOM", "K'A'AK'", "WA'ALIK")
                        "Otomi" -> listOf("HATSA'I", "JAMÄDI", "B'AYE", "MÄS'I", "NXUTSI")
                        else -> listOf("OCELOTL", "TLALOC", "METZTLI", "TOCHTLI", "XOCHITL", "PAPALOTL", "TONATIUH", "CITLALIN")
                    }
                    else -> when (lang) { // Hard (9-15 chars)
                        "Totona" -> listOf("PAXCATGATZILH", "TLANKATSATZI", "CHICHINITLAN", "PAPANTLATA")
                        "Maya" -> listOf("K'UK'ULKAN", "CHICHENITZA", "DIOSBO'OTIK", "BA'AXKAWA'ALIK")
                        "Otomi" -> listOf("KI-HATSA'I", "DI-JAMÄDI", "HYADI-ZÄNÄ", "MÄ-HETS'I")
                        else -> listOf("QUETZALCOATL", "CHALCHIUHTLICUE", "HUEHUETEOTL", "NEZAHUALCOYOTL", "TEOTIHUACAN", "TLAMATILIZTLI")
                    }
                }
                var available = pool.filter { !recentScrambleWords.contains(it) }
                if (available.isEmpty()) {
                    recentScrambleWords.clear()
                    available = pool
                }
                val chosenWord = available.shuffled().first()
                recentScrambleWords.add(chosenWord)
                scrambleOriginalWord = chosenWord
                
                var shuffled = chosenWord.toList().shuffled().joinToString("")
                var attempts = 0
                while (shuffled == chosenWord && chosenWord.length > 1 && attempts < 10) {
                    shuffled = chosenWord.toList().shuffled().joinToString("")
                    attempts++
                }
                scrambleShuffledWord = shuffled
            }
            "QuickTranslate" -> {
                val optionCount = when {
                    levelId <= 15 -> 3
                    levelId <= 35 -> 4
                    else -> 5
                }
                val allPairs = when (lang) {
                    "Totona" -> listOf(
                        Pair("Tlan tlan", "Hola"), Pair("Paxcatgatzilh", "Gracias"), Pair("Chuchut", "Agua"),
                        Pair("Lhkuyat", "Fuego"), Pair("Chichini", "Sol"), Pair("Papak", "Luna"),
                        Pair("Misin", "Jaguar"), Pair("Xa'lh", "Águila"), Pair("Luwa", "Serpiente"),
                        Pair("Xanat", "Flor de Vainilla"), Pair("Tlan katsatzi", "Buenos días"), Pair("Tiyán", "Tierra")
                    )
                    "Maya" -> listOf(
                        Pair("Ba'ax ka wa'alik", "Hola"), Pair("Dios bo'otik", "Gracias"), Pair("Ma'alob k'iin", "Buenos días"),
                        Pair("Ja'", "Agua"), Pair("K'a'ak'", "Fuego"), Pair("K'iin", "Sol"),
                        Pair("Uj", "Luna"), Pair("Balam", "Jaguar"), Pair("Kan", "Serpiente"), Pair("Ch'oom", "Águila")
                    )
                    "Otomi" -> listOf(
                        Pair("Ki hats'i", "Hola"), Pair("Di jamädi", "Gracias"), Pair("Ki xudi", "Buenos días"),
                        Pair("Dehe", "Agua"), Pair("Sipi", "Fuego"), Pair("Hyadi", "Sol"),
                        Pair("Zänä", "Luna"), Pair("Zat'i", "Jaguar"), Pair("K'ëñä", "Serpiente")
                    )
                    else -> listOf(
                        Pair("Niltze", "Hola"), Pair("Tlazohcamati", "Gracias"), Pair("Cualli tonalli", "Buenos días"),
                        Pair("Atl", "Agua"), Pair("Tletl", "Fuego"), Pair("Tonatiuh", "Sol"),
                        Pair("Metztli", "Luna"), Pair("Ocelotl", "Jaguar"), Pair("Coatl", "Serpiente"),
                        Pair("Cuauhtli", "Águila"), Pair("Tochtli", "Conejo"), Pair("Citlalin", "Estrella"),
                        Pair("Xochitl", "Flor"), Pair("Yoloti", "Corazón")
                    )
                }

                var available = allPairs.filter { !recentQTKeys.contains(it.first) }
                if (available.isEmpty()) {
                    recentQTKeys.clear()
                    available = allPairs
                }
                val current = available.shuffled().first()
                recentQTKeys.add(current.first)

                qtNahuatlWord = current.first
                qtCorrectAnswer = current.second

                val distractors = allPairs.filter { it.second != current.second }.map { it.second }.shuffled().take(optionCount - 1)
                qtOptions = (distractors + current.second).shuffled()
            }
            "GlyphMatch" -> {
                val optionCount = when {
                    levelId <= 15 -> 3
                    levelId <= 35 -> 4
                    else -> 6
                }
                val glyphMatches = when (lang) {
                    "Totona" -> listOf(
                        Pair("Chichini", "☀️ Sol"), Pair("Papak", "🌙 Luna"), Pair("Chuchut", "💧 Agua"),
                        Pair("Lhkuyat", "🔥 Fuego"), Pair("Staku", "⭐ Estrella"), Pair("Misin", "🐆 Jaguar"),
                        Pair("Xa'lh", "🦅 Águila"), Pair("Luwa", "🐍 Serpiente"), Pair("Chiya", "🐇 Conejo"),
                        Pair("Xanat", "🌸 Flor"), Pair("Mäs'i", "🌽 Maíz")
                    )
                    "Otomi" -> listOf(
                        Pair("Hyadi", "☀️ Sol"), Pair("Zänä", "🌙 Luna"), Pair("Dehe", "💧 Agua"),
                        Pair("Sipi", "🔥 Fuego"), Pair("Tso'e", "⭐ Estrella"), Pair("Zat'i", "🐆 Jaguar"),
                        Pair("T'oomi", "🦅 Águila"), Pair("K'ue", "🐍 Serpiente"), Pair("Thuju", "🐇 Conejo"),
                        Pair("Ngü", "🏠 Casa")
                    )
                    "Maya" -> listOf(
                        Pair("K'iin", "☀️ Sol"), Pair("Uj", "🌙 Luna"), Pair("Ha'", "💧 Agua"),
                        Pair("K'a'ak'", "🔥 Fuego"), Pair("Ek'", "⭐ Estrella"), Pair("Balam", "🐆 Jaguar"),
                        Pair("Ch'oom", "🦅 Águila"), Pair("Kan", "🐍 Serpiente"), Pair("T'u'ul", "🐇 Conejo")
                    )
                    else -> listOf(
                        Pair("Tonatiuh", "☀️ Sol"), Pair("Metztli", "🌙 Luna"), Pair("Atl", "💧 Agua"),
                        Pair("Tletl", "🔥 Fuego"), Pair("Citlalin", "⭐ Estrella"), Pair("Ocelotl", "🐆 Jaguar"),
                        Pair("Cuauhtli", "🦅 Águila"), Pair("Coatl", "🐍 Serpiente"), Pair("Tochtli", "🐇 Conejo"),
                        Pair("Xochitl", "🌸 Flor"), Pair("Centeotl", "🌽 Maíz"), Pair("Ehecatl", "💨 Viento")
                    )
                }

                var available = glyphMatches.filter { !recentGlyphKeys.contains(it.first) }
                if (available.isEmpty()) {
                    recentGlyphKeys.clear()
                    available = glyphMatches
                }
                val target = available.shuffled().first()
                recentGlyphKeys.add(target.first)

                glyphNahuatlWord = target.first
                glyphCorrectSymbol = target.second

                val distractors = glyphMatches.filter { it.second != target.second }.map { it.second }.shuffled().take(optionCount - 1)
                glyphOptions = (distractors + target.second).shuffled()
            }
            "Ahorcado" -> {
                hangmanMaxAttempts = when {
                    levelId <= 15 -> 6
                    levelId <= 35 -> 5
                    else -> 4
                }
                val vocabulary = when {
                    levelId <= 15 -> when (lang) {
                        "Totona" -> listOf("TLAN", "LUWA", "MISIN", "PAPAK", "CHIYA", "STAKU")
                        else -> listOf("NILTZE", "COATL", "AMATL", "TEOTL", "ATLE", "MIZTO")
                    }
                    levelId <= 35 -> when (lang) {
                        "Totona" -> listOf("CHICHINI", "CHUCHUT", "LHKUYAT", "TSUMUUT", "PAXCAT")
                        else -> listOf("OCELOTL", "TLALOC", "METZTLI", "TOCHTLI", "XOCHITL", "PAPALOTL", "TONATIUH")
                    }
                    else -> when (lang) {
                        "Totona" -> listOf("PAXCATGATZILH", "TLANKATSATZI", "CHICHINITLAN", "PAPANTLATA")
                        else -> listOf("QUETZALCOATL", "CHALCHIUHTLICUE", "HUEHUETEOTL", "NEZAHUALCOYOTL", "TEOTIHUACAN")
                    }
                }
                var available = vocabulary.filter { !recentHangmanWords.contains(it) }
                if (available.isEmpty()) {
                    recentHangmanWords.clear()
                    available = vocabulary
                }
                val word = available.shuffled().first()
                recentHangmanWords.add(word)

                hangmanOriginalWord = word
                hangmanGuessedLetters = emptySet()
                hangmanAttemptsLeft = hangmanMaxAttempts
                hangmanIsCorrect = null
            }
            "FillBlank" -> {
                val optionCount = when {
                    levelId <= 15 -> 3
                    levelId <= 35 -> 4
                    else -> 5
                }
                val phrasePool = when (lang) {
                    "Totona" -> listOf(
                        Triple("Tum ___ nemi.", "chiya", listOf("chiya", "misin", "ts'mu'ut", "xa'lh", "staku")),
                        Triple("Tuy ___ tlanextia.", "papak", listOf("papak", "tum", "tutu", "chuchut", "luwa")),
                        Triple("Tutu ts'umu'ut ___.", "patlani", listOf("patlani", "chiya", "misin", "papak", "xanat")),
                        Triple("Tati misin ___.", "tequiti", listOf("tequiti", "canta", "aprende", "habla", "camina")),
                        Triple("Chichini tlanextia ___.", "tiyán", listOf("tiyán", "katsatzi", "naku", "staku", "chuchut")),
                        Triple("Papak tlahvia ___.", "kakni", listOf("kakni", "chichini", "chuchut", "lhkuyat", "papak")),
                        Triple("___ patlani ilhuicac.", "Xa'lh", listOf("Xa'lh", "Luwa", "Chiya", "Misin", "Papak")),
                        Triple("Chuchut nemi ___.", "kakni", listOf("kakni", "naku", "tiyán", "staku", "misin"))
                    )
                    else -> listOf(
                        Triple("Ce ___ choloa.", "conetl", listOf("conetl", "mizton", "papalotl", "ocelotl", "coatl")),
                        Triple("Ome ___ choca.", "mizto", listOf("mizto", "ce", "eyi", "atl", "tletl")),
                        Triple("Eyi papalotzin ___.", "patlani", listOf("patlani", "choloa", "choca", "nemi", "tlaloc")),
                        Triple("Nahui tecpatl ___.", "mocahua", listOf("mocahua", "tlatia", "ihcac", "nemiliztli", "xochitl")),
                        Triple("Macuilli ___ tonalmitl.", "ce", listOf("ce", "ome", "eyi", "macuilli", "chicome")),
                        Triple("Ocelotl tlacua ___.", "cuauhtla", listOf("cuauhtla", "ilhuicac", "metztli", "tlallan", "yoloti")),
                        Triple("___ patlani ilhuicac.", "Cuauhtli", listOf("Cuauhtli", "Coatl", "Tochtli", "Mizton", "Tonatiuh")),
                        Triple("Coatl nemi ___.", "tlallan", listOf("tlallan", "ilhuicac", "metztli", "cuauhtla", "citlalin"))
                    )
                }

                var available = phrasePool.filter { !recentFillBlankKeys.contains(it.first) }
                if (available.isEmpty()) {
                    recentFillBlankKeys.clear()
                    available = phrasePool
                }
                val current = available.shuffled().first()
                recentFillBlankKeys.add(current.first)

                fillBlankQuestionPhrase = current.first
                fillBlankCorrectWord = current.second

                val opts = current.third.take(optionCount).toMutableList()
                if (!opts.contains(current.second)) {
                    opts[0] = current.second
                }
                fillBlankOptions = opts.shuffled()
                fillBlankUserAnswer = null
                fillBlankIsCorrect = null
            }
        }
    }

    fun submitHangmanLetter(char: Char) {
        if (hangmanIsCorrect != null) return
        val upperChar = char.uppercaseChar()
        hangmanGuessedLetters = hangmanGuessedLetters + upperChar
        if (!hangmanOriginalWord.uppercase().contains(upperChar)) {
            hangmanAttemptsLeft--
            if (hangmanAttemptsLeft <= 0) {
                hangmanIsCorrect = false
                deductLifeAndSave()
            }
        } else {
            val isWon = hangmanOriginalWord.uppercase().all { it == ' ' || hangmanGuessedLetters.contains(it) }
            if (isWon) {
                hangmanIsCorrect = true
                viewModelScope.launch {
                    repository.addXp(30)
                    repository.playMatch()
                }
            }
        }
    }

    fun submitFillBlankAnswer(answer: String) {
        fillBlankUserAnswer = answer
        val corr = answer.trim().equals(fillBlankCorrectWord.trim(), ignoreCase = true)
        fillBlankIsCorrect = corr
        if (corr) {
            viewModelScope.launch {
                repository.addXp(25)
                repository.playMatch()
            }
        } else {
            deductLifeAndSave()
        }
    }

    // Submit game level actions
    fun submitScrambleAnswer() {
        val corr = scrambleUserAnswer.trim().uppercase() == scrambleOriginalWord
        scrambleIsCorrect = corr
        if (corr) {
            viewModelScope.launch {
                repository.addXp(25)
                repository.playMatch()
            }
        } else {
            deductLifeAndSave()
        }
    }

    fun submitQuickTranslateAnswer(answer: String) {
        qtUserAnswer = answer
        val corr = answer == qtCorrectAnswer
        qtIsCorrect = corr
        if (corr) {
            viewModelScope.launch {
                repository.addXp(20)
                repository.playMatch()
            }
        } else {
            deductLifeAndSave()
        }
    }

    fun submitGlyphMatchAnswer(symbol: String) {
        glyphSelectedSymbol = symbol
        val corr = symbol.trim().equals(glyphCorrectSymbol.trim(), ignoreCase = true)
        glyphIsCorrect = corr
        if (corr) {
            viewModelScope.launch {
                repository.addXp(25)
                repository.playMatch()
            }
        } else {
            deductLifeAndSave()
        }
    }

    // --- Mascot & Lives Core System Logic ---

    fun deductLifeAndSave() {
        viewModelScope.launch {
            val progress = repository.getProgress() ?: return@launch
            if (progress.activeShieldCharges > 0) {
                repository.updateProgress(progress.copy(
                    activeShieldCharges = progress.activeShieldCharges - 1
                ))
                shieldSavedTrigger = true
                return@launch
            }
            val currentLives = progress.lives
            if (currentLives > 0) {
                val newLives = currentLives - 1
                val newRestoreTime = if (currentLives == 5) System.currentTimeMillis() else progress.lastLifeRestoreTime
                
                // Decrement pet hunger/happiness slightly on mistake to make caring tied to performance!
                val updatedHunger = if (progress.petType.isNotEmpty() && !progress.isPetDead) maxOf(0, progress.petHunger - 10) else progress.petHunger
                val updatedHappiness = if (progress.petType.isNotEmpty() && !progress.isPetDead) maxOf(0, progress.petHappiness - 10) else progress.petHappiness
                val isPetDeadNow = progress.petType.isNotEmpty() && updatedHunger <= 0
                
                repository.updateProgress(progress.copy(
                    lives = newLives,
                    lastLifeRestoreTime = newRestoreTime,
                    petHunger = updatedHunger,
                    petHappiness = updatedHappiness,
                    isPetDead = isPetDeadNow
                ))
                
                if (newLives <= 0) {
                    // Kick out of full screen challenges and show warning dialog
                    activeLesson = null
                    lessonRitualActive = false
                    selectedGameLevel = null
                    showOutOfLivesDialog = true
                    currentTab = 5 // Direct to Temple Mayor
                }
            } else {
                activeLesson = null
                lessonRitualActive = false
                selectedGameLevel = null
                showOutOfLivesDialog = true
                currentTab = 5
            }
        }
    }

    fun refreshLivesAndPetStatus() {
        viewModelScope.launch {
            val progress = repository.getProgress() ?: return@launch
            val now = System.currentTimeMillis()
            var updated = progress
            
            // 1. Lives Regeneration (1 life every 10 minutes)
            if (progress.lives < 5) {
                val lastRestore = progress.lastLifeRestoreTime
                if (lastRestore > 0) {
                    val elapsed = now - lastRestore
                    val interval = 10 * 60 * 1000 // 10 min
                    val restored = (elapsed / interval).toInt()
                    if (restored > 0) {
                        val newLives = minOf(5, progress.lives + restored)
                        val newRestoreTime = if (newLives == 5) 0L else lastRestore + (restored * interval)
                        updated = updated.copy(
                            lives = newLives,
                            lastLifeRestoreTime = newRestoreTime
                        )
                    }
                } else {
                    updated = updated.copy(lastLifeRestoreTime = now)
                }
            }
            
            // 2. Pet Hunger / Happiness Decay (3 hunger, 2 happiness per hour) & 3-Day Neglect Removal
            if (progress.petType.isNotEmpty()) {
                val lastCare = progress.petLastCaredTime
                if (lastCare > 0) {
                    val elapsed = now - lastCare
                    val threeDaysMs = 3L * 24 * 60 * 60 * 1000 // 3 days
                    if (elapsed > threeDaysMs) {
                        // Penalty: Completely remove mascot from the game
                        updated = updated.copy(
                            petType = "",
                            petName = "",
                            petHunger = 100,
                            petHappiness = 100,
                            isPetDead = false,
                            petAccessories = "",
                            petLastCaredTime = 0L
                        )
                        petErrorMessage = "RETIRADO: Al no recibir cuidados por más de 3 días, tu compañero ha regresado a la selva del Anáhuac. Se ha removido del juego."
                    } else if (!progress.isPetDead) {
                        val hrInterval = 60 * 60 * 1000 // 1 hour
                        val hours = (elapsed / hrInterval).toInt()
                        if (hours > 0) {
                            val hungerDecay = hours * 3
                            val happinessDecay = hours * 2
                            val newHunger = maxOf(0, progress.petHunger - hungerDecay)
                            val newHappiness = maxOf(0, progress.petHappiness - happinessDecay)
                            val isDead = newHunger <= 0 || newHappiness <= 0
                            
                            updated = updated.copy(
                                petHunger = newHunger,
                                petHappiness = newHappiness,
                                isPetDead = isDead,
                                petLastCaredTime = lastCare + (hours * hrInterval)
                            )
                        }
                    }
                } else {
                    updated = updated.copy(petLastCaredTime = now)
                }
            }
            
            if (updated != progress) {
                repository.updateProgress(updated)
            }
        }
    }

    fun buyPet(type: String, name: String, cost: Int) {
        viewModelScope.launch {
            val progress = repository.getProgress() ?: return@launch
            if (progress.xp >= cost) {
                repository.updateProgress(progress.copy(
                    xp = progress.xp - cost,
                    petType = type,
                    petName = name.ifBlank { type },
                    petHunger = 100,
                    petHappiness = 100,
                    isPetDead = false,
                    petAccessories = "",
                    petLastCaredTime = System.currentTimeMillis()
                ))
                petSuccessMessage = "¡Felicidades! Has adoptado a un sagrado $type de nombre $name."
            } else {
                petErrorMessage = "No tienes suficiente XP (necesitas $cost XP)."
            }
        }
    }

    fun feedPet(foodName: String, cost: Int, hungerBoost: Int, happinessBoost: Int) {
        // Delegate for compatibility
        val type = when {
            foodName.contains("Maíz") -> "Maíz"
            foodName.contains("Tamal") -> "Tamal"
            else -> "Cacao"
        }
        viewModelScope.launch {
            val progress = repository.getProgress() ?: return@launch
            if (progress.xp >= cost) {
                repository.updateProgress(progress.copy(xp = progress.xp - cost))
                when (type) {
                    "Maíz" -> inventoryMaizCount++
                    "Tamal" -> inventoryTamalCount++
                    "Cacao" -> inventoryCacaoCount++
                }
                feedPetFromInventory(type)
            } else {
                petErrorMessage = "No tienes suficiente XP para comprar $foodName."
            }
        }
    }

    fun buyFoodToInventory(foodType: String, cost: Int) {
        viewModelScope.launch {
            val progress = repository.getProgress() ?: return@launch
            if (progress.petType.isEmpty() || progress.isPetDead) {
                petErrorMessage = "Necesitas una mascota viva para comprarle alimento."
                return@launch
            }
            if (progress.xp >= cost) {
                repository.updateProgress(progress.copy(xp = progress.xp - cost))
                when (foodType) {
                    "Maíz" -> {
                        inventoryMaizCount++
                        petSuccessMessage = "¡Has comprado Maíz Sagrado! Guardado en mochila. 🌽"
                    }
                    "Tamal" -> {
                        inventoryTamalCount++
                        petSuccessMessage = "¡Has comprado Tamal Sagrado! Guardado en mochila. 🫔"
                    }
                    "Cacao" -> {
                        inventoryCacaoCount++
                        petSuccessMessage = "¡Has comprado Cacao Ceremonial! Guardado en mochila. ☕"
                    }
                }
            } else {
                petErrorMessage = "No tienes suficiente XP (necesitas $cost XP)."
            }
        }
    }

    fun feedPetFromInventory(foodType: String) {
        viewModelScope.launch {
            val progress = repository.getProgress() ?: return@launch
            if (progress.petType.isEmpty() || progress.isPetDead) {
                petErrorMessage = "No tienes un compañero vivo activo."
                return@launch
            }
            if (isFeedingAnimating) return@launch

            var hasItem = false
            var hungerBoost = 0
            var happinessBoost = 0
            var reaction = ""
            
            when (foodType) {
                "Maíz" -> {
                    if (inventoryMaizCount > 0) {
                        hasItem = true
                        hungerBoost = 25
                        happinessBoost = 10
                        reaction = "¡Yum! ¡El sagrado Centli (Maíz) me llena de la energía del sol! 🌽"
                    }
                }
                "Tamal" -> {
                    if (inventoryTamalCount > 0) {
                        hasItem = true
                        hungerBoost = 50
                        happinessBoost = 20
                        reaction = "¡Mmm! ¡Qué tamal tan esponjoso y sagrado! Me siento súper fuerte. 🫔"
                    }
                }
                "Cacao" -> {
                    if (inventoryCacaoCount > 0) {
                        hasItem = true
                        hungerBoost = 35
                        happinessBoost = 35
                        reaction = "¡Ah, el cacao de los dioses! Mi mente brilla y mi espíritu baila. ☕"
                    }
                }
            }
            
            if (hasItem) {
                feedingFoodItem = foodType
                isFeedingAnimating = true
                kotlinx.coroutines.delay(1000)

                when (foodType) {
                    "Maíz" -> inventoryMaizCount--
                    "Tamal" -> inventoryTamalCount--
                    "Cacao" -> inventoryCacaoCount--
                }

                val newHunger = minOf(100, progress.petHunger + hungerBoost)
                val newHappiness = minOf(100, progress.petHappiness + happinessBoost)
                repository.updateProgress(progress.copy(
                    petHunger = newHunger,
                    petHappiness = newHappiness,
                    petLastCaredTime = System.currentTimeMillis()
                ))
                petBounceState = true
                petSpeechBubble = reaction
                petSuccessMessage = "Le has dado de comer a ${progress.petName}."

                kotlinx.coroutines.delay(1200)
                isFeedingAnimating = false
                feedingFoodItem = null
            } else {
                petErrorMessage = "No tienes $foodType en tu mochila. ¡Cómpralo en la tienda primero!"
            }
        }
    }

    fun toggleEquipAccessory(item: String) {
        viewModelScope.launch {
            val progress = repository.getProgress() ?: return@launch
            if (progress.petType.isEmpty() || progress.isPetDead) {
                petErrorMessage = "No tienes una mascota viva."
                return@launch
            }
            val currentAccs = progress.petAccessories.split(",").filter { it.isNotBlank() }.toSet()
            if (!currentAccs.contains(item)) {
                petErrorMessage = "Primero debes comprar este accesorio en el armario."
                return@launch
            }
            
            val equipping = !activeEquippedAccessories.contains(item)
            activeEquippedAccessories = if (equipping) {
                activeEquippedAccessories + item
            } else {
                activeEquippedAccessories - item
            }
            
            if (equipping) {
                petAccessoryEquippedFlash = item
                petRotationState = if (petRotationState == 0f) 720f else 0f
                petSpeechBubble = when (item) {
                    "Penacho Sagrado" -> "¡Mírame! ¿Acaso no parezco un emperador de Tenochtitlan? 👑"
                    "Capa de Manta" -> "Me siento abrigadito y majestuoso con mi capa tradicional. 🧥"
                    else -> "¡Brillo con la pureza y el valor del precioso Jade! 📿"
                }
                kotlinx.coroutines.delay(1200)
                petAccessoryEquippedFlash = null
            } else {
                petRotationState = if (petRotationState == 0f) -360f else 0f
                petSpeechBubble = "Me he quitado el $item."
            }
        }
    }

    fun buyAccessory(item: String, cost: Int) {
        viewModelScope.launch {
            val progress = repository.getProgress() ?: return@launch
            if (progress.petType.isEmpty() || progress.isPetDead) {
                petErrorMessage = "Necesitas una mascota viva para comprarle accesorios."
                return@launch
            }
            val currentAcc = progress.petAccessories.split(",").filter { it.isNotBlank() }.toSet()
            if (currentAcc.contains(item)) {
                petErrorMessage = "Ya has comprado este accesorio para tu mascota."
                return@launch
            }
            if (progress.xp >= cost) {
                val newAcc = (currentAcc + item).joinToString(",")
                val newHappiness = minOf(100, progress.petHappiness + 25)
                repository.updateProgress(progress.copy(
                    xp = progress.xp - cost,
                    petAccessories = newAcc,
                    petHappiness = newHappiness,
                    petLastCaredTime = System.currentTimeMillis()
                ))
                petSuccessMessage = "Has vestido a tu mascota con $item. ¡Se ve espectacular y majestuosa!"
            } else {
                petErrorMessage = "No tienes suficiente XP para comprar $item."
            }
        }
    }

    fun playWithPet() {
        viewModelScope.launch {
            val progress = repository.getProgress() ?: return@launch
            if (progress.petType.isEmpty() || progress.isPetDead) {
                petErrorMessage = "No tienes una mascota activa."
                return@launch
            }
            val newHappiness = minOf(100, progress.petHappiness + 15)
            repository.updateProgress(progress.copy(
                petHappiness = newHappiness,
                petLastCaredTime = System.currentTimeMillis()
            ))
            petSuccessMessage = "Has acariciado y jugado con ${progress.petName}. ¡Mueve su colita alegremente!"
        }
    }

    fun recoverPet() {
        viewModelScope.launch {
            val progress = repository.getProgress() ?: return@launch
            if (progress.petType.isEmpty() || !progress.isPetDead) {
                petErrorMessage = "Tu mascota no ha fallecido ni está perdida."
                return@launch
            }
            if (progress.xp >= 500) {
                repository.updateProgress(progress.copy(
                    xp = progress.xp - 500,
                    isPetDead = false,
                    petHunger = 100,
                    petHappiness = 100,
                    petLastCaredTime = System.currentTimeMillis()
                ))
                petSuccessMessage = "¡Ritual de Resurrección completado! Has rescatado a ${progress.petName} del Mictlán. Ha vuelto a la vida."
            } else {
                petErrorMessage = "No tienes los 500 XP necesarios para la ofrenda de resurrección."
            }
        }
    }

    fun performSacrifice(type: String) {
        viewModelScope.launch {
            val progress = repository.getProgress() ?: return@launch
            var cost = 0
            var shieldGained = 0
            var successText = ""
            
            when (type) {
                "Codorniz" -> {
                    cost = 100
                    shieldGained = 0
                    successText = "Has ofrecido una Codorniz Sagrada en el Templo Mayor. Los sacerdotes entonan cantos y los dioses restauran tus 5 vidas."
                }
                "Guajolote" -> {
                    cost = 180
                    shieldGained = 1
                    successText = "Has ofrecido un Guajolote Ritual. Tlaloc sonríe desde el Tlalocan; tus vidas se restauran y recibes 1 Escudo Divino contra errores."
                }
                "Ofrenda_Xolo" -> {
                    cost = 300
                    shieldGained = 3
                    successText = "Has presentado un Xoloitzcuintli de Ofrenda. Los sacerdotes derraman copal; recibes 5 vidas y 3 Escudos Divinos de protección."
                }
                "Mascota" -> {
                    if (progress.petType.isEmpty() || progress.isPetDead) {
                        petErrorMessage = "No tienes una mascota viva para realizar este supremo sacrificio."
                        return@launch
                    }
                    cost = 0
                    shieldGained = 5
                    successText = "En un acto de devoción absoluta, entregas a tu mascota ${progress.petName} como ofrenda en el Templo Mayor. El cielo se tiñe de fuego; Huitzilopochtli te otorga 5 vidas, 5 Escudos Divinos y la Bendición Suprema del Sol."
                }
            }
            
            if (cost > 0 && progress.xp < cost) {
                petErrorMessage = "No tienes suficiente XP para comprar este animal de ofrenda (necesitas $cost XP)."
                return@launch
            }
            
            // Set active ceremony to show overlay
            activeSacrificeCeremony = type
            
            // Stage 1: Preparation (dark screen, priest approaching, drums beating)
            sacrificeAnimationStep = 1
            kotlinx.coroutines.delay(2000)
            
            // Stage 2: The Sacred Stroke (obsidian flash, screen shake, fire explosion)
            sacrificeAnimationStep = 2
            kotlinx.coroutines.delay(1800)
            
            // Stage 3: Spirit Ascension (glowing soul floats up and is carried away by divine creature)
            sacrificeAnimationStep = 3
            kotlinx.coroutines.delay(2600)
            
            // Stage 4: Divine Blessing (glowing golden light refracts down, filling hearts & shield)
            sacrificeAnimationStep = 4
            
            val newLives = 5
            val updatedXp = progress.xp - cost
            
            val updatedProgress = if (type == "Mascota") {
                progress.copy(
                    xp = updatedXp,
                    lives = newLives,
                    activeShieldCharges = progress.activeShieldCharges + shieldGained,
                    petType = "",
                    petName = "",
                    petHunger = 0,
                    petHappiness = 0,
                    petAccessories = "",
                    isPetDead = false
                )
            } else {
                progress.copy(
                    xp = updatedXp,
                    lives = newLives,
                    activeShieldCharges = progress.activeShieldCharges + shieldGained
                )
            }
            
            repository.updateProgress(updatedProgress)
            petSuccessMessage = successText
            
            kotlinx.coroutines.delay(2500)
            
            // Finish and dismiss the overlay
            sacrificeAnimationStep = 0
            activeSacrificeCeremony = null
        }
    }
}

// --- Factory Provider for ViewModel ---
class MainViewModelFactory(private val application: Application) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MainViewModel(application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
