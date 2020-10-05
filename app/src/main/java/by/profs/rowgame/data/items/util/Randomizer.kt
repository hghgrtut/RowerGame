package by.profs.rowgame.data.items.util

import android.util.Log
import by.profs.rowgame.data.items.Boat
import by.profs.rowgame.data.items.Oar
import by.profs.rowgame.data.items.Rower

object Randomizer {
    private const val big = 1000000
    // from 1 to max inclusive
    private fun getRandomInt(max: Int): Int = (Math.random() * big).toInt() % max + 1
    private fun getRandomFromList(list: List<*>) = list[getRandomInt(list.size) - 1]

    fun getRandomOar(): Oar {
        val manufacturers = Oar.getManufacturersList()
        var oar: Oar
        do {
            oar = Oar(null,
                getRandomFromList(manufacturers) as String,
                getRandomInt(Oar.ELITE),
                getRandomInt(Oar.ELITE),
                Oar.SCULL // getRandomInt(Oar.SWEEP)
            )
        } while (notValid(oar))
        return oar
    }

    fun getRandomBoat(): Boat {
        val type = BoatTypes.SingleScull.name
        val manufacturer = getRandomFromList(Boat.getManufacturersList()) as String
        val premiumBoatList =
            listOf(Manufacturer.Empacher.name, Manufacturer.Filippi.name)
        var body: Int
        do {
            body = getRandomInt(Boat.UNIVERSAL)
        } while (notValid(manufacturer, body))
        val weight =
            when {
                body == Boat.UNIVERSAL -> Boat.RECREATIONAL
                manufacturer == Manufacturer.Nemiga.name -> Boat.SPORTIVE
                premiumBoatList.contains(manufacturer) -> Boat.ELITE
                else -> getRandomInt(2) + 1
            }
        val wing = getRandomWing(manufacturer)
        Log.d("competitionDebug", "boat generated")
        return Boat(null, type, manufacturer, body, weight, wing)
    }

    // Default values for random arriving
    fun getRandomRower(minAge: Int = 10, maxAge: Int = 20, minSkill: Int = 1, maxSkill: Int = 8):
            Rower {
        val fem = Rower.FEMALE
        // Logic is to increase percentage of mans
        val gender = if (getRandomInt(THREE) == 1) fem else Rower.MALE
        val name = "${getRandomFromList(commonSurnames)}${
            if (gender == fem) "a ${getRandomFromList(namesFemale)}"
            else " ${getRandomFromList(namesMale)}"
        }"
        val age = getRandomInt(maxAge - minAge) + minAge - 1
        val height = getRandomHeight(age, gender)
        val weight = when { // Random deviation + minimal weight for this height
            height < HEIGHT_152 -> getRandomInt(RW_DIAP) + WEIGHT_152
            height < HEIGHT_158 -> getRandomInt(RW_DIAP) + WEIGHT_158
            height < HEIGHT_164 -> getRandomInt(RW_DIAP) + WEIGHT_164
            height < HEIGHT_170 -> getRandomInt(RW_DIAP) + WEIGHT_170
            height < HEIGHT_176 -> getRandomInt(RW_DIAP) + WEIGHT_176
            height < HEIGHT_182 -> getRandomInt(RW_DIAP) + WEIGHT_182
            height < HEIGHT_190 -> getRandomInt(RW_DIAP) + WEIGHT_190
            else -> getRandomInt(RW_DIAP * 2) + WEIGHT_200
        }
        return Rower(
            name,
            gender,
            age,
            height,
            weight,
            getRandomInt(maxSkill - minSkill) + minSkill - 1,
            getRandomInt(maxSkill - minSkill) + minSkill - 1,
            getRandomInt(maxSkill - minSkill) + minSkill - 1
        )
    }

    private fun getRandomHeight(age: Int, gender: Int): Int =
        if (gender == Rower.FEMALE) {
            when {
                age < AGE_KID -> getRandomInt(HEIGHT_WOMAN_KID_DEVIATION) + HEIGHT_WOMAN_KID_MINIMAL
                age < AGE_JUN -> getRandomInt(HEIGHT_WOMAN_JUN_DEVIATION) + HEIGHT_WOMAN_JUN_MINIMAL
                else -> getRandomInt(HEIGHT_WOMAN_DEVIATION) + HEIGHT_WOMAN_MINIMAL
            }
        } else {
            when {
                age < AGE_KID -> getRandomInt(HEIGHT_MAN_KID_DEVIATION) + HEIGHT_MAN_KID_MINIMAL
                age < AGE_JUN -> getRandomInt(HEIGHT_MAN_JUN_DEVIATION) + HEIGHT_MAN_JUN_MINIMAL
                else -> getRandomInt(HEIGHT_MAN_DEVIATION) + HEIGHT_MAN_MINIMAL
            }
        }

    private fun getRandomWing(manufacturer: String): Int {
        val wings = when (manufacturer) {
            Manufacturer.Nemiga.name -> listOf(Boat.ALUMINIUM_WING)
            Manufacturer.Hudson.name -> listOf(Boat.ALUMINIUM_WING, Boat.BACKWING)
            Manufacturer.Empacher.name ->
                listOf(Boat.ALUMINIUM_WING, Boat.CARBON_WING, Boat.BACKWING)
            Manufacturer.Peisheng.name ->
                listOf(Boat.ALUMINIUM_WING, Boat.CARBON_WING, Boat.BACKWING)
            else -> listOf(Boat.CLASSIC_STAY, Boat.ALUMINIUM_WING, Boat.CARBON_WING, Boat.BACKWING)
        }
        return getRandomFromList(wings) as Int
    }

    private fun notValid(oar: Oar): Boolean =
        oar.weight == Oar.RECREATIONAL && (oar.blade == Oar.ELITE || oar.type == Oar.SWEEP)

    private fun notValid(manufacturer: String, body: Int): Boolean {
        return when (manufacturer) {
            Manufacturer.Nemiga.name -> body != Boat.LONG && body != Boat.MEDIUM_LONG
            Manufacturer.Hudson.name -> body == Boat.UNIVERSAL
            else -> false
        }
    }

    private const val AGE_KID = 14
    private const val AGE_JUN = 14
    private const val HEIGHT_WOMAN_KID_DEVIATION = 62
    private const val HEIGHT_WOMAN_JUN_DEVIATION = 47
    private const val HEIGHT_WOMAN_DEVIATION = 54
    private const val HEIGHT_MAN_KID_DEVIATION = 66
    private const val HEIGHT_MAN_JUN_DEVIATION = 52
    private const val HEIGHT_MAN_DEVIATION = 56
    private const val HEIGHT_WOMAN_KID_MINIMAL = 123
    private const val HEIGHT_WOMAN_JUN_MINIMAL = 147
    private const val HEIGHT_WOMAN_MINIMAL = 150
    private const val HEIGHT_MAN_KID_MINIMAL = 116
    private const val HEIGHT_MAN_JUN_MINIMAL = 147
    private const val HEIGHT_MAN_MINIMAL = 160
    private const val THREE = 3
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