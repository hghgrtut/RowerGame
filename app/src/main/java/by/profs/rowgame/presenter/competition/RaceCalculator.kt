package by.profs.rowgame.presenter.competition

import by.profs.rowgame.data.items.Boat
import by.profs.rowgame.data.items.Oar
import by.profs.rowgame.data.items.Rower
import by.profs.rowgame.view.competition.CompetitionFragment.Companion.CONCEPT
import by.profs.rowgame.view.competition.CompetitionFragment.Companion.OFP
import by.profs.rowgame.view.competition.CompetitionFragment.Companion.WATER

class RaceCalculator(private val raceType: Int) {

    fun calculateRace(
        boats: List<Boat>,
        oars: List<Oar>,
        rowers: List<Rower>,
        rating: ArrayList<Pair<Rower, Int>>
    ): ArrayList<Pair<Rower, Int>> {
        val distances = IntArray(rowers.size) { (0..maxGap[raceType]!!).random() }
        distances.sort()
        if (raceType == OFP) distances.forEachIndexed { ind, it -> distances[ind] = MAX_SCORE - it }
        val chances: MutableList<Int> = MutableList(rowers.size) { pos ->
            if (raceType == WATER) calculatePower(boats[pos], oars[pos], rowers[pos])
            else calculatePower(rower = rowers[pos])
        }
        var j = 0
        var total = chances.sum()
        while (total > 0) {
            var rand = (0 until total).random()
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
        val excessGap: Int = if (raceType != OFP) rating.map { it.second }.minOrNull()!!
            else distances.maxOrNull()!! - MAX_SCORE
        rating.forEachIndexed { index, it -> rating[index] = Pair(it.first, it.second - excessGap) }
        return rating
    }

    private fun calculatePower(boat: Boat? = null, oar: Oar? = null, rower: Rower): Int {
        var power: Int = 0
        when (raceType) {
            WATER -> {
                power = (rower.technics * KEY_SKILL_COEF).toInt() + rower.endurance + rower.power +
                        (boat!!.weight + boat.wing + oar!!.blade + oar.weight) * BOAT_OAR_COEF
                if (minIdealWeight[boat.body]!! <= rower.weight &&
                    rower.weight <= maxIdealWeight[boat.body]!!
                ) power = (power * IDEAL_WEIGHT_COEF).toInt()
            }
            CONCEPT -> power = (rower.endurance * KEY_SKILL_COEF).toInt() + rower.power +
                    (rower.technics / KEY_SKILL_COEF).toInt()
            OFP -> power = (rower.power * KEY_SKILL_COEF).toInt() + rower.endurance +
                    (rower.technics / KEY_SKILL_COEF).toInt()
        }
        return power
    }

    companion object {
        private const val BOAT_OAR_COEF = 10
        private const val IDEAL_WEIGHT_COEF = 1.25
        private const val KEY_SKILL_COEF = 1.5
        private const val MAX_SCORE = 100

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
        private val maxGap: HashMap<Int, Int> = hashMapOf(CONCEPT to 40, OFP to 57, WATER to 15)
    }
}