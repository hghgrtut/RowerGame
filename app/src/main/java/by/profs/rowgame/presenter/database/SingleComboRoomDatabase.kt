package by.profs.rowgame.presenter.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import by.profs.rowgame.data.combos.CombinationSingleScull
import by.profs.rowgame.presenter.dao.SingleComboDao
import by.profs.rowgame.utils.TABLE_COMBO_SINGLE
import kotlinx.coroutines.CoroutineScope

@Database(entities = [CombinationSingleScull::class], version = 2)
abstract class SingleComboRoomDatabase : RoomDatabase() {
    abstract fun singleComboDao(): SingleComboDao

    companion object {
        @Volatile
        private var INSTANCE: SingleComboRoomDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): SingleComboRoomDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                        context.applicationContext,
                        SingleComboRoomDatabase::class.java,
                        TABLE_COMBO_SINGLE)
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                return instance
            }
        }
    }
}