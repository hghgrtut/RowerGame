package by.profs.rowgame.data.items.util

import by.profs.rowgame.data.competition.Ages
import by.profs.rowgame.data.competition.CompetitionStrategy
import by.profs.rowgame.data.items.Boat
import by.profs.rowgame.data.items.Oar
import by.profs.rowgame.data.items.Rower

object Randomizer {
    private fun getRandomFromList(list: List<*>) = list[(0..list.lastIndex).random()]

    fun getRandomOar(): Oar {
        val manufacturers = Oar.getManufacturersList()
        var oar: Oar
        do {
            oar = Oar(null,
                getRandomFromList(manufacturers) as String,
                (Oar.RECREATIONAL..Oar.ELITE).random(),
                (Oar.RECREATIONAL..Oar.ELITE).random(),
                Oar.SCULL
            )
        } while (notValid(oar))
        return oar
    }

    fun getRandomBoat(): Boat {
        val type = BoatTypes.SingleScull.name
        val manufacturer = getRandomFromList(Boat.getManufacturersList()) as String
        val premiumBoatList =
            listOf(Manufacturer.Empacher.name, Manufacturer.Filippi.name)
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
        return Boat(null, type, manufacturer, body, weight, wing)
    }

    // Default values for random arriving
    fun getRandomRower(minAge: Int = 14, maxAge: Int = 20, minSkill: Int = 1, maxSkill: Int = 8):
            Rower {
        val gender = Rower.MALE
        val name = "${getRandomFromList(commonSurnames)} ${getRandomFromList(namesMale)}"
        val age = (minAge..maxAge).random()
        val height = getRandomHeight(age, gender)
        val weight = when { // Minimal weight for this height + random deviation
            height < HEIGHT_152 -> (WEIGHT_152..WEIGHT_152 + RW_DIAP).random()
            height < HEIGHT_158 -> (WEIGHT_158..WEIGHT_158 + RW_DIAP).random()
            height < HEIGHT_164 -> (WEIGHT_164..WEIGHT_164 + RW_DIAP).random()
            height < HEIGHT_170 -> (WEIGHT_170..WEIGHT_170 + RW_DIAP).random()
            height < HEIGHT_176 -> (WEIGHT_176..WEIGHT_176 + RW_DIAP).random()
            height < HEIGHT_182 -> (WEIGHT_182..WEIGHT_182 + RW_DIAP).random()
            height < HEIGHT_190 -> (WEIGHT_190..WEIGHT_190 + RW_DIAP).random()
            else -> (WEIGHT_200..WEIGHT_200 + RW_DIAP * 2).random()
        }
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

    private fun getRandomHeight(age: Int, gender: Int): Int =
        if (gender == Rower.FEMALE) {
            when {
                age < Ages.Kid.age -> (HEIGHT_WOMAN_KID_MINIMAL..HEIGHT_WOMAN_KID_MAXIMAL).random()
                age < Ages.Jun.age -> (HEIGHT_WOMAN_JUN_MINIMAL..HEIGHT_WOMAN_JUN_MAXIMAL).random()
                else -> (HEIGHT_WOMAN_MINIMAL..HEIGHT_WOMAN_MAXIMAL).random()
            }
        } else {
            when {
                age < Ages.Kid.age -> (HEIGHT_MAN_KID_MINIMAL..HEIGHT_MAN_KID_MAXIMAL).random()
                age < Ages.Jun.age -> (HEIGHT_MAN_JUN_MINIMAL..HEIGHT_MAN_JUN_MAXIMAL).random()
                else -> (HEIGHT_MAN_MINIMAL..HEIGHT_MAN_MAXIMAL).random()
            }
        }

    private fun getRandomWing(manufacturer: String): Int {
        val wings = when (manufacturer) {
            Manufacturer.Nemiga.name -> listOf(Boat.ALUMINIUM_WING)
            Manufacturer.Hudson.name -> listOf(Boat.ALUMINIUM_WING, Boat.BACKWING)
            Manufacturer.Empacher.name, Manufacturer.Peisheng.name ->
                listOf(Boat.ALUMINIUM_WING, Boat.CARBON_WING, Boat.BACKWING)
            else -> listOf(Boat.CLASSIC_STAY, Boat.ALUMINIUM_WING, Boat.CARBON_WING, Boat.BACKWING)
        }
        return getRandomFromList(wings) as Int
    }

    private fun notValid(oar: Oar): Boolean =
        oar.weight == Oar.RECREATIONAL && (oar.blade == Oar.ELITE || oar.type == Oar.SWEEP)

    private fun notValid(manufacturer: String, body: Int): Boolean {
        return if (body < Boat.EXTRA_SMALL || body > Boat.UNIVERSAL) true else when (manufacturer) {
            Manufacturer.Nemiga.name -> body != Boat.LONG && body != Boat.MEDIUM_LONG
            Manufacturer.Hudson.name -> body == Boat.UNIVERSAL
            else -> false
        }
    }

    private const val HEIGHT_WOMAN_KID_MAXIMAL = 185
    private const val HEIGHT_WOMAN_JUN_MAXIMAL = 194
    private const val HEIGHT_WOMAN_MAXIMAL = 204
    private const val HEIGHT_MAN_KID_MAXIMAL = 182
    private const val HEIGHT_MAN_JUN_MAXIMAL = 199
    private const val HEIGHT_MAN_MAXIMAL = 216
    private const val HEIGHT_WOMAN_KID_MINIMAL = 123
    private const val HEIGHT_WOMAN_JUN_MINIMAL = 147
    private const val HEIGHT_WOMAN_MINIMAL = 150
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