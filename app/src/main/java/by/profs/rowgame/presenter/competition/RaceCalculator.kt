package by.profs.rowgame.presenter.competition

import by.profs.rowgame.data.items.Boat
import by.profs.rowgame.data.items.Oar
import by.profs.rowgame.data.items.Rower
import by.profs.rowgame.utils.NumberGenerator
import by.profs.rowgame.view.CompetitionActivity

object RaceCalculator {

    fun calculateRace(
        boats: List<Boat>,
        oars: List<Oar>,
        rowers: List<Rower>,
        rating: ArrayList<Pair<Rower, Int>>
    ): ArrayList<Pair<Rower, Int>> {

        if (rating.isEmpty()) for (i in rowers.indices) rating.add(Pair(rowers[i], 0))
        val distances = IntArray(CompetitionActivity.raceSize) {
            NumberGenerator.generatePositiveIntOrNull(MAX_GAP)
        }
        val chances: MutableList<Int> = MutableList(CompetitionActivity.raceSize) { pos ->
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
        return rating
    }

    private fun calculatePower(boat: Boat, oar: Oar, rower: Rower): Int {
        var power = rower.power + rower.technics + rower.endurance +
                (boat.weight + boat.wing + oar.blade + oar.weight) * BOAT_OAR_COEF
        if (minIdealWeight[boat.body]!! <= rower.weight &&
            rower.weight <= maxIdealWeight[boat.body]!!) power = (power * IDEAL_WEIGHT_COEF).toInt()
        return power
    }

    private const val BOAT_OAR_COEF = 10
    private const val IDEAL_WEIGHT_COEF = 1.25
    private const val MAX_GAP = 15 // max increase in distance between leader and last boat on 500 m

    private val minIdealWeight: HashMap<Int, Int> = hashMapOf(
        Boat.UNIVERSAL to 60,
        Boat.EXTRA_SMALL to 50,
        Boat.SMALL to 61,
        Boat.MEDIUM_SMALL to 70,
        Boat.MEDIUM_LONG to 74,
        Boat.LONG to 84,
        Boat.EXTRA_LONG to 98
    )
    private val maxIdealWeight: HashMap<Int, Int> = hashMapOf(
        Boat.UNIVERSAL to 100,
        Boat.EXTRA_SMALL to 65,
        Boat.SMALL to 75,
        Boat.MEDIUM_SMALL to 85,
        Boat.MEDIUM_LONG to 87,
        Boat.LONG to 100,
        Boat.EXTRA_LONG to 120
    )
}