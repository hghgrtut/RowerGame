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
import by.profs.rowgame.app.ServiceLocator
import by.profs.rowgame.data.items.Boat
import by.profs.rowgame.data.items.Oar
import by.profs.rowgame.data.items.Rower
import by.profs.rowgame.databinding.FragmentTrainingBinding
import by.profs.rowgame.presenter.database.MyRoomDatabase
import by.profs.rowgame.presenter.database.dao.ComboDao
import by.profs.rowgame.presenter.database.dao.CompetitionDao
import by.profs.rowgame.presenter.trainer.Trainer
import by.profs.rowgame.utils.TRAIN_ENDURANCE
import by.profs.rowgame.utils.TRAIN_POWER
import by.profs.rowgame.utils.TRAIN_TECHNICALITY
import by.profs.rowgame.view.activity.ActivityWithInfoBar
import by.profs.rowgame.view.activity.InfoBar
import by.profs.rowgame.view.adapters.ComboViewAdapter
import by.profs.rowgame.view.extensions.showToast
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class TrainingFragment : Fragment(R.layout.fragment_training) {
    private var binding: FragmentTrainingBinding? = null
    private lateinit var competitionDays: IntArray
    private var _infoBar: InfoBar? = null
    private val infoBar: InfoBar get() = requireNotNull(_infoBar)
    private lateinit var recyclerView: RecyclerView
    private lateinit var singleComboDao: ComboDao
    private val scope: CoroutineScope = CoroutineScope(Dispatchers.IO)

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

    private fun nextDay() {
        infoBar.nextAndShowDay()
        infoBar.showDay()
        val day = infoBar.getDay()
        if (competitionDays.contains(day)) {
            if (competitionDays.last() == day) {
                scope.launch { ServiceLocator.get(CompetitionDao::class).deleteLicences() } }
            requireContext().showToast(R.string.time_to_race)
            TrainingFragmentDirections.actionTrainingFragmentToPreCompetitionFragment().also {
                findNavController().navigate(it) }
        } else { requireContext().showToast(R.string.train_sucess) }
    }

    private suspend fun refreshView() {
        val database: MyRoomDatabase = ServiceLocator.locate()
        val boatDao = database.boatDao()
        val oarDao = database.oarDao()
        val rowerDao = database.rowerDao()
        singleComboDao = database.comboDao()

        competitionDays = withContext(Dispatchers.IO) {
            ServiceLocator.get(CompetitionDao::class).getCompetitionDays() }

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

        val trainer = Trainer(database, deleteComboFun)

        binding?.run {
            buttonTrainEndurance.setOnClickListener {
                scope.launch { trainer.startTraining(TRAIN_ENDURANCE, combos) }
                nextDay() }
            buttonTrainPower.setOnClickListener {
                scope.launch { trainer.startTraining(TRAIN_POWER, combos) }
                nextDay() }
            buttonTrainTechnical.setOnClickListener {
                scope.launch { trainer.startTraining(TRAIN_TECHNICALITY, combos) }
                nextDay() }
        }
    }
}