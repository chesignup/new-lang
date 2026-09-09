package com.newlang.french.data

import android.content.Context
import java.time.LocalDate
import java.time.temporal.ChronoUnit

class ProgressRepository(context: Context) {
    private val prefs = context.getSharedPreferences("newlang_progress", Context.MODE_PRIVATE)

    fun load(): ProgressSnapshot {
        val today = LocalDate.now().toString()
        val last = prefs.getString("lastActiveDay", "") ?: ""
        val dailyDate = prefs.getString("dailyXpDate", "") ?: ""
        val streak = prefs.getInt("streak", 0)
        val adjustedStreak = when {
            last.isBlank() -> 0
            last == today -> streak
            ChronoUnit.DAYS.between(LocalDate.parse(last), LocalDate.now()) == 1L -> streak
            else -> 0
        }
        return ProgressSnapshot(
            xp = prefs.getInt("xp", 0),
            streak = adjustedStreak,
            lastActiveDay = last,
            completedToday = (prefs.getString("completedToday", "") ?: "")
                .split(",").filter { it.isNotBlank() }.toSet()
                .let { if (last == today) it else emptySet() },
            badges = (prefs.getString("badges", "") ?: "").split(",").filter { it.isNotBlank() }.toSet(),
            conversations = prefs.getInt("conversations", 0),
            quizzes = prefs.getInt("quizzes", 0),
            writings = prefs.getInt("writings", 0),
            cafeSessions = prefs.getInt("cafeSessions", 0),
            routines = prefs.getInt("routines", 0),
            minutesLearned = prefs.getInt("minutesLearned", 0),
            dailyXp = if (dailyDate == today) prefs.getInt("dailyXp", 0) else 0,
            dailyXpDate = if (dailyDate == today) dailyDate else today,
            longestStreak = prefs.getInt("longestStreak", 0).coerceAtLeast(adjustedStreak)
        )
    }

    @Synchronized
    fun award(lesson: Lesson, minutes: Int = 2): ProgressSnapshot {
        val today = LocalDate.now()
        val todayStr = today.toString()
        val current = load()
        val last = current.lastActiveDay
        val newStreak = when {
            last.isBlank() -> 1
            last == todayStr -> current.streak.coerceAtLeast(1)
            ChronoUnit.DAYS.between(LocalDate.parse(last), today) == 1L -> current.streak + 1
            else -> 1
        }
        val already = current.completedToday.contains(lesson.id)
        val gained = if (already) (lesson.xp / 4).coerceAtLeast(5) else lesson.xp
        val completed = current.completedToday + lesson.id
        val counters = when (lesson) {
            Lesson.Conversation -> current.copy(conversations = current.conversations + 1)
            Lesson.Translate -> current.copy(quizzes = current.quizzes + 1)
            Lesson.Cafe -> current.copy(cafeSessions = current.cafeSessions + 1)
            Lesson.Writing -> current.copy(writings = current.writings + 1)
            Lesson.Phrases -> current
            Lesson.Routine -> current.copy(routines = current.routines + 1)
        }
        val badges = current.badges.toMutableSet()
        if (current.xp + gained > 0) badges += "first_words"
        if (counters.conversations >= 3) badges += "talker"
        if (counters.cafeSessions >= 3) badges += "cafe_regular"
        if (counters.writings >= 3) badges += "writer"
        if (lesson == Lesson.Phrases) badges += "slang_kid"
        if (counters.quizzes >= 5) badges += "quiz_brain"
        if (counters.routines >= 1) badges += "routine_hero"
        if (newStreak >= 3) badges += "streak_3"
        if (newStreak >= 7) badges += "streak_7"
        if (newStreak >= 30) badges += "streak_30"

        val next = counters.copy(
            xp = current.xp + gained,
            streak = newStreak,
            lastActiveDay = todayStr,
            completedToday = completed,
            badges = badges,
            minutesLearned = current.minutesLearned + minutes,
            dailyXp = current.dailyXp + gained,
            dailyXpDate = todayStr,
            longestStreak = maxOf(current.longestStreak, newStreak)
        )
        persist(next)
        return next
    }

    private fun persist(p: ProgressSnapshot) {
        prefs.edit()
            .putInt("xp", p.xp)
            .putInt("streak", p.streak)
            .putString("lastActiveDay", p.lastActiveDay)
            .putString("completedToday", p.completedToday.joinToString(","))
            .putString("badges", p.badges.joinToString(","))
            .putInt("conversations", p.conversations)
            .putInt("quizzes", p.quizzes)
            .putInt("writings", p.writings)
            .putInt("cafeSessions", p.cafeSessions)
            .putInt("routines", p.routines)
            .putInt("minutesLearned", p.minutesLearned)
            .putInt("dailyXp", p.dailyXp)
            .putString("dailyXpDate", p.dailyXpDate)
            .putInt("longestStreak", p.longestStreak)
            .apply()
    }
}
