package by.profs.rowgame.app

import android.app.Application
import by.profs.rowgame.data.preferences.USER_PREF
import by.profs.rowgame.presenter.database.MyRoomDatabase

class App : Application() {
    override fun onCreate() {
        super.onCreate()

        ServiceLocator.register(applicationContext)
        ServiceLocator.register(applicationContext.getSharedPreferences(USER_PREF, MODE_PRIVATE))

        val database = MyRoomDatabase.getDatabase(applicationContext)
        ServiceLocator.register(database.boatDao())
        ServiceLocator.register(database.comboDao())
        ServiceLocator.register(database.competitionDao())
        ServiceLocator.register(database.oarDao())
        ServiceLocator.register(database.rowerDao())
    }
}