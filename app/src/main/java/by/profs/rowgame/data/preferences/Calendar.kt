package by.profs.rowgame.data.preferences

import android.content.SharedPreferences
import by.profs.rowgame.utils.DAY

class Calendar(private val preferences: SharedPreferences) {

    fun getGlobalDay(): Int = preferences.getInt(DAY, PreferenceEditor.lastDay)

    fun getDayOfYear(): Int = getGlobalDay() % PreferenceEditor.lastDay + 1

    fun nextDay() {
        preferences.edit().apply {
            putInt(DAY, if (getGlobalDay() != Int.MAX_VALUE) getGlobalDay() + 1 else 0)
            apply()
        }
    }
}