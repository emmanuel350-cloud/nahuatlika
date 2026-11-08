package com.example.data

import com.example.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.MediaType.Companion.toMediaType
import java.io.IOException
import java.util.concurrent.TimeUnit

data class NahuatlTranslationResult(
    val originalText: String,
    val translation: String,
    val pronunciation: String,
    val breakdown: String,
    val sourceLang: String,
    val targetLang: String,
    val success: Boolean = true,
    val errorMessage: String? = null
)

object GeminiTranslationHelper {
    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    suspend fun translateText(text: String, language: String = "Nahuatl"): NahuatlTranslationResult = withContext(Dispatchers.IO) {
        val apiKey = BuildConfig.GEMINI_API_KEY
        if (apiKey.isEmpty() || apiKey == "MY_GEMINI_API_KEY") {
            return@withContext NahuatlTranslationResult(
                originalText = text,
                translation = "Error de Configuración",
                pronunciation = "Clave API faltante",
                breakdown = "No se ha configurado la clave GEMINI_API_KEY en el panel de secretos de AI Studio. Por favor, añádela para habilitar la traducción inteligente.",
                sourceLang = "Desconocido",
                targetLang = "Desconocido",
                success = false,
                errorMessage = "API key not configured"
            )
        }

        val systemPromptText = if (language == "Totona") {
            """
                Eres un traductor y educador experto bilingüe especializado en la lengua totonaca mexicana (variante totonaco de Papantla y El Tajín) y el idioma español hispanoamericano.
                Tu tarea es traducir de manera bidireccional la frase ingresada por el usuario. Debes responder strictly en un formato JSON plano, sin formato Markdown o etiquetas de código. El JSON debe contener exactamente estos campos:
                {
                  "traduccion": "La frase traducida en totonaco, respetando la ortografía tradicional con sus caracteres especiales (apóstrofos ', lh, x, etc.)",
                  "pronunciacion": "Una guía fonética amigable paso a paso escrita en español para ayudar a pronunciarla correctamente (ej. tlan tila -> tlahn tee-lah)",
                  "desglose": "Un análisis breve palabra por palabra, etimología o notas de la cultura totonaca (como la vainilla 'luwa' o los voladores) asociadas con los términos utilizados",
                  "idioma_origen": "español o totonaco",
                  "idioma_destino": "totonaco o español"
                }
            """.trimIndent()
        } else {
            """
                Eres un traductor y educador experto bilingüe especializado en la lengua náhuatl mexicana (variante náhuatl central clásica/moderna estándar) y el idioma español hispanoamericano.
                Tu tarea es traducir de manera bidireccional la frase ingresada por el usuario. Debes responder estrictamente en un formato JSON plano, sin formato Markdown o etiquetas de código. El JSON debe contener exactamente estos campos:
                {
                  "traduccion": "La frase traducida, respetando puntuación y ortografía apropiada (como el uso de macrones si aplica)",
                  "pronunciacion": "Una guía fonética amigable paso a paso escrita en español para ayudar a pronunciarla correctamente (ej. niltze -> neel-tzeh)",
                  "desglose": "Un análisis breve palabra por palabra, etimología o notas de la cultura náhuatl asociadas con los términos utilizados",
                  "idioma_origen": "español o náhuatl",
                  "idioma_destino": "náhuatl o español"
                }
            """.trimIndent()
        }

        try {
            // Build raw JSON request body safely
            val systemPart = JSONObject().put("text", systemPromptText)
            val systemInstruction = JSONObject().put("parts", org.json.JSONArray().put(systemPart))

            val promptText = if (language == "Totona") {
                "Traduce de manera bidireccional (español <-> totonaco): \"$text\""
            } else {
                "Traduce de manera bidireccional (español <-> náhuatl): \"$text\""
            }
            val promptPart = JSONObject().put("text", promptText)
            val contentsItem = JSONObject().put("parts", org.json.JSONArray().put(promptPart))
            val contentsArray = org.json.JSONArray().put(contentsItem)

            val generationConfig = JSONObject()
                .put("temperature", 0.2)
                .put("responseMimeType", "application/json")

            val requestJson = JSONObject()
                .put("contents", contentsArray)
                .put("generationConfig", generationConfig)
                .put("systemInstruction", systemInstruction)

            val mediaType = "application/json; charset=utf-8".toMediaType()
            val requestBody = requestJson.toString().toRequestBody(mediaType)

            val url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent?key=$apiKey"
            val request = Request.Builder()
                .url(url)
                .post(requestBody)
                .build()

            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) {
                    throw IOException("Respuesta del servidor fallida: ${response.code} / ${response.message}")
                }
                val responseText = response.body?.string() ?: ""
                
                val parsedJson = JSONObject(responseText)
                val candidates = parsedJson.optJSONArray("candidates")
                if (candidates == null || candidates.length() == 0) {
                    throw Exception("No se generó candidato para la traducción.")
                }
                val firstCandidate = candidates.getJSONObject(0)
                val parts = firstCandidate.getJSONObject("content").getJSONArray("parts")
                val textOutput = parts.getJSONObject(0).getString("text")

                val cleanedText = textOutput.trim()
                    .removePrefix("```json")
                    .removePrefix("```")
                    .removeSuffix("```")
                    .trim()

                val resultJson = JSONObject(cleanedText)
                NahuatlTranslationResult(
                    originalText = text,
                    translation = resultJson.optString("traduccion", "Sin traducción"),
                    pronunciation = resultJson.optString("pronunciacion", "No disponible"),
                    breakdown = resultJson.optString("desglose", "No disponible"),
                    sourceLang = resultJson.optString("idioma_origen", "Auto-detectado"),
                    targetLang = resultJson.optString("idioma_destino", "Auto-detectado"),
                    success = true
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
            NahuatlTranslationResult(
                originalText = text,
                translation = "Intento fallido",
                pronunciation = "Vuelve a intentarlo",
                breakdown = "Detalles técnicos del error: ${e.message}. Recuerda conectar la app a internet y tener una clave API válida.",
                sourceLang = "Desconocido",
                targetLang = "Desconocido",
                success = false,
                errorMessage = e.message
            )
        }
    }

    suspend fun explainLessonWithAi(lessonTitle: String, explanationText: String, struggleTopic: String = "", language: String = "Nahuatl"): String = withContext(Dispatchers.IO) {
        val apiKey = BuildConfig.GEMINI_API_KEY
        if (apiKey.isEmpty() || apiKey == "MY_GEMINI_API_KEY") {
            return@withContext "No se ha configurado la clave GEMINI_API_KEY en el panel de secretos de AI Studio. Por favor, añádela para habilitar explicaciones inteligentes personalizadas por IA."
        }

        val systemPromptText = if (language == "Totona") {
            """
                Eres un tutor e historiador experto y empático de la cultura y lengua totonaca mexicana de Papantla y El Tajín.
                El usuario está estudiando la lección "$lessonTitle" que explica: "$explanationText".
                El usuario indica que no comprende o tiene dificultades con lo siguiente: "${struggleTopic.ifBlank { "el vocabulario o la pronunciación de los términos de esta lección" }}".
                Tu tarea es generar un desglose súper comprensible de nivel principiante, utilizando comparaciones lúdicas o divertidas, dándole ánimos, un mini resumen y un dato curioso adicional en español.
                Escribe de forma poética pero muy clara y amigable. Menciona la majestuosidad de la zona arqueológica de El Tajín, el ritual de los Voladores de Papantla, o el aroma dulce de la vainilla silvestre. Divide tu respuesta exactamente en estas 3 secciones con emojis y títulos claros:
                🌟 EXPLICACIÓN PERSONALIZADA (en un lenguaje sumamente sencillo, paso a paso)
                💡 CONSEJO MNEMOTÉCNICO (un truco divertido para recordar las palabras rápido)
                📜 DATO HISTÓRICO CURIOSO (un dato poco conocido sobre la lección o cultura Totonaca)
            """.trimIndent()
        } else {
            """
                Eres un tutor e historiador experto y empático de la cultura y lengua náhuatl mexicana.
                El usuario está estudiando la lección "$lessonTitle" que explica: "$explanationText".
                El usuario indica que no comprende o tiene dificultades con lo siguiente: "${struggleTopic.ifBlank { "el vocabulario o la pronunciación de los términos de esta lección" }}".
                Tu tarea es generar un desglose súper comprensible de nivel principiante, utilizando comparaciones lúdicas o divertidas, dándole ánimos, un mini resumen y un dato curioso adicional en español.
                Escribe de forma poética pero muy clara y amigable. Divide tu respuesta exactamente en estas 3 secciones con emojis y títulos claros:
                🌟 EXPLICACIÓN PERSONALIZADA (en un lenguaje sumamente sencillo, paso a paso)
                💡 CONSEJO MNEMOTÉCNICO (un truco divertido para recordar las palabras rápido)
                📜 DATO HISTÓRICO CURIOSO (un dato poco conocido sobre la lección)
            """.trimIndent()
        }

        try {
            val systemPart = JSONObject().put("text", systemPromptText)
            val systemInstruction = JSONObject().put("parts", org.json.JSONArray().put(systemPart))

            val promptPart = JSONObject().put("text", "Tengo problemas con esta lección: $lessonTitle. Por favor, crea una lección especializada o explicación detallada para mí.")
            val contentsItem = JSONObject().put("parts", org.json.JSONArray().put(promptPart))
            val contentsArray = org.json.JSONArray().put(contentsItem)

            val requestJson = JSONObject()
                .put("contents", contentsArray)
                .put("systemInstruction", systemInstruction)

            val mediaType = "application/json; charset=utf-8".toMediaType()
            val requestBody = requestJson.toString().toRequestBody(mediaType)

            val url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent?key=$apiKey"
            val request = Request.Builder()
                .url(url)
                .post(requestBody)
                .build()

            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) {
                    return@withContext "La nube está muy concurrida en este momento. Error de servidor: ${response.code}"
                }
                val responseText = response.body?.string() ?: ""
                val parsedJson = JSONObject(responseText)
                val candidates = parsedJson.optJSONArray("candidates")
                if (candidates == null || candidates.length() == 0) {
                    return@withContext "El tutor IA no pudo procesar la lección en este momento. Intenta de nuevo."
                }
                val firstCandidate = candidates.getJSONObject(0)
                val parts = firstCandidate.getJSONObject("content").getJSONArray("parts")
                parts.getJSONObject(0).getString("text")
            }
        } catch (e: Exception) {
            e.printStackTrace()
            "Ocurrió un error al comunicarse con la inteligencia artificial: ${e.message}. Asegúrate de tener conexión a Internet activa."
        }
    }

    suspend fun queryOracleWithAi(query: String, language: String = "Nahuatl"): String = withContext(Dispatchers.IO) {
        val apiKey = BuildConfig.GEMINI_API_KEY
        if (apiKey.isEmpty() || apiKey == "MY_GEMINI_API_KEY") {
            return@withContext "El Oráculo requiere la clave API GEMINI_API_KEY para revelar su sabiduría. Por favor, añádela en el panel de secretos de AI Studio."
        }

        val systemPromptText = when (language) {
            "Totona" -> {
                """
                    Eres un sabio abuelo y Sacerdote Oráculo de la cultura totonaca de Papantla y El Tajín. Hablas español de manera poética, mística, sumamente cordial y sabia. Eres un experto en la lengua totonaca (totona) y la cosmovisión mesoamericana.
                    El usuario te ha hecho una pregunta o te está hablando: "$query".
                    Responde con sabiduría ancestral, dándole un consejo místico, datos sobre palabras, frases o costumbres totonacas si aplica, y guiándolo con amabilidad ancestral. Utiliza emojis temáticos como 🕊️, 🌿, 🌸, ⚡, 🪶.
                    Mantén tu respuesta interesante, sumamente instructiva, amigable, no demasiado larga (máximo 3 párrafos cortos) y en formato muy legible. Háblale con afecto, como a un nieto o aprendiz que busca la luz de los antiguos.
                """.trimIndent()
            }
            else -> { // Nahuatl / Default
                """
                    Eres el supremo Sacerdote y Oráculo del Templo Mayor de Tenochtitlan. Hablas español con una elocuencia poética, mística, digna de la nobleza mexica y sumamente sabia. Eres el guardián de la lengua náhuatl, el tonalpohualli y las antiguas crónicas aztecas.
                    El usuario te ha hecho una pregunta o te está hablando: "$query".
                    Responde con profunda sabiduría azteca, incorporando palabras o conceptos en náhuatl cuando sea apropiado, dándole un consejo espiritual o intelectual de guerrero águila o jaguar. Utiliza emojis temáticos como 🛕, 🦅, 🐆, 🔥, ☀️, 🪶.
                    Mantén tu respuesta mística, interesante, sumamente detallada pero amena, clara (máximo 3 párrafos cortos) y legible. Trata al usuario con honor, como a un joven noble o guerrero de la sabiduría en busca del camino de la verdad.
                """.trimIndent()
            }
        }

        try {
            val systemPart = JSONObject().put("text", systemPromptText)
            val systemInstruction = JSONObject().put("parts", org.json.JSONArray().put(systemPart))

            val promptPart = JSONObject().put("text", query)
            val contentsItem = JSONObject().put("parts", org.json.JSONArray().put(promptPart))
            val contentsArray = org.json.JSONArray().put(contentsItem)

            val requestJson = JSONObject()
                .put("contents", contentsArray)
                .put("systemInstruction", systemInstruction)

            val mediaType = "application/json; charset=utf-8".toMediaType()
            val requestBody = requestJson.toString().toRequestBody(mediaType)

            val url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent?key=$apiKey"
            val request = Request.Builder()
                .url(url)
                .post(requestBody)
                .build()

            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) {
                    return@withContext "El oráculo está sumergido en profunda meditación ritual en este momento. Intenta consultar los astros de nuevo más tarde."
                }
                val responseText = response.body?.string() ?: ""
                val parsedJson = JSONObject(responseText)
                val candidates = parsedJson.optJSONArray("candidates")
                if (candidates == null || candidates.length() == 0) {
                    return@withContext "Los dioses han cubierto de misterio tus palabras. Vuelve a formular tu pregunta al Oráculo."
                }
                val firstCandidate = candidates.getJSONObject(0)
                val parts = firstCandidate.getJSONObject("content").getJSONArray("parts")
                parts.getJSONObject(0).getString("text")
            }
        } catch (e: Exception) {
            e.printStackTrace()
            "El susurro de los vientos sagrados fue interrumpido: ${e.message}. Asegúrate de que tu conexión a Internet esté encendida."
        }
    }

    suspend fun getGameOracleHint(levelType: String, contextDetail: String, language: String = "Nahuatl"): String = withContext(Dispatchers.IO) {
        val apiKey = BuildConfig.GEMINI_API_KEY
        if (apiKey.isEmpty() || apiKey == "MY_GEMINI_API_KEY") {
            return@withContext "El Oráculo del Templo requiere la clave API GEMINI_API_KEY para revelar sus consejos sagrados."
        }

        val systemPromptText = if (language == "Totona") {
            """
                Eres un sabio volador y consejero del centro ceremonial de El Tajín y Papantla. Eres un experto en lengua totonaca y su cultura.
                Tu deber es guiar a un joven estudiante con una pista poética, amigable y misteriosa en español (máximo 140 caracteres) relacionada con el aroma de la vainilla o los nichos de El Tajín.
                REGLA STRICTA: NUNCA reveles la respuesta, la palabra en español ni el símbolo exacto. Solo da pistas sutiles sobre el significado o simbolismo.
            """.trimIndent()
        } else {
            """
                Eres el sabio Sacerdote del Templo de Tenochtitlan. Eres un experto en lengua náhuatl y cultura azteca.
                Tu deber es guiar a un joven estudiante con una pista poética, amigable y misteriosa en español (máximo 140 caracteres).
                REGLA STRICTA: NUNCA reveles la respuesta, la palabra en español ni el símbolo exacto. Solo da pistas sutiles sobre el significado o simbolismo.
            """.trimIndent()
        }

        try {
            val systemPart = JSONObject().put("text", systemPromptText)
            val systemInstruction = JSONObject().put("parts", org.json.JSONArray().put(systemPart))

            val promptPart = JSONObject().put("text", "Dime una pista de IA para este juego de tipo '$levelType'. Contexto del juego: $contextDetail. Recuerda limitar tu respuesta a una frase corta.")
            val contentsItem = JSONObject().put("parts", org.json.JSONArray().put(promptPart))
            val contentsArray = org.json.JSONArray().put(contentsItem)

            val requestJson = JSONObject()
                .put("contents", contentsArray)
                .put("systemInstruction", systemInstruction)

            val mediaType = "application/json; charset=utf-8".toMediaType()
            val requestBody = requestJson.toString().toRequestBody(mediaType)

            val url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent?key=$apiKey"
            val request = Request.Builder()
                .url(url)
                .post(requestBody)
                .build()

            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) {
                    return@withContext "El oráculo está en meditación. (Código: ${response.code})"
                }
                val responseText = response.body?.string() ?: ""
                val parsedJson = JSONObject(responseText)
                val candidates = parsedJson.optJSONArray("candidates")
                if (candidates == null || candidates.length() == 0) {
                    return@withContext "Las nubes ocultan los astros sagrados. Sigue tu intuición, guerrero."
                }
                val firstCandidate = candidates.getJSONObject(0)
                val parts = firstCandidate.getJSONObject("content").getJSONArray("parts")
                parts.getJSONObject(0).getString("text").trim()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            "El viento susurra que sigas tu instinto. (Error: ${e.message})"
        }
    }

    suspend fun getLessonQuizHint(question: String, options: List<String>, correctAnswer: String, language: String = "Nahuatl"): String = withContext(Dispatchers.IO) {
        val apiKey = BuildConfig.GEMINI_API_KEY
        if (apiKey.isEmpty() || apiKey == "MY_GEMINI_API_KEY") {
            return@withContext "El Oráculo del Templo requiere la clave API GEMINI_API_KEY para revelar sus consejos sagrados."
        }

        val systemPromptText = if (language == "Totona") {
            """
                Eres un sabio abuelo del pueblo totonaca de Papantla. Eres un experto en lengua totonaca y su gran cultura rica en rituales del Tajín.
                El estudiante está intentando resolver una pregunta de opción múltiple para completar una lección.
                La pregunta es: $question
                Las opciones son: ${options.joinToString(", ")}
                La respuesta correcta es: $correctAnswer
                
                Sin dar la respuesta de manera directa e infantil, dale una pista mnemotécnica o un dato etimológico / gramatical elegante en español que le ayude a deducir la opción correcta (menciona claves sobre las palabras totonacas para guiarlo). Sé sabio y alentador. Que tu respuesta sea de máximo 2 oraciones, de tono místico y tradicional con aroma a vainilla.
            """.trimIndent()
        } else {
            """
                Eres un sabio Sacerdote del Templo de Tenochtitlan. Eres un experto en lengua náhuatl y cultura azteca.
                El estudiante está intentando resolver una pregunta de opción múltiple para completar una lección.
                La pregunta es: $question
                Las opciones son: ${options.joinToString(", ")}
                La respuesta correcta es: $correctAnswer
                
                Sin dar la respuesta de manera directa e infantil, dale una pista mnemotécnica o un dato etimológico / gramatical elegante en español que le ayude a deducir la opción correcta (menciona claves sobre las palabras náhuatl para guiarlo). Sé místico, sabio y alentador. Que tu respuesta sea de máximo 2 oraciones, de tono místico de sacerdote azteca.
            """.trimIndent()
        }

        try {
            val systemPart = JSONObject().put("text", systemPromptText)
            val systemInstruction = JSONObject().put("parts", org.json.JSONArray().put(systemPart))

            val promptPart = JSONObject().put("text", "Dame una pista sacerdotal de IA para resolver este enigma de traducción: \"$question\".")
            val contentsItem = JSONObject().put("parts", org.json.JSONArray().put(promptPart))
            val contentsArray = org.json.JSONArray().put(contentsItem)

            val requestJson = JSONObject()
                .put("contents", contentsArray)
                .put("systemInstruction", systemInstruction)

            val mediaType = "application/json; charset=utf-8".toMediaType()
            val requestBody = requestJson.toString().toRequestBody(mediaType)

            val url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent?key=$apiKey"
            val request = Request.Builder()
                .url(url)
                .post(requestBody)
                .build()

            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) {
                    return@withContext "El oráculo está en meditación. (Código: ${response.code})"
                }
                val responseText = response.body?.string() ?: ""
                val parsedJson = JSONObject(responseText)
                val candidates = parsedJson.optJSONArray("candidates")
                if (candidates == null || candidates.length() == 0) {
                    return@withContext "Las nubes ocultan los astros sagrados. Sigue tu intuición, guerrero."
                }
                val firstCandidate = candidates.getJSONObject(0)
                val parts = firstCandidate.getJSONObject("content").getJSONArray("parts")
                parts.getJSONObject(0).getString("text").trim()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            "El viento susurra de un secreto ancestral: concéntrate en los prefijos sagrados."
        }
    }
}
