package by.profs.rowgame.presenter.competition

import by.profs.rowgame.data.items.Boat
import by.profs.rowgame.data.items.Oar
import by.profs.rowgame.data.items.Rower
import by.profs.rowgame.utils.NumberGenerator
import by.profs.rowgame.view.competition.CompetitionFragment.Companion.raceSize
import kotlin.math.abs

object WaterRaceCalculator : RaceCalculator {

    override fun calculateRace(
        boats: List<Boat>,
        oars: List<Oar>,
        rowers: List<Rower>,
        rating: ArrayList<Pair<Rower, Int>>
    ): ArrayList<Pair<Rower, Int>> {

        if (rating.isEmpty()) for (i in rowers.indices) rating.add(Pair(rowers[i], 0))
        val distances = IntArray(raceSize) {
            NumberGenerator.generatePositiveIntOrNull(MAX_GAP)
        }
        val chances: MutableList<Int> = MutableList(raceSize) { pos ->
            calculatePower(boats[pos], oars[pos], rowers[pos])
        }
        distances.sort()
        var j = 0
        var total = chances.sum()
        while (total > 0) {
            var rand = NumberGenerator.generatePositiveIntOrNull(total)
            var i = -1
            while (rand >= 0) {
                i++
                rand -= chances[i]
            }
            rating[i] = Pair(rowers[i], rating[i].second + distances[j])
            total -= chances[i]
            chances[i] = 0
            j++
        }
        val excessGap: Int = rating.map { it.second }.minOrNull()!!
        rating.forEachIndexed { index, it -> rating[index] = Pair(it.first, it.second - excessGap) }
        return rating
    }

    private fun calculatePower(boat: Boat, oar: Oar, rower: Rower): Int {
        val rowerPenalty = abs(rower.power - rower.technics).coerceAtLeast(
            abs(rower.power - rower.endurance).coerceAtLeast(
                abs(rower.technics - rower.endurance))
        )
        var power = rower.power + rower.technics + rower.endurance - rowerPenalty +
                (boat.weight + boat.wing + oar.blade + oar.weight) * BOAT_OAR_COEF
        if (minIdealWeight[boat.body]!! <= rower.weight &&
            rower.weight <= maxIdealWeight[boat.body]!!) power = (power * IDEAL_WEIGHT_COEF).toInt()
        return power
    }

    private const val BOAT_OAR_COEF = 10
    private const val IDEAL_WEIGHT_COEF = 1.25
    private const val MAX_GAP = 15 // max increase in distance between leader and last boat on 500 m

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
}