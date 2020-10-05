package by.profs.rowgame.presenter.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import by.profs.rowgame.data.items.Oar
import by.profs.rowgame.presenter.dao.OarDao
import by.profs.rowgame.utils.TABLE_OAR
import kotlinx.coroutines.CoroutineScope

@Database(entities = [Oar::class], version = 1)
abstract class OarRoomDatabase : RoomDatabase() {
    abstract fun oarDao(): OarDao

    companion object {
        @Volatile
        private var INSTANCE: OarRoomDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): OarRoomDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                        context.applicationContext, OarRoomDatabase::class.java, TABLE_OAR)
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}