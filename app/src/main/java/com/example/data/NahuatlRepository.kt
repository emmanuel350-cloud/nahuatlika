package com.example.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class NahuatlRepository(
    private val userProgressDao: UserProgressDao,
    private val lessonDao: LessonDao,
    private val wordDao: WordDao,
    private val challengeDao: ChallengeDao,
    private val dailyStreakDao: DailyStreakDao,
    private val sacerdoteAudioCacheDao: SacerdoteAudioCacheDao? = null
) {
    val progressFlow: Flow<UserProgress?> = userProgressDao.getProgressFlow()
    val allLessonsFlow: Flow<List<LessonEntity>> = lessonDao.getAllLessonsFlow()
    val allWordsFlow: Flow<List<WordEntity>> = wordDao.getAllWordsFlow()
    val allChallengesFlow: Flow<List<WeeklyChallenge>> = challengeDao.getAllChallengesFlow()
    val dailyInteractionsFlow: Flow<List<DailyInteractionEntity>> = dailyStreakDao.getAllInteractionsFlow()
    val distinctLoginDatesFlow: Flow<List<String>> = dailyStreakDao.getDistinctLoginDatesFlow()
    val cachedSacerdoteAudiosFlow: Flow<List<SacerdoteAudioCacheEntity>> =
        sacerdoteAudioCacheDao?.getAllCachedAudiosFlow() ?: kotlinx.coroutines.flow.flowOf(emptyList())
    val cachedSacerdoteAudiosCountFlow: Flow<Int> =
        sacerdoteAudioCacheDao?.getCacheCountFlow() ?: kotlinx.coroutines.flow.flowOf(0)

    suspend fun recordDailyInteraction(
        interactionType: String = "LOGIN",
        xpEarned: Int = 0,
        dateString: String = DailyStreakCalculator.getCurrentDateString()
    ): DailyStreakStats = withContext(Dispatchers.IO) {
        val entity = DailyInteractionEntity(
            loginDate = dateString,
            timestamp = System.currentTimeMillis(),
            interactionType = interactionType,
            xpEarned = xpEarned
        )
        dailyStreakDao.insertInteraction(entity)
        calculateStreakStatsInternal()
    }

    suspend fun calculateStreakStats(): DailyStreakStats = withContext(Dispatchers.IO) {
        calculateStreakStatsInternal()
    }

    private suspend fun calculateStreakStatsInternal(): DailyStreakStats {
        val distinctDates = dailyStreakDao.getDistinctLoginDates()
        val stats = DailyStreakCalculator.calculateStreakStats(distinctDates)
        // Keep UserProgress.streakDays synced with the Room-computed consecutive streak
        val current = userProgressDao.getProgress()
        if (current != null && current.streakDays != stats.currentStreak && stats.currentStreak > 0) {
            userProgressDao.insertOrUpdateProgress(
                current.copy(
                    streakDays = stats.currentStreak,
                    lastPracticeTime = System.currentTimeMillis()
                )
            )
        }
        return stats
    }

    suspend fun getAllInteractions(): List<DailyInteractionEntity> = withContext(Dispatchers.IO) {
        dailyStreakDao.getAllInteractions()
    }

    suspend fun deleteInteractionById(id: Long) = withContext(Dispatchers.IO) {
        dailyStreakDao.deleteById(id)
    }

    suspend fun clearDailyInteractions() = withContext(Dispatchers.IO) {
        dailyStreakDao.clearAll()
    }

    suspend fun getProgress(): UserProgress? = userProgressDao.getProgress()

    suspend fun updateProgress(progress: UserProgress) {
        userProgressDao.insertOrUpdateProgress(progress)
    }


    suspend fun addXp(xp: Int) {
        userProgressDao.addXp(xp)
        // Check achievements or levels
        val current = userProgressDao.getProgress()
        if (current != null) {
            val nextLevel = (current.xp / 100) + 1
            if (nextLevel > current.level) {
                userProgressDao.insertOrUpdateProgress(
                    current.copy(level = nextLevel, xp = current.xp + xp)
                )
            }
        }
    }

    suspend fun updateLessonCompletion(id: Int, completed: Boolean) {
        val progress = userProgressDao.getProgress() ?: UserProgress()
        val lang = progress.selectedLanguage
        
        val updatedProgress = when (lang) {
            "Totona" -> {
                val newStr = if (completed) {
                    addCompletedLesson(progress.completedLessonsTotona, id)
                } else {
                    removeCompletedLesson(progress.completedLessonsTotona, id)
                }
                val completedCount = if (newStr.isBlank()) 0 else newStr.split(";").size
                progress.copy(
                    completedLessonsTotona = newStr,
                    completedLessonsCount = completedCount,
                    xp = progress.xp + (if (completed) 50 else 0)
                )
            }
            else -> { // Nahuatl
                val newStr = if (completed) {
                    addCompletedLesson(progress.completedLessonsNahuatl, id)
                } else {
                    removeCompletedLesson(progress.completedLessonsNahuatl, id)
                }
                val completedCount = if (newStr.isBlank()) 0 else newStr.split(";").size
                progress.copy(
                    completedLessonsNahuatl = newStr,
                    completedLessonsCount = completedCount,
                    xp = progress.xp + (if (completed) 50 else 0)
                )
            }
        }
        
        userProgressDao.insertOrUpdateProgress(updatedProgress)

        // Increment weekly challenges
        incrementChallengeProgress(id = 4, amount = 1) // Challenge 4 is 'Completa 2 lecciones'
    }

    private fun addCompletedLesson(completedStr: String, id: Int): String {
        val list = if (completedStr.isBlank()) mutableListOf() else completedStr.split(";").mapNotNull { it.toIntOrNull() }.toMutableList()
        if (!list.contains(id)) {
            list.add(id)
        }
        return list.joinToString(";")
    }

    private fun removeCompletedLesson(completedStr: String, id: Int): String {
        val list = if (completedStr.isBlank()) mutableListOf() else completedStr.split(";").mapNotNull { it.toIntOrNull() }.toMutableList()
        list.remove(id)
        return list.joinToString(";")
    }

    suspend fun incrementChallengeProgress(id: Int, amount: Int) {
        val challenges = challengeDao.getAllChallengesFlow()
        // Simple query
        val dbSnapshot = challengeDao.getChallengesCount()
        if (dbSnapshot > 0) {
            // Find specific challenge and update
        }
    }

    suspend fun searchWords(query: String): List<WordEntity> = withContext(Dispatchers.IO) {
        wordDao.searchWords(query)
    }

    suspend fun playMatch() {
        val progress = userProgressDao.getProgress() ?: UserProgress()
        val lang = progress.selectedLanguage
        
        val updatedProgress = when (lang) {
            "Totona" -> {
                progress.copy(
                    matchesPlayedTotona = progress.matchesPlayedTotona + 1,
                    matchesPlayed = progress.matchesPlayed + 1,
                    xp = progress.xp + 15
                )
            }
            else -> { // Nahuatl
                progress.copy(
                    matchesPlayedNahuatl = progress.matchesPlayedNahuatl + 1,
                    matchesPlayed = progress.matchesPlayed + 1,
                    xp = progress.xp + 15
                )
            }
        }
        
        userProgressDao.insertOrUpdateProgress(updatedProgress)
        // Challenge id 3 is "Maestro del Juego": Juega y gana 3 partidas
        updateChallengeProgressDirect(3, updatedProgress.matchesPlayed)
    }

    suspend fun logTranslation() {
        val progress = userProgressDao.getProgress() ?: UserProgress()
        userProgressDao.insertOrUpdateProgress(
            progress.copy(
                translationsCount = progress.translationsCount + 1
            )
        )
        // Challenge id 1 is "Traductor Inteligente": Traduce 3 frases con la IA
        updateChallengeProgressDirect(1, progress.translationsCount + 1)
    }

    private suspend fun updateChallengeProgressDirect(challengeId: Int, value: Int) {
        // Will manually update challenges in database
        val target = when (challengeId) {
            1 -> 3 // 3 translations
            2 -> 5 // 5 searches
            3 -> 3 // 3 games
            4 -> 2 // 2 lessons
            else -> 10
        }
        val completed = value >= target
        challengeDao.updateChallengeProgress(challengeId, minOf(value, target), completed)
    }

    suspend fun initializeDatabaseIfNeeded() = withContext(Dispatchers.IO) {
        // 0. Setup default streak logins in Room DB if empty
        if (dailyStreakDao.getTotalActiveDaysCount() == 0) {
            val sdf = DailyStreakCalculator.getDateFormat()
            // Seed 3 consecutive interaction days leading to today
            for (offset in 2 downTo 0) {
                val cal = java.util.Calendar.getInstance().apply {
                    add(java.util.Calendar.DAY_OF_YEAR, -offset)
                }
                dailyStreakDao.insertInteraction(
                    DailyInteractionEntity(
                        loginDate = sdf.format(cal.time),
                        timestamp = cal.timeInMillis,
                        interactionType = if (offset == 0) "LOGIN" else "PRACTICE",
                        xpEarned = 20
                    )
                )
            }
        }

        // 1. Setup default user progress
        if (userProgressDao.getProgress() == null) {
            val stats = calculateStreakStatsInternal()
            userProgressDao.insertOrUpdateProgress(
                UserProgress(
                    id = 1,
                    xp = 60,
                    level = 1,
                    streakDays = maxOf(3, stats.currentStreak), // Initial motivation streak
                    lastPracticeTime = System.currentTimeMillis()
                )
            )
        } else {
            calculateStreakStatsInternal()
        }


        // 2. Setup default dictionary words (Mesoamerican vocabulary base)
        if (wordDao.getWordsCount() == 0) {
            val words = listOf(
                // Greetings & Saludos
                WordEntity(nahuatl = "Niltze", spanish = "Hola", pronunciation = "Neel-tzeh", category = "Saludos", sentenceNahuatl = "Niltze, ¿queniuhqui ticate?", sentenceSpanish = "Hola, ¿cómo estás?"),
                WordEntity(nahuatl = "Tlazohcamati", spanish = "Gracias", pronunciation = "Tlah-zoh-kah-mah-tee", category = "Saludos", sentenceNahuatl = "Tlazohcamati ipampa moceltzin.", sentenceSpanish = "Gracias por tu amabilidad."),
                WordEntity(nahuatl = "Cualli tonalli", spanish = "Buenos días", pronunciation = "Kwah-lee toh-nah-lee", category = "Saludos", sentenceNahuatl = "Cualli tonalli, noconetzin.", sentenceSpanish = "Buenos días, mi niño."),
                WordEntity(nahuatl = "Queniuhqui ticate?", spanish = "¿Cómo estás?", pronunciation = "Keh-nee-ook-ee tee-kah-teh", category = "Saludos", sentenceNahuatl = "Queniuhqui ticate oncan?", sentenceSpanish = "¿Cómo estás allí?"),
                WordEntity(nahuatl = "Noyolotl", spanish = "Mi corazón / Cariño", pronunciation = "Noh-yoh-lohtl", category = "Saludos", sentenceNahuatl = "Noyolotl tlein ticpiani.", sentenceSpanish = "Mi corazón, quédate tranquilo."),
                
                // Numbers / Números
                WordEntity(nahuatl = "Ce", spanish = "Uno", pronunciation = "Seh", category = "Números", sentenceNahuatl = "Ce conetl choloa.", sentenceSpanish = "Un niño corre."),
                WordEntity(nahuatl = "Ome", spanish = "Dos", pronunciation = "Oh-meh", category = "Números", sentenceNahuatl = "Ome mizto choca.", sentenceSpanish = "Dos gatos chillan."),
                WordEntity(nahuatl = "Eyi", spanish = "Tres", pronunciation = "Eh-yee", category = "Números", sentenceNahuatl = "Eyi papalotzin patlani.", sentenceSpanish = "Tres mariposas vuelan."),
                WordEntity(nahuatl = "Nahui", spanish = "Cuatro", pronunciation = "Nah-wee", category = "Números", sentenceNahuatl = "Nahui tecpatl mocahua.", sentenceSpanish = "Cuatro pedernales quedan."),
                WordEntity(nahuatl = "Macuilli", spanish = "Cinco", pronunciation = "Mah-kwee-lee", category = "Números", sentenceNahuatl = "Macuilli tonalmitl.", sentenceSpanish = "Cinco rayos de sol."),

                // Animals / Animales
                WordEntity(nahuatl = "Ocelotl", spanish = "Jaguar", pronunciation = "Oh-seh-lohtl", category = "Animales", sentenceNahuatl = "Ocelotl tlacua cuauhtla.", sentenceSpanish = "El jaguar come en la selva."),
                WordEntity(nahuatl = "Coatl", spanish = "Serpiente", pronunciation = "Koh-ah-tl", category = "Animales", sentenceNahuatl = "Quetzalcoatl ihcac nemilitztli.", sentenceSpanish = "La serpiente emplumada es vida."),
                WordEntity(nahuatl = "Cuauhtli", spanish = "Águila", pronunciation = "Kwah-oo-tlee", category = "Animales", sentenceNahuatl = "Cuauhtli patlani ilhuicac.", sentenceSpanish = "El águila vuela en el cielo."),
                WordEntity(nahuatl = "Tochtli", spanish = "Conejo", pronunciation = "Tohch-tlee", category = "Animales", sentenceNahuatl = "Tochtli tlacua metztli tlallan.", sentenceSpanish = "El conejo come bajo la luz de la luna."),
                WordEntity(nahuatl = "Papalotl", spanish = "Mariposa", pronunciation = "Pah-pah-lohtl", category = "Animales", sentenceNahuatl = "Papalotl quitemoa xochitl.", sentenceSpanish = "La mariposa busca una flor."),

                // Nature / Naturaleza
                WordEntity(nahuatl = "Tonatiuh", spanish = "Sol", pronunciation = "Toh-nah-tee-oo", category = "Naturaleza", sentenceNahuatl = "Tonatiuh tlanextia iyoliz.", sentenceSpanish = "El sol alumbra la vida."),
                WordEntity(nahuatl = "Metztli", spanish = "Luna", pronunciation = "Metz-tlee", category = "Naturaleza", sentenceNahuatl = "Metztli tlahvia yohualnepantla.", sentenceSpanish = "La luna ilumina a medianoche."),
                WordEntity(nahuatl = "Atl", spanish = "Agua", pronunciation = "Ah-tl", category = "Naturaleza", sentenceNahuatl = "Atl chicahuac ihcac.", sentenceSpanish = "El agua corre abundante."),
                WordEntity(nahuatl = "Tletl", spanish = "Fuego", pronunciation = "Tleh-tl", category = "Naturaleza", sentenceNahuatl = "Tletl tlatia tlanextiliztli.", sentenceSpanish = "El fuego quema brillante."),
                WordEntity(nahuatl = "Citlalin", spanish = "Estrella", pronunciation = "See-tlah-leen", category = "Naturaleza", sentenceNahuatl = "Citlalin tlanextia ilhuicac.", sentenceSpanish = "La estrella brilla en el cielo.")
            )
            wordDao.insertWords(words)
        }

        // 3. Setup default weekly challenges
        if (challengeDao.getChallengesCount() == 0) {
            val challenges = listOf(
                WeeklyChallenge(id = 1, title = "Traductor Inteligente", description = "Realiza 3 traducciones bidireccionales de oraciones usando el traductor inteligente.", currentProgress = 0, targetProgress = 3, xpReward = 100),
                WeeklyChallenge(id = 2, title = "Estudio Culto", description = "Completa 2 lecciones avanzadas de cultura precolombina.", currentProgress = 0, targetProgress = 2, xpReward = 150),
                WeeklyChallenge(id = 3, title = "Maestro del Memorama", description = "Reta tu memoria completando 3 juegos recreativos de destreza lingüística.", currentProgress = 0, targetProgress = 3, xpReward = 120),
                WeeklyChallenge(id = 4, title = "Reto de Constancia", description = "Termina satisfactoriamente 2 lecciones del camino real de progreso.", currentProgress = 0, targetProgress = 2, xpReward = 80)
            )
            challengeDao.insertChallenges(challenges)
        }

        // 4. Prepopulate dynamic lessons database with exactly 1500 lessons (500 Principiante, 500 Intermedio, 500 Avanzado)
        val currentCount = lessonDao.getLessonsCount()
        val fourthLesson = if (currentCount > 0) lessonDao.getLessonById(4) else null
        val hasEmmanuel = fourthLesson?.nahuatlPhrases?.contains("Emmanuel") ?: false
        val isRepetitive = fourthLesson?.explanation?.contains("Avanza en tu dominio") ?: true
        val isThematic = fourthLesson?.explanation?.contains("[thematic_v5]") ?: false
        
        if (currentCount < 1500 || hasEmmanuel || isRepetitive || !isThematic) {
            lessonDao.clearAllLessons()
            val lessonsList = ArrayList<LessonEntity>()
            // Handcrafted high-yield lessons (1 to 5)
            lessonsList.add(
                LessonEntity(
                    id = 1,
                    title = "Introducción a los Saludos Nahuatl",
                    unit = 1,
                    category = "Vocabulario",
                    explanation = "Los saludos en náhuatl expresan gran respeto por el entorno físico y espiritual del interlocutor. Al saludar, se honra la luz del día y la paz del hogar.",
                    nahuatlPhrases = "Niltze;Cualli tonalli;Cualli yohualli;Tlazohcamati;Queniuhqui ticate?",
                    spanishTranslations = "Hola (saludo general);Buenos días;Buenas noches;Gracias;¿Cómo estás?",
                    difficulty = "Principiante",
                    aiSummary = "La inteligencia artificial sugiere: Esta lección introduce palabras clave con fonética básica. Ideal para fundamentar tu oído en este dialecto precolombino.",
                    didYouKnow = "¿Sabías que la palabra 'náhuatl' proviene de 'nahuac' que significa 'cerca' o 'hablar con claridad'?"
                )
            )
            lessonsList.add(
                LessonEntity(
                    id = 2,
                    title = "Los Números Sagrados del Universo",
                    unit = 1,
                    category = "Frases",
                    explanation = "El sistema numérico náhuatl es vigesimal (base 20), reflejando la suma de dedos de manos y pies. Cada número posee una alta carga filosófica.",
                    nahuatlPhrases = "Ce macehualli;Ome papalotzin;Eyi citlalin;Nahui ehecatl;Macuilli xochitl",
                    spanishTranslations = "Una persona indígena;Dos mariposas;Tres estrellas;Cuatro vientos;Cinco flores",
                    difficulty = "Principiante",
                    aiSummary = "La IA resume: Aprenderás las bases del sistema vigesimal mesoamericano. Asocia cada número con elementos de la naturaleza.",
                    didYouKnow = "¿Sabías que los aztecas usaban un sistema matemático de puntos y barras similar al maya para operaciones astronómicas complejas?"
                )
            )
            lessonsList.add(
                LessonEntity(
                    id = 3,
                    title = "Los Guardianes de la Tierra",
                    unit = 1,
                    category = "Cultura",
                    explanation = "En la cosmovisión mesoamericana, los animales no son inferiores, sino nahualli (guardianes espirituales) que guían los rumbos del destino cósmico.",
                    nahuatlPhrases = "Ocelotl tlanemilia;Cuauhtli tlahvia;Coatl nemi tlallan;Mizton mapaca",
                    spanishTranslations = "El jaguar medita;El águila vigila;La serpiente recorre la tierra;El gato se asea",
                    difficulty = "Principiante",
                    aiSummary = "La IA resume: Esta lección te conecta con el nahualismo, entendiendo la sagrada correlación animal-humano en Mesoamérica.",
                    didYouKnow = "¿Sabías que el 'Nahualli' o Nahual es el gemelo animal asignado espiritualmente a cada ser humano al nacer?"
                )
            )
            val possibleNahuatlNames = listOf("Citlalli", "Tlaneci", "Xochitl", "Cuauhtli", "Yolotl", "Nelli", "Miztli", "Tenoch")
            val possibleSpanishNames = listOf("Citlalli", "Tlaneci", "Xóchitl", "Cuauhtli", "Yólotl", "Nelli", "Miztli", "Tenoch")
            val randomNameIndex = (possibleNahuatlNames.indices).random()
            val chosenNameN = possibleNahuatlNames[randomNameIndex]
            val chosenNameS = possibleSpanishNames[randomNameIndex]

            lessonsList.add(
                LessonEntity(
                    id = 4,
                    title = "Diálogo Cordial en el Hogar",
                    unit = 1,
                    category = "Conversación",
                    explanation = "Aprende a presentarte y compartir afecto en una conversación cotidiana dentro del hogar. Los sufijos '-tzin' añaden reverencia y amor. [thematic_v5]",
                    nahuatlPhrases = "Noitlah noyolotl;Queniuhqui motoca?;Notoca $chosenNameN;¿Canin tichanti?;Notzo, tlazohcamati",
                    spanishTranslations = "De nada, mi cariño;¿Cómo te llamas?;Me llamo $chosenNameS;¿Dónde vives?;Buenas tardes, gracias",
                    difficulty = "Principiante",
                    aiSummary = "La IA resume: Práctica del habla viva en el núcleo familiar azteca empleando diminutivos cariñosos reverenciales.",
                    didYouKnow = "¿Sabías que colocarle el sufijo '-tzin' a un nombre o sustantivo no solo significa 'pequeño' sino de máxima reverencia honorífica?"
                )
            )
            lessonsList.add(
                LessonEntity(
                    id = 5,
                    title = "La Gramática y el Plural Cósmico",
                    unit = 1,
                    category = "Gramática",
                    explanation = "A diferencia del español, los sustantivos inanimados en náhuatl generalmente no tienen plural gramatical, solo los seres animados y sagrados se pluralizan con sufijos como '-tin' o '-me'.",
                    nahuatlPhrases = "Coyomeh choloah;Papalomeh patlanih;Totomeh tzicnih;Cihuateh tequitih;Tlacameh tli-tlacnah",
                    spanishTranslations = "Los coyotes huyen;Las mariposas vuelan;Los pájaros cantan;Las mujeres trabajan;Los hombres conversan",
                    difficulty = "Principiante",
                    aiSummary = "La IA resume: Introducción a la regla morfológica del plural de seres vivos contra inanimados en el náhuatl estándar.",
                    didYouKnow = "¿Sabías que para los antiguos nahuas las piedras y ríos poseen alma y vida, por lo que algunos sabios sí pluralizan elementos de la naturaleza?"
                )
            )

            val categories = listOf("Vocabulario", "Gramática", "Conversación", "Frases", "Cultura")
            val unitsThematic = listOf(
                // 1-10 Principiante
                "Saludos y Cortesía", "Matemáticas y Cosmos", "La Familia y Personas", 
                "Flora y Fauna Silvestre", "Alimentos y Cocina", "Colores y Texturas", 
                "El Cuerpo Humano", "Acciones del Día", "Clima y Estaciones", "La Computadora y el Código",
                // 11-20 Intermedio
                "El Comercio y Tianguis", "Herramientas y Oficios", "Geografía y Pueblos", 
                "Fiestas y Ceremonias", "Las Emociones y Sentir", "Agricultura y Cosechas", 
                "Vestimenta y Arte", "Cantos y Expresiones", "Mitología del Jaguar", "Sistemas Digitales y Redes",
                // 21-30 Avanzado
                "Filosofía Ancestral", "Leyendas Precolombinas", "Medicina Tradicional", 
                "Leyes y Gobernantes", "Arquitectura Piramidal", "El Arte de la Guerra", 
                "Poesía de Nezahualcóyotl", "El Tiempo y Calendarios", "El Oráculo del Sol", "Inteligencia Artificial y Neologismos"
            )

            for (idx in 6..1500) {
                val currentUnit = ((idx - 1) / 50) + 1 // Unit 1 to 30
                val category = categories[idx % categories.size]
                val unitTitle = unitsThematic[(currentUnit - 1) % unitsThematic.size]
                
                val diffStr = when {
                    currentUnit <= 10 -> "Principiante"
                    currentUnit <= 20 -> "Intermedio"
                    else -> "Avanzado"
                }

                val isAiUnit = unitTitle in listOf("La Computadora y el Código", "Sistemas Digitales y Redes", "Inteligencia Artificial y Neologismos")

                // Programmatic selection of 5 completely unique phrases using index math from thematic pools
                val phraseNahuatlList = ArrayList<String>()
                val phraseSpanishList = ArrayList<String>()
                val usedPhrases = HashSet<String>()
                var currentK = 0
                while (phraseNahuatlList.size < 5 && currentK < 100) {
                    val (nPhrase, sPhrase) = getThematicPhrasesForUnit(currentUnit, idx, currentK)
                    if (!usedPhrases.contains(nPhrase)) {
                        usedPhrases.add(nPhrase)
                        phraseNahuatlList.add(nPhrase)
                        phraseSpanishList.add(sPhrase)
                    }
                    currentK++
                }
                // Fallback if we didn't get 5 unique
                while (phraseNahuatlList.size < 5) {
                    val (nPhrase, sPhrase) = getThematicPhrasesForUnit(currentUnit, idx, currentK)
                    phraseNahuatlList.add(nPhrase)
                    phraseSpanishList.add(sPhrase)
                    currentK++
                }

                val nahuatlPhrases = phraseNahuatlList.joinToString(";")
                val spanishTranslations = phraseSpanishList.joinToString(";")

                val firstPhraseN = phraseNahuatlList.firstOrNull() ?: ""
                val firstPhraseS = phraseSpanishList.firstOrNull() ?: ""

                // Seeded Random for high entropy variation in explanations and titles
                val randTitle = java.util.Random(idx * 79L)
                
                val categoryFocuses = when (category) {
                    "Vocabulario" -> listOf(
                        "Nos enfocaremos en enriquecer el léxico mediante términos descriptivos clave.",
                        "Ampliaremos el repertorio léxico con vocablos tradicionales y descriptivos de uso común.",
                        "Estudiaremos un glosario selecto para enriquecer la fluidez de tus palabras.",
                        "Identificaremos términos fundamentales de la vida diaria y la naturaleza.",
                        "Aprenderemos la etimología de vocablos esenciales de la cultura mesoamericana."
                    )
                    "Gramática" -> listOf(
                        "Estudiaremos la conjugación verbal, prefijos de objeto y las variaciones morfológicas.",
                        "Analizaremos las reglas de posesión, prefijos pronominales y sufijos distributivos.",
                        "Profundizaremos en la estructura sintáctica para armar enunciados de forma natural.",
                        "Examinaremos la diferencia morfológica entre sustantivos animados e inanimados.",
                        "Practicaremos la composición de palabras uniendo raíces verbales y sustantivos."
                    )
                    "Conversación" -> listOf(
                        "Practicaremos diálogos fluidos que emulan el intercambio de ideas entre hablantes nativos.",
                        "Simularemos diálogos respetuosos del entorno hogareño y comunitario del Anáhuac.",
                        "Fomentaremos la comprensión auditiva mediante la interacción conversacional viva.",
                        "Aprenderemos a deconstruir preguntas y respuestas cordiales con modulación nativa.",
                        "Analizaremos saludos, réplicas y expresiones de agradecimiento cotidiano en el habla real."
                    )
                    "Frases" -> listOf(
                        "Aprenderemos expresiones cotidianas de uso directo y su traducción semántica precisa.",
                        "Exploraremos modismos útiles y frases hechas para facilitar la comunicación diaria.",
                        "Estudiaremos enunciados completos de gran utilidad en la vida del campo y de la ciudad.",
                        "Analizaremos expresiones comunes con una alta carga metafórica y poética.",
                        "Practicaremos frases de cortesía y respeto ideales para entablar comunicación."
                    )
                    else -> listOf( // Cultura
                        "Analizaremos el contexto histórico, mítico o social de las palabras y su legado cultural.",
                        "Aprenderemos la relación estrecha entre el idioma, la naturaleza y la cosmovisión mesoamericana.",
                        "Exploraremos leyendas, cantares o poemas que revelan el pensamiento filosófico nahua.",
                        "Estudiaremos tradiciones ancestrales que se mantienen vigentes en los pueblos actuales.",
                        "Conoceremos el significado sagrado del maíz, el fuego y las fiestas rituales del pueblo."
                    )
                }

                val progressionSentences = listOf(
                    "Esta lección te guiará paso a paso para dominar las raíces de la unidad, asimilando la pronunciación correcta.",
                    "A través de ejemplos prácticos, comprenderás cómo los prefijos cambian el sentido de la acción.",
                    "El análisis detallado te permitirá estructurar oraciones complejas con total confianza.",
                    "Expandiremos tu comprensión auditiva e intelectual mediante reflexiones de nivel $diffStr.",
                    "Esta sección de estudio promueve un aprendizaje intuitivo y respetuoso de la fonética del náhuatl.",
                    "Mediante la práctica constante de esta lección, fijarás las estructuras gramaticales principales.",
                    "Conectarás de inmediato la traducción semántica para evitar la memorización rígida.",
                    "Esta aproximación pedagógica te facilitará integrar el idioma a tus pensamientos diarios."
                )

                val focus = categoryFocuses[randTitle.nextInt(categoryFocuses.size)]
                val prog = progressionSentences[randTitle.nextInt(progressionSentences.size)]

                val explanation = if (isAiUnit) {
                    val baseAi = when (unitTitle) {
                        "La Computadora y el Código" -> "Exploraremos la adopción de la tecnología en la lengua náhuatl. Aprenderemos cómo se combinan raíces como 'tepoztli' (metal/máquina) y 'amatl' (papel/libro) para nombrar herramientas modernas como la computadora y el código."
                        "Sistemas Digitales y Redes" -> "Profundizaremos en la estructura morfológica de los sistemas digitales. El náhuatl utiliza conceptos poéticos como 'matlatl' (red de pesca) para referirse a redes de internet y transmisión de datos."
                        else -> "Analizaremos el concepto de Inteligencia Artificial ('Tepozixmatiliztli') bajo la óptica filosófica nahua. Los sabios debaten cómo la conciencia de las máquinas puede ser expresada a través de metáforas."
                    }
                    "$baseAi Estudiaremos la expresión '$firstPhraseN' ($firstPhraseS). Esta lección $idx te ayudará a integrar el conocimiento en la era digital."
                } else {
                    "${getUnitBaseExplanation(unitTitle)} En particular, analizaremos la frase '$firstPhraseN' ($firstPhraseS). $focus $prog (Lección $idx)"
                }

                val factsPool = listOf(
                    "¿Sabías que el chocolate proviene de 'xocolatl' (agua amarga)? Es un regalo de Mesoamérica al mundo.",
                    "¿Sabías que el aguacate proviene de 'ahuacatl' (testículo), llamado así por su forma colgante en el árbol?",
                    "¿Sabías que palabras tan cotidianas como tomate (tomatl), chicle (tzictli) y tianguis (tianquiztli) son de origen náhuatl?",
                    "¿Sabías que Nezahualcóyotl, el gran rey de Texcoco, construyó presas y compuso bellísimos cantos poéticos?",
                    "¿Sabías que la entrañable palabra 'apapachar' proviene de 'patzhoa', que significa abrazar o acariciar con el alma?",
                    "¿Sabías que 'Piyali' es un saludo cordial náhuatl similar a 'hola' que se usa para desear bienestar?",
                    "¿Sabías que el náhuatl es una lengua aglutinante, lo que significa que junta varias raíces para formar palabras larguísimas?",
                    "¿Sabías que Tenochtitlan fue fundada sobre un islote en el lago de Texcoco siguiendo la señal de un águila sagrada?",
                    "¿Sabías que los antiguos mexicanos tenían escuelas públicas llamadas Calmécac (para nobles) y Telpochcalli (para jóvenes)?",
                    "¿Sabías que en náhuatl no existe propiamente la palabra 'adiós', sino expresiones como 'Noyolotl' (mi corazón) o 'Toteotsin mitzmopilixtzino'?",
                    "¿Sabías que la palabra coyote viene de 'coyotl', considerado un animal astuto e inteligente en el folclor prehispánico?",
                    "¿Sabías que el maíz ('centli' o 'tlaolli') era considerado una sustancia sagrada de la que estaban hechos los seres humanos?",
                    "¿Sabías que el baño de vapor tradicional 'temazcal' purifica el cuerpo y el espíritu mediante hierbas medicinales?",
                    "¿Sabías que los códices prehispánicos ('amoxtli') eran pintados sobre papel amate extraído de la corteza de árboles?",
                    "¿Sabías que el 'copal' es una resina aromática que se quema como ofrenda para comunicar la tierra con el cielo?",
                    "¿Sabías que el jaguar ('ocelotl') era el máximo símbolo de poder militar, astucia nocturna y sabiduría mística?",
                    "¿Sabías que la mariposa ('papalotl') representaba las almas de los guerreros caídos que regresaban a visitar a sus familias?",
                    "¿Sabías que el sistema vigesimal náhuatl usaba la base 20 porque representaba la suma de los dedos de manos y pies de una persona?",
                    "¿Sabías que para los nahuahablantes las piedras y los cerros tienen alma ('yollotl'), por lo que merecen el máximo respeto?",
                    "¿Sabías que el chicle proviene del náhuatl 'tzictli', la savia que extraían los antiguos del árbol de chicozapote?"
                )

                val baseFact = if (isAiUnit) {
                    val aiFacts = listOf(
                        "¿Sabías que en náhuatl moderno 'Inteligencia Artificial' se traduce como 'Tepozixmatiliztli' (conocimiento de metal)?",
                        "¿Sabías que para decir 'Internet', los hablantes utilizan 'Tepoztlatolmatlatl' (red de palabras mecánicas)?",
                        "¿Sabías que un robot se denomina 'Tepoztlacatl' (persona de metal), reflejando una visión antropomórfica de la tecnología?"
                    )
                    aiFacts[randTitle.nextInt(aiFacts.size)]
                } else {
                    factsPool[randTitle.nextInt(factsPool.size)]
                }
                val customFact = "$baseFact En esta lección $idx, podrás aplicar esto practicando la hermosa frase '$firstPhraseN' ($firstPhraseS)."

                val vocabSub = listOf("Expresiones Esenciales", "Vocablos Clave", "Términos del Cosmos", "Léxico Sagrado", "Palabras de la Tierra", "Nuevos Conceptos", "Raíces del Saber", "Glosario Poético", "Nomenclatura Tradicional", "Terminología Práctica")
                val grammarSub = listOf("Estructura del Verbo", "Uso de Prefijos", "Sufijos Reverenciales", "Sustantivos Animados", "Pluralización Sagrada", "Sintaxis Práctica", "Pronombres de Objeto", "Modificadores de Acción", "Flexión Verbal", "Construcción de Frases")
                val conversationSub = listOf("Diálogo Familiar", "Intercambio en el Tianguis", "Plática de Sabios", "Consejo del Abuelo", "Saludo al Tlahtoani", "Charla en la Milpa", "Conversación del Día", "Acuerdo Comunitario", "Palabra Compartida", "Encuentro en el Camino")
                val phrasesSub = listOf("Frases de Respeto", "Expresiones Cotidianas", "Fórmulas de Cortesía", "Declamación Poética", "Dichos Ancestrales", "Oraciones de Trabajo", "Voces de la Naturaleza", "Pensamientos del Corazón", "Mensajes de Paz", "Líneas de Sabiduría")
                val cultureSub = listOf("Legado de Nezahualcóyotl", "Cosmovisión del Jaguar", "Medicina del Temazcal", "El Canto y el Copal", "Leyenda de los Soles", "La Milpa y el Cacao", "Pintura en Códices", "El Calendario Solar", "Alineación de Pirámides", "Sabiduría Colectiva")
                
                val listSub = when (category) {
                    "Vocabulario" -> vocabSub
                    "Gramática" -> grammarSub
                    "Conversación" -> conversationSub
                    "Frases" -> phrasesSub
                    else -> cultureSub
                }
                val categorySubtheme = listSub[randTitle.nextInt(listSub.size)]
                val lessonTitle = "Lección $idx: $categorySubtheme - $firstPhraseS"

                lessonsList.add(
                    LessonEntity(
                        id = idx,
                        title = lessonTitle,
                        unit = currentUnit,
                        category = category,
                        explanation = explanation,
                        nahuatlPhrases = nahuatlPhrases,
                        spanishTranslations = spanishTranslations,
                        difficulty = diffStr,
                        aiSummary = "La inteligencia artificial del Oráculo resume: Dominarás tópicos de nivel $diffStr de la unidad '$unitTitle'. Estudiaremos la expresión '$firstPhraseN' para fijar el aprendizaje.",
                        didYouKnow = customFact
                    )
                )
            }
            // Insert all 1500 lessons in batch inside a single transaction!
            lessonDao.insertLessons(lessonsList)
        }
    }

    private fun getThematicPhrasesForUnit(unit: Int, idx: Int, k: Int): Pair<String, String> {
        val (subjectsN, subjectsS) = when (unit) {
            1 -> listOf("Noyolotl", "Notlemah", "Tlamatini", "Cihuatequitini", "Tlacatequitini", "Noitlah") to
                 listOf("Mi corazón / cariño", "Mi respeto", "El sabio", "La trabajadora", "El trabajador", "Mi tío/tía")
            2 -> listOf("Citlalin", "Metztli", "Tonatiuh", "Ilhuicatl", "Cempohualiztli", "Ehecatl") to
                 listOf("La estrella", "La luna", "El sol", "El cielo", "La cuenta vigesimal", "El viento cósmico")
            3 -> listOf("Nohuan", "Nopapan", "Nolnan", "Noconetzin", "Notzotzin", "Noitlah") to
                 listOf("Mi hermano", "Mi padre", "Mi madre", "Mi niño", "Mi abuelo/abuela", "Mi pariente")
            4 -> listOf("Ocelotl", "Cuauhtli", "Coatl", "Tochtli", "Papalotl", "Tototl") to
                 listOf("El jaguar", "El águila", "La serpiente", "El conejo", "La mariposa", "El pájaro silvestre")
            5 -> listOf("Tlacualchihualoni", "Tzoalli", "Tlaxcalli", "Etl", "Cacahuatl", "Comalli") to
                 listOf("La cocinera", "El amaranto", "La tortilla de maíz", "El frijol", "El grano de cacao", "El comal caliente")
            6 -> listOf("Tlapaloni", "Chichiltic", "Quiltic", "Coztic", "Texotic", "Amatl") to
                 listOf("El pintor/tlacuilo", "El tinte rojo", "El color verde", "El color amarillo", "El tinte azul", "El lienzo amate")
            7 -> listOf("Yollotl", "Cuaxicolli", "Mapilli", "Ixtli", "Nacaztli", "Metztli") to
                 listOf("El corazón", "La cabeza", "El dedo", "El rostro", "El oído", "El ojo/fuerza vital")
            8 -> listOf("Tlacatequitini", "Conetl", "Macehualli", "Cihuatequitini", "Mizton", "Chichi") to
                 listOf("El trabajador", "El niño", "El habitante", "La trabajadora", "El gato del hogar", "El perro")
            9 -> listOf("Quiahuitl", "Ehecatl", "Mixpantli", "Cepayahuitl", "Tlemitl", "Tonatiuh") to
                 listOf("La lluvia sagrada", "El viento fuerte", "La nube gris", "La helada", "El rayo de sol", "El calor solar")
            10 -> listOf("Tepoznemilini", "Tepozamatl", "Tepozmachiyotl", "Tepozicpatl", "Tepozmachiyoni", "Tepozmatlatl") to
                  listOf("La computadora", "La pantalla digital", "El código", "El cable / conexión", "El programador de IA", "La red local")
            11 -> listOf("Tianquiztli", "Tlanamacani", "Tlacohuani", "Patoloni", "Cacahuatlahtolli", "Xochitl") to
                  listOf("El tianguis", "El vendedor", "El comprador", "La moneda / trueque", "El pacto de cacao", "La flor vendida")
            12 -> listOf("Amantecatl", "Tepoztequini", "Tlaxcalchihuani", "Tlacuilo", "Milli-tequitini", "Tetzotzonqui") to
                  listOf("El artesano plomero", "El carpintero", "La panadera tradicional", "El escribano de códices", "El sembrador de la milpa", "El cantero escultor")
            13 -> listOf("Altepetl", "Tepetl", "Ameyalli", "Oztotl", "Tlallan", "Chinamitl") to
                  listOf("El pueblo / cerro-agua", "El cerro majestuoso", "El manantial", "La cueva antigua", "El territorio local", "La chinampa productiva")
            14 -> listOf("Mitotiani", "Teopixqui", "Teponaztli", "Copal", "Xochicuicatl", "Huehuetl") to
                  listOf("El danzante ritual", "El sacerdote", "El tambor de madera", "El copal ceremonial", "El canto florido", "El gran tambor de cuero")
            15 -> listOf("Yolpaquiliztli", "Yochocani", "Yolchicahuac", "Yolnepantla", "Noyolotzin", "Tlazohcamati") to
                  listOf("La alegría", "La tristeza", "La valentía", "La paz mental", "Mi alma / cariño", "El agradecimiento")
            16 -> listOf("Millacatl", "Centli", "Milli", "Atoctli", "Tepoztlacolli", "Amochtli") to
                  listOf("El agricultor de milpa", "El maíz seco", "La milpa verde", "La tierra fértil", "La hoz de cobre", "El agua de riego")
            17 -> listOf("Huepilli", "Maxtlatl", "Tilmatli", "Amanteca", "Xochicoscatl", "Coyolli") to
                  listOf("El huipil bordado", "El ceñidor tradicional", "La manta preciosa", "El artista plumario", "El collar de flores", "El cascabel de bronce")
            18 -> listOf("Cuicapicqui", "Xochicuicatl", "Acolhuacan", "Tlahtolli", "Teponaztli", "Noyolocuicatl") to
                  listOf("El poeta ancestral", "El canto florido", "La cuna de poetas", "La hermosa palabra", "El tambor ritual", "Mi canto del corazón")
            19 -> listOf("Tepeyollotl", "Ocelotl", "Yoalli", "Yohualli Ehecatl", "Mahuiztli", "Oztotl") to
                  listOf("El jaguar divino", "El jaguar nocturno", "La noche oscura", "El viento de la noche", "La honra guerrera", "La cueva sagrada")
            20 -> listOf("Tepozmatlatl", "Tepozamatlacuilolli", "Tepoznepaloni", "Tepozmatlamatiliztli", "Tepozcemanahuatl", "Tepozcode") to
                  listOf("La red de internet", "El correo electrónico", "La interfaz digital", "La cibernética", "El mundo digital", "El código de silicio")
            21 -> listOf("Teotl", "Ometeotl", "Tlamatiliztli", "Tlamatini", "Macehualtlamatiliztli", "Cemixmatiliztli") to
                  listOf("La energía cósmica", "La dualidad creadora", "La sabiduría", "El sabio filósofo", "La conciencia colectiva", "El entendimiento pleno")
            22 -> listOf("Quetzalcoatl", "Mictlantecuhtli", "Cozamalotl", "Nahualli", "Tlacuilo-amatl", "Huitzilopochtli") to
                  listOf("La serpiente emplumada", "El señor del Mictlán", "La leyenda del arcoíris", "El nagual protector", "El códice de leyendas", "El colibrí de la voluntad")
            23 -> listOf("Tepati", "Papatli", "Temazcalli", "Chicahualiztli", "Yolpatic", "Xochipapatli") to
                  listOf("El curandero sabio", "La planta curativa", "El baño temazcal", "La salud fuerte", "El elixir del corazón", "La flor de medicina")
            24 -> listOf("Tlahtoani", "Tecpancaltzin", "Nahuatilli", "Tlahtolli chicahuac", "Tepoztequiliztli", "Altepeconetl") to
                  listOf("El gobernante supremo", "El gran palacio", "La ley justa", "La palabra firme", "La vara de mando", "El consejo del pueblo")
            25 -> listOf("Teocalli", "Tepoztecatl", "Tetl", "Tepozcalli", "Citlalmachiyotl", "Chinamitlapal") to
                  listOf("La gran pirámide", "El arquitecto", "La piedra grabada", "La bóveda del templo", "La alineación estelar", "El muro pintado")
            26 -> listOf("Cuauhtla-quixqui", "Ocelo-quixqui", "Chimalli", "Macuahuitl", "Tepozmitl", "Yaocelotl") to
                  listOf("El guerrero águila", "El guerrero jaguar", "El escudo emplumado", "La macana de obsidiana", "La flecha templada", "El jaguar de guerra")
            27 -> listOf("Nezahualcoyotl", "Acolhuacan", "Xochimeh", "Canto-tlalli", "Teyolocuicatl", "Tlamatilizyolotl") to
                  listOf("El rey poeta", "El señorío lírico", "Las flores de poesía", "El valle de cantares", "El poema espiritual", "El saber lírico")
            28 -> listOf("Xiuhpohualli", "Tonalpohualli", "Tonalpouhqui", "Nemontemi", "Cempohualilhuitl", "Metztli-cuenta") to
                  listOf("El calendario solar", "El calendario adivinatorio", "El lector de días", "Los días de reflexión", "El festival de veintena", "La cuenta de lunas")
            29 -> listOf("Tonalpouhqui", "Tonalli", "Cozamalotl", "Citlalin oncan", "Teopixqui", "Yolmachiyotl") to
                  listOf("El sacerdote lector", "El signo de nacimiento", "El arcoíris protector", "La estrella de destino", "El astrólogo del templo", "La señal del alma")
            30 -> listOf("Tepozixmatiliztli", "Tepoznemilini", "Tepozmachiyoti", "Tepozmatlatl neuronal", "Tepozamatlacuilolli", "Tepoznemiliztli") to
                  listOf("La inteligencia artificial", "La computadora neuronal", "El algoritmo digital", "La red neuronal", "La base de datos", "La conciencia cibernética")
            else -> listOf("Macehualli", "Tlamatiliztli", "Cali", "Atl", "Tletl", "Yollotl") to
                    listOf("El habitante", "La sabiduría", "La casa", "El agua", "El fuego", "El corazón")
        }

        val (verbsN, verbsS) = when (unit) {
            1 -> listOf("nonotza", "tlazohcamati", "paqui", "yolcehuia", "pialia", "nonotzaloa") to
                 listOf("saluda", "agradece", "se alegra", "brinda tranquilidad", "saluda afectuosamente", "da la bienvenida")
            2 -> listOf("pohua", "tlanextia", "tlahvia", "nemilia", "ihcac", "cempohua") to
                 listOf("cuenta", "alumbra", "ilumina", "calcula / medita", "permanece firme", "mide los ciclos")
            3 -> listOf("tlazohtla", "tequiti", "momachtia", "nonotza", "tlachia", "yolcehuia") to
                 listOf("ama", "trabaja", "aprende", "dialoga", "cuida", "consuela")
            4 -> listOf("choloa", "patlani", "nemi", "tlacua", "quitemoa", "tzicni") to
                 listOf("corre / salta", "vuela alto", "habita / camina", "se alimenta", "busca con atención", "salta alegremente")
            5 -> listOf("tlacualchia", "popoca", "patli", "tzopelia", "molia", "yecoa") to
                 listOf("prepara comida", "humea", "sana", "endulza", "muele finamente", "cocina con amor")
            6 -> listOf("tlapala", "tlanextia", "chipahua", "melahua", "yecoa", "quichihua") to
                 listOf("pinta", "resplandece", "limpia / purifica", "suaviza las fibras", "diseña con arte", "elabora colores")
            7 -> listOf("tzicni", "tlanemilia", "tlachia", "caqui", "chicahua", "nemiliztli") to
                 listOf("palpita", "piensa", "observa", "escucha", "fortalece", "da energía vital")
            8 -> listOf("tequiti", "momachtia", "tlacua", "cochi", "mehua", "nemiliztli") to
                 listOf("trabaja", "estudia", "come", "duerme", "se levanta", "actúa")
            9 -> listOf("huetzi", "pica", "tlatlatzinia", "tlahvia", "ceceya", "quiahua") to
                 listOf("cae suavemente", "sopla", "truena con fuerza", "ilumina el cielo", "enfría los campos", "derrama lluvia")
            10 -> listOf("tequipanoa", "ihcuiloa", "tlachia", "nonotza", "chihua", "tlanemilia") to
                  listOf("procesa datos", "escribe código", "observa la pantalla", "enlaza sistemas", "ejecuta", "resuelve ecuaciones")
            11 -> listOf("tlanamaca", "tlacohuia", "patla", "pohua", "nonotza", "tzopelia") to
                  listOf("vende", "compra", "intercambia", "cuenta las ganancias", "negocia el precio", "ofrece chocolate")
            12 -> listOf("chihua", "tlaxcaloa", "tlapala", "tequiti", "quitzquia", "quetza") to
                  listOf("construye", "amasa harina", "pinta dibujos", "trabaja duro", "sostiene la herramienta", "yergue muros")
            13 -> listOf("ihcac", "nemi", "machiyotia", "quitzquia", "tlanextia", "huetzi") to
                  listOf("se alza", "habita", "delimita la frontera", "protege", "embellece el paisaje", "fluye agua")
            14 -> listOf("mitotia", "tlenamactia", "tzotzona", "popoca", "cuica", "yolpaqui") to
                  listOf("danza alegremente", "ofrece sahumerio", "toca música de percusión", "despide humo sagrado", "canta himnos", "celebra con júbilo")
            15 -> listOf("paqui", "choca", "yolchicahua", "yolcehuia", "tlazohtla", "pialia") to
                  listOf("se alegra", "llora", "se llena de valor", "encuentra calma", "ama intensamente", "siente")
            16 -> listOf("pixca", "toca", "quiahua", "yoliza", "tequiti", "chipahua") to
                  listOf("cosecha las mazorcas", "siembra las semillas", "riega el campo", "germina", "labra la milpa", "deshierba la tierra")
            17 -> listOf("quihcuiloa", "quitzquia", "quipia", "tlapala", "quichihua", "yecoa") to
                  listOf("borda patrones", "teje en telar", "porta con orgullo", "tiñe telas", "crea plomería artística", "viste de gala")
            18 -> listOf("cuicapiqui", "tzotzona", "nonotza", "tlanemilia", "quiahua", "cuica") to
                  listOf("compone versos", "toca el tambor", "recita poemas", "medita sus cantos", "derrama metáforas", "entona melodías")
            19 -> listOf("choloa", "tlachia", "nemilia", "chicahua", "quixmatia", "nemi") to
                  listOf("merodea en la sombra", "vigila con fuego", "gobierna los bosques", "da fuerza mística", "conoce los misterios", "recorre los senderos")
            20 -> listOf("nonotza", "tequipanoa", "tlapohua", "quitzquia", "chicahua", "chihua") to
                  listOf("conecta terminales", "procesa flujos", "desencripta algoritmos", "establece comunicación", "da soporte técnico", "programa redes")
            21 -> listOf("tlanemilia", "tlachia", "nemilia", "chicahua", "quitzquia", "paqui") to
                  listOf("reflexiona sobre el cosmos", "observa la verdad", "comprende la vida", "da armonía al espíritu", "sostiene la sabiduría", "se regocija")
            22 -> listOf("pohua", "tlachia", "ihcac", "nemi", "chihua", "nonotza") to
                  listOf("relata los mitos", "revela leyendas", "persiste en el tiempo", "viaja espiritualmente", "realiza portentos", "conversa con deidades")
            23 -> listOf("tepatia", "popoca", "quitzquia", "yolcehuia", "chicahua", "molia") to
                  listOf("cura enfermedades", "sahuma purificando", "aplica hierbas", "alivia la mente", "devuelve la fuerza", "muele plantas medicinales")
            24 -> listOf("tlahtoa", "nahuatia", "quitzquia", "quixmatia", "tequipanoa", "melahua") to
                  listOf("dictamina justicia", "decreta ordenanzas", "empuña la vara de mando", "hace cumplir la ley", "gobierna al pueblo", "da equidad")
            25 -> listOf("quetza", "quihcuiloa", "machiyotia", "ihcac", "tlanextia", "chihua") to
                  listOf("edifica la pirámide", "labra la piedra", "mide los astros", "se yergue solemne", "ilumina el valle", "traza cimientos")
            26 -> listOf("quitzquia", "yaotl chihua", "choloa", "chicahua", "yaoyecoa", "tlanextia") to
                  listOf("empuña el escudo", "combate con honor", "avanza sigilosamente", "lucha con valentía", "concluye el asalto", "deslumbra en combate")
            27 -> listOf("cuicapiqui", "tlanemilia", "quiahua", "nemilia", "yolcehuia", "cuica") to
                  listOf("compone poesía lírica", "medita en el cambio", "derrama cantares", "busca la verdad espiritual", "consuela las almas", "declama versos")
            28 -> listOf("pohua", "machiyotia", "tlachia", "nemilia", "yecoa", "cempohua") to
                  listOf("registra los años", "traza el solsticio", "observa las constelaciones", "calcula eclipses", "termina el ciclo solar", "cuenta los meses")
            29 -> listOf("tlachia", "nonotza", "tlanemilia", "chicahua", "yolpialia", "pohua") to
                  listOf("interpreta presagios", "revela el destino", "descifra el porvenir", "limpia la mala energía", "guarda el secreto natal", "lee las estrellas")
            30 -> listOf("tlanemilia", "tequipanoa", "nonotza", "ihcuiloa", "chihua", "chipahua") to
                  listOf("piensa de forma artificial", "procesa flujos cibernéticos", "interconecta nodos", "programa código digital", "ejecuta neologismos", "optimiza la red")
            else -> listOf("nemi", "tequiti", "chihua", "tlachia", "paqui", "momachtia") to
                    listOf("camina", "trabaja", "hace", "mira", "ríe", "aprende")
        }

        val (locsN, locsS) = when (unit) {
            1 -> listOf("ihuan yollotl", "tlanextli oncan", "teopancaltzinco", "cali ipan", "yolcehuiliztli ihuan", "tlalnamiquiliztli can") to
                 listOf("con el corazón", "en la claridad del día", "en el templo", "dentro de la casa", "con tranquilidad de espíritu", "con buen pensamiento")
            2 -> listOf("cempohualli can", "ilhuicac", "cemitzmitl ipan", "mictlan", "tonalmitl ihuan", "citlalmatlatl tloc") to
                 listOf("veinte veces", "en la bóveda celeste", "en la medida cósmica", "hacia el inframundo", "con rayos de sol", "cerca de la constelación")
            3 -> listOf("cali oncan", "chan ihuan", "macehualtlalli ipan", "yolotl tzon", "tlahtolli can", "calmilli tloc") to
                 listOf("en la casa familiar", "junto a la familia", "en las tierras del pueblo", "desde el corazón", "con tiernas palabras", "junto al huerto familiar")
            4 -> listOf("cuauhtla", "xochitl can", "tlallan", "zacatlan ipan", "cuauhmeh tzon", "meyaltzin tloc") to
                 listOf("en la selva profunda", "entre flores silvestres", "en la madre tierra", "en el pastizal", "en las copas de los árboles", "junto al manantial")
            5 -> listOf("tletl tloc", "comalli ipan", "molcaxitl oncan", "tlacualoyan", "xocolatl ihuan", "etepantla can") to
                 listOf("cerca del fuego", "sobre el comal caliente", "en el molcajete de piedra", "en la cocina tradicional", "con agua de chocolate", "en la olla de frijoles")
            6 -> listOf("amatl ipan", "icpatl ihuan", "xochitl tlapal", "cali tzacual", "icpalli oncan", "amatlacuilol tzon") to
                 listOf("en el papel amate", "con hilos tejidos a mano", "con pigmentos de flores", "en el muro sagrado", "en el trono de estera", "en las páginas del códice")
            7 -> listOf("nemiliztli can", "tlalnamiquiliztli ihuan", "nacayo can", "tonal tloc", "yoliliztli tzon", "ixtli tloc") to
                 listOf("con gran vitalidad", "con un pensamiento sano", "en todo el cuerpo", "junto al alma protectora", "en el origen de la vida", "en la mirada")
            8 -> listOf("tlaneci can", "yohuac oncan", "milli ipan", "calmilli tloc", "tonatiuh tlanextli ihuan", "tequipanoyan can") to
                 listOf("durante el amanecer", "en la noche estrellada", "en la milpa", "cerca del jardín", "bajo la luz del sol", "en el lugar de trabajo")
            9 -> listOf("tlalticpactli ipan", "cuauhtla can", "tepetl tzon", "amelli tloc", "ilhuicac oncan", "ehecatl can") to
                 listOf("sobre la faz de la tierra", "en los bosques densos", "en la cima de la montaña", "cerca del manantial cristalino", "en el firmamento", "por acción del viento")
            10 -> listOf("tepozmatlatl ipan", "tepozcuaxicolli tzon", "tepozpantli yohuac", "tepozixmatiliztli ihuan", "tepozmachiyotl oncan", "tepozmatlatl neuronal") to
                  listOf("en la red de internet", "en el procesador de silicio", "en la pantalla interactiva", "con inteligencia digital", "en el archivo de código", "en el nodo neuronal")
            11 -> listOf("tianquizco oncan", "cacao-tlalpan", "mecatl ihuan", "chiquihuitl ipan", "tlantli tloc", "tianquiztli tzon") to
                  listOf("en el mercado del tianguis", "con valiosos granos de cacao", "mediante cuerdas de medición", "en la canasta de mimbre", "junto al puesto de venta", "en la plaza principal")
            12 -> listOf("tepoztepactli ihuan", "amatl ipan", "tlalticpac", "tequipanoyan", "tepoztli tloc", "cali tzacual tloc") to
                  listOf("con cincel de bronce", "en la superficie de papel amate", "sobre la tierra fértil", "en el taller de oficios", "junto a las herramientas", "cerca de la construcción")
            13 -> listOf("Anahuac ipan", "tepetlan", "ameyaltzin tloc", "oztoc oncan", "chinamitl can", "altepetl tzon") to
                  listOf("en los valles del Anáhuac", "entre las montañas escarpadas", "junto al manantial sagrado", "en la cueva ceremonial", "en la chinampa fértil", "en los linderos del pueblo")
            14 -> listOf("teopancaltzinco", "tletl tloc", "copaltzin ihuan", "xochimeh can", "ilhuicac oncan", "mitotiliztli ipan") to
                  listOf("en el recinto del templo", "junto al fuego ceremonial", "con el humo del incienso de copal", "entre flores sagradas", "frente a los astros del cielo", "en la gran pista de danza")
            15 -> listOf("nemiliztli ipan", "yollotl oncan", "tlalnamiquiliztli ihuan", "tlahtolli can", "cemixmatiliztli tzon", "yolnepantla can") to
                  listOf("en el transcurso de la vida", "en lo profundo del pecho", "con nobles pensamientos", "mediante dulces palabras", "en el estado de plenitud", "con paz en el corazón")
            16 -> listOf("milli ipan", "tlalli chipahuac ihuan", "tonatiuh tlanextli tloc", "tlaolli chiquihuitl oncan", "ameyalli can", "atoctli ipan") to
                  listOf("en el campo de la milpa", "con tierra fértil y abonada", "bajo el sol del mediodía", "en el cesto de mazorcas", "cerca del canal de riego", "en la planicie aluvial")
            17 -> listOf("tzotzopaztli ihuan", "quitzalli ihuan", "tonal can", "pantli oncan", "cali tzacual tzon", "huepilli ipan") to
                  listOf("con el madero del telar de cintura", "con hermosas plumas de quetzal", "con un brillo ancestral", "en el lienzo de algodón blanco", "en la recámara del palacio", "sobre el bordado fino")
            18 -> listOf("xochitlalpan", "ilhuicac can", "yolotl tloc", "cuicacalco oncan", "tlamatiliztli ihuan", "xochicuicatl ipan") to
                 listOf("en la tierra de las flores del saber", "con inspiración del cielo", "cerca de los corazones de la gente", "en la casa de cantos y danzas", "con gran elocuencia poética", "en la cadencia del cantar")
            19 -> listOf("oztoc ipan", "cuauhtla yohuac", "tepetl tzon", "mictlan can", "tonal tloc", "ocelocalco oncan") to
                  listOf("en la cueva del jaguar", "en la selva nocturna y lluviosa", "en el corazón de la montaña", "hacia los rumbos del inframundo", "con protección del espíritu", "en el cuartel de los guerreros")
            20 -> listOf("tepozmatlatl ipan", "tepozcuaxicolli tzon", "tepozicpatl ihuan", "tepozmachiyoti can", "tepozpantli oncan", "tepozcode can") to
                  listOf("en el ciberespacio global", "dentro de la unidad de proceso digital", "con cables de fibra de vidrio", "mediante algoritmos binarios", "en la interfaz del monitor", "en el segmento de código")
            21 -> listOf("Anahuac ipan", "cempoal tlalpan", "yolotl tzon", "teocaltzin tloc", "ilhuicac ihuan", "tlamatiliztli can") to
                  listOf("en el pensamiento del Anáhuac", "en la totalidad del universo", "en lo más hondo del alma", "frente al altar del templo", "en armonía celeste", "desde la gran sabiduría")
            22 -> listOf("yohualnepantla", "mictlan oncan", "altepetl tzon", "cuauhtlan can", "tletl tloc", "legend-amatl ipan") to
                  listOf("a altas horas de la noche", "en el reino mítico del Mictlán", "en la memoria viva de los abuelos", "en los bosques de leyenda", "junto al fuego de la fogata", "en los textos sagrados de amate")
            23 -> listOf("temazcalco oncan", "papatli can", "atl tloc", "yollotl ipan", "tlalli tzon", "tepatiliztli ihuan") to
                  listOf("dentro del baño de temazcal", "con compresas de hojas curativas", "con infusiones de agua de manantial", "en el centro del espíritu", "con raíces de la madre tierra", "mediante rezos de curación")
            24 -> listOf("tecpanco oncan", "nahuatilpan", "altepetl ipan", "tlamatiliztli ihuan", "macehualtlalli can", "tlahtoque tloc") to
                  listOf("en la gran corte del palacio", "bajo el marco de las leyes", "a lo largo del territorio del pueblo", "con absoluta justicia", "en las parcelas comunales", "junto al consejo de ancianos")
            25 -> listOf("ilhuicac tzon", "tepan can", "tetl ihuan", "altepeco oncan", "tonal tloc", "teocalli ipan") to
                  listOf("apuntando al cenit celeste", "en las terrazas de piedra", "con grandes bloques labrados", "en el centro de la ciudad", "alineado al equinoccio solar", "en la cumbre de la pirámide")
            26 -> listOf("yaotlalpan", "chimalli ihuan", "macehualli can", "tepetl tloc", "tonatiuh tzon", "yaocalco oncan") to
                  listOf("en el campo de batalla de honor", "con escudo y macana de obsidiana", "por la defensa de los macehuales", "en las faldas del cerro fortificado", "bajo el astro sol de guerra", "en la guarnición militar")
            27 -> listOf("xochitlalpan", "cemitzmitl can", "ilhuicac ihuan", "yolotl oncan", "amatlacuilol tzon", "nezahual-calco") to
                  listOf("en el prado florido de la sabiduría", "en este breve instante terrenal", "sintonizado con el cielo", "en lo hondo del corazón del hombre", "en las páginas pintadas a mano", "en los jardines del palacio real")
            28 -> listOf("tonalmatlatl ipan", "ilhuicac tzon", "teopantla can", "cemixmatiliztli ihuan", "tonali can", "pohualoyan can") to
                  listOf("en la superficie de la gran piedra solar", "con la posición de las pléyades", "en el patio del observatorio", "con total precisión calendárica", "en los días marcados por el destino", "en el sitio de cómputo astronómico")
            29 -> listOf("tonal-amatl ipan", "ilhuicac can", "yollotl tzon", "teocalco oncan", "cempohual can", "tonali can") to
                  listOf("en el libro pictográfico del destino", "conforme a los astros celestes", "en la personalidad natal del recién nacido", "en el santuario de lectura de códices", "en las veinte fuerzas primordiales", "bajo el glifo protector")
            30 -> listOf("tepozmatlatl neuronal ipan", "tepozcuaxicolli tzon", "tepozpantli yohuac", "digital-amatl can", "cemanahuac ipan", "tepozixmatiliztli tloc") to
                  listOf("en la red neuronal artificial", "dentro del clúster de cómputo electrónico", "en la pantalla holográfica", "en la base de datos distribuida en la nube", "en el ecosistema cibernético", "junto a los agentes inteligentes artificiales")
            else -> listOf("tlalticpac", "ilhuicac", "cali", "cuauhtla", "opan", "milli ipan") to
                    listOf("en la tierra", "en el cielo", "en la casa", "en la selva", "arriba", "en la milpa")
        }

        val generalSubjN = listOf("Temachtiani", "Mumachtli", "Cihuapilli", "Piltzintli", "Macehualli", "Tlacuilo")
        val generalSubjS = listOf("El maestro", "El alumno", "La noble dama", "El niño amado", "La persona del pueblo", "El escribano de códices")
        
        val generalVerbN = listOf("ahci", "itqui", "neci", "tlahtoa", "caqui", "itta")
        val generalVerbS = listOf("llega", "lleva / porta", "aparece", "habla", "escucha", "ve / observa")
        
        val generalLocN = listOf("ipan altepetl", "chanco", "ixpan teocalli", "tloc calmilli", "ipan cuauhtla", "tzon tepetl")
        val generalLocS = listOf("en la ciudad", "en el hogar", "frente al templo", "junto al huerto", "en la selva", "en la cima del cerro")

        val extendedSubjN = subjectsN + generalSubjN
        val extendedSubjS = subjectsS + generalSubjS
        val extendedVerbN = verbsN + generalVerbN
        val extendedVerbS = verbsS + generalVerbS
        val extendedLocN = locsN + generalLocN
        val extendedLocS = locsS + generalLocS

        val seed = (idx * 1337L + k * 101L + unit * 73L)
        val rand = java.util.Random(seed)
        
        val sIdx = rand.nextInt(extendedSubjN.size)
        val vIdx = rand.nextInt(extendedVerbN.size)
        val lIdx = rand.nextInt(extendedLocN.size)
        val modIdx = rand.nextInt(6)

        val baseN = extendedSubjN[sIdx]
        val baseS = extendedSubjS[sIdx]

        val (modifiedN, modifiedS) = applyModifier(baseN, baseS, modIdx)

        val nPhrase = "$modifiedN ${extendedVerbN[vIdx]} ${extendedLocN[lIdx]}"
        val sPhrase = "$modifiedS ${extendedVerbS[vIdx]} ${extendedLocS[lIdx]}"
        return Pair(nPhrase, sPhrase)
    }

    private fun applyModifier(nahuatlSubj: String, spanishSubj: String, modIdx: Int): Pair<String, String> {
        if (modIdx == 0) return Pair(nahuatlSubj, spanishSubj)
        
        val (nahuatlMod, spanishModMasculine, spanishModFeminine) = when (modIdx) {
            1 -> Triple("Hueyi ", "gran ", "gran ")
            2 -> Triple("Chicahuac ", "fuerte ", "fuerte ")
            3 -> Triple("Cualli ", "buen ", "buena ")
            4 -> Triple("Yancuic ", "nuevo ", "nueva ")
            5 -> Triple("Chipahuac ", "puro ", "pura ")
            else -> Triple("Yolpaqui ", "alegre ", "alegre ")
        }

        val finalN = nahuatlMod + nahuatlSubj
        
        val finalS = when {
            spanishSubj.startsWith("El ") -> {
                val base = spanishSubj.removePrefix("El ")
                if (modIdx == 3) "El buen $base" else "El $spanishModMasculine$base"
            }
            spanishSubj.startsWith("La ") -> {
                val base = spanishSubj.removePrefix("La ")
                "La $spanishModFeminine$base"
            }
            spanishSubj.startsWith("Los ") -> {
                val base = spanishSubj.removePrefix("Los ")
                "Los ${spanishModMasculine}s $base"
            }
            spanishSubj.startsWith("Las ") -> {
                val base = spanishSubj.removePrefix("Las ")
                "Las ${spanishModFeminine}s $base"
            }
            spanishSubj.startsWith("Mi ") -> {
                val base = spanishSubj.removePrefix("Mi ")
                "Mi $spanishModMasculine$base"
            }
            else -> "$spanishModMasculine$spanishSubj"
        }
        
        return Pair(finalN, finalS)
    }

    private fun getUnitBaseExplanation(title: String): String {
        return when (title) {
            "Saludos y Cortesía" -> "Aprenderemos la cortesía ritual nahua, donde saludar va más allá de un gesto físico y busca sintonizar los corazones en armonía ('yolcehuia')."
            "Matemáticas y Cosmos" -> "Estudiaremos la concepción vigesimal del espacio-tiempo, donde el conteo de 20 en 20 se alinea con la biología humana y el movimiento de los astros."
            "La Familia y Personas" -> "Aprenderemos los términos de parentesco y cómo el uso de afijos posesivos ('no-', 'mo-', 'i-') define el estrecho tejido social y respeto comunitario."
            "Flora y Fauna Silvestre" -> "Exploraremos el vocabulario biológico nahua, identificando cómo los nombres de plantas y animales describen de forma precisa sus hábitos y hábitat."
            "Alimentos y Cocina" -> "Nos adentraremos en el vocabulario gastronómico sagrado, conociendo la molienda del maíz ('tlaxcalli') y los términos del fuego sagrado de la cocina."
            "Colores y Texturas" -> "Analizaremos el origen poético de los colores en náhuatl, muchos de los cuales provienen de minerales, flores o insectos como la grana cochinilla."
            "El Cuerpo Humano" -> "Estudiaremos la anatomía desde una perspectiva espiritual nahua, donde órganos como el corazón ('yollotl') y el hígado ('elli') albergan fuerzas vitales animadas."
            "Acciones del Día" -> "Conjugaremos verbos cotidianos de acción intransitiva y transitiva, estructurando oraciones completas sobre las labores desde el amanecer hasta el anochecer."
            "Clima y Estaciones" -> "Aprenderemos a describir fenómenos atmosféricos como la lluvia ('quiahuitl') y el viento ('ehecatl'), entendidos como deidades activas que moldean la tierra."
            "La Computadora y el Código" -> "Exploraremos la adopción de la tecnología en la lengua náhuatl, donde se combinan raíces como 'tepoztli' (metal/máquina) y 'amatl' (papel/libro) para nombrar herramientas modernas."
            "El Comercio y Tianguis" -> "Estudiaremos la economía tradicional del tianguis ('tianquiztli'), el sistema de trueque y las unidades de medida ancestrales usadas para el intercambio de bienes."
            "Herramientas y Oficios" -> "Aprenderemos los nombres de las herramientas tradicionales de labranza y los oficios mesoamericanos de carpinteros, canteros, tejedores y pintores de códices."
            "Geografía y Pueblos" -> "Aprenderemos la toponimia náhuatl, descubriendo cómo los nombres de los pueblos mesoamericanos describen exactamente su relieve geográfico y recursos naturales."
            "Fiestas y Ceremonias" -> "Analizaremos la estructura ceremonial y ritual de las festividades nahuas, prestando atención al vocabulario sagrado del canto, la danza y el copal aromatizante."
            "Las Emociones y Sentir" -> "Exploraremos el rico léxico de los estados anímicos y emocionales, donde el amor, la tristeza y el enojo se expresan mediante metáforas vinculadas al corazón."
            "Agricultura y Cosechas" -> "Estudiaremos el ciclo agrícola de la milpa, la siembra del maíz y los términos técnicos del cultivo que han sostenido la vida mesoamericana por milenios."
            "Vestimenta y Arte" -> "Conoceremos los nombres de las prendas tradicionales como el huipil ('huepilli') y el maxtlatl, así como la simbología plasmada en los tejidos de telar de cintura."
            "Cantos y Expresiones" -> "Analizaremos los cantos floridos o poesía lírica náhuatl, aprendiendo a estructurar frases rítmicas con palabras compuestas de alto valor estético."
            "Mitología del Jaguar" -> "Estudiaremos el simbolismo del jaguar ('ocelotl'), el señor de la noche y el inframundo, examinando oraciones que describen su sigilo y fuerza sagrada."
            "Sistemas Digitales y Redes" -> "Profundizaremos en la estructura morfológica de los sistemas digitales, donde el náhuatl utiliza conceptos poéticos como 'matlatl' (red) para referirse a internet."
            "Filosofía Ancestral" -> "Nos adentraremos en el pensamiento profundo de los tlamatinime (sabios), analizando el concepto de 'Teotl' (la energía suprema) y la dualidad creadora del universo."
            "Leyendas Precolombinas" -> "Estudiaremos relatos mitológicos sobre la creación del sol, la luna y el hombre, enriqueciendo nuestra comprensión lectora mediante narraciones complejas."
            "Medicina Tradicional" -> "Conoceremos el vocabulario de la herbolaria sagrada, los temazcales y las prácticas curativas nahuas basadas en equilibrar las energías frías y calientes."
            "Leyes y Gobernantes" -> "Analizaremos la estructura política mesoamericana, los discursos solemnes de los gobernantes ('tlahtoque') y el vocabulario de la justicia y los tribunales."
            "Arquitectura Piramidal" -> "Aprenderemos los términos constructivos de los centros ceremoniales, la alineación astronómica de los templos y los nombres de las partes de una pirámide."
            "El Arte de la Guerra" -> "Estudiaremos la organización de las órdenes guerreras (águilas y jaguares), la disciplina militar y los términos históricos de la defensa territorial azteca."
            "Poesía de Nezahualcóyotl" -> "Leeremos y analizaremos fragmentos poéticos atribuidos al rey poeta de Texcoco, prestando especial atención a las metáforas sobre la brevedad de la vida."
            "El Tiempo y Calendarios" -> "Comprenderemos el funcionamiento del calendario solar ('xiuhpohualli') y el calendario sagrado ('tonalpohualli'), así como la división de los días y meses."
            "El Oráculo del Sol" -> "Analizaremos los vaticinios y la lectura del destino según el día de nacimiento, utilizando construcciones gramaticales complejas del náhuatl clásico."
            "Inteligencia Artificial y Neologismos" -> "Debatiremos cómo la conciencia de las máquinas puede ser expresada bajo la óptica de la filosofía nahua a través de metáforas de pensamiento y redes neuronales."
            else -> "Analizaremos la estructura morfológica de palabras y expresiones nahuas tradicionales para asimilar su uso práctico cotidiano."
        }
    }

    // --- Sacerdote Audio Cache Room Operations ---

    suspend fun getCachedSacerdoteAudioByHash(hash: String): SacerdoteAudioCacheEntity? = withContext(Dispatchers.IO) {
        sacerdoteAudioCacheDao?.getCachedAudioByHash(hash)
    }

    suspend fun getCachedSacerdoteAudioByText(queryText: String): SacerdoteAudioCacheEntity? = withContext(Dispatchers.IO) {
        sacerdoteAudioCacheDao?.getCachedAudioByText(queryText.trim())
    }

    suspend fun saveCachedSacerdoteAudio(
        textHash: String,
        queryText: String,
        filePath: String,
        fileSizeBytes: Long = 0L,
        audioFormat: String = "wav"
    ) = withContext(Dispatchers.IO) {
        val entity = SacerdoteAudioCacheEntity(
            textHash = textHash,
            queryText = queryText.trim(),
            filePath = filePath,
            audioFormat = audioFormat,
            fileSizeBytes = fileSizeBytes,
            createdAt = System.currentTimeMillis(),
            lastPlayedAt = System.currentTimeMillis(),
            playCount = 1
        )
        sacerdoteAudioCacheDao?.insertCacheEntry(entity)
    }

    suspend fun recordSacerdoteAudioPlayed(hash: String) = withContext(Dispatchers.IO) {
        sacerdoteAudioCacheDao?.recordAudioPlayed(hash)
    }

    suspend fun deleteSacerdoteAudioCacheByHash(hash: String) = withContext(Dispatchers.IO) {
        sacerdoteAudioCacheDao?.deleteByHash(hash)
    }

    suspend fun clearSacerdoteAudioCache() = withContext(Dispatchers.IO) {
        sacerdoteAudioCacheDao?.clearCache()
    }
}
