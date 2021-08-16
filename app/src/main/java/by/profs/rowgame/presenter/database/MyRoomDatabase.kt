package by.profs.rowgame.presenter.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import by.profs.rowgame.data.combos.Combo
import by.profs.rowgame.data.competition.Competition
import by.profs.rowgame.data.items.Boat
import by.profs.rowgame.data.items.Oar
import by.profs.rowgame.data.items.Rower
import by.profs.rowgame.presenter.dao.BoatDao
import by.profs.rowgame.presenter.dao.ComboDao
import by.profs.rowgame.presenter.dao.OarDao
import by.profs.rowgame.presenter.dao.RowerDao
import by.profs.rowgame.utils.TABLE_ROWERS

@Database(
    entities = [Boat::class, Combo::class, Competition::class, Oar::class, Rower::class],
    version = 5
)
abstract class MyRoomDatabase : RoomDatabase() {
    abstract fun boatDao(): BoatDao
    abstract fun comboDao(): ComboDao
    abstract fun rowerDao(): RowerDao
    abstract fun oarDao(): OarDao

    companion object {
        @Volatile
        private var INSTANCE: MyRoomDatabase? = null

        fun getDatabase(context: Context): MyRoomDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext, MyRoomDatabase::class.java, TABLE_ROWERS)
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}