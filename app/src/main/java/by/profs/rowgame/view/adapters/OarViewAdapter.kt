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
import by.profs.rowgame.app.ServiceLocator
import by.profs.rowgame.data.items.Oar
import by.profs.rowgame.data.items.util.Manufacturer
import by.profs.rowgame.data.preferences.LevelEditor
import by.profs.rowgame.data.preferences.PairingPreferences
import by.profs.rowgame.presenter.database.dao.ComboDao
import by.profs.rowgame.presenter.informators.OarInformator
import by.profs.rowgame.presenter.informators.OarInformator.Companion.bladeImages
import by.profs.rowgame.presenter.traders.OarTrader
import by.profs.rowgame.view.activity.InfoBar
import by.profs.rowgame.view.fragments.extensions.showToast
import by.profs.rowgame.view.fragments.pairing.PairingFragmentDirections
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

class OarViewAdapter(
    private val oars: List<Oar>,
    private val type: Int,
    infoBar: InfoBar
) : RecyclerView.Adapter<OarViewAdapter.ViewHolder>(), MyViewAdapter<Oar> {

    private lateinit var context: Context
    private lateinit var fragment: Fragment
    private val informator: OarInformator = OarInformator()
    private val level: Int = LevelEditor.get()
    private val trader: OarTrader = OarTrader(infoBar)
    private val scope = CoroutineScope(Dispatchers.IO)

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
        holder.button.run{
            if (type == SHOP && oar.getLevel() > level) isEnabled = false
            else setOnClickListener { tradeOar(oar, itemView) }
        }
    }

    override fun getItemCount(): Int = oars.size

    class ViewHolder(view: View) : MyViewAdapter.ViewHolder(view) {
        val bladeImage: ImageView = view.findViewById(R.id.blade_image)
        val blade: TextView = view.findViewById(R.id.blade)
        val power: TextView = view.findViewById(R.id.power)
    }

    private fun displayItem(holder: ViewHolder, oar: Oar) {
        displayItem(holder as MyViewAdapter.ViewHolder, oar)
        showManufacturerSpecificInfo(holder, oar)
        holder.power.text = oar.getPower().toString()
    }

    override fun displayItem(holder: MyViewAdapter.ViewHolder, item: Oar) = holder.run {
        logo.setImageResource(Manufacturer.valueOf(item.manufacturer).logoResId)
        cost.text = context.getString(R.string.cost, trader.calculateCost(item))
        damage.text = context.getString(R.string.damage, item.damage)
        manufacturer.text = context.getString(R.string.manufacturer, item.manufacturer)
        button.text = context.getString(when (this@OarViewAdapter.type) {
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
            holder.weight.text = context.getString(R.string.item_weight, info[2])
        } catch (e: KotlinNullPointerException) {
            Log.e("incomp oar", oar.toString())
        }
    }

    private fun tradeOar(oar: Oar, itemView: View) {
        when (type) {
            INVENTORY -> {
                trader.sell(oar)
                itemView.visibility = View.GONE
                context.showToast(R.string.sell_sucess)
            }
            SHOP -> context.showToast(
                if (trader.buy(oar)) R.string.buy_sucess else R.string.check_balance)
            PAIRING -> {
                val pairingPreferences = PairingPreferences
                pairingPreferences.occupyOar(oar.id!!)
                scope.launch {
                    ServiceLocator.get(ComboDao::class).insertCombo(pairingPreferences.getCombo())
                    val navController by lazy(LazyThreadSafetyMode.NONE) {
                        NavHostFragment.findNavController(fragment) }
                    MainScope().launch { PairingFragmentDirections.actionPairingFragmentSelf()
                        .also { navController.navigate(it) } } }
            }
        }
    }
}