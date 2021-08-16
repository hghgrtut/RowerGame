package by.profs.rowgame.view.pairing

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import by.profs.rowgame.R
import by.profs.rowgame.databinding.FragmentPairingBinding
import by.profs.rowgame.presenter.database.MyRoomDatabase
import by.profs.rowgame.presenter.navigation.INTENT_BOATS
import by.profs.rowgame.presenter.navigation.INTENT_OARS
import by.profs.rowgame.presenter.navigation.INTENT_ROWERS
import by.profs.rowgame.view.activity.ActivityWithInfoBar
import by.profs.rowgame.view.activity.InfoBar
import by.profs.rowgame.view.adapters.BoatViewAdapter
import by.profs.rowgame.view.adapters.OarViewAdapter
import by.profs.rowgame.view.adapters.PAIRING
import by.profs.rowgame.view.adapters.RowerViewAdapter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class PairingFragment : Fragment(R.layout.fragment_pairing) {
    private val args by navArgs<PairingFragmentArgs>()
    private var binding: FragmentPairingBinding? = null
    private lateinit var database: MyRoomDatabase
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
        recyclerView = binding!!.list.apply {
            setHasFixedSize(true)
            this.layoutManager = LinearLayoutManager(context)
        }
        database = MyRoomDatabase.getDatabase(requireContext())
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
        val dao = database.boatDao()
        val boatIds = withContext(Dispatchers.IO) { getComboDao().getBoatIds() }
        val freeBoats = withContext(Dispatchers.IO) {
            ArrayList(dao.getItems().filter { boat -> !boatIds.contains(boat.id) }) }
        val viewAdapter = BoatViewAdapter(freeBoats, PAIRING, infoBar, dao)
        recyclerView.apply { adapter = viewAdapter }
        requireActivity().setTitle(R.string.choose_boat)
    }

    private suspend fun choosingRower() {
        val dao = database.rowerDao()
        val rowerIds = withContext(Dispatchers.IO) { getComboDao().getRowerIds() }
        val rowers = withContext(Dispatchers.IO) {
            dao.getItems().filter { rower -> !rowerIds.contains(rower.id!!) } }
        val viewAdapter = RowerViewAdapter(PAIRING, rowers)
        recyclerView.apply { adapter = viewAdapter }
        requireActivity().setTitle(R.string.choose_rower)
    }

    private suspend fun choosingOar() {
        val dao = database.oarDao()
        val oarIds = withContext(Dispatchers.IO) { getComboDao().getOarIds() }
        val oars = withContext(Dispatchers.IO) {
            dao.getItems().filter { oar -> !oarIds.contains(oar.id) } }
        val viewAdapter = OarViewAdapter(oars, PAIRING, infoBar, database)
        recyclerView.apply { adapter = viewAdapter }
        requireActivity().setTitle(R.string.choose_oar)
    }

    private fun getComboDao() = database.comboDao()
}