package by.profs.rowgame.view.fragments.inventory

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import by.profs.rowgame.R
import by.profs.rowgame.app.ServiceLocator
import by.profs.rowgame.databinding.FragmentInventoryBinding
import by.profs.rowgame.presenter.database.dao.BoatDao
import by.profs.rowgame.presenter.database.dao.OarDao
import by.profs.rowgame.presenter.database.dao.RowerDao
import by.profs.rowgame.presenter.navigation.INTENT_BOATS
import by.profs.rowgame.presenter.navigation.INTENT_OARS
import by.profs.rowgame.presenter.navigation.INTENT_ROWERS
import by.profs.rowgame.view.activity.ActivityWithInfoBar
import by.profs.rowgame.view.activity.InfoBar
import by.profs.rowgame.view.adapters.BoatViewAdapter
import by.profs.rowgame.view.adapters.INVENTORY
import by.profs.rowgame.view.adapters.OarViewAdapter
import by.profs.rowgame.view.adapters.RowerViewAdapter
import by.profs.rowgame.view.fragments.extensions.makeVisible
import by.profs.rowgame.view.fragments.extensions.setup
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class InventoryFragment : Fragment(R.layout.fragment_inventory) {
    private val itemType = listOf(INTENT_BOATS, INTENT_OARS, INTENT_ROWERS).random() // TODO: normal getting of type
    private val navController by lazy(LazyThreadSafetyMode.NONE) { findNavController() }
    private var binding: FragmentInventoryBinding? = null
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
        MainScope().launch { when (itemType) {
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

    private suspend fun showBoats() = ServiceLocator.get(BoatDao::class).getItems().collectLatest {
        recyclerView.adapter = BoatViewAdapter(ArrayList(it), INVENTORY, infoBar)
    }

    private suspend fun showOars() = ServiceLocator.get(OarDao::class).getItems().collectLatest {
        recyclerView.adapter = OarViewAdapter(it, INVENTORY, infoBar)
    }

    private suspend fun showRowers() {
        binding?.fab?.apply {
            makeVisible()
            setOnClickListener { navController.navigate(
                InventoryFragmentDirections.actionInventoryFragmentToRowerDetailsFragment())
            }
        }
        ServiceLocator.get(RowerDao::class).getItems().collectLatest {
            recyclerView.adapter = RowerViewAdapter(INVENTORY, it, navController)
        }
    }
}