package by.profs.rowgame.view.training

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import by.profs.rowgame.R
import by.profs.rowgame.data.combos.Combo
import by.profs.rowgame.data.items.Boat
import by.profs.rowgame.data.items.Oar
import by.profs.rowgame.data.items.Rower
import by.profs.rowgame.databinding.FragmentTrainingBinding
import by.profs.rowgame.presenter.dao.BoatDao
import by.profs.rowgame.presenter.dao.ComboDao
import by.profs.rowgame.presenter.dao.OarDao
import by.profs.rowgame.presenter.dao.RowerDao
import by.profs.rowgame.presenter.database.MyRoomDatabase
import by.profs.rowgame.presenter.trainer.Trainer
import by.profs.rowgame.utils.TRAIN_ENDURANCE
import by.profs.rowgame.utils.TRAIN_POWER
import by.profs.rowgame.utils.TRAIN_TECHNICALITY
import by.profs.rowgame.view.activity.ActivityWithInfoBar
import by.profs.rowgame.view.activity.InfoBar
import by.profs.rowgame.view.adapters.ComboViewAdapter
import by.profs.rowgame.view.competition.CompetitionFragment.Companion.CONCEPT
import by.profs.rowgame.view.competition.CompetitionFragment.Companion.OFP
import by.profs.rowgame.view.competition.CompetitionFragment.Companion.WATER
import by.profs.rowgame.view.extensions.showToast
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class TrainingFragment : Fragment(R.layout.fragment_training) {
    private var binding: FragmentTrainingBinding? = null
    private var _infoBar: InfoBar? = null
    private val infoBar: InfoBar get() = requireNotNull(_infoBar)
    private lateinit var recyclerView: RecyclerView
    private lateinit var boatDao: BoatDao
    private lateinit var oarDao: OarDao
    private lateinit var rowerDao: RowerDao
    private lateinit var singleComboDao: ComboDao
    private val scope: CoroutineScope = CoroutineScope(Dispatchers.IO)
    private lateinit var trainer: Trainer

    private val deleteComboFun: (Int?) -> Unit = {
        scope.launch {
            singleComboDao.deleteCombo(it!!)
            MainScope().launch { refreshView() } }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentTrainingBinding.inflate(inflater, container, false)
        _infoBar = (requireActivity() as ActivityWithInfoBar).infoBar
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val context = requireContext()
        infoBar.showDay()

        val database = MyRoomDatabase.getDatabase(context)
        boatDao = database.boatDao()
        oarDao = database.oarDao()
        rowerDao = database.rowerDao()
        singleComboDao = database.comboDao()

        trainer = Trainer(database, deleteComboFun)
        recyclerView = binding!!.list.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(context)
        }
        MainScope().launch { refreshView() }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

    private fun train(combos: MutableList<Combo>, mode: Int) {
        scope.launch { trainer.startTraining(mode, combos) }
        infoBar.nextAndShowDay()
        infoBar.showDay()
        val day = infoBar.getDay()
        if (day % DIM != 0) { requireContext().showToast(R.string.train_sucess)
        } else {
            requireContext().showToast(R.string.time_to_race)
            TrainingFragmentDirections.actionTrainingFragmentToCompetitionFragment(
                type = when (day) {
                    OFP_DAY -> OFP
                    in WATER_START..WATER_END -> WATER
                    else -> CONCEPT
            }).also { findNavController().navigate(it) }
        }
    }

    private suspend fun refreshView() {
        val combos = withContext(Dispatchers.IO) { singleComboDao.getAllCombos().toMutableList() }
        val boats = ArrayList<Boat>()
        val oars = ArrayList<Oar>()
        val rowers = ArrayList<Rower>()
        combos.forEach {
            boats.add(withContext(Dispatchers.IO) { boatDao.search(it.boatId)!! })
            oars.add(withContext(Dispatchers.IO) { oarDao.search(it.oarId)!! })
            rowers.add(withContext(Dispatchers.IO) { rowerDao.search(it.rowerId)!! })
        }
        val viewAdapter =
            ComboViewAdapter(boats.toList(), oars.toList(), rowers.toList(), deleteComboFun)
        recyclerView = binding!!.list.apply { adapter = viewAdapter }

        binding?.run {
            buttonTrainEndurance.setOnClickListener { train(combos, TRAIN_ENDURANCE) }
            buttonTrainPower.setOnClickListener { train(combos, TRAIN_POWER) }
            buttonTrainTechnical.setOnClickListener { train(combos, TRAIN_TECHNICALITY) }
        }
    }

    companion object {
        private const val DIM = 30 // Days in month
        private const val OFP_DAY = 210
        private const val WATER_START = 61
        private const val WATER_END = 359
    }
}