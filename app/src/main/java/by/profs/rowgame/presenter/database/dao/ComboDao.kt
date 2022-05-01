package by.profs.rowgame.presenter.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import by.profs.rowgame.data.combos.Combo
import by.profs.rowgame.data.consts.COL_ROWER_AGE
import by.profs.rowgame.data.consts.ID_BOAT
import by.profs.rowgame.data.consts.ID_OAR
import by.profs.rowgame.data.consts.ID_ROWER
import by.profs.rowgame.data.consts.TABLE_COMBO_SINGLE
import by.profs.rowgame.data.consts.TABLE_ROWERS

@Dao
interface ComboDao {
    @Query("SELECT * FROM $TABLE_COMBO_SINGLE")
    fun getAllCombos(): List<Combo>

    @Query("SELECT * FROM $TABLE_COMBO_SINGLE WHERE $ID_ROWER IN (" +
            "SELECT $ID_ROWER FROM $TABLE_ROWERS WHERE $COL_ROWER_AGE <= (:maxAge))")
    fun getCombosToAge(maxAge: Int): List<Combo>

    @Query("SELECT $ID_BOAT FROM $TABLE_COMBO_SINGLE")
    fun getBoatIds(): List<Int>

    @Query("SELECT $ID_OAR FROM $TABLE_COMBO_SINGLE")
    fun getOarIds(): List<Int>

    @Query("SELECT $ID_ROWER FROM $TABLE_COMBO_SINGLE")
    fun getRowerIds(): List<Int>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertCombo(combo: Combo)

    @Query("DELETE FROM $TABLE_COMBO_SINGLE WHERE $ID_BOAT = (:boatId)")
    fun deleteComboWithBoat(boatId: Int)

    @Query("DELETE FROM $TABLE_COMBO_SINGLE WHERE $ID_OAR = (:oarId)")
    fun deleteComboWithOar(oarId: Int)

    /** Delete combos where rower with given rowerId takes part */
    @Query("DELETE FROM $TABLE_COMBO_SINGLE WHERE $ID_ROWER = (:rowerId)")
    fun deleteComboWithRower(rowerId: Int)

    @Query("DELETE FROM $TABLE_COMBO_SINGLE")
    fun dropTable()
}