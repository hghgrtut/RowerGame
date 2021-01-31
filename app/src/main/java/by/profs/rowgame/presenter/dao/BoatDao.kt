package by.profs.rowgame.presenter.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import by.profs.rowgame.data.items.Boat
import by.profs.rowgame.utils.TABLE_BOAT

@Dao
interface BoatDao : MyDao<Boat> {
    @Query("SELECT * FROM $TABLE_BOAT")
    override fun getItems(): List<Boat>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    override fun insert(item: Boat)

    @Query("SELECT * FROM $TABLE_BOAT WHERE boat_id = (:id) LIMIT 1")
    override fun search(id: Int): Boat?

    @Query("DELETE FROM $TABLE_BOAT WHERE boat_id = (:id)")
    override fun deleteItem(id: Int)

    @Update
    override fun updateItem(item: Boat)
}