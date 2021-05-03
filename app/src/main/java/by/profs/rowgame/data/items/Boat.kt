package by.profs.rowgame.data.items

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import by.profs.rowgame.data.items.util.Manufacturer
import by.profs.rowgame.utils.IDEAL
import by.profs.rowgame.utils.ID_BOAT
import by.profs.rowgame.utils.TABLE_BOAT

@Entity(tableName = TABLE_BOAT)
data class Boat(
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = ID_BOAT) override val id: Int?,
    @ColumnInfo(name = "type") val type: String,
    @ColumnInfo(name = "manufacturer") val manufacturer: String,
    @ColumnInfo(name = "body") val body: Int,
    @ColumnInfo(name = "weight") val weight: Int,
    @ColumnInfo(name = "wing") val wing: Int,
    @ColumnInfo(name = "damage") var damage: Int = 0
) : Damageable {
    companion object {
        // Bodies
        const val EXTRA_SMALL = 1
        const val SMALL = 2
        const val MEDIUM_SMALL = 3
        const val MEDIUM_LONG = 4
        const val LONG = 5
        const val EXTRA_LONG = 6
        const val UNIVERSAL = 7
        // Weights
        const val RECREATIONAL = 1
        const val SPORTIVE = 2
        const val ELITE = 3
        // Wings
        const val CLASSIC_STAY = 1
        const val ALUMINIUM_WING = 2
        const val CARBON_WING = 3
        const val BACKWING = 4

        const val BASIC_COST = 3000
        const val WING_COST = 500
        const val NEMIGA_COST = 8000

        fun getManufacturersList(): List<String> = listOf(Manufacturer.Empacher.name,
            Manufacturer.Filippi.name,
            Manufacturer.Hudson.name,
            Manufacturer.Nemiga.name,
            Manufacturer.Peisheng.name,
            Manufacturer.Swift.name
        )
    }

    override fun broke(damag: Int): Boolean {
        return if (damage + damag < IDEAL) {
            damage += damag
            true
        } else false
    }
}