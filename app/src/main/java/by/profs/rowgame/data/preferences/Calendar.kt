package by.profs.rowgame.data.preferences

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import by.profs.rowgame.utils.DAY
import by.profs.rowgame.utils.USER_PREF

class Calendar(context: Context) {
    private val preferences: SharedPreferences =
        context.getSharedPreferences(USER_PREF, AppCompatActivity.MODE_PRIVATE)

    fun getGlobalDay(): Int = preferences.getInt(DAY, lastDay)

    fun getDayOfYear(): Int = getGlobalDay() % lastDay + 1

    fun nextDay() {
        preferences.edit().apply {
            putInt(DAY, if (getGlobalDay() != Int.MAX_VALUE) getGlobalDay() + 1 else 1)
            apply()
        }
    }

    companion object { private const val lastDay = 365 }
}