package by.profs.rowgame.data.competition

import android.content.Context
import android.content.res.Resources
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import by.profs.rowgame.R
import by.profs.rowgame.app.ServiceLocator
import by.profs.rowgame.data.competition.CompetitionLevel.Companion.isRegional
import by.profs.rowgame.data.consts.COL_AGE_CATEGORY
import by.profs.rowgame.data.consts.COL_COMPETITION_LEVEL
import by.profs.rowgame.data.consts.COL_DAY
import by.profs.rowgame.data.consts.COL_TYPE
import by.profs.rowgame.data.consts.ID_COMPETITION
import by.profs.rowgame.data.consts.TABLE_COMPETITION

@Entity(tableName = TABLE_COMPETITION)
data class CompetitionInfo(
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = ID_COMPETITION) val id: Int?,
    @ColumnInfo(name = COL_COMPETITION_LEVEL) val level: Int,
    @ColumnInfo(name = COL_DAY) val day: Int,
    @ColumnInfo(name = COL_TYPE) val type: Int,
    @ColumnInfo(name = COL_AGE_CATEGORY) val age: Int
) {
    override fun toString(): String {
        val competitionInfo = getMainInfo()
        if (!level.isRegional()) competitionInfo.appendLocation(level)
        return String(competitionInfo)
    }

    fun getMainInfo(): StringBuilder {
        val resources: Resources = ServiceLocator.get(Context::class).resources
        val competitionInfo =
            StringBuilder(resources.getStringArray(R.array.competition_level)[level])
        competitionInfo.append(" ${resources.getStringArray(R.array.competition_types)[type]}")
        if (age != Ages.Adult.ordinal) competitionInfo.append(" U${Ages.values()[age].age}")
        return competitionInfo
    }
}

private fun StringBuilder.appendLocation(level: Int) {
    val locations = ServiceLocator.get(Context::class).resources.getStringArray(when (level) {
        CompetitionLevel.Continental.ordinal -> R.array.locations_continental
        CompetitionLevel.World.ordinal -> R.array.locations_world
        else -> R.array.locations_republic
    })
    this.append("\n${locations.random()}")
}