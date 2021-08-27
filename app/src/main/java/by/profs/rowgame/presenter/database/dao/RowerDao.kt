package by.profs.rowgame.presenter.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import by.profs.rowgame.data.consts.ID_ROWER
import by.profs.rowgame.data.consts.NAME_ROWER
import by.profs.rowgame.data.consts.TABLE_ROWERS
import by.profs.rowgame.data.items.Rower

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

    @Query("SELECT * FROM $TABLE_ROWERS WHERE $NAME_ROWER LIKE :name LIMIT 1")
    fun searchByName(name: String): Rower?
}