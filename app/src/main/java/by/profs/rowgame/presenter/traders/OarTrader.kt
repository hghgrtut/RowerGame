package by.profs.rowgame.presenter.traders

import by.profs.rowgame.data.items.Oar
import by.profs.rowgame.data.preferences.PreferenceEditor
import by.profs.rowgame.presenter.dao.OarDao
import by.profs.rowgame.utils.IDEAL
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class OarTrader(private val prefEditor: PreferenceEditor, private val dao: OarDao) :
    Trader<Oar>(prefEditor, dao) {
    private val scope = CoroutineScope(Dispatchers.IO)

    override fun calculateCost(item: Oar): Int =
        Oar.BASIC_COST * item.weight * item.blade * (IDEAL - item.damage) / IDEAL

    override fun sell(item: Oar) {
        prefEditor.setBalance(prefEditor.getBalance() + calculateCost(item))
        scope.launch { dao.deleteItem(item.id!!) }
    }
}