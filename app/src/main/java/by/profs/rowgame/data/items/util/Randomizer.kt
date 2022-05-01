package by.profs.rowgame.data.items.util

import by.profs.rowgame.data.competition.Ages
import by.profs.rowgame.data.competition.CompetitionStrategy
import by.profs.rowgame.data.items.Boat
import by.profs.rowgame.data.items.Oar
import by.profs.rowgame.data.items.Rower

object Randomizer {
    fun getRandomOar(maxLevel: Int = Int.MAX_VALUE): Oar {
        val manufacturers = Oar.getManufacturersList()
        var oar: Oar
        do {
            oar = Oar(null,
                manufacturers.random(),
                (Oar.RECREATIONAL..Oar.ELITE).random(),
                (Oar.RECREATIONAL..Oar.ELITE).random(),
                Oar.SCULL
            )
        } while (oar.notValid() || oar.getLevel() > maxLevel)
        return oar
    }

    fun getRandomBoat(maxLevel: Int = Int.MAX_VALUE): Boat {
        val type = BoatTypes.SingleScull.name
        val manufacturer = Boat.getManufacturersList().random()
        val premiumBoatList = listOf(Manufacturer.Empacher.name, Manufacturer.Filippi.name)
        var body = -1
        while (notValid(manufacturer, body)) body = (1..Boat.UNIVERSAL).random()
        val weight =
            when {
                body == Boat.UNIVERSAL -> Boat.RECREATIONAL
                manufacturer == Manufacturer.Nemiga.name -> Boat.SPORTIVE
                premiumBoatList.contains(manufacturer) -> Boat.ELITE
                else -> (Boat.SPORTIVE..Boat.ELITE).random()
            }
        val wing = getRandomWing(manufacturer)
        val boat = Boat(null, type, manufacturer, body, weight, wing)
        return if (boat.getLevel() > maxLevel) getRandomBoat(maxLevel) else boat
    }

    // Default values for random arriving
    fun getRandomRower(minAge: Int = 14, maxAge: Int = 20, minSkill: Int = 1, maxSkill: Int = 8):
            Rower {
        val gender = Rower.MALE
        val name = "${commonSurnames.random()} ${namesMale.random()}"
        val age = (minAge..maxAge).random()
        val height = when {
            age < Ages.Kid.age -> (HEIGHT_MAN_KID_MINIMAL..HEIGHT_MAN_KID_MAXIMAL)
            age < Ages.Jun.age -> (HEIGHT_MAN_JUN_MINIMAL..HEIGHT_MAN_JUN_MAXIMAL)
            else -> (HEIGHT_MAN_MINIMAL..HEIGHT_MAN_MAXIMAL)
        }.random()
        val weight = when { // Minimal weight for this height + random deviation
            height < HEIGHT_152 -> (WEIGHT_152..WEIGHT_152 + RW_DIAP)
            height < HEIGHT_158 -> (WEIGHT_158..WEIGHT_158 + RW_DIAP)
            height < HEIGHT_164 -> (WEIGHT_164..WEIGHT_164 + RW_DIAP)
            height < HEIGHT_170 -> (WEIGHT_170..WEIGHT_170 + RW_DIAP)
            height < HEIGHT_176 -> (WEIGHT_176..WEIGHT_176 + RW_DIAP)
            height < HEIGHT_182 -> (WEIGHT_182..WEIGHT_182 + RW_DIAP)
            height < HEIGHT_190 -> (WEIGHT_190..WEIGHT_190 + RW_DIAP)
            else -> (WEIGHT_200..WEIGHT_200 + RW_DIAP * 2)
        }.random()
        return Rower(
            id = null,
            name,
            gender,
            age,
            height,
            weight,
            (minSkill..maxSkill).random(),
            (minSkill..maxSkill).random(),
            (minSkill..maxSkill).random(),
            strategy = (CompetitionStrategy.values().indices).random()
        )
    }

    private fun getRandomWing(manufacturer: String): Int = when (manufacturer) {
        Manufacturer.Nemiga.name -> listOf(Boat.ALUMINIUM_WING)
        Manufacturer.Hudson.name -> listOf(Boat.ALUMINIUM_WING, Boat.BACKWING)
        Manufacturer.Empacher.name, Manufacturer.Peisheng.name ->
            listOf(Boat.ALUMINIUM_WING, Boat.CARBON_WING, Boat.BACKWING)
        else -> listOf(Boat.CLASSIC_STAY, Boat.ALUMINIUM_WING, Boat.CARBON_WING, Boat.BACKWING)
    }.random()

    private fun Oar.notValid(): Boolean =
        weight == Oar.RECREATIONAL && (blade == Oar.ELITE || type == Oar.SWEEP)

    private fun notValid(manufacturer: String, body: Int): Boolean {
        return if (body < Boat.EXTRA_SMALL || body > Boat.UNIVERSAL) true else when (manufacturer) {
            Manufacturer.Nemiga.name -> body != Boat.LONG && body != Boat.MEDIUM_LONG
            Manufacturer.Hudson.name -> body == Boat.UNIVERSAL
            else -> false
        }
    }

    private const val HEIGHT_MAN_KID_MAXIMAL = 182
    private const val HEIGHT_MAN_JUN_MAXIMAL = 199
    private const val HEIGHT_MAN_MAXIMAL = 216
    private const val HEIGHT_MAN_KID_MINIMAL = 116
    private const val HEIGHT_MAN_JUN_MINIMAL = 147
    private const val HEIGHT_MAN_MINIMAL = 160
    // Rower weights
    private const val RW_DIAP = 16
    private const val HEIGHT_152 = 152
    private const val HEIGHT_158 = 158
    private const val HEIGHT_164 = 164
    private const val HEIGHT_170 = 170
    private const val HEIGHT_176 = 176
    private const val HEIGHT_182 = 182
    private const val HEIGHT_190 = 190
    // Minimal rower weight (kg) for specified height (sm)
    private const val WEIGHT_152 = 32
    private const val WEIGHT_158 = 43
    private const val WEIGHT_164 = 46
    private const val WEIGHT_170 = 54
    private const val WEIGHT_176 = 60
    private const val WEIGHT_182 = 64
    private const val WEIGHT_190 = 69
    private const val WEIGHT_200 = 76
}