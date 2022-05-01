package by.profs.rowgame.presenter.competition.type

import by.profs.rowgame.data.items.Rower
import by.profs.rowgame.presenter.competition.RaceCalculator

interface AbstractCompetition {
    suspend fun setupRace()

    fun getRaceCalculator(): RaceCalculator

    fun deleteRaceCalculator()

    fun getRaceRowers(): List<Rower>

    fun raceTitle(): String

    fun calculateRace(): Array<Pair<Rower, Int>> = getRaceCalculator().calculateRace()

    val changeStrategy: (Int, Int) -> Unit

    companion object {
        const val BEFORE = 14
        const val START = 15
        const val HALF = 16
        const val ONE_AND_HALF = 17
        const val FINISH = 18

        const val CONCEPT = 1
        const val OFP = 2
        const val WATER = 0

        const val TOTAL_ROWERS = 36

        fun Int.isWaterCompetition(): Boolean = this == WATER
        fun Int.isOFPCompetition(): Boolean = this == OFP
    }
}