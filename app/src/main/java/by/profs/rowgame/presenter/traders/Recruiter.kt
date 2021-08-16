package by.profs.rowgame.presenter.traders

import by.profs.rowgame.data.items.Rower
import by.profs.rowgame.presenter.dao.RowerDao
import by.profs.rowgame.view.activity.InfoBar
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class Recruiter(private val infoBar: InfoBar, private val dao: RowerDao) :
    Trader<Rower>(infoBar, dao) {
    private val scope = CoroutineScope(Dispatchers.IO)

    override fun calculateCost(item: Rower): Int = item.cost

    override fun buy(item: Rower): Boolean {
        val cost = item.cost
        if (cost > 0) {
            val fame = infoBar.getFame()
            if (fame >= cost) infoBar.setFame(fame - cost) else return false
        }
        scope.launch { dao.insert(item) }
        return true
    }

    override fun sell(item: Rower) { scope.launch { dao.deleteItem(item.id!!) } }
}