package by.profs.rowgame.presenter.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import by.profs.rowgame.data.items.Boat
import by.profs.rowgame.presenter.dao.BoatDao
import by.profs.rowgame.utils.TABLE_BOAT
import kotlinx.coroutines.CoroutineScope

@Database(entities = [Boat::class], version = 1)
abstract class BoatRoomDatabase : RoomDatabase() {
    abstract fun boatDao(): BoatDao

    companion object {
        @Volatile
        private var INSTANCE: BoatRoomDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): BoatRoomDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext, BoatRoomDatabase::class.java, TABLE_BOAT)
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}