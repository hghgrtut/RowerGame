package by.profs.rowgame.view.fragments.pairing

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.RecyclerView
import by.profs.rowgame.R
import by.profs.rowgame.app.ServiceLocator
import by.profs.rowgame.databinding.FragmentPairingBinding
import by.profs.rowgame.presenter.database.dao.BoatDao
import by.profs.rowgame.presenter.database.dao.ComboDao
import by.profs.rowgame.presenter.database.dao.OarDao
import by.profs.rowgame.presenter.database.dao.RowerDao
import by.profs.rowgame.presenter.navigation.INTENT_BOATS
import by.profs.rowgame.presenter.navigation.INTENT_OARS
import by.profs.rowgame.presenter.navigation.INTENT_ROWERS
import by.profs.rowgame.view.activity.ActivityWithInfoBar
import by.profs.rowgame.view.activity.InfoBar
import by.profs.rowgame.view.adapters.BoatViewAdapter
import by.profs.rowgame.view.adapters.OarViewAdapter
import by.profs.rowgame.view.adapters.PAIRING
import by.profs.rowgame.view.adapters.RowerViewAdapter
import by.profs.rowgame.view.fragments.extensions.setup
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class PairingFragment : Fragment(R.layout.fragment_pairing) {
    private val args by navArgs<PairingFragmentArgs>()
    private var binding: FragmentPairingBinding? = null
    private val comboDao: ComboDao = ServiceLocator.locate()
    private lateinit var recyclerView: RecyclerView
    private var _infoBar: InfoBar? = null
    private val infoBar: InfoBar get() = requireNotNull(_infoBar)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentPairingBinding.inflate(inflater, container, false)
        _infoBar = (requireActivity() as ActivityWithInfoBar).infoBar
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recyclerView = binding!!.list.setup()
        MainScope().launch { when (args.item) { // intent type
            INTENT_BOATS -> choosingBoat()
            INTENT_OARS -> choosingOar()
            INTENT_ROWERS -> choosingRower()
        } }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

    private suspend fun choosingBoat() {
        val dao: BoatDao = ServiceLocator.locate()
        val boatIds = withContext(Dispatchers.IO) { comboDao.getBoatIds() }
        dao.getItems().collectLatest {
            recyclerView.adapter = BoatViewAdapter(
                ArrayList(it.filter { boat -> !boatIds.contains(boat.id) }), PAIRING, infoBar)
        }
        requireActivity().setTitle(R.string.choose_boat)
    }

    private suspend fun choosingRower() {
        val dao: RowerDao = ServiceLocator.locate()
        val rowerIds = withContext(Dispatchers.IO) { comboDao.getRowerIds() }
        dao.getItems().collectLatest {
            recyclerView.adapter = RowerViewAdapter(
                PAIRING, it.filter { rower -> !rowerIds.contains(rower.id!!) })
        }
        requireActivity().setTitle(R.string.choose_rower)
    }

    private suspend fun choosingOar() {
        val dao: OarDao = ServiceLocator.locate()
        val oarIds = withContext(Dispatchers.IO) { comboDao.getOarIds() }
        dao.getItems().collectLatest {
            recyclerView.adapter =
                OarViewAdapter(it.filter { oar -> !oarIds.contains(oar.id) }, PAIRING, infoBar)
        }
        requireActivity().setTitle(R.string.choose_oar)
    }
}