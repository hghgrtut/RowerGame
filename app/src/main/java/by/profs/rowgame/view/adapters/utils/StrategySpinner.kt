package by.profs.rowgame.view.adapters.utils

import android.view.View
import android.widget.AdapterView
import by.profs.rowgame.app.ServiceLocator
import by.profs.rowgame.presenter.database.dao.RowerDao
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class StrategySpinner(private val rowerId: Int, private val updateFun: (Int) -> Unit)
    : AdapterView.OnItemSelectedListener {
    override fun onItemSelected(p: AdapterView<*>?, v: View?, pos: Int, i: Long) {
        updateFun(pos)
        CoroutineScope(Dispatchers.IO).launch {
            ServiceLocator.get(RowerDao::class).setStrategy(rowerId, pos)
        }
    }

    override fun onNothingSelected(parent: AdapterView<*>?) = Unit
}