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
        if (raceType.isWaterCompetition()) calculatePower(boats!![lane], oars!![lane], rowers[lane])
        else calculatePower(rower = rowers[lane])
    } }
    private val rating: Array<Pair<Rower, Int>> = Array(rowers.size) { Pair(rowers[it], 0) }

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
        if (raceType.isOFPCompetition())
            distances.forEachIndexed { ind, it -> distances[ind] = MAX_SCORE - it }
        // TODO: check that it selects valid array
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
        val excessGap: Int =
            if (!raceType.isOFPCompetition()) rating.map { it.second }.minOrNull()!!
            else distances.maxOrNull()!! - MAX_SCORE
        rating.forEachIndexed { index, it -> rating[index] = Pair(it.first, it.second - excessGap) }
        return rating
    }

    private fun calculatePower(boat: Boat? = null, oar: Oar? = null, rower: Rower): Int {
        var power: Int = 0
        when (raceType) {
            AbstractCompetition.WATER -> {
                power = (rower.technics * KEY_SKILL_COEF).toInt() + rower.endurance + rower.power +
                        (boat!!.weight + boat.wing + oar!!.blade + oar.weight) * BOAT_OAR_COEF
                if (minIdealWeight[boat.body]!! <= rower.weight &&
                    rower.weight <= maxIdealWeight[boat.body]!!
                ) power = (power * IDEAL_WEIGHT_COEF).toInt()
            }
            AbstractCompetition.CONCEPT -> power = rower.power +
                    (rower.technics / KEY_SKILL_COEF + rower.endurance * KEY_SKILL_COEF).toInt()
            AbstractCompetition.OFP -> power = rower.endurance +
                    (rower.technics / KEY_SKILL_COEF + rower.power * KEY_SKILL_COEF).toInt()
        }
        return power
    }

    fun sortedRating(): List<Pair<Rower, Int>> =
        rating.sortedBy { if (!raceType.isOFPCompetition()) it.second else -it.second }

    companion object {
        private const val BOAT_OAR_COEF = 10
        private const val IDEAL_WEIGHT_COEF = 1.25
        private const val KEY_SKILL_COEF = 1.5
        private const val MAX_SCORE = 100
        private const val OVERALL = 1.5

        private val minIdealWeight: HashMap<Int, Int> = hashMapOf(
            Boat.UNIVERSAL to FOR_UNIVERSAL_MIN,
            Boat.EXTRA_SMALL to FOR_EXTRA_SMALL_MIN,
            Boat.SMALL to FOR_SMALL_MIN,
            Boat.MEDIUM_SMALL to FOR_MEDIUM_SMALL_MIN,
            Boat.MEDIUM_LONG to FOR_MEDIUM_LONG_MIN,
            Boat.LONG to FOR_LONG_MIN,
            Boat.EXTRA_LONG to FOR_EXTRA_LONG_MIN
        )
        private val maxIdealWeight: HashMap<Int, Int> = hashMapOf(
            Boat.UNIVERSAL to FOR_UNIVERSAL_MAX,
            Boat.EXTRA_SMALL to FOR_EXTRA_SMALL_MAX,
            Boat.SMALL to FOR_SMALL_MAX,
            Boat.MEDIUM_SMALL to FOR_MEDIUM_SMALL_MAX,
            Boat.MEDIUM_LONG to FOR_MEDIUM_LONG_MAX,
            Boat.LONG to FOR_LONG_MAX,
            Boat.EXTRA_LONG to FOR_EXTRA_LONG_MAX
        )
        private val maxGap: HashMap<Int, Int> = hashMapOf(
            AbstractCompetition.CONCEPT to 40,
            AbstractCompetition.OFP to 57,
            AbstractCompetition.WATER to 15
        )
    }
}