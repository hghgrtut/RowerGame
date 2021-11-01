package by.profs.rowgame.presenter.traders

import by.profs.rowgame.app.ServiceLocator
import by.profs.rowgame.data.items.Oar
import by.profs.rowgame.presenter.database.dao.ComboDao
import by.profs.rowgame.presenter.database.dao.OarDao
import by.profs.rowgame.utils.IDEAL
import by.profs.rowgame.view.activity.InfoBar
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class OarTrader(private val infoBar: InfoBar) :
    Trader<Oar>(infoBar, ServiceLocator.get(OarDao::class)) {
    private val comboDao: ComboDao by ServiceLocator.locateLazy()

    override fun calculateCost(item: Oar): Int =
        Oar.BASIC_COST * item.weight * item.blade * (IDEAL - item.damage) / IDEAL

    override fun sell(item: Oar) {
        infoBar.changeMoney(calculateCost(item))
        CoroutineScope(Dispatchers.IO).launch {
            dao.deleteItem(item.id!!)
            comboDao.deleteComboWithOar(item.id)
        }
    }
}