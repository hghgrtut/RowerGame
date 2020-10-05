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

class FinalStandingViewAdapter(private val standing: ArrayList<Rower>) :
    RecyclerView.Adapter<FinalStandingViewAdapter.ViewHolder>() {
    private lateinit var context: Context

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val name: TextView = view.findViewById(R.id.name)
        val position: TextView = view.findViewById(R.id.position)
        val rowerPic: ImageView = view.findViewById(R.id.rower_pic)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        context = parent.context
        return FinalStandingViewAdapter.ViewHolder(
            LayoutInflater.from(context).inflate(R.layout.item_final_standing, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.position.text = (1 + position).toString()
        holder.name.text = standing[position].name
        loadThumb(standing[position], holder.rowerPic)
    }

    override fun getItemCount(): Int = standing.size
}