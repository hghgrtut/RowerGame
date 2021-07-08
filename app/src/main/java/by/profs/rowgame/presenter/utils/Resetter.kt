package by.profs.rowgame.presenter.utils

import android.content.Context
import by.profs.rowgame.data.preferences.PreferenceEditor
import by.profs.rowgame.presenter.database.MyRoomDatabase
import by.profs.rowgame.utils.START_MONEY_BALANCE
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object Resetter {
    suspend fun giveInitMoney(prefEditor: PreferenceEditor, context: Context): Boolean =
        withContext(Dispatchers.IO) {
            val balance = prefEditor.getBalance()
            val database = MyRoomDatabase.getDatabase(context)

            if (balance >= START_MONEY_BALANCE ||
                database.oarDao().getItems().isNotEmpty() ||
                database.boatDao().getItems().isNotEmpty()
            ) return@withContext false

            prefEditor.setBalance(START_MONEY_BALANCE)
            return@withContext true
        }
}