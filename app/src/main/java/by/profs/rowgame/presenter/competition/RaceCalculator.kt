package by.profs.rowgame.presenter.competition

import by.profs.rowgame.data.competition.CompetitionStrategy
import by.profs.rowgame.data.items.Boat
import by.profs.rowgame.data.items.Oar
import by.profs.rowgame.data.items.Rower
import by.profs.rowgame.presenter.competition.type.AbstractCompetition
import by.profs.rowgame.presenter.competition.type.AbstractCompetition.Companion.isOFPCompetition
import by.profs.rowgame.presenter.competition.type.AbstractCompetition.Companion.isWaterCompetition

class RaceCalculator(
    private val raceType: Int,
    private val rowers: List<Rower>,
    boats: List<Boat>? = null,
    oars: List<Oar>? = null
) {
    var phase: Int = AbstractCompetition.BEFORE
        private set
    private val chances = Array(CompetitionStrategy.values().size) { Array(rowers.size) { lane ->
        if (!raceType.isWaterCompetition()) rowers[lane].calculatePower()
        else getPowerOnWater(boats!![lane], oars!![lane], rowers[lane])
    } }
    private val rating: Array<Pair<Rower, Int>> = Array(rowers.size) { Pair(rowers[it], 0) }
    private val isOfp: Boolean = raceType.isOFPCompetition()

    init {
        val overall = CompetitionStrategy.OVERALL.ordinal
        rowers.forEachIndexed { index, rower -> chances[rower.strategy][index] =
                if (rower.strategy != overall) chances[rower.strategy][index] * 2
                else (chances[rower.strategy][index] * OVERALL).toInt()
        }
    }

    fun calculateRace(): Array<Pair<Rower, Int>> {
        phase++
        val distances = IntArray(rating.size) { (0..maxGap[raceType]!!).random() }
        distances.sort()
        if (isOfp) distances.forEachIndexed { ind, it -> distances[ind] = MAX_SCORE - it }
        val chance = chances[when (phase) {
            AbstractCompetition.START -> 0
            AbstractCompetition.FINISH -> 2
            else -> 1
        }].clone()
        var total = chance.sum()
        var place = 0
        while (total > 0) {
            var rand = (0 until total).random()
            var i = -1
            while (rand >= 0) {
                i++
                rand -= chance[i]
            }
            rating[i] = Pair(rowers[i], rating[i].second + distances[place])
            total -= chance[i]
            chance[i] = 0
            place++
        }
        if (!isOfp) {
            val excessGap: Int = rating.map { it.second }.minOrNull()!!
            rating.forEachIndexed { ind, it -> rating[ind] = Pair(it.first, it.second - excessGap) }
        }
        return rating
    }

    private fun Rower.calculatePower(): Int = if (raceType == AbstractCompetition.CONCEPT) {
        power + (technics / KEY_SKILL_COEF + endurance * KEY_SKILL_COEF).toInt()
    } else { endurance + (technics / KEY_SKILL_COEF + power * KEY_SKILL_COEF).toInt() }

    fun sortedRating() = rating.sortedBy { if (!isOfp) it.second else -it.second }

    companion object {
        fun getPowerOnWater(boat: Boat, oar: Oar, rower: Rower): Int {
            var result = rower.run { (technics * KEY_SKILL_COEF).toInt() + endurance + power }
            result += boat.getPower() + oar.getPower()
            if (boat.isIdealFor(rower.weight)) result = (result * IDEAL_WEIGHT_COEF).toInt()
            return result
        }

        private const val IDEAL_WEIGHT_COEF = 1.25
        private const val KEY_SKILL_COEF = 1.5
        private const val MAX_SCORE = 100
        private const val OVERALL = 1.5
        private val maxGap: HashMap<Int, Int> = hashMapOf(
            AbstractCompetition.CONCEPT to 40,
            AbstractCompetition.OFP to 57,
            AbstractCompetition.WATER to 15
        )
    }
}