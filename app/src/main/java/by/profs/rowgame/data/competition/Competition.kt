package by.profs.rowgame.data.competition

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import by.profs.rowgame.utils.ID_COMPETITION
import by.profs.rowgame.utils.TABLE_COMPETITION

@Entity(tableName = TABLE_COMPETITION)
data class Competition(
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = ID_COMPETITION) val id: Int?,
    @ColumnInfo(name = "level") val level: Int
)