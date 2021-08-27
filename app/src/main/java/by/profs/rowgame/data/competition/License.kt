package by.profs.rowgame.data.competition

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import by.profs.rowgame.data.consts.COL_AGE_CATEGORY
import by.profs.rowgame.data.consts.COL_COMPETITION_LEVEL
import by.profs.rowgame.data.consts.ID_LICENSE
import by.profs.rowgame.data.consts.ID_ROWER
import by.profs.rowgame.data.consts.TABLE_LICENSE

@Entity(tableName = TABLE_LICENSE)
data class License(
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = ID_LICENSE) val id: Int?,
    @ColumnInfo(name = ID_ROWER) val rower: Int,
    @ColumnInfo(name = COL_COMPETITION_LEVEL) val level: Int,
    @ColumnInfo(name = COL_AGE_CATEGORY) val ageCategory: Int
)