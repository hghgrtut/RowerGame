package by.profs.rowgame.presenter.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import by.profs.rowgame.data.items.Rower
import by.profs.rowgame.utils.ID_ROWER
import by.profs.rowgame.utils.TABLE_ROWERS

@Dao
interface RowerDao : MyDao<Rower> {
    @Query("SELECT * FROM $TABLE_ROWERS")
    override fun getItems(): List<Rower>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    override fun insert(item: Rower)

    @Query("DELETE FROM $TABLE_ROWERS WHERE $ID_ROWER = (:id)")
    override fun deleteItem(id: Int)

    @Query("SELECT * FROM $TABLE_ROWERS WHERE $ID_ROWER = (:id) LIMIT 1")
    override fun search(id: Int): Rower?

    @Update
    override fun updateItem(item: Rower)
}