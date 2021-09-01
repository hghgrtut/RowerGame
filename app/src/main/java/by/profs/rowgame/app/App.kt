package by.profs.rowgame.app

import android.app.Application
import android.content.Context
import by.profs.rowgame.presenter.database.MyRoomDatabase

class App : Application() {
    override fun onCreate() {
        super.onCreate()

        // ServiceLocator.register(MyRoomDatabase.getDatabase(ServiceLocator.get(Context::class)))
        ServiceLocator.register(Context::class, applicationContext)
        ServiceLocator.register(MyRoomDatabase.getDatabase(applicationContext))
        ServiceLocator.register(ServiceLocator.get(MyRoomDatabase::class).boatDao())
        ServiceLocator.register(ServiceLocator.get(MyRoomDatabase::class).comboDao())
        ServiceLocator.register(ServiceLocator.get(MyRoomDatabase::class).competitionDao())
        ServiceLocator.register(ServiceLocator.get(MyRoomDatabase::class).oarDao())
        ServiceLocator.register(ServiceLocator.get(MyRoomDatabase::class).rowerDao())
    }
}