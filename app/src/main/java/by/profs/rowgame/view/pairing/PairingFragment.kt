package by.profs.rowgame.view.pairing

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import by.profs.rowgame.R
import by.profs.rowgame.data.preferences.PreferenceEditor
import by.profs.rowgame.databinding.FragmentPairingBinding
import by.profs.rowgame.presenter.database.BoatRoomDatabase
import by.profs.rowgame.presenter.database.OarRoomDatabase
import by.profs.rowgame.presenter.database.RowerRoomDatabase
import by.profs.rowgame.presenter.database.SingleComboRoomDatabase
import by.profs.rowgame.presenter.navigation.INTENT_BOATS
import by.profs.rowgame.presenter.navigation.INTENT_OARS
import by.profs.rowgame.presenter.navigation.INTENT_ROWERS
import by.profs.rowgame.view.adapters.BoatViewAdapter
import by.profs.rowgame.view.adapters.OarViewAdapter
import by.profs.rowgame.view.adapters.PAIRING
import by.profs.rowgame.view.adapters.RowerViewAdapter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class PairingFragment : Fragment(R.layout.fragment_pairing) {
    private val args by navArgs<PairingFragmentArgs>()
    private var binding: FragmentPairingBinding? = null
    private lateinit var contex: Context
    private lateinit var recyclerView: RecyclerView
    private lateinit var prefEditor: PreferenceEditor
    private val scope: CoroutineScope = CoroutineScope(Dispatchers.IO)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentPairingBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        contex = requireContext()
        prefEditor = PreferenceEditor(contex)
        recyclerView = binding!!.list.apply {
            setHasFixedSize(true)
            this.layoutManager = LinearLayoutManager(context)
        }
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
        val dao = BoatRoomDatabase.getDatabase(contex, scope).boatDao()
        val boatIds = withContext(Dispatchers.IO) { getComboDao().getBoatIds() }
        val freeBoats = withContext(Dispatchers.IO) {
            ArrayList(dao.getItems().filter { boat -> !boatIds.contains(boat.id) }) }
        val viewAdapter = BoatViewAdapter(freeBoats, PAIRING, prefEditor, dao)
        recyclerView.apply { adapter = viewAdapter }
        requireActivity().setTitle(R.string.choose_boat)
    }

    private suspend fun choosingRower() {
        val dao = RowerRoomDatabase.getDatabase(contex, scope).rowerDao()
        val rowerIds = withContext(Dispatchers.IO) { getComboDao().getRowerIds() }
        val rowers = withContext(Dispatchers.IO) {
            dao.getItems().filter { rower -> !rowerIds.contains(rower.id!!) } }
        val viewAdapter = RowerViewAdapter(PAIRING, rowers)
        recyclerView.apply { adapter = viewAdapter }
        requireActivity().setTitle(R.string.choose_rower)
    }

    private suspend fun choosingOar() {
        val dao = OarRoomDatabase.getDatabase(contex, scope).oarDao()
        val oarIds = withContext(Dispatchers.IO) { getComboDao().getOarIds() }
        val oars = withContext(Dispatchers.IO) {
            dao.getItems().filter { oar -> !oarIds.contains(oar.id) } }
        val viewAdapter = OarViewAdapter(oars, PAIRING, prefEditor, dao)
        recyclerView.apply { adapter = viewAdapter }
        requireActivity().setTitle(R.string.choose_oar)
    }

    private fun getComboDao() = SingleComboRoomDatabase.getDatabase(contex, scope).singleComboDao()
}