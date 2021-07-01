package by.profs.rowgame.view.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.findFragment
import androidx.navigation.fragment.NavHostFragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import by.profs.rowgame.R
import by.profs.rowgame.data.items.Boat
import by.profs.rowgame.data.items.util.BoatTypes
import by.profs.rowgame.data.items.util.Manufacturer
import by.profs.rowgame.data.preferences.PreferenceEditor
import by.profs.rowgame.presenter.dao.BoatDao
import by.profs.rowgame.presenter.informators.BoatInformator
import by.profs.rowgame.presenter.navigation.INTENT_ROWERS
import by.profs.rowgame.presenter.traders.BoatTrader
import by.profs.rowgame.view.pairing.PairingFragmentDirections
import by.profs.rowgame.view.utils.HelperFuns

class BoatViewAdapter(
    private val boats: ArrayList<Boat>,
    private val type: Int,
    private val prefEditor: PreferenceEditor,
    dao: BoatDao
) : RecyclerView.Adapter<BoatViewAdapter.ViewHolder>(),
    MyViewAdapter<Boat> {

    private lateinit var context: Context
    private lateinit var fragment: Fragment
    private val informator: BoatInformator = BoatInformator()
    private val trader: BoatTrader = BoatTrader(prefEditor, dao)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        context = parent.context
        fragment = parent.findFragment()
        return ViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_boat, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val boat = boats[position]
        displayItem(holder, boat)
        holder.button.setOnClickListener { tradeBoat(boat, holder.itemView) }
    }

    override fun getItemCount(): Int = boats.size

    class ViewHolder(view: View) : MyViewAdapter.ViewHolder(view) {
        val typeImage: ImageView = view.findViewById(R.id.type_image)
        val wingImage: ImageView = view.findViewById(R.id.wing_image)

        val lenght: TextView = view.findViewById(R.id.shell_lenght)
        val rigger: TextView = view.findViewById(R.id.rigger)
        val rowerWeight: TextView = view.findViewById(R.id.rower_weight)
    }

    private fun displayItem(holder: ViewHolder, item: Boat) {
        displayItem(holder as MyViewAdapter.ViewHolder, item)
        showWingAndType(holder, item)
        holder.rigger.text = context.getString(R.string.rigger, when (item.wing) {
                Boat.CLASSIC_STAY -> context.getString(R.string.rigger_classic)
                Boat.ALUMINIUM_WING -> context.getString(R.string.rigger_aluminium_wing)
                Boat.CARBON_WING -> context.getString(R.string.rigger_carbon_wing)
                else -> context.getString(R.string.rigger_backwing)
        })
        val info = informator.getItemInfo(item)
        holder.lenght.text = context.getString(R.string.shell_lenght, info[0])
        holder.rowerWeight.text = context.getString(R.string.recommended_weight, info[1])
        holder.model.text = context.getString(R.string.model, info[2])
    }

    override fun displayItem(holder: MyViewAdapter.ViewHolder, item: Boat) {
        holder.logo.setImageResource(Manufacturer.valueOf(item.manufacturer).logoResId)
        holder.cost.text = context.getString(R.string.cost, trader.calculateCost(item))
        holder.damage.text = context.getString(R.string.damage, item.damage)
        holder.manufacturer.text = context.getString(R.string.manufacturer, item.manufacturer)
        holder.button.text = context.getString(when (type) {
            INVENTORY -> R.string.sell
            SHOP -> R.string.buy
            else -> R.string.take
        })
        showWeight(holder.weight, item)
        holder.type.text = context.getString(R.string.type, BoatTypes.valueOf(item.type).type)
    }

    private fun showWeight(view: TextView, boat: Boat) {
        view.text = context.getString(R.string.item_weight,
            when (boat.type) {
                BoatTypes.SingleScull.name -> when (boat.weight) {
                    Boat.ELITE -> "14"
                    Boat.SPORTIVE -> "16"
                    else -> "18"
                }
//                BoatTypes.DoubleScull.name ->
//                BoatTypes.Pair.name ->
//                BoatTypes.CoxedPair.name ->
//                BoatTypes.QuadrupleScull.name ->
//                BoatTypes.Four.name ->
//                BoatTypes.CoxedFour.name ->
                else -> throw NotImplementedError("${boat.type} not supported")
        })
    }

    private fun showWingAndType(holder: ViewHolder, item: Boat) {
        holder.wingImage.setImageResource(when (item.wing) {
            Boat.CLASSIC_STAY -> R.drawable.wing_classic
            Boat.ALUMINIUM_WING -> R.drawable.wing_aluminium
            Boat.CARBON_WING -> R.drawable.wing_carbon
            else -> R.drawable.wing_backwing
        })
        holder.typeImage.setImageResource(when (item.type) {
            BoatTypes.SingleScull.name -> R.drawable.boat_single_scull
            BoatTypes.DoubleScull.name -> R.drawable.boat_double_scull
            BoatTypes.Pair.name -> R.drawable.boat_pair
            BoatTypes.CoxedPair.name -> R.drawable.boat_coxed_pair
            BoatTypes.QuadrupleScull.name -> R.drawable.boat_quadruple_scull
            BoatTypes.Four.name -> R.drawable.boat_four
            BoatTypes.CoxedFour.name -> R.drawable.boat_coxed_four
            else -> R.drawable.boat_eight
        })
    }

    private fun tradeBoat(boat: Boat, itemView: View) { when (type) {
            INVENTORY -> {
                trader.sell(boat)
                itemView.visibility = View.GONE
                HelperFuns.showToast(context, R.string.sell_sucess)
            }
            SHOP -> {
                HelperFuns.showToast(
                    context, if (trader.buy(boat)) R.string.buy_sucess else R.string.check_balance)
            }
            PAIRING -> {
                prefEditor.occupyBoat(boat.id!!)
                val navController by lazy(LazyThreadSafetyMode.NONE) { findNavController(fragment) }
                PairingFragmentDirections.actionPairingFragmentSelf(item = INTENT_ROWERS)
                    .also { navController.navigate(it) }
            }
        }
    }
}