package by.profs.rowgame.app

import android.app.Application
import by.profs.rowgame.presenter.database.MyRoomDatabase

class App : Application() {
    override fun onCreate() {
        super.onCreate()

        // ServiceLocator.register(MyRoomDatabase.getDatabase(ServiceLocator.get(Context::class)))
        ServiceLocator.register(applicationContext)
        val database = MyRoomDatabase.getDatabase(applicationContext)
        ServiceLocator.register(database.boatDao())
        ServiceLocator.register(database.comboDao())
        ServiceLocator.register(database.competitionDao())
        ServiceLocator.register(database.oarDao())
        ServiceLocator.register(database.rowerDao())
    }
}