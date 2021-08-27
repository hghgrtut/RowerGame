package by.profs.rowgame.presenter.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import by.profs.rowgame.data.combos.Combo
import by.profs.rowgame.data.competition.Competition
import by.profs.rowgame.data.competition.License
import by.profs.rowgame.data.consts.COL_AGE_CATEGORY
import by.profs.rowgame.data.consts.COL_COMPETITION_LEVEL
import by.profs.rowgame.data.consts.COL_DAY
import by.profs.rowgame.data.consts.COL_TYPE
import by.profs.rowgame.data.consts.ID_ROWER
import by.profs.rowgame.data.consts.NAME_ROWER
import by.profs.rowgame.data.consts.TABLE_COMBO_SINGLE
import by.profs.rowgame.data.consts.TABLE_COMPETITION
import by.profs.rowgame.data.consts.TABLE_LICENSE
import by.profs.rowgame.data.consts.TABLE_ROWERS

@Dao
interface CompetitionDao {
    @Query("INSERT INTO " +
            "$TABLE_COMPETITION($COL_DAY, $COL_COMPETITION_LEVEL, $COL_TYPE, $COL_AGE_CATEGORY)" +
            " VALUES (30, 0, 1, 4), (60, 1, 1, 4), (90, 0, 0, 2), (120, 0, 0, 3), (150, 1, 0, 2)," +
            " (180, 1, 0, 3), (210, 1, 0, 4), (240, 2, 0, 3), (270, 2, 0, 4), (300, 3, 0, 3)," +
            " (330, 3, 0, 4), (360, 0, 2, 1)")
    fun createCompetitions()

    @Query("SELECT * FROM $TABLE_COMPETITION WHERE $COL_DAY = (:day) LIMIT 1")
    fun search(day: Int): Competition

    suspend fun getCompetitionDays(): IntArray {
        var competitionDays = execGetCompetitionDay()
        if (competitionDays.isEmpty()) {
            createCompetitions()
            competitionDays = execGetCompetitionDay()
        }
        return competitionDays
    }

    @Query("SELECT $NAME_ROWER FROM $TABLE_ROWERS WHERE $ID_ROWER IN (" +
            "SELECT DISTINCT $ID_ROWER FROM $TABLE_LICENSE" +
            " WHERE $COL_COMPETITION_LEVEL = (:level) AND $COL_AGE_CATEGORY = (:age))")
    fun getParticipantsNames(level: Int, age: Int): List<String>

    @Query("SELECT * FROM $TABLE_COMBO_SINGLE WHERE $ID_ROWER IN (" +
            "SELECT DISTINCT $ID_ROWER FROM $TABLE_LICENSE" +
            " WHERE $COL_COMPETITION_LEVEL = (:level) AND $COL_AGE_CATEGORY = (:age))")
    fun getParticipants(level: Int, age: Int): List<Combo>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addLicense(license: License)

    @Query("DELETE FROM $TABLE_LICENSE")
    fun deleteLicences()

    @Query("SELECT $COL_DAY FROM $TABLE_COMPETITION")
    fun execGetCompetitionDay(): IntArray
}