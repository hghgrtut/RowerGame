package by.profs.rowgame.presenter.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import by.profs.rowgame.data.combos.CombinationSingleScull
import by.profs.rowgame.utils.ID_BOAT
import by.profs.rowgame.utils.ID_OAR
import by.profs.rowgame.utils.ID_ROWER
import by.profs.rowgame.utils.TABLE_COMBO_SINGLE

@Dao
interface SingleComboDao {
    @Query("SELECT * FROM $TABLE_COMBO_SINGLE")
    fun getAllCombos(): List<CombinationSingleScull>

    @Query("SELECT $ID_BOAT FROM $TABLE_COMBO_SINGLE")
    fun getBoatIds(): List<Int>

    @Query("SELECT $ID_OAR FROM $TABLE_COMBO_SINGLE")
    fun getOarIds(): List<Int>

    @Query("SELECT $ID_ROWER FROM $TABLE_COMBO_SINGLE")
    fun getRowerIds(): List<Int>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertCombo(combo: CombinationSingleScull)

    // Delete combos where rower with given rowerId takes part
    @Query("DELETE FROM $TABLE_COMBO_SINGLE WHERE $ID_ROWER = (:rowerId)")
    fun deleteCombo(rowerId: Int)

    @Query("DELETE FROM $TABLE_COMBO_SINGLE")
    fun dropTable()
}