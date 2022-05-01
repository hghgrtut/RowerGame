package by.profs.rowgame.view.adapters

import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import by.profs.rowgame.R

interface MyViewAdapter<T> {

    abstract class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val logo: ImageView = view.findViewById(R.id.logo)

        val cost: TextView = view.findViewById(R.id.cost)
        val damage: TextView = view.findViewById(R.id.damage)
        val manufacturer: TextView = view.findViewById(R.id.manufacturer)
        val model: TextView = view.findViewById(R.id.model)
        val weight: TextView = view.findViewById(R.id.weight)
        val locked: TextView = view.findViewById(R.id.locked)

        val button: Button = view.findViewById(R.id.button_trade)
    }

    fun displayItem(holder: ViewHolder, item: T)
}