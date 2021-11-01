package by.profs.rowgame.view.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import by.profs.rowgame.R
import by.profs.rowgame.app.ServiceLocator
import by.profs.rowgame.data.items.Rower
import by.profs.rowgame.presenter.imageloader.loadThumb

class StandingViewAdapter(
    private val standing: ArrayList<Rower>,
    private val mode: Int,
    private val gap: ArrayList<Int> = ArrayList()
) : RecyclerView.Adapter<StandingViewAdapter.ViewHolder>() {

    private val context: Context = ServiceLocator.locate()

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val gap: TextView = view.findViewById(R.id.gap)
        val name: TextView = view.findViewById(R.id.name)
        val position: TextView = view.findViewById(R.id.position)
        val rowerPic: ImageView = view.findViewById(R.id.rower_pic)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder = ViewHolder(
        LayoutInflater.from(parent.context).inflate(R.layout.item_final_standing, parent, false))

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (mode != RESULTS) holder.gap.text = context.getString(
            if (mode == RACE) R.string.standing_gap else R.string.standing_score,
            gap[position]
        )
        val rower: Rower = standing[position]
        holder.position.text = (1 + position).toString()
        holder.name.text = rower.name
        holder.rowerPic.loadThumb(rower.thumb)
    }

    override fun getItemCount(): Int = standing.size

    companion object {
        const val RACE = 0
        const val RESULTS = 1
        const val SCORE = 2
    }
}