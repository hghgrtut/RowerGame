package by.profs.rowgame.presenter.competition

import by.profs.rowgame.data.items.Boat
import by.profs.rowgame.data.items.Oar
import by.profs.rowgame.data.items.Rower
import by.profs.rowgame.utils.NumberGenerator
import by.profs.rowgame.view.CompetitionActivity

object RaceCalculator {
    fun calculateFinal(boats: List<Boat>, oars: List<Oar>, rowers: List<Rower>):
            ArrayList<Rower> {
        val rating = calculateRace(boats, oars, rowers)
        return ArrayList(rating.map { it.first })
    }

    fun calculateRace(
        boats: List<Boat>,
        oars: List<Oar>,
        rowers: List<Rower>,
        rating: ArrayList<Pair<Rower, Int>> = ArrayList()
    ): ArrayList<Pair<Rower, Int>> {

        if (rating.isEmpty()) for (i in rowers.indices) rating.add(Pair(rowers[i], 0))
        val distances = IntArray(CompetitionActivity.raceSize) {
            NumberGenerator.generatePositiveIntOrNull(
                CompetitionActivity.MAX_GAP
            )
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

    private fun calculatePower(boat: Boat, oar: Oar, rower: Rower) = rower.power + rower.technics +
            rower.endurance + (boat.weight + boat.wing + oar.blade + oar.weight) * BOAT_OAR_COEF


    const val BOAT_OAR_COEF = 10
}