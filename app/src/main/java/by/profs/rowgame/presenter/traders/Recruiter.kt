package by.profs.rowgame.presenter.traders

import by.profs.rowgame.data.items.Rower
import by.profs.rowgame.data.preferences.PreferenceEditor
import by.profs.rowgame.presenter.dao.RowerDao
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class Recruiter(private val prefEditor: PreferenceEditor, private val dao: RowerDao) :
    Trader<Rower>(prefEditor, dao) {
    private val scope = CoroutineScope(Dispatchers.IO)

    override fun calculateCost(item: Rower): Int = item.cost

    override fun buy(item: Rower): Boolean {
        val cost = item.cost
        if (cost > 0) {
            val fame = prefEditor.getFame()
            if (fame >= cost) prefEditor.setFame(fame - cost) else return false
        }
        scope.launch { dao.insert(item) }
        return true
    }

    override fun sell(item: Rower) { scope.launch { dao.deleteItem(item.id!!) } }
}