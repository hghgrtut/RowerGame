package by.profs.rowgame.presenter.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import by.profs.rowgame.data.items.Rower
import by.profs.rowgame.utils.NAME_ROWER
import by.profs.rowgame.utils.TABLE_ROWERS

@Dao
interface RowerDao : MyDao<Rower> {
    @Query("SELECT * FROM $TABLE_ROWERS")
    override fun getItems(): List<Rower>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    override fun insert(item: Rower)

    @Query("SELECT * FROM $TABLE_ROWERS")
    override fun deleteItem(id: Int) { throw NotImplementedError("Use deleteRower instead") }

    @Query("DELETE FROM $TABLE_ROWERS WHERE $NAME_ROWER = (:name)")
    fun deleteRower(name: String)

    @Query("SELECT * FROM $TABLE_ROWERS WHERE $NAME_ROWER = (:name) LIMIT 1")
    fun search(name: String): List<Rower>

    @Update
    override fun updateItem(item: Rower)
}