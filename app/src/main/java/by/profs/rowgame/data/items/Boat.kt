package by.profs.rowgame.data.items

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import by.profs.rowgame.data.consts.ID_BOAT
import by.profs.rowgame.data.consts.TABLE_BOAT
import by.profs.rowgame.data.items.util.Manufacturer
import by.profs.rowgame.presenter.competition.FOR_EXTRA_LONG_MAX
import by.profs.rowgame.presenter.competition.FOR_EXTRA_LONG_MIN
import by.profs.rowgame.presenter.competition.FOR_EXTRA_SMALL_MAX
import by.profs.rowgame.presenter.competition.FOR_EXTRA_SMALL_MIN
import by.profs.rowgame.presenter.competition.FOR_LONG_MAX
import by.profs.rowgame.presenter.competition.FOR_LONG_MIN
import by.profs.rowgame.presenter.competition.FOR_MEDIUM_LONG_MAX
import by.profs.rowgame.presenter.competition.FOR_MEDIUM_LONG_MIN
import by.profs.rowgame.presenter.competition.FOR_MEDIUM_SMALL_MAX
import by.profs.rowgame.presenter.competition.FOR_MEDIUM_SMALL_MIN
import by.profs.rowgame.presenter.competition.FOR_SMALL_MAX
import by.profs.rowgame.presenter.competition.FOR_SMALL_MIN
import by.profs.rowgame.presenter.competition.FOR_UNIVERSAL_MAX
import by.profs.rowgame.presenter.competition.FOR_UNIVERSAL_MIN
import by.profs.rowgame.utils.IDEAL

@Entity(tableName = TABLE_BOAT)
data class Boat(
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = ID_BOAT) override val id: Int?,
    @ColumnInfo(name = "type") val type: String,
    @ColumnInfo(name = "manufacturer") val manufacturer: String,
    @ColumnInfo(name = "body") val body: Int,
    @ColumnInfo(name = "weight") val weight: Int,
    @ColumnInfo(name = "wing") val wing: Int,
    @ColumnInfo(name = "damage") var damage: Int = 0
) : Item {
    override fun getPower(): Int = weight * weightCoeff + wing * wingCoeff

    override fun getLevel(): Int = weight * wing - 1

    override fun broke(damag: Int): Boolean {
        return if (damage + damag < IDEAL) {
            damage += damag
            true
        } else false
    }

    internal fun isIdealFor(weight: Int): Boolean =
        minIdealWeight[body]!! <= weight && weight <= maxIdealWeight[body]!!

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

        private const val wingCoeff = 100
        private const val weightCoeff = 150
        private val minIdealWeight: HashMap<Int, Int> = hashMapOf(
            UNIVERSAL to FOR_UNIVERSAL_MIN,
            EXTRA_SMALL to FOR_EXTRA_SMALL_MIN,
            SMALL to FOR_SMALL_MIN,
            MEDIUM_SMALL to FOR_MEDIUM_SMALL_MIN,
            MEDIUM_LONG to FOR_MEDIUM_LONG_MIN,
            LONG to FOR_LONG_MIN,
            EXTRA_LONG to FOR_EXTRA_LONG_MIN
        )
        private val maxIdealWeight: HashMap<Int, Int> = hashMapOf(
            UNIVERSAL to FOR_UNIVERSAL_MAX,
            EXTRA_SMALL to FOR_EXTRA_SMALL_MAX,
            SMALL to FOR_SMALL_MAX,
            MEDIUM_SMALL to FOR_MEDIUM_SMALL_MAX,
            MEDIUM_LONG to FOR_MEDIUM_LONG_MAX,
            LONG to FOR_LONG_MAX,
            EXTRA_LONG to FOR_EXTRA_LONG_MAX
        )

        fun getManufacturersList(): List<String> = listOf(Manufacturer.Empacher.name,
            Manufacturer.Filippi.name,
            Manufacturer.Hudson.name,
            Manufacturer.Nemiga.name,
            Manufacturer.Peisheng.name,
            Manufacturer.Swift.name
        )
    }
}