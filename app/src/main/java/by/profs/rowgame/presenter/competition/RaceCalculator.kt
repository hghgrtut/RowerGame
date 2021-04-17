package by.profs.rowgame.presenter.competition

import by.profs.rowgame.data.items.Boat
import by.profs.rowgame.data.items.Oar
import by.profs.rowgame.data.items.Rower

interface RaceCalculator {
    fun calculateRace(
        boats: List<Boat>,
        oars: List<Oar>,
        rowers: List<Rower>,
        rating: ArrayList<Pair<Rower, Int>>
    ): ArrayList<Pair<Rower, Int>>
}