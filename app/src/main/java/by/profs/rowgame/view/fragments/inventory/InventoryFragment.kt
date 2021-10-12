package by.profs.rowgame.view.fragments.inventory

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.NavDirections
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.RecyclerView
import by.profs.rowgame.R
import by.profs.rowgame.app.ServiceLocator
import by.profs.rowgame.databinding.FragmentInventoryBinding
import by.profs.rowgame.presenter.database.MyRoomDatabase
import by.profs.rowgame.presenter.navigation.INTENT_BOATS
import by.profs.rowgame.presenter.navigation.INTENT_OARS
import by.profs.rowgame.presenter.navigation.INTENT_ROWERS
import by.profs.rowgame.view.activity.ActivityWithInfoBar
import by.profs.rowgame.view.activity.InfoBar
import by.profs.rowgame.view.adapters.BoatViewAdapter
import by.profs.rowgame.view.adapters.INVENTORY
import by.profs.rowgame.view.adapters.OarViewAdapter
import by.profs.rowgame.view.adapters.RowerViewAdapter
import by.profs.rowgame.view.fragments.extensions.setup
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class InventoryFragment : Fragment(R.layout.fragment_inventory) {
    private val args by navArgs<InventoryFragmentArgs>()
    private val navController by lazy(LazyThreadSafetyMode.NONE) { findNavController() }
    private var binding: FragmentInventoryBinding? = null
    private val database: MyRoomDatabase = ServiceLocator.locate()
    private lateinit var recyclerView: RecyclerView
    private var _infoBar: InfoBar? = null
    private val infoBar: InfoBar get() = requireNotNull(_infoBar)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentInventoryBinding.inflate(inflater, container, false)
        _infoBar = (requireActivity() as ActivityWithInfoBar).infoBar
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recyclerView = binding!!.list.setup()
    }

    override fun onResume() {
        super.onResume()
        MainScope().launch { when (args.itemType) { // intent type
                INTENT_OARS -> showOars()
                INTENT_BOATS -> showBoats()
                INTENT_ROWERS -> showRowers()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

    private suspend fun showBoats() {
        requireActivity().setTitle(R.string.boat_inventory)
        fabListenerNavigateToShop()
        val dao = database.boatDao()
        dao.getItems().collectLatest {
            val viewAdapter = BoatViewAdapter(ArrayList(it), INVENTORY, infoBar, dao)
            recyclerView.adapter = viewAdapter
        }
    }

    private suspend fun showOars() {
        requireActivity().setTitle(R.string.oar_inventory)
        fabListenerNavigateToShop()
        database.oarDao().getItems().collectLatest {
            val viewAdapter = OarViewAdapter(it, INVENTORY, infoBar, database)
            recyclerView.adapter = viewAdapter
        }
    }

    private suspend fun showRowers() {
        fabListenerNavigateTo(
            InventoryFragmentDirections.actionInventoryFragmentToRowerDetailsFragment())
        requireActivity().setTitle(R.string.rower_inventory)
        val dao = database.rowerDao()
        dao.getItems().collectLatest {
            val viewAdapter = RowerViewAdapter(INVENTORY, it, navController)
            recyclerView.adapter = viewAdapter
        }
    }

    private fun fabListenerNavigateTo(navDirections: NavDirections): Unit? =
        binding?.fab?.setOnClickListener { navController.navigate(navDirections) }

    private fun fabListenerNavigateToShop(): Unit? = fabListenerNavigateTo(
        InventoryFragmentDirections.actionInventoryFragmentToShopFragment(args.itemType))
}