package by.profs.rowgame.view.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.findFragment
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.recyclerview.widget.RecyclerView
import by.profs.rowgame.R
import by.profs.rowgame.data.items.Rower
import by.profs.rowgame.data.preferences.PreferenceEditor
import by.profs.rowgame.presenter.dao.RowerDao
import by.profs.rowgame.presenter.dao.SingleComboDao
import by.profs.rowgame.presenter.imageloader.CoilImageLoader
import by.profs.rowgame.presenter.imageloader.ImageLoader
import by.profs.rowgame.presenter.navigation.INTENT_OARS
import by.profs.rowgame.utils.USER_PREF
import by.profs.rowgame.view.inventory.InventoryFragmentDirections
import by.profs.rowgame.view.inventory.RowerDetailsFragment.Companion.FROM_LIST
import by.profs.rowgame.view.pairing.PairingFragmentDirections
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class RowerViewAdapter(
    private val target: Int,
    private var dao: RowerDao,
    private var navController: NavController? = null,
    private var singleComboDao: SingleComboDao? = null
) : RecyclerView.Adapter<RowerViewAdapter.ViewHolder>() {

    private lateinit var rowers: List<Rower>
    private lateinit var context: Context
    private lateinit var fragment: Fragment
    private val imageLoader: ImageLoader = CoilImageLoader
    private lateinit var prefEditor: PreferenceEditor

    init { refreshDataSet() }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        context = parent.context
        fragment = parent.findFragment()
        prefEditor = PreferenceEditor(context.getSharedPreferences(USER_PREF, Context.MODE_PRIVATE))
        return ViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_rower, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val rower = rowers[position]
        showImage(holder.rowerImage, rower)
        holder.age.text = context.getString(R.string.rower_age, rower.age)
        holder.endurance.text = context.getString(R.string.rower_endurance, rower.endurance)
        holder.height.text = context.getString(R.string.rower_height, rower.height)
        holder.name.text = rower.name
        holder.power.text = context.getString(R.string.rower_power, rower.power)
        holder.technicality.text = context.getString(R.string.rower_technicality, rower.technics)
        holder.weight.text = context.getString(R.string.rower_weight, rower.weight)
        holder.itemView.setOnClickListener {
            if (target == INVENTORY) {
                InventoryFragmentDirections
                    .actionInventoryFragmentToRowerDetailsFragment(FROM_LIST, rower.id!!)
                    .also { navController!!.navigate(it) }
            } else {
                prefEditor.occupyRower(rower.id!!)
                val navController by lazy(LazyThreadSafetyMode.NONE) {
                    NavHostFragment.findNavController(fragment) }
                PairingFragmentDirections.actionPairingFragmentSelf(INTENT_OARS)
                    .also { navController.navigate(it) }
            }
        }
    }

    override fun getItemCount(): Int = rowers.size

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val rowerImage: ImageView = view.findViewById(R.id.rower_pic)

        val age: TextView = view.findViewById(R.id.age)
        val endurance: TextView = view.findViewById(R.id.endurance)
        val height: TextView = view.findViewById(R.id.height)
        val name: TextView = view.findViewById(R.id.name)
        val power: TextView = view.findViewById(R.id.power)
        val technicality: TextView = view.findViewById(R.id.technicality)
        val weight: TextView = view.findViewById(R.id.weight)
    }

    private fun refreshDataSet() { CoroutineScope(Dispatchers.IO).launch {
        rowers = withContext(Dispatchers.IO) { if (target == INVENTORY) { dao.getItems()
            } else {
                val rowerIds = singleComboDao!!.getRowerIds()
                dao.getItems().filter { rower -> !rowerIds.contains(rower.id!!) }
            } }
        }
    }

    private fun showImage(view: ImageView, rower: Rower) {
        if (rower.thumb != null) { imageLoader.loadImageFromNetwork(view, rower.thumb)
        } else { view.setImageResource(
                if (rower.gender == Rower.MALE) { R.drawable.placeholder_man
                } else { R.drawable.placeholder_woman })
        }
    }
}