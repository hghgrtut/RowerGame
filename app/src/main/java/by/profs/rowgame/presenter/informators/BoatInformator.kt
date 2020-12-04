package by.profs.rowgame.presenter.informators

import by.profs.rowgame.data.items.Boat
import by.profs.rowgame.data.items.util.Manufacturer
import java.lang.StringBuilder

class BoatInformator : Informator<Boat> {
    // Return length, rower weight, model
    override fun getItemInfo(item: Boat): List<String> = when (item.manufacturer) {
        Manufacturer.Filippi.name -> getFilippiInfo(item)
        Manufacturer.Empacher.name -> getEmpacherInfo(item)
        Manufacturer.Hudson.name -> getHudsonInfo(item)
        Manufacturer.Nemiga.name -> getNemigaInfo(item)
        Manufacturer.Peisheng.name -> getPeishengInfo(item)
        Manufacturer.Swift.name -> getSwiftInfo(item)
        else -> throw IllegalArgumentException("Unknown manufacturer: $item")
    }

    private fun getFilippiInfo(boat: Boat): List<String> {
        return when (boat.body) {
            Boat.UNIVERSAL -> listOf("7,2", "60-75", "7,20 as/aw")
            Boat.EXTRA_SMALL -> listOf("7,4", "50-60", "F44")
            Boat.SMALL -> listOf("7,75", "65-75", "F15")
            Boat.MEDIUM_SMALL -> listOf("7,86", "70-85", "F45")
            Boat.MEDIUM_LONG -> listOf("8,0", "75-85", "F22")
            Boat.LONG -> listOf("8,33", "85-100", "F14/F21")
            else -> listOf("8,44", "95-110", "F39")
        }
    }

    private fun getEmpacherInfo(boat: Boat): List<String> {
        val model = StringBuilder(when (boat.wing) {
            Boat.ALUMINIUM_WING -> "R"
            Boat.CARBON_WING -> "C"
            else -> "X"
        })
        return when (boat.body) {
            Boat.UNIVERSAL -> listOf("6,32", "60-100", model.append("07").toString())
            Boat.EXTRA_SMALL -> listOf("7,4", "45-65", model.append("14").toString())
            Boat.SMALL -> listOf("7,66", "65-75", model.append("17").toString())
            Boat.MEDIUM_SMALL -> listOf("7,78", "75-85", model.append("16").toString())
            Boat.MEDIUM_LONG -> listOf("7,92", "80-90", model.append("08").toString())
            Boat.LONG -> listOf("8,2", "85-100", model.append("10").toString())
            else -> listOf("8,3", "95-120", model.append("11").toString())
        }
    }

    private fun getHudsonInfo(boat: Boat): List<String> {
        val model = StringBuilder(if (boat.wing == Boat.ALUMINIUM_WING) "S" else "U")
        return when (boat.body) {
            Boat.EXTRA_SMALL -> listOf("7,24", "52-63", model.append("1.12").toString())
            Boat.SMALL -> listOf("7,4", "52-66", model.append("1.11").toString())
            Boat.MEDIUM_SMALL -> listOf("8,0", "66-79", model.append("1.21").toString())
            Boat.MEDIUM_LONG -> listOf("8,0", "73-86", model.append("1.32").toString())
            Boat.LONG -> listOf("8,2", "84-98", model.append("1.42").toString())
            else -> listOf("8,2", "98-111", model.append("1.42+").toString())
        }
    }

    private fun getNemigaInfo(boat: Boat): List<String> = when (boat.body) {
            Boat.LONG -> listOf("7,96", "85-100", "СНЛК 855")
            else -> listOf("8,0", "75-85", "СНЛК 823")
    }

    private fun getPeishengInfo(boat: Boat): List<String> {
        val weight = if (boat.weight == Boat.ELITE) "A++" else "A"
        return when (boat.body) {
            Boat.UNIVERSAL -> listOf("6,16", "70-100", "Gig")
            Boat.EXTRA_SMALL -> listOf("7,36", "55-65", "210 $weight")
            Boat.SMALL -> listOf("7,78", "65-75", "212 $weight")
            Boat.MEDIUM_SMALL -> listOf("7,91", "70-85", "120 $weight")
            Boat.MEDIUM_LONG -> listOf("7,8", "75-85", "115 $weight")
            Boat.LONG -> listOf("8,35", "85-100", "113/211 $weight")
            else -> listOf("8,33", "100-120", "119 $weight")
        }
    }

    private fun getSwiftInfo(boat: Boat): List<String> {
        val weight = if (boat.weight == Boat.ELITE) "Elite Carbon" else "Club A"
        return when (boat.body) {
            Boat.UNIVERSAL -> listOf("6,36", "65-105", "Recreational $weight")
            Boat.EXTRA_SMALL -> listOf("7,4", "50-65", "105- $weight")
            Boat.SMALL -> listOf("7,75", "57-70", "107- $weight")
            Boat.MEDIUM_SMALL -> listOf("7,86", "70-83", "104- $weight")
            Boat.MEDIUM_LONG -> listOf("8,0", "75-90", "110- $weight")
            Boat.LONG -> listOf("8,33", "83-100", "103- $weight")
            else -> listOf("8,44", "100-115", "106- $weight")
        }
    }
}