package com.example.data

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

/**
 * Data model for daily interaction and streak metrics.
 */
data class DailyStreakStats(
    val currentStreak: Int = 0,
    val longestStreak: Int = 0,
    val totalActiveDays: Int = 0,
    val lastLoginDate: String = "",
    val hasLoggedInToday: Boolean = false,
    val consecutiveDates: List<String> = emptyList(),
    val recent7DaysStatus: List<DayStreakStatus> = emptyList()
)

/**
 * Visual indicator for a day in the streak calendar strip.
 */
data class DayStreakStatus(
    val dateString: String,      // "2026-11-06"
    val dayOfWeekLabel: String,  // "LUN", "MAR", "MIÉ", "JUE", "VIE", "SÁB", "DOM"
    val dayOfMonth: Int,         // 6
    val isToday: Boolean,
    val isCompleted: Boolean     // Whether user logged in / interacted on this date
)

/**
 * Pure, reliable streak calculation engine using calendar-safe date comparisons.
 */
object DailyStreakCalculator {

    private const val DATE_PATTERN = "yyyy-MM-dd"

    fun getDateFormat(): SimpleDateFormat {
        return SimpleDateFormat(DATE_PATTERN, Locale.US)
    }

    /**
     * Returns today's date formatted as YYYY-MM-DD.
     */
    fun getCurrentDateString(now: Date = Date()): String {
        return getDateFormat().format(now)
    }

    /**
     * Returns yesterday's date formatted as YYYY-MM-DD.
     */
    fun getYesterdayDateString(now: Date = Date()): String {
        val cal = Calendar.getInstance().apply {
            time = now
            add(Calendar.DAY_OF_YEAR, -1)
        }
        return getDateFormat().format(cal.time)
    }

    /**
     * Calculates the number of days between two YYYY-MM-DD date strings.
     * Returns positive integer if date2 is after date1.
     */
    fun daysBetween(dateStr1: String, dateStr2: String): Int {
        return try {
            val sdf = getDateFormat()
            val d1 = sdf.parse(dateStr1) ?: return 0
            val d2 = sdf.parse(dateStr2) ?: return 0
            val diffMs = d2.time - d1.time
            (diffMs / (1000 * 60 * 60 * 24)).toInt()
        } catch (_: Exception) {
            0
        }
    }

    /**
     * Parses a YYYY-MM-DD date string into midnight Calendar.
     */
    private fun parseToMidnightCalendar(dateStr: String): Calendar? {
        return try {
            val sdf = getDateFormat()
            val date = sdf.parse(dateStr) ?: return null
            Calendar.getInstance().apply {
                time = date
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }
        } catch (_: Exception) {
            null
        }
    }

    /**
     * Evaluates consecutive days from a set/list of distinct login dates stored in Room DB.
     *
     * @param distinctDates List of YYYY-MM-DD strings recorded in the database.
     * @param referenceDate Optional reference date (defaults to today).
     */
    fun calculateStreakStats(
        distinctDates: List<String>,
        referenceDate: Date = Date()
    ): DailyStreakStats {
        if (distinctDates.isEmpty()) {
            val empty7Days = buildRecent7DaysStatus(emptySet(), referenceDate)
            return DailyStreakStats(
                currentStreak = 0,
                longestStreak = 0,
                totalActiveDays = 0,
                lastLoginDate = "",
                hasLoggedInToday = false,
                consecutiveDates = emptyList(),
                recent7DaysStatus = empty7Days
            )
        }

        val todayStr = getCurrentDateString(referenceDate)
        val yesterdayStr = getYesterdayDateString(referenceDate)
        val dateSet = distinctDates.toSet()
        val hasLoggedInToday = dateSet.contains(todayStr)

        // Clean & sort unique calendars descending
        val validDateStrings = distinctDates
            .filter { it.isNotBlank() }
            .distinct()
            .sortedDescending()

        val parsedCalendars = validDateStrings.mapNotNull { dateStr ->
            parseToMidnightCalendar(dateStr)?.let { cal -> Pair(dateStr, cal) }
        }

        if (parsedCalendars.isEmpty()) {
            return DailyStreakStats(recent7DaysStatus = buildRecent7DaysStatus(dateSet, referenceDate))
        }

        val lastLoginDate = parsedCalendars.first().first

        // --- Current Consecutive Streak Calculation ---
        var currentStreak = 0
        val consecutiveDatesList = mutableListOf<String>()

        val referenceCal = Calendar.getInstance().apply {
            time = referenceDate
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }

        val yesterdayCal = Calendar.getInstance().apply {
            time = referenceDate
            add(Calendar.DAY_OF_YEAR, -1)
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }

        // Determine starting date for consecutive check
        val mostRecentCal = parsedCalendars.first().second
        val diffFromTodayDays = ((referenceCal.timeInMillis - mostRecentCal.timeInMillis) / (1000 * 60 * 60 * 24)).toInt()

        if (diffFromTodayDays == 0 || diffFromTodayDays == 1) {
            // Either logged in today (diff 0) or logged in yesterday (diff 1, streak not lost yet)
            var expectedCal = Calendar.getInstance().apply {
                timeInMillis = mostRecentCal.timeInMillis
            }

            for (pair in parsedCalendars) {
                val cal = pair.second
                val dayDiff = ((expectedCal.timeInMillis - cal.timeInMillis) / (1000 * 60 * 60 * 24)).toInt()

                if (dayDiff == 0) {
                    currentStreak++
                    consecutiveDatesList.add(pair.first)
                    // Set next expected day: 1 day earlier
                    expectedCal.add(Calendar.DAY_OF_YEAR, -1)
                } else if (dayDiff > 0) {
                    // Gap detected! Streak ends here.
                    break
                }
            }
        } else {
            // Most recent login was 2 or more days ago -> streak is broken
            currentStreak = 0
        }

        // --- Longest Consecutive Streak Calculation (All-Time) ---
        var longestStreak = 0
        val ascendingCalendars = parsedCalendars.reversed()
        var tempStreak = 0
        var prevCal: Calendar? = null

        for (pair in ascendingCalendars) {
            val cal = pair.second
            if (prevCal == null) {
                tempStreak = 1
            } else {
                val diffDays = ((cal.timeInMillis - prevCal.timeInMillis) / (1000 * 60 * 60 * 24)).toInt()
                if (diffDays == 1) {
                    tempStreak++
                } else if (diffDays > 1) {
                    tempStreak = 1
                }
            }
            if (tempStreak > longestStreak) {
                longestStreak = tempStreak
            }
            prevCal = Calendar.getInstance().apply { timeInMillis = cal.timeInMillis }
        }

        longestStreak = maxOf(longestStreak, currentStreak)

        val recent7Days = buildRecent7DaysStatus(dateSet, referenceDate)

        return DailyStreakStats(
            currentStreak = currentStreak,
            longestStreak = longestStreak,
            totalActiveDays = dateSet.size,
            lastLoginDate = lastLoginDate,
            hasLoggedInToday = hasLoggedInToday,
            consecutiveDates = consecutiveDatesList,
            recent7DaysStatus = recent7Days
        )
    }

    /**
     * Builds the last 7 calendar days ending on referenceDate (e.g., today).
     */
    fun buildRecent7DaysStatus(
        recordedDates: Set<String>,
        referenceDate: Date = Date()
    ): List<DayStreakStatus> {
        val sdf = getDateFormat()
        val result = mutableListOf<DayStreakStatus>()

        // 6 days ago up to today (7 days total)
        for (offset in 6 downTo 0) {
            val cal = Calendar.getInstance().apply {
                time = referenceDate
                add(Calendar.DAY_OF_YEAR, -offset)
            }
            val dateStr = sdf.format(cal.time)
            val dayOfWeek = cal.get(Calendar.DAY_OF_WEEK)
            val dayLabel = when (dayOfWeek) {
                Calendar.MONDAY -> "LUN"
                Calendar.TUESDAY -> "MAR"
                Calendar.WEDNESDAY -> "MIÉ"
                Calendar.THURSDAY -> "JUE"
                Calendar.FRIDAY -> "VIE"
                Calendar.SATURDAY -> "SÁB"
                Calendar.SUNDAY -> "DOM"
                else -> "D"
            }
            val dayOfMonth = cal.get(Calendar.DAY_OF_MONTH)
            val isToday = (offset == 0)
            val isCompleted = recordedDates.contains(dateStr)

            result.add(
                DayStreakStatus(
                    dateString = dateStr,
                    dayOfWeekLabel = dayLabel,
                    dayOfMonth = dayOfMonth,
                    isToday = isToday,
                    isCompleted = isCompleted
                )
            )
        }
        return result
    }
}
