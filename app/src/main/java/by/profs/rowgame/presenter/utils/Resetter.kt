package by.profs.rowgame.presenter.utils

import android.app.Application
import by.profs.rowgame.data.preferences.PreferenceEditor
import by.profs.rowgame.presenter.database.BoatRoomDatabase
import by.profs.rowgame.presenter.database.OarRoomDatabase
import by.profs.rowgame.utils.START_MONEY_BALANCE
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object Resetter {
    suspend fun giveInitMoney(prefEditor: PreferenceEditor, application: Application): Boolean =
        withContext(Dispatchers.IO) {
            val balance = prefEditor.getBalance()
            val scope = CoroutineScope(Dispatchers.IO)
            if (balance >= START_MONEY_BALANCE ||
                OarRoomDatabase.getDatabase(application, scope).oarDao().getItems().isNotEmpty() ||
                BoatRoomDatabase.getDatabase(application, scope).boatDao().getItems().isNotEmpty()
            ) false
            else {
                prefEditor.setBalance(START_MONEY_BALANCE)
                true
            }
    }
}