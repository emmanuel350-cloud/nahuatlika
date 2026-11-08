package com.example

import com.example.data.DailyStreakCalculator
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class DailyStreakTest {

    private val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.US)

    private fun dateDaysAgo(daysAgo: Int, baseDate: Date = Date()): String {
        val cal = Calendar.getInstance().apply {
            time = baseDate
            add(Calendar.DAY_OF_YEAR, -daysAgo)
        }
        return sdf.format(cal.time)
    }

    @Test
    fun testEmptyDatesReturnsZeroStreak() {
        val stats = DailyStreakCalculator.calculateStreakStats(emptyList())
        assertEquals(0, stats.currentStreak)
        assertEquals(0, stats.longestStreak)
        assertEquals(0, stats.totalActiveDays)
        assertFalse(stats.hasLoggedInToday)
    }

    @Test
    fun testSingleDayLoginToday() {
        val today = dateDaysAgo(0)
        val stats = DailyStreakCalculator.calculateStreakStats(listOf(today))
        assertEquals(1, stats.currentStreak)
        assertEquals(1, stats.longestStreak)
        assertEquals(1, stats.totalActiveDays)
        assertTrue(stats.hasLoggedInToday)
        assertEquals(listOf(today), stats.consecutiveDates)
    }

    @Test
    fun testConsecutiveThreeDaysLeadingToToday() {
        val today = dateDaysAgo(0)
        val yesterday = dateDaysAgo(1)
        val dayBefore = dateDaysAgo(2)

        val stats = DailyStreakCalculator.calculateStreakStats(listOf(today, yesterday, dayBefore))
        assertEquals(3, stats.currentStreak)
        assertEquals(3, stats.longestStreak)
        assertEquals(3, stats.totalActiveDays)
        assertTrue(stats.hasLoggedInToday)
        assertEquals(listOf(today, yesterday, dayBefore), stats.consecutiveDates)
    }

    @Test
    fun testStreakPreservedFromYesterdayBeforeLoggingInToday() {
        val yesterday = dateDaysAgo(1)
        val twoDaysAgo = dateDaysAgo(2)
        val threeDaysAgo = dateDaysAgo(3)

        // User hasn't logged in today yet, but has 3 days up to yesterday
        val stats = DailyStreakCalculator.calculateStreakStats(listOf(yesterday, twoDaysAgo, threeDaysAgo))
        assertEquals(3, stats.currentStreak)
        assertEquals(3, stats.longestStreak)
        assertEquals(3, stats.totalActiveDays)
        assertFalse(stats.hasLoggedInToday)
        assertEquals(listOf(yesterday, twoDaysAgo, threeDaysAgo), stats.consecutiveDates)
    }

    @Test
    fun testStreakBrokenWhenSkippingADay() {
        val today = dateDaysAgo(0)
        // Missed yesterday (-1), but logged in on -2 and -3
        val twoDaysAgo = dateDaysAgo(2)
        val threeDaysAgo = dateDaysAgo(3)

        val stats = DailyStreakCalculator.calculateStreakStats(listOf(today, twoDaysAgo, threeDaysAgo))
        // Since yesterday was skipped, current consecutive streak from today is 1
        assertEquals(1, stats.currentStreak)
        // All-time longest streak is 2 (from twoDaysAgo and threeDaysAgo)
        assertEquals(2, stats.longestStreak)
        assertEquals(3, stats.totalActiveDays)
        assertTrue(stats.hasLoggedInToday)
        assertEquals(listOf(today), stats.consecutiveDates)
    }

    @Test
    fun testStreakZeroWhenInactiveForMultipleDays() {
        val fourDaysAgo = dateDaysAgo(4)
        val fiveDaysAgo = dateDaysAgo(5)

        val stats = DailyStreakCalculator.calculateStreakStats(listOf(fourDaysAgo, fiveDaysAgo))
        assertEquals(0, stats.currentStreak)
        assertEquals(2, stats.longestStreak)
        assertFalse(stats.hasLoggedInToday)
    }

    @Test
    fun testLongestStreakCalculationAcrossGaps() {
        val d0 = dateDaysAgo(0)
        val d1 = dateDaysAgo(1) // block 1: 2 days (d0, d1)

        val d5 = dateDaysAgo(5)
        val d6 = dateDaysAgo(6)
        val d7 = dateDaysAgo(7)
        val d8 = dateDaysAgo(8) // block 2: 4 days (d5, d6, d7, d8)

        val d15 = dateDaysAgo(15) // block 3: 1 day

        val stats = DailyStreakCalculator.calculateStreakStats(listOf(d0, d1, d5, d6, d7, d8, d15))
        assertEquals(2, stats.currentStreak)
        assertEquals(4, stats.longestStreak)
        assertEquals(7, stats.totalActiveDays)
    }

    @Test
    fun testRecent7DaysStripBuildsAccurately() {
        val today = dateDaysAgo(0)
        val yesterday = dateDaysAgo(1)

        val statusList = DailyStreakCalculator.buildRecent7DaysStatus(setOf(today, yesterday))
        assertEquals(7, statusList.size)

        val todayStatus = statusList.last()
        assertTrue(todayStatus.isToday)
        assertTrue(todayStatus.isCompleted)
        assertEquals(today, todayStatus.dateString)

        val yesterdayStatus = statusList[5]
        assertFalse(yesterdayStatus.isToday)
        assertTrue(yesterdayStatus.isCompleted)
        assertEquals(yesterday, yesterdayStatus.dateString)

        val twoDaysAgoStatus = statusList[4]
        assertFalse(twoDaysAgoStatus.isCompleted)
    }
}
