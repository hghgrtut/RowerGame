package by.profs.rowgame.presenter.mappers

import android.content.Context
import by.profs.rowgame.R
import by.profs.rowgame.app.ServiceLocator
import by.profs.rowgame.data.combos.ComboItem
import by.profs.rowgame.data.items.Boat
import by.profs.rowgame.data.items.Oar
import by.profs.rowgame.data.items.Rower
import by.profs.rowgame.data.items.util.Manufacturer
import by.profs.rowgame.presenter.competition.RaceCalculator.Companion.getPowerOnWater
import by.profs.rowgame.presenter.informators.OarInformator

object ComboItemWrapper {
    private val oarInformator: OarInformator = OarInformator()

    fun map(boats: List<Boat>, oars: List<Oar>, rowers: List<Rower>): List<ComboItem> =
        List(rowers.size) { i -> map(boats[i], oars[i], rowers[i]) }

    private fun map(boat: Boat, oar: Oar, rower: Rower): ComboItem {
        val logoBoat = Manufacturer.valueOf(boat.manufacturer).logoResId
        val logoOar = Manufacturer.valueOf(oar.manufacturer).logoResId

        val context = ServiceLocator.get(Context::class)
        val height = context.getString(R.string.rower_height, rower.height)
        val rowerWeight = context.getString(R.string.rower_weight, rower.weight)
        val age = context.getString(R.string.rower_age, rower.age)

        val rigger = context.getString(R.string.rigger, when (boat.wing) {
                Boat.CLASSIC_STAY -> context.getString(R.string.rigger_classic)
                Boat.ALUMINIUM_WING -> context.getString(R.string.rigger_aluminium_wing)
                Boat.CARBON_WING -> context.getString(R.string.rigger_carbon_wing)
                else -> context.getString(R.string.rigger_backwing)
            })
        val boatWeight = context.getString(R.string.item_weight, when (boat.weight) {
                    Boat.ELITE -> "14"
                    Boat.SPORTIVE -> "16"
                    else -> "18"
            })

        val info = oarInformator.getItemInfo(oar)
        val blade = context.getString(R.string.blade, info[0])
        val oarModel = context.getString(R.string.model, info[1])
        val oarWeight = context.getString(R.string.item_weight, info[2])
        return ComboItem(
            rower.id,
            logoBoat,
            logoOar,
            rower.thumb,
            rower.name,
            height,
            rowerWeight,
            age,
            strategy = rower.strategy,
            rigger,
            boatWeight,
            blade,
            oarModel,
            oarWeight,
            getPowerOnWater(boat, oar, rower).toString()
        )
    }
}