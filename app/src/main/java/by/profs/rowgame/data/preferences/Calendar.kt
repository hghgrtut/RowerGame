package by.profs.rowgame.data.preferences

import android.content.SharedPreferences
import android.text.format.DateUtils.DAY_IN_MILLIS
import android.text.format.DateUtils.HOUR_IN_MILLIS
import by.profs.rowgame.app.ServiceLocator

object Calendar {
    private const val lastDay = 365
    private const val defaultTrainingTime = 6 * HOUR_IN_MILLIS // 9:00, Minsk (+3)
    private val preferences: SharedPreferences = ServiceLocator.locate()

    private fun getGlobalDay(): Int = preferences.getInt(DAY, lastDay)

    fun getTrainingTime(): Long = preferences.getLong(TRAINING_TIME, defaultTrainingTime)

    fun getToday(): Int = (System.currentTimeMillis() / DAY_IN_MILLIS).toInt()

    fun getLastDailyDay(): Int = preferences.getInt(LAST_DAILY, 1)

    fun setLastDailyDay(today: Int) {
        preferences.edit().apply() {
            putInt(LAST_DAILY, today)
            apply()
        }
    }

    fun getDayOfYear(): Int = getGlobalDay() % lastDay + 1

    fun nextDay() {
        preferences.edit().apply {
            putInt(DAY, if (getGlobalDay() != Int.MAX_VALUE) getGlobalDay() + 1 else 1)
            apply()
        }
    }
}