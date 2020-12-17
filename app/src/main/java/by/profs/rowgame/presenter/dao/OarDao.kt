package by.profs.rowgame.presenter.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import by.profs.rowgame.data.items.Oar
import by.profs.rowgame.utils.ID_OAR
import by.profs.rowgame.utils.TABLE_OAR

@Dao
interface OarDao : MyDao<Oar, Int> {
    @Query("SELECT * FROM $TABLE_OAR")
    override fun getItems(): List<Oar>

    @Query("SELECT * FROM $TABLE_OAR WHERE $ID_OAR = (:id) LIMIT 1")
    override fun search(id: Int): Oar

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    override fun insert(item: Oar)

    @Query("DELETE FROM $TABLE_OAR WHERE $ID_OAR = (:id)")
    override fun deleteItem(id: Int)

    @Update
    override fun updateItem(item: Oar)
}