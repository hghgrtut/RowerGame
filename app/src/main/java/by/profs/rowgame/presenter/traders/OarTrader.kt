package by.profs.rowgame.presenter.traders

import by.profs.rowgame.data.items.Oar
import by.profs.rowgame.presenter.dao.OarDao
import by.profs.rowgame.utils.IDEAL
import by.profs.rowgame.view.activity.InfoBar
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class OarTrader(private val infoBar: InfoBar, private val dao: OarDao) :
    Trader<Oar>(infoBar, dao) {
    private val scope = CoroutineScope(Dispatchers.IO)

    override fun calculateCost(item: Oar): Int =
        Oar.BASIC_COST * item.weight * item.blade * (IDEAL - item.damage) / IDEAL

    override fun sell(item: Oar) {
        infoBar.changeMoney(calculateCost(item))
        scope.launch { dao.deleteItem(item.id!!) }
    }
}