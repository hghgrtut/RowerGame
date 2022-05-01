package by.profs.rowgame.data.preferences

import android.content.SharedPreferences
import by.profs.rowgame.app.ServiceLocator

object LevelEditor {
    private val preferences: SharedPreferences = ServiceLocator.locate()

    fun get() = preferences.getInt(LEVEL, 0)

    fun trySet(level: Int) { if (level > get()) preferences.edit().putInt(LEVEL, level).apply() }
}