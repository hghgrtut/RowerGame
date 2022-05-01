package by.profs.rowgame.presenter.traders

import android.content.Context
import by.profs.rowgame.R
import by.profs.rowgame.app.ServiceLocator
import by.profs.rowgame.data.items.Rower
import by.profs.rowgame.presenter.database.dao.ComboDao
import by.profs.rowgame.presenter.database.dao.RowerDao
import by.profs.rowgame.view.activity.InfoBar
import by.profs.rowgame.view.fragments.extensions.showToast
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class Recruiter(private val infoBar: InfoBar) :
    Trader<Rower>(infoBar, ServiceLocator.get(RowerDao::class)) {
    private val scope = CoroutineScope(Dispatchers.IO)
    private val comboDao: ComboDao by ServiceLocator.locateLazy()

    override fun calculateCost(item: Rower): Int = item.cost

    override fun buy(item: Rower): Boolean {
        if (infoBar.getFame() < item.cost) return false
        infoBar.changeFame(- item.cost)
        scope.launch { dao.insert(item) }
        ServiceLocator.get(Context::class).showToast(R.string.recruit_success)
        return true
    }

    override fun sell(item: Rower) {
        scope.launch {
            dao.deleteItem(item.id!!)
            comboDao.deleteComboWithRower(item.id)
        }
        ServiceLocator.get(Context::class).showToast(R.string.fired)
    }
}