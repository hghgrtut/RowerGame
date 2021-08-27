package by.profs.rowgame.data.preferences

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import by.profs.rowgame.utils.START_FAME
import by.profs.rowgame.utils.START_MONEY_BALANCE
import by.profs.rowgame.utils.UNDEFINED

class PreferenceEditor(context: Context) {
    private val preferences: SharedPreferences =
        context.getSharedPreferences(USER_PREF, AppCompatActivity.MODE_PRIVATE)

    fun getMoney(): Int {
        var balance = preferences.getInt(MONEY, UNDEFINED)
        if (balance == UNDEFINED) {
            balance = START_MONEY_BALANCE
            setMoney(balance)
        }
        return balance
    }

    fun setMoney(money: Int) {
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
}