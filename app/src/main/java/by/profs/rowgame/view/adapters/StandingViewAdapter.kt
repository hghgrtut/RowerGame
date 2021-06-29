package by.profs.rowgame.view.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import by.profs.rowgame.R
import by.profs.rowgame.data.items.Rower
import by.profs.rowgame.view.utils.HelperFuns.loadThumb

class StandingViewAdapter(
    private val standing: ArrayList<Pair<Rower, Int>>,
    private val mode: Int
) : RecyclerView.Adapter<StandingViewAdapter.ViewHolder>() {

    private lateinit var context: Context

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val gap: TextView = view.findViewById(R.id.gap)
        val name: TextView = view.findViewById(R.id.name)
        val position: TextView = view.findViewById(R.id.position)
        val rowerPic: ImageView = view.findViewById(R.id.rower_pic)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        context = parent.context
        return ViewHolder(
            LayoutInflater.from(context).inflate(R.layout.item_final_standing, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (mode != RESULTS) holder.gap.text = context.getString(
            if (mode == RACE) R.string.standing_gap else R.string.standing_score,
            standing[position].second
        )
        val rower: Rower = standing[position].first
        holder.position.text = (1 + position).toString()
        holder.name.text = rower.name
        loadThumb(rower, holder.rowerPic)
    }

    override fun getItemCount(): Int = standing.size

    companion object {
        const val RACE = 0
        const val RESULTS = 1
        const val SCORE = 2
    }
}