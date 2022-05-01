package by.profs.rowgame.view.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Spinner
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import by.profs.rowgame.R
import by.profs.rowgame.app.ServiceLocator
import by.profs.rowgame.data.items.Rower
import by.profs.rowgame.databinding.ItemFinalStandingBinding
import by.profs.rowgame.presenter.database.dao.RowerDao
import by.profs.rowgame.view.adapters.utils.StrategySpinner
import by.profs.rowgame.view.fragments.extensions.loadThumb
import by.profs.rowgame.view.fragments.extensions.makeInvisible
import by.profs.rowgame.view.fragments.extensions.makeVisible
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class StandingViewAdapter(
    private val standing: List<Rower>,
    private val mode: Int,
    private val gaps: ArrayList<Int> = ArrayList(),
    private val changeStrategyFun: ((Int, Int) -> Unit)? = null,
    private val myRowerIds: List<Int>? = null
) : RecyclerView.Adapter<StandingViewAdapter.ViewHolder>() {
    private val context: Context = ServiceLocator.locate()

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val binding = ItemFinalStandingBinding.bind(view)
        val gap: TextView = binding.gap
        val name: TextView = binding.name
        val rowerPic: ImageView = binding.rowerPic
        val spinner: Spinner = binding.spinner
        val strategyTitle: TextView = binding.strategy

        fun showPlace(place: Int) = binding.position.setText(place.toString())
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder = ViewHolder(
        LayoutInflater.from(parent.context).inflate(R.layout.item_final_standing, parent, false)
    )

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = with(holder) {
        val rower: Rower = standing[position]
        name.text = rower.name
        rowerPic.loadThumb(rower.thumb)

        if (mode == BEFORE) {
            if (myRowerIds!!.contains(rower.id)) {
                strategyTitle.makeVisible()
                StrategySpinner(spinner, rower.strategy) { strategy ->
                    changeStrategyFun!!(rower.id!!, strategy)
                    CoroutineScope(Dispatchers.IO).launch {
                        ServiceLocator.get(RowerDao::class).setStrategy(rower.id, strategy) } }
            } else {
                strategyTitle.makeInvisible()
                spinner.makeInvisible()
            }
            return@with
        }

        showPlace(1 + position)
        when (mode) {
            RACE -> gap.text = context.getString(R.string.standing_gap, gaps[position])
            SCORE -> gap.text = context.getString(R.string.standing_score, gaps[position])
        }
    }

    override fun getItemCount(): Int = standing.size

    companion object {
        const val BEFORE = -1
        const val RACE = 0
        const val RESULTS = 1
        const val SCORE = 2
    }
}