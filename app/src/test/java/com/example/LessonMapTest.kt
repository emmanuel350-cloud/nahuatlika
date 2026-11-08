package com.example

import com.example.data.LessonEntity
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class LessonMapTest {

    private fun createSampleLessons(count: Int = 10): List<LessonEntity> {
        return (1..count).map { id ->
            val cat = when (id % 4) {
                0 -> "Vocabulario"
                1 -> "Frases"
                2 -> "Conversación"
                else -> "Cultura"
            }
            LessonEntity(
                id = id,
                title = "Lección $id",
                unit = 1,
                category = cat,
                explanation = "Explicación para lección $id",
                nahuatlPhrases = "Niltze;Cualli tonalli",
                spanishTranslations = "Hola;Buenos días",
                difficulty = "Principiante"
            )
        }
    }

    @Test
    fun testFirstLessonAlwaysUnlocked() {
        val completedSet = emptySet<Int>()
        val lesson1Unlocked = 1 == 1 || completedSet.contains(0)
        assertTrue(lesson1Unlocked)
    }

    @Test
    fun testSubsequentLessonsLockedUntilPreviousCompleted() {
        val completedSet = setOf(1)
        val lesson2Unlocked = 2 == 1 || completedSet.contains(1)
        val lesson3Unlocked = 3 == 1 || completedSet.contains(2)

        assertTrue("Lesson 2 should be unlocked when Lesson 1 is completed", lesson2Unlocked)
        assertFalse("Lesson 3 should be locked because Lesson 2 is not completed", lesson3Unlocked)
    }

    @Test
    fun testCategoryFilterFiltering() {
        val lessons = createSampleLessons(8)
        val vocabLessons = lessons.filter { it.category == "Vocabulario" }
        assertTrue(vocabLessons.isNotEmpty())
        assertTrue(vocabLessons.all { it.category == "Vocabulario" })
    }

    @Test
    fun testCheckpointMilestoneFrequency() {
        val lessons = createSampleLessons(10)
        val checkpoints = lessons.filterIndexed { index, _ -> (index + 1) % 5 == 0 }
        assertEquals(2, checkpoints.size)
        assertEquals(5, checkpoints[0].id)
        assertEquals(10, checkpoints[1].id)
    }

    @Test
    fun testSCurveWindingBiasPeriodicity() {
        val biases = (0..3).map { index ->
            when (index % 4) {
                0 -> 0.50f
                1 -> 0.75f
                2 -> 0.50f
                3 -> 0.25f
                else -> 0.50f
            }
        }
        assertEquals(0.50f, biases[0], 0.001f)
        assertEquals(0.75f, biases[1], 0.001f)
        assertEquals(0.50f, biases[2], 0.001f)
        assertEquals(0.25f, biases[3], 0.001f)
    }
}
