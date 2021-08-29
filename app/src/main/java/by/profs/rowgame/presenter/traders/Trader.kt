package by.profs.rowgame.presenter.traders

import by.profs.rowgame.presenter.database.dao.MyDao
import by.profs.rowgame.view.activity.InfoBar
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

abstract class Trader<T> (private val infoBar: InfoBar, private val dao: MyDao<T>) {
    private val scope = CoroutineScope(Dispatchers.IO)

    abstract fun calculateCost(item: T): Int

    /**
     *  !! Also withdraw money/fame (no need to do it manually) !!
     *  Returns true if buying successfully else false.
     */
    open fun buy(item: T): Boolean {
        val balance = infoBar.getMoney()
        val cost = calculateCost(item)
        return if (calculateCost(item) <= balance) {
            scope.launch { dao.insert(item) }
            infoBar.changeMoney(- cost)
            true
        } else false
    }

    abstract fun sell(item: T)
}