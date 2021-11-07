package by.profs.rowgame.data.items

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import by.profs.rowgame.app.ServiceLocator
import by.profs.rowgame.data.consts.COL_ROWER_AGE
import by.profs.rowgame.data.consts.COL_STRATEGY
import by.profs.rowgame.data.consts.ID_ROWER
import by.profs.rowgame.data.consts.NAME_ROWER
import by.profs.rowgame.data.consts.TABLE_ROWERS
import by.profs.rowgame.presenter.database.dao.RowerDao
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Entity(tableName = TABLE_ROWERS)
data class Rower(
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = ID_ROWER) val id: Int?,
    @ColumnInfo(name = NAME_ROWER) val name: String,
    @ColumnInfo(name = "gender") val gender: Int,
    @ColumnInfo(name = COL_ROWER_AGE) val age: Int,
    @ColumnInfo(name = "height") val height: Int,
    @ColumnInfo(name = "weight") val weight: Int,
    @ColumnInfo(name = "power") var power: Int,
    @ColumnInfo(name = "technics") var technics: Int,
    @ColumnInfo(name = "endurance") var endurance: Int,
    @ColumnInfo(name = COL_STRATEGY) var strategy: Int,
    @ColumnInfo(name = "thumb") val thumb: String? = null,
    @ColumnInfo(name = "about") val about: String? = null,
    @ColumnInfo(name = "cost") val cost: Int = 0,
    @ColumnInfo(name = "isMine") var isMine: Boolean = true
) {
    companion object {
        const val MALE = 1
        const val FEMALE = 2
    }

    fun upEndurance(level: Int = 1) { endurance += level }

    fun upPower(level: Int = 1) { power += level }

    fun upTechnics(level: Int = 1) { technics += level }

    fun hurt(injur: Int): Boolean {
        if (endurance < injur || power < injur || technics < injur) {
            ServiceLocator.get(RowerDao::class).deleteItem(id!!)
            return false
        }
        upEndurance(-injur)
        upPower(-injur)
        upTechnics(-injur)
        saveUpdate()
        return true
    }

    fun saveUpdate() = CoroutineScope(Dispatchers.IO).launch {
        ServiceLocator.get(RowerDao::class).updateItem(this@Rower)
    }
}