package by.profs.rowgame.presenter.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import by.profs.rowgame.data.consts.TABLE_BOAT
import by.profs.rowgame.data.items.Boat
import kotlinx.coroutines.flow.Flow

@Dao
interface BoatDao : MyDao<Boat> {
    @Query("SELECT * FROM $TABLE_BOAT")
    override fun getItems(): Flow<List<Boat>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    override fun insert(item: Boat)

    @Query("SELECT * FROM $TABLE_BOAT WHERE boat_id = (:id) LIMIT 1")
    override fun search(id: Int): Boat?

    @Query("DELETE FROM $TABLE_BOAT WHERE boat_id = (:id)")
    override fun deleteItem(id: Int)

    @Update
    override fun updateItem(item: Boat)
}