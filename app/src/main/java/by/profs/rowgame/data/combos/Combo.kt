package by.profs.rowgame.data.combos

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import by.profs.rowgame.utils.ID_BOAT
import by.profs.rowgame.utils.ID_COMBO
import by.profs.rowgame.utils.ID_OAR
import by.profs.rowgame.utils.ID_ROWER
import by.profs.rowgame.utils.TABLE_COMBO_SINGLE

@Entity(tableName = TABLE_COMBO_SINGLE)
data class Combo(
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = ID_COMBO) val combinationId: Int?,
    @ColumnInfo(name = ID_BOAT) val boatId: Int,
    @ColumnInfo(name = ID_OAR) val oarId: Int,
    @ColumnInfo(name = ID_ROWER) val rowerId: Int
)