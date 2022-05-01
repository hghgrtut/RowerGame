package by.profs.rowgame.data.preferences

import android.content.SharedPreferences
import by.profs.rowgame.app.ServiceLocator
import by.profs.rowgame.data.combos.Combo

object PairingPreferences {
    private val preferences: SharedPreferences = ServiceLocator.locate()

    fun occupyBoat(id: Int) {
        preferences.edit().apply {
            putInt(BOAT, id)
            apply()
        }
    }

    fun occupyRower(id: Int) {
        preferences.edit().apply {
            putInt(FIRST_ROWER, id)
            apply()
        }
    }

    fun occupyOar(id: Int, number: Int = 1) {
        preferences.edit().apply {
            putInt(FIRST_OAR, id)
            apply()
        }
    }

    fun getCombo() = Combo(
        null,
        preferences.getInt(BOAT, 0),
        preferences.getInt(FIRST_OAR, 0),
        preferences.getInt(FIRST_ROWER, 0)
    )
}