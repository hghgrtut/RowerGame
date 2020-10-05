package by.profs.rowgame.view.adapters

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import by.profs.rowgame.R
import by.profs.rowgame.data.combos.CombinationSingleScull
import by.profs.rowgame.data.items.Boat
import by.profs.rowgame.data.items.util.BoatTypes
import by.profs.rowgame.data.items.util.Manufacturer
import by.profs.rowgame.presenter.dao.BoatDao
import by.profs.rowgame.presenter.dao.OarDao
import by.profs.rowgame.presenter.dao.RowerDao
import by.profs.rowgame.presenter.dao.SingleComboDao
import by.profs.rowgame.presenter.imageloader.GlideImageLoader
import by.profs.rowgame.presenter.imageloader.ImageLoader
import by.profs.rowgame.presenter.informators.OarInformator
import by.profs.rowgame.utils.TRAIN_ALL
import by.profs.rowgame.utils.TRAIN_ENDURANCE
import by.profs.rowgame.utils.TRAIN_POWER
import by.profs.rowgame.utils.TRAIN_TECHNICALITY
import by.profs.rowgame.view.utils.HelperFuns.loadThumb
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class PairViewAdapter(
    private val boatDao: BoatDao,
    private val oarDao: OarDao,
    private val rowerDao: RowerDao,
    private val singleComboDao: SingleComboDao
) : RecyclerView.Adapter<PairViewAdapter.ViewHolder>() {

    private val boats = mutableListOf<Boat>()
    private val combos = mutableListOf<CombinationSingleScull>()
    private lateinit var context: Context
    private val imageLoader: ImageLoader = GlideImageLoader
    private val oarInformator: OarInformator = OarInformator()

    init { refreshCombos() }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val rowerPic: ImageView = view.findViewById(R.id.rower_pic)
        val boatTypePic: ImageView = view.findViewById(R.id.boat_type_pic)
        val boatManufacturePic: ImageView = view.findViewById(R.id.boat_manuf_pic)
        val oarManufacturerPic: ImageView = view.findViewById(R.id.oar_manuf_pic)

        val rowerName: TextView = view.findViewById(R.id.name)
        val rowerHeight: TextView = view.findViewById(R.id.height)
        val rowerWeight: TextView = view.findViewById(R.id.weight_rower)
        val rowerAge: TextView = view.findViewById(R.id.age)
        val boatType: TextView = view.findViewById(R.id.type)
        val boatRigger: TextView = view.findViewById(R.id.rigger)
        val boatWeight: TextView = view.findViewById(R.id.weight_boat)
        val oarModel: TextView = view.findViewById(R.id.model_oar)
        val oarBlade: TextView = view.findViewById(R.id.blade)
        val oarWeight: TextView = view.findViewById(R.id.weight_oar)

        val button: Button = view.findViewById(R.id.detachButton)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PairViewAdapter.ViewHolder {
        context = parent.context
        return PairViewAdapter.ViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_pair, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val boat =
                    withContext(Dispatchers.IO) { boatDao.search(combos[position].boatId)[0] }
                val oar = withContext(Dispatchers.IO) { oarDao.search(combos[position].oarId)[0] }
                val rower =
                    withContext(Dispatchers.IO) { rowerDao.search(combos[position].rowerId)[0] }
            MainScope().launch {
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

                holder.boatRigger.text = context.getString(R.string.rigger, when (boat.wing) {
                    Boat.CLASSIC_STAY -> context.getString(R.string.rigger_classic)
                    Boat.ALUMINIUM_WING -> context.getString(R.string.rigger_aluminium_wing)
                    Boat.CARBON_WING -> context.getString(R.string.rigger_carbon_wing)
                    else -> context.getString(R.string.rigger_backwing)
                })
                holder.boatWeight.text = context.getString(R.string.weight,
                    when (boat.type) {
                        BoatTypes.SingleScull.name -> when (boat.weight) {
                            Boat.ELITE -> "14"
                            Boat.SPORTIVE -> "16"
                            else -> "18"
                        }
                        else -> throw NotImplementedError("${boat.type} not supported")
                    })

                val info = oarInformator.getItemInfo(oar)
                try {
                    holder.oarBlade.text = context.getString(R.string.blade, info[0])
                    holder.oarModel.text = context.getString(R.string.model, info[1])
                    holder.oarWeight.text = context.getString(R.string.weight, info[2])
                } catch (e: KotlinNullPointerException) {
                    Log.e("incomp oar", oar.toString())
                }

                holder.button.setOnClickListener {
                    CoroutineScope(Dispatchers.IO).launch {
                        singleComboDao.deleteCombo(combos[position].combinationId!!)
                    }
                    holder.itemView.visibility = View.GONE
                }
            } } catch (e: IndexOutOfBoundsException) {
                singleComboDao.dropTable()
            }
        }
    }

    override fun getItemCount(): Int = combos.size

    fun refreshCombos() {
        CoroutineScope(Dispatchers.IO).launch {
            combos.addAll(singleComboDao.getAllCombos())
            notifyDataSetChanged()
        }
    }

    suspend fun startTraining(mode: Int) {
        combos.forEach { combo ->
            var random = (Math.random() * big).toInt() % rowerUpChance
            if (random < rowerCharacteristicsNumber) {
                val rower = withContext(Dispatchers.IO) { rowerDao.search(combo.rowerId)[0] }
                when (mode) {
                    TRAIN_ALL -> { when (random) {
                            0 -> rower.upEndurance()
                            1 -> rower.upPower()
                            2 -> rower.upTechnics()
                        }
                    }
                    TRAIN_ENDURANCE -> rower.upEndurance(random + 1)
                    TRAIN_POWER -> rower.upPower(random + 1)
                    TRAIN_TECHNICALITY -> rower.upTechnics(random + 1)
                }
                rowerDao.updateItem(rower)
            }
            random = (Math.random() * big).toInt() % damageChance
            if (random < acceptableDamage) {
                val boat = withContext(Dispatchers.IO) { boatDao.search(combo.boatId)[0] }
                if (boat.broke(random)) boatDao.updateItem(boat)
                else {
                    singleComboDao.deleteCombo(combo.combinationId!!)
                    boatDao.deleteItem(boat.id!!)
                }
            }
            random = (Math.random() * big).toInt() % damageChance
            if (random < acceptableDamage) {
                val oar = withContext(Dispatchers.IO) { oarDao.search(combo.oarId)[0] }
                if (oar.broke(random)) oarDao.updateItem(oar)
                else {
                    singleComboDao.deleteCombo(combo.combinationId!!)
                    oarDao.deleteItem(oar.id!!)
                }
            }
        }
    }

    companion object {
        private const val acceptableDamage = 11
        private const val big = 1000000
        private const val damageChance = 365
        private const val rowerCharacteristicsNumber = 3
        private const val rowerUpChance = 33
    }
}