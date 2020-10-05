package by.profs.rowgame.presenter.traders

import by.profs.rowgame.data.PreferenceEditor
import by.profs.rowgame.presenter.dao.MyDao
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

abstract class Trader<T> (private val prefEditor: PreferenceEditor, private val dao: MyDao<T>) {
    private val scope = CoroutineScope(Dispatchers.IO)

    abstract fun calculateCost(item: T): Int

    // Returns true if buying successfully else false
    open fun buy(item: T): Boolean {
        val balance = prefEditor.getBalance()
        val cost = calculateCost(item)
        return if (calculateCost(item) <= balance) {
            scope.launch { dao.insert(item) }
            prefEditor.setBalance(balance - cost)
            true
        } else false
    }

    abstract fun sell(item: T)
}