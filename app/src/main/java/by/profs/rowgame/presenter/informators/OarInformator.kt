package by.profs.rowgame.presenter.informators

import by.profs.rowgame.R
import by.profs.rowgame.data.items.Oar
import by.profs.rowgame.data.items.util.Manufacturer

class OarInformator : Informator<Oar> {
    override fun getItemInfo(item: Oar): List<String> {
        return when (item.manufacturer) {
            Manufacturer.Braca.name -> getBracaInfo(item)
            Manufacturer.Concept.name -> getConceptInfo(item)
            Manufacturer.Croker.name ->
                crokerOarInfo[item.type * TYPE + item.blade * BL + item.weight]!!.split("|")
            else -> emptyList()
        }
    }

    private fun getBracaInfo(oar: Oar): List<String> = listOf(
        bracaOarBlade[oar.blade]!!,
        bracaOarModels[oar.weight]!!,
        bracaOarScullWeight[oar.weight]!!
    )

    private fun getConceptInfo(oar: Oar): List<String> {
        return listOf(
            conceptOarBlade[oar.blade]!!,
            conceptOarModels[oar.weight]!!,
            conceptOarScullWeight[oar.weight]!!
        )
    }

    companion object {
        // For calculating hashes
        const val TYPE = 100
        const val BL = 10

        // Blades
        const val ARROW = "Arrow"
        const val BIG_BLADE = "Big Blade"
        const val DOUBLE_WING = "Double Wing"
        const val FAT2 = "Fat2"
        const val MACON = "Macon"
        const val SLICK = "Slick"
        const val SMOOTHIE2 = "Smoothie2"

        val bladeImages: HashMap<String, Int> = hashMapOf(
            ARROW to R.drawable.blade_arrow,
            BIG_BLADE to R.drawable.blade_big_blade,
            DOUBLE_WING to R.drawable.blade_double_wing,
            FAT2 to R.drawable.blade_fat2,
            MACON to R.drawable.blade_macon,
            SLICK to R.drawable.blade_slick,
            SMOOTHIE2 to R.drawable.blade_smoothie2
        )

        private val bracaOarBlade: HashMap<Int, String> = hashMapOf(
            Oar.RECREATIONAL to MACON,
            Oar.SPORTIVE to BIG_BLADE,
            Oar.ELITE to DOUBLE_WING
        )
        private val bracaOarModels: HashMap<Int, String> = hashMapOf(
            Oar.RECREATIONAL to "Recreational",
            Oar.SPORTIVE to "Standart",
            Oar.ELITE to "Ultra Light"
        )
        private val bracaOarScullWeight: HashMap<Int, String> = hashMapOf(
            Oar.RECREATIONAL to "1,85",
            Oar.SPORTIVE to "1,6",
            Oar.ELITE to "1,4"
        )

        private val conceptOarBlade: HashMap<Int, String> = hashMapOf(
            Oar.RECREATIONAL to MACON,
            Oar.SPORTIVE to SMOOTHIE2,
            Oar.ELITE to FAT2
        )
        private val conceptOarModels: HashMap<Int, String> = hashMapOf(
            Oar.RECREATIONAL to "Low I",
            Oar.SPORTIVE to "UltraLight",
            Oar.ELITE to "Skinny"
        )
        private val conceptOarScullWeight: HashMap<Int, String> = hashMapOf(
            Oar.RECREATIONAL to "1,8",
            Oar.SPORTIVE to "1.6",
            Oar.ELITE to "1,35"
        )

        // Type, blade, model
        private val crokerOarInfo: HashMap<Int, String> = hashMapOf(
            111 to "Macon|S5/S6|1,8",
            112 to "Macon|S3|1,65",
            113 to "Macon|S7/S4|1,315",
            121 to "Slick|S5/S6|1,8",
            122 to "Slick|S3|1,65",
            123 to "Slick|S7/S4|1,315",
            132 to "Arrow|S40|1,5",
            133 to "Arrow|S39|1,3",
            211 to "Macon|M1/M5|3,35",
            212 to "Macon|M2|2,65",
            213 to "Macon|M4|2,5",
            221 to "Slick|M1/M5|3,35",
            222 to "Slick|M2|2,65",
            223 to "Slick|M4|2,5",
            232 to "Arrow|M49|2,8",
            233 to "Arrow|M47|2,35"
        )
    }
}