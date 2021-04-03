package by.profs.rowgame.view.adapters

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.findFragment
import androidx.navigation.fragment.NavHostFragment
import androidx.recyclerview.widget.RecyclerView
import by.profs.rowgame.R
import by.profs.rowgame.data.items.Oar
import by.profs.rowgame.data.items.util.Manufacturer
import by.profs.rowgame.data.items.util.Randomizer
import by.profs.rowgame.data.preferences.PreferenceEditor
import by.profs.rowgame.presenter.dao.OarDao
import by.profs.rowgame.presenter.dao.SingleComboDao
import by.profs.rowgame.presenter.database.SingleComboRoomDatabase
import by.profs.rowgame.presenter.informators.OarInformator
import by.profs.rowgame.presenter.informators.OarInformator.Companion.bladeImages
import by.profs.rowgame.presenter.traders.OarTrader
import by.profs.rowgame.utils.SHOP_SIZE
import by.profs.rowgame.view.pairing.PairingFragmentDirections
import by.profs.rowgame.view.utils.HelperFuns.showToast
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class OarViewAdapter(
    private val type: Int,
    private val prefEditor: PreferenceEditor,
    private var dao: OarDao,
    private val singleComboDao: SingleComboDao? = null
) : RecyclerView.Adapter<OarViewAdapter.ViewHolder>(), MyViewAdapter<Oar> {

    private lateinit var oars: List<Oar>
    private lateinit var context: Context
    private lateinit var fragment: Fragment
    private val informator: OarInformator = OarInformator()
    private val trader: OarTrader = OarTrader(prefEditor, dao)
    private val scope = CoroutineScope(Dispatchers.IO)

    init { refreshDataSet() }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        context = parent.context
        fragment = parent.findFragment()
        return ViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_oar, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val itemView = holder.itemView
        val oar = oars[position]
        displayItem(holder, oar)
        holder.button.setOnClickListener { tradeOar(oar, itemView) }
    }

    override fun getItemCount(): Int = oars.size

    class ViewHolder(view: View) : MyViewAdapter.ViewHolder(view) {
        val bladeImage: ImageView = view.findViewById(R.id.blade_image)
        val blade: TextView = view.findViewById(R.id.blade)
    }

    private fun displayItem(holder: ViewHolder, oar: Oar) {
        displayItem(holder as MyViewAdapter.ViewHolder, oar)
        showManufacturerSpecificInfo(holder, oar)
    }

    override fun displayItem(holder: MyViewAdapter.ViewHolder, item: Oar) {
        holder.logo.setImageResource(Manufacturer.valueOf(item.manufacturer).logoResId)
        holder.cost.text = context.getString(R.string.cost, trader.calculateCost(item))
        holder.damage.text = context.getString(R.string.damage, item.damage)
        holder.manufacturer.text = context.getString(R.string.manufacturer, item.manufacturer)
        holder.type.text =
            context.getString(if (item.type == Oar.SCULL) R.string.scull else R.string.sweep)
        holder.button.text = context.getString(when (type) {
            INVENTORY -> R.string.sell
            PAIRING -> R.string.take
            else -> R.string.buy
        })
    }

    private fun showManufacturerSpecificInfo(holder: ViewHolder, oar: Oar) {
        val info = informator.getItemInfo(oar)
        try {
            holder.bladeImage.setImageResource(bladeImages[info[0]]!!)
            holder.blade.text = context.getString(R.string.blade, info[0])
            holder.model.text = context.getString(R.string.model, info[1])
            holder.weight.text = context.getString(R.string.weight, info[2])
        } catch (e: KotlinNullPointerException) {
            Log.e("incomp oar", oar.toString())
        }
    }

    private fun tradeOar(oar: Oar, itemView: View) {
        when (type) {
            INVENTORY -> {
                trader.sell(oar)
                itemView.visibility = View.GONE
                showToast(context, R.string.sell_sucess)
            }
            SHOP -> { showToast(context,
                if (trader.buy(oar)) R.string.buy_sucess else R.string.check_balance) }
            PAIRING -> {
                prefEditor.occupyOar(oar.id!!)
                scope.launch {
                    SingleComboRoomDatabase.getDatabase(context, scope)
                        .singleComboDao()
                        .insertCombo(prefEditor.getCombo())
                    val navController by lazy(LazyThreadSafetyMode.NONE) {
                        NavHostFragment.findNavController(fragment) }
                    MainScope().launch { PairingFragmentDirections.actionPairingFragmentSelf()
                        .also { navController.navigate(it) } } }
            }
        }
    }

    override fun refreshDataSet() {
        CoroutineScope(Dispatchers.IO).launch {
            oars = withContext(Dispatchers.IO) { when (type) {
                INVENTORY -> dao.getItems()
                SHOP -> List(SHOP_SIZE) { Randomizer.getRandomOar() }
                else -> {
                    val oarIds = singleComboDao!!.getOarIds()
                    dao.getItems().filter { oar -> !oarIds.contains(oar.id) }
                } }
            }
        }
    }
}