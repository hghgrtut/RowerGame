package by.profs.rowgame.view.training

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import by.profs.rowgame.R
import by.profs.rowgame.data.combos.CombinationSingleScull
import by.profs.rowgame.data.preferences.Calendar
import by.profs.rowgame.databinding.FragmentTrainingBinding
import by.profs.rowgame.presenter.dao.BoatDao
import by.profs.rowgame.presenter.dao.OarDao
import by.profs.rowgame.presenter.dao.RowerDao
import by.profs.rowgame.presenter.dao.SingleComboDao
import by.profs.rowgame.presenter.database.BoatRoomDatabase
import by.profs.rowgame.presenter.database.OarRoomDatabase
import by.profs.rowgame.presenter.database.RowerRoomDatabase
import by.profs.rowgame.presenter.database.SingleComboRoomDatabase
import by.profs.rowgame.presenter.trainer.Trainer
import by.profs.rowgame.utils.TRAIN_ENDURANCE
import by.profs.rowgame.utils.TRAIN_POWER
import by.profs.rowgame.utils.TRAIN_TECHNICALITY
import by.profs.rowgame.view.adapters.CompetitionViewAdapter
import by.profs.rowgame.view.utils.HelperFuns.showToast
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class TrainingFragment : Fragment(R.layout.fragment_training) {
    private var binding: FragmentTrainingBinding? = null
    private lateinit var calendar: Calendar
    private lateinit var recyclerView: RecyclerView
    private lateinit var boatDao: BoatDao
    private lateinit var oarDao: OarDao
    private lateinit var rowerDao: RowerDao
    private lateinit var singleComboDao: SingleComboDao
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
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val context = requireContext()
        calendar = Calendar(context)
        showDay()

        boatDao = BoatRoomDatabase.getDatabase(context, scope).boatDao()
        oarDao = OarRoomDatabase.getDatabase(context, scope).oarDao()
        rowerDao = RowerRoomDatabase.getDatabase(context, scope).rowerDao()
        singleComboDao = SingleComboRoomDatabase.getDatabase(context, scope).singleComboDao()

        trainer = Trainer(boatDao, oarDao, rowerDao, deleteComboFun)
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

    private fun showDay() {
        binding?.day?.text = this.getString(R.string.day, calendar.getDayOfYear()) }

    private fun train(combos: MutableList<CombinationSingleScull>, mode: Int) {
        scope.launch { trainer.startTraining(mode, combos) }
        calendar.nextDay()
        showDay()
        showToast(
            requireContext(), if (calendar.getDayOfYear() % DIM != 0) R.string.train_sucess
            else R.string.time_to_race
        )
    }

    private suspend fun refreshView() {
        val boats = withContext(Dispatchers.IO) { boatDao.getItems() }
        val combos = withContext(Dispatchers.IO) { singleComboDao.getAllCombos().toMutableList() }
        val oars = withContext(Dispatchers.IO) { oarDao.getItems() }
        val rowers = withContext(Dispatchers.IO) { rowerDao.getItems() }
        val viewAdapter = CompetitionViewAdapter(boats, oars, rowers, deleteComboFun)
        recyclerView = binding!!.list.apply { adapter = viewAdapter }

        binding?.buttonTrainEndurance?.setOnClickListener { train(combos, TRAIN_ENDURANCE) }
        binding?.buttonTrainPower?.setOnClickListener { train(combos, TRAIN_POWER) }
        binding?.buttonTrainTechnical?.setOnClickListener { train(combos, TRAIN_TECHNICALITY) }
    }

    companion object { private const val DIM = 30 } // Days in month
}