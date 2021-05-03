package by.profs.rowgame.view.training

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import by.profs.rowgame.R
import by.profs.rowgame.data.preferences.Calendar
import by.profs.rowgame.data.preferences.PreferenceEditor
import by.profs.rowgame.databinding.FragmentTrainingBinding
import by.profs.rowgame.presenter.dao.RowerDao
import by.profs.rowgame.presenter.database.BoatRoomDatabase
import by.profs.rowgame.presenter.database.OarRoomDatabase
import by.profs.rowgame.presenter.database.RowerRoomDatabase
import by.profs.rowgame.presenter.database.SingleComboRoomDatabase
import by.profs.rowgame.presenter.trainer.Trainer
import by.profs.rowgame.utils.TRAIN_ENDURANCE
import by.profs.rowgame.utils.TRAIN_POWER
import by.profs.rowgame.utils.TRAIN_TECHNICALITY
import by.profs.rowgame.view.adapters.PairViewAdapter
import by.profs.rowgame.view.utils.HelperFuns.showToast
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class TrainingFragment : Fragment(R.layout.fragment_training) {
    private var binding: FragmentTrainingBinding? = null
    private lateinit var calendar: Calendar
    private lateinit var prefEditor: PreferenceEditor
    private lateinit var recyclerView: RecyclerView
    private lateinit var rowerDao: RowerDao
    private val scope: CoroutineScope = CoroutineScope(Dispatchers.IO)
    private lateinit var trainer: Trainer

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
        prefEditor = PreferenceEditor(context)
        LinearLayoutManager(context)
        showDay()

        val boatDao = BoatRoomDatabase.getDatabase(context, scope).boatDao()
        val oarDao = OarRoomDatabase.getDatabase(context, scope).oarDao()
        rowerDao = RowerRoomDatabase.getDatabase(context, scope).rowerDao()
        val singleComboDao = SingleComboRoomDatabase.getDatabase(context, scope).singleComboDao()

        trainer = Trainer(boatDao, oarDao, rowerDao, singleComboDao)
        val viewAdapter =
            PairViewAdapter(boatDao, oarDao, rowerDao, singleComboDao, calendar.getGlobalDay())
        recyclerView = binding!!.list.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(context)
            adapter = viewAdapter
        }

        binding?.buttonTrainEndurance?.setOnClickListener { train(viewAdapter, TRAIN_ENDURANCE) }
        binding?.buttonTrainPower?.setOnClickListener { train(viewAdapter, TRAIN_POWER) }
        binding?.buttonTrainTechnical?.setOnClickListener { train(viewAdapter, TRAIN_TECHNICALITY) }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

    private fun showDay() {
        binding?.day?.text = this.getString(R.string.day, calendar.getDayOfYear()) }

    private fun train(viewAdapter: PairViewAdapter, mode: Int) {
        scope.launch { trainer.startTraining(mode, viewAdapter.combos, calendar.getGlobalDay()) }
        calendar.nextDay()
        if (calendar.getGlobalDay() == 1)
            rowerDao.getItems().forEach { rower ->
                rower.injury = 0
                rowerDao.updateItem(rower) }
        showDay()
        showToast(
            requireContext(), if (calendar.getDayOfYear() % DIM != 0) R.string.train_sucess
            else R.string.time_to_race
        )
    }

    companion object { private const val DIM = 30 } // Days in month
}