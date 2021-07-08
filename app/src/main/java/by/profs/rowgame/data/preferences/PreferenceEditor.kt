package by.profs.rowgame.data.preferences

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import by.profs.rowgame.data.combos.Combo
import by.profs.rowgame.utils.BOAT
import by.profs.rowgame.utils.FAME
import by.profs.rowgame.utils.FIRST_OAR
import by.profs.rowgame.utils.FIRST_ROWER
import by.profs.rowgame.utils.MONEY
import by.profs.rowgame.utils.START_FAME
import by.profs.rowgame.utils.START_MONEY_BALANCE
import by.profs.rowgame.utils.UNDEFINED
import by.profs.rowgame.utils.USER_PREF

class PreferenceEditor(context: Context) {
    private val preferences: SharedPreferences =
        context.getSharedPreferences(USER_PREF, AppCompatActivity.MODE_PRIVATE)

    fun getBalance(): Int {
        var balance = preferences.getInt(MONEY, UNDEFINED)
        if (balance == UNDEFINED) {
            balance = START_MONEY_BALANCE
            setBalance(balance)
        }
        return balance
    }

    fun setBalance(money: Int) {
        preferences.edit().apply {
            putInt(MONEY, money)
            apply()
        }
    }

    fun getFame(): Int {
        var fame = preferences.getInt(FAME, UNDEFINED)
        if (fame == UNDEFINED) {
            fame = START_FAME
            setFame(fame)
        }
        return fame
    }

    fun setFame(fame: Int) {
        preferences.edit().apply {
            putInt(FAME, fame)
            apply()
        }
    }

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
            putInt(when (number) { else -> FIRST_OAR }, id)
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