package by.profs.rowgame.view.inventory

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import by.profs.rowgame.R
import by.profs.rowgame.data.preferences.PreferenceEditor
import by.profs.rowgame.databinding.FragmentInventoryBinding
import by.profs.rowgame.presenter.database.MyRoomDatabase
import by.profs.rowgame.presenter.navigation.INTENT_BOATS
import by.profs.rowgame.presenter.navigation.INTENT_OARS
import by.profs.rowgame.presenter.navigation.INTENT_ROWERS
import by.profs.rowgame.view.adapters.BoatViewAdapter
import by.profs.rowgame.view.adapters.INVENTORY
import by.profs.rowgame.view.adapters.OarViewAdapter
import by.profs.rowgame.view.adapters.RowerViewAdapter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class InventoryFragment : Fragment(R.layout.fragment_inventory) {
    private val args by navArgs<InventoryFragmentArgs>()
    private val navController by lazy(LazyThreadSafetyMode.NONE) { findNavController() }
    private var binding: FragmentInventoryBinding? = null
    private lateinit var database: MyRoomDatabase
    private lateinit var recyclerView: RecyclerView
    private lateinit var prefEditor: PreferenceEditor

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentInventoryBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        prefEditor = PreferenceEditor(requireContext())
        recyclerView = binding!!.list.apply {
            setHasFixedSize(true)
            this.layoutManager = LinearLayoutManager(context)
        }
    }

    override fun onResume() {
        super.onResume()
        binding?.money?.text = this.getString(R.string.money_balance, prefEditor.getBalance())

        database = MyRoomDatabase.getDatabase(requireContext())
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
        val dao = database.boatDao()
        val myBoats = withContext(Dispatchers.IO) { ArrayList(dao.getItems()) }
        val viewAdapter = BoatViewAdapter(myBoats, INVENTORY, prefEditor, dao)
        recyclerView.apply { adapter = viewAdapter }
        requireActivity().setTitle(R.string.boat_inventory)
        binding?.fab?.setOnClickListener {
            InventoryFragmentDirections.actionInventoryFragmentToShopFragment(args.itemType)
                .also { navController.navigate(it) }
        }
    }

    private suspend fun showOars() {
        val oars = withContext(Dispatchers.IO) { database.oarDao().getItems() }
        val viewAdapter = OarViewAdapter(oars, INVENTORY, prefEditor, database)
        recyclerView.apply { adapter = viewAdapter }
        requireActivity().setTitle(R.string.oar_inventory)
        binding?.fab?.setOnClickListener {
            InventoryFragmentDirections.actionInventoryFragmentToShopFragment(args.itemType)
                .also { navController.navigate(it) }
        }
    }

    private suspend fun showRowers() {
        val dao = database.rowerDao()
        val rowers = withContext(Dispatchers.IO) { dao.getItems() }
        val viewAdapter = RowerViewAdapter(INVENTORY, rowers, navController)
        recyclerView.apply { adapter = viewAdapter }
        binding?.fab?.setOnClickListener {
            InventoryFragmentDirections.actionInventoryFragmentToRowerDetailsFragment()
                .also { navController.navigate(it) }
        }
        requireActivity().setTitle(R.string.rower_inventory)
    }
}