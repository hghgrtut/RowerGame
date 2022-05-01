package by.profs.rowgame.view.adapters

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.Spinner
import android.widget.TextView
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import by.profs.rowgame.R
import by.profs.rowgame.app.ServiceLocator
import by.profs.rowgame.data.combos.ComboItem
import by.profs.rowgame.databinding.ItemComboBinding
import by.profs.rowgame.presenter.database.dao.ComboDao
import by.profs.rowgame.presenter.database.dao.RowerDao
import by.profs.rowgame.view.adapters.utils.DetachDialog
import by.profs.rowgame.view.adapters.utils.StrategySpinner
import by.profs.rowgame.view.fragments.extensions.enableClick
import by.profs.rowgame.view.fragments.extensions.loadThumb
import by.profs.rowgame.view.fragments.extensions.makeInvisible
import by.profs.rowgame.view.fragments.extensions.makeVisible
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ComboViewAdapter(
    private val items: List<ComboItem>,
    private val myRowerIds: List<Int>? = null,
    private val fragmentManager: FragmentManager? = null,
    private val changeStrategyFun: ((Int, Int) -> Unit)? = null
) : RecyclerView.Adapter<ComboViewAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val binding = ItemComboBinding.bind(view)
        val rowerPic: ImageView = binding.rowerPic
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
        val comboPower: TextView = binding.comboPower
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_combo, parent, false))

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.rowerPic.loadThumb(item.rowerPicUrl)

        holder.boatManufacturePic.setImageResource(item.logoBoat)
        holder.oarManufacturerPic.setImageResource(item.logoOar)

        holder.rowerName.text = item.rowerName
        holder.rowerHeight.text = item.height
        holder.rowerWeight.text = item.rowerWeight
        holder.rowerAge.text = item.rowerAge

        holder.boatRigger.text = item.rigger
        holder.boatWeight.text = item.boatWeight

        holder.oarBlade.text = item.blade
        holder.oarModel.text = item.oarModel
        holder.oarWeight.text = item.oarWeight

        val rowerId = item.rowerId
        val scope = CoroutineScope(Dispatchers.IO)
        myRowerIds?.let {
            Log.d("jjj", myRowerIds.toString())
            if (myRowerIds.contains(rowerId)) with(holder) {
                strategyTitle.makeVisible()
                StrategySpinner(spinner, item.strategy) { strategy ->
                    changeStrategyFun!!(rowerId!!, strategy)
                    CoroutineScope(Dispatchers.IO).launch {
                        ServiceLocator.get(RowerDao::class).setStrategy(rowerId, strategy)
                    }
                }
            }
        } ?: holder.run {
            button.enableClick {
                ServiceLocator.get(Context::class)
                DetachDialog {
                    holder.itemView.makeInvisible()
                    scope.launch {
                        ServiceLocator.get(ComboDao::class).deleteComboWithRower(rowerId!!)
                    }
                }.show(fragmentManager!!, "detachDialog")
            }
            comboPower.apply {
                text = item.basicPower
                makeVisible()
            }
        }
    }

    override fun getItemCount(): Int = items.size
}