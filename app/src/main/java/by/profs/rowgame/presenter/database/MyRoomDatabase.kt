package by.profs.rowgame.presenter.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import by.profs.rowgame.data.combos.Combo
import by.profs.rowgame.data.competition.CompetitionInfo
import by.profs.rowgame.data.competition.License
import by.profs.rowgame.data.consts.TABLE_ROWERS
import by.profs.rowgame.data.items.Boat
import by.profs.rowgame.data.items.Oar
import by.profs.rowgame.data.items.Rower
import by.profs.rowgame.presenter.database.dao.BoatDao
import by.profs.rowgame.presenter.database.dao.ComboDao
import by.profs.rowgame.presenter.database.dao.CompetitionDao
import by.profs.rowgame.presenter.database.dao.OarDao
import by.profs.rowgame.presenter.database.dao.RowerDao

@Database(
    entities = [Boat::class,
        Combo::class,
        CompetitionInfo::class,
        License::class,
        Oar::class,
        Rower::class],
    version = 7
)
abstract class MyRoomDatabase : RoomDatabase() {
    abstract fun boatDao(): BoatDao
    abstract fun comboDao(): ComboDao
    abstract fun competitionDao(): CompetitionDao
    abstract fun rowerDao(): RowerDao
    abstract fun oarDao(): OarDao

    companion object {
        @Volatile
        private var INSTANCE: MyRoomDatabase? = null

        fun getDatabase(context: Context): MyRoomDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext, MyRoomDatabase::class.java, TABLE_ROWERS
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}