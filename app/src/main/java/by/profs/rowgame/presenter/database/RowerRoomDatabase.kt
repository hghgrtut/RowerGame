package by.profs.rowgame.presenter.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import by.profs.rowgame.data.items.Rower
import by.profs.rowgame.presenter.dao.RowerDao
import by.profs.rowgame.utils.TABLE_ROWERS
import kotlinx.coroutines.CoroutineScope

@Database(entities = [Rower::class], version = 1)
abstract class RowerRoomDatabase : RoomDatabase() {
    abstract fun rowerDao(): RowerDao

    companion object {
        @Volatile
        private var INSTANCE: RowerRoomDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): RowerRoomDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext, RowerRoomDatabase::class.java, TABLE_ROWERS)
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}