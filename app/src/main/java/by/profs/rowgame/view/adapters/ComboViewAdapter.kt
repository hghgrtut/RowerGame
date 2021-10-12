package by.profs.rowgame.view.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ImageView
import android.widget.Spinner
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import by.profs.rowgame.R
import by.profs.rowgame.app.ServiceLocator
import by.profs.rowgame.data.competition.CompetitionStrategy
import by.profs.rowgame.data.items.Boat
import by.profs.rowgame.data.items.Oar
import by.profs.rowgame.data.items.Rower
import by.profs.rowgame.data.items.util.BoatTypes
import by.profs.rowgame.data.items.util.Manufacturer
import by.profs.rowgame.databinding.ItemPairBinding
import by.profs.rowgame.presenter.database.dao.ComboDao
import by.profs.rowgame.presenter.imageloader.loadThumb
import by.profs.rowgame.presenter.informators.OarInformator
import by.profs.rowgame.view.fragments.extensions.makeInvisible
import by.profs.rowgame.view.fragments.extensions.makeVisible

class ComboViewAdapter(
    private val boats: List<Boat>,
    private val oars: List<Oar>,
    private val rowers: List<Rower>,
    private val myRowerIds: List<Int>? = null
) : RecyclerView.Adapter<ComboViewAdapter.ViewHolder>() {

    private lateinit var context: Context
    private val oarInformator: OarInformator = OarInformator()

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val binding = ItemPairBinding.bind(view)
        val rowerPic: ImageView = binding.rowerPic
        val boatTypePic: ImageView = binding.boatTypePic
        val boatManufacturePic: ImageView = binding.boatManufPic
        val oarManufacturerPic: ImageView = binding.oarManufPic

        val rowerName: TextView = binding.name
        val rowerHeight: TextView = binding.height
        val rowerWeight: TextView = binding.weightRower
        val rowerAge: TextView = binding.age
        val boatRigger: TextView = binding.rigger
        val boatWeight: TextView = binding.weightBoat
        val oarModel: TextView = binding.modelOar
        val oarBlade: TextView = binding.blade
        val oarWeight: TextView = binding.weightOar

        val button: Button = binding.detachButton
        val spinner: Spinner = binding.spinner
        val strategyTitle: TextView = binding.strategy
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
        holder.rowerPic.loadThumb(rower)

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
            R.string.item_weight,
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
        holder.oarWeight.text = context.getString(R.string.item_weight, info[2])

        myRowerIds?.let { if (myRowerIds.contains(rower.id)) with(holder) {
            strategyTitle.makeVisible()
            spinner.apply {
                makeVisible()
                val list = CompetitionStrategy.values().map { context.getString(it.strategyName) }
                adapter = ArrayAdapter(context, android.R.layout.simple_spinner_dropdown_item, list)
                onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(p: AdapterView<*>?, v: View?, pos: Int, i: Long) {
                        rower.strategy = pos
                        rower.saveUpdate()
                    }
                    override fun onNothingSelected(parent: AdapterView<*>?) = Unit
                }
            } }
        } ?: run {
            holder.button.makeVisible()
            holder.button.setOnClickListener {
                ServiceLocator.get(ComboDao::class).deleteComboWithRower(rower.id!!)
                holder.itemView.makeInvisible()
            }
        }
    }

    override fun getItemCount(): Int = boats.size
}