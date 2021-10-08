package by.profs.rowgame.data.preferences

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import by.profs.rowgame.app.ServiceLocator.get
import by.profs.rowgame.utils.START_FAME
import by.profs.rowgame.utils.START_MONEY_BALANCE

class MoneyFameEditor {
    private val preferences: SharedPreferences =
        get(Context::class).getSharedPreferences(USER_PREF, AppCompatActivity.MODE_PRIVATE)

    fun getMoney(): Int = preferences.getInt(MONEY, START_MONEY_BALANCE)

    fun setMoney(money: Int) {
        preferences.edit().apply {
            putInt(MONEY, money)
            apply()
        }
    }

    fun getFame(): Int = preferences.getInt(FAME, START_FAME)

    fun setFame(fame: Int) {
        preferences.edit().apply {
            putInt(FAME, fame)
            apply()
        }
    }
}