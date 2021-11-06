package by.profs.rowgame.data.items

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import by.profs.rowgame.data.consts.ID_OAR
import by.profs.rowgame.data.consts.TABLE_OAR
import by.profs.rowgame.data.items.util.Manufacturer
import by.profs.rowgame.utils.IDEAL

@Entity(tableName = TABLE_OAR)
data class Oar(
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = ID_OAR) override val id: Int?,
    @ColumnInfo(name = "manufacturer") val manufacturer: String,
    @ColumnInfo(name = "weight") val weight: Int,
    @ColumnInfo(name = "blade") val blade: Int,
    @ColumnInfo(name = "type") val type: Int,
    @ColumnInfo(name = "damage") var damage: Int = 0
) : Item {
    override fun getPower(): Int = (blade + weight) * powerCoeff

    override fun getLevel(): Int = blade * weight - 1

    override fun broke(damag: Int): Boolean {
        damage += damag
        return damage + damag < IDEAL
    }

    companion object {
        const val RECREATIONAL = 1
        const val SPORTIVE = 2
        const val ELITE = 3

        const val SCULL = 1
        const val SWEEP = 2

        const val BASIC_COST = 60

        private const val powerCoeff = 10

        fun getManufacturersList(): List<String> =
            listOf(Manufacturer.Braca.name, Manufacturer.Concept.name, Manufacturer.Croker.name)
    }
}