package by.profs.rowgame.data.items

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import by.profs.rowgame.utils.ID_ROWER
import by.profs.rowgame.utils.NAME_ROWER
import by.profs.rowgame.utils.TABLE_ROWERS
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
@Entity(tableName = TABLE_ROWERS)
data class Rower(
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = ID_ROWER) val id: Int?,
    @ColumnInfo(name = NAME_ROWER) val name: String,
    @ColumnInfo(name = "gender") val gender: Int,
    @ColumnInfo(name = "age") val age: Int,
    @ColumnInfo(name = "height") val height: Int,
    @ColumnInfo(name = "weight") val weight: Int,
    @ColumnInfo(name = "power") var power: Int,
    @ColumnInfo(name = "technics") var technics: Int,
    @ColumnInfo(name = "endurance") var endurance: Int,
    @ColumnInfo(name = "thumb") val thumb: String? = null,
    @ColumnInfo(name = "photo") val photo: String? = null,
    @ColumnInfo(name = "endpointAbout") val endpointAbout: String? = null,
    @ColumnInfo(name = "cost") val cost: Int = 0,
    @ColumnInfo(name = "injury") var injury: Int = 0,
    @ColumnInfo(name = "injurability") val injurability: Double = 1.0
) {
    companion object {
        private const val daysToDegradation = 10

        const val MALE = 1
        const val FEMALE = 2
    }

    fun upEndurance(level: Int = 1) { endurance += level }

    fun upPower(level: Int = 1) { power += level }

    fun upTechnics(level: Int = 1) { technics += level }

    fun hurt(days: Int, today: Int): Boolean {
        val injur: Int = days / daysToDegradation

        if (endurance < injur || power < injur || technics < injur) return false
        if (injury < today) injury = today
        if (Int.MAX_VALUE - injury >= days) { // check on not overflowing of injury property
            upEndurance(-injur)
            upPower(-injur)
            upTechnics(-injur)
            injury += days
        }
        return true
    }
}