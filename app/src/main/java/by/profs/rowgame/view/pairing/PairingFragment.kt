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
        when (args.item) { // intent type
            INTENT_BOATS -> choosingBoat()
            INTENT_OARS -> choosingOar()
            INTENT_ROWERS -> choosingRower()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

    private fun choosingBoat() {
        val dao = BoatRoomDatabase.getDatabase(contex, scope).boatDao()
        val viewAdapter = BoatViewAdapter(PAIRING, prefEditor, dao, getComboDao())
        recyclerView.apply { adapter = viewAdapter }
        requireActivity().setTitle(R.string.choose_boat)
    }

    private fun choosingRower(number: Int = 1) {
        val dao = RowerRoomDatabase.getDatabase(contex, scope).rowerDao()
        val viewAdapter = RowerViewAdapter(PAIRING, dao, singleComboDao = getComboDao())
        recyclerView.apply { adapter = viewAdapter }
        requireActivity().setTitle(R.string.choose_rower)
    }

    private fun choosingOar(number: Int = 1) {
        val dao = OarRoomDatabase.getDatabase(contex, scope).oarDao()
        val viewAdapter = OarViewAdapter(PAIRING, prefEditor, dao, getComboDao())
        recyclerView.apply { adapter = viewAdapter }
        requireActivity().setTitle(R.string.choose_oar)
    }

    private fun getComboDao() = SingleComboRoomDatabase.getDatabase(contex, scope).singleComboDao()
}