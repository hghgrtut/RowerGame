package by.profs.rowgame.view.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import by.profs.rowgame.R
import by.profs.rowgame.data.items.Boat
import by.profs.rowgame.data.items.Oar
import by.profs.rowgame.data.items.Rower
import by.profs.rowgame.data.items.util.BoatTypes
import by.profs.rowgame.data.items.util.Manufacturer
import by.profs.rowgame.presenter.informators.OarInformator
import by.profs.rowgame.view.utils.HelperFuns.loadThumb

class CompetitionViewAdapter(
    private val boats: List<Boat>,
    private val oars: List<Oar>,
    private val rowers: List<Rower>,
    private val deleteRowerFun: ((Int?) -> Unit) ? = null
) : RecyclerView.Adapter<CompetitionViewAdapter.ViewHolder>() {

    private lateinit var context: Context
    private val oarInformator: OarInformator = OarInformator()

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val rowerPic: ImageView = view.findViewById(R.id.rower_pic)
        val boatTypePic: ImageView = view.findViewById(R.id.boat_type_pic)
        val boatManufacturePic: ImageView = view.findViewById(R.id.boat_manuf_pic)
        val oarManufacturerPic: ImageView = view.findViewById(R.id.oar_manuf_pic)

        val rowerName: TextView = view.findViewById(R.id.name)
        val rowerHeight: TextView = view.findViewById(R.id.height)
        val rowerWeight: TextView = view.findViewById(R.id.weight_rower)
        val rowerAge: TextView = view.findViewById(R.id.age)
        val boatRigger: TextView = view.findViewById(R.id.rigger)
        val boatWeight: TextView = view.findViewById(R.id.weight_boat)
        val oarModel: TextView = view.findViewById(R.id.model_oar)
        val oarBlade: TextView = view.findViewById(R.id.blade)
        val oarWeight: TextView = view.findViewById(R.id.weight_oar)

        val button: Button = view.findViewById(R.id.detachButton)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        context = parent.context
        return ViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_pair, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val boat = boats[position]
        val oar = oars[position]
        val rower = rowers[position]
        loadThumb(rower, holder.rowerPic)

        holder.boatTypePic.setImageResource(R.drawable.boat_single_scull)
        holder.boatManufacturePic.setImageResource(
            Manufacturer.valueOf(boat.manufacturer).logoResId)
        holder.oarManufacturerPic.setImageResource(
            Manufacturer.valueOf(oar.manufacturer).logoResId)

        holder.rowerName.text = rower.name
        holder.rowerHeight.text = context.getString(R.string.rower_height, rower.height)
        holder.rowerWeight.text = context.getString(R.string.rower_weight, rower.weight)
        holder.rowerAge.text = context.getString(R.string.rower_age, rower.age)

        holder.boatRigger.text = context.getString(
            R.string.rigger, when (boat.wing) {
            Boat.CLASSIC_STAY -> context.getString(R.string.rigger_classic)
            Boat.ALUMINIUM_WING -> context.getString(R.string.rigger_aluminium_wing)
            Boat.CARBON_WING -> context.getString(R.string.rigger_carbon_wing)
            else -> context.getString(R.string.rigger_backwing)
        })
        holder.boatWeight.text = context.getString(
            R.string.weight,
            when (boat.type) {
                BoatTypes.SingleScull.name -> when (boat.weight) {
                    Boat.ELITE -> "14"
                    Boat.SPORTIVE -> "16"
                    else -> "18"
                }
                else -> throw NotImplementedError("${boat.type} not supported")
            })

        val info = oarInformator.getItemInfo(oar)
        holder.oarBlade.text = context.getString(R.string.blade, info[0])
        holder.oarModel.text = context.getString(R.string.model, info[1])
        holder.oarWeight.text = context.getString(R.string.weight, info[2])

        if (deleteRowerFun != null) {
            holder.button.visibility = View.VISIBLE
            holder.button.setOnClickListener {
                deleteRowerFun.invoke(rower.id)
                holder.itemView.visibility = View.GONE
            }
        }
    }

    override fun getItemCount(): Int = boats.size
}