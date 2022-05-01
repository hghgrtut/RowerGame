package by.profs.rowgame.data.preferences

import android.content.SharedPreferences
import by.profs.rowgame.app.ServiceLocator
import by.profs.rowgame.utils.START_FAME
import by.profs.rowgame.utils.START_MONEY_BALANCE

object MoneyFameEditor {
    private val preferences: SharedPreferences = ServiceLocator.locate()

    fun getMoney(): Int = preferences.getInt(MONEY, START_MONEY_BALANCE)

    fun setMoney(money: Int) = preferences.edit().putInt(MONEY, money).apply()

    fun getFame(): Int = preferences.getInt(FAME, START_FAME)

    fun setFame(fame: Int) = preferences.edit().putInt(FAME, fame).apply()
}