package com.example.data

import android.content.Context
import android.media.MediaPlayer
import android.os.Handler
import android.os.Looper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.File
import java.io.FileOutputStream
import java.security.MessageDigest
import java.util.concurrent.TimeUnit

/**
 * Configuration for XTTS-v2 Voice Cloning & Synthesis Server
 */
data class XttsConfig(
    val isEnabled: Boolean = true,
    val serverUrl: String = "https://coqui-xtts.hf.space/tts_to_audio",
    val apiKey: String = "",
    val speakerProfile: String = SACERDOTE_PERMANENT_VOICE,
    val speakerWavUrl: String = "",
    val useLocalSacerdoteSample: Boolean = true,
    val isVoiceLocked: Boolean = true,
    val language: String = "es",
    val speed: Float = 0.95f,
    val temperature: Float = 0.70f
) {
    companion object {
        const val SACERDOTE_PERMANENT_VOICE = "🛕 Sacerdote del Templo (Víctor Florencio)"
    }
}

/**
 * Client to connect to XTTS (Coqui XTTS-v2 / FastSpeech / Hugging Face Spaces / Custom API)
 * Synthesizes voice remotely, caches audio files locally, and plays with MediaPlayer.
 */
class XttsAudioClient(private val context: Context) {

    private val httpClient = OkHttpClient.Builder()
        .protocols(listOf(okhttp3.Protocol.HTTP_1_1))
        .connectTimeout(25, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(25, TimeUnit.SECONDS)
        .retryOnConnectionFailure(true)
        .build()

    private var mediaPlayer: MediaPlayer? = null
    private val mainHandler = Handler(Looper.getMainLooper())

    var isSynthesizing: Boolean = false
        private set
    var isPlaying: Boolean = false
        private set
    var lastError: String? = null
        private set

    fun normalizeServerUrl(rawUrl: String): String {
        var url = rawUrl.trim().replace("\\s+".toRegex(), "")
        if (url.isBlank()) return url

        // Handle pasting Hugging Face Space page URL: https://huggingface.co/spaces/<owner>/<name>
        val hfSpaceRegex = Regex("https?://huggingface\\.co/spaces/([a-zA-Z0-9_.-]+)/([a-zA-Z0-9_.-]+)/?")
        val match = hfSpaceRegex.find(url)
        if (match != null) {
            val owner = match.groupValues[1].replace("_", "-").lowercase()
            val spaceName = match.groupValues[2].replace("_", "-").lowercase()
            return "https://$owner-$spaceName.hf.space/tts_to_audio"
        }

        // If without scheme, prepend https://
        if (!url.startsWith("http://") && !url.startsWith("https://")) {
            url = "https://$url"
        }

        // If HF Space root without path: append /tts_to_audio
        if (url.contains(".hf.space") && !url.substringAfter(".hf.space").contains("/")) {
            url = url.trimEnd('/') + "/tts_to_audio"
        }

        return url
    }

    suspend fun checkServerHealth(rawUrl: String, apiKey: String): Pair<Boolean, String> = withContext(Dispatchers.IO) {
        val trimmed = rawUrl.trim()
        if (trimmed.isBlank()) return@withContext Pair(false, "URL vacía. Ingresa la URL de tu Space.")
        val targetUrl = normalizeServerUrl(trimmed)
        try {
            val rootUrl = if (targetUrl.contains(".hf.space")) {
                targetUrl.substringBefore(".hf.space") + ".hf.space"
            } else {
                targetUrl
            }
            val reqBuilder = Request.Builder()
                .url(rootUrl)
                .header("User-Agent", "Navegante-Android/1.0")
                .header("Accept", "*/*")
                .get()

            if (apiKey.isNotBlank()) {
                val token = apiKey.trim()
                val authHeader = if (token.startsWith("Bearer ", ignoreCase = true)) token else "Bearer $token"
                reqBuilder.addHeader("Authorization", authHeader)
            }

            val res = httpClient.newCall(reqBuilder.build()).execute()
            val body = try { res.body?.string() ?: "" } catch (_: Exception) { "" }

            if (res.isSuccessful) {
                Pair(true, "🟢 Space activo y respondiendo correctamente (HTTP ${res.code})")
            } else if (res.code == 503 || res.code == 505 || body.contains("space is in error", ignoreCase = true) || body.contains("sleeping", ignoreCase = true)) {
                Pair(false, "🟠 Space dormido o pausado (HTTP ${res.code}). Ve a huggingface.co/spaces para 'Restart Space'.")
            } else {
                Pair(false, "Aviso del Space (HTTP ${res.code}): ${res.message}")
            }
        } catch (e: Exception) {
            Pair(false, "🔴 No se pudo conectar: ${e.localizedMessage ?: "Error de red"}")
        }
    }

    fun getSacerdoteSampleBase64(): String? {
        return try {
            val bytes = try {
                context.assets.open("voz_sacerdote_sample.wav").use { it.readBytes() }
            } catch (_: Exception) {
                context.resources.openRawResource(com.example.R.raw.voz_sacerdote_sample).use { it.readBytes() }
            }
            android.util.Base64.encodeToString(bytes, android.util.Base64.NO_WRAP)
        } catch (_: Exception) {
            null
        }
    }

    fun playLocalResource(
        rawResId: Int,
        onStart: () -> Unit = {},
        onCompletion: () -> Unit = {}
    ) {
        try {
            stopAudio()
            mediaPlayer = MediaPlayer.create(context, rawResId)?.apply {
                setOnCompletionListener {
                    this@XttsAudioClient.isPlaying = false
                    onCompletion()
                }
                setOnErrorListener { _, _, _ ->
                    this@XttsAudioClient.isPlaying = false
                    false
                }
                this@XttsAudioClient.isPlaying = true
                onStart()
                start()
            }
        } catch (e: Exception) {
            isPlaying = false
            lastError = e.localizedMessage
        }
    }

    /**
     * Synthesizes audio using remote XTTS server or retrieves from local cache, then plays it.
     * Returns true if audio played successfully, false if fallback to native TTS is needed.
     */
    suspend fun synthesizeAndPlay(
        text: String,
        config: XttsConfig,
        onStart: () -> Unit = {},
        onCompletion: () -> Unit = {},
        onError: (String) -> Unit = {},
        onAudioCached: ((File) -> Unit)? = null
    ): Boolean = withContext(Dispatchers.IO) {
        if (!config.isEnabled || config.serverUrl.isBlank() || text.isBlank()) {
            return@withContext false
        }

        try {
            isSynthesizing = true
            lastError = null

            // 1. Check local cache first to avoid re-generating identical audio
            val cacheFile = getCacheFileForText(text, config)
            if (cacheFile.exists() && cacheFile.length() > 0) {
                isSynthesizing = false
                playAudioFile(cacheFile, onStart, onCompletion)
                return@withContext true
            }

            val targetUrl = normalizeServerUrl(config.serverUrl)

            // 2. Prepare JSON payload with Victor's voice sample permanently embedded
            val priestBase64 = getSacerdoteSampleBase64()
            val jsonPayload = JSONObject().apply {
                put("text", text)
                put("language", config.language)
                put("speed", config.speed)
                put("temperature", config.temperature)
                put("speaker_id", "sacerdote_victor")
                put("speaker_name", XttsConfig.SACERDOTE_PERMANENT_VOICE)

                if (priestBase64 != null) {
                    put("speaker_wav", "data:audio/wav;base64,$priestBase64")
                    put("speaker_wav_base64", priestBase64)
                    put("audio_base64", priestBase64)
                }

                // Gradio payload compatibility (data array with [text, audio_data])
                val gradioDataArray = org.json.JSONArray().apply {
                    put(text)
                    if (priestBase64 != null) put("data:audio/wav;base64,$priestBase64")
                }
                put("data", gradioDataArray)
            }

            val mediaType = "application/json; charset=utf-8".toMediaType()
            val requestBody = jsonPayload.toString().toRequestBody(mediaType)

            val requestBuilder = Request.Builder()
                .url(targetUrl)
                .header("User-Agent", "Navegante-Android/1.0 (Linux; Android)")
                .header("Accept", "*/*")
                .post(requestBody)

            if (config.apiKey.isNotBlank()) {
                val token = config.apiKey.trim()
                val authHeader = if (token.startsWith("Bearer ", ignoreCase = true)) token else "Bearer $token"
                requestBuilder.addHeader("Authorization", authHeader)
                requestBuilder.addHeader("x-hf-token", token.removePrefix("Bearer ").trim())
            }

            val response = httpClient.newCall(requestBuilder.build()).execute()
            if (!response.isSuccessful) {
                val responseBodyStr = try { response.body?.string() } catch (_: Exception) { "" }
                val errorMsg = when (response.code) {
                    503, 505 -> {
                        if (targetUrl.contains(".hf.space") || responseBodyStr?.contains("space", ignoreCase = true) == true) {
                            "El Space de Hugging Face está dormido o en pausa (Error ${response.code}). Entra a huggingface.co para reiniciarlo ('Restart Space')."
                        } else {
                            "Error de protocolo o servidor no disponible (HTTP ${response.code})."
                        }
                    }
                    401, 403 -> "Acceso denegado (HTTP ${response.code}). Verifica tu Hugging Face Token (hf_...)."
                    404 -> "Endpoint no encontrado (HTTP 404). Verifica la ruta de tu Space."
                    else -> "XTTS Server HTTP ${response.code}: ${response.message}"
                }
                lastError = errorMsg
                isSynthesizing = false
                withContext(Dispatchers.Main) { onError(errorMsg) }
                return@withContext false
            }

            val responseBody = response.body ?: run {
                lastError = "Respuesta vacía del servidor XTTS"
                isSynthesizing = false
                return@withContext false
            }

            cacheFile.parentFile?.mkdirs()

            // 3. Process Response: Direct Audio Stream OR JSON payload with audio URL / Base64
            val contentType = response.header("Content-Type", "")?.lowercase() ?: ""
            if (contentType.contains("json")) {
                val jsonStr = responseBody.string()
                var audioExtracted = false
                try {
                    val jsonObj = JSONObject(jsonStr)
                    val base64Data = when {
                        jsonObj.has("audio_base64") -> jsonObj.getString("audio_base64")
                        jsonObj.has("audio") -> jsonObj.getString("audio")
                        else -> null
                    }
                    if (!base64Data.isNullOrBlank()) {
                        val cleanB64 = base64Data.substringAfter("base64,")
                        val decodedBytes = android.util.Base64.decode(cleanB64, android.util.Base64.DEFAULT)
                        cacheFile.writeBytes(decodedBytes)
                        audioExtracted = true
                    } else {
                        // Check for URL in JSON (e.g. Gradio response {"data": [{"url": "..."}]})
                        val audioUrl = when {
                            jsonObj.has("url") -> jsonObj.getString("url")
                            jsonObj.has("audio_url") -> jsonObj.getString("audio_url")
                            jsonObj.has("data") -> {
                                val dataArr = jsonObj.optJSONArray("data")
                                val firstItem = dataArr?.opt(0)
                                if (firstItem is JSONObject && firstItem.has("url")) firstItem.getString("url")
                                else firstItem?.toString()
                            }
                            else -> null
                        }

                        if (!audioUrl.isNullOrBlank() && audioUrl.startsWith("http")) {
                            val audioReq = Request.Builder().url(audioUrl).build()
                            val audioRes = httpClient.newCall(audioReq).execute()
                            if (audioRes.isSuccessful && audioRes.body != null) {
                                FileOutputStream(cacheFile).use { out ->
                                    audioRes.body!!.byteStream().copyTo(out)
                                }
                                audioExtracted = true
                            }
                        }
                    }
                } catch (_: Exception) {}

                if (!audioExtracted) {
                    lastError = "No se pudo extraer audio de la respuesta JSON"
                    isSynthesizing = false
                    withContext(Dispatchers.Main) { onError(lastError ?: "Error") }
                    return@withContext false
                }
            } else {
                // Raw audio binary bytes
                FileOutputStream(cacheFile).use { output ->
                    responseBody.byteStream().copyTo(output)
                }
            }

            isSynthesizing = false

            // Notify Room audio cache manager
            onAudioCached?.invoke(cacheFile)

            // 4. Play downloaded audio on Main thread
            playAudioFile(cacheFile, onStart, onCompletion)
            return@withContext true

        } catch (e: Exception) {
            lastError = e.localizedMessage ?: "Error de conexión con XTTS"
            isSynthesizing = false
            withContext(Dispatchers.Main) {
                onError(lastError ?: "Error desconocido")
            }
            return@withContext false
        }
    }

    suspend fun playAudioFile(
        file: File,
        onStart: () -> Unit = {},
        onCompletion: () -> Unit = {}
    ) = withContext(Dispatchers.Main) {
        try {
            stopAudio()
            mediaPlayer = MediaPlayer().apply {
                setDataSource(file.absolutePath)
                setOnPreparedListener { mp ->
                    this@XttsAudioClient.isPlaying = true
                    onStart()
                    mp.start()
                }
                setOnCompletionListener {
                    this@XttsAudioClient.isPlaying = false
                    onCompletion()
                }
                setOnErrorListener { _, _, _ ->
                    this@XttsAudioClient.isPlaying = false
                    false
                }
                prepareAsync()
            }
        } catch (e: Exception) {
            this@XttsAudioClient.isPlaying = false
            lastError = e.localizedMessage
        }
    }

    fun stopAudio() {
        try {
            mediaPlayer?.let {
                if (it.isPlaying) {
                    it.stop()
                }
                it.reset()
                it.release()
            }
            mediaPlayer = null
            isPlaying = false
        } catch (_: Exception) {}
    }

    fun getCacheFileForText(text: String, config: XttsConfig): File {
        val cacheDir = File(context.cacheDir, "xtts_audio")
        val hash = hashString("$text|${config.speakerProfile}|${config.language}")
        return File(cacheDir, "xtts_$hash.wav")
    }

    fun hashString(input: String): String {
        val bytes = MessageDigest.getInstance("MD5").digest(input.toByteArray())
        return bytes.joinToString("") { "%02x".format(it) }
    }
}
