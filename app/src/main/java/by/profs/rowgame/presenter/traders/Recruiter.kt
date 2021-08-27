package by.profs.rowgame.presenter.traders

import by.profs.rowgame.data.items.Rower
import by.profs.rowgame.presenter.database.dao.RowerDao
import by.profs.rowgame.view.activity.InfoBar
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class Recruiter(private val infoBar: InfoBar, private val dao: RowerDao) :
    Trader<Rower>(infoBar, dao) {
    private val scope = CoroutineScope(Dispatchers.IO)

    override fun calculateCost(item: Rower): Int = item.cost

    override fun buy(item: Rower): Boolean {
        if (infoBar.getFame() < item.cost) return false
        infoBar.changeFame(- item.cost)
        scope.launch { dao.insert(item) }
        return true
    }

    override fun sell(item: Rower) { scope.launch { dao.deleteItem(item.id!!) } }
}