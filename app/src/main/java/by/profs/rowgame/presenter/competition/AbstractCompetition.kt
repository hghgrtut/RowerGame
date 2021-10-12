package by.profs.rowgame.presenter.competition

import by.profs.rowgame.data.items.Rower

interface AbstractCompetition {
    var raceCalculator: RaceCalculator?

    suspend fun setupRace()

    fun getRaceRowers(): MutableList<Rower>

    fun raceTitle(): String

    fun calculateRace(): Array<Pair<Rower, Int>> = raceCalculator!!.calculateRace()

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