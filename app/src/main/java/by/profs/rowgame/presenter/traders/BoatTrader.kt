package by.profs.rowgame.presenter.traders

import by.profs.rowgame.data.items.Boat
import by.profs.rowgame.data.items.util.Manufacturer
import by.profs.rowgame.data.preferences.PreferenceEditor
import by.profs.rowgame.presenter.dao.BoatDao
import by.profs.rowgame.utils.IDEAL
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class BoatTrader(private val prefEditor: PreferenceEditor, private val dao: BoatDao) :
    Trader<Boat>(prefEditor, dao) {
    private val scope = CoroutineScope(Dispatchers.IO)

    override fun calculateCost(item: Boat): Int {
        val price = if (item.manufacturer == Manufacturer.Nemiga.name) { Boat.NEMIGA_COST
            } else { Boat.BASIC_COST * classCoef(item.weight) + Boat.WING_COST * item.wing }
        return price * (IDEAL - item.damage) / IDEAL
    }

    override fun sell(item: Boat) {
        prefEditor.setBalance(prefEditor.getBalance() + calculateCost(item))
        scope.launch { dao.deleteItem(item.id!!) }
    }

    private fun classCoef(weight: Int): Int = when (weight) {
        Boat.ELITE -> COEFF_ELITE
        Boat.SPORTIVE -> COEFF_SPORT
        else -> COEFF_RECREAT
    }

    companion object {
        const val COEFF_ELITE = 5
        const val COEFF_SPORT = 3
        const val COEFF_RECREAT = 1
    }
}