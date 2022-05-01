package by.profs.rowgame.view.adapters.utils

import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import by.profs.rowgame.data.competition.CompetitionStrategy
import by.profs.rowgame.view.fragments.extensions.makeVisible

class StrategySpinner(spinner: Spinner, strategy: Int, private val updateFun: (Int) -> Unit) {
    init {
        spinner.apply {
            makeVisible()
            val list = CompetitionStrategy.values().map { context.getString(it.strategyName) }
            adapter = ArrayAdapter(context, android.R.layout.simple_spinner_dropdown_item, list)
            setSelection(strategy)
            onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(p: AdapterView<*>?, v: View?, pos: Int, i: Long) =
                    updateFun(pos)
                override fun onNothingSelected(parent: AdapterView<*>?) = Unit
            }
        }
    }
}