package com.example.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

// --- Room Database Entities ---

@Entity(tableName = "user_progress")
data class UserProgress(
    @PrimaryKey val id: Int = 1,
    val xp: Int = 0,
    val level: Int = 1,
    val streakDays: Int = 1,
    val lastPracticeTime: Long = 0,
    val completedLessonsCount: Int = 0,
    val translationsCount: Int = 0,
    val matchesPlayed: Int = 0,
    val soundSettingsOn: Boolean = true,
    val notificationTime: String = "08:00 PM",
    val notificationEnabled: Boolean = true,
    val userEmail: String = "",
    val isLogged: Boolean = false,
    val cloudBackupTimestamp: Long = 0,
    val lastActiveLessonId: Int = 1,
    val isBiometricLockEnabled: Boolean = false,
    val biometricPinBypass: String = "1234",
    val lives: Int = 5,
    val lastLifeRestoreTime: Long = 0,
    val petType: String = "",
    val petName: String = "",
    val petHunger: Int = 100,
    val petHappiness: Int = 100,
    val petAccessories: String = "",
    val petLastCaredTime: Long = 0,
    val isPetDead: Boolean = false,
    val activeShieldCharges: Int = 0,
    val selectedLanguage: String = "Nahuatl",
    val selectedNahuatlVariant: String = "Náhuatl de la Huasteca veracruzana",
    val selectedTotonaVariant: String = "Totonaco de la costa",
    val selectedMayaVariant: String = "Maya Yucateco (Maayat'aan)",
    val selectedZapotecoVariant: String = "Zapoteco del Istmo (Diidxazá)",
    val selectedMixtecoVariant: String = "Mixteco de la Mixteca Alta (Tu'un Savi)",
    val selectedOtomiVariant: String = "Otomí del Valle del Mezquital (Hñähñu)",
    val completedLessonsNahuatl: String = "",
    val completedLessonsOtomi: String = "",
    val completedLessonsTotona: String = "",
    val completedLessonsMaya: String = "",
    val completedLessonsZapoteco: String = "",
    val completedLessonsMixteco: String = "",
    val matchesPlayedNahuatl: Int = 0,
    val matchesPlayedOtomi: Int = 0,
    val matchesPlayedTotona: Int = 0,
    val matchesPlayedMaya: Int = 0,
    val matchesPlayedZapoteco: Int = 0,
    val matchesPlayedMixteco: Int = 0,
    val hasSeenIntroStory: Boolean = false
)

@Entity(tableName = "lessons")
data class LessonEntity(
    @PrimaryKey val id: Int, // 1 to 1500
    val title: String,
    val unit: Int, // 1 to 30 (each has 50 lessons)
    val category: String, // Vocabulario, Conversación, Gramática, Cultura, Frases
    val explanation: String,
    val nahuatlPhrases: String, // Semicolon separated, or simple format
    val spanishTranslations: String, // Semicolon separated
    val isCompleted: Boolean = false,
    val difficulty: String = "Principiante", // Principiante, Intermedio, Avanzado
    val aiSummary: String = "",
    val didYouKnow: String = ""
)

@Entity(tableName = "words")
data class WordEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val nahuatl: String,
    val spanish: String,
    val pronunciation: String,
    val category: String, // "Saludos", "Números", "Animales", "Familia", "Comida", "Naturaleza"
    val sentenceNahuatl: String = "",
    val sentenceSpanish: String = ""
)

@Entity(tableName = "challenges")
data class WeeklyChallenge(
    @PrimaryKey val id: Int,
    val title: String,
    val description: String,
    val currentProgress: Int,
    val targetProgress: Int,
    val xpReward: Int,
    val completed: Boolean = false
)


// --- DAOs (Data Access Objects) ---

@Dao
interface UserProgressDao {
    @Query("SELECT * FROM user_progress WHERE id = 1 LIMIT 1")
    fun getProgressFlow(): Flow<UserProgress?>

    @Query("SELECT * FROM user_progress WHERE id = 1 LIMIT 1")
    suspend fun getProgress(): UserProgress?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateProgress(progress: UserProgress)

    @Query("UPDATE user_progress SET xp = xp + :gainedXp WHERE id = 1")
    suspend fun addXp(gainedXp: Int)
}

@Dao
interface LessonDao {
    @Query("SELECT * FROM lessons ORDER BY id ASC")
    fun getAllLessonsFlow(): Flow<List<LessonEntity>>

    @Query("SELECT * FROM lessons WHERE id = :id LIMIT 1")
    suspend fun getLessonById(id: Int): LessonEntity?

    @Query("SELECT COUNT(*) FROM lessons")
    suspend fun getLessonsCount(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLessons(lessons: List<LessonEntity>)

    @Query("UPDATE lessons SET isCompleted = :completed WHERE id = :id")
    suspend fun updateLessonCompletion(id: Int, completed: Boolean)

    @Query("DELETE FROM lessons")
    suspend fun clearAllLessons()
}

@Dao
interface WordDao {
    @Query("SELECT * FROM words ORDER BY nahuatl ASC")
    fun getAllWordsFlow(): Flow<List<WordEntity>>

    @Query("SELECT COUNT(*) FROM words")
    suspend fun getWordsCount(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWords(words: List<WordEntity>)

    @Query("SELECT * FROM words WHERE nahuatl LIKE '%' || :query || '%' OR spanish LIKE '%' || :query || '%'")
    suspend fun searchWords(query: String): List<WordEntity>
}

@Dao
interface ChallengeDao {
    @Query("SELECT * FROM challenges ORDER BY id ASC")
    fun getAllChallengesFlow(): Flow<List<WeeklyChallenge>>

    @Query("SELECT COUNT(*) FROM challenges")
    suspend fun getChallengesCount(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChallenges(challenges: List<WeeklyChallenge>)

    @Query("UPDATE challenges SET currentProgress = :progress, completed = :completed WHERE id = :id")
    suspend fun updateChallengeProgress(id: Int, progress: Int, completed: Boolean)
}

// --- Daily Interaction / Login Streak Entity & DAO ---

@Entity(
    tableName = "daily_interactions",
    indices = [Index(value = ["loginDate"], unique = true)]
)
data class DailyInteractionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val loginDate: String, // Format: "yyyy-MM-dd" (e.g. "2026-11-06")
    val timestamp: Long = System.currentTimeMillis(),
    val interactionType: String = "LOGIN", // "LOGIN", "CHECK_IN", "LESSON", "GAME", "ORACLE"
    val xpEarned: Int = 0
)

@Dao
interface DailyStreakDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertInteraction(interaction: DailyInteractionEntity): Long

    @Query("SELECT * FROM daily_interactions ORDER BY loginDate DESC")
    fun getAllInteractionsFlow(): Flow<List<DailyInteractionEntity>>

    @Query("SELECT * FROM daily_interactions ORDER BY loginDate DESC")
    suspend fun getAllInteractions(): List<DailyInteractionEntity>

    @Query("SELECT DISTINCT loginDate FROM daily_interactions ORDER BY loginDate DESC")
    suspend fun getDistinctLoginDates(): List<String>

    @Query("SELECT DISTINCT loginDate FROM daily_interactions ORDER BY loginDate DESC")
    fun getDistinctLoginDatesFlow(): Flow<List<String>>

    @Query("SELECT EXISTS(SELECT 1 FROM daily_interactions WHERE loginDate = :dateStr LIMIT 1)")
    suspend fun hasInteractionOnDate(dateStr: String): Boolean

    @Query("SELECT COUNT(DISTINCT loginDate) FROM daily_interactions")
    suspend fun getTotalActiveDaysCount(): Int

    @Query("DELETE FROM daily_interactions WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("DELETE FROM daily_interactions")
    suspend fun clearAll()
}

// --- Room Sacerdote Audio Cache Entity & DAO ---

/**
 * Room Entity to store metadata and local disk file paths of audios synthesized
 * with Víctor Florencio's voice via XTTS, preventing repeated server calls.
 */
@Entity(tableName = "sacerdote_audio_cache")
data class SacerdoteAudioCacheEntity(
    @PrimaryKey val textHash: String,
    val queryText: String,
    val filePath: String,
    val audioFormat: String = "wav",
    val fileSizeBytes: Long = 0L,
    val durationMs: Long = 0L,
    val createdAt: Long = System.currentTimeMillis(),
    val lastPlayedAt: Long = System.currentTimeMillis(),
    val playCount: Int = 1,
    val speakerName: String = "Víctor Florencio"
)

@Dao
interface SacerdoteAudioCacheDao {
    @Query("SELECT * FROM sacerdote_audio_cache WHERE textHash = :hash LIMIT 1")
    suspend fun getCachedAudioByHash(hash: String): SacerdoteAudioCacheEntity?

    @Query("SELECT * FROM sacerdote_audio_cache WHERE queryText = :queryText LIMIT 1")
    suspend fun getCachedAudioByText(queryText: String): SacerdoteAudioCacheEntity?

    @Query("SELECT * FROM sacerdote_audio_cache ORDER BY lastPlayedAt DESC")
    fun getAllCachedAudiosFlow(): Flow<List<SacerdoteAudioCacheEntity>>

    @Query("SELECT * FROM sacerdote_audio_cache ORDER BY lastPlayedAt DESC")
    suspend fun getAllCachedAudios(): List<SacerdoteAudioCacheEntity>

    @Query("SELECT COUNT(*) FROM sacerdote_audio_cache")
    suspend fun getCacheCount(): Int

    @Query("SELECT COUNT(*) FROM sacerdote_audio_cache")
    fun getCacheCountFlow(): Flow<Int>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCacheEntry(entry: SacerdoteAudioCacheEntity)

    @Query("UPDATE sacerdote_audio_cache SET lastPlayedAt = :now, playCount = playCount + 1 WHERE textHash = :hash")
    suspend fun recordAudioPlayed(hash: String, now: Long = System.currentTimeMillis())

    @Query("DELETE FROM sacerdote_audio_cache WHERE textHash = :hash")
    suspend fun deleteByHash(hash: String)

    @Query("DELETE FROM sacerdote_audio_cache")
    suspend fun clearCache()
}

// --- Room App Database ---

@Database(
    entities = [
        UserProgress::class,
        LessonEntity::class,
        WordEntity::class,
        WeeklyChallenge::class,
        DailyInteractionEntity::class,
        SacerdoteAudioCacheEntity::class
    ],
    version = 13,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userProgressDao(): UserProgressDao
    abstract fun lessonDao(): LessonDao
    abstract fun wordDao(): WordDao
    abstract fun challengeDao(): ChallengeDao
    abstract fun dailyStreakDao(): DailyStreakDao
    abstract fun sacerdoteAudioCacheDao(): SacerdoteAudioCacheDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: android.content.Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = androidx.room.Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "nahuatl_database"
                )
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}

